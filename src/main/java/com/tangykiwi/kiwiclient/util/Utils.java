package com.tangykiwi.kiwiclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Utils {
    public static MinecraftClient mc;

    public static boolean canUpdate() {
        return mc != null && mc.world != null && mc.player != null;
    }

    public static String nameToTitle(String name) {
        return Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static void addEnchantment(ItemStack itemStack, Enchantment enchantment, int level) {
        NbtCompound tag = itemStack.getOrCreateNbt();
        NbtList listTag;

        if (!tag.contains("Enchantments", 9)) {
            listTag = new NbtList();
            tag.put("Enchantments", listTag);
        } else {
            listTag = tag.getList("Enchantments", 10);
        }

        String enchId = Registry.ENCHANTMENT.getId(enchantment).toString();

        for (NbtElement _t : listTag) {
            NbtCompound t = (NbtCompound) _t;

            if (t.getString("id").equals(enchId)) {
                t.putShort("lvl", (short) level);
                return;
            }
        }

        NbtCompound enchTag = new NbtCompound();
        enchTag.putString("id", enchId);
        enchTag.putShort("lvl", (short) level);

        listTag.add(enchTag);
    }

    public static void clearEnchantments(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null) nbt.remove("Enchantments");
    }

    public static void removeEnchantment(ItemStack itemStack, Enchantment enchantment) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return;

        if (!nbt.contains("Enchantments", 9)) return;
        NbtList list = nbt.getList("Enchantments", 10);

        String enchId = Registry.ENCHANTMENT.getId(enchantment).toString();

        for (Iterator<NbtElement> it = list.iterator(); it.hasNext();) {
            NbtCompound ench = (NbtCompound) it.next();

            if (ench.getString("id").equals(enchId)) {
                it.remove();
                break;
            }
        }
    }
}
