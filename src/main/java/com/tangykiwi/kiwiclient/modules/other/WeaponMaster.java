package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

public class WeaponMaster extends Module {
    public WeaponMaster() {
        super("WeaponMaster", "Renders your toolbar on your model", KEY_UNBOUND, Category.OTHER,
            new ToggleSetting("Banner", true),
            new ToggleSetting("Shield", true),
            new ToggleSetting("Slot 1", true),
            new ToggleSetting("Slot 2", true),
            new ToggleSetting("Slot 3", true),
            new ToggleSetting("Slot 4", true),
            new ToggleSetting("Slot 5", true),
            new ToggleSetting("Slot 6", true),
            new ToggleSetting("Slot 7", true),
            new ToggleSetting("Slot 8", true));
    }
}
