package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.ModuleArgumentType;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.commands.SharedSuggestionProvider;

public class Toggle extends Command {

    public Toggle() {
        super("toggle", "Toggles a module on / off", "t");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.then(argument("module", ModuleArgumentType.module())
            .executes(context -> {
                Module m = ModuleArgumentType.getModule(context, "module");
                m.toggle();
                addMessage("Toggled §d" + m.getName() + " §a" + (m.isEnabled() ? "ON" : "OFF"));
                return SINGLE_SUCCESS;
            })
        );
    }
}
