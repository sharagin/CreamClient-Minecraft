package com.creamclient.discord;

import com.creamclient.CreamClientMod;
import com.creamclient.module.ModuleManager;
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

    // Configurable via creamclient.json ("discordAppId")
    public static String appId = "1542144804178370560";

    private DiscordRPC() {}

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
            try {
                pipe.close();
            } catch (Exception ignored) {
            }
            pipe = null;
        }
    }

    private static void loop() {
        while (running && ModuleManager.DISCORD_RPC.isEnabled()) {
            try {
                if (pipe == null) {
                    connectAndHandshake();
                }
                if (pipe != null) {
                    sendActivity();
                }
            } catch (Exception e) {
                closePipe();
            }
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                break;
            }
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
        Minecraft mc = Minecraft.getInstance();
        String details = "Playing Minecraft 1.16.5";
        String state = "In Main Menu";

        if (mc.level != null && mc.player != null) {
            if (mc.isLocalServer()) {
                state = "Singleplayer";
            } else if (mc.getCurrentServer() != null && ModuleManager.RPC_SHOW_SERVER.isEnabled()) {
                state = "Server: " + mc.getCurrentServer().ip;
            } else {
                state = "Multiplayer";
            }
            if (ModuleManager.RPC_SHOW_NICK.isEnabled()) {
                details = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION + " | " + mc.player.getName().getString();
            } else {
                details = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION + " (1.16.5)";
            }
        } else {
            details = CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION;
        }

        JsonObject activity = new JsonObject();
        activity.addProperty("details", details);
        activity.addProperty("state", state);

        JsonObject timestamps = new JsonObject();
        timestamps.addProperty("start", startTimestamp);
        activity.add("timestamps", timestamps);

        JsonObject assets = new JsonObject();
        assets.addProperty("large_image", "cream_logo");
        assets.addProperty("large_text", CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION);
        activity.add("assets", assets);

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
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            return Integer.parseInt(name.split("@")[0]);
        } catch (Exception e) {
            return 1000;
        }
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
        hBuf.getInt(); // op
        int len = hBuf.getInt();
        if (len > 0 && len < 65536) {
            byte[] body = new byte[len];
            pipe.readFully(body);
        }
    }
}
