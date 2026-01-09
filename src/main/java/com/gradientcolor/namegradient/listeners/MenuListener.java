package com.gradientcolor.namegradient.listeners;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.gui.GradientMenu;
import com.gradientcolor.namegradient.model.Gradient;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MenuListener implements Listener {

    private final NameGradient plugin;

    public MenuListener(NameGradient plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        // Check if the inventory holder is our menu
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof GradientMenu)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        GradientMenu menu = (GradientMenu) event.getInventory().getHolder();
        
        // Make sure click is in the top inventory (our menu), not player inventory
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }
        
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Extract action from PDC (for navigation and clear buttons)
        String action = GradientMenu.extractAction(plugin, clickedItem);
        if (action != null) {
            switch (action) {
                case GradientMenu.ACTION_PAGE_BACK:
                    handlePageNavigation(player, menu, -1);
                    return;
                case GradientMenu.ACTION_PAGE_FORWARD:
                    handlePageNavigation(player, menu, 1);
                    return;
                case GradientMenu.ACTION_CLEAR:
                    handleClearGradient(player);
                    return;
            }
        }

        // Check for gradient selection using PDC
        int gradientId = GradientMenu.extractGradientId(plugin, clickedItem);
        if (gradientId != -1) {
            handleGradientSelection(player, gradientId);
        }
        // No debug message needed - pane items will just be ignored
    }

    private void handlePageNavigation(Player player, GradientMenu menu, int direction) {
        int newPage = menu.getPage() + direction;

        if (newPage >= 0) {
            player.closeInventory();
            new GradientMenu(plugin, player, newPage).open();
        }
    }

    private void handleClearGradient(Player player) {
        // Clear the gradient - always allow
        plugin.getPlayerDataManager().clearPlayerGradient(player.getUniqueId());
        GradientHelper.updatePlayerName(plugin, player);
        player.sendMessage(plugin.getMessageManager().getMessage("clear"));
        player.closeInventory();
    }

    private void handleGradientSelection(Player player, int gradientId) {
        Gradient gradient = plugin.getGradientManager().getGradient(gradientId);
        
        if (gradient == null) {
            player.sendMessage(plugin.getMessageManager().getMessage("gradient_nonexistent"));
            return;
        }

        // Check if player has permission to use this gradient
        if (!gradient.hasPermission(player)) {
            player.sendMessage(plugin.getMessageManager().getMessage("no_permission_gradient"));
            return;
        }

        // Apply the gradient - allow changing anytime
        plugin.getPlayerDataManager().setPlayerGradient(player.getUniqueId(), gradientId);
        GradientHelper.updatePlayerName(plugin, player);

        // Send success message with gradient preview
        String gradientName = GradientHelper.getGradientPreview(gradient, player.getName());
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{GRADIENT}", gradientName);
        player.sendMessage(plugin.getMessageManager().getMessage("apply", placeholders));
        
        player.closeInventory();
    }
}
