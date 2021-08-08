package com.tangykiwi.kiwiclient.command;

import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import net.minecraft.text.LiteralText;

public class Toggle extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"toggle", "t"};
    }

    @Override
    public String getDescription() {
        return "Toggles a mod with a command.";
    }

    @Override
    public String getSyntax() {
        return ".toggle [Module]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args.length >= 1) {
            for (Module m : ModuleManager.moduleList) {
                if (args[0].equalsIgnoreCase(m.getName())) {
                    m.toggle();
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Toggled " + m.getName()));
                    return;
                }
            }
            mc.inGameHud.getChatHud().addMessage(new LiteralText("Module \"" + args[0] + "\" Not Found!"));
        }
        else mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
    }
}