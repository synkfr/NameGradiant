package com.gradientcolor.namegradient.placeholders;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.util.GradientHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameGradientExpansion extends PlaceholderExpansion {

    private final NameGradient plugin;

    public NameGradientExpansion(NameGradient plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "namegradient";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().isEmpty() ? 
                "Unknown" : plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) return null;
        
        Player player = offlinePlayer.getPlayer();
        if (player == null) return offlinePlayer.getName();

        // %namegradient_name% - returns the player's name with gradient applied
        if (params.equalsIgnoreCase("name")) {
            return GradientHelper.applyGradientToName(plugin, player);
        }

        return null;
    }
}
