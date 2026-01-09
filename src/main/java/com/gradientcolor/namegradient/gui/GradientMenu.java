package com.gradientcolor.namegradient.gui;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.model.Gradient;
import com.gradientcolor.namegradient.util.ColorUtil;
import com.gradientcolor.namegradient.util.GradientHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GradientMenu implements InventoryHolder {

    // PDC Keys for item identification
    private static NamespacedKey keyGradientId;
    private static NamespacedKey keyAction;
    
    // Action values
    public static final String ACTION_CLEAR = "clear";
    public static final String ACTION_PAGE_BACK = "page_back";
    public static final String ACTION_PAGE_FORWARD = "page_forward";

    private final NameGradient plugin;
    private final Player player;
    private final int page;
    private Inventory inventory;

    public GradientMenu(NameGradient plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        
        // Initialize keys if not already done
        if (keyGradientId == null) {
            keyGradientId = new NamespacedKey(plugin, "gradient_id");
            keyAction = new NamespacedKey(plugin, "menu_action");
        }
        
        createInventory();
    }

    private void createInventory() {
        String title = ColorUtil.colorize(plugin.getConfigManager().getMenuTitle());
        int size = plugin.getConfigManager().getMenuSize();
        
        inventory = Bukkit.createInventory(this, size, title);
        
        // Fill pane slots
        fillPanes();
        
        // Add gradient items
        addGradientItems();
        
        // Add navigation items
        addNavigationItems();
    }

    private void fillPanes() {
        ItemStack pane = new ItemStack(plugin.getConfigManager().getPaneMaterial());
        ItemMeta paneMeta = pane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(ColorUtil.colorize(plugin.getConfigManager().getPaneName()));
            pane.setItemMeta(paneMeta);
        }

        for (int slot : plugin.getConfigManager().getPaneSlots()) {
            if (slot < inventory.getSize()) {
                inventory.setItem(slot, pane);
            }
        }
    }

    private void addGradientItems() {
        Map<Integer, Gradient> allGradients = plugin.getGradientManager().getAllGradients();
        List<Gradient> gradientList = new ArrayList<>(allGradients.values());
        
        int perPage = plugin.getConfigManager().getPerPage();
        int startIndex = page * perPage;
        int endIndex = Math.min(startIndex + perPage, gradientList.size());

        // Get available slots (excluding pane slots and navigation slots)
        List<Integer> availableSlots = getAvailableSlots();
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex && slotIndex < availableSlots.size(); i++) {
            Gradient gradient = gradientList.get(i);
            ItemStack item = createGradientItem(gradient);
            inventory.setItem(availableSlots.get(slotIndex), item);
            slotIndex++;
        }
    }

    private List<Integer> getAvailableSlots() {
        List<Integer> paneSlots = plugin.getConfigManager().getPaneSlots();
        int pageBackSlot = plugin.getConfigManager().getPageBackSlot();
        int pageForwardSlot = plugin.getConfigManager().getPageForwardSlot();
        int clearSlot = plugin.getConfigManager().getClearGradientSlot();
        
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!paneSlots.contains(i) && i != pageBackSlot && i != pageForwardSlot && i != clearSlot) {
                available.add(i);
            }
        }
        return available;
    }

    private ItemStack createGradientItem(Gradient gradient) {
        ItemStack item = new ItemStack(plugin.getConfigManager().getGradientItemMaterial());
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Apply gradient to the item name
            String gradientName = GradientHelper.getGradientPreview(gradient, gradient.getName());
            meta.setDisplayName(gradientName);
            
            // Set lore based on permission
            List<String> lore = new ArrayList<>();
            lore.add(""); // Empty line
            lore.add(ColorUtil.colorize("&7Preview: ") + 
                    GradientHelper.getGradientPreview(gradient, player.getName()));
            lore.add("");
            
            if (gradient.hasPermission(player)) {
                for (String line : plugin.getConfigManager().getGradientLorePermission()) {
                    lore.add(ColorUtil.colorize(line));
                }
            } else {
                for (String line : plugin.getConfigManager().getGradientLoreNoPermission()) {
                    lore.add(ColorUtil.colorize(line));
                }
            }
            
            meta.setLore(lore);
            
            // Store gradient ID using PersistentDataContainer (modern approach)
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(keyGradientId, PersistentDataType.INTEGER, gradient.getId());
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    private void addNavigationItems() {
        Map<Integer, Gradient> allGradients = plugin.getGradientManager().getAllGradients();
        int perPage = plugin.getConfigManager().getPerPage();
        int totalPages = (int) Math.ceil((double) allGradients.size() / perPage);

        // Page back item
        if (page > 0) {
            ItemStack pageBack = new ItemStack(plugin.getConfigManager().getPageBackMaterial());
            ItemMeta backMeta = pageBack.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(ColorUtil.colorize(plugin.getConfigManager().getPageBackName()));
                
                // Store action using PDC
                PersistentDataContainer pdc = backMeta.getPersistentDataContainer();
                pdc.set(keyAction, PersistentDataType.STRING, ACTION_PAGE_BACK);
                
                pageBack.setItemMeta(backMeta);
            }
            inventory.setItem(plugin.getConfigManager().getPageBackSlot(), pageBack);
        }

        // Page forward item
        if (page < totalPages - 1) {
            ItemStack pageForward = new ItemStack(plugin.getConfigManager().getPageForwardMaterial());
            ItemMeta forwardMeta = pageForward.getItemMeta();
            if (forwardMeta != null) {
                forwardMeta.setDisplayName(ColorUtil.colorize(plugin.getConfigManager().getPageForwardName()));
                
                // Store action using PDC
                PersistentDataContainer pdc = forwardMeta.getPersistentDataContainer();
                pdc.set(keyAction, PersistentDataType.STRING, ACTION_PAGE_FORWARD);
                
                pageForward.setItemMeta(forwardMeta);
            }
            inventory.setItem(plugin.getConfigManager().getPageForwardSlot(), pageForward);
        }

        // Clear gradient item
        ItemStack clearItem = new ItemStack(plugin.getConfigManager().getClearGradientMaterial());
        ItemMeta clearMeta = clearItem.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName(ColorUtil.colorize(plugin.getConfigManager().getClearGradientName()));
            
            // Store action using PDC
            PersistentDataContainer pdc = clearMeta.getPersistentDataContainer();
            pdc.set(keyAction, PersistentDataType.STRING, ACTION_CLEAR);
            
            clearItem.setItemMeta(clearMeta);
        }
        inventory.setItem(plugin.getConfigManager().getClearGradientSlot(), clearItem);
    }

    public void open() {
        player.openInventory(inventory);
    }

    public int getPage() {
        return page;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Extract gradient ID from item using PDC
     */
    public static int extractGradientId(NameGradient plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return -1;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;
        
        NamespacedKey key = new NamespacedKey(plugin, "gradient_id");
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        if (pdc.has(key, PersistentDataType.INTEGER)) {
            Integer id = pdc.get(key, PersistentDataType.INTEGER);
            return id != null ? id : -1;
        }
        
        return -1;
    }

    /**
     * Extract action from item using PDC
     */
    public static String extractAction(NameGradient plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        
        NamespacedKey key = new NamespacedKey(plugin, "menu_action");
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        
        if (pdc.has(key, PersistentDataType.STRING)) {
            return pdc.get(key, PersistentDataType.STRING);
        }
        
        return null;
    }
}
