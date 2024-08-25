package com.tangykiwi.kiwiclient.mixin;

import com.mojang.authlib.GameProfile;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUDLineVisible;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChatHudLine.Visible.class)
public abstract class ChatHudLineVisibleMixin implements IChatHUDLineVisible {
    @Shadow @Final private OrderedText content;
    @Unique private int id;
    @Unique private GameProfile sender;
    @Unique private boolean startOfEntry;

    @Override
    public String kiwiclient$getText() {
        StringBuilder sb = new StringBuilder();

        content.accept((index, style, codePoint) -> {
            sb.appendCodePoint(codePoint);
            return true;
        });

        return sb.toString();
    }

    @Override
    public int kiwiclient$getId() {
        return id;
    }

    @Override
    public void kiwiclient$setId(int id) {
        this.id = id;
    }

    @Override
    public GameProfile kiwiclient$getSender() {
        return sender;
    }

    @Override
    public void kiwiclient$setSender(GameProfile profile) {
        sender = profile;
    }

    @Override
    public boolean kiwiclient$isStartOfEntry() {
        return startOfEntry;
    }

    @Override
    public void kiwiclient$setStartOfEntry(boolean start) {
        startOfEntry = start;
    }
}
