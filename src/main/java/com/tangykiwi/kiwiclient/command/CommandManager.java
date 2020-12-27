package com.tangykiwi.kiwiclient.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {

    public static ArrayList<Command> commandList = new ArrayList<Command>();
    public static MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        commandList.add(new Say());
        commandList.add(new Toggle());
    }

    public ArrayList<Command> getCommandList() {
        return commandList;
    }

    public static void callCommand(String input) {
        String[] split = input.split(" ", -1);
        System.out.println(Arrays.asList(split));
        String command = split[0];
        String args = input.substring(command.length()).trim();
        for (Command c : commandList) {
            if (c.getAlias().equalsIgnoreCase(command)) {
                try {
                    c.onCommand(command, args.split(" "));
                } catch (Exception e) {
                    e.printStackTrace();
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Invalid Syntax"));
                    mc.inGameHud.getChatHud().addMessage(new LiteralText(c.getSyntax()));
                }
                return;
            }
        }
        mc.inGameHud.getChatHud().addMessage(new LiteralText("Command Not Found"));
    }
}
