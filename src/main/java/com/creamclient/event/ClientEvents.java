package com.creamclient.event;

import com.creamclient.CreamClientMod;
import com.creamclient.module.ModuleManager;
import com.creamclient.ui.ClickGuiScreen;
import com.creamclient.ui.CreamMainMenuScreen;
import com.creamclient.ui.HudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof MainMenuScreen) {
            event.setGui(new CreamMainMenuScreen());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (CreamClientMod.clickGuiKey != null && CreamClientMod.clickGuiKey.consumeClick()) {
            if (mc.screen == null && mc.player != null) {
                mc.setScreen(new ClickGuiScreen());
            } else if (mc.screen instanceof ClickGuiScreen) {
                mc.setScreen(null);
            }
        }

        if (mc.player != null && ModuleManager.TOGGLE_SPRINT != null && ModuleManager.TOGGLE_SPRINT.isEnabled()) {
            if (mc.player.input != null && mc.player.input.forwardImpulse > 0 && !mc.player.isShiftKeyDown() && !mc.player.horizontalCollision) {
                mc.player.setSprinting(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            HudOverlay.render(
                    event.getMatrixStack(),
                    event.getWindow().getGuiScaledWidth(),
                    event.getWindow().getGuiScaledHeight()
            );
        }
    }
}
