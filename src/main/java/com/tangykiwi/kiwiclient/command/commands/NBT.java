package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DataResult;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.ComponentMapArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.component.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class NBT extends Command {
    private static final DynamicCommandExceptionType MALFORMED_ITEM_EXCEPTION = new DynamicCommandExceptionType(
        error -> Text.stringifiedTranslatable("arguments.item.malformed", error)
    );

    public NBT() {
        super("nbt", "Item NBT manipulation commands");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes(ctx -> {
            ItemStack stack = mc.player.getInventory().getSelectedStack();

            if (validBasic(stack)) {
                ComponentMap itemComponents = stack.getComponents();
                ComponentMap newComponents = ComponentMapArgumentType.getComponentMap(ctx, "component");

                ComponentMap testComponents = ComponentMap.of(itemComponents, newComponents);
                DataResult<Unit> dataResult = ItemStack.validateComponents(testComponents);
                dataResult.getOrThrow(MALFORMED_ITEM_EXCEPTION::create);

                stack.applyComponentsFrom(testComponents);

                setStack(stack);
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("set").then(argument("component", ComponentMapArgumentType.componentMap(REGISTRY_ACCESS)).executes(ctx -> {
            ItemStack stack = mc.player.getInventory().getSelectedStack();

            if (validBasic(stack)) {
                ComponentMap components = ComponentMapArgumentType.getComponentMap(ctx, "component");
                MergedComponentMap stackComponents = (MergedComponentMap) stack.getComponents();

                DataResult<Unit> dataResult = ItemStack.validateComponents(components);
                dataResult.getOrThrow(MALFORMED_ITEM_EXCEPTION::create);

                ComponentChanges.Builder changesBuilder = ComponentChanges.builder();
                Set<ComponentType<?>> types = stackComponents.getTypes();

                //set changes
                for (Component<?> entry : components) {
                    changesBuilder.add(entry);
                    types.remove(entry.type());
                }

                //remove the rest
                for (ComponentType<?> type : types) {
                    changesBuilder.remove(type);
                }

                stackComponents.applyChanges(changesBuilder.build());

                setStack(stack);
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("component", RegistryKeyArgumentType.registryKey(RegistryKeys.DATA_COMPONENT_TYPE)).executes(ctx -> {
            ItemStack stack = mc.player.getInventory().getSelectedStack();

            if (validBasic(stack)) {
                @SuppressWarnings("unchecked")
                RegistryKey<ComponentType<?>> componentTypeKey = (RegistryKey<ComponentType<?>>) ctx.getArgument("component", RegistryKey.class);

                ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(componentTypeKey);

                MergedComponentMap components = (MergedComponentMap) stack.getComponents();
                components.applyChanges(ComponentChanges.builder().remove(componentType).build());

                setStack(stack);
            }

            return SINGLE_SUCCESS;
        }).suggests((ctx, suggestionsBuilder) -> {
            ItemStack stack = mc.player.getInventory().getSelectedStack();
            if (stack != ItemStack.EMPTY) {
                ComponentMap components = stack.getComponents();
                String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

                CommandSource.forEachMatching(components.getTypes().stream().map(Registries.DATA_COMPONENT_TYPE::getEntry).toList(), remaining, entry -> {
                    if (entry.getKey().isPresent()) return entry.getKey().get().getValue();
                    return null;
                }, entry -> {
                    ComponentType<?> dataComponentType = entry.value();
                    if (dataComponentType.getCodec() != null) {
                        if (entry.getKey().isPresent()) {
                            suggestionsBuilder.suggest(entry.getKey().get().getValue().toString());
                        }
                    }
                });
            }

            return suggestionsBuilder.buildFuture();
        })));

        builder.then(literal("get").executes(context -> {
            DataCommandObject dataCommandObject = new EntityDataObject(mc.player);
            NbtPathArgumentType.NbtPath handPath = NbtPathArgumentType.NbtPath.parse("SelectedItem");

            MutableText text = Text.empty();
            String nbt = "{}";

            try {
                List<NbtElement> nbtElement = handPath.get(dataCommandObject.getNbt());
                if (!nbtElement.isEmpty()) {
                    text.append(" ").append(NbtHelper.toPrettyPrintedText(nbtElement.getFirst()));
                    nbt = nbtElement.getFirst().toString();
                }
            } catch (CommandSyntaxException e) {
                text.append("{}");
            }

            MutableText copyButton = Text.literal("NBT").setStyle(Style.EMPTY
                .withFormatting(Formatting.UNDERLINE)
                .withHoverEvent(new HoverEvent.ShowText(
                    Text.literal("Copy the NBT data to your clipboard.")
                ))
                .withClickEvent(new ClickEvent.CopyToClipboard(nbt)));

            text = copyButton.append(text);

            addMessage(text);

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            DataCommandObject dataCommandObject = new EntityDataObject(mc.player);
            NbtPathArgumentType.NbtPath handPath = NbtPathArgumentType.NbtPath.parse("SelectedItem");

            MutableText text = Text.empty();
            String nbt = "{}";

            try {
                List<NbtElement> nbtElement = handPath.get(dataCommandObject.getNbt());
                if (!nbtElement.isEmpty()) {
                    text.append(" ").append(NbtHelper.toPrettyPrintedText(nbtElement.getFirst()));
                    nbt = nbtElement.getFirst().toString();
                }
            } catch (CommandSyntaxException e) {
                text.append("{}");
            }

            mc.keyboard.setClipboard(nbt);

            text.append(" data copied!");

            MutableText copyButton = Text.literal("NBT").setStyle(Style.EMPTY
                .withFormatting(Formatting.UNDERLINE)
                .withHoverEvent(new HoverEvent.ShowText(
                    Text.literal("Copy the NBT data to your clipboard.")
                ))
                .withClickEvent(new ClickEvent.CopyToClipboard(nbt)));

            text = copyButton.append(text);
            
            addMessage(text);

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            ItemStack stack = mc.player.getInventory().getSelectedStack();

            if (validBasic(stack)) {
                int count = IntegerArgumentType.getInteger(context, "count");
                stack.setCount(count);
                setStack(stack);
                addMessage("Set mainhand stack count to " + count + ".");
            }

            return SINGLE_SUCCESS;
        })));
    }

    private void setStack(ItemStack stack) {
        mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().getSelectedSlot(), stack));
    }

    private boolean validBasic(ItemStack stack) {
        if (!mc.player.getAbilities().creativeMode) {
            addMessage("Creative mode only.");
            return false;
        }

        if (stack == ItemStack.EMPTY) {
            addMessage("You must hold an item in your main hand.");
            return false;
        }
        return true;
    }
}
