

# CreamClient

**Кастомный клиент-мод для Minecraft Forge 1.16.5 с лаймовой эстетикой, 3D-визуалами, переработанными меню и гибкой кастомизацией**






[Build Status](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/actions)
[GitHub Stars](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/stargazers)
[Last Commit](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/commits)



[Репозиторий](https://github.com/sharagin/CreamClient-Minecraft) | [Сборка в Actions](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/actions) | [Скачать релиз](https://www.google.com/search?q=https://github.com/sharagin/CreamClient-Minecraft/releases)

## Возможности клиента

* **Полностью переработанный интерфейс:** кастомное главное меню с созвездиями и ASCII-баннером, меню паузы, мультиплеер, настройки, стилизация инвентаря и чёрный игровой курсор.
* **Мультиязычность (`RU` / `EN` / `UK`):** мгновенное переключение языка интерфейса прямо в меню, настройках или ClickGUI.
* **Гибкая типографика:** чёткий HD-шрифт `CreamHD` (с возможностью замены на любой `.ttf`, включая `Terentiev SP Regular`) и быстрый возврат на `Vanilla` шрифт.
* **Динамические цветовые режимы:** 7 встроенных палитр (`Lime`, `Citrus`, `Mint`, `CyberCyan`, `Amethyst`, `Crimson`, `CreamGold`) и режимы анимации цвета (`Breathing`, `Rainbow`, `Static`).
* **Discord Rich Presence:** встроенная интеграция с Discord Desktop через Windows Named Pipe без сторонних DLL.
* **Медиа-система:** загрузка собственных картинок на фон (`.png` / `.jpg`) и фоновый плеер музыки (`.wav`).

## Модули и настройки

> Нажмите **`Right Shift`** в игре, чтобы открыть ClickGUI.
> **ЛКМ** включает модуль или меняет значение, **ПКМ** раскрывает тонкие настройки.

| Категория | Модули | Основные параметры |
| ----- | ----- | ----- |
| **Visuals** | `ParticleAura`, `HitParticles`, `TargetRing`, `BlockOverlay`, `ChinaHat`, `JumpCircles`, `Trails`, `BoxESP`, `CustomFog`, `TimeChanger`, `NoWeather`, `ViewModel`, `Fullbright`, `NoHurtCam`, `Zoom` | Радиус и скорость колец, число частиц, длительность шлейфа, цвет тумана RGB, смещение и масштаб руки, время суток, зум на `C` |
| **HUD** | `Watermark`, `TargetHUD`, `SessionInfo`, `ScreenGlow`, `CustomHotbar`, `Coordinates`, `Keystrokes`, `ModuleList`, `ArmorHUD`, `PotionHUD`, `Speedometer`, `Crosshair` | Логотип, FPS, Ping, биом, счётчик CPS (`LMB`/`RMB`), интенсивность свечения краёв, размер прицела |
| **Movement** | `ToggleSprint`, `AutoRespawn` | Автоматический спринт и мгновенный респаун |
| **Interface** | `Appearance`, `CustomMenus`, `BlackCursor`, `DiscordRPC` | Палитра, `ColorMode`, шрифт, язык, прозрачность панелей, скорость анимаций, показ сервера и ника в Discord |
| **Media** | `Background`, `MenuMusic` | Выбор фона (`DefaultImage` / `Animated` / `CustomImage`), затемнение фона, громкость музыки |

## Кастомный фон, музыка и свой шрифт

После первого запуска клиент создаёт рабочие папки в директории игры (`%appdata%\.minecraft\creamclient\`):

* **Свой фон:** поместите любые `.png` или `.jpg` изображения в `creamclient/backgrounds/` и выберите режим `CustomImage` в меню или ClickGUI.
* **Своя музыка:** поместите треки в формате **`.wav` (PCM 16-bit, 44100 Hz)** в `creamclient/music/` и включите плеер кнопкой в правом верхнем углу меню.
* **Установка своего шрифта (например, `Terentiev SP Regular`):** перед сборкой замените файл `src/main/resources/assets/creamclient/font/client_font.ttf` на нужный `.ttf` файл.
* **Свой Discord Application ID:** при желании укажите `discordAppId` в файле `.minecraft/config/creamclient.json`.

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
