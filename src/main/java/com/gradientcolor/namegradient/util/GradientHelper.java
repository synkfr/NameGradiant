package com.gradientcolor.namegradient.util;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.config.okaeri.PluginConfig;
import com.gradientcolor.namegradient.model.Gradient;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GradientHelper {

    /**
     * Get the source name for a player based on the configured mode
     */
    public static String getSourceName(NameGradient plugin, Player player) {
        PluginConfig config = plugin.getPluginConfig();
        String mode = config.getNameMode().toUpperCase();

        switch (mode) {
            case "DISPLAYNAME":
                return ColorUtil.stripColors(player.getDisplayName());
            case "ESSENTIALS":
                if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                    try {
                        com.earth2me.essentials.Essentials ess = (com.earth2me.essentials.Essentials) Bukkit
                                .getPluginManager().getPlugin("Essentials");
                        if (ess != null) {
                            return ColorUtil.stripColors(ess.getUser(player).getNickname());
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to get Essentials nickname for " + player.getName());
                    }
                }
                return player.getName();
            case "CMI":
                // CMI integration would go here
                return player.getName();
            case "PLACEHOLDER":
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    String placeholder = config.getPlaceholderSource();
                    String result = PlaceholderAPI.setPlaceholders(player, placeholder);
                    return ColorUtil.stripColors(result);
                }
                return player.getName();
            case "PLAYERNAME":
            default:
                return player.getName();
        }
    }

    /**
     * Check if the source name has color codes (for colour-dependent mode)
     */
    public static boolean hasColouredSource(NameGradient plugin, Player player) {
        PluginConfig config = plugin.getPluginConfig();
        String mode = config.getNameMode().toUpperCase();

        switch (mode) {
            case "DISPLAYNAME":
                return ColorUtil.hasColorCodes(player.getDisplayName());
            case "ESSENTIALS":
                if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                    try {
                        com.earth2me.essentials.Essentials ess = (com.earth2me.essentials.Essentials) Bukkit
                                .getPluginManager().getPlugin("Essentials");
                        if (ess != null) {
                            String nick = ess.getUser(player).getNickname();
                            return nick != null && ColorUtil.hasColorCodes(nick);
                        }
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            case "PLACEHOLDER":
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    String placeholder = config.getPlaceholderSource();
                    String result = PlaceholderAPI.setPlaceholders(player, placeholder);
                    return ColorUtil.hasColorCodes(result);
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Get the active gradient for a player (returns null if none selected)
     */
    public static Gradient getActiveGradient(NameGradient plugin, Player player) {
        // Get the player's selected gradient
        Integer gradientId = plugin.getPlayerDataManager().getPlayerGradient(player.getUniqueId());
        if (gradientId != null) {
            return plugin.getGradientsConfig().getGradient(gradientId);
        }
        return null;
    }

    /**
     * Apply gradient to a player's name
     */
    public static String applyGradientToName(NameGradient plugin, Player player) {
        Gradient gradient = getActiveGradient(plugin, player);

        if (gradient == null) {
            return getSourceName(plugin, player);
        }

        // Check colour-dependent mode
        if (plugin.getPluginConfig().isColourDependent() && hasColouredSource(plugin, player)) {
            return getSourceName(plugin, player);
        }

        String sourceName = getSourceName(plugin, player);
        return ColorUtil.applyGradient(sourceName, gradient.getStartColour(), gradient.getEndColour());
    }

    /**
     * Update a player's display name with their gradient
     */
    public static void updatePlayerName(NameGradient plugin, Player player) {
        if (!plugin.getPluginConfig().isOverrideDisplayname()) {
            return;
        }

        String gradientName = applyGradientToName(plugin, player);
        player.setDisplayName(gradientName);
        player.setPlayerListName(gradientName);
    }

    /**
     * Get a preview of the gradient applied to a name
     */
    public static String getGradientPreview(Gradient gradient, String name) {
        return ColorUtil.applyGradient(name, gradient.getStartColour(), gradient.getEndColour());
    }
}
