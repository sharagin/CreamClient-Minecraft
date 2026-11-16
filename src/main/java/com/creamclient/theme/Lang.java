package com.creamclient.theme;

import com.creamclient.module.ModuleManager;

import java.util.HashMap;
import java.util.Map;

public final class Lang {
    private static final Map<String, String> RU = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();
    private static final Map<String, String> UK = new HashMap<>();

    static {
        put("menu.singleplayer", "Одиночная игра", "Singleplayer", "Одиночна гра");
        put("menu.multiplayer", "Сетевая игра", "Multiplayer", "Мережева гра");
        put("menu.mods", "Моды", "Mods", "Моди");
        put("menu.options", "Настройки", "Options", "Налаштування");
        put("menu.quit", "Выйти из игры", "Quit Game", "Вийти з гри");
        put("menu.settings", "Настройки клиента", "Client Settings", "Налаштування клієнта");
        put("menu.bg", "Сменить фон", "Switch Background", "Змінити фон");
        put("menu.music", "Музыка: Играть/След.", "Music: Play / Next", "Музика: Грати/Далі");
        put("menu.font", "Шрифт: ", "Font: ", "Шрифт: ");
        put("menu.lang", "Язык: ", "Lang: ", "Мова: ");

        put("pause.resume", "Продолжить игру", "Resume Game", "Продовжити гру");
        put("pause.clickgui", "Меню CreamClient", "CreamClient ClickGUI", "Меню CreamClient");
        put("pause.options", "Настройки", "Options", "Налаштування");
        put("pause.disconnect", "Отключиться", "Disconnect", "Відключитися");

        put("sp.title", "ОДИНОЧНАЯ ИГРА  //  МИРЫ", "SINGLEPLAYER  //  WORLDS", "ОДИНОЧНА ГРА  //  СВІТИ");
        put("sp.play", "Играть в мир", "Play World", "Грати у світ");
        put("sp.create", "Создать новый мир", "Create New World", "Створити новий світ");
        put("sp.delete", "Удалить", "Delete", "Видалити");
        put("sp.empty", "Нет сохранённых миров. Нажмите Создать новый мир.", "No saved worlds yet. Click Create New World.", "Немає збережених світів. Натисніть Створити новий світ.");

        put("mp.title", "СЕТЕВАЯ ИГРА  //  СЕРВЕРЫ", "MULTIPLAYER  //  SERVERS", "МЕРЕЖЕВА ГРА  //  СЕРВЕРИ");
        put("mp.connect", "Подключиться", "Join Server", "Підключитися");
        put("mp.direct", "Быстрый вход (IP):", "Direct Connect (IP):", "Швидкий вхід (IP):");
        put("mp.refresh", "Обновить", "Refresh", "Оновити");
        put("mp.add", "Добавить", "Add Server", "Додати");
        put("mp.delete", "Удалить", "Delete", "Видалити");
        put("mp.back", "Назад", "Back", "Назад");
        put("mp.empty", "Серверы не добавлены. Введите IP ниже или нажмите Добавить.", "No servers yet. Enter IP below or click Add Server.", "Серверів немає. Введіть IP нижче або натисніть Додати.");

        put("opt.title", "ЦЕНТР НАСТРОЕК CREAMCLIENT", "CREAMCLIENT SETTINGS HUB", "ЦЕНТР НАЛАШТУВАНЬ CREAMCLIENT");
        put("opt.tab.game", "Игра и Графика", "Game & Video", "Гра та Графіка");
        put("opt.tab.sound", "Звук", "Audio", "Звук");
        put("opt.tab.client", "Интерфейс", "CreamClient", "Інтерфейс");
        put("opt.fov", "Поле зрения (FOV)", "Field of View (FOV)", "Поле зору (FOV)");
        put("opt.gamma", "Яркость", "Brightness", "Яскравість");
        put("opt.dist", "Дальность прорисовки", "Render Distance", "Дальність промальовки");
        put("opt.fps", "Лимит FPS", "Max Framerate", "Ліміт FPS");
        put("opt.bob", "Покачивание камеры: ", "View Bobbing: ", "Хитання камери: ");
        put("opt.shadows", "Тени сущностей: ", "Entity Shadows: ", "Тіні сутностей: ");
        put("opt.vanilla", "Управление и Ресурспаки", "Controls & Resource Packs", "Керування та Ресурспаки");
        put("opt.back", "Сохранить и назад", "Save & Back", "Зберегти та назад");

        put("gui.hint", "ЛКМ: Вкл/Перекл  |  ПКМ: Настройки  |  Колёсико: Скролл  |  ESC: Сохранить",
                "LMB: Toggle / Cycle  |  RMB: Expand Settings  |  Scroll: Move  |  ESC: Save & Close",
                "ЛКМ: Увімк/Змін  |  ПКМ: Налаштування  |  Колесо: Скрол  |  ESC: Зберегти");
    }

