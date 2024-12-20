package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;
import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.module.ModuleManager;
import com.tangykiwi.kiwiclient.util.discord.Discord;
import com.tangykiwi.kiwiclient.util.discord.DiscordEventHandlers;
import com.tangykiwi.kiwiclient.util.discord.DiscordRichPresence;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiwiClient implements ModInitializer {
	public static final String MOD_ID = "kiwiclient";
	public static final String NAME;
	public static final Logger LOGGER;
	public static final ModMetadata MOD_META;
	public static final String VERSION;

	public static MinecraftClient mc;

	public static DiscordRichPresence discordRPC;
	public static Discord discord = Discord.INSTANCE;

	public static EventBus eventBus = new EventBus();

	public static ModuleManager moduleManager;
	public static CommandManager commandManager;

	public static String PREFIX = ",";

	public static Identifier LOGO = Identifier.of("kiwiclient", "textures/logo.png");
	public static Identifier LOGO2 = Identifier.of("kiwiclient", "textures/logo2.png");

	static {
		MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
		NAME = MOD_META.getName();
		VERSION = MOD_META.getVersion().toString();
		LOGGER = LoggerFactory.getLogger(NAME);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing KiwiClient");

		mc = MinecraftClient.getInstance();

		LOGGER.info("Initializing DiscordRPC");
		discordRPC = new DiscordRichPresence();
		startRPC();
		LOGGER.info("DiscordRPC running!");

		LOGGER.info("Initializing ModuleManager");
		moduleManager = new ModuleManager();
		moduleManager.init();
		eventBus.register(moduleManager);

		LOGGER.info("Initializing CommandManager");
		commandManager = new CommandManager();
		commandManager.init();
	}

	public static void startRPC() {
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		discord.Discord_Initialize("790758093113917491", handlers, true, "");
		discordRPC.startTimestamp = System.currentTimeMillis() / 1000L;
		discordRPC.largeImageKey = "discord_background";
		discordRPC.details = "Loading";
		discordRPC.button_label_1 = "Download";
		discordRPC.button_url_1 = "https://github.com/TangyKiwi/KiwiClient";

		discord.Discord_UpdatePresence(discordRPC);
	}
}