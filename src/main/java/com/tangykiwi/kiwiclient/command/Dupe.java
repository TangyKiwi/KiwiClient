package com.tangykiwi.kiwiclient.command;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Dupe extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"dupe", "d"};
    }

    @Override
    public String getDescription() {
        return "Book overload dupe.";
    }

    @Override
    public String getSyntax() {
        return ".dupe";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        NbtList listTag = new NbtList();

        StringBuilder builder1 = new StringBuilder();
        for(int i = 0; i < 21845; i++)
            builder1.append((char)2077);

        listTag.addElement(0, NbtString.of(builder1.toString()));

        StringBuilder builder2 = new StringBuilder();
        for(int i = 0; i < 32; i++)
            builder2.append("KiwiClient, first I quacc then I hacc");

        String string2 = builder2.toString();
        for(int i = 1; i < 40; i++)
            listTag.addElement(i, NbtString.of(string2));

        ItemStack bookStack = new ItemStack(Items.WRITABLE_BOOK, 1);
        bookStack.setSubNbt("title",
                NbtString.of("If you can see this, it didn't work"));
        bookStack.setSubNbt("pages", listTag);

        ArrayList<String> pages = listTag.stream().map(NbtElement::asString).collect(Collectors.toCollection(ArrayList::new));

        mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, Optional.of("If you can see this, it didn't work")));
    }
}
