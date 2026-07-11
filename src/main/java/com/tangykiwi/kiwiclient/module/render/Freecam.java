package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ChunkOcclusionEvent;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.CameraAccessor;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;

import net.minecraft.client.Camera;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Freecam extends Module {

    private ClientInput savedInput;
	private Vec3 prevPos;
	private Vec3 pos;

    public Freecam() {
        super("Freecam", "Detaches your camera", Category.RENDER,
            new SliderSetting("Speed", "Camera movement speed", 0.1, 10, 2, 1));
    }

    @Override
    public void onEnable() {
        Camera camera = mc.gameRenderer.mainCamera();
        CameraAccessor iCamera = (CameraAccessor) camera;
        LocalPlayer player = mc.player;

        Vec3 startPos;
        if (player != null) {
            startPos = player.position().add(0, player.getEyeHeight(), 0);
            savedInput = player.input;
            player.input = new ClientInput();
        } else {
            startPos = camera.position();
        }

        prevPos = startPos;
        pos = startPos;
        iCamera.setCameraPos(startPos);

        mc.levelRenderer.invalidateCompiledGeometry(mc.level, mc.options, camera, mc.getBlockColors());

        super.onEnable();
    }

    @Override
    public void onDisable() {
        LocalPlayer player = mc.player;
		if (player != null && savedInput != null) {
			player.input = savedInput;
		}

		savedInput = null;

        mc.levelRenderer.invalidateCompiledGeometry(mc.level, mc.options, mc.gameRenderer.mainCamera(), mc.getBlockColors());

        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onLevelRender(LevelRenderEvent event) {
        if (mc.player == null || mc.level == null) return;

        Camera camera = mc.gameRenderer.mainCamera();
        CameraAccessor iCamera = (CameraAccessor) camera;

        float tickDelta = event.getPartialTicks();

        Vec3 interpolatedPos = new Vec3(Mth.lerp(tickDelta, prevPos.x, pos.x),
            Mth.lerp(tickDelta, prevPos.y, pos.y),
            Mth.lerp(tickDelta, prevPos.z, pos.z));
        iCamera.setCameraPos(interpolatedPos);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPreTick(TickEvent.Pre event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        if (player.input instanceof KeyboardInput) {
            savedInput = player.input;
            player.input = new ClientInput();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPostTick(TickEvent.Post event) {
        if (mc.player == null) return;

        Camera camera = mc.gameRenderer.mainCamera();
        Vec3 cameraPos = camera.position();
        prevPos = cameraPos;

        Vec3 forward = Vec3.directionFromRotation(0, camera.yRot());
        Vec3 right = Vec3.directionFromRotation(0, camera.yRot() + 90);

        Vec3 velocity = new Vec3(0, 0, 0);

        double flySpeed = ((SliderSetting) getSetting("Speed")).getValue();

        if (mc.options.keyUp.isDown()) {
			velocity = velocity.add(forward.scale(flySpeed));
		} else if (mc.options.keyDown.isDown()) {
			velocity = velocity.subtract(forward.scale(flySpeed));
		}

		if (mc.options.keyRight.isDown()) {
			velocity = velocity.add(right.scale(flySpeed));
		} else if (mc.options.keyLeft.isDown()) {
			velocity = velocity.subtract(right.scale(flySpeed));
		}

		if (mc.options.keyJump.isDown()) {
			velocity = velocity.add(0, flySpeed, 0);
		} else if (mc.options.keyShift.isDown())
			velocity = velocity.add(0, -flySpeed, 0);

		pos = cameraPos.add(velocity);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onChunkOcclusion(ChunkOcclusionEvent e) {
        e.cancel();
    }
}
