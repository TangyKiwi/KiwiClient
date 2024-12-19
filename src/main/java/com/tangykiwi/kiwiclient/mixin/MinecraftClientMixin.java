package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Icons;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourcePack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.tangykiwi.kiwiclient.KiwiClient.discord;
import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setIcon(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/client/util/Icons;)V"))
    private void onChangeIcon(Window instance, ResourcePack resourcePack, Icons icons) throws IOException {
        RenderSystem.assertOnRenderThreadOrInit();

        if (GLFW.glfwGetPlatform() == 393218) {
            MacWindowUtil.setApplicationIconImage(icons.getMacIcon(resourcePack));
            return;
        }
        setWindowIcon(KiwiClient.class.getResourceAsStream("/assets/kiwiclient/icon64.png"), KiwiClient.class.getResourceAsStream("/assets/kiwiclient/icon128.png"));
    }

    public void setWindowIcon(InputStream img16x16, InputStream img32x32) {
        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            GLFWImage.Buffer buffer = GLFWImage.malloc(2, memorystack);
            List<InputStream> imgList = List.of(img16x16, img32x32);
            List<ByteBuffer> buffers = new ArrayList<>();

            for (int i = 0; i < imgList.size(); i++) {
                NativeImage nativeImage = NativeImage.read(imgList.get(i));
                ByteBuffer bytebuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);

                bytebuffer.asIntBuffer().put(nativeImage.copyPixelsArgb());
                buffer.position(i);
                buffer.width(nativeImage.getWidth());
                buffer.height(nativeImage.getHeight());
                buffer.pixels(bytebuffer);

                buffers.add(bytebuffer);
            }

            GLFW.glfwSetWindowIcon(KiwiClient.mc.getWindow().getHandle(), buffer);
            buffers.forEach(MemoryUtil::memFree);
        } catch (IOException ignored) {
        }
    }

    @Inject(method = "getWindowTitle", at = @At(value = "TAIL"), cancellable = true)
    private void getWindowTitle(final CallbackInfoReturnable<String> info) {
        MinecraftClient client = KiwiClient.mc;

        String title = KiwiClient.NAME + " v" + KiwiClient.VERSION + " - MC " + SharedConstants.getGameVersion().getName();

        ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            title += " | ";
            discordRPC.details = "Playing";
            if (client.getServer() != null && !client.getServer().isRemote()) {
                title += I18n.translate("title.singleplayer");
                discordRPC.state = "Singleplayer";
            } else if (client.getCurrentServerEntry().isRealm()) {
                title += I18n.translate("title.multiplayer.realms");
                discordRPC.state = "Realms";
            } else if (client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal())) {
                title += I18n.translate("title.multiplayer.other");
//                if(KiwiClient.moduleManager.getModule(NoIP.class).isEnabled()) {
                    discordRPC.state = "Multiplayer";
//                } else {
//                    discordRPC.state = client.getCurrentServerEntry().address;
//                }
            } else {
                title += I18n.translate("title.multiplayer.lan");
                discordRPC.state = "LAN Server";
            }
        } else {
            discordRPC.details = "Idle";
            discordRPC.state = "Main Menu";
        }

        discord.Discord_UpdatePresence(discordRPC);

        info.setReturnValue(title);
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        discord.Discord_Shutdown();
    }
}
