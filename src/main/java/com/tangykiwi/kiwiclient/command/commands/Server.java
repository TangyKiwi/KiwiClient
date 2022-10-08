package com.tangykiwi.kiwiclient.command.commands;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.util.Utils;
import joptsimple.internal.Strings;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Server extends Command {
    private static final List<String> ANTICHEAT_LIST = Arrays.asList("nocheatplus", "negativity", "warden", "horizon", "illegalstack", "coreprotect", "exploitsx", "vulcan", "abc", "spartan", "kauri", "anticheatreloaded", "witherac", "godseye", "matrix", "wraith");
    private static final String completionStarts = "/:abcdefghijklmnopqrstuvwxyz0123456789-";
    private int ticks = 0;
    private List<String> plugins = new ArrayList<>();

    public Server() {
        super("server", "Displays information about the server");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            boolean sp = Utils.mc.isIntegratedServerRunning();
            if (!sp && Utils.mc.getCurrentServerEntry() == null) {
                Utils.mc.inGameHud.getChatHud().addMessage(Text.literal("Error getting server info."));
                return SINGLE_SUCCESS;
            }

            Utils.mc.inGameHud.getChatHud().addMessage(Text.literal("\u00a77" + "------ Server Info ------"));
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
            getPlugins();

            return SINGLE_SUCCESS;
        });
    }

    public Text createText(String name, String value) {
        boolean newlines = value.contains("\n");
        return Text.literal("§7" + name + "§f:" + (newlines ? "\n" : " " ) + "§a" + value).styled(style -> style
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy to clipboard")))
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

    public void getPlugins() {
        ticks = 0;
        plugins.clear();
        KiwiClient.eventBus.register(this);
        (new Thread(() -> {
            Random random = new Random();
            completionStarts.chars().forEach(i -> {
                Utils.mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(random.nextInt(200), Character.toString(i)));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        })).start();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        ticks++;

        if (ticks >= 100) {
            Collections.sort(plugins);

            for (int i = 0; i < plugins.size(); i++) {
                plugins.set(i, formatName(plugins.get(i)));
            }

            if (!plugins.isEmpty()) {
                Utils.mc.inGameHud.getChatHud().addMessage(createText("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Plugins §f(" + plugins.size() + ")", "§a" + String.join("§f, §a", plugins)));
            } else {
                Utils.mc.inGameHud.getChatHud().addMessage(createText("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Plugins", "None"));
            }

            ticks = 0;
            plugins.clear();
            KiwiClient.eventBus.unregister(this);
        }
    }

    @Subscribe
    public void onReadPacket(ReceivePacketEvent event) {
        try {
            if (event.packet instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions matches = packet.getSuggestions();
                if (matches == null) {
                    Utils.mc.inGameHud.getChatHud().addMessage(Text.literal("Invalid Packet."));
                    return;
                }

                for (Suggestion suggestion : matches.getList()) {
                    String[] command = suggestion.getText().split(":");
                    if (command.length > 1) {
                        String pluginName = command[0].replace("/", "");

                        if (!plugins.contains(pluginName)) {
                            plugins.add(pluginName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Utils.mc.inGameHud.getChatHud().addMessage(Text.literal("An error occurred while trying to find plugins"));
        }
    }

    private String formatName(String name) {
        if (ANTICHEAT_LIST.contains(name)) {
            return String.format("%s%s", Formatting.RED, name);
        }
        else if (name.contains("exploit") || name.contains("cheat") || name.contains("illegal")) {
            return String.format("%s%s", Formatting.RED, name);
        }

        return String.format("%s%s", Formatting.AQUA, name);
    }
}
