package com.gradientcolor.namegradient.listeners;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.config.okaeri.ChatFormatConfig;
import com.gradientcolor.namegradient.hooks.LuckPermsHook;
import com.gradientcolor.namegradient.util.ColorUtil;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

/**
 * Listener for chat events - handles LuckPerms group-based formatting
 */
public class ChatListener implements Listener {

    private final NameGradient plugin;

    public ChatListener(NameGradient plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ChatFormatConfig chatConfig = plugin.getChatFormatConfig();

        // Check if chat formatting is enabled
        if (!chatConfig.isEnable()) {
            return;
        }

        Player player = event.getPlayer();
        String format = getFormatForPlayer(player, chatConfig);

        // Apply placeholders
        format = applyPlaceholders(format, player, event.getMessage());

        // Apply colors (including &#RRGGBB support)
        format = ColorUtil.colorize(format);

        // Set the format (Bukkit expects %s for player name and %s for message, but
        // we're replacing everything)
        // We need to escape % signs that aren't our placeholders
        format = format.replace("%", "%%");

        event.setFormat(format);
    }

    /**
     * Get the appropriate format for a player based on their LuckPerms groups
     */
    private String getFormatForPlayer(Player player, ChatFormatConfig chatConfig) {
        if (!LuckPermsHook.isAvailable()) {
            return chatConfig.getDefaultFormat();
        }

        // Get player's primary group
        String primaryGroup = LuckPermsHook.getPrimaryGroup(player);

        // Check if the group has a format defined (case-insensitive)
        Map<String, ChatFormatConfig.GroupFormat> groups = chatConfig.getGroups();
        for (Map.Entry<String, ChatFormatConfig.GroupFormat> entry : groups.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(primaryGroup)) {
                return entry.getValue().getFormat();
            }
        }

        // Check if player is in any of the defined groups (for inherited groups)
        for (Map.Entry<String, ChatFormatConfig.GroupFormat> entry : groups.entrySet()) {
            if (LuckPermsHook.isInGroup(player, entry.getKey())) {
                return entry.getValue().getFormat();
            }
        }

        return chatConfig.getDefaultFormat();
    }

    /**
     * Apply placeholders to the format string
     */
    private String applyPlaceholders(String format, Player player, String message) {
        // Get player name with gradient applied
        String gradientName = GradientHelper.applyGradientToName(plugin, player);

        // Apply all placeholders
        format = format.replace("{player}", player.getName());
        format = format.replace("{displayname}", gradientName);
        format = format.replace("{message}", message);
        format = format.replace("{world}", player.getWorld().getName());

        // LuckPerms placeholders
        if (LuckPermsHook.isAvailable()) {
            format = format.replace("{prefix}", LuckPermsHook.getPrefix(player));
            format = format.replace("{suffix}", LuckPermsHook.getSuffix(player));
            format = format.replace("{group}", LuckPermsHook.getPrimaryGroup(player));
        } else {
            format = format.replace("{prefix}", "");
            format = format.replace("{suffix}", "");
            format = format.replace("{group}", "default");
        }

        return format;
    }
}
