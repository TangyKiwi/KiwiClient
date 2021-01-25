package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.ItemPhysics;
import com.tangykiwi.kiwiclient.util.ItemEntityRotator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    @Shadow @Final private Random random;
    @Shadow @Final private ItemRenderer itemRenderer;
    @Shadow protected abstract int getRenderedAmount(ItemStack stack);

    private ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(ItemEntity dropped, float f, float partialTicks, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callback) {
        if(KiwiClient.moduleManager.getModule(ItemPhysics.class).isEnabled()) {
            ItemStack itemStack = dropped.getStack();

            int seed = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
            this.random.setSeed(seed);

            matrix.push();
            BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, dropped.world, null);
            boolean hasDepthInGui = bakedModel.hasDepth();

            int renderCount = this.getRenderedAmount(itemStack);

            ItemEntityRotator rotator = (ItemEntityRotator) dropped;

            boolean renderBlockFlat = false;
            if (dropped.getStack().getItem() instanceof BlockItem && !(dropped.getStack().getItem() instanceof AliasedBlockItem)) {
                Block b = ((BlockItem) dropped.getStack().getItem()).getBlock();
                VoxelShape shape = b.getOutlineShape(b.getDefaultState(), dropped.world, dropped.getBlockPos(), ShapeContext.absent());

                if (shape.getMax(Direction.Axis.Y) <= .5) {
                    renderBlockFlat = true;
                }
            }

            Item item = dropped.getStack().getItem();
            if (item instanceof BlockItem && !(item instanceof AliasedBlockItem) && !renderBlockFlat) {
                matrix.translate(0, -0.06, 0);
            }

            if (!renderBlockFlat) {
                matrix.translate(0, .185, .0);
                matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(1.571F));
                matrix.translate(0, -.185, -.0);
            }

            boolean isAboveWater = dropped.world.getBlockState(dropped.getBlockPos()).getFluidState().getFluid().isIn(FluidTags.WATER);
            if (!dropped.isOnGround() && (!dropped.isSubmergedInWater() && !isAboveWater)) {
                float rotation = ((float) dropped.getAge() + partialTicks) / 20.0F + dropped.hoverHeight;

                if (!renderBlockFlat) {
                    matrix.translate(0, .185, .0);
                    matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(rotation));
                    matrix.translate(0, -.185, .0);

                    rotator.setRotation(new Vec3d(0, 0, rotation));
                } else {
                    matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(rotation));

                    rotator.setRotation(new Vec3d(0, rotation, 0));

                    matrix.translate(0, -.065, 0);
                }

                if (dropped.getStack().getItem() instanceof AliasedBlockItem) {
                    matrix.translate(0, 0, .195);
                } else if (!(dropped.getStack().getItem() instanceof BlockItem)) {
                    matrix.translate(0, 0, .195);
                }
            } else if (dropped.getStack().getItem() instanceof AliasedBlockItem) {
                matrix.translate(0, .185, .0);
                matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion((float) rotator.getRotation().z));
                matrix.translate(0, -.185, .0);

                matrix.translate(0, 0, .195);
            } else if (renderBlockFlat) {
                matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float) rotator.getRotation().y));

                matrix.translate(0, -.065, 0);
            } else {
                if (!(dropped.getStack().getItem() instanceof BlockItem)) {
                    matrix.translate(0, 0, .195);
                }

                matrix.translate(0, .185, .0);
                matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion((float) rotator.getRotation().z));
                matrix.translate(0, -.185, .0);
            }

            if (dropped.world.getBlockState(dropped.getBlockPos()).getBlock().equals(Blocks.SOUL_SAND)) {
                matrix.translate(0, 0, -.1);
            }

            if (dropped.getStack().getItem() instanceof BlockItem) {
                if (((BlockItem) dropped.getStack().getItem()).getBlock() instanceof SkullBlock) {
                    matrix.translate(0, .11, 0);
                }
            }

            float scaleX = bakedModel.getTransformation().ground.scale.getX();
            float scaleY = bakedModel.getTransformation().ground.scale.getY();
            float scaleZ = bakedModel.getTransformation().ground.scale.getZ();

            float x;
            float y;
            if (!hasDepthInGui) {
                float r = -0.0F * (float) (renderCount) * 0.5F * scaleX;
                x = -0.0F * (float) (renderCount) * 0.5F * scaleY;
                y = -0.09375F * (float) (renderCount) * 0.5F * scaleZ;
                matrix.translate(r, x, y);
            }

            for (int u = 0; u < renderCount; ++u) {
                matrix.push();

                if (u > 0) {
                    if (hasDepthInGui) {
                        x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float z = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        matrix.translate(x, y, z);
                    } else {
                        x = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        y = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                        matrix.translate(x, y, 0.0D);
                        matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.random.nextFloat()));
                    }
                }

                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrix, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
                matrix.pop();
                if (!hasDepthInGui) {
                    matrix.translate(0.0F * scaleX, 0.0F * scaleY, 0.0625F * scaleZ);
                }
            }

            matrix.pop();
            super.render(dropped, f, partialTicks, matrix, vertexConsumerProvider, i);
            callback.cancel();
        }
    }
}
