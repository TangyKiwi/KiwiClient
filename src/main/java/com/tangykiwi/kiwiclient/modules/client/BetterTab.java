package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.awt.*;

public class BetterTab extends Module {
    public BetterTab() {
        super("BetterTab", "Tab modifications with more information", KEY_UNBOUND, Category.CLIENT,
            new SliderSetting("Tab Size", 1, 1000, 80, 0).withDesc("Changes the number of people shown in tab (default 80)"),
            new ToggleSetting("Latency", true).withDesc("Shows ping next to player names"),
            new ToggleSetting("Gamemode", true).withDesc("Shows gamemode next to player names"));
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;

        name = playerListEntry.getDisplayName();
        if (name == null) name = Text.literal(playerListEntry.getProfile().getName());

        if (getSetting(2).asToggle().state) {
            GameMode gm = playerListEntry.getGameMode();
            String gmText = "?";
            if (gm != null) {
                gmText = switch (gm) {
                    case SPECTATOR -> "Sp";
                    case SURVIVAL -> "S";
                    case CREATIVE -> "C";
                    case ADVENTURE -> "A";
                };
            }
            MutableText text = Text.literal("");
            text.append(name);
            text.append(" [" + gmText + "]");
            name = text;
        }

        return name;
    }

    // handling done in PlayerListHudMixin
}
