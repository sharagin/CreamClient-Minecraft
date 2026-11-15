package com.creamclient;

import com.creamclient.event.ClientEvents;
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
    public static final String CLIENT_VERSION = "1.0.0";

    public static KeyBinding clickGuiKey;

    public CreamClientMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
            ModuleManager.init();
            MinecraftForge.EVENT_BUS.register(new ClientEvents());
        }
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        clickGuiKey = new KeyBinding("key.creamclient.clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, "CreamClient");
        ClientRegistry.registerKeyBinding(clickGuiKey);

        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getWindow() != null) {
                mc.getWindow().setTitle(CLIENT_NAME + " // 1.16.5");
            }
        });
    }
}
