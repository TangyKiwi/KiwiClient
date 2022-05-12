package com.tangykiwi.kiwiclient.gui.clickgui.window;

import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.Setting;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class ModuleWindow extends ClickGuiWindow {

	public List<Module> modList = new ArrayList<>();
	public LinkedHashMap<Module, Boolean> mods = new LinkedHashMap<>();

	private int len;
	private int start;
	private int max;
	private int numMods = 24;

	private Set<Module> searchedModules;

	private Triple<Integer, Integer, String> tooltip = null;

	public ModuleWindow(List<Module> mods, int x1, int y1, int len, String title, ItemStack icon) {
		super(x1, y1, x1 + len, 0, title, icon);

		this.len = len;
		start = 0;
		max = start + numMods;
		modList = mods;

		for (Module m : mods)
			this.mods.put(m, false);

		y2 = getHeight();
	}

	// numMods lines max
	public void render(MatrixStack matrices, int mouseX, int mouseY) {
		int lines = getLines();
		boolean scrollable = lines > numMods;

		tooltip = null;
		int x = x1 + 1;
		int y = y1 + 13;
		x2 = x + len + 1;
		y2 = hiding ? y1 + 13 : y1 + 13 + getHeight();

		if (mwScroll != 0 && mouseOver(x, y, x2, y2) && !InputUtil.isKeyPressed(Utils.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
			start = MathHelper.clamp(start - mwScroll, 0, lines - numMods);
		}

		max = start + numMods;

		super.render(matrices, mouseX, mouseY);

		if (hiding) return;

		GlyphPageFontRenderer textRend = IFont.CONSOLAS;
		//TextRenderer textRend = mc.textRenderer;

		int curY = 0;
		int index = 0;
		boolean stopRender = false;
		for (Entry<Module, Boolean> m : mods.entrySet()) {
			if(index >= start) {
				if (mouseOver(x, y + curY, x + len, y + 12 + curY)) {
					DrawableHelper.fill(matrices, x, y + curY, x + len, y + 12 + curY, 0x70303070);
				}

				// If they match: Module gets marked red
				if (searchedModules != null && searchedModules.contains(m.getKey())) {
					DrawableHelper.fill(matrices, x, y + curY, x + len, y + 12 + curY, 0x50ff0000);
				}

				textRend.drawStringWithShadow(matrices, textRend.trimStringToWidth(m.getKey().getName(), len),
						x + 2, y + 2 + curY, m.getKey().isEnabled() ? 0x70efe0 : 0xc0c0c0, 1);

				String color2 = m.getValue() ? "\u00a7a" : "\u00a7c";
				if(!m.getKey().getSettings().isEmpty()) {
					if (m.getValue()) {
						IFont.CONSOLAS.drawString(matrices,
								color2 + "v",
								x + len - 8, y + 2 + curY, -1, 1);
					} else if (m.getKey().getSettings().size() > 1){
						IFont.CONSOLAS.drawStringWithShadow(matrices,
								color2 + "\u00a7l>",
								x + len - 8, y + 2 + curY, -1, 1);
					}
				}

				// Set which module settings show on
				if (mouseOver(x, y + curY, x + len, y + 12 + curY)) {
					tooltip = Triple.of(x + len + 2, y + curY, m.getKey().getDescription());

					if (lmDown)
						m.getKey().toggle();
					if (rmDown)
						mods.replace(m.getKey(), !m.getValue());
					if (lmDown || rmDown)
						mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				}

				curY += 12;
			}
			index++;

			if(index == max) {
				stopRender = true;
				break;
			}

			// draw settings
			if (m.getValue()) {
				for (Setting s : m.getKey().getSettings()) {
					if(index >= start) {
						index = s.render(this, matrices, x + 1, y + curY, len - 1, index, max);

						if (!s.getDesc().isEmpty() && mouseOver(x + 2, y + curY, x + len, y + s.getHeight(len) + curY)) {
							tooltip = s.getGuiDesc(this, x + 1, y + curY, len - 1);
						}

						DrawableHelper.fill(matrices, x + 1, y + curY, x + 2, y + curY + s.getHeight(len), 0xff8070b0);

						curY += s.getHeight(len);
					}
					index++;
					if(index >= max) {
						stopRender = true;
						break;
					}
				}
			}

			if(stopRender) {
				break;
			}
		}

		if (scrollable) {
			int scrollbarTop = y + (y2 - y - 2) * start / lines;
			int scrollbarBottom = y + (y2 - y - 2) * max / lines;
			DrawableHelper.fill(matrices, x2 - 2, scrollbarTop, x2 - 1, scrollbarBottom, Color.WHITE.getRGB());
		}
	}

	public Triple<Integer, Integer, String> getTooltip() {
		return tooltip;
	}

	public void setSearchedModule(Set<Module> mods) {
		searchedModules = mods;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getLines() {
		int lines = 0;
		for (Entry<Module, Boolean> e : mods.entrySet()) {
			lines++;

			if (e.getValue()) {
				for (Setting s : e.getKey().getSettings()) {
					lines++;
					try {
						if(s.asToggle().isExpanded()) {
							lines += s.asToggle().getChildren().size();
						}
					} catch (ClassCastException error) {

					}
				}
			}
		}

		return lines;
	}

	public int getHeight() {
		int h = 1;
		int count = 0;

		for (Entry<Module, Boolean> e : mods.entrySet()) {

			if (count >= start) {
				h += 12;
			}
			count++;
			if (count >= max) {
				return h;
			}

			if (e.getValue()) {
				for (Setting s : e.getKey().getSettings()) {
					if (count >= start) {
						h += s.getHeight(len);
					}
					count++;
					try {
						if (s.asToggle().isExpanded()) {
							count += s.asToggle().getChildren().size();
						}
					} catch (ClassCastException error ) {

					}
					if (count >= max) {
						return h;
					}
				}
			}
		}

		return h;
	}
}
