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
}