    private Lang() {}
    private static void put(String key, String ru, String en, String uk) { RU.put(key, ru); EN.put(key, en); UK.put(key, uk); }
    public static String tr(String key) {
        String lang = ModuleManager.LANG_MODE != null ? ModuleManager.LANG_MODE.getMode() : "RU";
        Map<String, String> map = "EN".equals(lang) ? EN : ("UK".equals(lang) ? UK : RU);
        return map.getOrDefault(key, key);
    }

    public static String trModule(String name, boolean ru) {
        if (!ru) return name;
        switch (name) {
            case "CustomSky": return "Небо (20 стилей)";
            case "JumpCircles": return "Круги при прыжке";
            case "StepCircles": return "Круги при ходьбе";
            case "Trails": return "Шлейф за игроком";
            case "Breadcrumbs": return "Линия пути";
            case "ChinaHat": return "Конус над головой";
            case "Halo": return "Нимб над головой";
            case "Wings": return "3D Крылья";
            case "OrbitalRings": return "Орбитальные кольца";
            case "CrystalOrbit": return "Кристаллы вокруг";
            case "PlayerAura": return "Аура под ногами";
            case "ParticleAura": return "Светлячки вокруг";
            case "HitParticles": return "Искры при ударе";
            case "TargetRing": return "Кольцо на цели";
            case "TargetCrown": return "Корона над целью";
            case "BlockOverlay": return "Подсветка блока";
            case "BoxESP": return "Рамки сущностей";
            case "Tracers": return "Линии к игрокам";
            case "CustomFog": return "Цветной туман";
            case "TimeChanger": return "Время суток";
            case "NoWeather": return "Убрать дождь/снег";
            case "NoFireOverlay": return "Убрать огонь с экрана";
            case "HandVisuals": return "Настройка руки";
            case "Fullbright": return "Полная яркость";
            case "NoHurtCam": return "Убрать тряску камеры";
            case "Zoom": return "Приближение [C]";
            case "WorldGrid": return "Неоновая сетка пола";
            case "PulseRings": return "Пульс-волны";
            case "StarField3D": return "3D Звёзды вокруг";
            case "FootstepGlow": return "Свет под шагами";
            case "EntityGlowRing": return "Круги под мобами";
            case "ProjectileTrails": return "3D след снарядов";
            case "DamagePopups": return "Надпись при ударе";
            case "HitShockwave": return "Волна при ударе";
            case "FreeLook": return "Свободная камера [Alt]";
            case "BlockBreathe": return "Дыхание блока (2с)";
            case "RainPuddle": return "Лужа под дождём (5с)";
            case "Projectile2DTrail": return "2D мазок от стрел";
            case "Waypoints": return "3D Метки [M]";
            case "NearbyPlayers": return "Радар игроков (150м)";
            case "MusicPlayerHUD": return "Плеер на экране";
            case "WeatherMiniOrb": return "Мини-погода в углу";
            case "HUD": return "Весь HUD (вкл/выкл)";
            case "CustomChat": return "Красивый чат";
            case "Watermark": return "Водяной знак";
            case "Notifications": return "Уведомления";
            case "CompassHUD": return "Компас сверху";
            case "RadarHUD": return "Мини-радар";
            case "FPSGraph": return "График FPS";
            case "TargetHUD": return "Панель цели";
            case "SessionInfo": return "Статистика сессии";
            case "ScreenGlow": return "Свечение краёв экрана";
            case "CustomHotbar": return "Стильный хотбар";
            case "Coordinates": return "Координаты и биом";
            case "Keystrokes": return "Клавиши WASD и CPS";
            case "ModuleList": return "Список модулей";
            case "ArmorHUD": return "Прочность брони";
            case "PotionHUD": return "Таймеры зелий";
            case "Speedometer": return "Скорость (бл/сек)";
            case "Crosshair": return "Прицел клиента";
            case "ToggleSprint": return "Авто-бег";
            case "ToggleSneak": return "Постоянный присед";
            case "AutoRespawn": return "Авто-возрождение";
            case "ChatTime": return "Время в чате";
            case "AntiAFK": return "Анти-АФК";
            case "Appearance": return "Оформление и тема";
            case "CustomMenus": return "Кастомные меню";
            case "BlackCursor": return "Чёрный курсор";
            case "DiscordRPC": return "Активность Discord";
            case "Background": return "Фон меню";
            case "MenuMusic": return "Музыка в меню";
            case "DeathCoords": return "Метка смерти";
            case "LowHealthPulse": return "Пульс низкого HP";
            case "SmoothFOV": return "Плавный FOV";
            case "MediaHotkeys": return "Клавиши плеера [F7-F9]";
            case "ArmorAlert": return "Сигнал брони";
            case "RealClock": return "Реальное время";
            case "UISounds": return "Звуки интерфейса";
            case "SmoothSwing": return "Плавный удар рукой";
            case "PotatoOptimizer": return "Оптимизация (Картошка)";
            default: return name;
        }
    }

