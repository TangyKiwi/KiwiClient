package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;

import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.ArrayList;

public class HUD extends Module {
    
    private double tps = 20;
    private double bps = 0;
    private int fps = 0;
    private long lastPacked = 0;
    private long timer = 0;
    private int ping = 0;
    private String ip = "";
    private float speed;

    private MatrixStack matrixStack = new MatrixStack();
    
    public List<String> info = new ArrayList<>();
    
    public HUD() {
        super("HUD", "Shows info as an overlay", GLFW.GLFW_KEY_H, Category.RENDER,
            new ToggleSetting("IP", true).withDesc("Shows Server Address"),
            new ToggleSetting("FPS", true).withDesc("Shows FPS"),
            new ToggleSetting("Ping", true).withDesc("Shows Ping"),
            new ToggleSetting("Coords", true).withDesc("Shows Player Position"),
            new ToggleSetting("Watermark", true).withDesc("KiwiClient Watermark")
        
        );
    }
    
    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            TextRenderer textRenderer = mc.textRenderer;
            // info.clear();
            // coords

            //speed = mc.player.getSpeed();

            if (getSetting(0).asToggle().state) {
                this.ip = mc.getCurrentServerEntry() == null ? "Singleplayer" : mc.getCurrentServerEntry().address;
            }
            
            if (getSetting(1).asToggle().state) {
                this.fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
                textRenderer.draw(e.matrix, String.format("FPS: %d", fps), 2, mc.getWindow().getScaledHeight() - 40, ColorUtil.getColorString(fps, 80, 60, 30, 15, 10, false));                
            }

            if (getSetting(2).asToggle().state) {
                PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
                this.ping = playerEntry == null ? 0 : playerEntry.getLatency();                
                textRenderer.draw(e.matrix, String.format("Ping: %d", ping), 2, mc.getWindow().getScaledHeight() - 50, ColorUtil.getColorString(ping, 10, 20, 50, 75, 100, true));
            }

            if (getSetting(3).asToggle().state) {
                Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
                Vec3d vec = mc.player.getPos();
                double altx = vec.x / 8;
                double altz = vec.z / 8;
    
                if (nether) {
                    altx = vec.x * 8;
                    altz = vec.z * 8;
                }
                if (nether) {
                    textRenderer.draw(e.matrix, String.format("(Overworld) X: %.1f Y: %.1f Z: %.1f", altx, vec.y, altz), 2, mc.getWindow().getScaledHeight() - 10, 0xFFAA00); 
                } else {
                    textRenderer.draw(e.matrix, String.format("(Nether) X: %.1f Y: %.1f Z: %.1f", altx, vec.y, altz), 2, mc.getWindow().getScaledHeight() - 10, 0xFFAA00);
                }        
                textRenderer.draw(e.matrix, String.format("X: %.1f Y: %.1f Z: %.1f", vec.x, vec.y, vec.z), 2, mc.getWindow().getScaledHeight() - 20, 0xFFAA00);
            }

            if (getSetting(4).asToggle().state) {
                textRenderer.draw(e.matrix, String.format("%s v%s", KiwiClient.name, KiwiClient.version), 2, mc.getWindow().getScaledHeight() - 30, 0xFFAA00);
            }
        }
    }
}