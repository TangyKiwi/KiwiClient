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
        ChatMessageC2SPacket[] packets = new ChatMessageC2SPacket[]{
            new ChatMessageC2SPacket("███████╗███████╗"),
            new ChatMessageC2SPacket("██╔════╝╚════██║"),
            new ChatMessageC2SPacket("█████╗░░░░███╔═╝"),
            new ChatMessageC2SPacket("██╔══╝░░██╔══╝░░"),
            new ChatMessageC2SPacket("███████╗███████╗"),
            new ChatMessageC2SPacket("╚══════╝╚══════╝")
        };

        long time = System.currentTimeMillis();
        for(int i = 0; i < packets.length; i++) {
            while(System.currentTimeMillis() - time <= 50) {
                //do nothing as a "buffer"
            }
            mc.getNetworkHandler().sendPacket(packets[i]);
            time = System.currentTimeMillis();
        }
        /**
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("███████╗███████╗"));
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("██╔════╝╚════██║"));
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("█████╗░░░░███╔═╝"));
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("██╔══╝░░██╔══╝░░"));
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("███████╗███████╗"));
         mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("╚══════╝╚══════╝"));
         */
    }
}