    public static String trDesc(String name, String fallback, boolean ru) {
        if (!ru) return fallback;
        switch (name) {
            case "CustomSky": return "Меняет небо на один из 20 стилей с настройкой цвета, звёзд и вращения";
            case "JumpCircles": return "Рисует красивые расходящиеся 3D-круги под ногами при прыжке";
            case "StepCircles": return "Добавляет мягкие светящиеся кольца при каждом шаге";
            case "Trails": return "Оставляет плавный светящийся 3D-шлейф позади твоего персонажа";
            case "Breadcrumbs": return "Рисует длинную линию маршрута, по которому ты прошёл";
            case "ChinaHat": return "Красивый светящийся конус над головой в виде от 3-го лица (F5)";
            case "Halo": return "Вращающийся 3D-нимб над головой в виде от 3-го лица (F5)";
            case "Wings": return "Геометрические анимированные 3D-крылья за спиной в F5";
            case "OrbitalRings": return "Гироскопические 3D-кольца, вращающиеся вокруг игрока в F5";
            case "CrystalOrbit": return "Летающие 3D-кристаллы на орбите вокруг персонажа в F5";
            case "PlayerAura": return "Мягкий светящийся круг ауры прямо под ногами";
            case "ParticleAura": return "Вокруг игрока плавно летают светящиеся частицы-светлячки";
            case "HitParticles": return "Выпускает яркие 3D-искры при каждом ударе по мобу или игроку";
            case "TargetRing": return "Рисует анимированное кольцо на цели, на которую ты смотришь";
            case "TargetCrown": return "Добавляет 3D-корону над головой выбранной цели";
            case "BlockOverlay": return "Обводит блок под прицелом светящейся рамкой цвета темы";
            case "BoxESP": return "Рисует аккуратные 3D-рамки вокруг игроков или мобов";
            case "Tracers": return "Проводит тонкие 3D-линии от экрана к ближайшим игрокам";
            case "CustomFog": return "Перекрашивает туман мира в цвет темы или твой уникальный RGB";
            case "TimeChanger": return "Визуально меняет время суток только у тебя (день/закат/ночь)";
            case "NoWeather": return "Отключает визуальный дождь и снег, не мешая другим эффектам";
            case "NoFireOverlay": return "Убирает огонь с нижней части экрана, когда ты горишь";
            case "HandVisuals": return "Полная настройка руки: сдвиг X/Y/Z, поворот, размер и вращение предмета";
            case "Fullbright": return "Делает темноту и пещеры полностью светлыми без факелов";
            case "NoHurtCam": return "Убирает раздражающую тряску экрана при получении урона";
            case "Zoom": return "Плавное приближение камеры при удержании клавиши C";
            case "WorldGrid": return "Проецирует неоновую кибер-сетку на пол под ногами";
            case "PulseRings": return "Регулярные пульсирующие 3D-волны, расходящиеся от игрока";
            case "StarField3D": return "Вокруг тебя в мире парят и мерцают объёмные 3D-звёзды";
            case "FootstepGlow": return "Оставляет мягкое свечение под ногами во время движения";
            case "EntityGlowRing": return "Рисует светящиеся кольца под ближайшими живыми существами";
            case "ProjectileTrails": return "Добавляет светящийся 3D-след за летящими стрелами и жемчугом";
            case "DamagePopups": return "Показывает всплывающую 3D-надпись HIT! в месте удара";
            case "HitShockwave": return "Создаёт расходящуюся 3D-волну в точке удара по цели";
            case "FreeLook": return "Позволяет осматриваться на 360 градусов, удерживая Left Alt";
            case "BlockBreathe": return "Только что поставленный блок мягко пульсирует 2 секунды и гаснет";
            case "RainPuddle": return "После 5 секунд под дождём под ногами появляется зеркальная лужа с бликами";
            case "Projectile2DTrail": return "Короткий 2D-мазок на экране за летящими стрелами и предметами (200 мс)";
            case "Waypoints": return "Ставь 3D-метки с лучом и дистанцией в метрах на клавишу M";
            case "NearbyPlayers": return "Предупреждает уведомлением и показывает игроков рядом (радиус до 500 м)";
            case "MusicPlayerHUD": return "Видимый плеер на экране: название трека, эквалайзер и прогресс";
            case "WeatherMiniOrb": return "Крошечный кружок-индикатор неба и погоды в углу экрана";
            case "HUD": return "Главный переключатель: включает или скрывает весь интерфейс клиента";
            case "CustomChat": return "Скруглённый чат с плавными анимациями и свободным перемещением";
            case "Watermark": return "Компактная плашка CreamClient 1.0 с FPS и пингом";
            case "Notifications": return "Плавные всплывающие уведомления о событиях и игроках рядом";
            case "CompassHUD": return "Полоса компаса сверху экрана (Север, Юг, Запад, Восток)";
            case "RadarHUD": return "Мини-радар на экране, показывающий ближайшие сущности точками";
            case "FPSGraph": return "График стабильности FPS в реальном времени";
            case "TargetHUD": return "Карточка цели под прицелом: имя, здоровье (HP) и дистанция";
            case "SessionInfo": return "Показывает длительность текущей игровой сессии и статистику";
            case "ScreenGlow": return "Мягкая цветная подсветка по краям экрана в цвет темы";
            case "CustomHotbar": return "Заменяет стандартный хотбар на плавный анимированный";
            case "Coordinates": return "Отображает твои точные координаты X Y Z и текущий биом";
            case "Keystrokes": return "Показывает нажатия WASD, пробела и твой CPS (кликов в секунду)";
            case "ModuleList": return "Аккуратный список всех включённых сейчас модулей";
            case "ArmorHUD": return "Показывает прочность надетой брони прямо на экране";
            case "PotionHUD": return "Показывает активные эффекты зелий и оставшееся время";
            case "Speedometer": return "Измеряет твою текущую скорость движения в блоках/сек";
            case "Crosshair": return "Минималистичный прицел в центре экрана в стиле клиента";
            case "ToggleSprint": return "Автоматически включает бег при движении вперёд";
            case "ToggleSneak": return "Держит присед включённым без постоянного удержания Shift";
            case "AutoRespawn": return "Автоматически нажимает Возродиться при смерти";
            case "ChatTime": return "Добавляет точное время [ЧЧ:ММ] перед каждым сообщением чата";
            case "AntiAFK": return "Периодически подпрыгивает, чтобы сервер не кикал за АФК";
            case "Appearance": return "Главная настройка цветов, скругления, прозрачности, отступов и языка";
            case "CustomMenus": return "Включает кастомное главное меню, мультиплеер, одиночный мир и паузу";
            case "BlackCursor": return "Заменяет стандартный курсор на стильный чёрный игровой курсор";
            case "DiscordRPC": return "Показывает в Discord твой трек, ник, сервер и кнопки GitHub";
            case "Background": return "Фон главного меню: картинка, созвездия или свой PNG/JPG";
            case "MenuMusic": return "Встроенный проигрыватель .wav музыки в меню и в игре";
            case "DeathCoords": return "Автоматически ставит красную метку там, где ты погиб";
            case "LowHealthPulse": return "Мягкое красное свечение по краям экрана при низком здоровье";
            case "SmoothFOV": return "Плавное изменение угла обзора (FOV) под себя";
            case "MediaHotkeys": return "Быстрое управление музыкой: F7 (Назад), F8 (Пауза), F9 (Вперёд)";
            case "ArmorAlert": return "Предупреждает уведомлением, если прочность брони упала ниже 15%";
            case "RealClock": return "Показывает реальное время (ЧЧ:ММ:СС) прямо на экране";
            case "UISounds": return "Приятные звуковые эффекты при нажатии вкладок, модулей и косметики";
            case "SmoothSwing": return "Настройка плавности, скорости и стиля удара рукой при атаке";
            case "PotatoOptimizer": return "Тонкая оптимизация для слабых ПК: чанки, частицы, свет и графика";
            default: return fallback;
        }
    }

