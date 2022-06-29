package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gl.Program;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Shader.class)
public class ShaderMixin {
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), require = 0)
    private Identifier init_identifier2(String string) {
        return replaceIdentifier(string);
    }

    @Redirect(method = "loadProgram", at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), require = 0)
    private static Identifier loadProgram_identifier(String string) {
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