package com.tangykiwi.kiwiclient.command;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;

import java.util.*;

public class CommandManager {

    public static ArrayList<Command> commandList = new ArrayList<Command>();
    public static MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        commandList.add(new Bind());
        commandList.add(new Ez());
        commandList.add(new Say());
        commandList.add(new SchwongleClip());
        commandList.add(new Toggle());
        commandList.add(new Unbind());
    }

    public ArrayList<Command> getCommandList() {
        return commandList;
    }

    public Collection<String> getCommands() {
        List<String> result = Lists.newArrayList();
        for(Command c : commandList) {
            for(String alias : c.getAliases()) {
                result.add(alias);
            }
        }

        Collections.sort(result);
        return result;
    }

    public static void callCommand(String input) {
        String[] split = input.split(" ", -1);
        System.out.println(Arrays.asList(split));
        String command = split[0];
        String args = input.substring(command.length()).trim();
        for (Command c : commandList) {
            for(String a : c.getAliases()) {
                if (a.equalsIgnoreCase(command)) {
                    try {
                        c.onCommand(command, args.split(" "));
                    } catch (Exception e) {
                        e.printStackTrace();
                        mc.inGameHud.getChatHud().addMessage(new LiteralText(c.getSyntax()));
                    }
                    return;
                }
            }
        }
        mc.inGameHud.getChatHud().addMessage(new LiteralText("Command Not Found"));
    }
}
