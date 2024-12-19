package com.tangykiwi.kiwiclient.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Comparator;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class CommandManager {
    public final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    public final ArrayList<Command> COMMANDS = new ArrayList<>();

    public void init() {
        add(new Ez());

        COMMANDS.sort(Comparator.comparing(Command::getName));
    }

    public void add(Command command) {
        COMMANDS.removeIf(existing -> existing.getName().equals(command.getName()));
        command.registerTo(DISPATCHER);
        COMMANDS.add(command);
    }

    public void dispatch(String message) throws CommandSyntaxException {
        DISPATCHER.execute(message, mc.getNetworkHandler().getCommandSource());
    }

    public Command get(String name) {
        for (Command command : COMMANDS) {
            if (command.getName().equals(name)) {
                return command;
            }
        }

        return null;
    }
}
