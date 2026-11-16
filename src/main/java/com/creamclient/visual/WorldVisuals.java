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
import net.minecraft.entity.item.ItemEntity;
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
    private static final List<JumpCircle> STEP_RINGS = new ArrayList<>();
    private static final List<TrailPoint> TRAIL = new ArrayList<>();
    private static final List<Vector3d> BREADCRUMB_LIST = new ArrayList<>();
    private static final List<HitSpark> SPARKS = new ArrayList<>();
    private static final Random RND = new Random();
    private static boolean wasOnGround = true;
    private static double lastStepX = 0, lastStepZ = 0;

    private WorldVisuals() {}

    public static void spawnHitSparks(Entity target) {
        if (!ModuleManager.HIT_PARTICLES.isEnabled() || target == null) return;
        int count = (int) ModuleManager.HIT_COUNT.getValue();
        double cx = target.getX(), cy = target.getY() + target.getBbHeight() * 0.55, cz = target.getZ();
        for (int i = 0; i < count; i++) {
            SPARKS.add(new HitSpark(cx, cy, cz, (RND.nextDouble() - 0.5) * 0.22, RND.nextDouble() * 0.16 + 0.04, (RND.nextDouble() - 0.5) * 0.22));
        }
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            CIRCLES.clear(); STEP_RINGS.clear(); TRAIL.clear(); BREADCRUMB_LIST.clear(); SPARKS.clear();
            return;
        }

        if (ModuleManager.JUMP_CIRCLES.isEnabled()) {
            boolean onGround = mc.player.isOnGround();
            if (wasOnGround && !onGround && mc.player.getDeltaMovement().y > 0.05) {
                CIRCLES.add(new JumpCircle(mc.player.getX(), mc.player.getY() + 0.03, mc.player.getZ()));
            }
            wasOnGround = onGround;
        }

        if (ModuleManager.STEP_CIRCLES.isEnabled() && mc.player.isOnGround()) {
            double dx = mc.player.getX() - lastStepX, dz = mc.player.getZ() - lastStepZ;
            if (dx * dx + dz * dz > 1.2) {
                lastStepX = mc.player.getX(); lastStepZ = mc.player.getZ();
                STEP_RINGS.add(new JumpCircle(lastStepX, mc.player.getY() + 0.02, lastStepZ));
            }
        }

        if (ModuleManager.TRAILS.isEnabled()) {
            TRAIL.add(new TrailPoint(mc.player.getX(), mc.player.getY() + 0.15, mc.player.getZ()));
            long maxAge = (long) ModuleManager.TRAIL_LENGTH.getValue();
            long now = System.currentTimeMillis();
            TRAIL.removeIf(p -> now - p.time > maxAge);
        } else TRAIL.clear();

        if (ModuleManager.BREADCRUMBS.isEnabled()) {
            Vector3d cur = new Vector3d(mc.player.getX(), mc.player.getY() + 0.1, mc.player.getZ());
            if (BREADCRUMB_LIST.isEmpty() || BREADCRUMB_LIST.get(BREADCRUMB_LIST.size() - 1).distanceToSqr(cur) > 0.08) {
                BREADCRUMB_LIST.add(cur);
            }
            int max = (int) ModuleManager.BREADCRUMB_MAX.getValue();
            while (BREADCRUMB_LIST.size() > max) BREADCRUMB_LIST.remove(0);
        } else BREADCRUMB_LIST.clear();

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
        RenderSystem.lineWidth(ModuleManager.BLOCK_LINE.getFloat());

        int accent = CreamTheme.getAccent();
        float r = ((accent >> 16) & 0xFF) / 255.0f;
        float g = ((accent >> 8) & 0xFF) / 255.0f;
        float b = (accent & 0xFF) / 255.0f;
        drawBoxOutline(ms.last().pose(), new AxisAlignedBB(pos).inflate(0.003), r, g, b, 0.92f);

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
        float r = ((accent >> 16) & 0xFF) / 255.0f, g = ((accent >> 8) & 0xFF) / 255.0f, b = (accent & 0xFF) / 255.0f;
        Matrix4f mat = ms.last().pose();

        // 1. JumpCircles
        if (ModuleManager.JUMP_CIRCLES.isEnabled() && !CIRCLES.isEmpty()) {
            long now = System.currentTimeMillis();
            float maxR = ModuleManager.CIRCLE_RADIUS.getFloat();
            float lifeMs = 850.0f / ModuleManager.CIRCLE_SPEED.getFloat();
            int rings = (int) ModuleManager.CIRCLE_RINGS.getValue();
            Iterator<JumpCircle> it = CIRCLES.iterator();
            while (it.hasNext()) {
                JumpCircle c = it.next();
                float age = (now - c.time) / lifeMs;
                if (age >= 1.0f) { it.remove(); continue; }
                float radius = 0.25f + age * maxR, alpha = 1.0f - age;
                for (int k = 0; k < rings; k++) {
                    float scale = 1.0f - k * 0.25f;
                    drawCircle(mat, c.x, c.y, c.z, radius * scale, r, g, b, alpha * scale);
                }
            }
        }

        // 2. StepCircles
        if (!STEP_RINGS.isEmpty()) {
            long now = System.currentTimeMillis();
            Iterator<JumpCircle> it = STEP_RINGS.iterator();
            while (it.hasNext()) {
                JumpCircle c = it.next();
                float age = (now - c.time) / 550.0f;
                if (age >= 1.0f) { it.remove(); continue; }
                drawCircle(mat, c.x, c.y, c.z, 0.15f + age * 0.55f, r, g, b, (1.0f - age) * 0.7f);
            }
        }

        // 3. Trails
        if (ModuleManager.TRAILS.isEnabled() && TRAIL.size() > 1) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            long now = System.currentTimeMillis();
            float maxAge = (float) ModuleManager.TRAIL_LENGTH.getValue();
            int idx = 0;
            for (TrailPoint p : TRAIL) {
                int c = CreamTheme.getAccentOffset(idx++);
                buf.vertex(mat, (float) p.x, (float) p.y, (float) p.z)
                        .color(((c >> 16) & 0xFF) / 255.0f, ((c >> 8) & 0xFF) / 255.0f, (c & 0xFF) / 255.0f, Math.max(0.0f, 1.0f - (now - p.time) / maxAge))
                        .endVertex();
            }
            tess.end();
        }

        // 4. Breadcrumbs
        if (ModuleManager.BREADCRUMBS.isEnabled() && BREADCRUMB_LIST.size() > 1) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (Vector3d v : BREADCRUMB_LIST) buf.vertex(mat, (float) v.x, (float) v.y, (float) v.z).color(r, g, b, 0.75f).endVertex();
            tess.end();
        }

        // 5. ParticleAura
        if (ModuleManager.PARTICLE_AURA.isEnabled() && mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            int count = (int) ModuleManager.AURA_COUNT.getValue();
            double rad = ModuleManager.AURA_RADIUS.getValue();
            double t = System.currentTimeMillis() / 900.0;
            for (int i = 0; i < count; i++) {
                double ang = t + (Math.PI * 2.0 * i) / count;
                double ox = px + Math.cos(ang) * rad, oz = pz + Math.sin(ang) * rad, oy = py + 0.9 + Math.sin(t * 2.0 + i) * 0.65;
                int c = CreamTheme.getAccentOffset(i);
                drawBoxOutline(mat, new AxisAlignedBB(ox - 0.04, oy - 0.04, oz - 0.04, ox + 0.04, oy + 0.04, oz + 0.04),
                        ((c >> 16) & 0xFF) / 255.0f, ((c >> 8) & 0xFF) / 255.0f, (c & 0xFF) / 255.0f, 0.85f);
            }
        }

        // 6. HitParticles
        if (!SPARKS.isEmpty()) {
            long now = System.currentTimeMillis();
            for (HitSpark s : SPARKS) {
                float a = Math.max(0.0f, 1.0f - (now - s.time) / 750.0f);
                drawBoxOutline(mat, new AxisAlignedBB(s.x - 0.04, s.y - 0.04, s.z - 0.04, s.x + 0.04, s.y + 0.04, s.z + 0.04), r, g, b, a);
            }
        }

        // 7. TargetRing
        if (ModuleManager.TARGET_ESP.isEnabled() && mc.crosshairPickEntity instanceof LivingEntity) {
            Entity tgt = mc.crosshairPickEntity;
            double tx = tgt.xOld + (tgt.getX() - tgt.xOld) * partialTicks;
            double ty = tgt.yOld + (tgt.getY() - tgt.yOld) * partialTicks;
            double tz = tgt.zOld + (tgt.getZ() - tgt.zOld) * partialTicks;
            double wave = (Math.sin(System.currentTimeMillis() / 350.0) * 0.5 + 0.5) * tgt.getBbHeight();
            drawCircle(mat, tx, ty + wave, tz, tgt.getBbWidth() * 0.85f, r, g, b, 0.95f);
        }

        // 8. ChinaHat, Halo, Wings, OrbitalRings (3rd person)
        if (mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            if (ModuleManager.CHINA_HAT.isEnabled()) drawChinaHat(mat, px, py + mc.player.getBbHeight() + 0.08, pz, r, g, b);
            if (ModuleManager.HALO.isEnabled()) {
                double hy = py + mc.player.getBbHeight() + 0.28 + Math.sin(System.currentTimeMillis() / 400.0) * 0.03;
                drawCircle(mat, px, hy, pz, 0.42f, r, g, b, 0.95f);
                drawCircle(mat, px, hy + 0.02, pz, 0.36f, r, g, b, 0.65f);
            }
            if (ModuleManager.WINGS.isEnabled()) drawWings(mat, px, py + 1.15, pz, mc.player.yRot, r, g, b);
            if (ModuleManager.ORBITAL_RINGS.isEnabled()) drawOrbitalRings(mat, px, py + 1.0, pz, r, g, b);
        }

        // 9. BoxESP, ItemESP & Tracers
        if (ModuleManager.BOX_ESP.isEnabled() || ModuleManager.ITEM_ESP.isEnabled() || ModuleManager.TRACERS.isEnabled()) {
            RenderSystem.disableDepthTest();
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks + mc.player.getEyeHeight();
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;

            for (Entity e : mc.level.entitiesForRendering()) {
                if (e == mc.player) continue;
                double ex = e.xOld + (e.getX() - e.xOld) * partialTicks;
                double ey = e.yOld + (e.getY() - e.yOld) * partialTicks;
                double ez = e.zOld + (e.getZ() - e.zOld) * partialTicks;

                if (ModuleManager.BOX_ESP.isEnabled()) {
                    boolean isP = e instanceof PlayerEntity, isM = e instanceof MonsterEntity;
                    if ((isP && ModuleManager.ESP_PLAYERS.isEnabled()) || (isM && ModuleManager.ESP_MOBS.isEnabled())) {
                        drawBoxOutline(mat, e.getBoundingBox().move(ex - e.getX(), ey - e.getY(), ez - e.getZ()), r, g, b, 0.85f);
                    }
                }
                if (ModuleManager.ITEM_ESP.isEnabled() && e instanceof ItemEntity) {
                    drawBoxOutline(mat, e.getBoundingBox().move(ex - e.getX(), ey - e.getY(), ez - e.getZ()), 1.0f, 0.9f, 0.2f, 0.85f);
                }
                if (ModuleManager.TRACERS.isEnabled() && e instanceof PlayerEntity) {
                    Tessellator tess = Tessellator.getInstance();
                    BufferBuilder buf = tess.getBuilder();
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, (float) px, (float) py, (float) pz, (float) ex, (float) (ey + e.getBbHeight() / 2.0), (float) ez, r, g, b, 0.7f);
                    tess.end();
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

    private static void drawOrbitalRings(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        double t = System.currentTimeMillis() / 700.0;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        for (int ring = 0; ring < 3; ring++) {
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            double tilt = (ring - 1) * 0.55;
            double rot = t + ring * 1.1;
            for (int i = 0; i < 36; i++) {
                double ang = (Math.PI * 2.0 * i) / 36;
                double lx = Math.cos(ang) * 1.05;
                double lz = Math.sin(ang) * 1.05;
                double ly = Math.sin(ang + rot) * tilt;
                buf.vertex(mat, (float) (cx + lx), (float) (cy + ly), (float) (cz + lz)).color(r, g, b, 0.75f).endVertex();
            }
            tess.end();
        }
    }

    private static void drawWings(Matrix4f mat, double cx, double cy, double cz, float yawDeg, float r, float g, float b) {
        double yawRad = Math.toRadians(yawDeg);
        double backX = Math.sin(yawRad) * 0.22, backZ = -Math.cos(yawRad) * 0.22;
        double rightX = Math.cos(yawRad), rightZ = Math.sin(yawRad);
        double flap = Math.sin(System.currentTimeMillis() / 280.0) * 0.14;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int side = -1; side <= 1; side += 2) {
            double bx = cx + backX, by = cy, bz = cz + backZ;
            double tx = bx + rightX * side * (0.95 + flap) + backX * 1.6, ty = by + 0.45 + flap, tz = bz + rightZ * side * (0.95 + flap) + backZ * 1.6;
            double lx = bx + rightX * side * 0.55 + backX * 1.2, ly = by - 0.35, lz = bz + rightZ * side * 0.55 + backZ * 1.2;
            line(buf, mat, (float) bx, (float) by, (float) bz, (float) tx, (float) ty, (float) tz, r, g, b, 0.9f);
            line(buf, mat, (float) tx, (float) ty, (float) tz, (float) lx, (float) ly, (float) lz, r, g, b, 0.7f);
            line(buf, mat, (float) lx, (float) ly, (float) lz, (float) bx, (float) by, (float) bz, r, g, b, 0.9f);
        }
        tess.end();
    }

    private static void drawChinaHat(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        float h = ModuleManager.HAT_HEIGHT.getFloat(), rad = ModuleManager.HAT_RADIUS.getFloat();
        double spin = ModuleManager.HAT_SPIN.isEnabled() ? (System.currentTimeMillis() / 600.0) : 0.0;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, (float) cx, (float) (cy + h), (float) cz).color(r, g, b, 0.72f).endVertex();
        for (int i = 0; i <= 36; i++) {
            double ang = spin + (Math.PI * 2.0 * i) / 36;
            buf.vertex(mat, (float) (cx + Math.cos(ang) * rad), (float) cy, (float) (cz + Math.sin(ang) * rad)).color(r, g, b, 0.25f).endVertex();
        }
        tess.end();
        drawCircle(mat, cx, cy, cz, rad, r, g, b, 0.9f);
    }

    private static void drawCircle(Matrix4f mat, double cx, double cy, double cz, float radius, float r, float g, float b, float a) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 44; i++) {
            double ang = (Math.PI * 2.0 * i) / 44;
            buf.vertex(mat, (float) (cx + Math.cos(ang) * radius), (float) cy, (float) (cz + Math.sin(ang) * radius)).color(r, g, b, a).endVertex();
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

    private static class JumpCircle { final double x, y, z; final long time = System.currentTimeMillis(); JumpCircle(double x, double y, double z) { this.x = x; this.y = y; this.z = z; } }
    private static class TrailPoint { final double x, y, z; final long time = System.currentTimeMillis(); TrailPoint(double x, double y, double z) { this.x = x; this.y = y; this.z = z; } }
    private static class HitSpark { double x, y, z, vx, vy, vz; final long time = System.currentTimeMillis(); HitSpark(double x, double y, double z, double vx, double vy, double vz) { this.x = x; this.y = y; this.z = z; this.vx = vx; this.vy = vy; this.vz = vz; } }
}
