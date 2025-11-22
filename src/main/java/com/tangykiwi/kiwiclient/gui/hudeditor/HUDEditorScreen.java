package com.tangykiwi.kiwiclient.gui.hudeditor;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.Base;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.ActiveModsComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.ArmorComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.BiomeComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.CoordsComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.FPSComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.HUDComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.IPComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.InventoryComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.NetherCoordsComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.PingComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.PlayerWaypointsComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.SpeedComponent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.TPSComponent;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class HUDEditorScreen extends Base {
    public static HUDEditorScreen INSTANCE = new HUDEditorScreen();
    public FontRenderer fontRenderer;

    public List<HUDComponent> components = new ArrayList<HUDComponent>();

    protected int keyDown = -1;
    protected boolean lmDown = false;
    protected boolean rmDown = false;
    protected boolean lmHeld = false;
    protected int mwhScroll = 0;
    protected int mwvScroll = 0;

    public HUDEditorScreen() {
        super(Text.literal("HUD Editor"));
        fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
    }

    public void initComponents() {
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();
        int i = 0;
        components.add(new NetherCoordsComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new CoordsComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new SpeedComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new BiomeComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new IPComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new TPSComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new PingComponent(0.3F, h - i * 6 + 2));
        i++;
        components.add(new FPSComponent(0.3F, h - i * 6 + 2));
        i++;

        components.add(new InventoryComponent(w - 164, h - 56));
        components.add(new ArmorComponent(w / 2, h / 2));

        components.add(new ActiveModsComponent(0, 0));
        // components.add(new PlayerWaypointsComponent(0, 0));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (HUDComponent component : components) {
            component.updateKeys(mouseX, mouseY, keyDown, lmDown, rmDown, lmHeld, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);

        context.fill(width / 2 - 50, -1, width / 2 - 2, 12, 
            mouseX >= width / 2 - 50 && mouseX <= width / 2 - 2 && mouseY >= 0 && mouseY <= 12 ? 0x60b070f0 : 0x60606090);
        context.fill(width / 2 + 2, -1, width / 2 + 50, 12,
            mouseX >= width / 2 + 2 && mouseX <= width / 2 + 50 && mouseY >= 0 && mouseY <= 12 ? 0x60b070f0 : 0x60606090);
        fontRenderer.drawCenteredStringWithShadow(context, "ClickGUI", width / 2 - 26, 2, 0xf0f0f0);
        fontRenderer.drawCenteredStringWithShadow(context, "HUD Editor", width / 2 + 26, 2, 0xf0f0f0);

        if (InputUtil.isKeyPressed(KiwiClient.mc.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            RenderUtils.drawLine2D(context, KiwiClient.mc.currentScreen.width / 2F, 0F, KiwiClient.mc.currentScreen.width / 2F, (float) KiwiClient.mc.currentScreen.height, 0.5F, 0xFFFFFFFF);
            RenderUtils.drawLine2D(context, 0F, KiwiClient.mc.currentScreen.height / 2F, (float) KiwiClient.mc.currentScreen.width, KiwiClient.mc.currentScreen.height / 2F, 0.5F, 0xFFFFFFFF);
        }

        for(HUDComponent component : components) {
            component.render(context);
        }

        lmDown = false;
        rmDown = false;
        mwhScroll = 0;
        mwvScroll = 0;
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0) {
            if (mouseX >= width / 2 - 50 && mouseX <= width / 2 - 2 && mouseY >= 0 && mouseY <= 12) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
				this.client.setScreen(ClickGUIScreen.INSTANCE);
			} else if (mouseX >= width / 2 + 2 && mouseX <= width / 2 + 50 && mouseY >= 0 && mouseY <= 12) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
                this.client.setScreen(INSTANCE);
			} else {
                lmDown = true;
                lmHeld = true;
            }
        } else if (button == 1) {
            rmDown = true;
        }

        for (HUDComponent component : components) {
            if (mouseX > component.getX() && mouseX < component.getX() + component.getWidth() && 
                mouseY > component.getY() && mouseY < component.getY() + component.getHeight()) {
                component.mouseClicked(mouseX, mouseY, button);
                break;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    public boolean mouseReleased(Click click) {
        if (click.button() == 0) lmHeld = false;

        for (HUDComponent component : components) {
            component.mouseReleased(click.x(), click.y(), click.button());
        }

        return super.mouseReleased(click);
    }

    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.getKeycode();
        int scanCode = keyInput.scancode();
        int modifiers = keyInput.modifiers();
        keyDown = keyCode;

        for (HUDComponent component : components) {
            component.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyInput);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        mwhScroll = (int) horizontalAmount;
        mwvScroll = (int) verticalAmount;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public HUDComponent getComponent(String componentName) {
        for (HUDComponent c : components) {
            if (c.getName().equals(componentName)) return c;
        }
        return null;
    }
}
