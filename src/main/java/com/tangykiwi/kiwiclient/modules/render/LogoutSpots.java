package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.event.AddEntityPrivateEvent;
import com.tangykiwi.kiwiclient.event.RenderWorldEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.*;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogoutSpots extends Module {
    public LogoutSpots() {
        super("LogoutSpots", "Shows where players logout if in render distance", KEY_UNBOUND, Category.RENDER);
    }

    private static final MeshBuilder MB = new MeshBuilder(64);

    private final List<Entry> players = new ArrayList<>();
    private final List<PlayerListEntry> lastPlayerList = new ArrayList<>();
    private final List<PlayerEntity> lastPlayers = new ArrayList<>();

    private int timer;
    private String lastDimension;

    @Override
    public void onEnable() {
        super.onEnable();
        lastPlayerList.addAll(mc.getNetworkHandler().getPlayerList());
        updateLastPlayers();

        timer = 10;
        lastDimension = getDimension();
    }

    @Subscribe
    private void onEntityAdded(AddEntityPrivateEvent event) {
        if (event.entity instanceof PlayerEntity) {
            int toRemove = -1;

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).uuid.equals(event.entity.getUuid())) {
                    toRemove = i;
                    break;
                }
            }

            if (toRemove != -1) {
                players.remove(toRemove);
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.getNetworkHandler().getPlayerList().size() != lastPlayerList.size()) {
            for (PlayerListEntry entry : lastPlayerList) {
                if (mc.getNetworkHandler().getPlayerList().stream().anyMatch(playerListEntry -> playerListEntry.getProfile().equals(entry.getProfile()))) continue;

                for (PlayerEntity player : lastPlayers) {
                    if (player.getUuid().equals(entry.getProfile().getId())) {
                        add(new Entry(player));
                    }
                }
            }

            lastPlayerList.clear();
            lastPlayerList.addAll(mc.getNetworkHandler().getPlayerList());
            updateLastPlayers();
        }

        if (timer <= 0) {
            updateLastPlayers();
            timer = 10;
        } else {
            timer--;
        }

        String dimension = getDimension();
        if(!dimension.equals(lastDimension)) players.clear();
        lastDimension = dimension;
    }

    @Subscribe
    private void onRender(RenderWorldEvent event) {
        for (Entry player : players) player.render(event);

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        DiffuseLighting.disable();
        RenderSystem.enableBlend();
    }

    @Override
    public void onDisable() {
        players.clear();
        lastPlayerList.clear();
    }

    private void updateLastPlayers() {
        lastPlayers.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity) lastPlayers.add((PlayerEntity) entity);
        }
    }

    private String getDimension() {
        switch (mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether": return "nether";
            case "the_end":    return "end";
            default:           return "overworld";
        }
    }

    private void add(Entry entry) {
        players.removeIf(player -> player.uuid.equals(entry.uuid));
        players.add(entry);
    }

    private class Entry {
        public final double x, y, z;
        public final double xWidth, zWidth, height;

        public final UUID uuid;
        public final String name;
        public final int health, maxHealth;
        public final String healthText;

        public Entry(PlayerEntity entity) {
            x = entity.getX();
            y = entity.getY();
            z = entity.getZ();

            xWidth = entity.getBoundingBox().getXLength();
            zWidth = entity.getBoundingBox().getZLength();
            height = entity.getBoundingBox().getYLength();

            uuid = entity.getUuid();
            name = entity.getGameProfile().getName();
            health = Math.round(entity.getHealth() + entity.getAbsorptionAmount());
            maxHealth = Math.round(entity.getMaxHealth() + entity.getAbsorptionAmount());

            healthText = " " + health;
        }

        public void render(RenderWorldEvent event) {
            Camera camera = mc.gameRenderer.getCamera();

            // Compute scale
            double scale = 0.025;
            double dist = Math.sqrt(mc.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z));
            if (dist > 8) scale *= dist / 8;

            if (dist > mc.options.viewDistance * 16) return;

            // Compute health things
            double healthPercentage = (double) health / maxHealth;

            // Render quad
            CustomRenderer.boxWithLines(CustomRenderer.NORMAL, CustomRenderer.LINES, x, y, z, x + xWidth, y + height, z + zWidth, new CustomColor(255, 0, 255, 55), new CustomColor(255, 0, 255), ShapeMode.Both, 0);

            // Get health color
            CustomColor healthColor;
            if (healthPercentage <= 0.333) healthColor = new CustomColor(225, 25, 25); // red
            else if (healthPercentage <= 0.666) healthColor = new CustomColor(225, 105, 25); // orange
            else healthColor = new CustomColor(25, 225, 25); // green

            // Setup the rotation
            CustomMatrix.push();
            CustomMatrix.translate(x + xWidth / 2 - event.offsetX, y + height + 0.5 - event.offsetY, z + zWidth / 2 - event.offsetZ);
            CustomMatrix.rotate(-camera.getYaw(), 0, 1, 0);
            CustomMatrix.rotate(camera.getPitch(), 1, 0, 0);
            CustomMatrix.scale(-scale, -scale, scale);

            // Render background
            TextRenderer textRenderer = mc.textRenderer;
            double i = textRenderer.getWidth(name) / 2.0 + textRenderer.getWidth(healthText) / 2.0;
            MB.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            MB.quad(-i - 1, -1, 0, -i - 1, 8, 0, i + 1, 8, 0, i + 1, -1, 0, new CustomColor(0, 0,0, 75));
            MB.end();

            // Render name and health texts
            double hX = render(textRenderer, name, -i, 0, new CustomColor(255, 255, 255));
            render(textRenderer, healthText, hX, 0, healthColor);

            CustomMatrix.pop();
        }

        public double render(TextRenderer textRenderer, String text, double x, double y, CustomColor customColor) {
            CustomMatrix.push();

            x += 0.5;
            y += 0.5;

            RenderSystem.disableDepthTest();
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            double r = textRenderer.draw(text, (float) x, (float) y, customColor.getPacked(), false, CustomMatrix.getTop(), immediate, true, 0, 15728880);
            immediate.draw();
            RenderSystem.enableDepthTest();

            CustomMatrix.pop();
            return r;
        }
    }
}
