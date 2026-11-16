package com.creamclient.media;

import com.creamclient.theme.CreamTheme;
import com.creamclient.ui.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ResourcePackManager {
    private ResourcePackManager() {}

    public static class PackEntry {
        public final String id;
        public final String title;
        public final boolean enabled;

        public PackEntry(String id, String title, boolean enabled) {
            this.id = id;
            this.title = title;
            this.enabled = enabled;
        }
    }

    public static List<PackEntry> listPacks() {
        List<PackEntry> list = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        ResourcePackList repo = mc.getResourcePackRepository();
        repo.reload();
        Set<String> selectedIds = new LinkedHashSet<>();
        for (ResourcePackInfo info : repo.getSelectedPacks()) {
            selectedIds.add(info.getId());
        }
        for (ResourcePackInfo info : repo.getAvailablePacks()) {
            String id = info.getId();
            if ("vanilla".equals(id) || "mod_resources".equals(id) || "programer_art".equals(id)) continue;
            String title = info.getTitle().getString();
            if (title == null || title.isEmpty()) title = id;
            list.add(new PackEntry(id, title, selectedIds.contains(id)));
        }
        return list;
    }

    public static void togglePack(String packId) {
        Minecraft mc = Minecraft.getInstance();
        ResourcePackList repo = mc.getResourcePackRepository();
        repo.reload();
        List<String> selected = new ArrayList<>();
        for (ResourcePackInfo info : repo.getSelectedPacks()) {
            selected.add(info.getId());
        }
        if (selected.contains(packId)) {
            selected.remove(packId);
        } else {
            selected.add(packId);
        }
        if (!selected.contains("vanilla")) selected.add(0, "vanilla");
        if (!selected.contains("mod_resources")) {
            for (ResourcePackInfo av : repo.getAvailablePacks()) {
                if ("mod_resources".equals(av.getId())) {
                    selected.add("mod_resources");
                    break;
                }
            }
        }
        repo.setSelected(selected);
        mc.options.resourcePacks.clear();
        for (String id : selected) {
            if (!"vanilla".equals(id) && !"mod_resources".equals(id)) {
                mc.options.resourcePacks.add(id);
            }
        }
        mc.options.save();
        mc.execute(() -> {
            mc.reloadResourcePacks();
            NotificationManager.push("ResourcePack", packId, CreamTheme.getAccent());
        });
    }

    public static void applyPacks() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            mc.getResourcePackRepository().reload();
            mc.reloadResourcePacks();
            NotificationManager.push("ResourcePacks", "Applied!", CreamTheme.getAccent());
        });
    }

    public static void openFolder() {
        Util.getPlatform().openFile(Minecraft.getInstance().getResourcePackDirectory());
    }
}
