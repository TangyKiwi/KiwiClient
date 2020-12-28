package com.tangykiwi.kiwiclient.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.text.LiteralText;

public class SchwongleClip extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"schwongleclip", "vclip"};
    }

    @Override
    public String getDescription() {
        return "Teleports you X blocks up/down.";
    }

    @Override
    public String getSyntax() {
        return ".vclip [blocks]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args.length >= 1) {
            try{
                int num = Integer.parseInt(args[0]);
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.player.updatePosition(mc.player.getX(), mc.player.getY() + Integer.parseInt(args[0]), mc.player.getZ());
                mc.inGameHud.getChatHud().addMessage(new LiteralText("~SchwongleClipped~ " + args[0] + " blocks."));
            } catch (NumberFormatException e) {
                mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
            }
        }
        else mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
    }
}
