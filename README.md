

[English](#english) | [Русский](#русский)

# CreamClient v1.0













---

&lt;a name=&quot;english&quot;&gt;&lt;/a&gt;
## English

**CreamClient v1.0** is a lime-citrus visual, HUD, cosmetic, and performance client mod for **Minecraft Forge**. It combines a unified Linux-style control panel (`Right Shift`) in Russian and English, 54 built-in 3D cosmetics, configurable weak-PC optimization, a drag-and-drop UI constructor, a seekable `.wav` music player, instant resource pack switching, and native Discord Rich Presence.

### Key Highlights

- **Unified Control Panel (`Right Shift`)**: 11 organized tabs (`Visuals`, `Cosmetics (54)`, `Sky & World`, `Hand & Camera`, `Waypoints & Radar`, `Music Player`, `HUD Constructor`, `Color Studio`, `Resource Packs`, `Linux Config`, `Profiles`), instant `RU / EN` switch, live search, per-module keybinder (`[Key]`), and `glScissor`-clipped smooth scrolling.
- **54 Built-In 3D Cosmetics**: Mix and match head accessories (cat ears, devil horns, angel halo, royal crown, cyber visor, wizard hat), wings and back gear (angel, dragon, cyber, butterfly, crystal wings, neon katana, capes), orbiting pets (floating cube, mini moon, lime drone, ghost, pixel heart), ground auras/shields, and ambient particles.
- **Advanced Visuals & Smooth Combat Feel**:
  - `SmoothSwing`: Fluid attack hand animation with 5 swing styles (`Smooth`, `1.7 Old`, `Spin`, `Push`, `Wave`), adjustable speed, and swing power.
  - `HandVisuals`: Full first-person X/Y/Z offset, 3D rotation, scale, and item spin.
  - `CustomSky`: 20 animated sky styles with custom RGB, alpha, rotation speed, stars, aurora, and fog sync.
  - Signature world effects: `BlockBreathe` (2s pulsing outline on placed blocks), `RainPuddle` (reflective sky puddle after 5s in rain), `Projectile2DTrail` (200ms screen-space brush trail), `WeatherMiniOrb`, `JumpCircles`, `PulseRings`, `WorldGrid`, `StarField3D`, `FootstepGlow`, `DamagePopups`, `HitShockwave`, `FreeLook` (`Left Alt`), and `ProjectileTrails`.
- **Weak-PC Optimization (`PotatoOptimizer`)**: Fine-tune render distance (`2-16` chunks), entity distance (`50%-100%`), particles, graphics mode, smooth lighting, clouds, mipmaps, and max FPS live without restarting.
- **Waypoints & Proximity Radar**: Drop 3D beacon waypoints (`M` key) with distance tags, auto-save red `Death` waypoints on death (`DeathCoords`), and get alerts when players enter a configurable radius (`NearbyPlayers`, default `150m`).
- **Music Player & Resource Packs**:
  - Built-in `.wav` player with clickable progress-bar seeking, `-10s`/`+10s` scrubbing, `F7`/`F8`/`F9` global hotkeys, main menu mini-player, and visible HUD equalizer.
  - Instant in-game Resource Pack manager (`creamclient` / `resourcepacks/`) that toggles and reloads packs immediately.
- **UI Constructor, Color Studio & Profiles**: Drag, resize (`0.50x-2.20x`), nudge by `1px` with arrow keys, and switch `Full / Compact` styles for all 9 HUD widgets; create and save custom `#HEX` / RGB colors; manage named `.json` config profiles.

### Community & Downloads
- **Discord Server**: [discord.gg/kRNZn7Qbz](https://discord.gg/kRNZn7Qbz)
- **Releases**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Build Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Build Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`

---

&lt;a name=&quot;русский&quot;&gt;&lt;/a&gt;
## Русский

**CreamClient v1.0** — визуальный, HUD, косметический и оптимизационный клиент-мод для **Minecraft Forge** в лаймово-цитрусовой палитре. Включает единую панель управления (`Right Shift`) с понятным русским переводом всех функций, 54 предмета 3D-косметики, гибкую настройку графики для слабых ПК, конструктор интерфейсов, музыкальный плеер с перемоткой, быструю установку ресурс-паков и интеграцию с Discord.

### Основные возможности

- **Единая панель управления (`Right Shift`)**: 11 вкладок (`Визуалы`, `Косметика (54)`, `Небо и Мир`, `Рука и Камера`, `Метки и Радар`, `Муз. Плеер`, `Конструктор HUD`, `Свои Цвета`, `Ресурс-паки`, `Настройки Linux`, `Профили`), мгновенный переключатель `RU / EN`, поиск, биндер клавиш на каждый модуль и плавная прокрутка без наложения кнопок.
- **54 предмета 3D-косметики**: Комбинируй аксессуары на голову (кошачьи ушки, рога, нимб, корона, кибер-визор, шляпа мага), крылья и спину (крылья ангела, дракона, бабочки, катана, плащи), летающих питомцев (куб, мини-луна, лайм-дрон, призрак, сердечко), ауры/щиты и частицы.
- **Визуалы и плавный удар**:
  - `Плавный удар рукой (SmoothSwing)`: Настраиваемая плавность, скорость, сила замаха и 5 стилей удара (`Smooth`, `1.7 Old`, `Spin`, `Push`, `Wave`).
  - `Настройка руки`: Позиция X/Y/Z, 3D-поворот, масштаб и вращение предмета.
  - `Небо (20 стилей)`: Настройка RGB, прозрачности, вращения, звёзд, северного сияния и тумана.
  - Уникальные эффекты: `Дыхание блока` (пульсация поставленного блока 2с), `Лужа под дождём` (зеркальное отражение неба через 5с под дождём), `2D мазок от стрел` (200 мс на экране), `Мини-погода` в углу, `Круги при прыжке`, `Пульс-волны`, `Неоновая сетка пола`, `3D Звёзды`, `Свет под шагами`, `Надпись при ударе`, `Волна при ударе`, `Свободная камера [Left Alt]` и `3D след снарядов`.
- **Оптимизация для слабых ПК («Картошка»)**: Настраивай дальность прорисовки (`2-16` чанков), дальность мобов (`50%-100%`), частицы, графику, мягкий свет, облака, мипмапы и лимит FPS прямо из панели.
- **Метки, смерть и радар игроков**: Быстрая 3D-метка на `M`, автосохранение красной метки смерти (`DeathCoords`), а также радар игроков рядом (`NearbyPlayers`, радиус по умолчанию `150м`) с уведомлениями и звуком.
- **Музыкальный плеер и ресурс-паки**:
  - Встроенный `.wav` плеер с перемоткой кликом по полосе, кнопками `-10s`/`+10s`, глобальными клавишами `F7`/`F8`/`F9`, мини-плеером в главном меню и эквалайзером на экране.
  - Быстрое управление ресурс-паками прямо в панели: включение и мгновенная перезагрузка `.zip` паков из `resourcepacks/`.
- **Конструктор HUD, Свои Цвета и Профили**: Перетаскивай и масштабируй все 9 виджетов (`СКМ` меняет стиль, стрелки двигают по 1px), создавай и сохраняй свои уникальные `#HEX` / RGB цвета, а также сохраняй готовые `.json` профили конфигов.

### Сообщество и сборка
- **Discord-сервер**: [discord.gg/kRNZn7Qbz](https://discord.gg/kRNZn7Qbz)
- **Релизы**: [GitHub Releases](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)
- **Сборка Forge 1.16.5 (Java 8, Gradle 7.4.2)**: `gradle build --no-daemon --stacktrace`
- **Сборка Forge 1.21.4 (Java 21, Gradle 8.10.2)**: `cd port-1.21.4 && gradle build --no-daemon --stacktrace`
