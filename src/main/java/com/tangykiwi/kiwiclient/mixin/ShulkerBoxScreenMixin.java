package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.player.AutoContainer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends HandledScreen<ShulkerBoxScreenHandler> implements ScreenHandlerProvider<ShulkerBoxScreenHandler> {
    private final int rows = 3;
    private int mode;

    public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        super.init();

        if(!KiwiClient.moduleManager.getModule(AutoContainer.class).isEnabled())
            return;

        addDrawableChild(new ButtonWidget(x + backgroundWidth + 10, y + 2,
                50, 20, Text.literal("Steal"), b -> steal()));

        addDrawableChild(new ButtonWidget(x + backgroundWidth + 10, y + 24,
                50, 20, Text.literal("Store"), b -> store()));
    }

    private void steal()
    {
        runInThread(() -> shiftClickSlots(0, rows * 9, 1));
    }

    private void store()
    {
        runInThread(() -> shiftClickSlots(rows * 9, rows * 9 + 44, 2));
    }

    private void runInThread(Runnable r)
    {
        new Thread(() -> {
            try
            {
                r.run();

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }

    private void shiftClickSlots(int from, int to, int mode)
    {
        this.mode = mode;

        for(int i = from; i < to; i++)
        {
            if(handler.slots.size() == i) break;
            Slot slot = handler.slots.get(i);
            if(slot.getStack().isEmpty())
                continue;

            waitForDelay();
            if(this.mode != mode || client.currentScreen == null)
                break;

            onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
        }
    }

    private void waitForDelay()
    {
        try
        {
            Thread.sleep(((AutoContainer) KiwiClient.moduleManager.getModule(AutoContainer.class)).getDelay());

        }catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
