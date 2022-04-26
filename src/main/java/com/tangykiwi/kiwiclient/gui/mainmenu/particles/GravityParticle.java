package com.tangykiwi.kiwiclient.gui.mainmenu.particles;

import com.tangykiwi.kiwiclient.util.render.color.CustomColor;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class GravityParticle extends Particle {

    public GravityParticle(float posX, float posY, float size, float speed, float alpha) {
        super(posX, posY, size, speed, alpha);
    }

    public void render(MatrixStack m, ParticleManager p) {
        super.render(m, p);
        setPosY(getPosY() + getSpeed());
        RenderUtils.drawFullCircle(m, getPosX(), getPosY(), getSize(), CustomColor.fromRGBA(255, 255, 255, (int) getAlpha()));
    }
}
