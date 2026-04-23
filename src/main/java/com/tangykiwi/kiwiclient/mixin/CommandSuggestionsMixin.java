package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tangykiwi.kiwiclient.KiwiClient;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {
    @Shadow
    private @Nullable ParseResults<ClientSuggestionProvider> currentParse;

    @Shadow @Final
    private EditBox input;

    @Shadow
    private CommandSuggestions.SuggestionsList suggestions;

    @Shadow 
    private boolean keepSuggestions;

    @Shadow
    private @Nullable CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    protected abstract void updateUsageInfo(ParseResults<ClientSuggestionProvider> currentParse, Suggestions suggestions);

    @Inject(method = "updateCommandInfo",
        at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false),
        cancellable = true
    )
    public void onRefresh(CallbackInfo ci, @Local(name = "reader") StringReader reader) {
        String prefix = KiwiClient.PREFIX;
        int length = prefix.length();

        if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor())) {
            reader.setCursor(reader.getCursor() + length);

            if (this.currentParse == null) {
                this.currentParse = KiwiClient.commandManager.DISPATCHER.parse(reader, mc.player.connection.getSuggestionsProvider());
            }

            int cursor = input.getCursorPosition();
            if (cursor >= length && (this.suggestions == null || !this.keepSuggestions)) {
                this.pendingSuggestions = KiwiClient.commandManager.DISPATCHER.getCompletionSuggestions(this.currentParse, cursor);
                this.pendingSuggestions.thenAccept(suggestionResult -> {
                    if (this.pendingSuggestions.isDone()) {
                        this.updateUsageInfo(this.currentParse, suggestionResult);
                    }
                });
            }

            ci.cancel();
        }
    }
}
