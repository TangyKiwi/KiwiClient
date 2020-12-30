package com.tangykiwi.kiwiclient.command;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public abstract class Command {

    public static String PREFIX = ".";
    public static int KEY = GLFW.GLFW_KEY_PERIOD;

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public abstract String[] getAliases();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void onCommand(String command, String[] args) throws Exception;
}