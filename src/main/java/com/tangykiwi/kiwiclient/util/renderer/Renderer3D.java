/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package com.tangykiwi.kiwiclient.util.renderer;

import com.tangykiwi.kiwiclient.util.CustomColor;
import com.tangykiwi.kiwiclient.util.CustomDirection;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class Renderer3D {
    public final Mesh lines = new ShaderMesh(Shaders.POS_COLOR, DrawMode.Lines, Mesh.Attrib.Vec3, Mesh.Attrib.Color);
    public final Mesh triangles = new ShaderMesh(Shaders.POS_COLOR, DrawMode.Triangles, Mesh.Attrib.Vec3, Mesh.Attrib.Color);

    public void begin() {
        lines.begin();
        triangles.begin();
    }

    public void end() {
        lines.end();
        triangles.end();
    }

    public void render(MatrixStack matrices) {
        lines.render(matrices);
        triangles.render(matrices);
    }

    // Lines

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color1, CustomColor color2) {
        lines.line(
            lines.vec3(x1, y1, z1).color(color1).next(),
            lines.vec3(x2, y2, z2).color(color2).next()
        );
    }

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color) {
        line(x1, y1, z1, x2, y2, z2, color, color);
    }

    @SuppressWarnings("Duplicates")
    public void boxLines(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color, int excludeCustomDirection) {
        int blb = lines.vec3(x1, y1, z1).color(color).next();
        int blf = lines.vec3(x1, y1, z2).color(color).next();
        int brb = lines.vec3(x2, y1, z1).color(color).next();
        int brf = lines.vec3(x2, y1, z2).color(color).next();
        int tlb = lines.vec3(x1, y2, z1).color(color).next();
        int tlf = lines.vec3(x1, y2, z2).color(color).next();
        int trb = lines.vec3(x2, y2, z1).color(color).next();
        int trf = lines.vec3(x2, y2, z2).color(color).next();

        if (excludeCustomDirection == 0) {
            // Bottom to top
            lines.line(blb, tlb);
            lines.line(blf, tlf);
            lines.line(brb, trb);
            lines.line(brf, trf);

            // Bottom loop
            lines.line(blb, blf);
            lines.line(brb, brf);
            lines.line(blb, brb);
            lines.line(blf, brf);

            // Top loop
            lines.line(tlb, tlf);
            lines.line(trb, trf);
            lines.line(tlb, trb);
            lines.line(tlf, trf);
        }
        else {
            // Bottom to top
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.WEST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.NORTH)) lines.line(blb, tlb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.WEST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.SOUTH)) lines.line(blf, tlf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.EAST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.NORTH)) lines.line(brb, trb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.EAST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.SOUTH)) lines.line(brf, trf);

            // Bottom loop
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.WEST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.DOWN)) lines.line(blb, blf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.EAST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.DOWN)) lines.line(brb, brf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.NORTH) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.DOWN)) lines.line(blb, brb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.SOUTH) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.DOWN)) lines.line(blf, brf);

            // Top loop
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.WEST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.UP)) lines.line(tlb, tlf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.EAST) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.UP)) lines.line(trb, trf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.NORTH) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.UP)) lines.line(tlb, trb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.SOUTH) && CustomDirection.isNot(excludeCustomDirection, CustomDirection.UP)) lines.line(tlf, trf);
        }

        lines.growIfNeeded();
    }

    public void blockLines(int x, int y, int z, CustomColor color, int excludeCustomDirection) {
        boxLines(x, y, z, x + 1, y + 1, z + 1, color, excludeCustomDirection);
    }

    // Quads

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, CustomColor topLeft, CustomColor topRight, CustomColor bottomRight, CustomColor bottomLeft) {
        triangles.quad(
            triangles.vec3(x1, y1, z1).color(bottomLeft).next(),
            triangles.vec3(x2, y2, z2).color(topLeft).next(),
            triangles.vec3(x3, y3, z3).color(topRight).next(),
            triangles.vec3(x4, y4, z4).color(bottomRight).next()
        );
    }

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, CustomColor color) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color, color, color, color);
    }

    public void quadVertical(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, color);
    }

    public void quadHorizontal(double x1, double y, double z1, double x2, double z2, CustomColor color) {
        quad(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, color);
    }

    public void gradientQuadVertical(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor topColor, CustomColor bottomColor) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, topColor, topColor, bottomColor, bottomColor);
    }

    // Sides

    @SuppressWarnings("Duplicates")
    public void side(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, CustomColor sideColor, CustomColor lineColor, ShapeMode mode) {
        if (mode.lines()) {
            int i1 = lines.vec3(x1, y1, z1).color(lineColor).next();
            int i2 = lines.vec3(x2, y2, z2).color(lineColor).next();
            int i3 = lines.vec3(x3, y3, z3).color(lineColor).next();
            int i4 = lines.vec3(x4, y4, z4).color(lineColor).next();

            lines.line(i1, i2);
            lines.line(i2, i3);
            lines.line(i3, i4);
            lines.line(i4, i1);
        }

        if (mode.sides()) {
            quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, sideColor);
        }
    }

    public void sideVertical(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor sideColor, CustomColor lineColor, ShapeMode mode) {
        side(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, sideColor, lineColor, mode);
    }

    public void sideHorizontal(double x1, double y, double z1, double x2, double z2, CustomColor sideColor, CustomColor lineColor, ShapeMode mode) {
        side(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, sideColor, lineColor, mode);
    }

    // Boxes

    @SuppressWarnings("Duplicates")
    public void boxSides(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color, int excludeCustomDirection) {
        int blb = triangles.vec3(x1, y1, z1).color(color).next();
        int blf = triangles.vec3(x1, y1, z2).color(color).next();
        int brb = triangles.vec3(x2, y1, z1).color(color).next();
        int brf = triangles.vec3(x2, y1, z2).color(color).next();
        int tlb = triangles.vec3(x1, y2, z1).color(color).next();
        int tlf = triangles.vec3(x1, y2, z2).color(color).next();
        int trb = triangles.vec3(x2, y2, z1).color(color).next();
        int trf = triangles.vec3(x2, y2, z2).color(color).next();

        if (excludeCustomDirection == 0) {
            // Bottom to top
            triangles.quad(blb, blf, tlf, tlb);
            triangles.quad(brb, trb, trf, brf);
            triangles.quad(blb, tlb, trb, brb);
            triangles.quad(blf, brf, trf, tlf);

            // Bottom
            triangles.quad(blb, brb, brf, blf);

            // Top
            triangles.quad(tlb, tlf, trf, trb);
        }
        else {
            // Bottom to top
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.WEST)) triangles.quad(blb, blf, tlf, tlb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.EAST)) triangles.quad(brb, trb, trf, brf);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.NORTH)) triangles.quad(blb, tlb, trb, brb);
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.SOUTH)) triangles.quad(blf, brf, trf, tlf);

            // Bottom
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.DOWN)) triangles.quad(blb, brb, brf, blf);

            // Top
            if (CustomDirection.isNot(excludeCustomDirection, CustomDirection.UP)) triangles.quad(tlb, tlf, trf, trb);
        }

        triangles.growIfNeeded();
    }

    public void blockSides(int x, int y, int z, CustomColor color, int excludeCustomDirection) {
        boxSides(x, y, z, x + 1, y + 1, z + 1, color, excludeCustomDirection);
    }

    public void box(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor sideColor, CustomColor lineColor, ShapeMode mode, int excludeCustomDirection) {
        if (mode.lines()) boxLines(x1, y1, z1, x2, y2, z2, lineColor, excludeCustomDirection);
        if (mode.sides()) boxSides(x1, y1, z1, x2, y2, z2, sideColor, excludeCustomDirection);
    }

    public void box(BlockPos pos, CustomColor sideColor, CustomColor lineColor, ShapeMode mode, int excludeCustomDirection) {
        if (mode.lines()) boxLines(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, lineColor, excludeCustomDirection);
        if (mode.sides()) boxSides(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, sideColor, excludeCustomDirection);
    }
}
