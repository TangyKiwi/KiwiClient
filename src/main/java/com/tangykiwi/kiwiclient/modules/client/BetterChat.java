package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.AddMessageEvent;
import com.tangykiwi.kiwiclient.event.SendChatMessageEvent;
import com.tangykiwi.kiwiclient.mixin.ChatHudAccessor;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUD;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BetterChat extends Module {
    private final Char2CharMap FANCY = new Char2CharArrayMap();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public BetterChat() {
        super("BetterChat", "Adds modifications to improve the chat function", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("Annoy", false).withDesc("MaKEs yOUr MeSSAgEs aNnOyInG"),
            new ToggleSetting("Fancy", false).withDesc("ᴍᴀᴋᴇꜱ ʏᴏᴜʀ ᴍᴇꜱꜱᴀɢᴇꜱ ғᴀɴᴄʏ!"),
            new ToggleSetting("Timestamps", true).withDesc("Adds timestamps to all messages in chat"),
            new ToggleSetting("Player Heads", true).withDesc("Adds the player's head next to their messages"),
            new ToggleSetting("Infinite Chat Box", true).withDesc("Bypasses the message character limit"),
            new ToggleSetting("Better History", true).withDesc("Let's you see more of chat history").withChildren(
                new SliderSetting("# Lines", 100, 1000, 1000, 0).withDesc("Extended number of lines")
            ));
        super.toggle();

        String[] a = "abcdefghijklmnopqrstuvwxyz".split("");
        String[] b = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩǫʀꜱᴛᴜᴠᴡxʏᴢ".split("");
        for (int i = 0; i < a.length; i++) FANCY.put(a[i].charAt(0), b[i].charAt(0));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onMessageRecieve(AddMessageEvent event) {
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);

        Text message = event.message;

        // Timestamps
        if (getSetting(2).asToggle().state) {
            Matcher matcher = Pattern.compile("^(<[0-9]{2}:[0-9]{2}:[0-9]{2}>\\s)").matcher(message.getString());
            if (matcher.matches()) message.getSiblings().subList(0, 8).clear();

            Text timestamp = new LiteralText("[" + dateFormat.format(new Date()) + "] ").formatted(Formatting.GRAY);

            message = new LiteralText("").append(timestamp).append(message);
        }

        // Player Heads
        if (getSetting(3).asToggle().state) {
            message = new LiteralText("   ").append(message);
        }

        event.setCancelled(true);
        ((IChatHUD) mc.inGameHud.getChatHud()).add(message, event.id, mc.inGameHud.getTicks(), false);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onMessageSend(SendChatMessageEvent event) {
        String message = event.message;

        // Annoy
        if (getSetting(0).asToggle().state) message = applyAnnoy(message);

        // Fancy
        if (getSetting(1).asToggle().state) message = applyFancy(message);

        event.message = message;
    }

    // Annoy
    private String applyAnnoy(String message) {
        StringBuilder sb = new StringBuilder(message.length());
        boolean upperCase = true;
        for (int cp : message.codePoints().toArray()) {
            if (upperCase) sb.appendCodePoint(Character.toUpperCase(cp));
            else sb.appendCodePoint(Character.toLowerCase(cp));
            upperCase = !upperCase;
        }
        message = sb.toString();
        return message;
    }

    // Fancy
    private String applyFancy(String message) {
        StringBuilder sb = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (FANCY.containsKey(ch)) sb.append(FANCY.get(ch));
            else sb.append(ch);
        }

        return sb.toString();
    }
}