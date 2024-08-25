package com.tangykiwi.kiwiclient.util.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.modules.client.Tooltips;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;

public class MapTooltipComponent implements TooltipComponent, ITooltipData {
    private static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.of("textures/map/map_background.png");
    private final int mapId;

    public MapTooltipComponent(int mapId) {
        this.mapId = mapId;
    }

    @Override
    public int getHeight() {
        double scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting(5).asToggle().getChild(0).asSlider().getValue();
        return (int) ((128 + 16) * scale) + 2;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        double scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting(5).asToggle().getChild(0).asSlider().getValue();
        return (int) ((128 + 16) * scale);
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        double scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting(5).asToggle().getChild(0).asSlider().getValue();

        // Background
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale((float) (scale) * 2, (float) (scale) * 2, 0);
        matrices.scale((64 + 8) / 64f, (64 + 8) / 64f, 0);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, TEXTURE_MAP_BACKGROUND);
        context.drawTexture(TEXTURE_MAP_BACKGROUND, 0, 0, 0, 0, 0, 64, 64, 64, 64);
        matrices.pop();

        // Contents
        VertexConsumerProvider.Immediate consumer = mc.getBufferBuilders().getEntityVertexConsumers();
        MapState mapState = FilledMapItem.getMapState(new MapIdComponent(mapId), mc.world);
        if (mapState == null) return;
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale((float) scale, (float) scale, 0);
        matrices.translate(8, 8, 0);
        mc.gameRenderer.getMapRenderer().draw(matrices, consumer, new MapIdComponent(mapId), mapState, false, 0xF000F0);
        consumer.draw();
        matrices.pop();
    }
}
