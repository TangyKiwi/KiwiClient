package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.InputConstants;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;

import net.minecraft.client.CameraType;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class Freecam extends Module {
    public Vec3 pos;
    public Vec3 prevPos;
    public float yaw;
    public float pitch;
    public float prevYaw;
    public float prevPitch;
    public boolean target;
    private boolean forward;
    private boolean backward;
    private boolean right;
    private boolean left;
    private boolean up;
    private boolean down;
    private boolean sneaking;
    private CameraType perspective;

    public Freecam() {
        super("Freecam", "Detaches your camera", Category.RENDER,
            new SliderSetting("Speed", "Camera movement speed", 0.1, 1.5, 0.5, 1));
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            this.toggle();
        } else {
            mc.levelRenderer.allChanged();
            this.yaw = mc.player.getYRot();
            this.pitch = mc.player.getXRot();
            this.pos = mc.gameRenderer.getMainCamera().position();
            this.prevPos = mc.gameRenderer.getMainCamera().position();
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.perspective = mc.options.getCameraType();

            this.forward = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_UP);
            this.backward = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_DOWN);
            this.right = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_RIGHT);
            this.left = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_LEFT);
            this.up = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_SPACE);
            this.down = InputConstants.isKeyDown(mc.getWindow(), InputConstants.KEY_LSHIFT);
            this.sneaking = mc.options.keyShift.isDown();
            this.unpress();
            super.onEnable();
        }
    }

    @Override
    public void onDisable() {
        this.unpress();
        mc.execute(mc.levelRenderer::allChanged);
        mc.options.setCameraType(perspective);
        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        if (mc.getCameraEntity().isInWall()) {
            mc.getCameraEntity().noPhysics = true;
        }
        if (!perspective.isFirstPerson()) {
            mc.options.setCameraType(CameraType.FIRST_PERSON);
        }

        if (mc.screen == null) {
            Vec3 forward = Vec3.directionFromRotation(0.0F, this.yaw);
            Vec3 right = Vec3.directionFromRotation(0.0F, this.yaw + 90.0F);
            double velX = 0.0D;
            double velY = 0.0D;
            double velZ = 0.0D;

            if(mc.hitResult instanceof EntityHitResult ehr) {
                lookAt(ehr.getLocation());
                target = true;
            } else if (mc.hitResult instanceof BlockHitResult bhr) {
                lookAt(bhr.getLocation());
                target = true;
            } else {
                target = false;
            }

            float speed = getSetting(0).asSlider().getValueFloat();
            boolean a = false;
            if (this.forward) {
                velX += forward.x * (double)speed;
                velZ += forward.z * (double)speed;
                a = true;
            }

            if (this.backward) {
                velX -= forward.x * (double)speed;
                velZ -= forward.z * (double)speed;
                a = true;
            }

            boolean b = false;
            if (this.right) {
                velX += right.x * (double)speed;
                velZ += right.z * (double)speed;
                b = true;
            }

            if (this.left) {
                velX -= right.x * (double)speed;
                velZ -= right.z * (double)speed;
                b = true;
            }

            if (a && b) {
                double diagonal = 1.0D / Math.sqrt(2.0D);
                velX *= diagonal;
                velZ *= diagonal;
            }

            if (this.up) {
                velY += speed;
            }

            if (this.down) {
                velY -= speed;
            }

            this.prevPos = this.pos;
            this.pos = new Vec3(this.pos.x + velX, this.pos.y + velY, this.pos.z + velZ);
        }
    }

    private void lookAt(Vec3 pos) {
        Vec3 player = mc.player.getEyePosition();
        double dirx = player.x - pos.x;
        double diry = player.y - pos.y;
        double dirz = player.z - pos.z;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;
        yaw += 90f;
        mc.player.setYRot((float) yaw);
        mc.player.setXRot((float) pitch);
    }

    private void unpress() {
        mc.options.keyUp.setDown(false);
        mc.options.keyDown.setDown(false);
        mc.options.keyRight.setDown(false);
        mc.options.keyLeft.setDown(false);
        mc.options.keyJump.setDown(false);
        mc.options.keyShift.setDown(false);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onOpenScreen(OpenScreenEvent e) {
        this.unpress();
        this.prevPos = this.pos;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    @Subscribe
    @AllowConcurrentEvents
    private void onKey(KeyPressEvent event) {
        KeyEvent input = event.getKeyInput();
        boolean cancel = true;
        if (mc.options.keyUp.matches(input)) {
            this.forward = event.getAction() != 0;
        } else if (mc.options.keyDown.matches(input)) {
            this.backward = event.getAction() != 0;
        } else if (mc.options.keyRight.matches(input)) {
            this.right = event.getAction() != 0;
        } else if (mc.options.keyLeft.matches(input)) {
            this.left = event.getAction() != 0;
        } else if (mc.options.keyJump.matches(input)) {
            this.up = event.getAction() != 0;
        } else if (mc.options.keyShift.matches(input)) {
            this.down = event.getAction() != 0;
        } else {
            cancel = false;
        }

        if(cancel) {
            event.setCancelled(true);
        }
    }

    public void changeLookDirection(double deltaX, double deltaY) {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.yaw = (float)((double)this.yaw + deltaX);
        this.pitch = (float)((double)this.pitch + deltaY);
        this.pitch = Mth.clamp(this.pitch, -90.0F, 90.0F);
    }

    public double getX(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevPos.x, this.pos.x);
    }

    public double getY(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevPos.y, this.pos.y);
    }

    public double getZ(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevPos.z, this.pos.z);
    }

    public double getYaw(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevYaw, this.yaw);
    }

    public double getPitch(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevPitch, this.pitch);
    }
}
