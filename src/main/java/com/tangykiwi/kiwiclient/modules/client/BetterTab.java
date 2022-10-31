package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

public class BetterTab extends Module {
    public BetterTab() {
        super("BetterTab", "Tab modifications with more information", KEY_UNBOUND, Category.CLIENT,
            new SliderSetting("Tab Size", 1, 1000, 80, 0).withDesc("Changes the number of people shown in tab (default 80)"),
            new ToggleSetting("Latency", true).withDesc("Shows ping next to player names"));
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;
        Color color = null;

        name = playerListEntry.getDisplayName();
        if (name == null) name = Text.literal(playerListEntry.getProfile().getName());

        if (color != null) {
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = Text.literal(nameString).setStyle(name.getStyle().withColor(TextColor.fromRgb(color.getRGB())));
        }

        return name;
    }

    // handling done in PlayerListHudMixin
}
