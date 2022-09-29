package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.argument.ModuleArgumentType;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Unbind extends Command {
    public Unbind() {
        super("unbind", "Unbinds a module", "ub");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module())
            .executes(context -> {
                Module m = ModuleArgumentType.getModule(context, "module");
                m.setKeyCode(Module.KEY_UNBOUND);
                addMessage("Unbound §d" + m.getName());
                return SINGLE_SUCCESS;
            })
        );
    }
}
