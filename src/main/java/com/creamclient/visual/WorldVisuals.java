package com.creamclient.visual;

import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class WorldVisuals {
    private static final List<JumpCircle> CIRCLES = new ArrayList<>();
    private static final List<TrailPoint> TRAIL = new ArrayList<>();
    private static boolean wasOnGround = true;

    private WorldVisuals() {}

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            CIRCLES.clear();
            TRAIL.clear();
            return;
        }

        if (ModuleManager.JUMP_CIRCLES.isEnabled()) {
            boolean onGround = mc.player.isOnGround();
            if (wasOnGround && !onGround && mc.player.getDeltaMovement().y > 0.05) {
                CIRCLES.add(new JumpCircle(mc.player.getX(), mc.player.getY() + 0.03, mc.player.getZ()));
            }
            wasOnGround = onGround;
        }

        if (ModuleManager.TRAILS.isEnabled()) {
            TRAIL.add(new TrailPoint(mc.player.getX(), mc.player.getY() + 0.15, mc.player.getZ()));
            long maxAge = (long) ModuleManager.TRAIL_LENGTH.getValue();
            long now = System.currentTimeMillis();
            TRAIL.removeIf(p -> now - p.time > maxAge);
        } else {
            TRAIL.clear();
        }
    }

    public static void render(MatrixStack ms, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        ms.pushPose();
        ms.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0f);

        int accent = CreamTheme.getAccent();
        float r = ((accent >> 16) & 0xFF) / 255.0f;
        float g = ((accent >> 8) & 0xFF) / 255.0f;
        float b = (accent & 0xFF) / 255.0f;
        Matrix4f mat = ms.last().pose();

        if (ModuleManager.JUMP_CIRCLES.isEnabled() && !CIRCLES.isEmpty()) {
            long now = System.currentTimeMillis();
            float maxR = ModuleManager.CIRCLE_RADIUS.getFloat();
            float lifeMs = 850.0f / ModuleManager.CIRCLE_SPEED.getFloat();
            Iterator<JumpCircle> it = CIRCLES.iterator();
            while (it.hasNext()) {
                JumpCircle c = it.next();
                float age = (now - c.time) / lifeMs;
                if (age >= 1.0f) {
                    it.remove();
                    continue;
                }
                float radius = 0.25f + age * maxR;
                float alpha = 1.0f - age;
                drawCircle(mat, c.x, c.y, c.z, radius, r, g, b, alpha);
            }
        }

        if (ModuleManager.TRAILS.isEnabled() && TRAIL.size() > 1) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            long now = System.currentTimeMillis();
            float maxAge = (float) ModuleManager.TRAIL_LENGTH.getValue();
            for (TrailPoint p : TRAIL) {
                float alpha = Math.max(0.0f, 1.0f - (now - p.time) / maxAge);
                buf.vertex(mat, (float) p.x, (float) p.y, (float) p.z).color(r, g, b, alpha).endVertex();
            }
            tess.end();
        }

        if (ModuleManager.BOX_ESP.isEnabled()) {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e == mc.player) continue;
                boolean isPlayer = e instanceof PlayerEntity;
                boolean isMob = e instanceof MonsterEntity;
                if ((isPlayer && ModuleManager.ESP_PLAYERS.isEnabled()) || (isMob && ModuleManager.ESP_MOBS.isEnabled())) {
                    double ex = e.xOld + (e.getX() - e.xOld) * partialTicks;
                    double ey = e.yOld + (e.getY() - e.yOld) * partialTicks;
                    double ez = e.zOld + (e.getZ() - e.zOld) * partialTicks;
                    AxisAlignedBB bb = e.getBoundingBox().move(ex - e.getX(), ey - e.getY(), ez - e.getZ());
                    drawBoxOutline(mat, bb, r, g, b, 0.85f);
                }
            }
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableCull();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
    }

    private static void drawCircle(Matrix4f mat, double cx, double cy, double cz, float radius, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        int segs = 44;
        for (int i = 0; i < segs; i++) {
            double ang = (Math.PI * 2.0 * i) / segs;
            float x = (float) (cx + Math.cos(ang) * radius);
            float z = (float) (cz + Math.sin(ang) * radius);
            buf.vertex(mat, x, (float) cy, z).color(r, g, b, a).endVertex();
        }
        tess.end();
    }

    private static void drawBoxOutline(Matrix4f mat, AxisAlignedBB bb, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        float x1 = (float) bb.minX, y1 = (float) bb.minY, z1 = (float) bb.minZ;
        float x2 = (float) bb.maxX, y2 = (float) bb.maxY, z2 = (float) bb.maxZ;

        line(buf, mat, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(buf, mat, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(buf, mat, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(buf, mat, x1, y1, z2, x1, y1, z1, r, g, b, a);
        line(buf, mat, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(buf, mat, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(buf, mat, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(buf, mat, x1, y2, z2, x1, y2, z1, r, g, b, a);
        line(buf, mat, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(buf, mat, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(buf, mat, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(buf, mat, x1, y1, z2, x1, y2, z2, r, g, b, a);

        tess.end();
    }

    private static void line(BufferBuilder buf, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        buf.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        buf.vertex(mat, x2, y2, z2).color(r, g, b, a).endVertex();
    }

    private static class JumpCircle {
        final double x, y, z;
        final long time = System.currentTimeMillis();
        JumpCircle(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    private static class TrailPoint {
        final double x, y, z;
        final long time = System.currentTimeMillis();
        TrailPoint(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }
}
