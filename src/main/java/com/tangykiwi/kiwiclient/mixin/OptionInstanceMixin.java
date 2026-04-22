package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public class OptionInstanceMixin<T> implements ISimpleOption<T> {
    @Shadow
    T value;

    @Shadow
    @Final
    private Consumer<T> onValueUpdate;

    @Override
    public void forceSetValue(T newValue)
    {
        if (!Minecraft.getInstance().isRunning())
        {
            value = newValue;
            return;
        }

        if (!Objects.equals(value, newValue))
        {
            value = newValue;
            onValueUpdate.accept(value);
        }
    }
}
