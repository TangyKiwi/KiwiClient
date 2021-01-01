package com.tangykiwi.kiwiclient.mixin;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestor.class)
public class CommandSuggestorMixin {

    @Shadow
    CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    TextFieldWidget textField;

    @Inject(method = "refresh", at = @At(value = "TAIL"))
    public void onRefresh(CallbackInfo callbackInfo) {
        String msg = this.textField.getText();
        String string = msg.substring(0, this.textField.getCursor());
        int start = this.textField.getCursor();
        Collection<String> collection = KiwiClient.commandManager.getCommands();
        this.pendingSuggestions = CommandSource.suggestMatching((Iterable) collection, new SuggestionsBuilder(string, start));
    }
}
