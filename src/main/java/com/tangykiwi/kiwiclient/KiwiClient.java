package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;
import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.module.ModuleManager;
import com.tangykiwi.kiwiclient.util.ConfigManager;
import com.tangykiwi.kiwiclient.util.DiscordRPC;
import com.tangykiwi.kiwiclient.util.TickRate;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiwiClient implements ModInitializer {
	public static final String MOD_ID = "kiwiclient";
	public static final String NAME;
	public static final Logger LOGGER;
	public static final ModMetadata MOD_META;
	public static final String VERSION;
	public static final String MC_VERSION = SharedConstants.getCurrentVersion().name();

	public static Minecraft mc;

	public static DiscordRPC discordRPC;

	public static EventBus eventBus = new EventBus();

	public static ModuleManager moduleManager;
	public static CommandManager commandManager;
	public static FontManager fontManager;
	
	public static TickRate tickRate;

	public static String PREFIX = ",";

	static {
		MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
		NAME = MOD_META.getName();
		VERSION = MOD_META.getVersion().toString();
		LOGGER = LoggerFactory.getLogger(NAME);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing KiwiClient");

		mc = Minecraft.getInstance();

		LOGGER.info("Initializing DiscordRPC");
		discordRPC = new DiscordRPC();
		discordRPC.start();

		LOGGER.info("DiscordRPC running!");

		LOGGER.info("Initializing FontManager");
		fontManager = new FontManager();

		LOGGER.info("Initializing ModuleManager");
		moduleManager = new ModuleManager();
		moduleManager.init();
		eventBus.register(moduleManager);

		LOGGER.info("Initializing CommandManager");
		commandManager = new CommandManager();
		commandManager.init();
    }

	public static void postInit() {
		ClickGUIScreen.INSTANCE.initWindows();
		HUDEditorScreen.INSTANCE.initComponents();

		LOGGER.info("Loading configs");
		ConfigManager.init();
		ConfigManager.loadModules("default");
		ConfigManager.loadClickGUI("default");
		ConfigManager.loadHUD("default");

		tickRate = new TickRate();
	}
}