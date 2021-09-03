package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Dupe extends Command {

    private static boolean preDoDupe = false;
    private static boolean actuallyPullThrough = false;

    public Dupe() {
        super("dupe", "Current: shulker dupe", "d");

    }

    public static boolean shouldDupe() {
        return preDoDupe && actuallyPullThrough;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            HitResult hr = MinecraftClient.getInstance().crosshairTarget;
            if (hr instanceof BlockHitResult) {
                BlockHitResult bhr = (BlockHitResult)hr;
                BlockState bs = MinecraftClient.getInstance().world.getBlockState(bhr.getBlockPos());
                if (bs.getBlock() instanceof ShulkerBoxBlock) {
                    MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, Hand.MAIN_HAND, bhr);
                    preDoDupe = true;
                }
            } else {
                Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Please look at a shulker box"));
            }
            return SINGLE_SUCCESS;
        });
//        builder.executes(context -> {
//            NbtList listTag = new NbtList();
//
//            StringBuilder builder1 = new StringBuilder();
//            for(int i = 0; i < 21845; i++)
//                builder1.append((char)2077);
//
//            listTag.addElement(0, NbtString.of(builder1.toString()));
//
//            StringBuilder builder2 = new StringBuilder();
//            for(int i = 0; i < 32; i++)
//                builder2.append("KiwiClient, first I quacc then I hacc");
//
//            String string2 = builder2.toString();
//            for(int i = 1; i < 40; i++)
//                listTag.addElement(i, NbtString.of(string2));
//
//            ItemStack bookStack = new ItemStack(Items.WRITABLE_BOOK, 1);
//            bookStack.setSubNbt("title",
//                    NbtString.of("If you can see this, it didn't work"));
//            bookStack.setSubNbt("pages", listTag);
//
//            ArrayList<String> pages = listTag.stream().map(NbtElement::asString).collect(Collectors.toCollection(ArrayList::new));
//
//            Utils.mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(Utils.mc.player.getInventory().selectedSlot, pages, Optional.of("If you can see this, it didn't work")));
//
//            return SINGLE_SUCCESS;
//        });
    }

    public static void packetSent(Packet<?> p) {
        if (shouldDupe() && p instanceof PlayerActionC2SPacket) {
            PlayerActionC2SPacket packet = (PlayerActionC2SPacket)p;
            if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                ScreenHandler var3 = MinecraftClient.getInstance().player.currentScreenHandler;
                if (var3 instanceof ShulkerBoxScreenHandler) {
                    ShulkerBoxScreenHandler screenHandler = (ShulkerBoxScreenHandler)var3;
                    Int2ObjectArrayMap<ItemStack> stack = new Int2ObjectArrayMap();
                    stack.put(0, screenHandler.getSlot(0).getStack());
                    ClickSlotC2SPacket cs = new ClickSlotC2SPacket(screenHandler.syncId, 0, 0, 0, SlotActionType.PICKUP, screenHandler.getSlot(0).getStack(), stack);
                    MinecraftClient.getInstance().getNetworkHandler().sendPacket(cs);
                    actuallyPullThrough = false;
                    preDoDupe = false;
                }
            }
        }

    }

    public static void tick() {
        if (preDoDupe) {
            if (MinecraftClient.getInstance().player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
                actuallyPullThrough = true;
            } else if (shouldDupe()) {
                actuallyPullThrough = false;
                preDoDupe = false;
            }

            HitResult hr = MinecraftClient.getInstance().crosshairTarget;
            if (hr instanceof BlockHitResult) {
                BlockHitResult bhr = (BlockHitResult) hr;
                if (!(MinecraftClient.getInstance().world.getBlockState(bhr.getBlockPos()).getBlock() instanceof ShulkerBoxBlock)) {
                    preDoDupe = false;
                } else {
                    MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(bhr.getBlockPos(), Direction.DOWN);
                }
            } else {
                preDoDupe = false;
            }
        }
    }
}
