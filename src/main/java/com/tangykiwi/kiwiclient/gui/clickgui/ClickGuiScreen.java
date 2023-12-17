package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.gui.clickgui.window.ClickGuiWindow;
import com.tangykiwi.kiwiclient.gui.window.Window;
import com.tangykiwi.kiwiclient.gui.window.WindowScreen;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ClickGuiScreen extends WindowScreen {

	protected int keyDown = -1;
	protected boolean lmDown = false;
	protected boolean rmDown = false;
	protected boolean lmHeld = false;
	protected int mwhScroll = 0;
	protected int mwvScroll = 0;

	public ClickGuiScreen(Text title) {
		super(title);
	}

	@Override
	public void init() {

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);

		for (Window w : getWindows()) {
			if (w instanceof ClickGuiWindow) {
				((ClickGuiWindow) w).updateKeys(mouseX, mouseY, keyDown, lmDown, rmDown, lmHeld, mwvScroll);
			}
		}

		super.render(context, mouseX, mouseY, delta);

		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(0, 0, 250);

		for (Window w : getWindows()) {
			if (w instanceof ClickGuiWindow) {
				Triple<Integer, Integer, String> tooltip = ((ClickGuiWindow) w).getTooltip();

				if (tooltip != null) {
					int tooltipY = tooltip.getMiddle();

					String[] split = tooltip.getRight().split("\n", -1);
					ArrayUtils.reverse(split);
					for (String s: split) {
						Matcher mat = Pattern.compile(".{1,22}\\b\\W*").matcher(s);

						List<String> lines = new ArrayList<>();

						while (mat.find())
							lines.add(mat.group().trim());

						if (lines.isEmpty())
							lines.add(s);

						int start = tooltipY + 1;
						for (int l = 0; l < lines.size(); l++) {
							if(lines.get(l).equals("ᴍᴀᴋᴇꜱ ʏᴏᴜʀ ᴍᴇꜱꜱᴀɢᴇꜱ ғᴀɴᴄʏ!")) {
								context.fill(tooltip.getLeft(), start + (l * 10) - 1,
										tooltip.getLeft() + textRenderer.getWidth(lines.get(l)) + 3,
										start + (l * 10) + 11, 0xff000000);

								context.drawText(textRenderer, lines.get(l), tooltip.getLeft() + 2, start + (l * 10) + 1, -1, false);
							}
							else {
								context.fill(tooltip.getLeft(), start + (l * 10) - 1,
										tooltip.getLeft() + IFont.CONSOLAS.getStringWidth(lines.get(l)) + 5,
										start + (l * 10) + 11, 0xff000000);

								IFont.CONSOLAS.drawString(matrices, lines.get(l), tooltip.getLeft() + 1.5, start + (l * 10) + 1.5, -1, 1);
							}
						}

						tooltipY += lines.size() * 10;
					}
				}
			}
		}

		matrices.pop();

		lmDown = false;
		rmDown = false;
		keyDown = -1;
		mwhScroll = 0;
		mwvScroll = 0;
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			lmDown = true;
			lmHeld = true;
		} else if (button == 1) {
			rmDown = true;
		}

		for (Window w : getWindows()) {
			if (mouseX > w.x1 && mouseX < w.x2 && mouseY > w.y1 && mouseY < w.y2 && !w.closed) {
				w.mouseClicked(mouseX, mouseY, button);
				break;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0)
			lmHeld = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		keyDown = keyCode;
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		mwhScroll = (int) horizontalAmount;
		mwvScroll = (int) verticalAmount;
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}
}
