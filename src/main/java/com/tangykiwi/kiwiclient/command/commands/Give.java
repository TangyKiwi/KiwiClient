package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.DynamicRegistryManager;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Give extends Command {
    private final static SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode."));
    private final static SimpleCommandExceptionType NO_SPACE = new SimpleCommandExceptionType(Text.literal("No space in hotbar."));

    public Give() {
        super("give", "Gives you any item with optional nbt");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack(new CommandRegistryAccess(DynamicRegistryManager.BUILTIN.get()))).executes(context -> {
            if (!Utils.mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE.create();

            ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
            if (!Utils.mc.player.getInventory().insertStack(item)) {
                throw NO_SPACE.create();
            }

            return SINGLE_SUCCESS;
        }).then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            if (!Utils.mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE.create();

            ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(IntegerArgumentType.getInteger(context, "number"), false);
            if (!Utils.mc.player.getInventory().insertStack(item)) {
                throw NO_SPACE.create();
            }

            return SINGLE_SUCCESS;
        })));
    }
}
