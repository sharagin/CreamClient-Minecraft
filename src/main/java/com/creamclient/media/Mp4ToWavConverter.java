package com.creamclient.media;

import com.creamclient.theme.CreamTheme;
import com.creamclient.ui.NotificationManager;

import java.io.File;

public final class Mp4ToWavConverter {
    private Mp4ToWavConverter() {}

    public static void convertAllInMusicDir() {
        MediaManager.initDirectories();
        File dir = MediaManager.getMusicDir();
        File[] files = dir.listFiles((d, n) -> {
            String l = n.toLowerCase();
            return l.endsWith(".mp4") || l.endsWith(".mp3") || l.endsWith(".m4a") || l.endsWith(".ogg");
        });
        if (files == null || files.length == 0) {
            NotificationManager.push("MP4->WAV", "No .mp4/.mp3 in creamclient/music", 0xFFFACC15);
            return;
        }
        Thread t = new Thread(() -> {
            int ok = 0;
            for (File f : files) {
                String outName = f.getName().replaceAll("\\.[^.]+$", "") + ".wav";
                File out = new File(dir, outName);
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            "ffmpeg", "-y", "-i", f.getAbsolutePath(),
                            "-vn", "-acodec", "pcm_s16le", "-ar", "44100", "-ac", "2",
                            out.getAbsolutePath()
                    );
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    if (p.waitFor() == 0 && out.exists()) ok++;
                } catch (Exception e) {
                    NotificationManager.push("MP4->WAV", "Install ffmpeg or use tools/mp4_to_wav.bat", 0xFFFF5555);
                    return;
                }
            }
            MediaManager.reloadAssets();
            NotificationManager.push("MP4->WAV", "Converted: " + ok, CreamTheme.getAccent());
        }, "CreamClient-MP4-Converter");
        t.setDaemon(true);
        t.start();
    }
}
