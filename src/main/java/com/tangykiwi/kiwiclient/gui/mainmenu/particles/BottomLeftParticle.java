package com.tangykiwi.kiwiclient.gui.mainmenu.particles;

import com.tangykiwi.kiwiclient.util.render.color.CustomColor;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

public class BottomLeftParticle extends Particle {
    public BottomLeftParticle(float posX, float posY, float size, float speed, float alpha) {
        super(posX, posY, size, speed, alpha);
    }

    public void render(MatrixStack m, ParticleManager p) {
        super.render(m, p);
        setPosY(getPosY() - getSpeed());
        setPosX(getPosX() + getSpeed());
        RenderUtils.drawFullCircle(m, getPosX(), getPosY(), getSize(), CustomColor.fromRGBA(255, 255, 255, (int) getAlpha()));
    }
}
