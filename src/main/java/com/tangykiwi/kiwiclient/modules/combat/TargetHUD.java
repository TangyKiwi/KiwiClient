package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.font.IFont;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class TargetHUD extends Module {
    public PlayerEntity playerEntity;
    public TargetHUD() {
        super("TargetHUD", "Displays information about your nearest combatant", KEY_UNBOUND, Category.COMBAT,
            new ModeSetting("Sort", "Distance", "Health", "Angle").withDesc("Target by closest distance, lowest health, or cursor angle"));
    }

    public PlayerEntity getNearestPlayer() {
        ArrayList<AbstractClientPlayerEntity> players = (ArrayList<AbstractClientPlayerEntity>) mc.world.getPlayers();
        players.remove(mc.player);
        if(getSetting(0).asMode().mode == 0) {
            players.sort(Comparator.comparing(mc.player::distanceTo));
        } else if(getSetting(0).asMode().mode == 1) {
            players.sort(Comparator.comparing(LivingEntity::getHealth));
        } else {
            players.sort(Comparator.comparing(e -> {
                Vec3d center = e.getBoundingBox().getCenter();

                double diffX = center.x - mc.player.getX();
                double diffY = center.y - mc.player.getEyeY();
                double diffZ = center.z - mc.player.getZ();

                double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

                float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
                float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

                return Math.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw())) + Math.abs(MathHelper.wrapDegrees(pitch - mc.player.getPitch()));
            }));
        }
        return players.isEmpty() ? null : players.get(0);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        int x = scaledWidth - 164;
        int y = scaledHeight - 56 - 100;

        DrawableHelper.fill(e.getMatrix(), x, y, scaledWidth, scaledHeight - 56, 0x90000000);

        playerEntity = getNearestPlayer();
        if(playerEntity == null) return;
        InventoryScreen.drawEntity(x + 26, y + 70, 30 , -MathHelper.wrapDegrees(playerEntity.prevYaw + (playerEntity.getYaw() - playerEntity.prevYaw) * mc.getTickDelta()), -playerEntity.getPitch(), playerEntity);

        // Health bar
        x = scaledWidth - 164;
        y = scaledHeight - 56 - 20;

        x += 5;
        y += 5;

        DrawableHelper.fill(e.getMatrix(), x, y, x + 154, y + 1, Color.GRAY.hashCode());
        DrawableHelper.fill(e.getMatrix(), x, y, x + 1, y + 11, Color.GRAY.hashCode());
        DrawableHelper.fill(e.getMatrix(), x + 153, y, x + 154, y + 11, Color.GRAY.hashCode());
        DrawableHelper.fill(e.getMatrix(), x, y + 10, x + 154, y + 11, Color.GRAY.hashCode());

        x += 2;
        y += 2;

        float maxHealth = playerEntity.getMaxHealth();
        float health = playerEntity.getHealth();
        float absorb = playerEntity.getAbsorptionAmount();

        float healthPercent = health / (maxHealth + absorb);
        float absorbPercent = absorb / (maxHealth + absorb);
        int healthWidth = (int)(150 * healthPercent);
        int absorbWidth = (int)(150 * absorbPercent);

        DrawableHelper.fill(e.getMatrix(), x, y, x + healthWidth, y + 7, getHealthColor(playerEntity));
        DrawableHelper.fill(e.getMatrix(), x + healthWidth, y, x + healthWidth + absorbWidth, y + 7, new Color(255, 218, 0).hashCode());

        x = scaledWidth - 164;
        y = scaledHeight - 56 - 100;

        x += 50;
        y += 5;

        String breakText = " | ";

        // Name
        String nameText = playerEntity.getEntityName();

        // Health
        String healthText = String.format("%.1f", playerEntity.getHealth());
        String absorptionText = playerEntity.getAbsorptionAmount() > 0 ? " " + String.format("%.1f", playerEntity.getAbsorptionAmount()) : "";
        int healthColor = getHealthColor(playerEntity);
        int absorptionColor = new Color(255, 218, 0).hashCode();

        // Ping
        int ping = EntityUtils.getPing(playerEntity);
        String pingText = ping + "ms";
        int pingColor = ColorUtil.getColorString(ping, 10, 20, 50, 75, 100, true);

        // Distance
        double dist = Math.round(mc.player.distanceTo(playerEntity) * 100.0) / 100.0;
        String distText = dist + "m";
        int distColor = ColorUtil.getColorString((int) dist, 10, 20, 50, 75, 100, false);

        GlyphPageFontRenderer textRenderer = IFont.CONSOLAS;
        float breakWidth = textRenderer.getStringWidth(breakText) * 0.9F;
        float healthTextWidth = textRenderer.getStringWidth(healthText) * 0.9F;
        float absorptionTextWidth = textRenderer.getStringWidth(absorptionText) * 0.9F;
        float pingWidth = textRenderer.getStringWidth(pingText) * 0.9F;

        textRenderer.drawString(e.getMatrix(), nameText, x, y, 0xFFFFFF, false,0.9F);
        y += textRenderer.getFontHeight() * 0.9;

        textRenderer.drawString(e.getMatrix(), healthText, x, y, healthColor, false, 0.9F);
        textRenderer.drawString(e.getMatrix(), absorptionText, x + healthTextWidth, y, absorptionColor, false, 0.9F);
        textRenderer.drawString(e.getMatrix(), breakText, x + healthTextWidth + absorptionTextWidth, y, 0xFFFFFF, false, 0.9F);
        textRenderer.drawString(e.getMatrix(), pingText, x + healthTextWidth + absorptionTextWidth + breakWidth, y, pingColor, false, 0.9F);
        textRenderer.drawString(e.getMatrix(), breakText, x + healthTextWidth + absorptionTextWidth + breakWidth + pingWidth, y, 0xFFFFFF, false, 0.9F);
        textRenderer.drawString(e.getMatrix(), distText, x + healthTextWidth + absorptionTextWidth + breakWidth + pingWidth + breakWidth, y, distColor, false, 0.9F);

        // Armor
        y += 7;

        int armorX;
        float armorY;
        int slot = 5;

        // Drawing armor

        for (int position = 0; position < 6; position++) {
            armorX = x + position * 18;
            armorY = y;

            ItemStack itemStack = getItem(slot);

            mc.getItemRenderer().renderGuiItemIcon(itemStack, armorX, (int) armorY);
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, armorX, (int) armorY);

            armorY += 18;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);

            for (Enchantment enchantment : enchantments.keySet()) {
                String enchantText = Utils.getEnchantmentName(enchantment);
                String enchantName = enchantText + " " + enchantments.get(enchantment);

                textRenderer.drawCenteredString(e.getMatrix(), enchantName, armorX + 8 + 0.5 * (textRenderer.getStringWidth(enchantName) / 2), armorY, enchantment.isCursed() ? Color.RED.hashCode() : 0xFFFFFF, 0.5F);
                armorY += textRenderer.getFontHeight() * 0.5F;
            }
            slot--;
        }
    }

    private ItemStack getItem(int i) {
        if (playerEntity == null) return ItemStack.EMPTY;

        return switch (i) {
            case 4 -> playerEntity.getOffHandStack();
            case 5 -> playerEntity.getMainHandStack();
            default -> playerEntity.getInventory().getArmorStack(i);
        };
    }

    public int getHealthColor(LivingEntity entity) {
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        if (health >= maxHealth * 0.7) {
            return new Color(0, 255, 0).hashCode();
        }
        else if (health >= maxHealth * 0.4) {
            return new Color(255, 170, 0).hashCode();
        }
        else if (health >= maxHealth * 0.2) {
            return new Color(255, 85, 85).hashCode();
        }
        return new Color(170, 0, 0).hashCode();
    }
}
