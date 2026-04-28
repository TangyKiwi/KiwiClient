package com.tangykiwi.kiwiclient.util.tooltip;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.module.client.Tooltips;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapTooltipComponent implements ITooltipData, ClientTooltipComponent {
    private static final Identifier TEXTURE_MAP_BACKGROUND = Identifier.parse("textures/map/map_background.png");
    private final int mapId;
    private final MapRenderState mapRenderState = new MapRenderState();

    public MapTooltipComponent(int mapId) {
        this.mapId = mapId;
    }

    @Override
    public int getHeight(Font textRenderer) {
        double scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting("Maps").asToggle().getChild(0).asSlider().getValue();
        return (int) ((128 + 16) * scale) + 2;
    }

    @Override
    public int getWidth(Font textRenderer) {
        double scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting("Maps").asToggle().getChild(0).asSlider().getValue();
        return (int) ((128 + 16) * scale);
    }

    @Override
    public ClientTooltipComponent getComponent() {
        return this;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        float scale = KiwiClient.moduleManager.getModule(Tooltips.class).getSetting("Maps").asToggle().getChild(0).asSlider().getValueFloat();

        // Background
        int size = (int) ((128 + 16) * scale);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_MAP_BACKGROUND, x, y, 0, 0, size, size, size, size);

        // Contents
        MapItemSavedData mapState = MapItem.getSavedData(new MapId(mapId), mc.level);
        if (mapState == null) return;

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.pose().translate(8, 8);

        mc.getMapRenderer().extractRenderState(new MapId(mapId), mapState, mapRenderState);
        graphics.map(mapRenderState);

        graphics.pose().popMatrix();
    }
}
