package com.tangykiwi.kiwiclient.util.render;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Simple wrapper around {@link StagedVertexBuffer} to replace Minecraft's
 * {@code MultiBufferSource} which was removed in 26.2-snapshot-5.
 * Taken from Wurst (https://github.com/Wurst-Imperium/Wurst7/blob/master/src/main/java/net/wurstclient/util/WurstBufferSource.java)
 */
public final class KiwiBufferSource
{
	private final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(
		() -> "KiwiBufferSource", RenderType.BIG_BUFFER_SIZE);
	private final List<StagedVertexBuffer.Draw> draws = new ArrayList<>();
	private final List<RenderType> drawTypes = new ArrayList<>();
	
	public VertexConsumer getBuffer(RenderType renderType)
	{
		if(!drawTypes.isEmpty() && drawTypes.getLast() == renderType
			&& renderType.canConsolidateConsecutiveGeometry())
			return stagedBuffer.getVertexBuilder(draws.getLast());
		
		StagedVertexBuffer.Draw draw =
			stagedBuffer.appendDraw(renderType.format(),
				renderType.primitiveTopology(), renderType.sortOnUpload()
					? RenderSystem.getProjectionType().vertexSorting() : null);
		
		draws.add(draw);
		drawTypes.add(renderType);
		return stagedBuffer.getVertexBuilder(draw);
	}
	
	public void uploadAndDraw()
	{
		try
		{
			if(draws.isEmpty())
				return;
			
			stagedBuffer.upload();
			
			for(int i = 0; i < draws.size(); i++)
				draw(drawTypes.get(i), draws.get(i));
			
			stagedBuffer.endDraw();
			
		}finally
		{
			draws.clear();
			drawTypes.clear();
			stagedBuffer.close();
		}
	}
	
	private void draw(RenderType type, StagedVertexBuffer.Draw draw)
	{
		StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);
		
		if(info != null) {
			type.prepare().drawFromBuffer(info);
		}	
	}
}
