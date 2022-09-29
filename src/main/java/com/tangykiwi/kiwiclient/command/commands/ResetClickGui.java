package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.gui.clickgui.window.ClickGuiWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.window.Window;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ResetClickGui extends Command {
    public ResetClickGui() {
        super("resetclickgui", "Resets the ClickGUI");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            int len = 85;
            int i = 10;
            for(Window w : ClickGui.clickGui.getWindows()) {
                if (w instanceof ClickGuiWindow) {
                    w.x1 = i;
                    w.y1 = 18;
                    i += len + 5;
                    ((ClickGuiWindow) w).hiding = false;
                }
            }
            addMessage("Reset ClickGUI");
            return SINGLE_SUCCESS;
        });
    }
}
