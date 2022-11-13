package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.modules.render.Search;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.util.registry.DynamicRegistryManager;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SearchBlocks extends Command {
    CommandRegistryAccess commandRegistryAccess = new CommandRegistryAccess(DynamicRegistryManager.BUILTIN.get());
    public SearchBlocks() {
        super("searchblocks", "Add/Remove/List blocks to search module", "sb", "search", "blocks");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)).executes(context -> {
            Search search = (Search) KiwiClient.moduleManager.getModule(Search.class);
            Block block = context.getArgument("block", BlockStateArgument.class).getBlockState().getBlock();
            search.blocks.add(block);
            addMessage("Added " + block.getName().getString() + " to Search");
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("rem").then(argument("block", BlockStateArgumentType.blockState(commandRegistryAccess)).executes(context -> {
            Search search = (Search) KiwiClient.moduleManager.getModule(Search.class);
            Block block = context.getArgument("block", BlockStateArgument.class).getBlockState().getBlock();
            search.blocks.remove(block);
            addMessage("Removed " + block.getName().getString() + " from Search");
            return SINGLE_SUCCESS;
        })));

        builder.then(literal("list").executes(context -> {
            Search search = (Search) KiwiClient.moduleManager.getModule(Search.class);
            addMessage("Blocks in Search:");
            for(Block block : search.blocks.stream().sorted().toList()) {
                addMessage(block.getName().getString());
            }
            return SINGLE_SUCCESS;
        }));
    }
}
