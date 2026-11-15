<div align="center">

<h1>✧ CreamClient ✧</h1>

<p><strong>Кастомный клиент-мод для Minecraft Forge 1.16.5 с лаймовой эстетикой, 3D-визуалами, переработанными меню и гибкой кастомизацией</strong></p>

<p>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/Minecraft-1.16.5-A3FF12?style=flat-square&logo=minecraft&logoColor=white&labelColor=1a1a1a" alt="Minecraft 1.16.5"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/Forge-36.2.42-A3FF12?style=flat-square&logo=curseforge&logoColor=white&labelColor=1a1a1a" alt="Forge 36.2.42"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/Java-8-A3FF12?style=flat-square&logo=openjdk&logoColor=white&labelColor=1a1a1a" alt="Java 8"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/Gradle-Build-A3FF12?style=flat-square&logo=gradle&logoColor=white&labelColor=1a1a1a" alt="Gradle"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/version-1.0-A3FF12?style=flat-square&labelColor=1a1a1a" alt="version 1.0"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/license-MIT-A3FF12?style=flat-square&labelColor=1a1a1a" alt="license MIT"></a>
</p>

<p>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/actions"><img src="https://img.shields.io/github/actions/workflow/status/sharagin/CreamClient-Minecraft/build.yml?style=flat-square&label=build&labelColor=1a1a1a&color=A3FF12" alt="Build"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/stargazers"><img src="https://img.shields.io/github/stars/sharagin/CreamClient-Minecraft?style=flat-square&labelColor=1a1a1a&color=A3FF12" alt="Stars"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/network/members"><img src="https://img.shields.io/github/forks/sharagin/CreamClient-Minecraft?style=flat-square&labelColor=1a1a1a&color=A3FF12" alt="Forks"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/issues"><img src="https://img.shields.io/github/issues/sharagin/CreamClient-Minecraft?style=flat-square&labelColor=1a1a1a&color=A3FF12" alt="Issues"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/commits"><img src="https://img.shields.io/github/last-commit/sharagin/CreamClient-Minecraft?style=flat-square&labelColor=1a1a1a&color=A3FF12" alt="Last commit"></a>
</p>

<p>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><strong>Репозиторий</strong></a> ·
  <a href="https://github.com/sharagin/CreamClient-Minecraft/actions"><strong>Сборка в Actions</strong></a> ·
  <a href="https://github.com/sharagin/CreamClient-Minecraft/releases"><strong>Скачать релиз</strong></a>
</p>

<pre>
  ✧  ╭───────────────────────────────────────╮  ✧
     │   ▄████▄  ██▀███  ▓█████ ▄▄▄   ██▄ ██ │
  ⋆  │  ▒██▀ ▀█ ▓██ ▒ ██▒▓█   ▀▒████▄ ▓██▒██ │  ⋆
     │  ▒▓█   ▄ ▓██ ░▄█ ▒▒███  ▒██ ▀█▄▓██░██ │
  ✧  │  ▒▓▓▄██▒ ▒██▀▀█▄  ▒▓█  ▄░██▄▄██▒██ ██ │  ✧
     ╰───────  ♡  c r e a m  c l i e n t  ♡  ─╯
</pre>

</div>

<hr>

<h2 id="toc">📖 Содержание</h2>

<ul>
  <li><a href="#features">✨ Ключевые особенности и архитектура</a></li>
  <li><a href="#palettes">🎨 Цветовые палитры и режимы</a></li>
  <li><a href="#modules">🧩 Модули и настройки</a></li>
  <li><a href="#hotkeys">⌨️ Горячие клавиши и управление</a></li>
  <li><a href="#custom">🖼️ Фон, музыка и свой шрифт</a></li>
  <li><a href="#structure">📁 Структура проекта</a></li>
  <li><a href="#build">⚙️ Сборка и установка</a></li>
</ul>

<hr>

<h2 id="features">✨ Ключевые особенности и архитектура</h2>

