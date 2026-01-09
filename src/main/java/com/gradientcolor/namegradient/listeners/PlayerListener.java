package com.gradientcolor.namegradient.listeners;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final NameGradient plugin;

    public PlayerListener(NameGradient plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Update player's display name with their gradient on join
        // Use a slight delay to ensure other plugins have processed the player
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            GradientHelper.updatePlayerName(plugin, player);
        }, 5L);
    }
}
