package com.tangykiwi.kiwiclient.command.commands;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.ClientPlayNetworkHandlerAccessor;

import joptsimple.internal.Strings;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Server extends Command {
    private static final Set<String> ANTICHEAT_LIST = Set.of("nocheatplus", "negativity", "warden", "horizon", "illegalstack", "coreprotect", "exploitsx", "vulcan", "abc", "spartan", "kauri", "anticheatreloaded", "witherac", "godseye", "matrix", "wraith", "antixrayheuristics", "grimac");
    private static final Set<String> VERSION_ALIASES = Set.of("version", "ver", "about", "bukkit:version", "bukkit:ver", "bukkit:about"); // aliases for bukkit:version

    private String alias;
    private int ticks = 0;
    private List<String> plugins = new ArrayList<>();
    private final List<String> commandTreePlugins = new ArrayList<>();

    public Server() {
        super("server", "Displays information about the server");
    }
    
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            boolean sp = mc.isIntegratedServerRunning();
            if (!sp && mc.getCurrentServerEntry() == null) {
                addMessage("Error getting server info.");
                return SINGLE_SUCCESS;
            }

            addMessage("\u00a77" + "------ Server Info ------");
            addMessage(createText("Address", getAddress(sp)));
            addMessage(createText("Brand", getBrand(sp)));
            addMessage(createText("Day", getDay()));
            addMessage(createText("Difficulty", getDifficulty()));
            addMessage(createText("IP", getIP(sp)));
            addMessage(createText("MOTD", getMotd(sp)));
            addMessage(createText("Ping", getPing()));
            addMessage(createText("Permission Level", getPerms()));
            addMessage(createText("Protocol", getProtocol(sp)));
            addMessage(createText("Version", getVersion(sp)));
            getPlugins();

            return SINGLE_SUCCESS;
        });
    }

    public Text createText(String name, String value) {
        boolean newlines = value.contains("\n");
        return Text.literal("§7" + name + "§f:" + (newlines ? "\n" : " " ) + "§a" + value).styled(style -> style
            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Copy to clipboard")))
            .withClickEvent(new ClickEvent.CopyToClipboard(value))
        );
    }

    public String getAddress(boolean singleplayer) {
        if (singleplayer)
            return "Singleplayer";

        return mc.getCurrentServerEntry().address != null ? mc.getCurrentServerEntry().address : "Unknown";
    }

    public String getBrand(boolean singleplayer) {
        if (singleplayer)
            return "Integrated Server";

        return mc.getNetworkHandler().getBrand() != null ? mc.getNetworkHandler().getBrand() : "Unknown";
    }

    public String getDay() {
        return "Day " + (mc.world.getTimeOfDay() / 24000L);
    }

    public String getDifficulty() {
        return mc.world.getDifficulty().getTranslatableName().getString() + " (Local: " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty() + ")";
    }

    public String getIP(boolean singleplayer) {
        try {
            if (singleplayer)
                return InetAddress.getLocalHost().getHostAddress();

            return mc.getCurrentServerEntry().address != null ? InetAddress.getByName(mc.getCurrentServerEntry().address).getHostAddress() : "Unknown";
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public String getMotd(boolean singleplayer) {
        if (singleplayer)
            return "-";

        return mc.getCurrentServerEntry().label != null ? mc.getCurrentServerEntry().label.getString() : "Unknown";
    }

    public String getPing() {
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        return playerEntry == null ? "0" : Integer.toString(playerEntry.getLatency());
    }

    public String getPerms() {
        int p = 0;
        while (mc.player.hasPermissionLevel(p + 1) && p < 5) p++;

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

        return Integer.toString(mc.getCurrentServerEntry().protocolVersion);
    }

    public String getVersion(boolean singleplayer) {
        if (singleplayer)
            return SharedConstants.getGameVersion().name();

        return mc.getCurrentServerEntry().version != null ? mc.getCurrentServerEntry().version.getString() : "Unknown (" + SharedConstants.getGameVersion().name() + ")";
    }

    public void getPlugins() {
        ticks = 0;
        plugins.clear();
        KiwiClient.eventBus.register(this);
    }

    @Subscribe
    public void onTick(TickEvent.Post e) {
        ticks++;

        if (ticks >= 100) {
             plugins.replaceAll(this::formatName);

            if (!plugins.isEmpty()) {
                addMessage("Plugins " + plugins.size() + ": " + Strings.join(plugins.toArray(new String[0]), ", "));
            } else {
                addMessage("Plugins (0): None Detected");
            }

            ticks = 0;
            plugins.clear();
            KiwiClient.eventBus.unregister(this);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendPacket(PacketEvent.Send e) {
        if (e.packet instanceof RequestCommandCompletionsC2SPacket) e.cancel();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof CommandTreeS2CPacket packet) {
            ClientPlayNetworkHandlerAccessor handler = (ClientPlayNetworkHandlerAccessor) event.connection.getPacketListener();
            commandTreePlugins.clear();
            alias = null;

            packet.getCommandTree(
                CommandRegistryAccess.of(handler.kiwiclient$getCombinedDynamicRegistries(), handler.kiwiclient$getEnabledFeatures()),
                ClientPlayNetworkHandlerAccessor.kiwiclient$getCommandNodeFactory()
            ).getChildren().forEach(node -> {
                String[] split = node.getName().split(":");
                if (split.length > 1) {
                    if (!commandTreePlugins.contains(split[0])) commandTreePlugins.add(split[0]);
                }

                if (alias == null && VERSION_ALIASES.contains(node.getName())) {
                    alias = node.getName();
                }
            });

        }

        try {
            if (event.packet instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions matches = packet.getSuggestions();

                if (matches.isEmpty()) {
                    addMessage("An error occurred while trying to find plugins.");
                    return;
                }

                for (Suggestion suggestion : matches.getList()) {
                    String pluginName = suggestion.getText();
                    if (!plugins.contains(pluginName.toLowerCase())) plugins.add(pluginName);
                }
            }
        } catch (Exception e) {
            addMessage("An error occurred while trying to find plugins.");
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
