package com.tangykiwi.kiwiclient.command;

import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import net.minecraft.text.LiteralText;

public class SchwongleClip extends Command {

    @Override
    public String getAlias() {
        return "schwongleclip";
    }

    @Override
    public String getDescription() {
        return "Clips through a block.";
    }

    @Override
    public String getSyntax() {
        return "schwongleclip [Distance]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        final double blocks = args[0]
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + blocks, mc.thePlayer.posZ);
        mc.inGameHud.getChatHud().addMessage(new LiteralText("Clipped " + blocks + " blocks."));
    }
}
