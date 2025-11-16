package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public interface IEntityRenderState {
    void setLabel(Text label);
    void setLabelPos(Vec3d pos);

    Text getLabel();
    Vec3d getLabelPos();
}
