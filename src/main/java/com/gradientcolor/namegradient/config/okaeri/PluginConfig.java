package com.gradientcolor.namegradient.config.okaeri;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@Header("## NAME SETTINGS")
@Header("## Altering these settings will likely require a server restart due to the varying dependency checks within different modes.")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfig extends OkaeriConfig {

    // Name settings
    @Comment("Available modes: PLAYERNAME, DISPLAYNAME, ESSENTIALS, CMI, PLACEHOLDER")
    private String nameMode = "PLAYERNAME";

    @Comment("If true, NameGradient will not apply a gradient if the name already has colours present")
    private boolean colourDependent = false;

    @Comment("The PlaceholderAPI placeholder to use if mode is set to PLACEHOLDER")
    private String placeholderSource = "%player_name%";

    @Comment("Must be false when using DISPLAYNAME or ESSENTIALS modes")
    private boolean overrideDisplayname = true;

    // Menu settings
    @Comment("")
    @Comment("## GUI/MENU SETTINGS")
    private String menuTitle = "§8Gradients";

    @Comment("Must be a multiple of 9")
    private int menuSize = 45;

    @Comment("Amount of gradients shown per page")
    private int perPage = 21;

    // Pane settings
    private List<Integer> paneSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 42,
            43, 44);
    private String paneName = "§7";
    private Material paneMaterial = Material.BLACK_STAINED_GLASS_PANE;

    // Gradient item settings
    private Material gradientItemMaterial = Material.NAME_TAG;
    private List<String> gradientLorePermission = Arrays.asList("§7♦ §eClick to apply this gradient.");
    private List<String> gradientLoreNoPermission = Arrays.asList("§7♦ §cYou don't have sufficient permission.");

    // Navigation items
    private int pageBackSlot = 39;
    private Material pageBackMaterial = Material.ARROW;
    private String pageBackName = "§7Page Back";

    private int pageForwardSlot = 41;
    private Material pageForwardMaterial = Material.ARROW;
    private String pageForwardName = "§7Page Forward";

    private int clearGradientSlot = 40;
    private Material clearGradientMaterial = Material.BARRIER;
    private String clearGradientName = "§cClear current gradient";

    // Getters
    public String getNameMode() {
        return nameMode;
    }

    public boolean isColourDependent() {
        return colourDependent;
    }

    public String getPlaceholderSource() {
        return placeholderSource;
    }

    public boolean isOverrideDisplayname() {
        return overrideDisplayname;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public int getMenuSize() {
        return menuSize;
    }

    public int getPerPage() {
        return perPage;
    }

    public List<Integer> getPaneSlots() {
        return paneSlots;
    }

    public String getPaneName() {
        return paneName;
    }

    public Material getPaneMaterial() {
        return paneMaterial;
    }

    public Material getGradientItemMaterial() {
        return gradientItemMaterial;
    }

    public List<String> getGradientLorePermission() {
        return gradientLorePermission;
    }

    public List<String> getGradientLoreNoPermission() {
        return gradientLoreNoPermission;
    }

    public int getPageBackSlot() {
        return pageBackSlot;
    }

    public Material getPageBackMaterial() {
        return pageBackMaterial;
    }

    public String getPageBackName() {
        return pageBackName;
    }

    public int getPageForwardSlot() {
        return pageForwardSlot;
    }

    public Material getPageForwardMaterial() {
        return pageForwardMaterial;
    }

    public String getPageForwardName() {
        return pageForwardName;
    }

    public int getClearGradientSlot() {
        return clearGradientSlot;
    }

    public Material getClearGradientMaterial() {
        return clearGradientMaterial;
    }

    public String getClearGradientName() {
        return clearGradientName;
    }
}
