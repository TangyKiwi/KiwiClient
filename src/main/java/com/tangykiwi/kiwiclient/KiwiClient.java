package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;

import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.gui.BrewingStandBlockEntityRenderer;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.modules.player.ArmorSwap;
import com.tangykiwi.kiwiclient.modules.render.BetterBrewingStands;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.DiscordRP;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class KiwiClient implements ModInitializer {

    public static final String MOD_ID = "kiwiclient";
    public static String name = "KiwiClient", version = "1.8.22";

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static DiscordRP discordRPC;
    public static EventBus eventBus = new EventBus();

    public static Identifier POTION_BOTTLES = new Identifier("kiwiclient:textures/brewing_stand.png");
    public static Identifier CAPE = new Identifier("kiwiclient:textures/cape.png");
    public static Identifier EARS = new Identifier("kiwiclient:textures/ears.png");

    @Override
    public void onInitialize() {
        moduleManager = new ModuleManager();
        moduleManager.init();

        commandManager = new CommandManager();
        commandManager.init();

        discordRPC = new DiscordRP();
        discordRPC.start();

        eventBus.register(moduleManager);

        ClickGui.clickGui.initWindows();

        BlockEntityRendererRegistry.INSTANCE.register(BlockEntityType.BREWING_STAND, BrewingStandBlockEntityRenderer::new);

        ClientSidePacketRegistryImpl.INSTANCE.register(POTION_BOTTLES,
                (packetContext, attachedData) -> {
                    if(moduleManager.getModule(BetterBrewingStands.class).isEnabled()) {
                        BlockPos pos = attachedData.readBlockPos();
                        DefaultedList<ItemStack> inv = DefaultedList.ofSize(5, ItemStack.EMPTY);
                        for (int i = 0; i < 4; i++) {
                            inv.set(i, attachedData.readItemStack());
                        }
                        packetContext.getTaskQueue().execute(() -> {
                            BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) MinecraftClient.getInstance().world.getBlockEntity(pos);
                            blockEntity.setStack(0, inv.get(0));
                            blockEntity.setStack(1, inv.get(1));
                            blockEntity.setStack(2, inv.get(2));
                            blockEntity.setStack(3, inv.get(3));
                            blockEntity.setStack(4, inv.get(4));
                        });
                    }
                });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerInteractionManager interactionManager = mc.interactionManager;

            if (mc.mouse.wasRightButtonClicked() && moduleManager.getModule(ArmorSwap.class).isEnabled()) {
                ItemStack stack = player.inventory.getMainHandStack();
                int currentItemIndex = player.inventory.main.indexOf(stack);

                EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
                int armorIndexSlot = determineIndex(equipmentSlot);

                if (hand == Hand.MAIN_HAND && armorIndexSlot != -1) {
                    player.playSound(stack.getItem() == Items.ELYTRA ? SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                    interactionManager.clickSlot(player.playerScreenHandler.syncId, armorIndexSlot, currentItemIndex, SlotActionType.SWAP, player);
                    return TypedActionResult.success(stack);
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        FabricLoader.getInstance().getModContainer("kiwiclient").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("kiwiclient:vanillatweaks"), "resourcepacks/vanillatweaks", modContainer, true);
        });
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
