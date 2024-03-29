package com.tangykiwi.kiwiclient.gui.clickgui.window;

import com.tangykiwi.kiwiclient.gui.window.Window;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;

public abstract class ClickGuiWindow extends Window {

	protected MinecraftClient mc = MinecraftClient.getInstance();

	public int mouseX;
	public int mouseY;

	public boolean hiding;

	public int keyDown = -1;
	public boolean lmDown = false;
	public boolean rmDown = false;
	public boolean lmHeld = false;
	public int mwScroll = 0;

	public ClickGuiWindow(int x1, int y1, int x2, int y2, String title, ItemStack icon) {
		super(x1, y1, x2, y2, title, icon);
	}

	public boolean shouldClose(int mouseX, int mouseY) {
		return false;
	}

	protected void drawBar(DrawContext context, int mouseX, int mouseY, GlyphPageFontRenderer textRend) {
		MatrixStack matrices = context.getMatrices();
		/* background */
		RenderUtils.renderRoundedQuad(matrices, new Color(0xff6060b0), x1, y1, x2, y2, 5, 20);

		if(!hiding) {
			RenderUtils.renderRoundedQuad(matrices, new Color(0x90606090), x1 + 1, y1 + 12, x2 - 1, y2 - 1, 5, 20);
		}

		/* title bar */
		RenderUtils.renderRoundedQuad(matrices, new Color(0xff8070b0), x1 + 1, y1 + 1, x2 - 1, y1 + 12, 5, 20);

		/* +/- text */
		textRend.drawString(matrices, hiding ? "+" : "_", x2 - 10, y1 + (hiding ? 4 : 2), 0x000000, 1);
		textRend.drawString(matrices, hiding ? "+" : "_", x2 - 11, y1 + (hiding ? 3 : 1), 0xffffff, 1);
	}

	public void render(DrawContext context, int mouseX, int mouseY) {
		super.render(context, mouseX, mouseY);

		if (rmDown && mouseOver(x1, y1, x1 + (x2 - x1), y1 + 13)) {
			mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			hiding = !hiding;
		}
	}
	
	public Triple<Integer, Integer, String> getTooltip() {
		return null;
	}

	public boolean mouseOver(int minX, int minY, int maxX, int maxY) {
		return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY < maxY;
	}

	public void updateKeys(int mouseX, int mouseY, int keyDown, boolean lmDown, boolean rmDown, boolean lmHeld, int mwScroll) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.keyDown = keyDown;
		this.lmDown = lmDown;
		this.rmDown = rmDown;
		this.lmHeld = lmHeld;
		this.mwScroll = mwScroll;
	}
}
