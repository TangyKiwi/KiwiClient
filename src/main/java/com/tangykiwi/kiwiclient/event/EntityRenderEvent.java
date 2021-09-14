package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRenderEvent extends Event {

    public static class Single extends EntityRenderEvent {

        protected Entity entity;
        protected MatrixStack matrices;
        protected VertexConsumerProvider vertex;

        public Entity getEntity() {
            return entity;
        }

        public MatrixStack getMatrix() {
            return matrices;
        }

        public VertexConsumerProvider getVertex() {
            return vertex;
        }

        public static class Pre extends Single {

            public Pre(Entity entity, MatrixStack matrices, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrices = matrices;
                this.vertex = vertex;
            }

            public void setMatrix(MatrixStack matrices) {
                this.matrices = matrices;
            }

            public void setVertex(VertexConsumerProvider vertex) {
                this.vertex = vertex;
            }

            public void setEntity(Entity entity) {
                this.entity = entity;
            }
        }

        public static class Post extends Single {

            public Post(Entity entity, MatrixStack matrices, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrices = matrices;
                this.vertex = vertex;
            }
        }

        public static class Label extends Single {

            public Label(Entity entity, MatrixStack matrices, VertexConsumerProvider vertex) {
                this.entity = entity;
                this.matrices = matrices;
                this.vertex = vertex;
            }

            public void setMatrix(MatrixStack matrices) {
                this.matrices = matrices;
            }

            public void setVertex(VertexConsumerProvider vertex) {
                this.vertex = vertex;
            }
        }
    }

    public static class PreAll extends EntityRenderEvent {
    }

    public static class PostAll extends EntityRenderEvent {
    }
}
