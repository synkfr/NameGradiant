package com.gradientcolor.namegradient.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Hook for LuckPerms integration
 */
public class LuckPermsHook {

    private static LuckPerms luckPerms;
    private static boolean available = false;

    /**
     * Initialize the LuckPerms hook
     */
    public static void init(Logger logger) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                luckPerms = LuckPermsProvider.get();
                available = true;
                logger.info("LuckPerms found! Chat format integration enabled.");
            } catch (IllegalStateException e) {
                logger.warning("LuckPerms plugin found but API not available.");
                available = false;
            }
        } else {
            logger.info("LuckPerms not found. Chat format integration disabled.");
            available = false;
        }
    }

    /**
     * Check if LuckPerms is available
     */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Get a player's primary group
     */
    public static String getPrimaryGroup(Player player) {
        if (!available || luckPerms == null) {
            return "default";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "default";
        }

        return user.getPrimaryGroup();
    }

    /**
     * Get a player's prefix from LuckPerms
     */
    public static String getPrefix(Player player) {
        if (!available || luckPerms == null) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        return prefix != null ? prefix : "";
    }

    /**
     * Get a player's suffix from LuckPerms
     */
    public static String getSuffix(Player player) {
        if (!available || luckPerms == null) {
            return "";
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String suffix = metaData.getSuffix();
        return suffix != null ? suffix : "";
    }

    /**
     * Check if a player is in a specific group
     */
    public static boolean isInGroup(Player player, String groupName) {
        if (!available || luckPerms == null) {
            return false;
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return false;
        }

        // Check primary group first
        if (user.getPrimaryGroup().equalsIgnoreCase(groupName)) {
            return true;
        }

        // Check inherited groups
        return user.getInheritedGroups(user.getQueryOptions()).stream()
                .anyMatch(group -> group.getName().equalsIgnoreCase(groupName));
    }
}
