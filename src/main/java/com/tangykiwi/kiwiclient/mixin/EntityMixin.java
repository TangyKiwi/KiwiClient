package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.movement.NoWorldBorder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
    @Redirect(method = "findCollisionsForMovement", at = @At(value = "INVOKE", target = "net/minecraft/world/border/WorldBorder.canCollide(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private static boolean adjustMovementForCollisions(WorldBorder instance, Entity entity, Box box) {
        return !KiwiClient.moduleManager.getModule(NoWorldBorder.class).isEnabled() && instance.canCollide(entity, box);
    }

}
