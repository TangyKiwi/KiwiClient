package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin extends BipedEntityModel {
    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(
            method = {"getTexturedModelData(Lnet/minecraft/client/model/Dilation;Z)Lnet/minecraft/client/model/ModelData;"},
            at = {@At("RETURN")},
            cancellable = true
    )
    private static void getTexturedModelData(Dilation dilation, boolean slim, CallbackInfoReturnable<ModelData> cir) {
        ModelData modelData = (ModelData)cir.getReturnValue();
        modelData.getRoot().addChild("ear", ModelPartBuilder.create(), ModelTransform.NONE);
        ModelPartData modelPartData = modelData.getRoot().getChild("ear");
        modelPartData.addChild("left_ear", ModelPartBuilder.create().uv(0, 0).cuboid(2.0F, -12.0F, -1.0F, 6.0F, 6.0F, 1.0F, dilation, 0.21875F, 0.109375F), ModelTransform.NONE);
        modelPartData.addChild("right_ear", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -12.0F, -1.0F, 6.0F, 6.0F, 1.0F, dilation, 0.21875F, 0.109375F), ModelTransform.NONE);
        cir.setReturnValue(modelData);
    }
}
