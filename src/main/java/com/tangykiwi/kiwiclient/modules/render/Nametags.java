package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", "Shows nametags above entities", KEY_UNBOUND, Category.RENDER,
            new ToggleSetting("Players", true).withDesc("Shows nametags over players"),
            new ToggleSetting("Animals", false).withDesc("Shows nametags over animals"),
            new ToggleSetting("Mobs", false).withDesc("Shows nametags over mobs"),
            new ToggleSetting("Items", false).withDesc("Shows nametags over items"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onLivingLabelRender(EntityRenderEvent.Single.Label event) {
        if ((EntityUtils.isAnimal(event.getEntity()) && getSetting(1).asToggle().state)
            || (event.getEntity() instanceof Monster && getSetting(2).asToggle().state)
            || (event.getEntity() instanceof PlayerEntity && getSetting(0).asToggle().state)
            || (event.getEntity() instanceof ItemEntity && getSetting(3).asToggle().state)) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRender(WorldRenderEvent.Post event) {
        for (Entity entity : mc.world.getEntities()) {
            Vec3d rPos = entity.getPos().subtract(RenderUtils.getInterpolationOffset(entity)).add(0, entity.getHeight(), 0);

            double d = entity.squaredDistanceTo(mc.cameraEntity);
            if (!(d > 4096.0D)) {
                float scale = 1f;

                if (Math.sqrt(d) > 10 ) scale *= Math.sqrt(d) / 10;

                if (entity instanceof ItemEntity && getSetting(3).asToggle().state) {
                    ItemEntity itemEntity = (ItemEntity) entity;
                    String name = itemEntity.getName().getString();
                    String customName = itemEntity.getStack().getName().getString();
                    String amount = "[x" + itemEntity.getStack().getCount() + "]";

                    if(!customName.equals(name)) {
                        double up = 0.7 + 0.9 * (Math.sqrt(d / 4096));
                        RenderUtils.drawWorldText(name + " " + getEmptyString(amount), rPos.x, rPos.y + up, rPos.z, scale, 0xFFAA00, true);
                        RenderUtils.drawWorldText(getEmptyString(name) + " " + amount, rPos.x, rPos.y + up, rPos.z, scale, 0xFFFF55, true);
                        RenderUtils.drawWorldText(customName, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFAA00, true);
                    } else {
                        //RenderUtils.drawWorldText("\u00a76" + name + " " + "\u00a7e" + amount, rPos.x, rPos.y + 0.5, rPos.z, scale, -1, true);
                        RenderUtils.drawWorldText(name + " " + getEmptyString(amount), rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFAA00, true);
                        RenderUtils.drawWorldText(getEmptyString(name) + " " + amount, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFF55, true);
                    }
                } else if (entity instanceof LivingEntity) {
                    if (entity == mc.player || entity.hasPassenger(mc.player) || mc.player.hasPassenger(entity)) {
                        continue;
                    }

                    if ((EntityUtils.isAnimal(entity) && getSetting(1).asToggle().state)
                        || (entity instanceof Monster && getSetting(2).asToggle().state)
                        || (entity instanceof PlayerEntity && getSetting(0).asToggle().state)
                        || (entity instanceof ItemEntity && getSetting(3).asToggle().state)) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        String name = livingEntity.getName().getString();
                        String health = String.format("%.1f", livingEntity.getHealth());

                        RenderUtils.drawWorldText(name + " " + getEmptyString(health), rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFFFF, true);
                        RenderUtils.drawWorldText(getEmptyString(name) + " " + health, rPos.x, rPos.y + 0.5, rPos.z, scale, getHealthColor(livingEntity), true);

                        int armorAmount = 0;
                        for(ItemStack i : livingEntity.getArmorItems()) {
                            if(!i.isEmpty()) {
                                armorAmount++;
                            }
                        }

                        double c = -3 + 0.5 * (4 - armorAmount);
                        double lscale = scale * 0.4;
                        double up = 0.7 + 0.9 * (Math.sqrt(d / 4096));

                        int height = 0;
                        height = Math.min(height, drawItem(rPos.x, rPos.y + up, rPos.z, 0.5 * (armorAmount + 1), 0, lscale, livingEntity.getEquippedStack(EquipmentSlot.MAINHAND)));
                        height = Math.min(height, drawItem(rPos.x, rPos.y + up, rPos.z, -0.5 * (armorAmount + 1), 0, lscale, livingEntity.getEquippedStack(EquipmentSlot.OFFHAND)));

                        for (ItemStack i : livingEntity.getArmorItems()) {
                            height = Math.min(height, drawItem(rPos.x, rPos.y + up, rPos.z, c + 1.5, 0, lscale, i));
                            if(!i.isEmpty()) {
                                c++;
                            }
                        }
                    }
                }
            }
        }
    }

    public String getEmptyString(String string) {
        String empty = "";
        while (IFont.CONSOLAS.getStringWidth(empty) < IFont.CONSOLAS.getStringWidth(string)) {
            empty += " ";
        }
        return empty;
    }

    public int getHealthColor(LivingEntity entity) {
        float health = entity.getHealth() + entity.getAbsorptionAmount();
        float maxHealth = entity.getMaxHealth();

        if (health > maxHealth) {
            return 0xFFFF55;
        }
        else if (health >= maxHealth * 0.7) {
            return 0x55FF55;
        }
        else if (health >= maxHealth * 0.4) {
            return 0xFFAA00;
        }
        else if (health >= maxHealth * 0.2) {
            return 0xFF5555;
        }
        return 0xAA0000;
    }

    private int drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        RenderUtils.drawWorldGuiItem(x, y, z, offX * scale, offY * scale, scale, item);

        if (!item.isEmpty() && item.getCount() != 1) {
            double w = IFont.CONSOLAS.getStringWidth("x" + item.getCount()) / 52d;
            RenderUtils.drawWorldText("x" + item.getCount(), x, y, z, (offX - w) * scale, (offY - 0.07) * scale, scale * 1.75, false, 0xFFFFFF, false);
        }

        if (item.isDamageable()) {
            String dur = item.getMaxDamage() - item.getDamage() + "";
            int durcolor = ColorUtil.guiColour();
            try {
                durcolor = MathHelper.hsvToRgb(((float) (item.getMaxDamage() - item.getDamage()) / item.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
            } catch (Exception exception) {
            }
            RenderUtils.drawWorldText(dur, x, y, z, (offX + 0.055) * scale, (offY + 0.75) * scale, scale * 1.4, false, durcolor, false);
        }

        int c = item.isDamageable() ? -1 : 0;
        for (Map.Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
//            String text = I18n.translate(m.getKey().getName(2).getString());
//
//            if (text.isEmpty())
//                continue;
//
//            text = WordUtils.capitalizeFully(text.replaceFirst("Curse of (.)", "C$1"));

            String subText = Utils.getEnchantmentName(m.getKey()) + m.getValue();

            RenderUtils.drawWorldText(subText, x, y, z, (offX + 0.055) * scale, (offY + 0.75 - c * 0.34) * scale, scale * 1.4, false, m.getKey().isCursed() ? 0xff5050 : 0xFFAA00, false);
            c--;
        }
        return c;
    }
}
