package com.tangykiwi.kiwiclient.module.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

public class HUD extends Module {
    public static ToggleSetting fps = new ToggleSetting("FPS", "Display your FPS", true);
    public static ToggleSetting ping = new ToggleSetting("Ping", "Display your ping", true);
    public static ToggleSetting tps = new ToggleSetting("TPS", "Display server TPS", true);
    public static ToggleSetting ip = new ToggleSetting("IP", "Display server IP", true);
    public static ToggleSetting biome = new ToggleSetting("Biome", "Display current biome", true);
    public static ToggleSetting speed = new ToggleSetting("Speed", "Display current speed", true);
    public static ToggleSetting coords = new ToggleSetting("Coords", "Display current coordinates", true);
    public static ToggleSetting nether_coords = new ToggleSetting("Nether Coords", "Display Nether/Overworld coordinates", true);
    public static ToggleSetting armor = new ToggleSetting("Armor", "Display armor status", true);

    public HUD() {
        super("HUD", "Displays the HUD", Category.CLIENT,
            fps,
            ping,
            tps,
            ip,
            biome,
            speed,
            coords,
            nether_coords,
            armor);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
            if(((ToggleSetting) getSetting(component.getName())).getValue()) {
                component.render(e.getContext());
            }
        }
    }
}
