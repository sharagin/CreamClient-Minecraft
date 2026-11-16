package com.creamclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("creamclient")
public class CreamClientMod {
    public static final String MOD_ID = "creamclient";
    public static final String VERSION = "1.0";

    public CreamClientMod(FMLJavaModLoadingContext context) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            context.getModEventBus().addListener(this::onClientSetup);
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        DiscordRPC.start("1571542048828293302");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            String server = mc.getCurrentServer() != null ? mc.getCurrentServer().ip : "Singleplayer";
            DiscordRPC.update("CreamClient 1.0 (1.21.4)", server);
        } else {
            DiscordRPC.update("CreamClient 1.0 (1.21.4)", "In Main Menu");
        }
    }

    @SubscribeEvent
    public void onScreenRender(ScreenEvent.Render.Post event) {
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        String label = "CreamClient 1.0 | Forge 1.21.4";
        int w = mc.font.width(label) + 12;
        gui.fill(6, 6, 6 + w, 20, 0xCC101414);
        gui.fill(6, 6, 8, 20, 0xFFA3E635);
        gui.drawString(mc.font, label, 12, 9, 0xFFE6F4EA);
    }
}
