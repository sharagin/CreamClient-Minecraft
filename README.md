[English](#english) | [Русский](#русский)

# CreamClient v1.0













---

&lt;a name=&quot;english&quot;&gt;&lt;/a&gt;
## English

Lime-citrus visual, HUD, and utility client for **Minecraft Forge** with a bilingual (`RU / EN`) Linux-style control panel, custom `FS Picto` banner typography, interactive drag-and-drop HUD constructor, seekable music player, 3D waypoints, player proximity radar, and Discord Rich Presence.

### Complete Feature List (A to Z)

#### 1. Visuals, Camera & 3D World Effects
| Module | Description |
| --- | --- |
| `BlockOverlay` | Glowing 3D highlight around targeted blocks |
| `BoxESP` | Clean 3D wireframe boxes around players and mobs |
| `Breadcrumbs` | Long glowing 3D path line behind movement |
| `ChinaHat` | 3D glowing cone above the player head in F5 |
| `CrystalOrbit` | Orbiting 3D crystals around the player in F5 |
| `CustomFog` | Custom RGB or theme-synced world fog tint |
| `CustomSky` | 20 customizable animated skies (RGB, alpha, spin, stars, aurora, fog-sync) |
| `DamagePopups` | Floating 3D hit indicators on entity attack |
| `EntityGlowRing` | Glowing 3D ring under nearby living entities |
| `FootstepGlow` | Glowing 3D accent particles at feet while moving |
| `FreeLook` | 360-degree camera look while holding `Left Alt` |
| `Fullbright` | Maximum gamma brightness everywhere |
| `Halo` | Rotating 3D angel halo above the player in F5 |
| `HandVisuals` | First-person hand X/Y/Z offset, 3D rotation, scale, and item spin |
| `HitParticles` | Glowing 3D sparks on entity attack |
| `HitShockwave` | Expanding 3D shockwave ring on attack |
| `JumpCircles` | Concentric expanding 3D rings when jumping |
| `NoFireOverlay` | Hides screen fire overlay |
| `NoHurtCam` | Disables camera shake when taking damage |
| `NoWeather` | Visually disables rain and snow |
| `OrbitalRings` | 3D gyroscopic orbits in F5 |
| `ParticleAura` | Floating 3D fireflies around the player |
| `PlayerAura` | Glowing 3D aura circle under feet |
| `ProjectileTrails` | Glowing 3D trails behind arrows, pearls, and projectiles |
| `PulseRings` | 3D expanding shockwave rings around the player |
| `StarField3D` | Floating 3D ambient stars orbiting around the player |
| `StepCircles` | Small glowing rings while walking |
| `TargetCrown` | 3D crown above the looked-at target |
| `TargetRing` | Animated 3D ring on the looked-at target |
| `TimeChanger` | Client-side sky time override |
| `Tracers` | 3D lines to nearby players |
| `Trails` | Glowing 3D ribbon behind the player |
| `Waypoints` | 3D world beacons and floating distance tags (quick key `M`) |
| `Wings` | Animated 3D geometric wings in F5 |
| `WorldGrid` | Neon cyber grid projected under the player |
| `Zoom` | Smooth FOV zoom on the `C` key |

#### 2. HUD & Interactive UI Constructor
- **UI Constructor**: Drag any widget (`Watermark`, `InfoHUD`, `MusicPlayerHUD`, `NearbyPlayers`, `WaypointsHUD`, `Keystrokes`, `SessionStats`, `ArmorHUD`) on a snap grid, scroll to resize (`0.50x` to `2.20x`), right-click to toggle, and middle-click to switch `Full / Compact` style.
- `ArmorHUD` / `PotionHUD` / `CompassHUD` / `Coordinates` / `Crosshair` / `FPSGraph` / `Keystrokes` / `ModuleList` / `SessionInfo` / `TargetHUD` / `RadarHUD` / `ScreenGlow` / `Watermark`.
- `CustomChat`: Smooth animated, draggable, rounded chat window.
- `CustomHotbar`: Sleek animated hotbar replacement.
- `MusicPlayerHUD`: Visible on-screen music player with animated 8-bar equalizer, progress bar, and track timer.
- `NearbyPlayers`: Proximity radar and toast/sound alerts when players enter radius (default `150m`), showing distance in meters, health, and direction arrow.
- `Notifications`: Smooth slide-in popup notifications.

#### 3. Movement & Utility
- `AntiAFK`, `AutoRespawn`, `ChatTime` (`[HH:MM]` prefix), `ToggleSneak`, `ToggleSprint`.

#### 4. Control Panel, Media, Linux Config & Discord RPC
- **Unified Control Panel (`Right Shift`)**: Bilingual `RU / EN` switch in header, live search, active module counter, and 8 tabs (`Visuals`, `Sky & World`, `Hand & Camera`, `Waypoints & Radar`, `Music Player`, `HUD Constructor`, `Linux Config`, `Profiles`).
- **Music Player**: Play/Pause, Prev/Next, Stop, Volume, `-10s` / `+10s` seek buttons, clickable progress bar scrubbing, and `.wav` track list (`creamclient/music/`).
- **Custom Menus**: Main Menu with `FS Picto` `"Cream"` banner, animated splash screen, custom `.png`/`.jpg` backgrounds (`creamclient/backgrounds/`), Multiplayer screen with signal-bar ping indicators, Singleplayer, Pause, and Options.
- **Linux Config & Profiles**: Accent RGB, Palette presets, Color Modes (`Breathing`, `Rainbow`, `Static`, `CustomRGB`), Corner Radius, Border/Panel Alpha, Animation Speed, Font Scale, HUD Padding, Glow Alpha, and named `.json` config profiles (`config/creamclient/configs/`).
- **Discord RPC**: Native Windows IPC (`App ID: 1571542048828293302`) showing active track, module count, player nick, server IP, and `GitHub` / `Download CreamClient` buttons.

### Download & Build
- **Pre-built JARs**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`

---

&lt;a name=&quot;русский&quot;&gt;&lt;/a&gt;
## Русский

Лаймово-цитрусовый визуальный и HUD-клиент для **Minecraft Forge** с двуязычной (`RU / EN`) панелью управления в стиле Linux, геометрическим шрифтом `FS Picto` в баннере, интерактивным конструктором интерфейсов, музыкальным плеером с перемоткой, 3D-метками, радаром игроков и Discord Rich Presence.

### Полный список функций (от А до Я)

#### 1. Визуалы, камера и 3D-эффекты мира
| Модуль | Описание |
| --- | --- |
| `BlockOverlay` | Светящаяся 3D-подсветка выбранного блока |
| `BoxESP` | Чистые 3D-рамки вокруг игроков и мобов |
| `Breadcrumbs` | Длинная светящаяся 3D-линия пути за игроком |
| `ChinaHat` | Светящийся 3D-конус над головой в режиме F5 |
| `CrystalOrbit` | Вращающиеся 3D-кристаллы вокруг игрока в F5 |
| `CustomFog` | Покраска тумана мира в RGB или цвет темы |
| `CustomSky` | 20 настраиваемых анимированных небес (RGB, прозрачность, вращение, звёзды, сияние, синхронизация с туманом) |
| `DamagePopups` | Всплывающие 3D-индикаторы попадания при ударе |
| `EntityGlowRing` | Светящееся 3D-кольцо под ближайшими существами |
| `FootstepGlow` | Светящиеся акцентные частицы под ногами при движении |
| `FreeLook` | Свободный обзор камерой на 360 градусов при удержании `Left Alt` |
| `Fullbright` | Максимальная яркость освещения везде |
| `Halo` | Вращающийся 3D-нимб над головой в F5 |
| `HandVisuals` | Настройка позиции руки (X/Y/Z), 3D-поворота, масштаба и вращения предмета |
| `HitParticles` | Светящиеся 3D-искры при ударе по сущности |
| `HitShockwave` | Расходящееся 3D-кольцо ударной волны при атаке |
| `JumpCircles` | Концентрические расходящиеся 3D-кольца при прыжке |
| `NoFireOverlay` | Убирает визуальное наложение огня на экран |
| `NoHurtCam` | Отключает тряску камеры при получении урона |
| `NoWeather` | Визуально отключает дождь и снег |
| `OrbitalRings` | 3D-гироскопические орбиты вокруг игрока в F5 |
| `ParticleAura` | Летающие 3D-светлячки вокруг игрока |
| `PlayerAura` | Светящийся 3D-круг ауры под ногами |
| `ProjectileTrails` | Светящиеся 3D-шлейфы за стрелами, эндер-жемчугом и снарядами |
| `PulseRings` | Расходящиеся 3D-кольца импульса вокруг игрока |
| `StarField3D` | Парящие 3D-звёзды вокруг игрока в мире |
| `StepCircles` | Небольшие светящиеся кольца при ходьбе |
| `TargetCrown` | 3D-корона над целью под прицелом |
| `TargetRing` | Анимированное 3D-кольцо на цели под прицелом |
| `TimeChanger` | Клиентская смена времени суток |
| `Tracers` | 3D-линии к ближайшим игрокам |
| `Trails` | Светящийся 3D-шлейф за игроком |
| `Waypoints` | 3D-метки с лучом и дистанцией в метрах (быстрая клавиша `M`) |
| `Wings` | Анимированные геометрические 3D-крылья в F5 |
| `WorldGrid` | Неоновая кибер-сетка на земле под игроком |
| `Zoom` | Плавное приближение камеры на клавишу `C` |

#### 2. HUD и интерактивный конструктор интерфейсов
- **Конструктор интерфейсов**: Перетаскивай мышью любые виджеты (`Watermark`, `InfoHUD`, `MusicPlayerHUD`, `NearbyPlayers`, `WaypointsHUD`, `Keystrokes`, `SessionStats`, `ArmorHUD`) по сетке, меняй масштаб колесиком (`0.50x`–`2.20x`), включай/выключай на ПКМ и меняй стиль `Full / Compact` на СКМ.
- `ArmorHUD` / `PotionHUD` / `CompassHUD` / `Coordinates` / `Crosshair` / `FPSGraph` / `Keystrokes` / `ModuleList` / `SessionInfo` / `TargetHUD` / `RadarHUD` / `ScreenGlow` / `Watermark`.
- `CustomChat`: Плавный, скруглённый и перетаскиваемый чат.
- `CustomHotbar`: Анимированный хотбар в стиле клиента.
- `MusicPlayerHUD`: Видимый наэкранный музыкальный плеер с 8-полосным эквалайзером, прогресс-баром и таймером трека.
- `NearbyPlayers`: Радар игроков в заданном радиусе (по умолчанию `150m`) с уведомлениями, звуком, расстоянием в метрах, здоровьем и стрелкой направления.
- `Notifications`: Плавные всплывающие уведомления.

#### 3. Движение и утилиты
- `AntiAFK`, `AutoRespawn`, `ChatTime` (префикс `[ЧЧ:ММ]`), `ToggleSneak`, `ToggleSprint`.

#### 4. Панель управления, плеер, настройки Linux и Discord RPC
- **Единая панель управления (`Right Shift`)**: Переключатель `RU / EN` в шапке, поиск, счётчик активных модулей и 8 вкладок (`Визуалы`, `Небо и Мир`, `Рука и Камера`, `Метки и Радар`, `Муз. Плеер`, `Конструктор HUD`, `Настройки Linux`, `Профили`).
- **Музыкальный плеер**: Play/Pause, Prev/Next, Stop, громкость, перемотка `-10s` / `+10s`, перемотка кликом по прогресс-бару и список `.wav` треков (`creamclient/music/`).
- **Кастомные меню**: Главное меню с баннером «Cream» (шрифт `FS Picto`), анимированный экран загрузки, свои `.png`/`.jpg` фоны (`creamclient/backgrounds/`), прокачанный экран мультиплеера с индикатором сигнала.
- **Настройки Linux и профили**: Accent RGB, палитры, режимы цвета (`Breathing`, `Rainbow`, `Static`, `CustomRGB`), скругление углов, прозрачность рамок и панелей, скорость анимаций, масштаб шрифта, отступы HUD, свечение и сохранение `.json` профилей в `config/creamclient/configs/`.
- **Discord RPC**: Нативный Windows IPC (`App ID: 1571542048828293302`) с отображением текущего трека, числа модулей, ника, сервера и кнопками `GitHub` и `Download CreamClient`.

### Скачивание и сборка
- **Готовые сборки**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`
