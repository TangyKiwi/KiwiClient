package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.nio.file.Path;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

// @Mixin(Downloader.class)
// public class DownloaderMixin {
//     @Shadow
//     @Final
//     private Path directory;

//     @WrapOperation(method = "method_55485", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;", ordinal = 0, remap = false))
//     private Path hookResolve(Path instance, String filename, Operation<Path> original) {
//         Path result = original.call(instance, filename);

//         if (result == null || !result.getParent().equals(directory)) {
//             return result;
//         }

//         UUID uuid = mc.getSession().getUuidOrNull();

//         if (uuid == null) {
//             uuid = Uuids.getOfflinePlayerUuid(mc.getSession().getUsername());
//         }

//         return original.call(instance.resolve(uuid.toString()), filename);
//     }
// }
