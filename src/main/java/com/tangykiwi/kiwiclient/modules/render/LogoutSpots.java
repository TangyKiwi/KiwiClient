package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.PlayerCopyEntity;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutSpots extends Module {

    private final Map<UUID, Pair<PlayerCopyEntity, Long>> players = new ConcurrentHashMap<>();

    public LogoutSpots() {
        super("LogoutSpots", "Shows logout locations of players near you", KEY_UNBOUND, Category.RENDER,
            new ToggleSetting("Text", true).withDesc("Adds text next to players.").withChildren(
                new ToggleSetting("Name", true).withDesc("Shows the name of the logged player."),
                new ToggleSetting("Coords", false).withDesc("Shows the coords of the logged player."),
                new ToggleSetting("Health", true).withDesc("Shows the health of the logged player."),
                new ToggleSetting("Time", true).withDesc("Shows the time ago the player logged.")),
            new ToggleSetting("Ghost", true).withDesc("Makes the logout spot players transparent."));
    }

    @Override
    public void onDisable() {
        players.clear();
        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onReadPacket(ReceivePacketEvent event) {
        if (!(event.getPacket() instanceof PlayerListS2CPacket) || mc.world == null) {
            return;
        }

        PlayerListS2CPacket list = (PlayerListS2CPacket) event.getPacket();

        // Spawns fake player when player leaves
        if (list.getActions().contains(PlayerListS2CPacket.Action.UPDATE_LISTED)) {
            for (PlayerListS2CPacket.Entry entry : list.getEntries()) {
                // player leave
                if (!entry.listed()) {
                    PlayerEntity player = mc.world.getPlayerByUuid(entry.profileId());

                    if (player != null && !mc.player.equals(player) && !players.containsKey(player.getUuid())) {
                        PlayerCopyEntity copy = new PlayerCopyEntity(player);
                        players.put(player.getUuid(), Pair.of(copy, System.currentTimeMillis()));
                        copy.spawn();
                    }
                } else { // player join
                    Pair<PlayerCopyEntity, Long> fakePlayer = players.remove(entry.profileId());

                    if (fakePlayer != null && mc.world != null) {
                        fakePlayer.getLeft().despawn();
                    }
                }

            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPostEntityRender(EntityRenderEvent.PostAll event) {
        for (Pair<PlayerCopyEntity, Long> playerPair: players.values()) {
            if (getSetting(0).asToggle().state) {
                PlayerCopyEntity player = playerPair.getLeft();

                Vec3d rVec = new Vec3d(player.lastRenderX + (player.getX() - player.lastRenderX) * mc.getTickDelta(),
                        player.lastRenderY + (player.getY() - player.lastRenderY) * mc.getTickDelta() + player.getHeight(),
                        player.lastRenderZ + (player.getZ() - player.lastRenderZ) * mc.getTickDelta());

                Vec3d offset = new Vec3d(0, 0, 0.45 + mc.textRenderer.getWidth(player.getDisplayName().getString()) / 90d)
                        .rotateY((float) -Math.toRadians(mc.player.getYaw() + 90));

                List<String> lines = new ArrayList<>();
                lines.add("Logout:");

                if (getSetting(0).asToggle().getChild(0).asToggle().state)
                    lines.add(player.getDisplayName().getString());

                if (getSetting(0).asToggle().getChild(1).asToggle().state)
                    lines.add((int) player.getX() + " " + (int) player.getY() + " " + (int) player.getZ());

                if (getSetting(0).asToggle().getChild(2).asToggle().state)
                    lines.add((int) Math.ceil(player.getHealth() + player.getAbsorptionAmount()) + "hp");

                if (getSetting(0).asToggle().getChild(3).asToggle().state)
                    lines.add(getTimeElapsed(playerPair.getRight()));

                for (int i = 0; i < lines.size(); i++) {
                    RenderUtils.drawWorldText(lines.get(i), rVec.x + offset.x, rVec.y + 0.1 - i * 0.25, rVec.z + offset.z, 1.0, 0xFFFFFF, true);
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onOpenScreen(OpenScreenEvent event) {
        if (event.getScreen() instanceof DisconnectedScreen) {
            players.clear();
        }
    }

    private String getTimeElapsed(long time) {
        long timeDiff = (System.currentTimeMillis() - time) / 1000L;

        if (timeDiff < 60L) {
            return String.format(Locale.ENGLISH, "%ds", timeDiff);
        }

        if (timeDiff < 3600L) {
            return String.format(Locale.ENGLISH, "%dm %ds", timeDiff / 60L, timeDiff % 60L);
        }

        if (timeDiff < 86400L) {
            return String.format(Locale.ENGLISH, "%dh %dm", timeDiff / 3600L, timeDiff / 60L % 60L);
        }

        return String.format(Locale.ENGLISH, "%dd %dh", timeDiff / 86400L, timeDiff / 3600L % 24L);
    }
}
