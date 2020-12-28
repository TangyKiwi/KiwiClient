package com.tangykiwi.kiwiclient.command;

import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class Bind extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"bind", "b"};
    }

    @Override
    public String getDescription() {
        return "Binds a module to a key.";
    }

    @Override
    public String getSyntax() {
        return ".bind [module] [key]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args.length >= 2) {
            for(Module m : ModuleManager.moduleList) {
                if(m.getName().equalsIgnoreCase(args[0])) {
                    int key = -1;

                    try {
                        key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode();
                    } catch (IllegalArgumentException e) {
                        if (args[1].toLowerCase().startsWith("right")) {
                            try {
                                key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase().replaceFirst("right", "right.")).getCode();
                            } catch (IllegalArgumentException e1) {
                                mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1] + " / " + args[1].toLowerCase().replaceFirst("right", "right.")));
                                return;
                            }
                        } else if (args[1].toLowerCase().startsWith("r")) {
                            try {
                                key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase().replaceFirst("r", "right.")).getCode();
                            } catch (IllegalArgumentException e1) {
                                mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1] + " / " + args[1].toLowerCase().replaceFirst("r", "right.")));
                                return;
                            }
                            /**
                             *  Please add custom keybinds for
                             *  +=
                             *  [{
                             *  ]}
                             *  \|
                             *  ;:
                             *  '"
                             *  ,<
                             *  .>
                             *  /?
                             */
                        } else if (args[1].equals("`") || args[1].equals("~")) {
                            key = GLFW.GLFW_KEY_GRAVE_ACCENT;
                        } else if (args[1].equals("-") || args[1].equals("_")) {
                            key = GLFW.GLFW_KEY_KP_SUBTRACT;
                        } else {
                            mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1]));
                            return;
                        }
                    }

                    m.setKeyCode(key);
                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Bound " + m.getName() + " to " + args[1] + " (KEY" + key + ")"));
                    return;
                }
            }
            mc.inGameHud.getChatHud().addMessage(new LiteralText("Module \"" + args[0] + "\" Not Found!"));
        }
        else mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
    }
}
