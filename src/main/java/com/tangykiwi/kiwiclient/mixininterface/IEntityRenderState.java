package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface IEntityRenderState {
    // void setLabel(Component label);
    // void setLabelPos(Vec3 pos);
    void setEntity(Entity entity);

    // Component getLabel();
    // Vec3 getLabelPos();
    Entity getEntity();
}
