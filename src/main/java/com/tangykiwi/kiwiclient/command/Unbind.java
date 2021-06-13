package com.tangykiwi.kiwiclient.command;

import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import net.minecraft.text.LiteralText;

public class Unbind extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"unbind", "ub"};
    }

    @Override
    public String getDescription() {
        return "Unbinds a module.";
    }

    @Override
    public String getSyntax() {
        return ".unbind [module]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args.length >= 1 && !args[0].equals("")) {
            for(Module m : ModuleManager.moduleList) {
                if (m.getName().equalsIgnoreCase(args[0])) {
                    m.setKeyCode(Module.KEY_UNBOUND);
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Unbound " + m.getName()));
                    return;
                }
            }
            mc.inGameHud.getChatHud().addMessage(new LiteralText("Module \"" + args[0] + "\" Not Found!"));
        }
        else mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
    }
}
