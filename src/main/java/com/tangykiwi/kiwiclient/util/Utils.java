package com.tangykiwi.kiwiclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.StringUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class Utils {
    public static MinecraftClient mc;

    public static boolean canUpdate() {
        return mc != null && mc.world != null && mc.player != null;
    }

    public static String nameToTitle(String name) {
        return Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static void addEnchantment(ItemStack itemStack, RegistryEntry<Enchantment> enchantment, int level) {
        ItemEnchantmentsComponent.Builder b = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(itemStack));
        b.add(enchantment, level);

        EnchantmentHelper.set(itemStack, b.build());
    }

    public static void clearEnchantments(ItemStack itemStack) {
        EnchantmentHelper.apply(itemStack, components -> components.remove(a -> true));
    }

    public static void removeEnchantment(ItemStack itemStack, Enchantment enchantment) {
        EnchantmentHelper.apply(itemStack, components -> components.remove(enchantment1 -> enchantment1.value().equals(enchantment)));
    }

    public static String getEnchantmentName(Enchantment enchantment) {
        String text = enchantment.description().getString();
        if(text.contains("aqua")) return "AqAf";
        if(text.contains("bane")) return "BnAr";
        if(text.contains("blas")) return "BlPr";
        if(text.contains("chan")) return "Chnl";
        if(text.contains("bind")) return "CuBi";
        if(text.contains("vani")) return "CuVa";
        if(text.contains("dept")) return "DStr";
        if(text.contains("effi")) return "Effi";
        if(text.contains("feat")) return "FeFa";
        if(text.contains("aspe")) return "FAsp";
        if(text.contains("fire")) return "FiPr";
        if(text.contains("flam")) return "Flme";
        if(text.contains("fort")) return "Fort";
        if(text.contains("fros")) return "FrWa";
        if(text.contains("impa")) return "Impl";
        if(text.contains("infi")) return "Infi";
        if(text.contains("knoc")) return "KnBa";
        if(text.contains("loot")) return "Loot";
        if(text.contains("loya")) return "Llty";
        if(text.contains("luck")) return "Luck";
        if(text.contains("mend")) return "Mend";
        if(text.contains("mult")) return "Mult";
        if(text.contains("pier")) return "Pier";
        if(text.contains("powe")) return "Powe";
        if(text.contains("proj")) return "PrPr";
        if(text.contains("prot")) return "Prot";
        if(text.contains("punc")) return "Pnch";
        if(text.contains("quic")) return "Chrg";
        if(text.contains("resp")) return "Resp";
        if(text.contains("ript")) return "Rptd";
        if(text.contains("shar")) return "Shrp";
        if(text.contains("silk")) return "Silk";
        if(text.contains("smit")) return "Smte";
        if(text.contains("soul")) return "SSpd";
        if(text.contains("swee")) return "SwpE";
        if(text.contains("swif")) return "SwSn";
        if(text.contains("thor")) return "Thrn";
        if(text.contains("unbr")) return "Unbr";
        return "NaN";
    }

    public static Quaternionf quaternionVector(Vector3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= 0.017453292F;
        }

        float f = sin(rotationAngle / 2.0F);
        float x = axis.x() * f;
        float y = axis.y() * f;
        float z = axis.z() * f;
        float w = cos(rotationAngle / 2.0F);

        return new Quaternionf(x, y, z, w);
    }

    public static Quaternionf quaternionf(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= 0.017453292F;
            y *= 0.017453292F;
            z *= 0.017453292F;
        }

        float f = sin(0.5F * x);
        float g = cos(0.5F * x);
        float h = sin(0.5F * y);
        float i = cos(0.5F * y);
        float j = sin(0.5F * z);
        float k = cos(0.5F * z);
        float xf = f * i * k + g * h * j;
        float yf = g * h * k - f * i * j;
        float zf = f * h * k + g * i * j;
        float wf = g * i * k - f * h * j;

        return new Quaternionf(xf, yf, zf, wf);
    }
}
