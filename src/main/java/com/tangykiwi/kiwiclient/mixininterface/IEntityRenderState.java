package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public interface IEntityRenderState {
    void setLabel(Text label);
    void setLabelPos(Vec3d pos);
    void setEntity(Entity entity);

    Text getLabel();
    Vec3d getLabelPos();
    Entity getEntity();
}
