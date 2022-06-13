package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {
    public Vec3d pos;
    public Vec3d prevPos;
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
    private Perspective perspective;

    public Freecam() {
        super("Freecam", "Detaches your camera", GLFW.GLFW_KEY_U, Category.RENDER,
            new SliderSetting("Speed", 0.1, 1.5, 0.5, 1));
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            this.toggle();
        } else {
            mc.chunkCullingEnabled = false;
            this.yaw = mc.player.getYaw();
            this.pitch = mc.player.getPitch();
            this.pos = mc.gameRenderer.getCamera().getPos();
            this.prevPos = mc.gameRenderer.getCamera().getPos();
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.perspective = mc.options.getPerspective();
            this.unpress();
            super.onEnable();
        }
    }

    @Override
    public void onDisable() {
        mc.chunkCullingEnabled = true;
        mc.options.setPerspective(perspective);
        this.repress();
        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if (mc.cameraEntity.isInsideWall()) {
            mc.getCameraEntity().noClip = true;
        }
        if (!perspective.isFirstPerson()) {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
        }

        if (mc.currentScreen == null) {
            Vec3d forward = Vec3d.fromPolar(0.0F, this.yaw);
            Vec3d right = Vec3d.fromPolar(0.0F, this.yaw + 90.0F);
            double velX = 0.0D;
            double velY = 0.0D;
            double velZ = 0.0D;

            if(mc.crosshairTarget instanceof EntityHitResult) {
                lookAt(((EntityHitResult) mc.crosshairTarget).getEntity().getPos());
                target = true;
            } else if (mc.crosshairTarget instanceof BlockHitResult) {
                lookAt(mc.crosshairTarget.getPos());
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
            this.pos = new Vec3d(this.pos.x + velX, this.pos.y + velY, this.pos.z + velZ);
        }
    }

    private void lookAt(Vec3d pos) {
        Vec3d player = mc.player.getEyePos();
        double dirx = player.getX() - pos.x;
        double diry = player.getY() - pos.y;
        double dirz = player.getZ() - pos.z;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;
        yaw += 90f;
        mc.player.setYaw((float) yaw);
        mc.player.setPitch((float) pitch);
    }

    private void unpress() {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);
    }

    private void repress() {
        mc.options.forwardKey.setPressed(this.forward);
        mc.options.backKey.setPressed(this.backward);
        mc.options.rightKey.setPressed(this.right);
        mc.options.leftKey.setPressed(this.left);
        mc.options.jumpKey.setPressed(this.up);
        mc.options.sneakKey.setPressed(this.down);
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
        int keyCode = event.getKeyCode();
        boolean cancel = true;
        if (mc.options.forwardKey.matchesKey(keyCode, 0)) {
            this.forward = event.getAction() != 0;
        } else if (mc.options.backKey.matchesKey(keyCode, 0)) {
            this.backward = event.getAction() != 0;
        } else if (mc.options.rightKey.matchesKey(keyCode, 0)) {
            this.right = event.getAction() != 0;
        } else if (mc.options.leftKey.matchesKey(keyCode, 0)) {
            this.left = event.getAction() != 0;
        } else if (mc.options.jumpKey.matchesKey(keyCode, 0)) {
            this.up = event.getAction() != 0;
        } else if (mc.options.sneakKey.matchesKey(keyCode, 0)) {
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
        this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
    }

    public double getX(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevPos.x, this.pos.x);
    }

    public double getY(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevPos.y, this.pos.y);
    }

    public double getZ(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevPos.z, this.pos.z);
    }

    public double getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
    }

    public double getPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
    }
}
