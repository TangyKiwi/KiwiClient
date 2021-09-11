package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.clickgui.window.ClickGuiWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.window.Window;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ModuleClickGuiScreen extends ClickGuiScreen {

	private TextFieldWidget searchField;

	public ModuleClickGuiScreen() {
		super(new LiteralText("ClickGui"));
	}

	public void init() {
		super.init();

		searchField = new TextFieldWidget(textRenderer, 2, 2, 100, 12, LiteralText.EMPTY /* @LasnikProgram is author lol */);
		searchField.visible = false;
		searchField.setMaxLength(20);
		searchField.setSuggestion("Search here");
		addDrawableChild(searchField);
	}

	public void initWindows() {
		int len = 85;

		int i = 10;

		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.PLAYER), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.PLAYER.toString())), new ItemStack(Items.PLAYER_HEAD)));
		i += len + 5;
		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.COMBAT), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.COMBAT.toString())), new ItemStack(Items.DIAMOND_SWORD)));
		i += len + 5;
		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.RENDER), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.RENDER.toString())), new ItemStack(Items.ENDER_EYE)));
		i += len + 5;
		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.MOVEMENT), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.MOVEMENT.toString())), new ItemStack(Items.DIAMOND_BOOTS)));
		i += len + 5;
		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.CLIENT), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.CLIENT.toString())), new ItemStack(Items.GLASS_PANE)));

		for (Window w: getWindows()) {
			if (w instanceof ClickGuiWindow) {
				((ClickGuiWindow) w).hiding = false;
			}
		}
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		searchField.visible = true;

		searchField.setSuggestion(searchField.getText().isEmpty() ? "Search here" : "");

		Set<Module> searchMods = new HashSet<>();
		if (!searchField.getText().isEmpty()) {
			for (Module m : KiwiClient.moduleManager.moduleList) {
				if (m.getName().toLowerCase(Locale.ENGLISH).contains(searchField.getText().toLowerCase(Locale.ENGLISH).replace(" ", ""))) {
					searchMods.add(m);
				}
			}
		}

		for (Window w : getWindows()) {
			if (w instanceof ModuleWindow) {
				((ModuleWindow) w).setSearchedModule(searchMods);
			}
		}

		int len = 85;
		for (Window w : getWindows()) {
			if (w instanceof ModuleWindow) {
				((ModuleWindow) w).setLen(len);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);

//		textRenderer.draw(matrices, "BleachHack-" + BleachHack.VERSION + "-" + SharedConstants.getGameVersion().getName(), 3, 3, 0x305090);
//		textRenderer.draw(matrices, "BleachHack-" + BleachHack.VERSION + "-" + SharedConstants.getGameVersion().getName(), 2, 2, 0x6090d0);

//		textRenderer.drawWithShadow(matrices, "Current prefix is: \"" + Command.getPrefix() + "\" (" + Command.getPrefix() + "help)", 2, height - 20, 0x99ff99);
//		textRenderer.drawWithShadow(matrices, "Use " + Command.getPrefix() + "clickgui to reset the clickgui", 2, height - 10, 0x9999ff);
	}
}
