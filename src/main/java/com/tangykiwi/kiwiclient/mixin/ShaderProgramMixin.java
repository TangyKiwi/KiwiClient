package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ShaderProgram.class)
public class ShaderProgramMixin {
    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"))
    private void init_identifier(Args args, ResourceFactory factory, String name, VertexFormat format) {
        args.set(0, replaceIdentifier(args.get(0), name));
    }
    @ModifyArgs(method = "loadShader", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;<init>(Ljava/lang/String;)V"))
    private static void loadProgram_identifier(Args args, ResourceFactory factory, ShaderStage.Type type, String name) {
        args.set(0, replaceIdentifier(args.get(0), name));
    }

    private static String replaceIdentifier(String string, String name) {
        String[] split = name.split(":");
        if (split.length > 1) {
            if ("__url__".equals(split[0]))
                return name;

            return split[0] + ":" + string.replace(name, split[1]);
        }

        return string;
    }
}