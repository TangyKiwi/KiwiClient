package com.tangykiwi.kiwiclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.client.HUD;
import com.tangykiwi.kiwiclient.util.TickRate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

// @Mixin(ClientPlayNetworkHandler.class)
// public class ClientPlayNetworkHandlerMixin {
//     @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
//     private void onSendChatMessage(String message, CallbackInfo ci) {
//         if (message.startsWith(KiwiClient.PREFIX)) {
//             try {
//                 KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
//             } catch (CommandSyntaxException e) {
//                 KiwiClient.LOGGER.error(e.getMessage());
//             }

//             mc.inGameHud.getChatHud().addToMessageHistory(message);
//             ci.cancel();
//         }
//     }

//     @Inject(method = "onGameJoin", at = @At("TAIL"))
//     private void onGameJoin(CallbackInfo ci) {
//         for (Module m : KiwiClient.moduleManager.getEnabledMods(null)) {
//             m.onEnable();
//         }

//         Arrays.fill(KiwiClient.tickRate.tickRates, 0);
//         KiwiClient.tickRate.nextIndex = 0;
//         KiwiClient.tickRate.timeGameJoined = KiwiClient.tickRate.timeLastTimeUpdate = System.currentTimeMillis();
//     }

//     @Inject(method = "onWaypoint", at = @At("HEAD"))
//     public void onWaypoint(WaypointS2CPacket packet, CallbackInfo ci) {
//         // KiwiClient.LOGGER.info("Received waypoint packet: " + packet.waypoint().getClass().getName());
//         if (packet.waypoint() instanceof TrackedWaypoint.Positional) {
//             TrackedWaypoint.Positional waypoint = (TrackedWaypoint.Positional) packet.waypoint();
//             Vec3i pos = waypoint.pos;
//             Map<Either<UUID, String>, Vec3i> waypoints = ((HUD) KiwiClient.moduleManager.getModule(HUD.class)).waypoints;
//             waypoints.put(packet.waypoint().getSource(), pos);
//         }
//     }
// }
