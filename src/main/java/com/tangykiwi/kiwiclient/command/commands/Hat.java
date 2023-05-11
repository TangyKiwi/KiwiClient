package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.util.Utils.mc;

public class Hat extends Command {
    public Hat() {
        super("hat", "Wear's the item you're holding on your head");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            int fromIndex = mc.player.getInventory().selectedSlot + 36;
            int toIndex = 39;
            ScreenHandler sh = mc.player.currentScreenHandler;
            Slot slot = sh.getSlot(fromIndex);
            Int2ObjectArrayMap stack = new Int2ObjectArrayMap();
            stack.put(fromIndex, slot.getStack());
            mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(sh.syncId, sh.getRevision(), slot.id, toIndex, SlotActionType.SWAP, sh.getSlot(fromIndex).getStack(), stack));
            return SINGLE_SUCCESS;
        });
    }
}
