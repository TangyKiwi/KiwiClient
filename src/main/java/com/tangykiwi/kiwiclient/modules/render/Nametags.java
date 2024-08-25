package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
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
                    String line = name + " " + amount;

                    if(!customName.equals(name)) {
                        double up = 0.7 + 0.9 * (Math.sqrt(d / 4096));
                        RenderUtils.drawWorldTextBackground(event.getMatrixStack(), line, rPos.x, rPos.y + up, rPos.z, scale);
                        RenderUtils.drawWorldText(name, line, 0, rPos.x, rPos.y + up, rPos.z, scale, 0xFFAA00, false);
                        RenderUtils.drawWorldText(amount, line, 1, rPos.x, rPos.y + up, rPos.z, scale, 0xFFFF55, false);
//                        RenderUtils.drawWorldText(name + " " + getEmptyString(amount), rPos.x, rPos.y + up, rPos.z, scale, 0xFFAA00, true);
//                        RenderUtils.drawWorldText(getEmptyString(name) + " " + amount, rPos.x, rPos.y + up, rPos.z, scale, 0xFFFF55, true);
                        RenderUtils.drawWorldText(customName, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFAA00, true);
                    } else {
                        //RenderUtils.drawWorldText("\u00a76" + name + " " + "\u00a7e" + amount, rPos.x, rPos.y + 0.5, rPos.z, scale, -1, true);
                        RenderUtils.drawWorldText(name, line, 0, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFAA00, false);
                        RenderUtils.drawWorldText(amount, line, 1, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFF55, false);
//                        RenderUtils.drawWorldText(name + " " + getEmptyString(amount), rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFAA00, true);
//                        RenderUtils.drawWorldText(getEmptyString(name) + " " + amount, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFF55, true);
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
                        String line = name + " " + health;

                        RenderUtils.drawWorldTextBackground(event.getMatrixStack(), line, rPos.x, rPos.y + 0.5, rPos.z, scale);
                        RenderUtils.drawWorldText(name, line, 0, rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFFFF, false);
                        RenderUtils.drawWorldText(health, line, 1, rPos.x, rPos.y + 0.5, rPos.z, scale, getHealthColor(livingEntity), false);

//                        RenderUtils.drawWorldText(name + " " + getEmptyString(health), rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFFFF, true);
//                        RenderUtils.drawWorldText(getEmptyString(name) + " " + health, rPos.x, rPos.y + 0.5, rPos.z, scale, getHealthColor(livingEntity), true);

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
            RenderUtils.drawWorldText("x" + item.getCount(), "x" + item.getCount(), 0, x, y, z, (offX - w) * scale, (offY - 0.07) * scale, scale * 1.75, false, 0xFFFFFF, false);
        }

        if (item.isDamageable()) {
            String dur = item.getMaxDamage() - item.getDamage() + "";
            int durcolor = ColorUtil.guiColour();
            try {
                durcolor = MathHelper.hsvToRgb(((float) (item.getMaxDamage() - item.getDamage()) / item.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
            } catch (Exception exception) {
            }
            RenderUtils.drawWorldText(dur, dur, 0, x, y, z, (offX + 0.055) * scale, (offY + 0.75) * scale, scale * 1.4, false, durcolor, false);
        }

        int c = item.isDamageable() ? -1 : 0;

        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(item);
        List<ObjectIntPair<RegistryEntry<Enchantment>>> enchantmentsToShow = new ArrayList<>();

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
            enchantmentsToShow.add(new ObjectIntImmutablePair<>(entry.getKey(), entry.getIntValue()));
        }

        for (ObjectIntPair<RegistryEntry<Enchantment>> entry : enchantmentsToShow) {
            String subText = Utils.getEnchantmentName(entry.left().value()) + " " + entry.rightInt();

            RenderUtils.drawWorldText(subText, subText, 0, x, y, z, (offX + 0.055) * scale, (offY + 0.75 - c * 0.34) * scale, scale * 1.4, false, entry.left().isIn(EnchantmentTags.CURSE) ? 0xff5050 : 0xFFAA00, false);
            c--;
        }
        return c;
    }
}
