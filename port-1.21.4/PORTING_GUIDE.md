# CreamClient Forge 1.21.4

Папка `port-1.21.4/` настроена как отдельный Gradle-проект под **Forge 1.21.4-54.1.0 (Java 21, ForgeGradle 6)** и собирается автоматически воркфлоу `.github/workflows/build-1.21.4.yml`.

Основные отличия API при расширении модулей из 1.16.5:
1. `MatrixStack` -> `com.mojang.blaze3d.vertex.PoseStack`
2. `Screen.render(MatrixStack, ...)` -> `Screen.render(GuiGraphics, int mouseX, int mouseY, float partialTick)` (`guiGraphics.pose()`)
3. `ResourceLocation` -> `ResourceLocation.fromNamespaceAndPath("creamclient", ...)`
4. `RenderWorldLastEvent` -> `RenderLevelStageEvent` (`Stage.AFTER_SKY` / `Stage.AFTER_PARTICLES`)
5. `EntityViewRenderEvent` -> `ViewportEvent` (`ComputeFogColor`, `ComputeFov`, `ComputeCameraAngles`)
