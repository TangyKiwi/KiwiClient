package com.tangykiwi.kiwiclient.mixin;

// import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// @Mixin(StringHelper.class)
// public abstract class StringHelperMixin {
//     @ModifyArg(method = "truncateChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringHelper;truncate(Ljava/lang/String;IZ)Ljava/lang/String;"), index = 1)
//     private static int injected(int maxLength) {
//         return Integer.MAX_VALUE;
//     }
// }