<table>
  <thead>
    <tr>
      <th align="left">Фича</th>
      <th align="left">Описание</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>🧬 <strong>Чистый Forge без Mixin-конфликтов</strong></td>
      <td>Вся графика, хуки экранов и 3D-рендеринг построены на стандартной шине событий <code>MinecraftForge.EVENT_BUS</code>, благодаря чему мод стабильно работает с другими Forge-модами и ресурспаками.</td>
    </tr>
    <tr>
      <td>🌀 <strong>Плавная физика интерфейса</strong></td>
      <td>Переходы меню, раскрытие ClickGUI, переключение модулей и перемещение рамки хотбара используют сглаживание <code>easeOutCubic</code> и <code>lerp</code> с настраиваемой скоростью.</td>
    </tr>
    <tr>
      <td>🔤 <strong>Чёткая типографика с выбором</strong></td>
      <td>Шрифт <code>CreamHD</code> растеризуется в повышенном разрешении (<code>size 15.0</code>, <code>oversample 8.0</code>) и масштабируется через <code>MatrixStack</code>, что убирает размытие; в любой момент можно переключиться на классический <code>Vanilla</code> шрифт.</td>
    </tr>
    <tr>
      <td>🪟 <strong>Кастомные экраны целиком</strong></td>
      <td>Переработаны <code>CreamMainMenuScreen</code> (созвездия, логотип, кнопка GitHub), <code>CreamMultiplayerScreen</code> (список серверов и прямой вход по IP), <code>CreamOptionsScreen</code>, <code>CreamPauseScreen</code>, а также тёмно-лаймовое оформление инвентаря и контейнеров.</td>
    </tr>
    <tr>
      <td>🎧 <strong>Нативный Discord Rich Presence без DLL</strong></td>
      <td>Клиент напрямую общается с Discord Desktop через Windows Named Pipe (<code>\\.\pipe\discord-ipc-*</code>), безопасно обновляя статус игрока в главном потоке.</td>
    </tr>
    <tr>
      <td>🖱️ <strong>Чёрный игровой курсор</strong></td>
      <td>При открытии любого меню системный указатель скрывается через <code>GLFW</code>, а поверх интерфейса рисуется кастомный PNG-курсор с акцентной обводкой.</td>
    </tr>
    <tr>
      <td>🌐 <strong>Мультиязычность</strong> (<code>RU</code> / <code>EN</code> / <code>UK</code>)</td>
      <td>Переключение языка переводит меню, настройки и подсказки на лету без перезапуска игры.</td>
    </tr>
  </tbody>
</table>

<hr>

<h2 id="palettes">🎨 Цветовые палитры и режимы</h2>

<p>В разделе <strong>ClickGUI → Interface → Appearance</strong> доступны <strong>7 палитр</strong> и <strong>3 динамических режима</strong> (<code>Breathing</code>, <code>Rainbow</code>, <code>Static</code>), которые меняют цвет интерфейса, курсора, хотбара и 3D-эффектов одновременно.</p>

<table>
  <thead>
    <tr>
      <th align="left">Палитра</th>
      <th align="center">Цвет</th>
      <th align="left">Основной HEX</th>
      <th align="left">Характер</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Lime</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-A3FF12-A3FF12?style=flat-square" alt="Lime"></td>
      <td><code>#A3FF12</code></td>
      <td>Фирменный неоново-лаймовый</td>
    </tr>
    <tr>
      <td><strong>Citrus</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-FF9F1C-FF9F1C?style=flat-square" alt="Citrus"></td>
      <td><code>#FF9F1C</code></td>
      <td>Тёплый цитрусово-апельсиновый</td>
    </tr>
    <tr>
      <td><strong>Mint</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-2EFFB2-2EFFB2?style=flat-square" alt="Mint"></td>
      <td><code>#2EFFB2</code></td>
      <td>Холодный мятный</td>
    </tr>
    <tr>
      <td><strong>CyberCyan</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-00E5FF-00E5FF?style=flat-square" alt="CyberCyan"></td>
      <td><code>#00E5FF</code></td>
      <td>Яркий кибер-голубой</td>
    </tr>
    <tr>
      <td><strong>Amethyst</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-C06CFF-C06CFF?style=flat-square" alt="Amethyst"></td>
      <td><code>#C06CFF</code></td>
      <td>Неоновый фиолетовый</td>
    </tr>
    <tr>
      <td><strong>Crimson</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-FF3B5C-FF3B5C?style=flat-square" alt="Crimson"></td>
      <td><code>#FF3B5C</code></td>
      <td>Насыщенный малиново-красный</td>
    </tr>
    <tr>
      <td><strong>CreamGold</strong></td>
      <td align="center"><img src="https://img.shields.io/badge/-FFD166-FFD166?style=flat-square" alt="CreamGold"></td>
      <td><code>#FFD166</code></td>
      <td>Мягкий кремово-золотой</td>
    </tr>
  </tbody>
