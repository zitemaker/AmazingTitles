package com.zitemaker.amazingtitles.code.Iintegrations;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderAPIIntegration {

    private static volatile boolean available;

    private PlaceholderAPIIntegration() {
    }

    public static void init() {
        available = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    public static boolean isAvailable() {
        return available;
    }

    public static String resolve(Player player, String text) {
        if (!available || text == null || text.indexOf('%') == -1)
            return text;
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
