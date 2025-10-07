package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;

import net.minecraft.command.CommandSource;

public class ResetHUD extends Command {
    public ResetHUD() {
        super("resethud", "Resets the HUD to default position");
    }
 
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();

            int i = 0;
            for (HUDComponent c : HUDEditorScreen.INSTANCE.components) {
                switch (c.getName()) {
                    case "Nether Coords":
                    case "Coords":
                    case "Speed":
                    case "Biome":
                    case "IP":
                    case "TPS":
                    case "Ping":
                    case "FPS":
                        c.setX(0.3F);
                        c.setY(h - i * 6 + 2);
                    case "Inventory":
                        c.setX(w - 164);
                        c.setY(h - 56);
                    case "Armor":
                        c.setX(w / 2);
                        c.setY(h / 2);
                    case "ActiveMods":
                        c.setX(0);
                        c.setY(0);
                }
                i++;
            }
            
            return SINGLE_SUCCESS;
        });
    }
}
