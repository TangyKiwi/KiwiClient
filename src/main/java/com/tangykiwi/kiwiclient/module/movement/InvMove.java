package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.InputConstants;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.CreativeModeInventoryScreenAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.world.item.CreativeModeTabs;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove", "Lets you move while in inventories", Category.MOVEMENT,
            new ToggleSetting("Sneak", "Allow sneaking", true),
            new ToggleSetting("Jump", "Allow jumping", true));
    }

    @Subscribe
    public void onTick(TickEvent event) {
        // if (shouldInvMove(mc.screen)) {
        //     for (KeyMapping k : new KeyMapping[] { mc.options.keyUp, mc.options.keyDown,
        //             mc.options.keyLeft, mc.options.keyRight, mc.options.keyJump, mc.options.keyShift }) {
        //         k.setDown(InputConstants.isKeyDown(mc.getWindow(), InputConstants.getKey(k.getName()).getValue()));
        //     }

        //     if (getSetting(0).asToggle().getValue()) {
        //         mc.options.keyShift.setDown(InputConstants.isKeyDown(mc.getWindow(), InputConstants.getKey(mc.options.keyShift.getName()).getValue()));
        //     }

        //     if (getSetting(1).asToggle().getValue()) {
        //         mc.options.keyJump.setDown(InputConstants.isKeyDown(mc.getWindow(), InputConstants.getKey(mc.options.keyJump.getName()).getValue()));
        //     }
        // }
    }

    private boolean shouldInvMove(Screen screen) {
        if (screen == null) {
            return false;
        }

        return !(screen instanceof ChatScreen
                || screen instanceof BookEditScreen
                || screen instanceof SignEditScreen
                || screen instanceof AbstractCommandBlockEditScreen
                || screen instanceof StructureBlockEditScreen
                || screen instanceof AnvilScreen
                || (screen instanceof CreativeModeInventoryScreen
                && (CreativeModeInventoryScreenAccessor.getSelectedTab() == CreativeModeTabs.searchTab())));
    }
}
