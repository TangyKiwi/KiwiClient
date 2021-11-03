package com.tangykiwi.kiwiclient.gui.mainmenu.particles;

import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.color.LineColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleManager {
    private List<Particle> particles = new CopyOnWriteArrayList<>();
    public MinecraftClient mc = MinecraftClient.getInstance();

    public ParticleManager() {
        getParticles().clear();
    }

    public void render(MatrixStack m, int x, int y) {
        int rand;
        if (getParticles().size() <= 5000) {
            for (int i = 0; i < 5; i++) {
                rand = random(0, 6);
                if (rand == 1) {
                    getParticles().add(new TopLeftParticle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 2), 0.15f, random(90, 150)));
                }
                if (rand == 2) {
                    getParticles().add(new GravityParticle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 3), 0.15f, random(90, 160)));
                }
                if (rand == 3) {
                    getParticles().add(new TopRightParticle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 3), 0.15f, random(90, 160)));
                }
                if (rand == 4) {
                    getParticles().add(new BottomLeftParticle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 3), 0.15f, random(90, 160)));
                }
                if (rand == 5) {
                    getParticles().add(new BottomRightParticle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 3), 0.15f, random(90, 160)));
                }
            }
        }
        int count = 0;
        for (Particle p : getParticles()) {
            if (p.getAlpha() <= 0.0F) {
                getParticles().remove(p);
            }

            p.render(m, this);

            // Particles
            //drawConnections(count);
            count++;
        }
    }

    public void drawConnections(int i) {
        if(i < particles.size()) {
            for(int j = 0; j < particles.size(); j++) {
                if(j != i) {
                    Particle pi = particles.get(i);
                    Particle pj = particles.get(j);
                    int alpha = (int) ((pi.getAlpha() + pj.getAlpha()) / 2);
                    if(getDistance(pi, pj) <= 30) {
                        RenderUtils.drawLine2D(pi.getPosX(), pi.getPosY(), pj.getPosX(), pj.getPosY(), LineColor.single(255, 255, 255, alpha), 1);
                    }
                }
            }
        }
    }

    public double getDistance(Particle x, Particle y) {
        return Math.sqrt(Math.pow(x.getPosX() - y.getPosX(), 2) + Math.pow(x.getPosY() - y.getPosY(), 2));
    }

    public int random(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low + 1) + low;
    }

    public int getScaledWidth() {
        return mc.getWindow().getScaledWidth();
    }

    public int getScaledHeight() {
        return mc.getWindow().getScaledHeight();
    }

    public int centerWidth() {
        return mc.getWindow().getScaledWidth() / 2;
    }

    public int centerHeight() {
        return mc.getWindow().getScaledHeight() / 2;
    }

    public List<Particle> getParticles() {
        return particles;
    }

}
