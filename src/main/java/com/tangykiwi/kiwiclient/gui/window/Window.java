package com.tangykiwi.kiwiclient.gui.window;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.gui.window.widget.WindowWidget;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class Window {

	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public String title;
	public ItemStack icon;

	public boolean closed;
	public boolean selected = false;

	private List<WindowWidget> widgets = new ArrayList<>();

	protected boolean dragging;
	public boolean draggable;
	protected int dragOffX;
	protected int dragOffY;

	public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon) {
		this(x1, y1, x2, y2, title, icon, false, true);
	}

	public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon, boolean closed) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.title = title;
		this.icon = icon;
		this.closed = closed;
		this.draggable = true;
	}

	public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon, boolean closed, boolean draggable) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.title = title;
		this.icon = icon;
		this.closed = closed;
		this.draggable = draggable;
	}

	public List<WindowWidget> getWidgets() {
		return widgets;
	}

	public <T extends WindowWidget> T addWidget(T widget) {
		widgets.add(widget);
		return widget;
	}

	public void render(DrawContext context, int mouseX, int mouseY) {
		MatrixStack matrices = context.getMatrices();
		GlyphPageFontRenderer textRend = IFont.CONSOLAS;

		if (dragging && draggable) {
			x2 = (x2 - x1) + mouseX - dragOffX - Math.min(0, mouseX - dragOffX);
			y2 = (y2 - y1) + mouseY - dragOffY - Math.min(0, mouseY - dragOffY);
			x1 = Math.max(0, mouseX - dragOffX);
			y1 = Math.max(0, mouseY - dragOffY);
		}

		drawBar(context, mouseX, mouseY, textRend);

		for (WindowWidget w : widgets) {
			if (w.shouldRender(x1, y1, x2, y2)) {
				w.render(context, x1, y1, mouseX, mouseY);
			}
		}

		boolean blockItem = icon != null && icon.getItem() instanceof BlockItem;

		/* window icon */
		if (icon != null) {
			matrices.push();
			matrices.translate(x1 + (blockItem ? 3 : 2), y1 + 1, 0);
			matrices.scale(0.6f, 0.6f, 1f);

			DiffuseLighting.enableGuiDepthLighting();
			context.drawItem(icon, 0, 0);
			DiffuseLighting.disableGuiDepthLighting();

			matrices.pop();
		}

		/* window title */
		textRend.drawStringWithShadow(matrices, title, x1 + (icon == null || icon.getItem() == Items.AIR ? 4 : (blockItem ? 15 : 14)), y1 + 3, -1, 1);
	}

	protected void drawBar(DrawContext context, int mouseX, int mouseY, GlyphPageFontRenderer textRend) {
		MatrixStack matrices = context.getMatrices();
		/* background */
		context.fill(x1, y1 + 1, x1 + 1, y2 - 1, 0xff6060b0);
		horizontalGradient(matrices, x1 + 1, y1, x2 - 1, y1 + 1, 0xff6060b0, 0xff8070b0);
		context.fill(x2 - 1, y1 + 1, x2, y2 - 1, 0xff8070b0);
		horizontalGradient(matrices, x1 + 1, y2 - 1, x2 - 1, y2, 0xff6060b0, 0xff8070b0);

		context.fill(x1 + 1, y1 + 12, x2 - 1, y2 - 1, 0x90606090);

		/* title bar */
		horizontalGradient(matrices, x1 + 1, y1 + 1, x2 - 1, y1 + 12, (selected ? 0xff6060b0 : 0xff606060), (selected ? 0xff8070b0 : 0xffa0a0a0));

		/* buttons */
		//fillGrey(matrix, x2 - 12, y1 + 3, x2 - 4, y1 + 11);
		textRend.drawString(matrices, "x", x2 - 10, y1 + 3, 0, 1);
		textRend.drawString(matrices, "x", x2 - 11, y1 + 2, -1, 1);
	}

	public boolean shouldClose(int mouseX, int mouseY) {
		if(selected && mouseX > x2 - 12 && mouseX < x2 && mouseY > y1 + 2 && mouseY < y1 + 12) {
			Utils.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}
		return false;
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (draggable && mouseX >= x1 && mouseX <= x2 - 2 && mouseY >= y1 && mouseY <= y1 + 11) {
			dragging = true;
			dragOffX = (int) mouseX - x1;
			dragOffY = (int) mouseY - y1;
		}

		if (selected) {
			try {
				for (WindowWidget w : widgets) {
					if (w.shouldRender(x1, y1, x2, y2)) {
						w.mouseClicked(x1, y1, (int) mouseX, (int) mouseY, button);
					}
				}
			} catch (ConcurrentModificationException ignored) {}
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if(draggable) dragging = false;

		if (selected) {
			for (WindowWidget w : widgets) {
				if (w.shouldRender(x1, y1, x2, y2)) {
					w.mouseReleased(x1, y1, (int) mouseX, (int) mouseY, button);
				}
			}
		}
	}

	public void tick() {
		for (WindowWidget w : widgets) {
			w.tick();
		}
	}

	public void charTyped(char chr, int modifiers) {
		if (selected) {
			for (WindowWidget w : widgets) {
				w.charTyped(chr, modifiers);
			}
		}
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (selected) {
			for (WindowWidget w : widgets) {
				w.keyPressed(keyCode, scanCode, modifiers);
			}
		}
	}

	public static void fill(DrawContext context, int x1, int y1, int x2, int y2) {
		fill(context, x1, y1, x2, y2, 0xff6060b0, 0xff8070b0, 0x00000000);
	}

	public static void fill(DrawContext context, int x1, int y1, int x2, int y2, int fill) {
		fill(context, x1, y1, x2, y2, 0xff6060b0, 0xff8070b0, fill);
	}

	public static void fill(DrawContext context, int x1, int y1, int x2, int y2, int colTop, int colBot, int colFill) {
		context.fill(x1, y1 + 1, x1 + 1, y2 - 1, colTop);
		context.fill(x1 + 1, y1, x2 - 1, y1 + 1, colTop);
		context.fill(x2 - 1, y1 + 1, x2, y2 - 1, colBot);
		context.fill(x1 + 1, y2 - 1, x2 - 1, y2, colBot);
		context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, colFill);
	}

	public static void horizontalGradient(MatrixStack matrices, int x1, int y1, int x2, int y2, int color1, int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
		float red1   = (color1 >> 16 & 255) / 255.0F;
		float green1 = (color1 >> 8 & 255) / 255.0F;
		float blue1  = (color1 & 255) / 255.0F;
		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2   = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2  = (color2 & 255) / 255.0F;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(x1, y1, 0).color(red1, green1, blue1, alpha1);
		bufferBuilder.vertex(x1, y2, 0).color(red1, green1, blue1, alpha1);
		bufferBuilder.vertex(x2, y2, 0).color(red2, green2, blue2, alpha2);
		bufferBuilder.vertex(x2, y1, 0).color(red2, green2, blue2, alpha2);
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.disableBlend();
	}
}