</table>

<hr>

<h2 id="modules">🧩 Модули и настройки</h2>

<table>
  <thead>
    <tr>
      <th align="left">Категория</th>
      <th align="left">Модули</th>
      <th align="left">Основные параметры</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>🎥 <strong>Visuals</strong></td>
      <td><code>ParticleAura</code>, <code>HitParticles</code>, <code>TargetRing</code>, <code>BlockOverlay</code>, <code>ChinaHat</code>, <code>JumpCircles</code>, <code>Trails</code>, <code>BoxESP</code>, <code>CustomFog</code>, <code>TimeChanger</code>, <code>NoWeather</code>, <code>ViewModel</code>, <code>Fullbright</code>, <code>NoHurtCam</code>, <code>Zoom</code></td>
      <td>Радиус и скорость колец, число частиц ауры и искр, длительность шлейфа, цвет тумана RGB, смещение и масштаб руки, время суток, зум на <kbd>C</kbd></td>
    </tr>
    <tr>
      <td>📊 <strong>HUD</strong></td>
      <td><code>Watermark</code>, <code>TargetHUD</code>, <code>SessionInfo</code>, <code>ScreenGlow</code>, <code>CustomHotbar</code>, <code>Coordinates</code>, <code>Keystrokes</code>, <code>ModuleList</code>, <code>ArmorHUD</code>, <code>PotionHUD</code>, <code>Speedometer</code>, <code>Crosshair</code></td>
      <td>Логотип, FPS, Ping, биом, счётчик CPS (<code>LMB</code>/<code>RMB</code>), интенсивность свечения по краям, размер прицела</td>
    </tr>
    <tr>
      <td>🏃 <strong>Movement</strong></td>
      <td><code>ToggleSprint</code>, <code>AutoRespawn</code></td>
      <td>Автоматический спринт при движении вперёд и мгновенный респаун</td>
    </tr>
    <tr>
      <td>🎛️ <strong>Interface</strong></td>
      <td><code>Appearance</code>, <code>CustomMenus</code>, <code>BlackCursor</code>, <code>DiscordRPC</code></td>
      <td>Палитра, <code>ColorMode</code>, шрифт, язык, прозрачность панелей, скорость анимаций, показ сервера и ника в Discord</td>
    </tr>
    <tr>
      <td>🎵 <strong>Media</strong></td>
      <td><code>Background</code>, <code>MenuMusic</code></td>
      <td>Выбор фона (<code>DefaultImage</code> / <code>Animated</code> / <code>CustomImage</code>), затемнение фона, громкость музыки</td>
    </tr>
  </tbody>
</table>

<hr>

<h2 id="hotkeys">⌨️ Горячие клавиши и управление</h2>

<table>
  <thead>
    <tr>
      <th align="left">Клавиша / Действие</th>
      <th align="left">Назначение</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><kbd>Right Shift</kbd></td>
      <td>Открыть или закрыть ClickGUI</td>
    </tr>
    <tr>
      <td><kbd>C</kbd></td>
      <td>Плавный оптический зум камеры (<code>Zoom</code>)</td>
    </tr>
    <tr>
      <td><kbd>ЛКМ</kbd> в ClickGUI</td>
      <td>Включить/выключить модуль, переключить режим или двигать слайдер</td>
    </tr>
    <tr>
      <td><kbd>ПКМ</kbd> в ClickGUI</td>
      <td>Раскрыть или свернуть дополнительные настройки модуля</td>
    </tr>
    <tr>
      <td><kbd>Колёсико мыши</kbd></td>
      <td>Вертикальная прокрутка колонок ClickGUI</td>
    </tr>
    <tr>
      <td><kbd>ESC</kbd></td>
      <td>Сохранить конфиг в <code>.minecraft/config/creamclient.json</code> и закрыть меню</td>
    </tr>
  </tbody>
