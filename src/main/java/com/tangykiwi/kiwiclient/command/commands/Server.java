package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Server extends Command {
    public Server() {
        super("server", "Displays information about the server");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            boolean sp = Utils.mc.isIntegratedServerRunning();
            if (!sp && Utils.mc.getCurrentServerEntry() == null) {
                Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Error getting server info."));
                return SINGLE_SUCCESS;
            }

            Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("\u00a77" + "------ Server Info ------"));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Address", getAddress(sp)));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Brand", getBrand(sp)));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Day", getDay()));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Difficulty", getDifficulty()));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("IP", getIP(sp)));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("MOTD", getMotd(sp)));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Ping", getPing()));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Permission Level", getPerms()));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Protocol", getProtocol(sp)));
            Utils.mc.inGameHud.getChatHud().addMessage(createText("Version", getVersion(sp)));

            return SINGLE_SUCCESS;
        });
    }

    public Text createText(String name, String value) {
        boolean newlines = value.contains("\n");
        return new LiteralText("\u00a77" + name + "\u00a7f:" + (newlines ? "\n" : " " ) + "\u00a7a" + value).styled(style -> style
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Formatting.strip(value))));
    }

    public String getAddress(boolean singleplayer) {
        if (singleplayer)
            return "Singleplayer";

        return Utils.mc.getCurrentServerEntry().address != null ? Utils.mc.getCurrentServerEntry().address : "Unknown";
    }

    public String getBrand(boolean singleplayer) {
        if (singleplayer)
            return "Integrated Server";

        return Utils.mc.player.getServerBrand() != null ? Utils.mc.player.getServerBrand() : "Unknown";
    }

    public String getDay() {
        return "Day " + (Utils.mc.world.getTimeOfDay() / 24000L);
    }

    public String getDifficulty() {
        return StringUtils.capitalize(Utils.mc.world.getDifficulty().getName()) + " (Local: " + Utils.mc.world.getLocalDifficulty(Utils.mc.player.getBlockPos()).getLocalDifficulty() + ")";
    }

    public String getIP(boolean singleplayer) {
        try {
            if (singleplayer)
                return InetAddress.getLocalHost().getHostAddress();

            return Utils.mc.getCurrentServerEntry().address != null ? InetAddress.getByName(Utils.mc.getCurrentServerEntry().address).getHostAddress() : "Unknown";
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public String getMotd(boolean singleplayer) {
        if (singleplayer)
            return "-";

        return Utils.mc.getCurrentServerEntry().label != null ? Utils.mc.getCurrentServerEntry().label.getString() : "Unknown";
    }

    public String getPing() {
        PlayerListEntry playerEntry = Utils.mc.player.networkHandler.getPlayerListEntry(Utils.mc.player.getGameProfile().getId());
        return playerEntry == null ? "0" : Integer.toString(playerEntry.getLatency());
    }

    public String getPerms() {
        int p = 0;
        while (Utils.mc.player.hasPermissionLevel(p + 1) && p < 5) p++;

        switch (p) {
            case 0: return "0 (No Perms)";
            case 1: return "1 (No Perms)";
            case 2: return "2 (Player Command Access)";
            case 3: return "3 (Server Command Access)";
            case 4: return "4 (Operator)";
            default: return p + " (Unknown)";
        }
    }

    public String getProtocol(boolean singleplayer) {
        if (singleplayer)
            return Integer.toString(SharedConstants.getProtocolVersion());

        return Integer.toString(Utils.mc.getCurrentServerEntry().protocolVersion);
    }

    public String getVersion(boolean singleplayer) {
        if (singleplayer)
            return SharedConstants.getGameVersion().getName();

        return Utils.mc.getCurrentServerEntry().version != null ? Utils.mc.getCurrentServerEntry().version.getString() : "Unknown (" + SharedConstants.getGameVersion().getName() + ")";
    }
}
