package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gl.ShaderStage;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(JsonEffectShaderProgram.class)
public class JsonEffectShaderProgramMixin {

    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;ofVanilla(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"))
    private void init_identifier(Args args, ResourceFactory resourceFactory, String name) {
        args.set(0, replaceIdentifier(args.get(0), name));
    }

    @ModifyArgs(method = "loadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;ofVanilla(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"))
    private static void loadEffect_identifier(Args args, ResourceFactory resourceFactory, ShaderStage.Type type, String name) {
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
