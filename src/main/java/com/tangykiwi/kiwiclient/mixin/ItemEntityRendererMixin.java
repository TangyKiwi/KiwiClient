package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.ItemPhysics;
import com.tangykiwi.kiwiclient.util.ItemPhysicsExtension;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    private static final double TWO_PI = Math.PI * 2;
    private static final double HALF_PI = Math.PI * 0.5;
    private static final double THREE_HALF_PI = Math.PI * 1.5;

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Shadow
    protected abstract int getRenderedAmount(ItemStack stack);

    private ItemEntityRendererMixin(EntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onConstructor(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.shadowRadius = 0;
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(ItemEntity entity, float f, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo callback) {
        if (!KiwiClient.moduleManager.getModule(ItemPhysics.class).isEnabled()) return;

        ItemStack itemStack = entity.getStack();

        //Calculate the random seed for this specific item so that we can use random values
        //This works differently to the vanilla one to create differences between item entities of the same type
        int seed = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) * entity.getId();
        this.random.setSeed(seed);

        matrices.push();
        BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, entity.world, null, seed);
        boolean hasDepth = bakedModel.hasDepth();

        int renderCount = this.getRenderedAmount(itemStack);
        ItemPhysicsExtension rotator = (ItemPhysicsExtension) entity;

        final var item = entity.getStack().getItem();
        final boolean itemIsActualBlock = item instanceof BlockItem && !(item instanceof AliasedBlockItem);
        //final boolean shouldNotBeRotated = itemIsActualBlock && ((BlockItem)item).getBlock().getOutlineShape(((BlockItem)item).getBlock().getDefaultState(), entity.world, entity.getBlockPos(), ShapeContext.absent()).getMax(Direction.Axis.Y) <= 0.5;

        float scaleX = bakedModel.getTransformation().ground.scale.getX();
        float scaleY = bakedModel.getTransformation().ground.scale.getY();
        float scaleZ = bakedModel.getTransformation().ground.scale.getZ();

        float groundDistance = itemIsActualBlock ? 0 : (float) (0.125 - 0.0625 * scaleZ);
        if (!itemIsActualBlock) groundDistance -= (renderCount - 1) * 0.05 * scaleZ;
        matrices.translate(0, -groundDistance, 0);

        //Translate randomly to avoid Z-Fighting
        matrices.translate(0, (random.nextDouble() - 0.5) * 0.005, 0);

        //Calculate rotation based on velocity or get the one the item had
        //before it hit the ground
        if (rotator.getRotation() == -1) rotator.setRotation((random.nextInt(20) - 10) * 0.15f);
        float angle = entity.isOnGround() ? rotator.getRotation() : (float) (rotator.getRotation() + ((MathHelper.clamp(entity.getVelocity().y * 0.25, 0.075, 0.3))) * (entity.isSubmergedInWater() ? 0.25f : 1));

        //Make sure the angle never exceeds two pi
        if (angle >= TWO_PI) angle -= TWO_PI;

        //Clusterfuck our way back to either 0 or 180 degrees. There has to be a better way to do this
        if (entity.isOnGround() && !(angle == 0 || angle == (float) Math.PI)) {
            if (angle > Math.PI) {
                if (angle > THREE_HALF_PI) angle += tickDelta * 0.5;
                else {
                    angle -= tickDelta * 0.5;
                }
            } else {
                if (angle > HALF_PI) {
                    angle += tickDelta * 0.5;
                    if (angle > Math.PI) angle = (float) Math.PI;
                } else angle -= tickDelta * 0.5;
            }

            if (angle < 0) angle = 0;
            if (angle > TWO_PI) angle = 0;
        }

        //Translate up so we rotate around the center of the item entity
        matrices.translate(0, 0.125f, 0);

        //Spin the item and store the value inside it should it hit the ground next tick
        matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) (angle + HALF_PI)));
        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(angle));
        matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(angle));
        rotator.setRotation(angle);

        //Restore the origin position
        matrices.translate(0, -0.125f, 0);

        //Translate so that the origin gets moved back for stacks with multiple items rendered
        matrices.translate(0, 0, ((0.09375 - (renderCount * 0.1)) * 0.5) * scaleZ);

        // Translate block items down because for some reason they like to float otherwise
        if (itemIsActualBlock) matrices.translate(0, -0.25 * scaleY, 0);

        float x;
        float y;

        for (int i = 0; i < renderCount; ++i) {

            //Only apply random transformation to the current item
            matrices.push();

            //Only apply transformations to items from the second one onwards
            if (i > 0) {

                //Decide whether to use random rotation or positioning based on whether the
                //item has depth, which most of the time means that it's a block
                if (hasDepth) {
                    x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrices.translate(x, y, z);
                } else {
                    matrices.translate(0, 0.125f, 0.0D);
                    matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion((this.random.nextFloat() - 0.5f)));
                    matrices.translate(0, -0.125f, 0.0D);
                }
            }

            this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrices, vertexConsumerProvider, light, OverlayTexture.DEFAULT_UV, bakedModel);

            matrices.pop();

            // Translate normal items to create visual layering
            if (!hasDepth) {
                matrices.translate(0, 0, 0.1F * scaleZ);
            }
        }

        matrices.pop();
        super.render(entity, f, tickDelta, matrices, vertexConsumerProvider, light);
        callback.cancel();
    }
}
