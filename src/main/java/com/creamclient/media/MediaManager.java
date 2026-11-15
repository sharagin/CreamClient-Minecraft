package com.creamclient.media;

import com.creamclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class MediaManager {
    private static final List<File> BG_FILES = new ArrayList<>();
    private static final List<File> MUSIC_FILES = new ArrayList<>();

    private static int bgIndex = 0;
    private static int musicIndex = 0;

    private static ResourceLocation currentBgTexture = null;
    private static Clip currentClip = null;
    private static String currentTrackName = "None";

    private MediaManager() {}

    public static File getBaseDir() {
        return new File(Minecraft.getInstance().gameDirectory, "creamclient");
    }

    public static File getBackgroundsDir() {
        return new File(getBaseDir(), "backgrounds");
    }

    public static File getMusicDir() {
        return new File(getBaseDir(), "music");
    }

    public static void initDirectories() {
        try {
            getBaseDir().mkdirs();
            getBackgroundsDir().mkdirs();
            getMusicDir().mkdirs();
            File readme = new File(getBaseDir(), "README.txt");
            if (!readme.exists()) {
                try (FileWriter fw = new FileWriter(readme)) {
                    fw.write("CreamClient 1.0 Media Folder\n\n");
                    fw.write("1. Put .png or .jpg images into /backgrounds/\n");
                    fw.write("2. Put .wav music tracks into /music/\n");
                    fw.write("3. Switch backgrounds and music in the Main Menu or ClickGUI [Right Shift] -> Media!\n");
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void reloadAssets() {
        BG_FILES.clear();
        File[] imgs = getBackgroundsDir().listFiles((dir, name) -> {
            String l = name.toLowerCase();
            return l.endsWith(".png") || l.endsWith(".jpg") || l.endsWith(".jpeg");
        });
        if (imgs != null) {
            for (File f : imgs) BG_FILES.add(f);
        }
        if (!BG_FILES.isEmpty()) {
            bgIndex = bgIndex % BG_FILES.size();
            loadBackgroundTexture(BG_FILES.get(bgIndex));
        } else {
            currentBgTexture = null;
        }

        MUSIC_FILES.clear();
        File[] tracks = getMusicDir().listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));
        if (tracks != null) {
            for (File f : tracks) MUSIC_FILES.add(f);
        }
    }

    public static void nextBackground() {
        if (BG_FILES.isEmpty()) {
            reloadAssets();
            return;
        }
        bgIndex = (bgIndex + 1) % BG_FILES.size();
        loadBackgroundTexture(BG_FILES.get(bgIndex));
    }

    private static void loadBackgroundTexture(File file) {
        try {
            BufferedImage bi = ImageIO.read(file);
            if (bi == null) return;
            int w = bi.getWidth();
            int h = bi.getHeight();
            NativeImage ni = new NativeImage(NativeImage.PixelFormat.RGBA, w, h, false);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int argb = bi.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                    ni.setPixelRGBA(x, y, abgr);
                }
            }
            DynamicTexture dyn = new DynamicTexture(ni);
            currentBgTexture = Minecraft.getInstance().getTextureManager().register("creamclient_bg_" + bgIndex, dyn);
        } catch (Exception ignored) {
            currentBgTexture = null;
        }
    }

    public static ResourceLocation getCurrentBgTexture() {
        return currentBgTexture;
    }

    public static void playMusic() {
        if (MUSIC_FILES.isEmpty()) {
            reloadAssets();
        }
        if (MUSIC_FILES.isEmpty() || "Off".equals(ModuleManager.MUSIC_MODE.getMode())) {
            currentTrackName = "No .wav in /creamclient/music";
            return;
        }
        stopMusic();
        try {
            File track = MUSIC_FILES.get(musicIndex % MUSIC_FILES.size());
            AudioInputStream ais = AudioSystem.getAudioInputStream(track);
            currentClip = AudioSystem.getClip();
            currentClip.open(ais);
            updateVolume();
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            currentClip.start();
            currentTrackName = track.getName();
        } catch (Exception e) {
            currentTrackName = "Audio error";
        }
    }

    public static void nextTrack() {
        if (MUSIC_FILES.isEmpty()) {
            reloadAssets();
        }
        if (MUSIC_FILES.isEmpty()) return;
        musicIndex = (musicIndex + 1) % MUSIC_FILES.size();
        if (ModuleManager.MUSIC_MOD.isEnabled()) {
            playMusic();
        }
    }

    public static void stopMusic() {
        if (currentClip != null) {
            try {
                currentClip.stop();
                currentClip.close();
            } catch (Exception ignored) {
            }
            currentClip = null;
        }
    }

    public static void updateVolume() {
        if (currentClip != null && currentClip.isOpen()) {
            try {
                FloatControl gain = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
                float vol = ModuleManager.MUSIC_VOL.getFloat();
                if (vol <= 0.01f) {
                    gain.setValue(gain.getMinimum());
                } else {
                    float dB = (float) (20.0 * Math.log10(vol));
                    gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static String getCurrentTrackName() {
        return currentTrackName;
    }
}
