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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.item.ItemEntity;
import java.util.HashMap;
import java.util.Map;
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
    private static final List<HitBubble> HIT_BUBBLES_LIST = new ArrayList<>();
    private static final List<DmgPopup> DMG_POPUPS = new ArrayList<>();
    private static final Map<BlockPos, Long> PLACED_BLOCKS = new HashMap<>();
    public static final Map<Integer, List<Trail2DPoint>> PROJ_2D_TRAILS = new HashMap<>();
    private static long rainStartTime = 0L;
    private static float puddleAlpha = 0.0f;
    private static final Random RND = new Random();
    private static boolean wasOnGround = true;
    private static double lastStepX = 0, lastStepZ = 0;

    private WorldVisuals() {}

    public static void onBlockPlace(BlockPos pos) {
        if (pos != null) PLACED_BLOCKS.put(pos.immutable(), System.currentTimeMillis());
    }

    public static void spawnHitSparks(Entity target) {
        if (target == null) return;
        double hcx = target.getX(), hcy = target.getY() + target.getBbHeight() * 0.6, hcz = target.getZ();
        if (ModuleManager.HIT_BUBBLES != null && ModuleManager.HIT_BUBBLES.isEnabled()) {
            HIT_BUBBLES_LIST.add(new HitBubble(hcx, hcy, hcz));
        }
        if (ModuleManager.DAMAGE_NUMBERS != null && ModuleManager.DAMAGE_NUMBERS.isEnabled()) {
            DMG_POPUPS.add(new DmgPopup(hcx + (RND.nextDouble() - 0.5) * 0.4, hcy + 0.4, hcz + (RND.nextDouble() - 0.5) * 0.4, "HIT!"));
        }
        if (!ModuleManager.HIT_PARTICLES.isEnabled()) return;
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
            if (BREADCRUMB_LIST.isEmpty() || BREADCRUMB_LIST.get(BREADCRUMB_LIST.size() - 1).distanceToSqr(cur) > 0.08) BREADCRUMB_LIST.add(cur);
            int max = (int) ModuleManager.BREADCRUMB_MAX.getValue();
            while (BREADCRUMB_LIST.size() > max) BREADCRUMB_LIST.remove(0);
        } else BREADCRUMB_LIST.clear();

        long now = System.currentTimeMillis();
        // RainPuddle 5s timer
        if (ModuleManager.RAIN_PUDDLE != null && ModuleManager.RAIN_PUDDLE.isEnabled()
                && mc.level.isRaining() && mc.level.canSeeSky(mc.player.blockPosition())) {
            if (rainStartTime == 0L) rainStartTime = now;
            if (now - rainStartTime >= 5000L) puddleAlpha = Math.min(0.55f, puddleAlpha + 0.02f);
        } else {
            rainStartTime = 0L;
            puddleAlpha = Math.max(0.0f, puddleAlpha - 0.03f);
        }

        // Projectile2DTrail 200ms buffer
        if (ModuleManager.PROJECTILE_2D_TRAIL != null && ModuleManager.PROJECTILE_2D_TRAIL.isEnabled()) {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof ProjectileEntity || (e instanceof ItemEntity && e.tickCount < 60)) {
                    List<Trail2DPoint> pts = PROJ_2D_TRAILS.computeIfAbsent(e.getId(), k -> new ArrayList<>());
                    pts.add(new Trail2DPoint(e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), now));
                    pts.removeIf(p -> now - p.time > 200L);
                }
            }
            PROJ_2D_TRAILS.entrySet().removeIf(en -> {
                en.getValue().removeIf(p -> now - p.time > 200L);
                return en.getValue().isEmpty();
            });
        } else {
            PROJ_2D_TRAILS.clear();
        }

        Iterator<HitSpark> it = SPARKS.iterator();
        while (it.hasNext()) {
            HitSpark s = it.next();
            if (now - s.time > 750L) { it.remove(); continue; }
            s.x += s.vx; s.y += s.vy; s.z += s.vz; s.vy -= 0.008;
        }
        HIT_BUBBLES_LIST.removeIf(b -> now - b.time > 650L);
        DMG_POPUPS.removeIf(d -> now - d.time > 900L);
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
        drawBoxOutline(ms.last().pose(), new AxisAlignedBB(pos).inflate(0.003),
                ((accent >> 16) & 0xFF) / 255.0f, ((accent >> 8) & 0xFF) / 255.0f, (accent & 0xFF) / 255.0f, 0.92f);

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
    }

    public static void render(MatrixStack ms, float partialTicks) {
        SkyRenderer.renderCustomSky(ms);

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

        if (ModuleManager.JUMP_CIRCLES.isEnabled() && !CIRCLES.isEmpty()) {
            long now = System.currentTimeMillis();
            float maxR = ModuleManager.CIRCLE_RADIUS.getFloat(), lifeMs = 850.0f / ModuleManager.CIRCLE_SPEED.getFloat();
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

        if (ModuleManager.BREADCRUMBS.isEnabled() && BREADCRUMB_LIST.size() > 1) {
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (Vector3d v : BREADCRUMB_LIST) buf.vertex(mat, (float) v.x, (float) v.y, (float) v.z).color(r, g, b, 0.75f).endVertex();
            tess.end();
        }

        if (ModuleManager.PARTICLE_AURA.isEnabled() && mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            int count = (int) ModuleManager.AURA_COUNT.getValue();
            double rad = ModuleManager.AURA_RADIUS.getValue(), t = System.currentTimeMillis() / 900.0;
            for (int i = 0; i < count; i++) {
                double ang = t + (Math.PI * 2.0 * i) / count;
                double ox = px + Math.cos(ang) * rad, oz = pz + Math.sin(ang) * rad, oy = py + 0.9 + Math.sin(t * 2.0 + i) * 0.65;
                int c = CreamTheme.getAccentOffset(i);
                drawBoxOutline(mat, new AxisAlignedBB(ox - 0.04, oy - 0.04, oz - 0.04, ox + 0.04, oy + 0.04, oz + 0.04),
                        ((c >> 16) & 0xFF) / 255.0f, ((c >> 8) & 0xFF) / 255.0f, (c & 0xFF) / 255.0f, 0.85f);
            }
        }

        if (!SPARKS.isEmpty()) {
            long now = System.currentTimeMillis();
            for (HitSpark s : SPARKS) {
                float a = Math.max(0.0f, 1.0f - (now - s.time) / 750.0f);
                drawBoxOutline(mat, new AxisAlignedBB(s.x - 0.04, s.y - 0.04, s.z - 0.04, s.x + 0.04, s.y + 0.04, s.z + 0.04), r, g, b, a);
            }
        }

        if (mc.crosshairPickEntity instanceof LivingEntity) {
            Entity tgt = mc.crosshairPickEntity;
            double tx = tgt.xOld + (tgt.getX() - tgt.xOld) * partialTicks;
            double ty = tgt.yOld + (tgt.getY() - tgt.yOld) * partialTicks;
            double tz = tgt.zOld + (tgt.getZ() - tgt.zOld) * partialTicks;
            if (ModuleManager.TARGET_ESP.isEnabled()) {
                double wave = (Math.sin(System.currentTimeMillis() / 350.0) * 0.5 + 0.5) * tgt.getBbHeight();
                drawCircle(mat, tx, ty + wave, tz, tgt.getBbWidth() * 0.85f, r, g, b, 0.95f);
            }
            if (ModuleManager.TARGET_CROWN.isEnabled()) {
                drawCrown(mat, tx, ty + tgt.getBbHeight() + 0.15, tz, r, g, b);
            }
        }

        if (mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            if (ModuleManager.PLAYER_SHADOW.isEnabled()) {
                drawCircle(mat, px, py + 0.03, pz, 0.65f, r, g, b, 0.6f);
                drawCircle(mat, px, py + 0.03, pz, 0.45f, r, g, b, 0.9f);
            }
            if (ModuleManager.CHINA_HAT.isEnabled()) drawChinaHat(mat, px, py + mc.player.getBbHeight() + 0.08, pz, r, g, b);
            if (ModuleManager.HALO.isEnabled()) {
                double hy = py + mc.player.getBbHeight() + 0.28 + Math.sin(System.currentTimeMillis() / 400.0) * 0.03;
                drawCircle(mat, px, hy, pz, 0.42f, r, g, b, 0.95f);
                drawCircle(mat, px, hy + 0.02, pz, 0.36f, r, g, b, 0.65f);
            }
            if (ModuleManager.WINGS.isEnabled()) drawWings(mat, px, py + 1.15, pz, mc.player.yRot, r, g, b);
            if (ModuleManager.ORBITAL_RINGS.isEnabled()) drawOrbitalRings(mat, px, py + 1.0, pz, r, g, b);
            if (ModuleManager.CRYSTAL_ORBIT.isEnabled()) drawCrystalOrbit(mat, px, py + 1.05, pz, r, g, b);
        }

        if (ModuleManager.BOX_ESP.isEnabled() || ModuleManager.TRACERS.isEnabled()) {
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

        if (ModuleManager.WORLD_GRID.isEnabled() && mc.player != null) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawWorldGrid(mat, px, Math.floor(py) + 0.02, pz, r, g, b);
        }

        if (ModuleManager.PULSE_RINGS.isEnabled() && mc.player != null) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawPulseRings(mat, px, py + 0.05, pz, r, g, b);
        }

        // BlockBreathe: 2s pulsing outline on newly placed blocks
        if (ModuleManager.BLOCK_BREATHE != null && ModuleManager.BLOCK_BREATHE.isEnabled() && !PLACED_BLOCKS.isEmpty()) {
            long nowBp = System.currentTimeMillis();
            PLACED_BLOCKS.entrySet().removeIf(en -> nowBp - en.getValue() > 2000L);
            for (Map.Entry<BlockPos, Long> en : PLACED_BLOCKS.entrySet()) {
                float age = (nowBp - en.getValue()) / 2000.0f;
                float pulse = (float) Math.abs(Math.sin(age * Math.PI * 2.5));
                float a = pulse * (1.0f - age) * 0.95f;
                drawBoxOutline(mat, new AxisAlignedBB(en.getKey()).inflate(0.004 + pulse * 0.015), r, g, b, a);
            }
        }

        // RainPuddle: sky-reflection circle under player feet
        if (puddleAlpha > 0.01f && mc.player != null) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            float shimmer = 0.85f + 0.15f * (float) Math.sin(System.currentTimeMillis() / 320.0);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            buf.vertex(mat, (float) px, (float) (py + 0.02), (float) pz).color(0.55f, 0.78f, 0.98f, puddleAlpha * shimmer).endVertex();
            for (int i = 0; i <= 28; i++) {
                double ang = i * Math.PI * 2.0 / 28.0;
                buf.vertex(mat, (float) (px + Math.cos(ang) * 0.95), (float) (py + 0.02), (float) (pz + Math.sin(ang) * 0.95)).color(0.35f, 0.58f, 0.85f, puddleAlpha * 0.25f).endVertex();
            }
            tess.end();
        }

        if (ModuleManager.HIT_BUBBLES != null && ModuleManager.HIT_BUBBLES.isEnabled() && !HIT_BUBBLES_LIST.isEmpty()) {
            long nowB = System.currentTimeMillis();
            for (HitBubble hb : HIT_BUBBLES_LIST) {
                float p = (nowB - hb.time) / 650.0f;
                if (p < 0f || p > 1f) continue;
                float rad = 0.2f + p * 1.35f;
                float a = (1.0f - p) * 0.85f;
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buf = tess.getBuilder();
                buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                for (int i = 0; i <= 32; i++) {
                    double ang = i * Math.PI * 2.0 / 32.0;
                    buf.vertex(mat, (float) (hb.x + Math.cos(ang) * rad), (float) hb.y, (float) (hb.z + Math.sin(ang) * rad)).color(r, g, b, a).endVertex();
                }
                tess.end();
            }
        }

        if (ModuleManager.DAMAGE_NUMBERS != null && ModuleManager.DAMAGE_NUMBERS.isEnabled() && !DMG_POPUPS.isEmpty()) {
            long nowD = System.currentTimeMillis();
            for (DmgPopup dp : DMG_POPUPS) {
                float p = (nowD - dp.time) / 900.0f;
                if (p < 0f || p > 1f) continue;
                ms.pushPose();
                ms.translate(dp.x, dp.y + p * 0.8, dp.z);
                ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                ms.scale(-0.03f, -0.03f, 0.03f);
                RenderSystem.enableTexture();
                mc.font.draw(ms, dp.text, -mc.font.width(dp.text) / 2f, 0, accent);
                RenderSystem.disableTexture();
                ms.popPose();
            }
        }

        if (ModuleManager.PROJECTILE_TRAILS != null && ModuleManager.PROJECTILE_TRAILS.isEnabled()) {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof ProjectileEntity) {
                    double ex = e.xOld + (e.getX() - e.xOld) * partialTicks;
                    double ey = e.yOld + (e.getY() - e.yOld) * partialTicks;
                    double ez = e.zOld + (e.getZ() - e.zOld) * partialTicks;
                    Tessellator tess = Tessellator.getInstance();
                    BufferBuilder buf = tess.getBuilder();
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, (float) e.xOld, (float) e.yOld, (float) e.zOld, (float) ex, (float) ey, (float) ez, r, g, b, 0.9f);
                    tess.end();
                }
            }
        }

        if (ModuleManager.ENTITY_HALO != null && ModuleManager.ENTITY_HALO.isEnabled()) {
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e != mc.player && e instanceof LivingEntity) {
                    double ex = e.xOld + (e.getX() - e.xOld) * partialTicks;
                    double ey = e.yOld + (e.getY() - e.yOld) * partialTicks;
                    double ez = e.zOld + (e.getZ() - e.zOld) * partialTicks;
                    Tessellator tess = Tessellator.getInstance();
                    BufferBuilder buf = tess.getBuilder();
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 24; i++) {
                        double ang = i * Math.PI * 2.0 / 24.0;
                        buf.vertex(mat, (float) (ex + Math.cos(ang) * 0.55), (float) (ey + 0.03), (float) (ez + Math.sin(ang) * 0.55)).color(r, g, b, 0.65f).endVertex();
                    }
                    tess.end();
                }
            }
        }

        if (ModuleManager.STAR_FIELD_3D != null && ModuleManager.STAR_FIELD_3D.isEnabled() && mc.player != null) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawStarField3D(mat, px, py + 1.2, pz, r, g, b);
        }

        if (ModuleManager.FOOTSTEP_DUST != null && ModuleManager.FOOTSTEP_DUST.isEnabled() && mc.player != null && (mc.player.xo != mc.player.getX() || mc.player.zo != mc.player.getZ())) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawFootstepGlow(mat, px, py + 0.04, pz, r, g, b);
        }

        if (mc.player != null && mc.options.getCameraType() != PointOfView.FIRST_PERSON) {
            double px = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * partialTicks;
            double py = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * partialTicks;
            double pz = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * partialTicks;
            drawCosmetics(mat, px, py, pz, mc.player.yRot);
        }
        if (ModuleManager.WAYPOINTS.isEnabled() && mc.player != null) {
            drawWaypoints(ms, mat, mc, partialTicks);
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableCull();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        ms.popPose();
    }

    private static void drawCrown(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        double spin = System.currentTimeMillis() / 500.0;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 10; i++) {
            double ang = spin + (Math.PI * 2.0 * i) / 10;
            double h = (i % 2 == 0) ? 0.22 : 0.04;
            buf.vertex(mat, (float) (cx + Math.cos(ang) * 0.32), (float) (cy + h), (float) (cz + Math.sin(ang) * 0.32)).color(r, g, b, 0.95f).endVertex();
        }
        tess.end();
    }

    private static void drawCrystalOrbit(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        double t = System.currentTimeMillis() / 800.0;
        for (int i = 0; i < 4; i++) {
            double ang = t + (Math.PI / 2.0) * i;
            double ox = cx + Math.cos(ang) * 0.95, oz = cz + Math.sin(ang) * 0.95, oy = cy + Math.sin(t * 2.0 + i) * 0.25;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            float s = 0.08f;
            line(buf, mat, (float) ox, (float) (oy + s), (float) oz, (float) (ox + s), (float) oy, (float) oz, r, g, b, 0.9f);
            line(buf, mat, (float) ox, (float) (oy + s), (float) oz, (float) (ox - s), (float) oy, (float) oz, r, g, b, 0.9f);
            line(buf, mat, (float) ox, (float) (oy - s), (float) oz, (float) (ox + s), (float) oy, (float) oz, r, g, b, 0.9f);
            line(buf, mat, (float) ox, (float) (oy - s), (float) oz, (float) (ox - s), (float) oy, (float) oz, r, g, b, 0.9f);
            tess.end();
        }
    }

    private static void drawOrbitalRings(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        double t = System.currentTimeMillis() / 700.0;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        for (int ring = 0; ring < 3; ring++) {
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            double tilt = (ring - 1) * 0.55, rot = t + ring * 1.1;
            for (int i = 0; i < 36; i++) {
                double ang = (Math.PI * 2.0 * i) / 36;
                buf.vertex(mat, (float) (cx + Math.cos(ang) * 1.05), (float) (cy + Math.sin(ang + rot) * tilt), (float) (cz + Math.sin(ang) * 1.05)).color(r, g, b, 0.75f).endVertex();
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
    private static class HitBubble { final double x, y, z; final long time = System.currentTimeMillis(); HitBubble(double x, double y, double z) { this.x = x; this.y = y; this.z = z; } }
    private static class DmgPopup { final double x, y, z; final String text; final long time = System.currentTimeMillis(); DmgPopup(double x, double y, double z, String text) { this.x = x; this.y = y; this.z = z; this.text = text; } }
    public static class Trail2DPoint { public final double x, y, z; public final long time; public Trail2DPoint(double x, double y, double z, long time) { this.x = x; this.y = y; this.z = z; this.time = time; } }
    private static class HitSpark { double x, y, z, vx, vy, vz; final long time = System.currentTimeMillis(); HitSpark(double x, double y, double z, double vx, double vy, double vz) { this.x = x; this.y = y; this.z = z; this.vx = vx; this.vy = vy; this.vz = vz; } }
    private static void drawCosmetics(Matrix4f mat, double px, double py, double pz, float yaw) {
        long now = System.currentTimeMillis();
        double radYaw = Math.toRadians(yaw + 90.0);
        float cosY = (float) Math.cos(radYaw), sinY = (float) Math.sin(radYaw);
        float rightX = (float) Math.cos(radYaw + Math.PI / 2.0), rightZ = (float) Math.sin(radYaw + Math.PI / 2.0);
        int idx = 0;

        for (CosmeticManager.CosmeticItem item : CosmeticManager.getItems()) {
            if (!item.enabled) continue;
            idx++;
            float r = ((item.color >> 16) & 0xFF) / 255.0f;
            float g = ((item.color >> 8) & 0xFF) / 255.0f;
            float b = (item.color & 0xFF) / 255.0f;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();

            if (item.type == 0) {
                float hy = (float) (py + 1.92);
                if (item.shape == 0 || item.shape == 1) {
                    // Detailed Multi-Layer Ears (outer + inner ear core + twitch)
                    float h = item.shape == 1 ? 0.38f : 0.23f;
                    float w = item.shape == 1 ? 0.08f : 0.13f;
                    float twitch = (float) Math.sin(now / 420.0 + idx) * 0.025f;
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    for (int s = -1; s <= 1; s += 2) {
                        float ex = (float) px + rightX * (s * 0.16f);
                        float ez = (float) pz + rightZ * (s * 0.16f);
                        float tipY = hy + h + (s == 1 ? twitch : 0f);
                        line(buf, mat, ex - rightX * w, hy, ez - rightZ * w, ex, tipY, ez, r, g, b, 0.95f);
                        line(buf, mat, ex + rightX * w, hy, ez + rightZ * w, ex, tipY, ez, r, g, b, 0.95f);
                        line(buf, mat, ex - rightX * w, hy, ez - rightZ * w, ex + rightX * w, hy, ez + rightZ * w, r, g, b, 0.95f);
                        // Inner bright ear layer
                        line(buf, mat, ex - rightX * (w * 0.5f), hy + 0.02f, ez - rightZ * (w * 0.5f), ex, tipY - 0.05f, ez, 1f, 0.85f, 0.92f, 0.85f);
                        line(buf, mat, ex + rightX * (w * 0.5f), hy + 0.02f, ez + rightZ * (w * 0.5f), ex, tipY - 0.05f, ez, 1f, 0.85f, 0.92f, 0.85f);
                    }
                    tess.end();
                } else if (item.shape == 2) {
                    // 3-Segment Tapered Horns with glowing collar rings
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    for (int s = -1; s <= 1; s += 2) {
                        float hx = (float) px + rightX * (s * 0.15f), hz = (float) pz + rightZ * (s * 0.15f);
                        float x1 = hx + rightX * (s * 0.11f) + cosY * 0.05f, z1 = hz + rightZ * (s * 0.11f) + sinY * 0.05f;
                        float x2 = hx + rightX * (s * 0.15f) + cosY * 0.11f, z2 = hz + rightZ * (s * 0.15f) + sinY * 0.11f;
                        float x3 = hx + rightX * (s * 0.08f) + cosY * 0.17f, z3 = hz + rightZ * (s * 0.08f) + sinY * 0.17f;
                        line(buf, mat, hx, hy, hz, x1, hy + 0.12f, z1, r, g, b, 0.92f);
                        line(buf, mat, x1, hy + 0.12f, z1, x2, hy + 0.24f, z2, r, g, b, 0.95f);
                        line(buf, mat, x2, hy + 0.24f, z2, x3, hy + 0.35f, z3, 1f, 1f, 1f, 0.95f);
                    }
                    tess.end();
                } else if (item.shape == 3) {
                    // Double Concentric Halo + 4 Orbiting Light Nodes
                    for (int ring = 0; ring < 2; ring++) {
                        float rad = ring == 0 ? 0.34f : 0.24f;
                        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                        for (int i = 0; i <= 24; i++) {
                            double a = i * Math.PI * 2.0 / 24.0;
                            buf.vertex(mat, (float) (px + Math.cos(a) * rad), hy + 0.24f + ring * 0.03f, (float) (pz + Math.sin(a) * rad)).color(r, g, b, ring == 0 ? 0.95f : 0.6f).endVertex();
                        }
                        tess.end();
                    }
                    for (int n = 0; n < 4; n++) {
                        double a = n * Math.PI / 2.0 + now / 700.0;
                        double nx = px + Math.cos(a) * 0.34, nz = pz + Math.sin(a) * 0.34;
                        drawBoxOutline(mat, new AxisAlignedBB(nx - 0.03, hy + 0.22, nz - 0.03, nx + 0.03, hy + 0.28, nz + 0.03), 1f, 1f, 1f, 0.95f);
                    }
                } else if (item.shape == 4) {
                    // Double-Band Crown + Floating Spike Gems
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 12; i++) {
                        double a = i * Math.PI * 2.0 / 12.0 + now / 1800.0;
                        float cy = hy + (i % 2 == 0 ? 0.20f : 0.06f);
                        buf.vertex(mat, (float) (px + Math.cos(a) * 0.28), cy, (float) (pz + Math.sin(a) * 0.28)).color(r, g, b, 0.95f).endVertex();
                    }
                    tess.end();
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 12; i++) {
                        double a = i * Math.PI * 2.0 / 12.0 + now / 1800.0;
                        buf.vertex(mat, (float) (px + Math.cos(a) * 0.28), hy + 0.02f, (float) (pz + Math.sin(a) * 0.28)).color(r, g, b, 0.8f).endVertex();
                    }
                    tess.end();
                } else if (item.shape == 5) {
                    // Cyber Visor with Animated Scanner Beam
                    float vx = (float) px + cosY * 0.27f, vz = (float) pz + sinY * 0.27f, vy = hy - 0.20f;
                    float scan = (float) Math.sin(now / 260.0) * 0.20f;
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    buf.vertex(mat, vx - rightX * 0.25f, vy + 0.06f, vz - rightZ * 0.25f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, vx + rightX * 0.25f, vy + 0.06f, vz + rightZ * 0.25f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, vx + rightX * 0.25f, vy - 0.06f, vz + rightZ * 0.25f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, vx - rightX * 0.25f, vy - 0.06f, vz - rightZ * 0.25f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, vx - rightX * 0.25f, vy + 0.06f, vz - rightZ * 0.25f).color(r, g, b, 0.95f).endVertex();
                    tess.end();
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, vx + rightX * scan, vy + 0.05f, vz + rightZ * scan, vx + rightX * scan, vy - 0.05f, vz + rightZ * scan, 1f, 1f, 1f, 0.95f);
                    tess.end();
                } else if (item.shape == 6) {
                    // Detailed Wizard Hat (wide brim + 3-ring cone)
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i < 16; i++) {
                        double a = i * Math.PI * 2.0 / 16.0, na = (i + 1) * Math.PI * 2.0 / 16.0;
                        line(buf, mat, (float) (px + Math.cos(a) * 0.45), hy, (float) (pz + Math.sin(a) * 0.45), (float) (px + Math.cos(na) * 0.45), hy, (float) (pz + Math.sin(na) * 0.45), r, g, b, 0.9f);
                        line(buf, mat, (float) (px + Math.cos(a) * 0.26), hy + 0.14f, (float) (pz + Math.sin(a) * 0.26), (float) (px + Math.cos(na) * 0.26), hy + 0.14f, (float) (pz + Math.sin(na) * 0.26), 1f, 0.85f, 0.2f, 0.9f);
                        line(buf, mat, (float) (px + Math.cos(a) * 0.28), hy, (float) (pz + Math.sin(a) * 0.28), (float) px, hy + 0.58f, (float) pz, r, g, b, 0.85f);
                    }
                    tess.end();
                } else if (item.shape == 7) {
                    // Alien Antenna + Orbiting Tip Ring
                    float sway = (float) Math.sin(now / 300.0) * 0.08f;
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, (float) px, hy, (float) pz, (float) px + sway, hy + 0.42f, (float) pz, r, g, b, 0.9f);
                    tess.end();
                    drawBoxOutline(mat, new AxisAlignedBB(px + sway - 0.06, hy + 0.40, pz - 0.06, px + sway + 0.06, hy + 0.52, pz + 0.06), 1f, 1f, 1f, 0.95f);
                } else if (item.shape == 8) {
                    // Samurai Crest + Center Diamond
                    float vx = (float) px + cosY * 0.26f, vz = (float) pz + sinY * 0.26f;
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, vx, hy, vz, vx - rightX * 0.30f, hy + 0.32f, vz - rightZ * 0.30f, r, g, b, 0.95f);
                    line(buf, mat, vx, hy, vz, vx + rightX * 0.30f, hy + 0.32f, vz + rightZ * 0.30f, r, g, b, 0.95f);
                    line(buf, mat, vx, hy + 0.04f, vz, vx, hy + 0.20f, vz, 1f, 0.9f, 0.2f, 0.95f);
                    tess.end();
                } else {
                    // Double-Arc Crescent Moon
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = -6; i <= 6; i++) {
                        double a = i * 0.22;
                        buf.vertex(mat, (float) px + rightX * (float) Math.sin(a) * 0.26f, hy + 0.25f + (1f - (float) Math.cos(a)) * 0.26f, (float) pz + rightZ * (float) Math.sin(a) * 0.26f).color(r, g, b, 0.95f).endVertex();
                    }
                    tess.end();
                }
            } else if (item.type == 1) {
                float flap = (float) Math.sin(now / 260.0 + idx) * 0.24f;
                float bx = (float) px - cosY * 0.22f, bz = (float) pz - sinY * 0.22f;
                if (item.shape == 4) {
                    // Full Katana (Blade + Tsuba Guard + Hilt)
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    line(buf, mat, bx - rightX * 0.40f, (float) py + 1.78f, bz - rightZ * 0.40f, bx + rightX * 0.34f, (float) py + 0.62f, bz + rightZ * 0.34f, r, g, b, 0.95f);
                    line(buf, mat, bx - rightX * 0.44f, (float) py + 1.60f, bz - rightZ * 0.44f, bx - rightX * 0.22f, (float) py + 1.72f, bz - rightZ * 0.22f, 1f, 0.85f, 0.2f, 0.95f);
                    tess.end();
                } else if (item.shape == 5) {
                    // 3-Row Billowing Cape Mesh
                    float sway = (float) Math.sin(now / 280.0) * 0.16f;
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    buf.vertex(mat, bx - rightX * 0.28f, (float) py + 1.42f, bz - rightZ * 0.28f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, bx + rightX * 0.28f, (float) py + 1.42f, bz + rightZ * 0.28f).color(r, g, b, 0.95f).endVertex();
                    buf.vertex(mat, bx + rightX * 0.36f - cosY * (0.28f + sway), (float) py + 0.25f, bz + rightZ * 0.36f - sinY * (0.28f + sway)).color(r, g, b, 0.65f).endVertex();
                    buf.vertex(mat, bx - rightX * 0.36f - cosY * (0.28f + sway), (float) py + 0.25f, bz - rightZ * 0.36f - sinY * (0.28f + sway)).color(r, g, b, 0.65f).endVertex();
                    buf.vertex(mat, bx - rightX * 0.28f, (float) py + 1.42f, bz - rightZ * 0.28f).color(r, g, b, 0.95f).endVertex();
                    tess.end();
                } else if (item.shape == 6) {
                    // Jetpack + Flickering Thruster Flames
                    float fl = 0.15f + 0.10f * (float) Math.sin(now / 60.0);
                    for (int s = -1; s <= 1; s += 2) {
                        double jx = bx + rightX * (s * 0.15), jz = bz + rightZ * (s * 0.15);
                        drawBoxOutline(mat, new AxisAlignedBB(jx - 0.08, py + 0.85, jz - 0.08, jx + 0.08, py + 1.38, jz + 0.08), r, g, b, 0.9f);
                        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                        line(buf, mat, (float) jx, (float) py + 0.85f, (float) jz, (float) jx, (float) py + 0.85f - fl, (float) jz, 1f, 0.55f, 0.1f, 0.95f);
                        tess.end();
                    }
                } else if (item.shape == 7) {
                    // Double Hexagon Aegis Shield
                    for (int k = 0; k < 2; k++) {
                        float sr = k == 0 ? 0.40f : 0.22f;
                        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                        for (int i = 0; i <= 6; i++) {
                            double a = i * Math.PI / 3.0;
                            buf.vertex(mat, bx + rightX * (float) Math.cos(a) * sr, (float) py + 1.12f + (float) Math.sin(a) * sr, bz + rightZ * (float) Math.cos(a) * sr).color(r, g, b, k == 0 ? 0.95f : 0.7f).endVertex();
                        }
                        tess.end();
                    }
                } else {
                    // Multi-Feather Layered Wings
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    for (int s = -1; s <= 1; s += 2) {
                        float wx = bx + (float) (Math.cos(radYaw + s * (0.95 + flap)) * 1.05);
                        float wz = bz + (float) (Math.sin(radYaw + s * (0.95 + flap)) * 1.05);
                        line(buf, mat, bx, (float) py + 1.34f, bz, wx, (float) py + 1.70f, wz, r, g, b, 0.95f);
                        line(buf, mat, wx, (float) py + 1.70f, wz, bx, (float) py + 0.68f, bz, r, g, b, 0.75f);
                        for (int f = 1; f <= 3; f++) {
                            float t = f / 4.0f;
                            line(buf, mat, bx, (float) py + 1.34f - t * 0.45f, bz, bx + (wx - bx) * (1.0f - t * 0.3f), (float) py + 1.70f - t * 0.85f, bz + (wz - bz) * (1.0f - t * 0.3f), r, g, b, 0.82f);
                        }
                    }
                    tess.end();
                }
            } else if (item.type == 2) {
                double ang = now / 900.0 + idx * 1.1;
                double ox = px + Math.cos(ang) * 0.95;
                double oy = py + 1.25 + Math.sin(now / 450.0 + idx) * 0.22;
                double oz = pz + Math.sin(ang) * 0.95;
                if (item.shape == 1) {
                    // 3D Octahedron Crystal (4 sides + equator)
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    float cr = 0.15f, ch = 0.25f;
                    line(buf, mat, (float) ox, (float) oy + ch, (float) oz, (float) ox + cr, (float) oy, (float) oz, r, g, b, 0.95f);
                    line(buf, mat, (float) ox, (float) oy + ch, (float) oz, (float) ox - cr, (float) oy, (float) oz, r, g, b, 0.95f);
                    line(buf, mat, (float) ox, (float) oy + ch, (float) oz, (float) ox, (float) oy, (float) oz + cr, r, g, b, 0.95f);
                    line(buf, mat, (float) ox, (float) oy + ch, (float) oz, (float) ox, (float) oy, (float) oz - cr, r, g, b, 0.95f);
                    line(buf, mat, (float) ox, (float) oy - ch, (float) oz, (float) ox + cr, (float) oy, (float) oz, r, g, b, 0.85f);
                    line(buf, mat, (float) ox, (float) oy - ch, (float) oz, (float) ox - cr, (float) oy, (float) oz, r, g, b, 0.85f);
                    tess.end();
                } else if (item.shape == 2) {
                    // Planet + Dual Tilted Rings
                    drawBoxOutline(mat, new AxisAlignedBB(ox - 0.10, oy - 0.10, oz - 0.10, ox + 0.10, oy + 0.10, oz + 0.10), r, g, b, 0.9f);
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 16; i++) {
                        double a = i * Math.PI * 2.0 / 16.0;
                        buf.vertex(mat, (float) (ox + Math.cos(a) * 0.26), (float) (oy + Math.sin(a) * 0.08), (float) (oz + Math.sin(a) * 0.26)).color(r, g, b, 0.95f).endVertex();
                    }
                    tess.end();
                } else if (item.shape == 4) {
                    // 4-Rotor Spinning Quad-Drone
                    drawBoxOutline(mat, new AxisAlignedBB(ox - 0.09, oy - 0.05, oz - 0.09, ox + 0.09, oy + 0.05, oz + 0.09), r, g, b, 0.95f);
                    buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    double spin = now / 45.0;
                    for (int arm = 0; arm < 4; arm++) {
                        double aa = arm * Math.PI / 2.0 + Math.PI / 4.0;
                        float rx = (float) (ox + Math.cos(aa) * 0.20), rz = (float) (oz + Math.sin(aa) * 0.20);
                        line(buf, mat, (float) ox, (float) oy, (float) oz, rx, (float) oy + 0.03f, rz, r, g, b, 0.9f);
                        line(buf, mat, rx - (float) Math.cos(spin) * 0.07f, (float) oy + 0.04f, rz - (float) Math.sin(spin) * 0.07f,
                                rx + (float) Math.cos(spin) * 0.07f, (float) oy + 0.04f, rz + (float) Math.sin(spin) * 0.07f, 1f, 1f, 1f, 0.9f);
                    }
                    tess.end();
                } else {
                    drawBoxOutline(mat, new AxisAlignedBB(ox - 0.12, oy - 0.12, oz - 0.12, ox + 0.12, oy + 0.12, oz + 0.12), r, g, b, 0.9f);
                }
            } else if (item.type == 3) {
                if (item.shape == 0) {
                    // Pentagram + Outer Rune Ring
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 5; i++) {
                        double a = (i * 2) * Math.PI * 2.0 / 5.0 + now / 2000.0;
                        buf.vertex(mat, (float) (px + Math.cos(a) * 0.95), (float) py + 0.04f, (float) (pz + Math.sin(a) * 0.95)).color(r, g, b, 0.9f).endVertex();
                    }
                    tess.end();
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    for (int i = 0; i <= 24; i++) {
                        double a = i * Math.PI * 2.0 / 24.0;
                        buf.vertex(mat, (float) (px + Math.cos(a) * 0.98), (float) py + 0.04f, (float) (pz + Math.sin(a) * 0.98)).color(r, g, b, 0.55f).endVertex();
                    }
                    tess.end();
                } else if (item.shape == 1) {
                    // Double-Strand DNA Helix
                    for (int st = 0; st < 2; st++) {
                        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                        for (int i = 0; i <= 32; i++) {
                            double a = i * 0.45 + now / 300.0 + st * Math.PI;
                            buf.vertex(mat, (float) (px + Math.cos(a) * 0.72), (float) (py + i * 0.055), (float) (pz + Math.sin(a) * 0.72)).color(r, g, b, 0.85f).endVertex();
                        }
                        tess.end();
                    }
                } else {
                    buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                    double rad = 0.78 + 0.14 * Math.sin(now / 350.0 + idx);
                    for (int i = 0; i <= 20; i++) {
                        double a = i * Math.PI * 2.0 / 20.0 + now / 1000.0;
                        buf.vertex(mat, (float) (px + Math.cos(a) * rad), (float) (py + 0.08 + (item.shape == 2 ? 0.85 : 0.0)), (float) (pz + Math.sin(a) * rad)).color(r, g, b, 0.85f).endVertex();
                    }
                    tess.end();
                }
            } else {
                buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                for (int p = 0; p < 12; p++) {
                    double a = (now / 1200.0) + p * 0.55 + idx;
                    double rad = 0.55 + (p % 3) * 0.45;
                    float sy = item.shape == 1
                            ? (float) (py + 2.2 - ((now / 14.0 + p * 22) % 220) / 100.0)
                            : (float) (py + ((now / 18.0 + p * 25) % 200) / 100.0);
                    float sx = (float) (px + Math.cos(a) * rad), sz = (float) (pz + Math.sin(a) * rad);
                    if (item.shape == 0) {
                        line(buf, mat, sx - 0.05f, sy, sz, sx + 0.05f, sy, sz, r, g, b, 0.9f);
                        line(buf, mat, sx, sy - 0.05f, sz, sx, sy + 0.05f, sz, r, g, b, 0.9f);
                    } else {
                        line(buf, mat, sx, sy, sz, sx, sy + 0.11f, sz, r, g, b, 0.9f);
                    }
                }
                tess.end();
            }
        }
    }

    private static void drawStarField3D(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        long now = System.currentTimeMillis();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 24; i++) {
            double ang = (i * 0.77) + (now / 4500.0) * (i % 2 == 0 ? 1 : -1);
            double rad = 2.2 + (i % 5) * 0.85;
            double yOff = Math.sin(now / 900.0 + i) * 1.4;
            float sx = (float) (cx + Math.cos(ang) * rad);
            float sy = (float) (cy + yOff);
            float sz = (float) (cz + Math.sin(ang) * rad);
            float a = 0.45f + 0.35f * (float) Math.sin(now / 400.0 + i * 1.3);
            line(buf, mat, sx - 0.05f, sy, sz, sx + 0.05f, sy, sz, r, g, b, a);
            line(buf, mat, sx, sy - 0.05f, sz, sx, sy + 0.05f, sz, r, g, b, a);
        }
        tess.end();
    }

    private static void drawFootstepGlow(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        long now = System.currentTimeMillis();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        float rad = 0.25f + 0.15f * (float) Math.sin(now / 120.0);
        for (int i = 0; i <= 16; i++) {
            double ang = i * Math.PI * 2.0 / 16.0;
            buf.vertex(mat, (float) (cx + Math.cos(ang) * rad), (float) cy, (float) (cz + Math.sin(ang) * rad)).color(r, g, b, 0.55f).endVertex();
        }
        tess.end();
    }

    private static void drawWorldGrid(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        int rad = ModuleManager.GRID_RADIUS != null ? (int) ModuleManager.GRID_RADIUS.getValue() : 6;
        float alpha = ModuleManager.GRID_ALPHA != null ? ModuleManager.GRID_ALPHA.getFloat() : 0.35f;
        float pulse = 0.7f + 0.3f * (float) Math.sin(System.currentTimeMillis() / 450.0);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        double gx0 = Math.floor(cx);
        double gz0 = Math.floor(cz);
        for (int i = -rad; i <= rad; i++) {
            float fade = 1.0f - Math.abs(i) / (float) (rad + 1);
            float a = alpha * fade * pulse;
            line(buf, mat, (float) (gx0 + i), (float) cy, (float) (gz0 - rad), (float) (gx0 + i), (float) cy, (float) (gz0 + rad), r, g, b, a);
            line(buf, mat, (float) (gx0 - rad), (float) cy, (float) (gz0 + i), (float) (gx0 + rad), (float) cy, (float) (gz0 + i), r, g, b, a);
        }
        tess.end();
    }

    private static void drawPulseRings(Matrix4f mat, double cx, double cy, double cz, float r, float g, float b) {
        long now = System.currentTimeMillis();
        for (int k = 0; k < 2; k++) {
            float phase = ((now + k * 1100L) % 2200L) / 2200.0f;
            float rad = 0.4f + phase * 2.4f;
            float a = (1.0f - phase) * 0.55f;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuilder();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 48; i++) {
                double ang = i * Math.PI * 2.0 / 48.0;
                buf.vertex(mat, (float) (cx + Math.cos(ang) * rad), (float) cy, (float) (cz + Math.sin(ang) * rad)).color(r, g, b, a).endVertex();
            }
            tess.end();
        }
    }

    private static void drawWaypoints(MatrixStack ms, Matrix4f mat, Minecraft mc, float partialTicks) {
        String dim = WaypointManager.getCurrentDimension();
        float beamAlpha = ModuleManager.WP_ALPHA != null ? ModuleManager.WP_ALPHA.getFloat() : 0.55f;
        RenderSystem.disableDepthTest();
        for (WaypointManager.Waypoint wp : WaypointManager.getWaypoints()) {
            if (!wp.enabled || !dim.equals(wp.dimension)) continue;
            float wr = ((wp.color >> 16) & 0xFF) / 255.0f;
            float wg = ((wp.color >> 8) & 0xFF) / 255.0f;
            float wb = (wp.color & 0xFF) / 255.0f;

            if (ModuleManager.WP_BEAM == null || ModuleManager.WP_BEAM.isEnabled()) {
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buf = tess.getBuilder();
                buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                for (int c = 0; c < 4; c++) {
                    double ang = c * Math.PI / 2.0 + (System.currentTimeMillis() % 4000L) / 4000.0 * Math.PI * 2.0;
                    float ox = (float) Math.cos(ang) * 0.35f;
                    float oz = (float) Math.sin(ang) * 0.35f;
                    line(buf, mat, (float) wp.x + ox, (float) wp.y - 2f, (float) wp.z + oz, (float) wp.x + ox, (float) wp.y + 80f, (float) wp.z + oz, wr, wg, wb, beamAlpha);
                }
                tess.end();
                drawBoxOutline(mat, new AxisAlignedBB(wp.x - 0.35, wp.y - 0.35, wp.z - 0.35, wp.x + 0.35, wp.y + 0.35, wp.z + 0.35), wr, wg, wb, 0.9f);
            }

            if (ModuleManager.WP_LABELS == null || ModuleManager.WP_LABELS.isEnabled()) {
                double dx = wp.x - mc.player.getX();
                double dy = wp.y - mc.player.getY();
                double dz = wp.z - mc.player.getZ();
                int dist = (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
                String tag = wp.name + " [" + dist + "m]";
                double scale = Math.max(0.025, Math.min(0.18, dist * 0.0025));
                ms.pushPose();
                ms.translate(wp.x, wp.y + 1.6, wp.z);
                ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                ms.scale((float) -scale, (float) -scale, (float) scale);
                RenderSystem.enableTexture();
                int tw = mc.font.width(tag);
                mc.font.draw(ms, tag, -tw / 2.0f, 0, wp.color);
                RenderSystem.disableTexture();
                ms.popPose();
            }
        }
        RenderSystem.enableDepthTest();
    }
}