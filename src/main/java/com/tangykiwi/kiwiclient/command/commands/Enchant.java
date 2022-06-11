package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Enchant extends Command {
    private final static SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode."));
    private final static SimpleCommandExceptionType NOT_HOLDING_ITEM = new SimpleCommandExceptionType(Text.literal("You must be holding an item."));

    public Enchant() {
        super("enchant", "Enchants the item in your hand, REQUIRES Creative mode");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("one").then(argument("enchantment", EnchantmentArgumentType.enchantment())
            .then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes(context -> {
                one(context, enchantment -> context.getArgument("level", Integer.class));
                return SINGLE_SUCCESS;
            })))
            .then(literal("max").executes(context -> {
                one(context, Enchantment::getMaxLevel);
                return SINGLE_SUCCESS;
            }))
        ));

        builder.then(literal("all_possible")
            .then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes(context -> {
                all(true, enchantment -> context.getArgument("level", Integer.class));
                return SINGLE_SUCCESS;
            })))
            .then(literal("max").executes(context -> {
                all(true, Enchantment::getMaxLevel);
                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("all")
            .then(literal("level").then(argument("level", IntegerArgumentType.integer()).executes(context -> {
                all(false, enchantment -> context.getArgument("level", Integer.class));
                return SINGLE_SUCCESS;
            })))
            .then(literal("max").executes(context -> {
                all(false, Enchantment::getMaxLevel);
                return SINGLE_SUCCESS;
            }))
        );

        builder.then(literal("clear").executes(context -> {
            ItemStack itemStack = tryGetItemStack();
            Utils.clearEnchantments(itemStack);

            syncItem();
            return SINGLE_SUCCESS;
        }));

        builder.then(literal("remove")
            .then(argument("enchantment", EnchantmentArgumentType.enchantment()).executes(context -> {
                ItemStack itemStack = tryGetItemStack();
                Utils.removeEnchantment(itemStack, context.getArgument("enchantment", Enchantment.class));

                syncItem();
                return SINGLE_SUCCESS;
            }))
            .then(literal("all").executes(context -> {
                ItemStack itemStack = tryGetItemStack();
                for(Enchantment enchantment : Registry.ENCHANTMENT) {
                    Utils.removeEnchantment(itemStack, enchantment);
                }

                syncItem();
                return SINGLE_SUCCESS;
            }))
        );
    }

    private void one(CommandContext<CommandSource> context, Function<Enchantment, Integer> level) throws CommandSyntaxException {
        ItemStack itemStack = tryGetItemStack();

        Enchantment enchantment = context.getArgument("enchantment", Enchantment.class);
        Utils.addEnchantment(itemStack, enchantment, level.apply(enchantment));

        syncItem();
    }

    private void all(boolean onlyPossible, Function<Enchantment, Integer> level) throws CommandSyntaxException {
        ItemStack itemStack = tryGetItemStack();

        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (!onlyPossible || enchantment.isAcceptableItem(itemStack)) {
                Utils.addEnchantment(itemStack, enchantment, level.apply(enchantment));
            }
        }

        syncItem();
    }

    private void syncItem() {
        Utils.mc.setScreen(new InventoryScreen(Utils.mc.player));
        Utils.mc.setScreen(null);
    }

    private ItemStack tryGetItemStack() throws CommandSyntaxException {
        if (!Utils.mc.player.isCreative()) throw NOT_IN_CREATIVE.create();

        ItemStack itemStack = getItemStack();
        if (itemStack == null) throw NOT_HOLDING_ITEM.create();

        return itemStack;
    }

    private ItemStack getItemStack() {
        ItemStack itemStack = Utils.mc.player.getMainHandStack();
        if (itemStack == null) itemStack = Utils.mc.player.getOffHandStack();
        return itemStack.isEmpty() ? null : itemStack;
    }
}