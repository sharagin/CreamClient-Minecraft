package com.creamclient.visual;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class CosmeticManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<CosmeticItem> ITEMS = new ArrayList<>();

    public static class CosmeticItem {
        public final String id;
        public final String nameRu;
        public final String categoryRu;
        public final int type; // 0=Head, 1=Back/Wings, 2=Pet/Orbit, 3=Aura/Floor, 4=Particles
        public final int color;
        public boolean enabled;

        public CosmeticItem(String id, String nameRu, String categoryRu, int type, int color) {
            this.id = id;
            this.nameRu = nameRu;
            this.categoryRu = categoryRu;
            this.type = type;
            this.color = color;
            this.enabled = false;
        }
    }

    static {
        // 1. Head / Hats / Ears (14)
        add("cat_ears", "Кошачьи ушки", "Голова", 0, 0xFFFF85C0);
        add("devil_horns", "Рога демона", "Голова", 0, 0xFFFF3B5C);
        add("angel_halo", "Золотой нимб", "Голова", 0, 0xFFFFD166);
        add("royal_crown", "Королевская корона", "Голова", 0, 0xFFFACC15);
        add("cyber_visor", "Кибер-визор", "Голова", 0, 0xFF00E5FF);
        add("samurai_crest", "Рога самурая", "Голова", 0, 0xFFFF6B6B);
        add("wizard_hat", "Шляпа мага", "Голова", 0, 0xFFC084FC);
        add("bunny_ears", "Ушки кролика", "Голова", 0, 0xFFF2F7EE);
        add("fox_ears", "Лисьи ушки", "Голова", 0, 0xFFFF9F1C);
        add("alien_antenna", "Антенна пришельца", "Голова", 0, 0xFFA3E635);
        add("viking_horns", "Рога викинга", "Голова", 0, 0xFFE2E8F0);
        add("neon_glasses", "Неоновые очки", "Голова", 0, 0xFF38BDF8);
        add("holo_crown", "Голо-корона", "Голова", 0, 0xFF2EFFB2);
        add("lunar_crest", "Полумесяц над головой", "Голова", 0, 0xFF93C5FD);

        // 2. Wings & Back Gear (10)
        add("angel_wings", "Крылья ангела", "Спина", 1, 0xFFF8FAFC);
        add("dragon_wings", "Крылья дракона", "Спина", 1, 0xFFEF4444);
        add("cyber_wings", "Кибер-крылья", "Спина", 1, 0xFF00E5FF);
        add("butterfly_wings", "Крылья бабочки", "Спина", 1, 0xFFF472B6);
        add("crystal_wings", "Кристальные крылья", "Спина", 1, 0xFFA3E635);
        add("bat_wings", "Крылья летучей мыши", "Спина", 1, 0xFF94A3B8);
        add("fairy_wings", "Крылья феи", "Спина", 1, 0xFF34D399);
        add("neon_katana", "Неоновая катана", "Спина", 1, 0xFF38BDF8);
        add("flame_cape", "Огненный плащ", "Спина", 1, 0xFFFF7A18);
        add("star_cape", "Звёздный плащ", "Спина", 1, 0xFFC084FC);

        // 3. Pets & Orbitals (10)
        add("floating_cube", "Летающий куб", "Питомец", 2, 0xFFA3E635);
        add("mini_moon", "Мини-Луна", "Питомец", 2, 0xFFE2E8F0);
        add("orbit_star", "Орбитальная звезда", "Питомец", 2, 0xFFFACC15);
        add("mini_drone", "Лайм-дрон", "Питомец", 2, 0xFF84CC16);
        add("ghost_pet", "Призрак-спутник", "Питомец", 2, 0xFFBAE6FD);
        add("pixel_heart", "Сердечко на орбите", "Питомец", 2, 0xFFFF4D6D);
        add("emerald_orb", "Изумрудная сфера", "Питомец", 2, 0xFF10B981);
        add("prism_shard", "Осколок призмы", "Питомец", 2, 0xFF22D3EE);
        add("glitch_box", "Глитч-куб", "Питомец", 2, 0xFFD946EF);
        add("solar_orb", "Солнечный шар", "Питомец", 2, 0xFFF59E0B);

        // 4. Auras, Shields & Rings (10)
        add("fire_aura", "Огненная аура", "Аура", 3, 0xFFFF5722);
        add("frost_aura", "Ледяная аура", "Аура", 3, 0xFF38BDF8);
        add("lightning_aura", "Аура молнии", "Аура", 3, 0xFFFDE047);
        add("void_aura", "Аура бездны", "Аура", 3, 0xFFA855F7);
        add("energy_shield", "Энерго-щит", "Аура", 3, 0xFF22D3EE);
        add("gyro_halo", "Гиро-сфера", "Аура", 3, 0xFFA3E635);
        add("laser_rings", "Лазерные кольца", "Аура", 3, 0xFFF43F5E);
        add("solar_ring", "Солнечное кольцо", "Аура", 3, 0xFFFBBF24);
        add("lime_vortex", "Лаймовый вихрь", "Аура", 3, 0xFF84CC16);
        add("quantum_ring", "Квантовый круг", "Аура", 3, 0xFF2EFFB2);

        // 5. Ambient Particles (10)
        add("sakura_petals", "Лепестки сакуры", "Частицы", 4, 0xFFF9A8D4);
        add("snowflakes", "Снежинки вокруг", "Частицы", 4, 0xFFE0F2FE);
        add("matrix_rain", "Дождь Матрицы", "Частицы", 4, 0xFF22C55E);
        add("star_dust", "Звёздная пыль", "Частицы", 4, 0xFFFDE047);
        add("emerald_sparks", "Изумрудные искры", "Частицы", 4, 0xFF34D399);
        add("music_notes", "Летающие ноты", "Частицы", 4, 0xFFC084FC);
        add("bubble_aura", "Пузырьки воздуха", "Частицы", 4, 0xFF7DD3FC);
        add("foot_flames", "Огонь под ногами", "Частицы", 4, 0xFFFB923C);
        add("rainbow_sparks", "Радужные искры", "Частицы", 4, 0xFFFF6B6B);
        add("cyber_dots", "Неоновые точки", "Частицы", 4, 0xFF00E5FF);
    }

    private CosmeticManager() {}

    private static void add(String id, String nameRu, String catRu, int type, int color) {
        ITEMS.add(new CosmeticItem(id, nameRu, catRu, type, color));
    }

    public static List<CosmeticItem> getItems() { return ITEMS; }

    public static void disableAll() {
        for (CosmeticItem item : ITEMS) item.enabled = false;
        save();
    }

    private static File getFile() {
        File dir = new File(Minecraft.getInstance().gameDirectory, "config/creamclient");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "cosmetics.json");
    }

    public static void save() {
        try {
            JsonArray arr = new JsonArray();
            for (CosmeticItem item : ITEMS) if (item.enabled) arr.add(item.id);
            try (FileWriter fw = new FileWriter(getFile())) {
                GSON.toJson(arr, fw);
            }
        } catch (Exception ignored) {}
    }

    public static void load() {
        File f = getFile();
        if (!f.exists()) return;
        try (FileReader fr = new FileReader(f)) {
            JsonArray arr = GSON.fromJson(fr, JsonArray.class);
            if (arr == null) return;
            for (CosmeticItem item : ITEMS) item.enabled = false;
            for (JsonElement el : arr) {
                String id = el.getAsString();
                for (CosmeticItem item : ITEMS) {
                    if (item.id.equals(id)) item.enabled = true;
                }
            }
        } catch (Exception ignored) {}
    }
}
