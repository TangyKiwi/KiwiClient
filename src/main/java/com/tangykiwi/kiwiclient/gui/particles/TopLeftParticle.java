package com.tangykiwi.kiwiclient.gui.particles;

import com.tangykiwi.kiwiclient.util.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class TopLeftParticle extends Particle {
    public TopLeftParticle(float posX, float posY, float size, float speed, float alpha) {
        super(posX, posY, size, speed, alpha);
    }

    public void render(MatrixStack m, ParticleManager p) {
        super.render(m, p);
        setPosY(getPosY() + getSpeed());
        setPosX(getPosX() + getSpeed());
        RenderUtils.drawCircle(m, getPosX(), getPosY(), getSize(), new Color(255, 255, 255, (int) getAlpha()).getRGB());    }
}