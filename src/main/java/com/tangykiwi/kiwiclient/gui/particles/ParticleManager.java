package com.tangykiwi.kiwiclient.gui.particles;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.util.RenderUtils;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleManager {
    private List<Particle> particles = new CopyOnWriteArrayList<>();

    public ParticleManager() {
        particles.clear();
    }

    public void render(MatrixStack m, int x, int y) {
        int rand;
        if (particles.size() <= 1000) {
            for (int i = 0; i < 5; i++) {
                rand = random(0, 4);
                Particle particle = new Particle(centerWidth() + random(-getScaledWidth(), getScaledWidth()), centerHeight() + random(-getScaledHeight(), getScaledHeight()), random(1, 2), 0.15f, random(90, 150));
                if (rand == 0) {
                    // down right
                    particle.setDx(1);
                    particle.setDy(1);
                }
                else if (rand == 1) {
                    // gravity
                    particle.setDx(0);
                    particle.setDy(1);
                }
                else if (rand == 2) {
                    // up right
                    particle.setDx(-1);
                    particle.setDy(-1);
                }
                else if (rand == 3) {
                    // up left
                    particle.setDx(1);
                    particle.setDy(-1);
                }
                else if (rand == 4) {
                    // down left
                    particle.setDx(-1);
                    particle.setDy(1);
                }
                particles.add(particle);
            }
        }
        for (Particle p : particles) {
            if (p.getAlpha() <= 0.0F) {
                particles.remove(p);
            }

            p.render(m);
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
        return KiwiClient.mc.getWindow().getScaledWidth();
    }

    public int getScaledHeight() {
        return KiwiClient.mc.getWindow().getScaledHeight();
    }

    public int centerWidth() {
        return KiwiClient.mc.getWindow().getScaledWidth() / 2;
    }

    public int centerHeight() {
        return KiwiClient.mc.getWindow().getScaledHeight() / 2;
    }
}
