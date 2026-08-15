package com.tangykiwi.kiwiclient.event;

import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public class PlayerMoveEvent extends Event {
    private MoverType moverType;
    private Vec3 vec;

    public PlayerMoveEvent(MoverType moverType, Vec3 vec) {
        this.moverType = moverType;
        this.vec = vec;
    }

    public MoverType getMoverType() {
        return moverType;
    }

    public Vec3 getVec() {
        return vec;
    }

    public void setMoverType(MoverType moverType) {
        this.moverType = moverType;
    }

    public void setVec(Vec3 vec) {
        this.vec = vec;
    }
}
