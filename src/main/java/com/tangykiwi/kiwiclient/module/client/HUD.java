package com.tangykiwi.kiwiclient.module.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.util.font.FontManager;

public class HUD extends Module {
    public ToggleSetting fps = new ToggleSetting("FPS", "Display your FPS", true);
    public ToggleSetting ping = new ToggleSetting("Ping", "Display your ping", true);
    public ToggleSetting tps = new ToggleSetting("TPS", "Display server TPS", true);
    public ToggleSetting ip = new ToggleSetting("IP", "Display server IP", true);
    public ToggleSetting biome = new ToggleSetting("Biome", "Display current biome", true);
    public ToggleSetting speed = new ToggleSetting("Speed", "Display current speed", true);
    public ToggleSetting coords = new ToggleSetting("Coords", "Display current coordinates", true);
    public ToggleSetting nether_coords = new ToggleSetting("Nether Coords", "Display Nether/Overworld coordinates", true);
    public ToggleSetting armor = new ToggleSetting("Armor", "Display armor status", true);

    public HUD() {
        super("HUD", "Displays the HUD", KEY_UNBOUND, Category.CLIENT);
        this.addSetting(fps);
        this.addSetting(ping);
        this.addSetting(tps);
        this.addSetting(ip);
        this.addSetting(biome);
        this.addSetting(speed);
        this.addSetting(coords);
        this.addSetting(nether_coords);
        this.addSetting(armor);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
            if(((ToggleSetting) getSetting(component.getName())).getSValue()) {
                component.render(e.getContext(), KiwiClient.fontManager.getSize(6, FontManager.Type.CONSOLAS));
            }
        }
    }
}
