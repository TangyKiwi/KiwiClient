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
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ModuleClickGuiScreen extends ClickGuiScreen {

	private TextFieldWidget searchField;

	public ModuleClickGuiScreen() {
		super(Text.literal("ClickGui"));
	}

	public void init() {
		super.init();

		searchField = new TextFieldWidget(textRenderer, 2, 2, 100, 12, Text.empty());
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
		i += len + 5;
		addWindow(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.OTHER), i, 18, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.OTHER.toString())), new ItemStack(Items.COMMAND_BLOCK)));

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

		int len = 85;

		for (Window w : getWindows()) {
			if (w instanceof ModuleWindow) {
				((ModuleWindow) w).setSearchedModule(searchMods);
				((ModuleWindow) w).setLen(len);
			}
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
