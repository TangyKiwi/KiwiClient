package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;

import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.gui.BrewingStandBlockEntityRenderer;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.modules.render.BetterBrewingStands;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.DiscordRP;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class KiwiClient implements ModInitializer {

    public static final String MOD_ID = "kiwiclient";
    public static String name = "KiwiClient", version = "1.7.18";

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static DiscordRP discordRPC;
    public static EventBus eventBus = new EventBus();

    public static Identifier POTION_BOTTLES = new Identifier("kiwiclient:textures/brewing_stand.png");

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
    }
}