</table>

<hr>

<h2 id="custom">🖼️ Кастомный фон, музыка и свой шрифт</h2>

<p>После первого запуска клиент создаёт рабочие папки в директории игры (<code>%appdata%\.minecraft\creamclient\</code>):</p>

<table>
  <thead>
    <tr>
      <th align="left">Что</th>
      <th align="left">Как</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>🖼️ <strong>Свой фон</strong></td>
      <td>Поместите любые <code>.png</code> или <code>.jpg</code> изображения в <code>creamclient/backgrounds/</code> и выберите режим <code>CustomImage</code> в меню или ClickGUI.</td>
    </tr>
    <tr>
      <td>🎵 <strong>Своя музыка</strong></td>
      <td>Поместите треки в формате <strong><code>.wav</code> (PCM 16-bit, 44100 Hz)</strong> в <code>creamclient/music/</code> и включите плеер кнопкой в правом верхнем углу меню.</td>
    </tr>
    <tr>
      <td>🔤 <strong>Установка своего шрифта</strong></td>
      <td>Перед сборкой замените файл <code>src/main/resources/assets/creamclient/font/client_font.ttf</code> на нужный <code>.ttf</code> файл (например, <code>Terentiev SP Regular</code>).</td>
    </tr>
    <tr>
      <td>🤖 <strong>Свой Discord Application ID</strong></td>
      <td>При желании укажите <code>discordAppId</code> в файле <code>.minecraft/config/creamclient.json</code>.</td>
    </tr>
  </tbody>
</table>

<hr>

<h2 id="structure">📁 Структура проекта</h2>

<pre>
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
</pre>

<hr>

<h2 id="build">⚙️ Сборка и установка</h2>

<h3>🤖 Автоматическая сборка через GitHub Actions</h3>

<ol>
  <li>Загрузите файлы проекта в репозиторий на GitHub вместе с <code>.github/workflows/build.yml</code>.</li>
  <li>Откройте вкладку <strong>Actions</strong>, выберите <strong>Build CreamClient 1.0</strong> и нажмите <strong>Run workflow</strong>.</li>
  <li>Скачайте готовый <code>.jar</code> файл из раздела <strong>Artifacts</strong>.</li>
</ol>

<h3>🛠️ Локальная сборка</h3>

<p>Требуется установленный <strong>JDK 8</strong> и <strong>Gradle</strong>:</p>

<pre><code>gradle build --no-daemon
</code></pre>

<p>Готовый мод появится в папке <code>build/libs/CreamClient-1.16.5-1.0.jar</code>.</p>

<h3>📥 Установка в игру</h3>

<ol>
  <li>Установите <strong>Minecraft Forge 1.16.5</strong> (<code>36.2.42</code>).</li>
  <li>Скопируйте <code>.jar</code> файл в папку <code>%appdata%\.minecraft\mods</code>.</li>
  <li>Запустите профиль <strong>Forge 1.16.5</strong>.</li>
</ol>

<hr>

<div align="center">

<p>
  <a href="https://github.com/sharagin/CreamClient-Minecraft"><img src="https://img.shields.io/badge/-GitHub-1a1a1a?style=flat-square&logo=github&logoColor=A3FF12" alt="GitHub"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/releases"><img src="https://img.shields.io/badge/-Releases-A3FF12?style=flat-square&logo=github&logoColor=1a1a1a" alt="Releases"></a>
  <a href="https://github.com/sharagin/CreamClient-Minecraft/issues"><img src="https://img.shields.io/badge/-Issues-1a1a1a?style=flat-square&logo=githubactions&logoColor=A3FF12" alt="Issues"></a>
</p>

<p><strong>Разработка:</strong> <a href="https://github.com/sharagin/CreamClient-Minecraft">sharagin</a></p>

<p>♡ <em>c r e a m &nbsp; c l i e n t</em> ♡</p>

</div>
