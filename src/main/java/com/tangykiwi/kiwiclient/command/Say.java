package com.tangykiwi.kiwiclient.command;

/**
 *  Dummy Command
 *  Actual handling is done in
 *  ClientPlayerEntityMixin
 */
public class Say extends Command {

    @Override
    public String[] getAliases() {
        return new String[]{"say"};
    }

    @Override
    public String getDescription() {
        return "Says a message in chat.";
    }

    @Override
    public String getSyntax() {
        return ".say [message]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {

    }
}
