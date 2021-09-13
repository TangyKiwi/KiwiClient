package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.Settings;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.util.CustomColor;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {
    
    private double tps = 20;
    private double bps = 0;
    private int fps = 0;
    private long lastPacked = 0;
    private long timer = 0;
    private int ping = 0;
    private String ip = "";

    private int defaultColor = 0xFFAA00;

    private MatrixStack matrixStack = new MatrixStack();

    private static Style CUSTOM_STYLE = Style.EMPTY.withFont(new Identifier("kiwiclient", "product_sans"));
    
    public List<String> info = new ArrayList<>();
    
    public HUD() {
        super("HUD", "Shows info as an overlay", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("FPS", true).withDesc("Shows FPS").withValue(0),
            new ToggleSetting("Ping", true).withDesc("Shows Ping").withValue(1),
            new ToggleSetting("IP", false).withDesc("Shows Server Address").withValue(2),
            new ToggleSetting("Biome", true).withDesc("Shows Current Biome").withValue(3),
            new ToggleSetting("Speed", true).withDesc("Shows Player Speed").withValue(4),
            new ToggleSetting("Coords", true).withDesc("Shows Player Position").withValue(5),
            new ToggleSetting("Nether Coords", true).withDesc("Shows Nether/Overworld Position").withValue(6),
            new ToggleSetting("Armor", true).withDesc("Shows Armor Status")
            //new ToggleSetting("Watermark", true).withDesc("KiwiClient Watermark")
        );
        super.toggle();
    }
    
    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            //net.minecraft.client.font.TextRenderer textRenderer = mc.textRenderer;
            GlyphPageFontRenderer textRenderer = IFont.CONSOLAS;
            //TextRenderer textRenderer = TextRenderer.get();
            //TextRenderer textRenderer = new TextRenderer((id) -> new FontStorage(mc.getTextureManager(), new Identifier("kiwiclient:fonts/product_sans.json")));

            List<Settings> settings = getSettings();
            int counter = 0;
            for(int i = settings.size() - 2; i >= 0; i--) {
                if(settings.get(i).asToggle().state) {
                    counter++;
                    drawSetting(textRenderer, e.getMatrix(), settings.get(i).asToggle().getValue(), (counter) * 8 + 2);
                    //drawSetting(textRenderer, e.getMatrix(), settings.get(i).asToggle().getValue(), (counter) * 10);
                }
            }

            // Armor HUD
            if(settings.get(7).asToggle().state && !mc.player.isSpectator()) {
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

                    e.getMatrix().push();
                    e.getMatrix().scale(0.75F, 0.75F, 0.75F);

                    RenderSystem.disableDepthTest();
                    String s = is.getCount() > 1 ? "x" + is.getCount() : "";
                    mc.textRenderer.drawWithShadow(e.getMatrix(), s, (x + 19 - mc.textRenderer.getWidth(s)) * 1.333f, (y + 9) * 1.333f, ColorUtil.guiColour());

                    if (is.isDamageable()) {
                        String dur = is.getMaxDamage() - is.getDamage() + "";
                        int durcolor = ColorUtil.guiColour();
                        try {
                            durcolor = MathHelper.hsvToRgb(((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
                        } catch (Exception exception) {
                        }

                        mc.textRenderer.drawWithShadow(e.getMatrix(), dur, (x + 10 - mc.textRenderer.getWidth(dur) / 2) * 1.333f, (y - 3) * 1.333f, durcolor);
                    }

                    RenderSystem.enableDepthTest();
                    e.getMatrix().pop();
                }
            }
        }
    }

    public void drawSetting(GlyphPageFontRenderer textRenderer, MatrixStack m, int i, int offset) {
        switch(i) {
            case 0:
                this.fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
                textRenderer.drawString(m, String.format("FPS: %d", fps), 0.3, mc.getWindow().getScaledHeight() - offset, ColorUtil.getColorString(fps, 80, 60, 30, 15, 10, false));
                break;
            case 1:
                PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
                this.ping = playerEntry == null ? 0 : playerEntry.getLatency();
                textRenderer.drawString(m, String.format("Ping: %d", ping), 0.3, mc.getWindow().getScaledHeight() - offset, ColorUtil.getColorString(ping, 10, 20, 50, 75, 100, true));
                break;
            case 2:
                this.ip = "IP: Singleplayer";
                if(mc.getCurrentServerEntry() != null) {
                    if(mc.isConnectedToRealms()) {
                        this.ip = "IP: " + mc.getCurrentServerEntry().name;
                    }
                    else this.ip = "IP: " + mc.getCurrentServerEntry().address;
                }
                textRenderer.drawString(m, ip, 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 3:
                String biome = "";
                Identifier id = mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(mc.world.getBiome(new BlockPos.Mutable().set(mc.player.getX(), mc.player.getY(), mc.player.getZ())));
                if (id == null) biome = "Unknown";
                else biome = Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
                textRenderer.drawString(m, "Biome: " + biome, 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 4:
                double tX = Math.abs(mc.player.getX() - mc.player.prevX);
                double tZ = Math.abs(mc.player.getZ() - mc.player.prevZ);
                double length = Math.sqrt(tX * tX + tZ * tZ);

                this.bps = length * 20;
                textRenderer.drawString(m, String.format("Speed: %.1f b/s", bps), 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 5:
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

                textRenderer.drawString(m, String.format("X: %.1f Y: %.1f Z: %.1f " + dir, vec.x, vec.y, vec.z), 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 6:
                Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
                Vec3d vec2 = mc.player.getPos();
                double altx = vec2.x / 8;
                double altz = vec2.z / 8;

                if (nether) {
                    altx = vec2.x * 8;
                    altz = vec2.z * 8;
                }
                if (nether) textRenderer.drawString(m, String.format("(Overworld) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                else textRenderer.drawString(m, String.format("(Nether) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), 0.3, mc.getWindow().getScaledHeight() - offset, 0xFFAA00);
                break;
            case 7:
                textRenderer.drawString(m, String.format("%s v%s", KiwiClient.name, KiwiClient.version), 0.3, mc.getWindow().getScaledHeight() - offset, ColorUtil.getRainbow(3, 0.8f, 1));
                break;
        }
    }
}