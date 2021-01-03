package com.tangykiwi.kiwiclient.command;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class Ez extends Command {
    @Override
    public String[] getAliases() {
        return new String[]{"ez", "easy"};
    }

    @Override
    public String getDescription() {
        return "Ez";
    }

    @Override
    public String getSyntax() {
        return ".ez";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("███████╗███████╗"));
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("██╔════╝╚════██║"));
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("█████╗░░░░███╔═╝"));
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("██╔══╝░░██╔══╝░░"));
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("███████╗███████╗"));
        mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("╚══════╝╚══════╝"));
    }
}
