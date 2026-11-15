

# CreamClient

**Кастомный клиент-мод для Minecraft Forge 1.16.5 с лаймовой эстетикой, 3D-визуалами, переработанными меню и гибкой кастомизацией**

[https://img.shields.io/badge/MC-1.16.5-a3ff12?style=flat-square&logo=minecraft&logoColor=0a0d0a&labelColor=141b14]
[https://img.shields.io/badge/Forge-36.2.42-a3ff12?style=flat-square&labelColor=141b14]
[https://img.shields.io/badge/v1.0-a3ff12?style=flat-square&labelColor=141b14]
[[https://img.shields.io/github/actions/workflow/status/sharagin/CreamClient-Minecraft/build.yml?style=flat-square&label=Build&color=a3ff12&labelColor=141b14]](https://github.com/sharagin/CreamClient-Minecraft/actions)
[[https://img.shields.io/github/stars/sharagin/CreamClient-Minecraft?style=flat-square&color=a3ff12&labelColor=141b14]](https://github.com/sharagin/CreamClient-Minecraft/stargazers)
[[https://skillicons.dev/icons?i=java,gradle,github,githubactions,discord&theme=dark]](https://github.com/sharagin/CreamClient-Minecraft)

## Технологический стек










[Репозиторий](https://github.com/sharagin/CreamClient-Minecraft) | [Сборка в Actions](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/actions) | [Скачать релиз](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)

```text
  ✧  ╭───────────────────────────────────────╮  ✧
     │   ▄████▄  ██▀███  ▓█████ ▄▄▄   ██▄ ██ │   
  ⋆  │  ▒██▀ ▀█ ▓██ ▒ ██▒▓█   ▀▒████▄ ▓██▒██ │  ⋆
     │  ▒▓█   ▄ ▓██ ░▄█ ▒▒███  ▒██ ▀█▄▓██░██ │   
  ✧  │  ▒▓▓▄██▒ ▒██▀▀█▄  ▒▓█  ▄░██▄▄██▒██ ██ │  ✧
     ╰───────  ♡  c r e a m  c l i e n t  ♡  ─╯   
```

## Ключевые особенности и архитектура

* **Чистый Forge без Mixin-конфликтов:** вся графика, хуки экранов и 3D-рендеринг построены на стандартной шине событий `MinecraftForge.EVENT_BUS`, благодаря чему мод стабильно работает с другими Forge-модами и ресурспаками.
* **Плавная физика интерфейса:** переходы меню, раскрытие ClickGUI, переключение модулей и перемещение рамки хотбара используют сглаживание `easeOutCubic` и `lerp` с настраиваемой скоростью.
* **Чёткая типографика с выбором:** шрифт `CreamHD` растеризуется в повышенном разрешении (`size 15.0`, `oversample 8.0`) и масштабируется через `MatrixStack`, что убирает размытие; в любой момент можно переключиться на классический `Vanilla` шрифт.
* **Кастомные экраны целиком:** переработаны `CreamMainMenuScreen` (созвездия, логотип, кнопка GitHub), `CreamMultiplayerScreen` (список серверов и прямой вход по IP), `CreamOptionsScreen`, `CreamPauseScreen`, а также тёмно-лаймовое оформление инвентаря и контейнеров.
* **Нативный Discord Rich Presence без DLL:** клиент напрямую общается с Discord Desktop через Windows Named Pipe (`\\.\pipe\discord-ipc-*`), безопасно обновляя статус игрока в главном потоке.
* **Чёрный игровой курсор:** при открытии любого меню системный указатель скрывается через `GLFW`, а поверх интерфейса рисуется кастомный PNG-курсор с акцентной обводкой.
* **Мультиязычность (`RU` / `EN` / `UK`):** переключение языка переводит меню, настройки и подсказки на лету без перезапуска игры.

## Цветовые палитры и режимы

В разделе **ClickGUI → Interface → Appearance** доступны 7 палитр и 3 динамических режима (`Breathing`, `Rainbow`, `Static`), которые меняют цвет интерфейса, курсора, хотбара и 3D-эффектов одновременно:

| Палитра | Основной HEX | Характер |
| :--- | :--- | :--- |
| **Lime** | `#A3FF12` | Фирменный неоново-лаймовый |
| **Citrus** | `#FF9F1C` | Тёплый цитрусово-апельсиновый |
| **Mint** | `#2EFFB2` | Холодный мятный |
| **CyberCyan** | `#00E5FF` | Яркий кибер-голубой |
| **Amethyst** | `#C06CFF` | Неоновый фиолетовый |
| **Crimson** | `#FF3B5C` | Насыщенный малиново-красный |
| **CreamGold** | `#FFD166` | Мягкий кремово-золотой |

## Модули и настройки

| Категория | Модули | Основные параметры |
| :--- | :--- | :--- |
| **Visuals** | `ParticleAura`, `HitParticles`, `TargetRing`, `BlockOverlay`, `ChinaHat`, `JumpCircles`, `Trails`, `BoxESP`, `CustomFog`, `TimeChanger`, `NoWeather`, `ViewModel`, `Fullbright`, `NoHurtCam`, `Zoom` | Радиус и скорость колец, число частиц ауры и искр, длительность шлейфа, цвет тумана RGB, смещение и масштаб руки, время суток, зум на `C` |
| **HUD** | `Watermark`, `TargetHUD`, `SessionInfo`, `ScreenGlow`, `CustomHotbar`, `Coordinates`, `Keystrokes`, `ModuleList`, `ArmorHUD`, `PotionHUD`, `Speedometer`, `Crosshair` | Логотип, FPS, Ping, биом, счётчик CPS (`LMB`/`RMB`), интенсивность свечения по краям, размер прицела |
| **Movement** | `ToggleSprint`, `AutoRespawn` | Автоматический спринт при движении вперёд и мгновенный респаун |
| **Interface** | `Appearance`, `CustomMenus`, `BlackCursor`, `DiscordRPC` | Палитра, `ColorMode`, шрифт, язык, прозрачность панелей, скорость анимаций, показ сервера и ника в Discord |
| **Media** | `Background`, `MenuMusic` | Выбор фона (`DefaultImage` / `Animated` / `CustomImage`), затемнение фона, громкость музыки |

## Горячие клавиши и управление

| Клавиша / Действие | Назначение |
| :--- | :--- |
| **`Right Shift`** | Открыть или закрыть ClickGUI |
| **`C`** | Плавный оптический зум камеры (`Zoom`) |
| **`ЛКМ`** в ClickGUI | Включить/выключить модуль, переключить режим или двигать слайдер |
| **`ПКМ`** в ClickGUI | Раскрыть или свернуть дополнительные настройки модуля |
| **`Колёсико мыши`** | Вертикальная прокрутка колонок ClickGUI |
| **`ESC`** | Сохранить конфиг в `.minecraft/config/creamclient.json` и закрыть меню |

## Кастомный фон, музыка и свой шрифт

После первого запуска клиент создаёт рабочие папки в директории игры (`%appdata%\.minecraft\creamclient\`):

* **Свой фон:** поместите любые `.png` или `.jpg` изображения в `creamclient/backgrounds/` и выберите режим `CustomImage` в меню или ClickGUI.
* **Своя музыка:** поместите треки в формате **`.wav` (PCM 16-bit, 44100 Hz)** в `creamclient/music/` и включите плеер кнопкой в правом верхнем углу меню.
* **Установка своего шрифта (например, `Terentiev SP Regular`):** перед сборкой замените файл `src/main/resources/assets/creamclient/font/client_font.ttf` на нужный `.ttf` файл.
* **Свой Discord Application ID:** при желании укажите `discordAppId` в файле `.minecraft/config/creamclient.json`.

## Структура проекта

```text
CreamClient-Minecraft/
├── .github/workflows/build.yml
├── src/main/java/com/creamclient/
│   ├── CreamClientMod.java
│   ├── config/CreamConfig.java
│   ├── discord/DiscordRPC.java
│   ├── event/ClientEvents.java
│   ├── media/MediaManager.java
│   ├── module/
│   │   ├── Category.java
│   │   ├── Module.java
│   │   ├── ModuleManager.java
│   │   └── Setting.java
│   ├── theme/
│   │   ├── CreamTheme.java
│   │   └── Lang.java
│   ├── ui/
│   │   ├── ClickGuiScreen.java
│   │   ├── CreamMainMenuScreen.java
│   │   ├── CreamMultiplayerScreen.java
│   │   ├── CreamOptionsScreen.java
│   │   ├── CreamPauseScreen.java
│   │   └── HudOverlay.java
│   └── visual/WorldVisuals.java
└── src/main/resources/
    ├── META-INF/mods.toml
    └── assets/creamclient/
        ├── font/
        └── textures/
```

## Сборка и установка

### Автоматическая сборка через GitHub Actions

1. Загрузите файлы проекта в репозиторий на GitHub вместе с `.github/workflows/build.yml`.
2. Откройте вкладку **Actions**, выберите **Build CreamClient 1.0** и нажмите **Run workflow**.
3. Скачайте готовый `.jar` файл из раздела **Artifacts**.

### Локальная сборка

Требуется установленный **JDK 8** и **Gradle**:

```bash
gradle build --no-daemon
```

Готовый мод появится в папке `build/libs/CreamClient-1.16.5-1.0.jar`.

### Установка в игру

1. Установите **Minecraft Forge 1.16.5** (`36.2.42`).
2. Скопируйте `.jar` файл в папку `%appdata%\.minecraft\mods`.
3. Запустите профиль **Forge 1.16.5**.

Разработка: [sharagin](https://github.com/sharagin/CreamClient-Minecraft)
