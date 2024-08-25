package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;
import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.*;
import com.tangykiwi.kiwiclient.util.discord.DiscordEventHandlers;
import com.tangykiwi.kiwiclient.util.discord.DiscordRPC;
import com.tangykiwi.kiwiclient.util.discord.DiscordRichPresence;
import com.tangykiwi.kiwiclient.util.render.CustomMatrix;
import com.tangykiwi.kiwiclient.util.tooltip.EChestMemory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KiwiClient implements ModInitializer {

	private MinecraftClient mc;

	public static String name = "KiwiClient " + SharedConstants.getGameVersion().getName(), version = "8.16.53";

	public static DiscordRichPresence discordRPC;
	public static DiscordRPC rpc = DiscordRPC.INSTANCE;
	public static ModuleManager moduleManager;
	public static CommandManager commandManager;
	public static String PREFIX = ",";
	public static EventBus eventBus = new EventBus();

	public static Identifier EARS = Identifier.of("kiwiclient:textures/cosmetic/ears.png");
	public static Identifier DUCK = Identifier.of("kiwiclient:textures/hud/duck.png");
	public static Identifier CAPE = Identifier.of("kiwiclient:textures/cosmetic/cape/cape.png");
	public static Identifier CAPE2 = Identifier.of("kiwiclient:textures/cosmetic/cape/gura_cape.png");
	public static Identifier CAPE3 = Identifier.of("kiwiclient:textures/cosmetic/cape/ahri_nsfw_cape.png");
	public static Identifier MENU = Identifier.of("kiwiclient:textures/background/background1.png");
	public static Identifier MENU_ARRXW = Identifier.of("kiwiclient:textures/background/background4.png");
	public static Identifier MENU2 = Identifier.of("kiwiclient:textures/background/background2.png");
	public static Identifier MENU3 = Identifier.of("kiwiclient:textures/background/background3.png");

	public static KeyBinding zoomKey = new KeyBinding("kiwiclient.zoom", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "KiwiClient");

	@Override
	public void onInitialize() {
		mc = MinecraftClient.getInstance();
		Utils.mc = mc;

		CustomMatrix.begin(new MatrixStack());

		moduleManager = new ModuleManager();
		moduleManager.init();

		commandManager = new CommandManager();
		commandManager.init();

		discordRPC = new DiscordRichPresence();
		startRPC();

		EChestMemory eChestMemory = new EChestMemory();
		TickRate tickRate = new TickRate();
		eventBus.register(eChestMemory);
		eventBus.register(tickRate);
		eventBus.register(moduleManager);

		ClickGui.clickGui.initWindows();

		ConfigManager.init();
		ConfigManager.loadModules("default");
		ConfigManager.loadClickGui("default");

		FabricLoader.getInstance().getModContainer("kiwiclient").ifPresent(modContainer -> {
			ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("kiwiclient:kiwitweaks"), "resourcepacks/kiwitweaks", modContainer, true);
		});

		KeyBindingHelper.registerKeyBinding(zoomKey);
	}


	public static void startRPC() {
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		rpc.Discord_Initialize("790758093113917491", handlers, true, "");
		discordRPC.startTimestamp = System.currentTimeMillis() / 1000L;
		discordRPC.largeImageKey = "discord_background";
		discordRPC.details = "Loading";
		discordRPC.button_label_1 = "Download";
		discordRPC.button_url_1 = "https://github.com/TangyKiwi/KiwiClient";

		rpc.Discord_UpdatePresence(discordRPC);
	}
}
