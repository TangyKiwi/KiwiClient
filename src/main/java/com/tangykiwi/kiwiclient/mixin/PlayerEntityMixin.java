package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.commands.Enchant;
import com.tangykiwi.kiwiclient.command.commands.SetTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.*;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(
            at = {@At("HEAD")},
            method = {"tick"}
    )
    public void onTick(CallbackInfo ci) {
        MinecraftClient MC = MinecraftClient.getInstance();
        if (MC.player != null) {
//            GameOptions options = MC.options;
//            options.tutorialStep = TutorialStep.PUNCH_TREE;
//            options.advancedItemTooltips = false;
//            options.debugEnabled = false;
//            options.joinedFirstServer = false;
//            options.smoothCameraEnabled = true;
//            options.getGamma().setValue(0.5);
//            options.getAutoJump().setValue(true);
//            options.getAttackIndicator().setValue(AttackIndicator.OFF);
//            options.getBobView().setValue(true);
//            options.getChatLineSpacing().setValue(0.0);
//            options.getChatScale().setValue(0.2);
//            options.getViewDistance().setValue(6);
//            options.getCloudRenderMode().setValue(CloudRenderMode.FANCY);
//            options.getMaxFps().setValue(60);
//            options.getMainArm().setValue(Arm.LEFT);
//            options.getGraphicsMode().setValue(GraphicsMode.FABULOUS);
//            options.getNarrator().setValue(NarratorMode.ALL);
//            options.getInvertYMouse().setValue(true);
//            options.getSprintToggled().setValue(false);
//            options.setSoundVolume(SoundCategory.MUSIC, 100.0F);
//            options.getGuiScale().setValue(3);



            if(KiwiClient.commandManager.get(SetTarget.class).target != "") {
                for(Entity player : MC.world.getEntities()) {
                    if(player.getDisplayName().contains(Text.of("TheDumbDude"))) {
                        Vec3d vel = player.getPos().add(MC.player.getPos().multiply(-1.0));
                        if (MC.player.getPos().distanceTo(player.getPos()) > 9.0) {
                            MC.player.setVelocity(vel.normalize().multiply(9.0));
                        } else {
                            MC.player.setPosition(player.getPos());
                            if (MC.player.getAttackCooldownProgress(0.0F) > 1.0F) {
                                MC.interactionManager.attackEntity(MC.player, player);
                                //MC.getNarratorManager().narrate("Attacking");
                            }
                        }
                    }
                }
            }

//            List<AbstractClientPlayerEntity> players = MC.world.getPlayers();
//            Iterator var5 = players.iterator();
//
//            while(var5.hasNext()) {
//                PlayerEntity player = (PlayerEntity)var5.next();
//                if (player.getDisplayName().contains(Text.of("TheDumbDude"))) {
//                    Vec3d vel = player.getPos().add(MC.player.getPos().multiply(-1.0));
//                    if (MC.player.getPos().distanceTo(player.getPos()) > 9.0) {
//                        MC.player.setVelocity(vel.normalize().multiply(9.0));
//                    } else {
//                        MC.player.setPosition(player.getPos());
//                        if (MC.player.getAttackCooldownProgress(0.0F) > 1.0F) {
//                            MC.interactionManager.attackEntity(MC.player, player);
//                            MC.getNarratorManager().narrate("Attacking");
//                        }
//                    }
//                }
//            }
        }

    }
}
