package com.creamclient.visual;

import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public final class SkyRenderer {
    private SkyRenderer() {}

    public static int getSkyColorInt() {
        String mode = ModuleManager.SKY_MODE != null ? ModuleManager.SKY_MODE.getMode() : "LimeAurora";
        switch (mode) {
            case "CyberGrid": return 0xFF0A1F2E;
            case "MidnightStars": return 0xFF050816;
            case "CitrusSunset": return 0xFFFF6B1A;
            case "BloodMoon": return 0xFF660914;
            case "VoidDark": return 0xFF040504;
            case "NeonPink": return 0xFFFF2E93;
            case "OceanicDeep": return 0xFF062C43;
            case "EmeraldNebula": return 0xFF0A4D2E;
            case "GoldHorizon": return 0xFFE6A817;
            case "RainbowPrism": {
                float hue = (System.currentTimeMillis() % 6000L) / 6000.0f;
                return Color.HSBtoRGB(hue, 0.7f, 0.85f);
            }
            case "ArcticFrost": return 0xFF6DD5FA;
            case "ToxicStorm": return 0xFF39FF14;
            case "SakuraSky": return 0xFFFFB7C5;
            case "AmethystSpace": return 0xFF4A148C;
            case "SolarFlare": return 0xFFFF4500;
            case "MatrixRain": return 0xFF003B00;
            case "PastelCream": return 0xFFF3E5AB;
            case "Eclipse": return 0xFF14101C;
            case "CustomRGB": {
                int r = (int) ModuleManager.SKY_R.getValue();
                int g = (int) ModuleManager.SKY_G.getValue();
                int b = (int) ModuleManager.SKY_B.getValue();
                return (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            }
            default: return 0xFF28590C; // LimeAurora
        }
    }

    public static void renderCustomSky(MatrixStack ms) {
        if (!ModuleManager.CUSTOM_SKY.isEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Vector3d cam = mc.gameRenderer.getMainCamera().getPosition();
        ms.pushPose();
        ms.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        int col = getSkyColorInt();
        float r = ((col >> 16) & 0xFF) / 255.0f;
        float g = ((col >> 8) & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;
        float alpha = ModuleManager.SKY_ALPHA.getFloat();
        double spin = (System.currentTimeMillis() / 1000.0) * ModuleManager.SKY_SPIN.getValue() * 0.25;

        Matrix4f mat = ms.last().pose();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuilder();

        // Sky dome canopy above player
        double cx = cam.x, cy = cam.y + 45.0, cz = cam.z;
        float rad = 95.0f;
        buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buf.vertex(mat, (float) cx, (float) (cy + 35.0), (float) cz).color(r, g, b, alpha).endVertex();
        for (int i = 0; i <= 32; i++) {
            double ang = spin + (Math.PI * 2.0 * i) / 32;
            buf.vertex(mat, (float) (cx + Math.cos(ang) * rad), (float) (cy - 10.0), (float) (cz + Math.sin(ang) * rad))
                    .color(r * 0.4f, g * 0.4f, b * 0.4f, alpha * 0.35f).endVertex();
        }
        tess.end();

        // Aurora / Nebula ribbons
        if (ModuleManager.SKY_AURORA.isEnabled()) {
            int accent = CreamTheme.getAccent();
            float ar = ((accent >> 16) & 0xFF) / 255.0f;
            float ag = ((accent >> 8) & 0xFF) / 255.0f;
            float ab = (accent & 0xFF) / 255.0f;
            RenderSystem.lineWidth(2.0f);
            for (int ring = 0; ring < 3; ring++) {
                buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
                double rr = 60.0 + ring * 14.0;
                for (int i = 0; i < 48; i++) {
                    double ang = spin * (ring % 2 == 0 ? 1.0 : -1.0) + (Math.PI * 2.0 * i) / 48;
                    double waveY = cy + 12.0 + Math.sin(ang * 3.0 + spin * 2.0) * 8.0;
                    buf.vertex(mat, (float) (cx + Math.cos(ang) * rr), (float) waveY, (float) (cz + Math.sin(ang) * rr))
                            .color(ar, ag, ab, alpha * 0.7f).endVertex();
                }
                tess.end();
            }
            RenderSystem.lineWidth(1.0f);
        }

        // Star ring
        if (ModuleManager.SKY_STARS.isEnabled()) {
            buf.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i < 60; i++) {
                double ang = spin * 0.5 + i * 2.399;
                double dist = 40.0 + (i % 7) * 8.0;
                double sy = cy + 18.0 + (i % 5) * 4.0;
                buf.vertex(mat, (float) (cx + Math.cos(ang) * dist), (float) sy, (float) (cz + Math.sin(ang) * dist))
                        .color(1.0f, 1.0f, 1.0f, alpha).endVertex();
            }
            tess.end();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        ms.popPose();
    }
}
