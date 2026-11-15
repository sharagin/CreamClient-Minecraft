package com.creamclient.visual;

import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class WorldVisuals {
    private static final List<JumpCircle> CIRCLES = new ArrayList<>();
    private static final List<TrailPoint> TRAIL = new ArrayList<>();
    private static final List<HitSpark> SPARKS = new ArrayList<>();
    private static final Random RND = new Random();
    private static boolean wasOnGround = true;

    private WorldVisuals() {}

    public static void spawnHitSparks(Entity target) {
        if (!ModuleManager.HIT_PARTICLES.isEnabled() || target == null) return;
        int count = (int) ModuleManager.HIT_COUNT.getValue();
        double cx = target.getX();
        double cy = target.getY() + target.getBbHeight() * 0.55;
        double cz = target.getZ();
        for (int i = 0; i < count; i++) {
            double vx = (RND.nextDouble() - 0.5) * 0.22;
            double vy = RND.nextDouble() * 0.16 + 0.04;
            double vz = (RND.nextDouble() - 0.5) * 0.22;
            SPARKS.add(new HitSpark(cx, cy, cz, vx, vy, vz));
        }
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            CIRCLES.clear(); TRAIL.clear(); SPARKS.clear();
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

        Iterator<HitSpark> it = SPARKS.iterator();
        long now = System.currentTimeMillis();
        while (it.hasNext()) {
            HitSpark s = it.next();
            if (now - s.time > 750L) { it.remove(); continue; }
            s.x += s.vx; s.y += s.vy; s.z += s.vz;
            s.vy -= 0.008;
        }
    }

    public static void renderBlockHighlight(MatrixStack ms, BlockPos pos) {
        if (!ModuleManager.BLOCK_OVERLAY.isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        ms.pushPose();
        ms.translate(-cam.x, -cam.y, -cam.z);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.2f);

        int accent = CreamTheme.getAccent();
        float r = ((accent >> 16) & 0xFF) / 255.0f;
        float g = ((accent >> 8) & 0xFF) / 255.0f;
        float b = (accent & 0xFF) / 255.0f;
        AxisAlignedBB bb = new AxisAlignedBB(pos).inflate(0.003);
        drawBoxOutline(ms.last().pose(), bb, r, g, b, 0.9f);

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
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

        // 1. Double JumpCircles
        if (ModuleManager.JUMP_CIRCLES.isEnabled() && !CIRCLES.isEmpty()) {
            long now = System.currentTimeMillis();
            float maxR = ModuleManager.CIRCLE_RADIUS.getFloat();
            float lifeMs = 850.0f / ModuleManager.CIRCLE_SPEED.getFloat();
            Iterator<JumpCircle> it = CIRCLES.iterator();
            while (it.hasNext()) {
                JumpCircle c = it.next();
                float age = (now - c.time) / lifeMs;
                if (age >= 1.0f) { it.remove(); continue; }
                float radius = 0.25f + age * maxR;
                float alpha = 1.0f - age;
                drawCircle(mat, c.x, c.y, c.z, radius, r, g, b, alpha);
                drawCircle(mat, c.x, c.y, c.z, radius * 0.65f, r, g, b, alpha * 0.6f);
            }
        }

        // 2. Trails
        if (ModuleManager.TRAILS.isEnabled() && TRAIL.size() > 1) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            long now = System.currentTimeMillis();
            float maxAge = (float) ModuleManager.TRAIL_LENGTH.getValue();
            int idx = 0;
            for (TrailPoint p : TRAIL) {
                int c = CreamTheme.getAccentOffset(idx++);
                float cr = ((c >> 16) & 0xFF) / 255.0f;
                float cg = ((c >> 8) & 0xFF) / 255.0f;
                float cb = (c & 0xFF) / 255.0f;
                float alpha = Math.max(0.0f, 1.0f - (now - p.time) / maxAge);
                buf.vertex(mat, (float) p.x, (float) p.y, (float) p.z).color(cr, cg, cb, alpha).endVertex();
            }
            tess.end();
        }

        // 3. ParticleAura (floating spiral fireflies around player in 3rd person)
        if (ModuleManager.PARTICLE_AURA.isEnabled() && mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            int count = (int) ModuleManager.AURA_COUNT.getValue();
            double rad = ModuleManager.AURA_RADIUS.getValue();
            double t = System.currentTimeMillis() / 900.0;
            for (int i = 0; i < count; i++) {
                double ang = t + (Math.PI * 2.0 * i) / count;
                double ox = px + Math.cos(ang) * rad;
                double oz = pz + Math.sin(ang) * rad;
                double oy = py + 0.9 + Math.sin(t * 2.0 + i) * 0.65;
                int c = CreamTheme.getAccentOffset(i);
                float cr = ((c >> 16) & 0xFF) / 255.0f;
                float cg = ((c >> 8) & 0xFF) / 255.0f;
                float cb = (c & 0xFF) / 255.0f;
                drawBoxOutline(mat, new AxisAlignedBB(ox - 0.04, oy - 0.04, oz - 0.04, ox + 0.04, oy + 0.04, oz + 0.04), cr, cg, cb, 0.85f);
            }
        }

        // 4. HitParticles
        if (!SPARKS.isEmpty()) {
            long now = System.currentTimeMillis();
            for (HitSpark s : SPARKS) {
                float a = Math.max(0.0f, 1.0f - (now - s.time) / 750.0f);
                drawBoxOutline(mat, new AxisAlignedBB(s.x - 0.04, s.y - 0.04, s.z - 0.04, s.x + 0.04, s.y + 0.04, s.z + 0.04), r, g, b, a);
            }
        }

        // 5. TargetRing
        if (ModuleManager.TARGET_ESP.isEnabled() && mc.crosshairPickEntity instanceof LivingEntity) {
            Entity tgt = mc.crosshairPickEntity;
            double tx = tgt.xOld + (tgt.getX() - tgt.xOld) * partialTicks;
            double ty = tgt.yOld + (tgt.getY() - tgt.yOld) * partialTicks;
            double tz = tgt.zOld + (tgt.getZ() - tgt.zOld) * partialTicks;
            double wave = (Math.sin(System.currentTimeMillis() / 350.0) * 0.5 + 0.5) * tgt.getBbHeight();
            drawCircle(mat, tx, ty + wave, tz, tgt.getBbWidth() * 0.85f, r, g, b, 0.95f);
        }

        // 6. ChinaHat
        if (ModuleManager.CHINA_HAT.isEnabled() && mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks + mc.player.getBbHeight() + 0.08;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawChinaHat(mat, px, py, pz, r, g, b);
        }

        // 7. BoxESP
        if (ModuleManager.BOX_ESP.isEnabled()) {
            RenderSystem.disableDepthTest();
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
            RenderSystem.enableDepthTest();
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableCull();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
    }

    private static void drawChinaHat(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, (float) cx, (float) (cy + 0.35), (float) cz).color(r, g, b, 0.72f).endVertex();
        int segs = 36;
        for (int i = 0; i <= segs; i++) {
            double ang = (Math.PI * 2.0 * i) / segs;
            float x = (float) (cx + Math.cos(ang) * 0.58);
            float z = (float) (cz + Math.sin(ang) * 0.58);
            buf.vertex(mat, x, (float) cy, z).color(r, g, b, 0.25f).endVertex();
        }
        tess.end();
        drawCircle(mat, cx, cy, cz, 0.58f, r, g, b, 0.9f);
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
        final double x, y, z; final long time = System.currentTimeMillis();
        JumpCircle(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }
    private static class TrailPoint {
        final double x, y, z; final long time = System.currentTimeMillis();
        TrailPoint(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }
    private static class HitSpark {
        double x, y, z, vx, vy, vz; final long time = System.currentTimeMillis();
        HitSpark(double x, double y, double z, double vx, double vy, double vz) {
            this.x = x; this.y = y; this.z = z; this.vx = vx; this.vy = vy; this.vz = vz;
        }
    }
}
