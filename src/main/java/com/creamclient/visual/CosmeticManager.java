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
        public final int type;   // 0=Head, 1=Back/Wings, 2=Pet/Orbit, 3=Aura/Floor, 4=Particles
        public final int shape;  // distinct sub-geometry ID
        public final int color;
        public boolean enabled;

        public CosmeticItem(String id, String nameRu, String categoryRu, int type, int shape, int color) {
            this.id = id;
            this.nameRu = nameRu;
            this.categoryRu = categoryRu;
            this.type = type;
            this.shape = shape;
            this.color = color;
            this.enabled = false;
        }
    }

    static {
        // === 1. HEAD (22) === (shapes: 0=ears, 1=bunny_ears, 2=horns, 3=halo, 4=crown, 5=visor, 6=wizard_hat, 7=antenna, 8=samurai, 9=crescent)
        add("cat_ears", "Кошачьи ушки", "Голова", 0, 0, 0xFFFF85C0);
        add("fox_ears", "Лисьи ушки", "Голова", 0, 0, 0xFFFF9F1C);
        add("wolf_ears", "Волчьи уши", "Голова", 0, 0, 0xFF94A3B8);
        add("neko_lime", "Лайм Неко-ушки", "Голова", 0, 0, 0xFFA3E635);
        add("bunny_ears", "Длинные ушки кролика", "Голова", 0, 1, 0xFFF8FAFC);
        add("cyber_bunny", "Кибер-уши кролика", "Голова", 0, 1, 0xFF00E5FF);
        add("devil_horns", "Рога демона", "Голова", 0, 2, 0xFFFF3B5C);
        add("viking_horns", "Рога викинга", "Голова", 0, 2, 0xFFE2E8F0);
        add("oni_horns", "Рога Они", "Голова", 0, 2, 0xFFC084FC);
        add("angel_halo", "Золотой нимб", "Голова", 0, 3, 0xFFFFD166);
        add("cyber_halo", "Неоновый нимб", "Голова", 0, 3, 0xFF2EFFB2);
        add("royal_crown", "Королевская корона", "Голова", 0, 4, 0xFFFACC15);
        add("ice_crown", "Ледяная корона", "Голова", 0, 4, 0xFF38BDF8);
        add("emerald_crown", "Изумрудная корона", "Голова", 0, 4, 0xFF10B981);
        add("cyber_visor", "Кибер-визор", "Голова", 0, 5, 0xFF00E5FF);
        add("neon_glasses", "Неоновые очки", "Голова", 0, 5, 0xFFF472B6);
        add("tactical_visor", "Тактический визор", "Голова", 0, 5, 0xFFA3E635);
        add("wizard_hat", "Шляпа мага", "Голова", 0, 6, 0xFFC084FC);
        add("witch_hat", "Шляпа ведьмы", "Голова", 0, 6, 0xFF6366F1);
        add("alien_antenna", "Антенна пришельца", "Голова", 0, 7, 0xFFA3E635);
        add("samurai_crest", "Шлем самурая", "Голова", 0, 8, 0xFFFF6B6B);
        add("lunar_crest", "Полумесяц Луны", "Голова", 0, 9, 0xFF93C5FD);

        // === 2. BACK / WINGS (20) === (shapes: 0=angel, 1=dragon, 2=butterfly, 3=cyber, 4=katana, 5=cape, 6=jetpack, 7=shield)
        add("angel_wings", "Крылья ангела", "Спина", 1, 0, 0xFFF8FAFC);
        add("seraph_wings", "Крылья серафима", "Спина", 1, 0, 0xFFFFD166);
        add("fallen_wings", "Крылья падшего", "Спина", 1, 0, 0xFF94A3B8);
        add("dragon_wings", "Крылья дракона", "Спина", 1, 1, 0xFFEF4444);
        add("bat_wings", "Крылья летучей мыши", "Спина", 1, 1, 0xFF64748B);
        add("void_dragon", "Крылья дракона Бездны", "Спина", 1, 1, 0xFFA855F7);
        add("butterfly_pink", "Крылья бабочки (Розовые)", "Спина", 1, 2, 0xFFF472B6);
        add("butterfly_cyan", "Крылья бабочки (Голубые)", "Спина", 1, 2, 0xFF38BDF8);
        add("fairy_wings", "Крылья феи", "Спина", 1, 2, 0xFF34D399);
        add("cyber_wings", "Кибер-крылья", "Спина", 1, 3, 0xFF00E5FF);
        add("lime_mech_wings", "Лайм меха-крылья", "Спина", 1, 3, 0xFFA3E635);
        add("crimson_mech", "Алые меха-крылья", "Спина", 1, 3, 0xFFFF3B5C);
        add("neon_katana", "Неоновая катана", "Спина", 1, 4, 0xFF38BDF8);
        add("crimson_katana", "Алая катана", "Спина", 1, 4, 0xFFFF4D6D);
        add("twin_blades", "Парные клинки", "Спина", 1, 4, 0xFFA3E635);
        add("flame_cape", "Огненный плащ", "Спина", 1, 5, 0xFFFF7A18);
        add("star_cape", "Звёздный плащ", "Спина", 1, 5, 0xFFC084FC);
        add("lime_cape", "Лаймовый плащ", "Спина", 1, 5, 0xFF84CC16);
        add("cyber_jetpack", "Реактивный ранец", "Спина", 1, 6, 0xFFF59E0B);
        add("aegis_shield", "Щит Эгиды за спиной", "Спина", 1, 7, 0xFF22D3EE);

        // === 3. PETS & ORBITALS (20) === (shapes: 0=cube, 1=crystal, 2=saturn, 3=heart, 4=drone, 5=ghost, 6=star, 7=sword_orbit)
        add("floating_cube", "Летающий куб", "Питомец", 2, 0, 0xFFA3E635);
        add("glitch_box", "Глитч-куб", "Питомец", 2, 0, 0xFFD946EF);
        add("obsidian_cube", "Обсидиан-куб", "Питомец", 2, 0, 0xFF818CF8);
        add("emerald_crystal", "Изумрудный кристалл", "Питомец", 2, 1, 0xFF10B981);
        add("prism_shard", "Осколок призмы", "Питомец", 2, 1, 0xFF22D3EE);
        add("ruby_crystal", "Рубиновый кристалл", "Питомец", 2, 1, 0xFFF43F5E);
        add("saturn_planet", "Планета Сатурн", "Питомец", 2, 2, 0xFFFBBF24);
        add("mini_moon", "Мини-Луна с орбитой", "Питомец", 2, 2, 0xFFE2E8F0);
        add("cyber_planet", "Кибер-планета", "Питомец", 2, 2, 0xFF00E5FF);
        add("pixel_heart", "Сердечко-спутник", "Питомец", 2, 3, 0xFFFF4D6D);
        add("gold_heart", "Золотое сердце", "Питомец", 2, 3, 0xFFFACC15);
        add("lime_drone", "Лайм-дрон", "Питомец", 2, 4, 0xFF84CC16);
        add("recon_drone", "Развед-дрон", "Питомец", 2, 4, 0xFF38BDF8);
        add("ghost_pet", "Призрак-спутник", "Питомец", 2, 5, 0xFFBAE6FD);
        add("shadow_spirit", "Тёмный дух", "Питомец", 2, 5, 0xFFA855F7);
        add("orbit_star", "Орбитальная звезда", "Питомец", 2, 6, 0xFFFACC15);
        add("nova_star", "Сверхновая звезда", "Питомец", 2, 6, 0xFFFF9F1C);
        add("orbit_sword", "Парящий клинок", "Питомец", 2, 7, 0xFF38BDF8);
        add("flame_sword_pet", "Огненный клинок", "Питомец", 2, 7, 0xFFFF5722);
        add("solar_orb", "Солнечная сфера", "Питомец", 2, 2, 0xFFF59E0B);

        // === 4. AURAS & SIGILS (20) === (shapes: 0=sigil_star, 1=helix, 2=dome, 3=double_ring, 4=lotus, 5=lightning, 6=square_sigil)
        add("arcane_sigil", "Магическая печать", "Аура", 3, 0, 0xFFC084FC);
        add("crimson_pentagram", "Алая печать", "Аура", 3, 0, 0xFFFF3B5C);
        add("lime_hexagram", "Лаймовая звезда", "Аура", 3, 0, 0xFFA3E635);
        add("dna_helix", "ДНК-спираль", "Аура", 3, 1, 0xFF2EFFB2);
        add("fire_helix", "Огненный вихрь", "Аура", 3, 1, 0xFFFF5722);
        add("frost_helix", "Ледяной вихрь", "Аура", 3, 1, 0xFF38BDF8);
        add("energy_shield", "Энерго-купол", "Аура", 3, 2, 0xFF22D3EE);
        add("void_dome", "Купол Бездны", "Аура", 3, 2, 0xFFA855F7);
        add("gyro_halo", "Гиро-сфера", "Аура", 3, 3, 0xFFA3E635);
        add("laser_rings", "Лазерные кольца", "Аура", 3, 3, 0xFFF43F5E);
        add("quantum_ring", "Квантовый круг", "Аура", 3, 3, 0xFF2EFFB2);
        add("lotus_aura", "Аура лотоса", "Аура", 3, 4, 0xFFF472B6);
        add("solar_lotus", "Солнечный лотос", "Аура", 3, 4, 0xFFFBBF24);
        add("lightning_aura", "Аура молнии", "Аура", 3, 5, 0xFFFDE047);
        add("storm_aura", "Грозовая аура", "Аура", 3, 5, 0xFF60A5FA);
        add("cyber_square", "Кибер-квадрат пола", "Аура", 3, 6, 0xFF00E5FF);
        add("emerald_square", "Изумрудный квадрат", "Аура", 3, 6, 0xFF10B981);
        add("gold_vortex", "Золотая орбита", "Аура", 3, 3, 0xFFFACC15);
        add("blood_ring", "Багровый круг", "Аура", 3, 0, 0xFFDC2626);
        add("mint_wave", "Мятная волна", "Аура", 3, 4, 0xFF34D399);

        // === 5. PARTICLES (18) === (shapes: 0=cross_snow, 1=falling_rain, 2=rising_spark, 3=orbit_dot, 4=zigzag)
        add("snowflakes", "Снежинки (*)", "Частицы", 4, 0, 0xFFE0F2FE);
        add("ice_crystals", "Кристаллы льда", "Частицы", 4, 0, 0xFF38BDF8);
        add("star_dust", "Звёздная пыль", "Частицы", 4, 0, 0xFFFDE047);
        add("matrix_rain", "Дождь Матрицы", "Частицы", 4, 1, 0xFF22C55E);
        add("cyber_rain", "Неоновый дождь", "Частицы", 4, 1, 0xFF00E5FF);
        add("blood_rain", "Алый дождь", "Частицы", 4, 1, 0xFFEF4444);
        add("foot_flames", "Искры пламени", "Частицы", 4, 2, 0xFFFB923C);
        add("emerald_sparks", "Изумрудные искры", "Частицы", 4, 2, 0xFF34D399);
        add("lime_embers", "Лаймовые угли", "Частицы", 4, 2, 0xFFA3E635);
        add("soul_embers", "Огни душ", "Частицы", 4, 2, 0xFF22D3EE);
        add("sakura_petals", "Лепестки сакуры", "Частицы", 4, 3, 0xFFF9A8D4);
        add("autumn_leaves", "Осенние листья", "Частицы", 4, 3, 0xFFF97316);
        add("music_notes", "Летающие ноты", "Частицы", 4, 3, 0xFFC084FC);
        add("bubble_aura", "Пузырьки воздуха", "Частицы", 4, 3, 0xFF7DD3FC);
        add("golden_dust", "Золотая пыльца", "Частицы", 4, 3, 0xFFFACC15);
        add("electric_sparks", "Электро-разряды", "Частицы", 4, 4, 0xFFFDE047);
        add("void_sparks", "Искры Бездны", "Частицы", 4, 4, 0xFFA855F7);
        add("rainbow_sparks", "Радужные искры", "Частицы", 4, 4, 0xFFFF6B6B);
    }

    private CosmeticManager() {}

    private static void add(String id, String nameRu, String catRu, int type, int shape, int color) {
        ITEMS.add(new CosmeticItem(id, nameRu, catRu, type, shape, color));
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
