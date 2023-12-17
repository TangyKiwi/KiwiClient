package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = ShaderProgram.class, priority = 1100)
public class ShaderProgramMixin {

    @Shadow
    @Final
    private String name;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"), allow = 1)
    private String modifyProgramId(String id) {
        return replaceIdentifier(id, name);
    }

    @ModifyVariable(method = "loadShader", at = @At("STORE"), ordinal = 1)
    private static String modifyStageId(String id, ResourceFactory factory, ShaderStage.Type type, String name) {
        return replaceIdentifier(id, name);
    }

    private static String replaceIdentifier(String id, String name) {
        String[] split = name.split(":");
        if (split.length > 1 && id.indexOf('/') < id.indexOf(':')) {
            if ("__url__".equals(split[0]))
                return name;

            return split[0] + ":" + id.replace(name, split[1]);
        }

        return id;
    }
}