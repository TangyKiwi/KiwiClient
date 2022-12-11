package com.tangykiwi.kiwiclient.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;

@Mixin(Screen.class)
public interface ScreenAccessor {

    @Accessor
    public abstract List<Drawable> getDrawables();

    @Accessor
    public abstract void setDrawables(List<Drawable> drawables);

    @Invoker
    public abstract void callRenderTextHoverEffect(MatrixStack matrices, Style style, int x, int y);
}
