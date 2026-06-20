package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.awt.Color;
import java.util.ArrayList;

import com.tangykiwi.kiwiclient.util.render.DisplayItemUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorComponent extends HUDComponent {
    public ArmorComponent(float x, float y) {
        super("Armor", x, y);
        setHeight(23);
        setWidth(80);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        Player player = mc.player;
        ArrayList<ItemStack> armor = new ArrayList<>();
        if (mc.player != null) {
            armor.add(player.getInventory().getItem(39));
            armor.add(player.getInventory().getItem(38));
            armor.add(player.getInventory().getItem(37));
            armor.add(player.getInventory().getItem(36));

            for (int i = 0; i < 4; i++) {
                ItemStack is = armor.get(i);
                if (is.isEmpty()) continue;
                RenderUtils.drawItem(context, is, (int) getX() + i * 20 + 2, (int) getY(), 1);

                if (is.isDamageableItem()) {
                    String dur = is.getMaxDamage() - is.getDamageValue() + "";
                    int durColor = 0xFF5555FF;
                    try {
                        durColor = Color.HSBtoRGB(((float) (is.getMaxDamage() - is.getDamageValue()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
                    } catch (Exception e) {
                    }

                    fontRenderer.drawString(context, dur, (int) getX() + i * 20 + 10 - fontRenderer.getStringWidth(dur) / 2, (int) getY() + 16, durColor);
                }
            }
        } else {
            String text = "Armor HUD";
            fontRenderer.drawString(context, text, (int) getX() + getWidth() / 2 - fontRenderer.getStringWidth(text) / 2, (int) getY() + getHeight() / 2 - fontRenderer.getStringHeight(text) / 2, 0xFFFFFFFF);
        }
    }
}
