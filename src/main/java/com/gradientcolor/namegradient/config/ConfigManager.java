package com.gradientcolor.namegradient.config;

import com.gradientcolor.namegradient.NameGradient;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final NameGradient plugin;
    private FileConfiguration config;
    private File configFile;

    // Name settings
    private String nameMode;
    private boolean colourDependent;
    private String placeholderSource;
    private boolean overrideDisplayname;

    // Menu settings
    private String menuTitle;
    private int menuSize;
    private int perPage;
    private List<Integer> paneSlots;
    private String paneName;
    private Material paneMaterial;
    private Material gradientItemMaterial;
    private List<String> gradientLorePermission;
    private List<String> gradientLoreNoPermission;
    private int pageBackSlot;
    private Material pageBackMaterial;
    private String pageBackName;
    private int pageForwardSlot;
    private Material pageForwardMaterial;
    private String pageForwardName;
    private int clearGradientSlot;
    private Material clearGradientMaterial;
    private String clearGradientName;

    public ConfigManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Load name settings
        nameMode = config.getString("name.mode", "PLAYERNAME");
        colourDependent = config.getBoolean("name.colour-dependent", false);
        placeholderSource = config.getString("name.placeholder-source", "%player_name%");
        overrideDisplayname = config.getBoolean("name.override-displayname", true);

        // Load menu settings
        menuTitle = config.getString("menu.title", "&8Gradients");
        menuSize = config.getInt("menu.size", 45);
        perPage = config.getInt("menu.per_page", 21);
        paneSlots = config.getIntegerList("menu.panes.slots");
        paneName = config.getString("menu.panes.name", "&7");
        paneMaterial = Material.matchMaterial(config.getString("menu.panes.material", "BLACK_STAINED_GLASS_PANE"));
        gradientItemMaterial = Material.matchMaterial(config.getString("menu.gradient_item.material", "NAME_TAG"));
        gradientLorePermission = config.getStringList("menu.gradient_item.lore_permission");
        gradientLoreNoPermission = config.getStringList("menu.gradient_item.lore_no_permission");
        pageBackSlot = config.getInt("menu.page_back_item.slot", 39);
        pageBackMaterial = Material.matchMaterial(config.getString("menu.page_back_item.material", "ARROW"));
        pageBackName = config.getString("menu.page_back_item.name", "&7Page Back");
        pageForwardSlot = config.getInt("menu.page_forward_item.slot", 41);
        pageForwardMaterial = Material.matchMaterial(config.getString("menu.page_forward_item.material", "ARROW"));
        pageForwardName = config.getString("menu.page_forward_item.name", "&7Page Forward");
        clearGradientSlot = config.getInt("menu.clear_gradient_item.slot", 40);
        clearGradientMaterial = Material.matchMaterial(config.getString("menu.clear_gradient_item.material", "BARRIER"));
        clearGradientName = config.getString("menu.clear_gradient_item.name", "&cClear current gradient");

        // Validate menu size
        if (menuSize % 9 != 0 || menuSize < 9 || menuSize > 54) {
            menuSize = 45;
            plugin.getLogger().warning("Invalid menu size in config. Using default: 45");
        }

        // Set default materials if null
        if (paneMaterial == null) paneMaterial = Material.BLACK_STAINED_GLASS_PANE;
        if (gradientItemMaterial == null) gradientItemMaterial = Material.NAME_TAG;
        if (pageBackMaterial == null) pageBackMaterial = Material.ARROW;
        if (pageForwardMaterial == null) pageForwardMaterial = Material.ARROW;
        if (clearGradientMaterial == null) clearGradientMaterial = Material.BARRIER;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml!");
            e.printStackTrace();
        }
    }

    // Getters
    public String getNameMode() { return nameMode; }
    public boolean isColourDependent() { return colourDependent; }
    public String getPlaceholderSource() { return placeholderSource; }
    public boolean isOverrideDisplayname() { return overrideDisplayname; }
    public String getMenuTitle() { return menuTitle; }
    public int getMenuSize() { return menuSize; }
    public int getPerPage() { return perPage; }
    public List<Integer> getPaneSlots() { return paneSlots; }
    public String getPaneName() { return paneName; }
    public Material getPaneMaterial() { return paneMaterial; }
    public Material getGradientItemMaterial() { return gradientItemMaterial; }
    public List<String> getGradientLorePermission() { return gradientLorePermission; }
    public List<String> getGradientLoreNoPermission() { return gradientLoreNoPermission; }
    public int getPageBackSlot() { return pageBackSlot; }
    public Material getPageBackMaterial() { return pageBackMaterial; }
    public String getPageBackName() { return pageBackName; }
    public int getPageForwardSlot() { return pageForwardSlot; }
    public Material getPageForwardMaterial() { return pageForwardMaterial; }
    public String getPageForwardName() { return pageForwardName; }
    public int getClearGradientSlot() { return clearGradientSlot; }
    public Material getClearGradientMaterial() { return clearGradientMaterial; }
    public String getClearGradientName() { return clearGradientName; }
}
