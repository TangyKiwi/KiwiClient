package com.tangykiwi.kiwiclient.module.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.eventbus.Subscribe;
import com.mojang.datafixers.util.Either;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.world.phys.Vec3;

import com.tangykiwi.kiwiclient.module.setting.ModeSetting;

public class HUD extends Module {
    public Map<Either<UUID, String>, Vec3> waypoints = new ConcurrentHashMap<>();

    public HUD() {
        super("HUD", "Displays the HUD", Category.CLIENT,
            new ToggleSetting("FPS", "Display your FPS", true),
            new ToggleSetting("Ping", "Display your ping", true),
            new ToggleSetting("TPS", "Display server TPS", true),
            new ToggleSetting("IP", "Display server IP", true),
            new ToggleSetting("Biome", "Display current biome", true),
            new ToggleSetting("Speed", "Display current speed", true),
            new ToggleSetting("Coords", "Display current coordinates", true),
            new ToggleSetting("Nether Coords", "Display Nether/Overworld coordinates", true),
            new ToggleSetting("Armor", "Display armor status", true),
            new ToggleSetting("Inventory", "Display your inventory", true),
            new ToggleSetting("ActiveMods", "Display active mods", true).withChildren(
                new ModeSetting("LR", "Left or Right orientation", 1, "Left", "Right"),
                new ModeSetting("UD", "Up or Down orientation", 1, "Up", "Down")
            )
            //, new ToggleSetting("Waypoints", "Display player waypoints", true)
            );
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
            if(getSetting(component.getName()).asToggle().getValue()) {
                component.render(e.getContext());
            }
        }
    }
}
