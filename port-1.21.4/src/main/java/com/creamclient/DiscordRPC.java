package com.creamclient;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DiscordRPC {
    private static volatile boolean running = false;
    private static volatile String cachedDetails = "CreamClient 1.0 (1.21.4)";
    private static volatile String cachedState = "In Main Menu";
    private static final long startTime = System.currentTimeMillis() / 1000L;

    public static void start(String appId) {
        if (running) return;
        running = true;
        Thread t = new Thread(() -> {
            RandomAccessFile pipe = null;
            for (int i = 0; i < 10; i++) {
                try {
                    pipe = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + i, "rw");
                    break;
                } catch (Exception ignored) {}
            }
            if (pipe == null) {
                running = false;
                return;
            }
            try {
                sendFrame(pipe, 0, "{\"v\":1,\"client_id\":\"" + appId + "\"}");
                Thread.sleep(400);
                while (running) {
                    String payload = "{\"cmd\":\"SET_ACTIVITY\",\"args\":{\"pid\":1,\"activity\":{"
                        + "\"details\":\"" + escape(cachedDetails) + "\","
                        + "\"state\":\"" + escape(cachedState) + "\","
                        + "\"timestamps\":{\"start\":" + startTime + "},"
                        + "\"assets\":{\"large_image\":\"logo\",\"large_text\":\"CreamClient 1.0\"},"
                        + "\"buttons\":["
                        + "{\"label\":\"Discord Server\",\"url\":\"https://discord.gg/kRNZn7Qbz\"},"
                        + "{\"label\":\"Download CreamClient\",\"url\":\"https://github.com/sharagin/CreamClient-Minecraft/releases\"}"
                        + "]}"
                        + "},\"nonce\":\"" + System.nanoTime() + "\"}";
                    sendFrame(pipe, 1, payload);
                    Thread.sleep(5000);
                }
            } catch (Exception ignored) {
            } finally {
                try { pipe.close(); } catch (Exception ignored) {}
                running = false;
            }
        }, "CreamClient-DiscordRPC-1.21.4");
        t.setDaemon(true);
        t.start();
    }

    public static void update(String details, String state) {
        cachedDetails = details != null ? details : "CreamClient 1.0 (1.21.4)";
        cachedState = state != null ? state : "";
    }

    private static void sendFrame(RandomAccessFile pipe, int opcode, String json) throws Exception {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(8 + data.length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(opcode);
        buf.putInt(data.length);
        buf.put(data);
        pipe.write(buf.array());
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
