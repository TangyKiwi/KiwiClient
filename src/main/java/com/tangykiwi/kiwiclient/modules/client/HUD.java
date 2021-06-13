package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.Settings;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    
    private double tps = 20;
    private double bps = 0;
    private int fps = 0;
    private long lastPacked = 0;
    private long timer = 0;
    private int ping = 0;
    private String ip = "";
    private float speed;

    private int defaultColor = 0xFFAA00;

    private MatrixStack matrixStack = new MatrixStack();
    
    public List<String> info = new ArrayList<>();
    
    public HUD() {
        super("HUD", "Shows info as an overlay", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("FPS", true).withDesc("Shows FPS").withValue(0),
            new ToggleSetting("Ping", true).withDesc("Shows Ping").withValue(1),
            new ToggleSetting("IP", false).withDesc("Shows Server Address").withValue(2),
            new ToggleSetting("BPS", false).withDesc("Shows Player Speed").withValue(3),
            new ToggleSetting("Coords", true).withDesc("Shows Player Position").withValue(4),
            new ToggleSetting("Alternate Coords", true).withDesc("Shows Nether/Overworld Position").withValue(5),
            new ToggleSetting("Armor", true).withDesc("Shows Armor Status")
            //new ToggleSetting("Watermark", true).withDesc("KiwiClient Watermark")
        );
        super.toggle();
    }
    
    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            TextRenderer textRenderer = mc.textRenderer;

            List<Settings> settings = getSettings();
            int counter = 0;
            for(int i = 5; i >= 0; i--) {
                if(settings.get(i).asToggle().state) {
                    counter++;
                    drawSetting(textRenderer, e.matrix, settings.get(i).asToggle().getValue(), (counter) * 10);
                }
            }

            if(settings.get(6).asToggle().state && !mc.player.isSpectator()) {
                int count = 0;
                int x1 = mc.getWindow().getScaledWidth() / 2;
                int y = mc.getWindow().getScaledHeight() -
                        (mc.player.isSubmergedInWater() || mc.player.getAir() < mc.player.getMaxAir() ? 66 : 56);
                for (ItemStack is : mc.player.getInventory().armor) {
                    count++;
                    if (is.isEmpty()) continue;
                    int x = x1 - 90 + (9 - count) * 20 + 2;

                    RenderSystem.enableDepthTest();
                    mc.getItemRenderer().zOffset = 200F;
                    mc.getItemRenderer().renderGuiItemIcon(is, x, y);
                    mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, is, x, y);

                    mc.getItemRenderer().zOffset = 0F;

                    e.matrix.push();
                    e.matrix.scale(0.75F, 0.75F, 0.75F);

                    RenderSystem.disableDepthTest();
                    String s = is.getCount() > 1 ? "x" + is.getCount() : "";
                    mc.textRenderer.drawWithShadow(e.matrix, s, (x + 19 - mc.textRenderer.getWidth(s)) * 1.333f, (y + 9) * 1.333f, ColorUtil.guiColour());

                    if (is.isDamageable()) {
                        String dur = is.getMaxDamage() - is.getDamage() + "";
                        int durcolor = ColorUtil.guiColour();
                        try {
                            durcolor = MathHelper.hsvToRgb(((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
                        } catch (Exception exception) {
                        }

                        mc.textRenderer.drawWithShadow(e.matrix, dur, (x + 10 - mc.textRenderer.getWidth(dur) / 2) * 1.333f, (y - 3) * 1.333f, durcolor);
                    }

                    RenderSystem.enableDepthTest();
                    e.matrix.pop();
                }

                e.matrix.pop();
            }
        }
    }

    public void drawSetting(TextRenderer textRenderer, MatrixStack m, int i, int offset) {
        switch(i) {
            case 0:
                this.fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
                textRenderer.draw(m, String.format("FPS: %d", fps), 2, mc.getWindow().getScaledHeight() - offset, ColorUtil.getColorString(fps, 80, 60, 30, 15, 10, false));
                break;
            case 1:
                PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
                this.ping = playerEntry == null ? 0 : playerEntry.getLatency();
                textRenderer.draw(m, String.format("Ping: %d", ping), 2, mc.getWindow().getScaledHeight() - offset, ColorUtil.getColorString(ping, 10, 20, 50, 75, 100, true));
                break;
            case 2:
                this.ip = mc.getCurrentServerEntry() == null ? "IP: Singleplayer" : "IP: " + mc.getCurrentServerEntry().address;
                textRenderer.draw(m, ip, 2, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 3:
                speed = mc.player.getSpeed();
                textRenderer.draw(m, String.format("Speed: %.1f b/s", speed), 2, mc.getWindow().getScaledHeight() - offset, ColorUtil.getColorString(Math.round(speed), 1, 5, 10, 20, 50, true));
                break;
            case 4:
                Vec3d vec = mc.player.getPos();
                float yaw = MathHelper.wrapDegrees(mc.getCameraEntity().getYaw());
                String dir = "";
                if(yaw > 157.5) dir = "N -Z";
                else if(yaw >= 112.5) dir = "NW -X, -Z";
                else if(yaw > 67.5) dir = "W -X";
                else if(yaw >= 22.5) dir = "SW -X, +Z";
                else if(yaw > -22.5) dir = "S +Z";
                else if(yaw >= -67.5) dir = "SE +X, +Z";
                else if(yaw > -112.5) dir = "E +X";
                else if(yaw >= -157.5) dir = "NE +X, -Z";
                else dir = "N -Z";

                textRenderer.draw(m, String.format("X: %.1f Y: %.1f Z: %.1f " + dir, vec.x, vec.y, vec.z), 2, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 5:
                Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
                Vec3d vec2 = mc.player.getPos();
                double altx = vec2.x / 8;
                double altz = vec2.z / 8;

                if (nether) {
                    altx = vec2.x * 8;
                    altz = vec2.z * 8;
                }
                if (nether) textRenderer.draw(m, String.format("(Overworld) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), 2, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                else textRenderer.draw(m, String.format("(Nether) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), 2, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 6:
                textRenderer.draw(m, String.format("%s v%s", KiwiClient.name, KiwiClient.version), 2, mc.getWindow().getScaledHeight() - offset, ColorUtil.getRainbow(3, 0.8f, 1));
                break;
        }
    }
}