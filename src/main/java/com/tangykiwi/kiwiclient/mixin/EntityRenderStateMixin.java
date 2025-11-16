package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.tangykiwi.kiwiclient.mixininterface.IEntityRenderState;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IEntityRenderState {
    @Unique
    private Text label;

    @Unique
    private Vec3d labelPos;

    @Override
    public void setLabel(Text label) {
        this.label = label;
    }

    @Override
    public Text getLabel() {
        return this.label;
    }

    @Override
    public void setLabelPos(Vec3d pos) {
        this.labelPos = pos;
    }
    
    @Override
    public Vec3d getLabelPos() {
        return this.labelPos;
    }
}
