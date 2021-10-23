package com.tangykiwi.kiwiclient.gui.mainmenu.particles;

import com.tangykiwi.kiwiclient.util.CustomColor;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class TopLeftParticle extends Particle {
    public TopLeftParticle(float posX, float posY, float size, float speed, float alpha) {
        super(posX, posY, size, speed, alpha);
    }

    public void render(MatrixStack m, ParticleManager p) {
        super.render(m, p);
        setPosY(getPosY() + getSpeed());
        setPosX(getPosX() + getSpeed());
        RenderUtils.drawFullCircle(m, getPosX(), getPosY(), getSize(), CustomColor.fromRGBA(255, 255, 255, (int) getAlpha()));
    }
}