package com.creamclient.media;

import com.creamclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MediaManager {
    private static final List<ResourceLocation> bgTextures = new ArrayList<>();
    private static int bgIndex = 0;
    private static ResourceLocation currentBgTexture = null;

    private static final List<File> musicTracks = new ArrayList<>();
    private static int trackIndex = 0;
    private static Clip activeClip = null;
    private static String currentTrackName = "No Track";
    private static boolean paused = false;
    private static long pausePositionMicros = 0;

    private MediaManager() {}

    public static File getBaseDir() { return new File(Minecraft.getInstance().gameDirectory, "creamclient"); }
    public static File getBackgroundsDir() { return new File(getBaseDir(), "backgrounds"); }
    public static File getMusicDir() { return new File(getBaseDir(), "music"); }

    public static void initDirectories() {
        if (!getBaseDir().exists()) getBaseDir().mkdirs();
        if (!getBackgroundsDir().exists()) getBackgroundsDir().mkdirs();
        if (!getMusicDir().exists()) getMusicDir().mkdirs();
        reloadAssets();
    }

    public static void reloadAssets() {
        bgTextures.clear();
        File[] imgs = getBackgroundsDir().listFiles((d, n) -> n.toLowerCase().endsWith(".png") || n.toLowerCase().endsWith(".jpg"));
        if (imgs != null) {
            Arrays.sort(imgs);
            for (int i = 0; i < imgs.length; i++) {
                try (InputStream is = new FileInputStream(imgs[i])) {
                    NativeImage ni = NativeImage.read(is);
                    DynamicTexture dt = new DynamicTexture(ni);
                    ResourceLocation rl = Minecraft.getInstance().getTextureManager().register("cream_bg_" + i, dt);
                    bgTextures.add(rl);
                } catch (Exception ignored) {}
            }
        }
        currentBgTexture = bgTextures.isEmpty() ? null : bgTextures.get(bgIndex % bgTextures.size());

        musicTracks.clear();
        File[] wavs = getMusicDir().listFiles((d, n) -> n.toLowerCase().endsWith(".wav"));
        if (wavs != null) {
            Arrays.sort(wavs);
            musicTracks.addAll(Arrays.asList(wavs));
        }
        if (musicTracks.isEmpty()) currentTrackName = "Put .wav in creamclient/music";
        else currentTrackName = musicTracks.get(trackIndex % musicTracks.size()).getName().replace(".wav", "");
    }

    public static void nextBackground() {
        if (bgTextures.isEmpty()) {
            reloadAssets();
            return;
        }
        bgIndex = (bgIndex + 1) % bgTextures.size();
        currentBgTexture = bgTextures.get(bgIndex);
    }

    public static ResourceLocation getCurrentBgTexture() { return currentBgTexture; }

    public static List<File> getTracks() { return musicTracks; }
    public static int getCurrentIndex() { return trackIndex; }
    public static boolean isPlaying() { return activeClip != null && activeClip.isRunning(); }
    public static boolean isPaused() { return paused; }

    public static void playMusic() {
        if (musicTracks.isEmpty()) {
            reloadAssets();
            if (musicTracks.isEmpty()) return;
        }
        playTrack(trackIndex % musicTracks.size());
    }

    public static void playTrack(int idx) {
        if (musicTracks.isEmpty()) return;
        stopMusic();
        trackIndex = ((idx % musicTracks.size()) + musicTracks.size()) % musicTracks.size();
        File track = musicTracks.get(trackIndex);
        currentTrackName = track.getName().replace(".wav", "");
        paused = false;
        pausePositionMicros = 0;
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(track);
            activeClip = AudioSystem.getClip();
            activeClip.open(ais);
            updateVolume();
            activeClip.loop(Clip.LOOP_CONTINUOUSLY);
            activeClip.start();
        } catch (Exception e) {
            currentTrackName = "Audio error (need 16-bit PCM .wav)";
        }
    }

    public static void togglePause() {
        if (activeClip == null) {
            playMusic();
            return;
        }
        if (activeClip.isRunning()) {
            pausePositionMicros = activeClip.getMicrosecondPosition();
            activeClip.stop();
            paused = true;
        } else {
            activeClip.setMicrosecondPosition(pausePositionMicros % Math.max(1, activeClip.getMicrosecondLength()));
            updateVolume();
            activeClip.loop(Clip.LOOP_CONTINUOUSLY);
            activeClip.start();
            paused = false;
        }
    }

    public static void nextTrack() {
        if (musicTracks.isEmpty()) return;
        playTrack((trackIndex + 1) % musicTracks.size());
    }

    public static void prevTrack() {
        if (musicTracks.isEmpty()) return;
        playTrack((trackIndex - 1 + musicTracks.size()) % musicTracks.size());
    }

    public static void stopMusic() {
        paused = false;
        pausePositionMicros = 0;
        if (activeClip != null) {
            try {
                activeClip.stop();
                activeClip.close();
            } catch (Exception ignored) {}
            activeClip = null;
        }
    }

    public static void updateVolume() {
        if (activeClip != null && activeClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) activeClip.getControl(FloatControl.Type.MASTER_GAIN);
            float vol = ModuleManager.MUSIC_VOL != null ? ModuleManager.MUSIC_VOL.getValueFloat() : 0.45f;
            float db = (float) (Math.log10(Math.max(0.01f, vol)) * 20.0);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        }
    }

    public static float getProgress() {
        if (activeClip == null || activeClip.getMicrosecondLength() <= 0) return 0f;
        long pos = paused ? pausePositionMicros : activeClip.getMicrosecondPosition();
        return Math.max(0f, Math.min(1f, (float) (pos % activeClip.getMicrosecondLength()) / (float) activeClip.getMicrosecondLength()));
    }

    public static String getPositionFormatted() {
        if (activeClip == null || activeClip.getMicrosecondLength() <= 0) return "00:00";
        long pos = paused ? pausePositionMicros : (activeClip.getMicrosecondPosition() % activeClip.getMicrosecondLength());
        long sec = pos / 1_000_000L;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    public static String getDurationFormatted() {
        if (activeClip == null || activeClip.getMicrosecondLength() <= 0) return "00:00";
        long sec = activeClip.getMicrosecondLength() / 1_000_000L;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }

    public static String getCurrentTrackName() { return currentTrackName; }

    public static void openMusicFolder() {
        initDirectories();
        Util.getPlatform().openFile(getMusicDir());
    }
}
