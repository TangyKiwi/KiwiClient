package com.tangykiwi.kiwiclient.util.render.shader;

import com.tangykiwi.kiwiclient.mixin.WorldRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;

public class OutlineShaderManager {

	public static void loadShader(ShaderEffect shader) {
		if (getCurrentShader() != null) {
			getCurrentShader().close();
		}

		((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).setEntityOutlineShader(shader);
		((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).setEntityOutlinesFramebuffer(shader.getSecondaryTarget("final"));
	}

	public static void loadDefaultShader() {
		MinecraftClient.getInstance().worldRenderer.loadEntityOutlineShader();
	}

	public static ShaderEffect getCurrentShader() {
		return ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getEntityOutlineShader();
	}
}
