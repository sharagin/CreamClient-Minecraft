package com.creamclient.discord;

import com.creamclient.CreamClientMod;
import com.creamclient.media.MediaManager;
import com.creamclient.module.Module;
import com.creamclient.module.ModuleManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class DiscordRPC {
    private static volatile boolean running = false;
    private static Thread rpcThread;
    private static RandomAccessFile pipe;
    private static long startTimestamp = System.currentTimeMillis() / 1000L;
    private static volatile String cachedDetails = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION;
    private static volatile String cachedState = "In Main Menu // 1.16.5";
    public static String appId = "1571542048828293302";

    private DiscordRPC() {}

    public static void updateStateFromMainThread() {
        try {
            Minecraft mc = Minecraft.getInstance();
            int enabledCount = 0;
            for (Module m : ModuleManager.getModules()) if (m.isEnabled()) enabledCount++;

            if (MediaManager.isPlaying()) {
                cachedDetails = "Listening: " + MediaManager.getCurrentTrackName();
            } else if (mc.level != null && mc.player != null && ModuleManager.RPC_SHOW_NICK.isEnabled()) {
                cachedDetails = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION + " | " + mc.player.getName().getString();
            } else {
                cachedDetails = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION + " [" + enabledCount + " mods]";
            }

            if (mc.level != null && mc.player != null) {
                if (mc.isLocalServer()) {
                    cachedState = "Singleplayer // 1.16.5";
                } else if (mc.getCurrentServer() != null && ModuleManager.RPC_SHOW_SERVER.isEnabled()) {
                    cachedState = "Server: " + mc.getCurrentServer().ip;
                } else {
                    cachedState = "Multiplayer // 1.16.5";
                }
            } else {
                cachedState = "In Main Menu // Forge 1.16.5";
            }
        } catch (Exception ignored) {}
    }

    public static synchronized void start() {
        if (running) return;
        running = true;
        startTimestamp = System.currentTimeMillis() / 1000L;
        rpcThread = new Thread(DiscordRPC::loop, "CreamClient-DiscordRPC");
        rpcThread.setDaemon(true);
        rpcThread.start();
    }

    public static synchronized void stop() {
        running = false;
        closePipe();
    }

    private static void closePipe() {
        if (pipe != null) {
            try { pipe.close(); } catch (Exception ignored) {}
            pipe = null;
        }
    }

    private static void loop() {
        while (running && (ModuleManager.DISCORD_RPC == null || ModuleManager.DISCORD_RPC.isEnabled())) {
            try {
                if (pipe == null) connectAndHandshake();
                if (pipe != null) sendActivity();
            } catch (Exception e) {
                closePipe();
            }
            try { Thread.sleep(4000L); } catch (InterruptedException e) { break; }
        }
        closePipe();
    }

    private static void connectAndHandshake() throws Exception {
        for (int i = 0; i < 10; i++) {
            try {
                pipe = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + i, "rw");
                JsonObject handshake = new JsonObject();
                handshake.addProperty("v", 1);
                handshake.addProperty("client_id", appId);
                writePacket(0, handshake.toString());
                readPacket();
                return;
            } catch (Exception ignored) {
                closePipe();
            }
        }
    }

    private static void sendActivity() throws Exception {
        JsonObject activity = new JsonObject();
        activity.addProperty("details", cachedDetails);
        activity.addProperty("state", cachedState);
        JsonObject timestamps = new JsonObject();
        timestamps.addProperty("start", startTimestamp);
        activity.add("timestamps", timestamps);
        JsonObject assets = new JsonObject();
        assets.addProperty("large_image", "cream_logo");
        assets.addProperty("large_text", CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION);
        activity.add("assets", assets);
        if (ModuleManager.RPC_BUTTONS == null || ModuleManager.RPC_BUTTONS.isEnabled()) {
            JsonArray buttons = new JsonArray();
            JsonObject btn1 = new JsonObject();
            btn1.addProperty("label", "Discord Server");
            btn1.addProperty("url", CreamClientMod.DISCORD_INVITE_URL);
            buttons.add(btn1);
            JsonObject btn2 = new JsonObject();
            btn2.addProperty("label", "Download CreamClient");
            btn2.addProperty("url", CreamClientMod.RELEASES_URL);
            buttons.add(btn2);
            activity.add("buttons", buttons);
        }
        JsonObject args = new JsonObject();
        args.addProperty("pid", getPid());
        args.add("activity", activity);
        JsonObject payload = new JsonObject();
        payload.addProperty("cmd", "SET_ACTIVITY");
        payload.add("args", args);
        payload.addProperty("nonce", UUID.randomUUID().toString());
        writePacket(1, payload.toString());
        readPacket();
    }

    private static int getPid() {
        try { return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]); }
        catch (Exception e) { return 1000; }
    }

    private static void writePacket(int op, String json) throws Exception {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(8 + data.length).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(op);
        buf.putInt(data.length);
        buf.put(data);
        pipe.write(buf.array());
    }

    private static void readPacket() throws Exception {
        byte[] header = new byte[8];
        pipe.readFully(header);
        ByteBuffer hBuf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
        hBuf.getInt();
        int len = hBuf.getInt();
        if (len > 0 && len < 65536) {
            byte[] body = new byte[len];
            pipe.readFully(body);
        }
    }
}
