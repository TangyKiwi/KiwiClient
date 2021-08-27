package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.argument.ModuleArgumentType;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Ez extends Command {

    ChatMessageC2SPacket[] packets = new ChatMessageC2SPacket[]{
        new ChatMessageC2SPacket("███████╗███████╗"),
        new ChatMessageC2SPacket("██╔════╝╚════██║"),
        new ChatMessageC2SPacket("█████╗░░░░███╔═╝"),
        new ChatMessageC2SPacket("██╔══╝░░██╔══╝░░"),
        new ChatMessageC2SPacket("███████╗███████╗"),
        new ChatMessageC2SPacket("╚══════╝╚══════╝")
    };

    public Ez() {
        super("ez", "Says a big EZ in chat");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            long time = System.currentTimeMillis();
            for(int i = 0; i < packets.length; i++) {
                while(System.currentTimeMillis() - time <= 500) {
                    //do nothing as a "buffer"
                }
                Utils.mc.getNetworkHandler().sendPacket(packets[i]);
                time = System.currentTimeMillis();
            }
            return SINGLE_SUCCESS;
        });
    }
}
