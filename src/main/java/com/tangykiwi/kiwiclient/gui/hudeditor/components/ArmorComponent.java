package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class ArmorComponent extends HUDComponent {
    public ArmorComponent(float x, float y) {
        super("Armor", x, y);
        setHeight(23);
        setWidth(80);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        ClientPlayerEntity player = mc.player;
        ArrayList<ItemStack> armor = new ArrayList<>();
        if (mc.player != null) {
            armor.add(player.getInventory().getStack(39));
            armor.add(player.getInventory().getStack(38));
            armor.add(player.getInventory().getStack(37));
            armor.add(player.getInventory().getStack(36));
        } else {
            armor.add(new ItemStack(Items.DIAMOND_HELMET, 1));
            armor.add(new ItemStack(Items.DIAMOND_CHESTPLATE, 1));
            armor.add(new ItemStack(Items.DIAMOND_LEGGINGS, 1));
            armor.add(new ItemStack(Items.DIAMOND_BOOTS, 1));
        }

        for (int i = 0; i < 4; i++) {
            ItemStack is = armor.get(i);
            if (is.isEmpty()) continue;
            context.drawItem(is, (int) getX() + i * 20 + 2, (int) getY());
            context.drawStackOverlay(mc.textRenderer, is, (int) getX() + i * 20 + 2, (int) getY());

            if (is.isDamageable()) {
                String dur = is.getMaxDamage() - is.getDamage() + "";
                int durColor = 0xFF5555FF;
                try {
                    durColor = MathHelper.hsvToRgb(((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
                } catch (Exception e) {
                }

                fontRenderer.drawString(context, dur, (int) getX() + i * 20 + 10 - fontRenderer.getStringWidth(dur) / 2, (int) getY() + 16, durColor);
            }
        }
    }
}
