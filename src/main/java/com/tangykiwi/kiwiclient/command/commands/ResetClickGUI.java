package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ResetClickGUI extends Command {
    public ResetClickGUI() {
        super("resetclickgui", "Resets the Click GUI");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(context -> {
            int i = 10;
            for (CategoryWindow w : ClickGUIScreen.INSTANCE.windows) {
                w.x = i;
                w.y = 18;
                w.expanded = true;
                i += 90;
            }
            addMessage("Reset ClickGUI");
            return SINGLE_SUCCESS;
        });
    }
}
