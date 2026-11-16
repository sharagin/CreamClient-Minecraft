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
            default: return name;
        }
    }

    public static String trDesc(String name, String fallback, boolean ru) {
        if (!ru) return fallback;
        switch (name) {
            case "BlockBreathe": return "Только что поставленный блок мягко пульсирует 2 секунды и гаснет";
            case "RainPuddle": return "После 5 секунд под дождём под ногами проявляется зеркальная лужа с бликами";
            case "Projectile2DTrail": return "Короткий 2D-мазок на экране за стрелами, снежками и жемчугом (200 мс)";
            case "WeatherMiniOrb": return "Крошечный индикатор неба (день/ночь/дождь/гроза) в углу экрана";
            case "NearbyPlayers": return "Предупреждает и показывает игроков рядом (по умолчанию 150 м)";
            case "Waypoints": return "Ставь 3D-метки с лучом и дистанцией на клавишу M";
            case "MusicPlayerHUD": return "Показывает текущий трек, эквалайзер и прогресс на экране";
            case "HandVisuals": return "Меняй позицию X/Y/Z, поворот, размер и вращение предмета в руке";
            case "CustomSky": return "20 стилей неба, настройка цвета RGB, звёзд, вращения и тумана";
            case "Appearance": return "Настройка цветов RGB, скругления, прозрачности, отступов и анимаций";
            case "DiscordRPC": return "Показывает твой трек, ник, сервер и кнопки GitHub в Discord";
            default: return fallback;
        }
    }
}