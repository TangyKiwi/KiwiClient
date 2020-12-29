package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;

import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;
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

    private MatrixStack matrixStack = new MatrixStack();
    
    public List<String> info = new ArrayList<>();
    
    public HUD() {
        super("HUD", "Shows info as an overlay", GLFW.GLFW_KEY_H, Category.RENDER);
    }
    
    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            TextRenderer textRenderer = mc.textRenderer;
            // info.clear();
            // // coords
            // Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
            // BlockPos pos = mc.player.getBlockPos();
            Vec3d vec = mc.player.getPos();

            // BlockPos position = nether ? new BlockPos(vec.getX() * 8, vec.getY() * 8, vec.getZ() * 8) : new BlockPos(vec.getX() / 8, vec.getY() / 8, vec.getZ() / 8);

            // ip
            // this.ip = mc.getCurrentServerEntry() == null ? "Singleplayer" : mc.getCurrentServerEntry().address;
            // fps
            this.fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
            textRenderer.draw(e.matrix, String.format("FPS: %d", fps), 2, mc.getWindow().getScaledHeight() - 30, ColorUtil.getColorString(fps, 80, 60, 30, 15, 10, false));


            // ping
            PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
            this.ping = playerEntry == null ? 0 : playerEntry.getLatency();


            textRenderer.draw(e.matrix, "X: " + (int) vec.x + " Y: " + (int) vec.y + " Z: " + (int) vec.z, 2, mc.getWindow().getScaledHeight() - 10, ColorUtil.getColorString(ping, 75, 180, 300, 500, 1000, true));
            textRenderer.draw(e.matrix, String.format("Ping: %d", ping), 2, mc.getWindow().getScaledHeight() - 40, ColorUtil.getColorString(ping, 75, 180, 300, 500, 1000, true));
            textRenderer.draw(e.matrix, "KiwiClient 1.0.0", 2, mc.getWindow().getScaledHeight() - 20, ColorUtil.getColorString(ping, 75, 180, 300, 500, 1000, true));

        }
    }
}