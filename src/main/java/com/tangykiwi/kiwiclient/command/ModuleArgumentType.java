package com.tangykiwi.kiwiclient.command;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ModuleArgumentType implements ArgumentType<Module> {
    private static final Collection<String> EXAMPLES = KiwiClient.moduleManager.moduleList
        .stream()
        .limit(3)
        .map(module -> module.getName())
        .collect(Collectors.toList());

    private static final DynamicCommandExceptionType NO_SUCH_MODULE = new DynamicCommandExceptionType(o ->
        Component.literal("Module with name " + o + " doesn't exist."));

    public static ModuleArgumentType module() {
        return new ModuleArgumentType();
    }

    public static Module getModule(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Module.class);
    }

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();
        Module module = KiwiClient.moduleManager.getModule(argument);

        if (module == null) throw NO_SUCH_MODULE.create(argument);

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(KiwiClient.moduleManager.moduleList.stream().map(module -> module.getName()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
