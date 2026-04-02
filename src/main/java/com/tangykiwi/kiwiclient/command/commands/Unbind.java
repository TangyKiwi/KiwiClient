package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.ModuleArgumentType;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.commands.SharedSuggestionProvider;

public class Unbind extends Command {
    public Unbind() {
        super("unbind", "Unbinds a module's keybind.", "ub");
    }

   @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.then(argument("module", ModuleArgumentType.module())
            .executes(context -> {
                Module m = ModuleArgumentType.getModule(context, "module");
                m.setKeyCode(-1);
                addMessage("Unbound §d" + m.getName());
                return SINGLE_SUCCESS;
            })
        );
    }
}
