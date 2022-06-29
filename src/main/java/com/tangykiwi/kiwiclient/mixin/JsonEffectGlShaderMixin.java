package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gl.Program;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gl.JsonEffectGlShader;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(JsonEffectGlShader.class)
public class JsonEffectGlShaderMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), require = 0)
    public Identifier init_identifier(String string) {
        return replaceIdentifier(string);
    }

    @Redirect(method = "loadEffect", at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), require = 0)
    private static Identifier loadEffect_identifier(String string) {
        return replaceIdentifier(string);
    }

    private static Identifier replaceIdentifier(String string) {
        int idEnd = string.indexOf(':');
        if (idEnd != -1) {
            int idStart = string.substring(0, idEnd).lastIndexOf('/') + 1;
            if (idStart != 0) {
                return new Identifier(string.substring(idStart, idEnd), string.substring(0, idStart) + string.substring(idEnd + 1));
            }
        }

        return new Identifier(string);
    }
}
