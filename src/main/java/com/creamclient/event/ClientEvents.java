package com.creamclient.event;

import com.creamclient.CreamClientMod;
import com.creamclient.discord.DiscordRPC;
import com.creamclient.module.ModuleManager;
import com.creamclient.theme.CreamTheme;
import com.creamclient.ui.NotificationManager;
import com.creamclient.ui.ClickGuiScreen;
import com.creamclient.ui.CreamChatManager;
import com.creamclient.ui.CreamChatScreen;
import com.creamclient.ui.CreamMainMenuScreen;
import com.creamclient.ui.CreamMultiplayerScreen;
import com.creamclient.ui.CreamOptionsScreen;
import com.creamclient.ui.CreamPauseScreen;
import com.creamclient.ui.CreamSingleplayerScreen;
import com.creamclient.ui.HudOverlay;
import com.creamclient.visual.SkyRenderer;
import com.creamclient.visual.WaypointManager;
import com.creamclient.visual.WorldVisuals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.item.BlockItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientEvents {
    private float currentFov = 70.0f;
    private int tickCounter = 0;
    private int afkCounter = 0;
    private boolean mKeyDown = false;
    private boolean f7Down = false, f8Down = false, f9Down = false;
    private boolean wasDead = false;
    private final Map<com.creamclient.module.Module, Boolean> KEY_HELD = new HashMap<>();
    private long lastArmorAlert = 0L;
    private float freelookYaw = 0f;
    private boolean wasFreelooking = false;
    private final Map<String, Long> NEARBY_COOLDOWN = new HashMap<>();

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) CreamTheme.updateDelta();
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (ModuleManager.AUTO_RESPAWN.isEnabled() && event.getGui() instanceof DeathScreen) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) { mc.player.respawn(); event.setCanceled(true); return; }
        }
        if (ModuleManager.CUSTOM_CHAT.isEnabled() && event.getGui() instanceof ChatScreen && !(event.getGui() instanceof CreamChatScreen)) {
            event.setGui(new CreamChatScreen(""));
            return;
        }
        if (!ModuleManager.CUSTOM_MENUS.isEnabled()) return;

        if (event.getGui() instanceof MainMenuScreen) event.setGui(new CreamMainMenuScreen());
        else if (event.getGui() instanceof IngameMenuScreen) event.setGui(new CreamPauseScreen());
        else if (event.getGui() instanceof WorldSelectionScreen && !(Minecraft.getInstance().screen instanceof CreamSingleplayerScreen)) {
            event.setGui(new CreamSingleplayerScreen(Minecraft.getInstance().screen != null ? Minecraft.getInstance().screen : new CreamMainMenuScreen()));
        } else if (event.getGui() instanceof MultiplayerScreen && !(Minecraft.getInstance().screen instanceof CreamMultiplayerScreen)) {
            event.setGui(new CreamMultiplayerScreen(Minecraft.getInstance().screen != null ? Minecraft.getInstance().screen : new CreamMainMenuScreen()));
        } else if (event.getGui() instanceof OptionsScreen && !(Minecraft.getInstance().screen instanceof CreamOptionsScreen)) {
            event.setGui(new CreamOptionsScreen(Minecraft.getInstance().screen != null ? Minecraft.getInstance().screen : new CreamMainMenuScreen()));
        }
    }

    @SubscribeEvent
    public void onGuiBackground(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!ModuleManager.CUSTOM_MENUS.isEnabled()) return;
        if (event.getGui() instanceof CreamMainMenuScreen
                || event.getGui() instanceof CreamPauseScreen
                || event.getGui() instanceof CreamSingleplayerScreen
                || event.getGui() instanceof CreamMultiplayerScreen
                || event.getGui() instanceof CreamOptionsScreen
                || event.getGui() instanceof CreamChatScreen
                || event.getGui() instanceof ClickGuiScreen) return;

        MatrixStack ms = event.getMatrixStack();
        int w = event.getGui().width, h = event.getGui().height, accent = CreamTheme.getAccent();
        if (event.getGui() instanceof ContainerScreen) {
            if (ModuleManager.INV_STYLING.isEnabled()) {
                ContainerScreen<?> cs = (ContainerScreen<?>) event.getGui();
                int gx = cs.getGuiLeft(), gy = cs.getGuiTop(), gw = cs.getXSize(), gh = cs.getYSize();
                CreamTheme.drawBorderedRect(ms, gx - 4, gy - 16, gx + gw + 4, gy + gh + 4, 0xCC0B0F0B, accent);
                CreamTheme.drawText(ms, CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION, gx, gy - 12, accent);
            }
        } else {
            AbstractGui.fill(ms, 0, 0, w, h, 0x66060906);
            AbstractGui.fill(ms, 0, 0, w, 2, accent);
            CreamTheme.drawText(ms, CreamClientMod.CLIENT_NAME + " " + CreamClientMod.CLIENT_VERSION, 8, 6, accent);
        }
    }

    @SubscribeEvent
    public void onGuiDrawPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;
        if (ModuleManager.CUSTOM_CURSOR.isEnabled()) {
            GLFW.glfwSetInputMode(mc.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            CreamTheme.drawCustomCursor(event.getMatrixStack(), event.getMouseX(), event.getMouseY());
        } else {
            GLFW.glfwSetInputMode(mc.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        ITextComponent msg = event.getMessage();
        if (ModuleManager.CHAT_TIMESTAMPS.isEnabled()) {
            String ts = new SimpleDateFormat("HH:mm").format(new Date());
            msg = new StringTextComponent("§8[§a" + ts + "§8] §r").append(msg);
            event.setMessage(msg);
        }
        CreamChatManager.addMessage(msg);
    }

    @SubscribeEvent
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if (ModuleManager.NO_FIRE.isEnabled() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen == null) HudOverlay.registerClick(event.getButton());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide && event.getItemStack().getItem() instanceof BlockItem) {
            WorldVisuals.onBlockPlace(event.getPos().relative(event.getFace()));
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player) WorldVisuals.spawnHitSparks(event.getTarget());
    }

    @SubscribeEvent
    public void onBlockHighlight(DrawHighlightEvent.HighlightBlock event) {
        if (ModuleManager.BLOCK_OVERLAY.isEnabled()) {
            event.setCanceled(true);
            WorldVisuals.renderBlockHighlight(event.getMatrix(), event.getTarget().getBlockPos());
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (CreamClientMod.clickGuiKey != null && CreamClientMod.clickGuiKey.consumeClick()) {
            if (mc.screen == null && mc.player != null) mc.setScreen(new ClickGuiScreen());
            else if (mc.screen instanceof ClickGuiScreen) mc.setScreen(null);
        }
        if (mc.player != null) {
            long win = mc.getWindow().getWindow();
        // Module Keybinder
        if (mc.screen == null) {
            for (com.creamclient.module.Module m : ModuleManager.getModules()) {
                int k = m.getKey();
                if (k > 0) {
                    boolean pressed = GLFW.glfwGetKey(win, k) == GLFW.GLFW_PRESS;
                    boolean prev = KEY_HELD.getOrDefault(m, false);
                    if (pressed && !prev) {
                        m.toggle();
                        com.creamclient.config.CreamConfig.save();
                    }
                    KEY_HELD.put(m, pressed);
                }
            }
        }

        // ArmorAlert (<15% durability)
        if (ModuleManager.ARMOR_ALERT != null && ModuleManager.ARMOR_ALERT.isEnabled() && mc.player != null) {
            long nowA = System.currentTimeMillis();
            if (nowA - lastArmorAlert > 12000L) {
                for (net.minecraft.item.ItemStack st : mc.player.inventory.armor) {
                    if (!st.isEmpty() && st.isDamageableItem()) {
                        float pct = 1.0f - (float) st.getDamageValue() / (float) st.getMaxDamage();
                        if (pct < 0.15f) {
                            lastArmorAlert = nowA;
                            NotificationManager.push("Armor Low", st.getHoverName().getString() + " (" + (int) (pct * 100) + "%)", 0xFFFF5555);
                            break;
                        }
                    }
                }
            }
        }

        // Global Music Hotkeys (F7 Prev, F8 Play/Pause, F9 Next)
        if (ModuleManager.MEDIA_HOTKEYS != null && ModuleManager.MEDIA_HOTKEYS.isEnabled() && mc.screen == null) {
            boolean p7 = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F7) == GLFW.GLFW_PRESS;
            boolean p8 = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS;
            boolean p9 = GLFW.glfwGetKey(win, GLFW.GLFW_KEY_F9) == GLFW.GLFW_PRESS;
            if (p7 && !f7Down) { com.creamclient.media.MediaManager.prevTrack(); NotificationManager.push("Music", com.creamclient.media.MediaManager.getCurrentTrackName(), CreamTheme.getAccent()); }
            if (p8 && !f8Down) { com.creamclient.media.MediaManager.togglePause(); NotificationManager.push("Music", com.creamclient.media.MediaManager.isPlaying() ? "Playing" : "Paused", CreamTheme.getAccent()); }
            if (p9 && !f9Down) { com.creamclient.media.MediaManager.nextTrack(); NotificationManager.push("Music", com.creamclient.media.MediaManager.getCurrentTrackName(), CreamTheme.getAccent()); }
            f7Down = p7; f8Down = p8; f9Down = p9;
        }

        // DeathCoords auto-waypoint
        if (ModuleManager.DEATH_COORDS != null && ModuleManager.DEATH_COORDS.isEnabled() && mc.player != null) {
            boolean dead = mc.player.isDeadOrDying();
            if (dead && !wasDead) {
                WaypointManager.addWaypoint("Death", mc.player.getX(), mc.player.getY() + 1.0, mc.player.getZ(), WaypointManager.getCurrentDimension(), 0xFFFF4444);
                NotificationManager.push("Death", (int) mc.player.getX() + ", " + (int) mc.player.getY() + ", " + (int) mc.player.getZ(), 0xFFFF4444);
            }
            wasDead = dead;
        }

        long winCheck = win;
        // Waypoint quick key (M)
        if (mc.screen == null && GLFW.glfwGetKey(win, GLFW.GLFW_KEY_M) == GLFW.GLFW_PRESS) {
            if (!mKeyDown) {
                mKeyDown = true;
                WaypointManager.addQuickWaypoint();
            }
        } else {
            mKeyDown = false;
        }

        // Nearby Players radar alert
        if (ModuleManager.NEARBY_PLAYERS != null && ModuleManager.NEARBY_PLAYERS.isEnabled() && mc.level != null && mc.player != null) {
            double rad = ModuleManager.NEARBY_RADIUS != null ? ModuleManager.NEARBY_RADIUS.getValue() : 150.0;
            long nowMs = System.currentTimeMillis();
            for (PlayerEntity p : mc.level.players()) {
                if (p == mc.player) continue;
                int dist = (int) mc.player.distanceTo(p);
                if (dist <= rad) {
                    String pname = p.getGameProfile().getName();
                    Long last = NEARBY_COOLDOWN.get(pname);
                    if (last == null || nowMs - last > 15000L) {
                        NEARBY_COOLDOWN.put(pname, nowMs);
                        if (ModuleManager.NEARBY_ALERT == null || ModuleManager.NEARBY_ALERT.isEnabled()) {
                            NotificationManager.push("Nearby Player", pname + " (" + dist + "m)", 0xFFFACC15);
                        }
                        if (ModuleManager.NEARBY_SOUND != null && ModuleManager.NEARBY_SOUND.isEnabled()) {
                            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.45f, 1.25f);
                        }
                    }
                }
            }
        }

        if (ModuleManager.TOGGLE_SPRINT.isEnabled() && mc.player.input != null && mc.player.input.forwardImpulse > 0
                    && !mc.player.isShiftKeyDown() && !mc.player.horizontalCollision) {
                mc.player.setSprinting(true);
            }
            if (ModuleManager.TOGGLE_SNEAK.isEnabled()) KeyBinding.set(mc.options.keyShift.getKey(), true);
            if (ModuleManager.ANTI_AFK.isEnabled()) {
                afkCounter++;
                if (afkCounter >= 600) { afkCounter = 0; mc.player.jumpFromGround(); }
            }
        }
        if (mc.level != null) {
            if (ModuleManager.TIME_CHANGER.isEnabled()) {
                long time = 18000L;
                switch (ModuleManager.TIME_MODE.getMode()) {
                    case "Day": time = 6000L; break;
                    case "Sunset": time = 12500L; break;
                    case "Midnight": time = 18000L; break;
                    default: time = 14000L; break;
                }
                mc.level.setDayTime(time);
            }
            if (ModuleManager.NO_WEATHER.isEnabled()) { mc.level.setRainLevel(0.0f); mc.level.setThunderLevel(0.0f); }
        }
        tickCounter++;
        if (tickCounter >= 20) { tickCounter = 0; DiscordRPC.updateStateFromMainThread(); }
        WorldVisuals.onClientTick();
    }

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && ModuleManager.HUD.isEnabled() && ModuleManager.CUSTOM_HOTBAR.isEnabled()) {
            event.setCanceled(true);
            HudOverlay.renderCustomHotbar(event.getMatrixStack(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && ModuleManager.HUD.isEnabled() && ModuleManager.CROSSHAIR.isEnabled()) {
            event.setCanceled(true);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT && ModuleManager.HUD.isEnabled() && ModuleManager.CUSTOM_CHAT.isEnabled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderOverlayPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            HudOverlay.render(event.getMatrixStack(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight());
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        WorldVisuals.render(event.getMatrixStack(), event.getPartialTicks());
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (ModuleManager.CUSTOM_SKY.isEnabled() && ModuleManager.SKY_SYNC_FOG.isEnabled()) {
            int c = SkyRenderer.getSkyColorInt();
            event.setRed(((c >> 16) & 0xFF) / 255.0f);
            event.setGreen(((c >> 8) & 0xFF) / 255.0f);
            event.setBlue((c & 0xFF) / 255.0f);
            return;
        }
        if (ModuleManager.CUSTOM_FOG.isEnabled()) {
            if (ModuleManager.FOG_SYNC.isEnabled()) {
                int c = CreamTheme.getAccent();
                event.setRed(((c >> 16) & 0xFF) / 255.0f);
                event.setGreen(((c >> 8) & 0xFF) / 255.0f);
                event.setBlue((c & 0xFF) / 255.0f);
            } else {
                event.setRed(ModuleManager.FOG_R.getFloat());
                event.setGreen(ModuleManager.FOG_G.getFloat());
                event.setBlue(ModuleManager.FOG_B.getFloat());
            }
        }
    }

    @SubscribeEvent
    public void onFov(EntityViewRenderEvent.FOVModifier event) {
        if (ModuleManager.ZOOM.isEnabled() && CreamClientMod.zoomKey != null && CreamClientMod.zoomKey.isDown()) {
            currentFov = CreamTheme.smooth(currentFov, ModuleManager.ZOOM_FOV.getFloat(), 0.3f);
            event.setFOV(currentFov);
        } else if (ModuleManager.DYNAMIC_FOV != null && ModuleManager.DYNAMIC_FOV.isEnabled() && ModuleManager.BASE_FOV != null) {
            currentFov = CreamTheme.smooth(currentFov, ModuleManager.BASE_FOV.getFloat(), 0.25f);
            event.setFOV(currentFov);
        } else {
            currentFov = (float) event.getFOV();
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getInstance();
        if (ModuleManager.NO_HURT_CAM.isEnabled() && mc.player != null) {
            mc.player.hurtTime = 0;
        }
        if (ModuleManager.FREELOOK != null && ModuleManager.FREELOOK.isEnabled() && mc.screen == null && mc.player != null) {
            long w = mc.getWindow().getWindow();
            if (GLFW.glfwGetKey(w, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS) {
                if (!wasFreelooking) { wasFreelooking = true; freelookYaw = event.getYaw(); }
                freelookYaw += 0.6f;
                event.setYaw(freelookYaw);
            } else {
                wasFreelooking = false;
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if (ModuleManager.VIEW_MODEL.isEnabled()) {
            MatrixStack ms = event.getMatrixStack();
            ms.translate(ModuleManager.HAND_X.getValue(), ModuleManager.HAND_Y.getValue(), ModuleManager.HAND_Z.getValue());
            float rx = ModuleManager.HAND_ROT_X.getFloat();
            float ry = ModuleManager.HAND_ROT_Y.getFloat();
            float rz = ModuleManager.HAND_ROT_Z.getFloat();
            if (ModuleManager.HAND_SPIN.isEnabled()) {
                ry += (System.currentTimeMillis() % 3600L) / 10.0f;
            }
            if (rx != 0.0f) ms.mulPose(Vector3f.XP.rotationDegrees(rx));
            if (ry != 0.0f) ms.mulPose(Vector3f.YP.rotationDegrees(ry));
            if (rz != 0.0f) ms.mulPose(Vector3f.ZP.rotationDegrees(rz));
            float s = ModuleManager.HAND_SCALE.getFloat();
            ms.scale(s, s, s);
        }
    }
}
