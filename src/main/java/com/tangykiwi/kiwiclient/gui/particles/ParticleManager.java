package com.tangykiwi.kiwiclient.gui.particles;

import com.tangykiwi.kiwiclient.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
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
        if (getParticles().size() <= 1000) {
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
        for (Particle p : getParticles()) {
            if (p.getAlpha() <= 0.0F) {
                getParticles().remove(p);
            }

            p.render(m, this);
        }

        drawConnections(m, x, y);
    }

    public void drawConnections(MatrixStack m, int x, int y) {
        List<Particle> nearbyParticles = new ArrayList<>();
        for (Particle p : particles) {
            if (getDistance(x, y, p) <= 30) {
                nearbyParticles.add(p);
            }
        }

        for (int i = 0; i < nearbyParticles.size(); i++) {
            for (int j = i + 1; j < nearbyParticles.size(); j++) {
                Particle pi = nearbyParticles.get(i);
                Particle pj = nearbyParticles.get(j);
                int alpha = (int) ((pi.getAlpha() + pj.getAlpha()) / 2);
                RenderUtils.drawLine2D(m, pi.getPosX(), pi.getPosY(), pj.getPosX(), pj.getPosY(), new Color(255, 255, 255, alpha).getRGB());
            }
        }
    }

    public double getDistance(int x, int y, Particle p) {
        return Math.sqrt(Math.pow(x - p.getPosX(), 2) + Math.pow(y - p.getPosY(), 2));
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