    public static String trSetting(String name, boolean ru) {
        if (!ru) return name;
        switch (name) {
            case "SkyStyle": return "Стиль неба";
            case "Sky R": return "Небо R";
            case "Sky G": return "Небо G";
            case "Sky B": return "Небо B";
            case "Sky Alpha": return "Прозрачность неба";
            case "Spin Speed": return "Скорость вращения";
            case "Aurora": return "Северное сияние";
            case "Stars": return "Звёзды";
            case "Sync Fog": return "Синхронизация тумана";
            case "Jump Radius": return "Радиус круга";
            case "Jump Speed": return "Скорость круга";
            case "Trail Ms": return "Длина шлейфа (мс)";
            case "Max Points": return "Макс. точек";
            case "Cone Size": return "Размер конуса";
            case "Hat Alpha": return "Прозрачность";
            case "Hit Count": return "Кол-во искр";
            case "Line Width": return "Толщина линий";
            case "Players": return "Игроки";
            case "Mobs": return "Мобы";
            case "Fog Theme": return "Цвет тумана из темы";
            case "Fog R": return "Туман R";
            case "Fog G": return "Туман G";
            case "Fog B": return "Туман B";
            case "Time": return "Время суток";
            case "Hand X": return "Рука по X";
            case "Hand Y": return "Рука по Y";
            case "Hand Z": return "Рука по Z";
            case "Rot X": return "Поворот X";
            case "Rot Y": return "Поворот Y";
            case "Rot Z": return "Поворот Z";
            case "Scale": return "Масштаб";
            case "Spin Item": return "Вращать предмет";
            case "Zoom FOV": return "Угол зума (FOV)";
            case "ShowPing": return "Показывать пинг";
            case "Ping": return "Пинг";
            case "ShowFPS": return "Показывать FPS";
            case "Glow Alpha": return "Сила свечения";
            case "Hotbar Smooth": return "Плавность хотбара";
            case "Show CPS": return "Показывать CPS";
            case "Crosshair Size": return "Размер прицела";
            case "Grid Radius": return "Радиус сетки";
            case "Grid Alpha": return "Яркость сетки";
            case "3D Beam": return "3D Луч";
            case "3D Labels": return "3D Подпись";
            case "Beam Alpha": return "Прозрачность луча";
            case "Radius (m)": return "Радиус (метры)";
            case "Alert Toast": return "Уведомление";
            case "Sound Alert": return "Звуковой сигнал";
            case "Show Health": return "Показывать HP";
            case "Equalizer": return "Эквалайзер";
            case "Progress Bar": return "Полоса прогресса";
            case "Palette": return "Палитра";
            case "ColorMode": return "Режим цвета";
            case "Custom R": return "Красный (R)";
            case "Custom G": return "Зелёный (G)";
            case "Custom B": return "Синий (B)";
            case "Corner Radius": return "Скругление углов";
            case "Font": return "Шрифт";
            case "FontScale": return "Размер шрифта";
            case "Language": return "Язык";
            case "Panel Alpha": return "Прозрачность панели";
            case "Anim Speed": return "Скорость анимаций";
            case "Border Alpha": return "Прозрачность рамок";
            case "HUD Padding": return "Отступы HUD";
            case "Menu Logo": return "Логотип в меню";
            case "Particles": return "Частицы фона";
            case "Constellations": return "Линии созвездий";
            case "Splash Screen": return "Экран загрузки";
            case "Style Inventory": return "Стиль инвентаря";
            case "Show Server": return "Показывать сервер";
            case "Show Nick": return "Показывать ник";
            case "Show Buttons": return "Кнопки GitHub";
            case "BgType": return "Тип фона";
            case "Dimming": return "Затемнение фона";
            case "Source": return "Источник";
            case "Volume": return "Громкость";
            case "SwingStyle": return "Стиль удара";
            case "SwingSpeed": return "Скорость удара";
            case "SwingPower": return "Сила замаха";
            case "Chunks": return "Дальность (чанки)";
            case "EntityDist%": return "Дальность мобов %";
            case "ParticlesOpt": return "Частицы";
            case "GraphicsOpt": return "Графика";
            case "SmoothLight": return "Мягкий свет";
            case "CloudsOpt": return "Облака";
            case "Mipmaps": return "Мипмапы";
            case "MaxFPS": return "Лимит FPS";
            default: return name;
        }
    }
}