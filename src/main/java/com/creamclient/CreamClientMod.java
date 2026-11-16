package com.creamclient;

import com.creamclient.config.CreamConfig;
import com.creamclient.discord.DiscordRPC;
import com.creamclient.event.ClientEvents;
import com.creamclient.media.MediaManager;
import com.creamclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.glfw.GLFW;

@Mod(CreamClientMod.MOD_ID)
public class CreamClientMod {
    public static final String MOD_ID = "creamclient";
    public static final String CLIENT_NAME = "CreamClient";
    public static final String CLIENT_VERSION = "1.0";
    public static final String GITHUB_URL = "https://github.com/sharagin/CreamClient-Minecraft";
    public static final String RELEASES_URL = "https://github.com/sharagin/CreamClient-Minecraft/releases";
    public static final long SESSION_START = System.currentTimeMillis();

    public static KeyBinding clickGuiKey;
    public static KeyBinding zoomKey;

    public CreamClientMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
            ModuleManager.init();
            CreamConfig.load();
            MediaManager.initDirectories();
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
        }
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        clickGuiKey = new KeyBinding("key.creamclient.clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, "CreamClient");
        zoomKey = new KeyBinding("key.creamclient.zoom", GLFW.GLFW_KEY_C, "CreamClient");
        ClientRegistry.registerKeyBinding(clickGuiKey);
        ClientRegistry.registerKeyBinding(zoomKey);

        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getWindow() != null) {
                mc.getWindow().setTitle(CLIENT_NAME + " " + CLIENT_VERSION + " // 1.16.5");
            }
            MediaManager.reloadAssets();
            if (ModuleManager.DISCORD_RPC.isEnabled()) {
                DiscordRPC.start();
            }
        });
    }
}
