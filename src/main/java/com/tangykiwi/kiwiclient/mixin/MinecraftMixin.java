package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.render.ESP;
import com.tangykiwi.kiwiclient.module.render.Freecam;
import com.tangykiwi.kiwiclient.util.ConfigManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.Entity;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.tangykiwi.kiwiclient.KiwiClient.LOGGER;
import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public ClientLevel level;
    @Shadow private IntegratedServer singleplayerServer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo callback) {
        KiwiClient.postInit();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo info) {
        KiwiClient.eventBus.post(TickEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo info) {
        KiwiClient.eventBus.post(TickEvent.Post.get());
    }

    // @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setIcon(Lnet/minecraft/resource/ResourcePack;Lnet/minecraft/client/util/Icons;)V"))
    // private void onChangeIcon(Window instance, ResourcePack resourcePack, Icons icons) throws IOException {
    //     RenderSystem.assertOnRenderThread();

    //     if (GLFW.glfwGetPlatform() == 393218) {
    //         MacWindowUtil.setApplicationIconImage(icons.getMacIcon(resourcePack));
    //         return;
    //     }
    //     setWindowIcon(KiwiClient.class.getResourceAsStream("/assets/kiwiclient/icon64.png"), KiwiClient.class.getResourceAsStream("/assets/kiwiclient/icon128.png"));
    // }

    // public void setWindowIcon(InputStream img16x16, InputStream img32x32) {
    //     try (MemoryStack memorystack = MemoryStack.stackPush()) {
    //         GLFWImage.Buffer buffer = GLFWImage.malloc(2, memorystack);
    //         List<InputStream> imgList = List.of(img16x16, img32x32);
    //         List<ByteBuffer> buffers = new ArrayList<>();

    //         for (int i = 0; i < imgList.size(); i++) {
    //             NativeImage nativeImage = NativeImage.read(imgList.get(i));
    //             ByteBuffer bytebuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);

    //             bytebuffer.asIntBuffer().put(nativeImage.copyPixelsArgb());
    //             buffer.position(i);
    //             buffer.width(nativeImage.getWidth());
    //             buffer.height(nativeImage.getHeight());
    //             buffer.pixels(bytebuffer);

    //             buffers.add(bytebuffer);
    //         }

    //         GLFW.glfwSetWindowIcon(KiwiClient.mc.getWindow().getHandle(), buffer);
    //         buffers.forEach(MemoryUtil::memFree);
    //     } catch (IOException ignored) {
    //     }
    // }

    @ModifyArg(method = "updateTitle", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setTitle(Ljava/lang/String;)V"))
    private String getWindowTitle(String original) {
        Minecraft client = KiwiClient.mc;

        String title = KiwiClient.NAME + " v" + KiwiClient.VERSION + " - MC " + KiwiClient.MC_VERSION;

        ClientPacketListener clientPlayNetworkHandler = client.getConnection();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isConnected()) {
            title += " | ";

            discordRPC.activity.setDetails("Playing");
            if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
                title += I18n.get("title.singleplayer");
                discordRPC.activity.setState("Singleplayer");
            } else if (client.getCurrentServer().isRealm()) {
                title += I18n.get("title.multiplayer.realms");
                discordRPC.activity.setState("Realms");
            } else if (this.singleplayerServer == null && (client.getCurrentServer() == null || !client.getCurrentServer().isLan())) {
                title += I18n.get("title.multiplayer.other");
//                if(KiwiClient.moduleManager.getModule(NoIP.class).isEnabled()) {
                    discordRPC.activity.setState("Multiplayer");
//                } else {
//                    discordRPC.state = client.getCurrentServerEntry().address;
//                }
            } else {
                title += I18n.get("title.multiplayer.lan");
                discordRPC.activity.setState("LAN Server");
            }
        } else {
            discordRPC.activity.setDetails("Idle");
            discordRPC.activity.setState("Main Menu");
        }

        discordRPC.update();

        return title;
    }

    @ModifyReturnValue(method = "shouldEntityAppearGlowing", at = @At("RETURN"))
    private boolean outlineEntities(boolean original, Entity entity) {
        ESP esp = (ESP) KiwiClient.moduleManager.getModule(ESP.class);
        if (esp.isEnabled() && esp.getSetting("Mode").asMode().getValue() == 0 && entity != mc.player) {
            return true;
        }
        return original;
    }

    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    public void openScreen(Screen screen, CallbackInfo info) {
        OpenScreenEvent event = new OpenScreenEvent(screen);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, boolean transferring, CallbackInfo info) {
        if (level != null) {
            for (Module m : KiwiClient.moduleManager.getEnabledMods(null)) {
                m.onDisable();
            }
        }
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        discordRPC.shutdown();

        KiwiClient.moduleManager.getModule(Freecam.class).setEnabled(false);

        LOGGER.info("Saving configs");
        ConfigManager.saveModules("default");
        ConfigManager.saveClickGUI("default");
        ConfigManager.saveHUD("default");
    }
}
