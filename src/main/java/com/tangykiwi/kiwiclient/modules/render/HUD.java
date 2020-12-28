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

    private TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    // PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
    private MatrixStack matrixStack = new MatrixStack();

    public List<String> info = new ArrayList<>();

    public HUD() {
        super("HUD", "Shows info as an overlay", GLFW.GLFW_KEY_H, Category.RENDER);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        // info.clear();
        // // coords
        // Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
        // BlockPos pos = mc.player.getBlockPos();
        // Vec3d vec = mc.player.getPos();
        // BlockPos position = nether ? new BlockPos(vec.getX() * 8, vec.getY() * 8, vec.getZ() * 8) : new BlockPos(vec.getX() / 8, vec.getY() / 8, vec.getZ() / 8);

        // // ip
        // this.ip = mc.getCurrentServerEntry() == null ? "Singleplayer" : mc.getCurrentServerEntry().address;
        // fps
        this.fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
        
        // ping
        // this.ping = playerEntry == null ? 0 : playerEntry.getLatency();
        
        textRenderer.draw(e.matrix, String.format("FPS:%d", fps), 10, mc.getWindow().getScaledHeight() - 20, ColorUtil.getColorString(fps, 120, 60, 30, 15, 10).getRGB());
    }
}