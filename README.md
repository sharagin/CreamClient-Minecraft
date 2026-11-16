[English](#english) | [Русский](#русский)

# CreamClient v1.0













---

&lt;a name=&quot;english&quot;&gt;&lt;/a&gt;
## English

Lime-citrus visual, HUD, and utility client for **Minecraft Forge** with a unified Linux-style control panel, deep customization, interactive drag-and-drop HUD editor, visible music player, waypoints, and player proximity radar.

### Complete Feature List (A to Z)

#### 1. Visuals & 3D World Effects
| Module | Description |
| --- | --- |
| `BlockOverlay` | Glowing 3D highlight around targeted blocks |
| `BoxESP` | Clean 3D wireframe boxes around players and mobs |
| `Breadcrumbs` | Long glowing 3D path line behind movement |
| `ChinaHat` | 3D glowing cone above the player head in third-person |
| `CrystalOrbit` | Orbiting 3D crystals around the player in F5 |
| `CustomFog` | Custom RGB or theme-synced world fog tint |
| `CustomSky` | 20 customizable animated skies (RGB, alpha, spin, stars, aurora, fog-sync) |
| `Fullbright` | Maximum gamma brightness everywhere |
| `Halo` | Rotating 3D angel halo above the player in F5 |
| `HandVisuals` | First-person hand X/Y/Z offset, 3D rotation, scale, and item spin |
| `HitParticles` | Glowing 3D sparks on entity attack |
| `JumpCircles` | Concentric expanding 3D rings when jumping |
| `NoFireOverlay` | Hides screen fire overlay |
| `NoHurtCam` | Disables camera shake when taking damage |
| `NoWeather` | Visually disables rain and snow |
| `OrbitalRings` | 3D gyroscopic orbits in F5 |
| `ParticleAura` | Floating 3D fireflies around the player |
| `PlayerAura` | Glowing 3D aura circle under feet |
| `PulseRings` | 3D expanding shockwave rings around the player |
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

#### 2. HUD & Interactive Layout
- **Interactive HUD Layout Editor**: Drag any HUD widget (`Watermark`, `InfoHUD`, `MusicPlayerHUD`, `NearbyPlayers`, `WaypointsHUD`) across a snap grid and scroll to resize.
- `ArmorHUD`: Equipped armor durability display.
- `CompassHUD`: Top directional compass bar.
- `Coordinates`: Live XYZ coordinates and biome display.
- `Crosshair`: Minimalist accent crosshair.
- `CustomChat`: Smooth animated, draggable, rounded chat window.
- `CustomHotbar`: Sleek animated hotbar replacement.
- `FPSGraph`: Live FPS chart monitor.
- `HUD`: Master toggle for all HUD overlays.
- `Keystrokes`: Animated WASD and CPS overlay.
- `ModuleList`: Active modules list with accent bar.
- `MusicPlayerHUD`: Visible on-screen music player with animated 8-bar equalizer, progress bar, and track timer.
- `NearbyPlayers`: Proximity radar and toast/sound alerts when players enter radius (default `150m`), showing distance in meters, health, and direction arrow.
- `Notifications`: Smooth slide-in popup notifications.
- `PotionHUD`: Active potion effect timers.
- `RadarHUD`: Mini-radar for nearby entities.
- `ScreenGlow`: Soft accent glow around screen edges.
- `SessionInfo`: Play time and session statistics.
- `TargetHUD`: Health and distance card for the current target.
- `Watermark`: Top-left brand pill with FPS and ping.

#### 3. Movement & Utility
- `AntiAFK`: Prevents idle kick on servers.
- `AutoRespawn`: Automatically respawns on death.
- `ChatTime`: Adds `[HH:MM]` timestamps to chat messages.
- `ToggleSneak`: Keeps sneaking without holding Shift.
- `ToggleSprint`: Auto-sprint when moving forward.

#### 4. Interface, Media, Linux Config & Profiles
- **Unified Control Panel (`Right Shift`)**: Sidebar-based panel with search, smooth animations, and 8 tabs (`Visuals`, `Sky & World`, `Hand & Camera`, `Waypoints & Radar`, `Music Player`, `HUD & Layout`, `Linux Config`, `Profiles`).
- `Appearance`: Palette presets (`Lime`, `Citrus`, `Mint`, `CyberCyan`, `Amethyst`, `Crimson`, `CreamGold`), Color Mode (`Breathing`, `Rainbow`, `Static`, `CustomRGB`), Custom R/G/B, Corner Radius, Border Alpha, Panel Alpha, Animation Speed, Font Scale, HUD Padding, Glow Alpha, Splash Screen, and Language (`RU` / `EN` / `UK`).
- `BlackCursor`: Custom black gaming cursor in GUI menus.
- `CustomMenus`: Custom Main Menu (with `"Cream"` banner and top icon), Singleplayer, Multiplayer (live server pinger), Pause, Options, and Inventory styling.
- `Background`: Wallpaper, animated constellation grid, or custom `.png`/`.jpg` images from `creamclient/backgrounds/`.
- `MenuMusic`: Built-in 16-bit PCM `.wav` music player (`Play/Pause`, `Prev`, `Next`, `Stop`, `Volume`, track browser for `creamclient/music/`).
- **Config Profiles**: Save, load, delete, and reset named `.json` config profiles in `config/creamclient/configs/`.
- `DiscordRPC`: Native Windows IPC integration (`App ID: 1571542048828293302`) showing server/menu state with `GitHub` and `Download CreamClient` buttons.

### Download & Build
- **Pre-built JARs**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`

---

&lt;a name=&quot;русский&quot;&gt;&lt;/a&gt;
## Русский

Лаймово-цитрусовый визуальный и HUD-клиент для **Minecraft Forge** с единой панелью управления в стиле Linux, глубокой кастомизацией, интерактивным редактором HUD, видимым музыкальным плеером, метками и радаром игроков.

### Полный список функций (от А до Я)

#### 1. Визуалы и 3D-эффекты мира
| Модуль | Описание |
| --- | --- |
| `BlockOverlay` | Светящаяся 3D-подсветка выбранного блока |
| `BoxESP` | Чистые 3D-рамки вокруг игроков и мобов |
| `Breadcrumbs` | Длинная светящаяся 3D-линия пути за игроком |
| `ChinaHat` | Светящийся 3D-конус над головой в режиме F5 |
| `CrystalOrbit` | Вращающиеся 3D-кристаллы вокруг игрока в F5 |
| `CustomFog` | Покраска тумана мира в RGB или цвет темы |
| `CustomSky` | 20 настраиваемых анимированных небес (RGB, прозрачность, вращение, звёзды, сияние, синхронизация с туманом) |
| `Fullbright` | Максимальная яркость освещения везде |
| `Halo` | Вращающийся 3D-нимб над головой в F5 |
| `HandVisuals` | Настройка позиции руки (X/Y/Z), 3D-поворота, масштаба и вращения предмета |
| `HitParticles` | Светящиеся 3D-искры при ударе по сущности |
| `JumpCircles` | Концентрические расходящиеся 3D-кольца при прыжке |
| `NoFireOverlay` | Убирает визуальное наложение огня на экран |
| `NoHurtCam` | Отключает тряску камеры при получении урона |
| `NoWeather` | Визуально отключает дождь и снег |
| `OrbitalRings` | 3D-гироскопические орбиты вокруг игрока в F5 |
| `ParticleAura` | Летающие 3D-светлячки вокруг игрока |
| `PlayerAura` | Светящийся 3D-круг ауры под ногами |
| `PulseRings` | Расходящиеся 3D-кольца импульса вокруг игрока |
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

#### 2. HUD и интерактивный макет
- **Интерактивный редактор HUD**: Перетаскивай мышью любые виджеты (`Watermark`, `InfoHUD`, `MusicPlayerHUD`, `NearbyPlayers`, `WaypointsHUD`) по сетке и меняй их масштаб колесиком.
- `ArmorHUD`: Отображение прочности надетой брони.
- `CompassHUD`: Верхняя полоса компаса с направлениями.
- `Coordinates`: Текущие координаты XYZ и биом.
- `Crosshair`: Минималистичный прицел с акцентом темы.
- `CustomChat`: Плавный, скруглённый и перетаскиваемый чат.
- `CustomHotbar`: Анимированный хотбар в стиле клиента.
- `FPSGraph`: График FPS в реальном времени.
- `HUD`: Общий переключатель всех HUD-виджетов.
- `Keystrokes`: Анимированные клавиши WASD и счётчик CPS.
- `ModuleList`: Список активных модулей с акцентной полосой.
- `MusicPlayerHUD`: Видимый наэкранный музыкальный плеер с 8-полосным эквалайзером, прогресс-баром и таймером трека.
- `NearbyPlayers`: Радар игроков в заданном радиусе (по умолчанию `150m`) с уведомлениями, звуком, расстоянием в метрах, здоровьем и стрелкой направления.
- `Notifications`: Плавные всплывающие уведомления.
- `PotionHUD`: Таймеры активных эффектов зелий.
- `RadarHUD`: Мини-радар ближайших сущностей.
- `ScreenGlow`: Мягкое свечение по краям экрана.
- `SessionInfo`: Время игры и статистика сессии.
- `TargetHUD`: Панель здоровья и дистанции до цели.
- `Watermark`: Верхний бренд-виджет с FPS и пингом.

#### 3. Движение и утилиты
- `AntiAFK`: Защита от кика за бездействие на серверах.
- `AutoRespawn`: Автоматическое возрождение после смерти.
- `ChatTime`: Добавляет время `[ЧЧ:ММ]` к сообщениям чата.
- `ToggleSneak`: Постоянный присед без удержания Shift.
- `ToggleSprint`: Автоматический бег при движении вперёд.

#### 4. Интерфейс, медиа, настройки Linux и профили
- **Единая панель управления (`Right Shift`)**: Боковое меню, поиск, плавные анимации и 8 вкладок (`Visuals`, `Sky & World`, `Hand & Camera`, `Waypoints & Radar`, `Music Player`, `HUD & Layout`, `Linux Config`, `Profiles`).
- `Appearance`: Палитры (`Lime`, `Citrus`, `Mint`, `CyberCyan`, `Amethyst`, `Crimson`, `CreamGold`), режимы цвета (`Breathing`, `Rainbow`, `Static`, `CustomRGB`), ручной RGB, радиус скругления, прозрачность рамок и панелей, скорость анимаций, масштаб шрифта, отступы HUD, свечение, загрузочный экран (`Splash Screen`) и язык (`RU` / `EN` / `UK`).
- `BlackCursor`: Кастомный чёрный игровой курсор в меню.
- `CustomMenus`: Кастомное главное меню (с баннером «Cream» и логотипом), экраны одиночной игры, мультиплеера (с пингером серверов), паузы, настроек и инвентаря.
- `Background`: Обои, анимированная сетка созвездий или свои `.png`/`.jpg` из `creamclient/backgrounds/`.
- `MenuMusic`: Встроенный плеер для 16-bit PCM `.wav` треков (`Play/Pause`, `Prev`, `Next`, `Stop`, громкость, список треков из `creamclient/music/`).
- **Профили конфигов**: Создание, загрузка, удаление и сброс именованных `.json` профилей в `config/creamclient/configs/`.
- `DiscordRPC`: Нативный Windows IPC (`App ID: 1571542048828293302`) со статусом сервера/меню и кнопками `GitHub` и `Download CreamClient`.

### Скачивание и сборка
- **Готовые сборки**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`
