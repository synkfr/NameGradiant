package com.gradientcolor.namegradient.model;

import org.bukkit.entity.Player;

public class Gradient {

    private final int id;
    private final String name;
    private final String startColour;
    private final String endColour;
    private final String alternatePermission;
    private final String overridePermission;
    private final int overrideWeight;

    public Gradient(int id, String name, String startColour, String endColour, 
                    String alternatePermission, String overridePermission, int overrideWeight) {
        this.id = id;
        this.name = name;
        this.startColour = startColour;
        this.endColour = endColour;
        this.alternatePermission = alternatePermission;
        this.overridePermission = overridePermission;
        this.overrideWeight = overrideWeight;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStartColour() {
        return startColour;
    }

    public String getEndColour() {
        return endColour;
    }

    public String getAlternatePermission() {
        return alternatePermission;
    }

    public String getOverridePermission() {
        return overridePermission;
    }

    public int getOverrideWeight() {
        return overrideWeight;
    }

    /**
     * Check if a player has permission to use this gradient
     */
    public boolean hasPermission(Player player) {
        // Check standard permission
        if (player.hasPermission("namegradient.gradient." + id)) {
            return true;
        }
        
        // Check alternate permission if set
        if (alternatePermission != null && !alternatePermission.isEmpty()) {
            return player.hasPermission(alternatePermission);
        }
        
        return false;
    }

    /**
     * Check if a player has the override permission for this gradient
     */
    public boolean hasOverridePermission(Player player) {
        if (overridePermission == null || overridePermission.isEmpty()) {
            return false;
        }
        return player.hasPermission(overridePermission);
    }

    /**
     * Get the permission string for this gradient
     */
    public String getPermissionString() {
        return "namegradient.gradient." + id;
    }
}
