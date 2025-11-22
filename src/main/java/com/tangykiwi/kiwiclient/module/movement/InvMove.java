package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.CreativeInventoryScreenAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroups;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove", "Lets you move while in inventories", Category.MOVEMENT,
            new ToggleSetting("Sneak", "Allow sneaking", true));
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (shouldInvMove(mc.currentScreen)) {
            for (KeyBinding k : new KeyBinding[] { mc.options.forwardKey, mc.options.backKey,
                    mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey }) {
                k.setPressed(InputUtil.isKeyPressed(mc.getWindow(),
                        InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));
            }

            if (getSetting(0).asToggle().getValue()) {
                mc.options.sneakKey.setPressed(InputUtil.isKeyPressed(mc.getWindow(),
                        InputUtil.fromTranslationKey(mc.options.sneakKey.getBoundKeyTranslationKey()).getCode()));
            }
        }
    }

    private boolean shouldInvMove(Screen screen) {
        if (screen == null) {
            return false;
        }

        return !(screen instanceof ChatScreen
                || screen instanceof BookEditScreen
                || screen instanceof SignEditScreen
                || screen instanceof JigsawBlockScreen
                || screen instanceof StructureBlockScreen
                || screen instanceof AnvilScreen
                || (screen instanceof CreativeInventoryScreen
                && (CreativeInventoryScreenAccessor.getSelectedTab() == ItemGroups.getSearchGroup())));
    }
}
