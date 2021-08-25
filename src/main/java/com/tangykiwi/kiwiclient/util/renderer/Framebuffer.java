/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package com.tangykiwi.kiwiclient.util.renderer;

import com.tangykiwi.kiwiclient.util.Utils;

import static org.lwjgl.opengl.GL32C.*;

public class Framebuffer {
    private int id;
    public int texture;

    public Framebuffer() {
        init();
    }

    private void init() {
        id = GL.genFramebuffer();
        bind();

        texture = GL.genTexture();
        GL.bindTexture(texture);
        GL.defaultPixelStore();

        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL.textureParam(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GL.textureImage2D(GL_TEXTURE_2D, 0, GL_RGB, Utils.mc.getWindow().getFramebufferWidth(), Utils.mc.getWindow().getFramebufferHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, null);
        GL.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        unbind();
    }

    public void bind() {
        GL.bindFramebuffer(id);
    }

    public void unbind() {
        Utils.mc.getFramebuffer().beginWrite(false);
    }

    public void resize() {
        GL.deleteFramebuffer(id);
        GL.deleteTexture(texture);

        init();
    }
}
