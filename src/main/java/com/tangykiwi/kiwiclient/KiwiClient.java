package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;
import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.modules.player.ArmorSwap;
import com.tangykiwi.kiwiclient.util.*;
import com.tangykiwi.kiwiclient.util.font.CustomFont;
import com.tangykiwi.kiwiclient.util.font.CustomFontOversample;
import com.tangykiwi.kiwiclient.util.renderer.Fonts;
import com.tangykiwi.kiwiclient.util.renderer.GL;
import com.tangykiwi.kiwiclient.util.renderer.Renderer2D;
import com.tangykiwi.kiwiclient.util.renderer.Shaders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class KiwiClient implements ModInitializer {

	public static final String MOD_ID = "kiwiclient";
	public static KiwiClient INSTANCE;
	public static String name = "KiwiClient 1.17.1", version = "3.2.2";
	private MinecraftClient mc;

	public static DiscordRP discordRPC;
	public static ModuleManager moduleManager;
	public static CommandManager commandManager;
	public static String PREFIX = ",";
	public static EventBus eventBus = new EventBus();

	public static Identifier EARS = new Identifier("kiwiclient:textures/ears.png");
	public static Identifier MENU = new Identifier("kiwiclient:background.jpg");
	public static Identifier DUCK = new Identifier("kiwiclient:textures/duck.png");

	public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "kiwiclient");

	public static KeyBinding zoomKey = new KeyBinding("kiwiclient.zoom", InputUtil.Type.MOUSE,
			GLFW.GLFW_MOUSE_BUTTON_5, "KiwiClient");

	@Override
	public void onInitialize() {
//		if (INSTANCE == null) {
//			INSTANCE = this;
//			return;
//		}
		mc = MinecraftClient.getInstance();
		Utils.mc = mc;

//		GL.init();
//		Shaders.init();
//		Renderer2D.init();
//		Fonts.init();

		CustomMatrix.begin(new MatrixStack());

		moduleManager = new ModuleManager();
		moduleManager.init();

		commandManager = new CommandManager();
		commandManager.init();

		discordRPC = new DiscordRP();
		discordRPC.start();

		eventBus.register(moduleManager);

		ClickGui.clickGui.initWindows();

		FabricLoader.getInstance().getModContainer("kiwiclient").ifPresent(modContainer -> {
			ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("kiwiclient:vanillatweaks"), "resourcepacks/vanillatweaks", modContainer, true);
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			MinecraftClient mc = MinecraftClient.getInstance();
			ClientPlayerInteractionManager interactionManager = mc.interactionManager;
			if (mc.mouse.wasRightButtonClicked() && moduleManager.getModule(ArmorSwap.class).isEnabled()) {
				ItemStack stack = player.getMainHandStack();
				int currentItemIndex = player.getInventory().main.indexOf(stack);
				EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
				int armorIndexSlot = determineIndex(equipmentSlot);

				if (hand == Hand.MAIN_HAND && armorIndexSlot != -1) {
					SoundEvent sound = determineSound(stack.getItem());
					player.playSound(sound, 1.0F, 1.0F);
					interactionManager.clickSlot(player.playerScreenHandler.syncId, armorIndexSlot, currentItemIndex, SlotActionType.SWAP, player);
					return TypedActionResult.success(stack);
				}
			}
			return TypedActionResult.pass(ItemStack.EMPTY);
		});

		//Fonts.load();

		KeyBindingHelper.registerKeyBinding(zoomKey);
	}

	private static SoundEvent determineSound(Item item) {
		String name = item.toString();
		if(name.contains("turtle")) return SoundEvents.ITEM_ARMOR_EQUIP_TURTLE;
		else if(name.contains("leather")) return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
		else if(name.contains("chain")) return SoundEvents.ITEM_ARMOR_EQUIP_CHAIN;
		else if(name.contains("iron")) return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		else if(name.contains("gold")) return SoundEvents.ITEM_ARMOR_EQUIP_GOLD;
		else if(name.contains("diamond")) return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
		else if(name.contains("netherite")) return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
		else if(name.contains("elytra")) return SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
		return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	}

	private static int determineIndex(EquipmentSlot type) {
		switch (type) {
			case HEAD:
				return 5;
			case CHEST:
				return 6;
			case LEGS:
				return 7;
			case FEET:
				return 8;
			default:
				return -1;
		}
	}
}
