package com.gradientcolor.namegradient.config.okaeri;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Header("## CHAT FORMAT SETTINGS")
@Header("## Placeholders: {player}, {displayname}, {message}, {world}, {prefix}, {suffix}")
@Header("## Supports &#RRGGBB hex color codes and standard &c color codes")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_UPPER_CASE)
public class ChatFormatConfig extends OkaeriConfig {

    @Comment("Enable or disable chat formatting")
    private boolean enable = true;

    @Comment("Default format when player has no matching group")
    private String defaultFormat = "&8[&7Default&8] &7{player} &9> &7{message}";

    @Comment("Group-specific chat formats")
    @Comment("Higher priority groups should be listed first if player has multiple")
    private Map<String, GroupFormat> groups = createDefaultGroups();

    private static Map<String, GroupFormat> createDefaultGroups() {
        Map<String, GroupFormat> groups = new LinkedHashMap<>();
        groups.put("Default", new GroupFormat("&8[&7Default&8] &7{player} &9> &7{message}"));
        groups.put("VIP", new GroupFormat("&8[&5VIP&8] &7{displayname} &9> &7{message}"));
        groups.put("Moderator", new GroupFormat("&b[&6Moderator&b] &a[&b{world}&a] &b{player} &9> &e{message}"));
        return groups;
    }

    // Getters
    public boolean isEnable() {
        return enable;
    }

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public Map<String, GroupFormat> getGroups() {
        return groups;
    }

    /**
     * Get the format for a specific group
     */
    public String getFormatForGroup(String groupName) {
        GroupFormat groupFormat = groups.get(groupName);
        if (groupFormat != null) {
            return groupFormat.getFormat();
        }
        return defaultFormat;
    }

    /**
     * Check if a group has a custom format defined
     */
    public boolean hasGroupFormat(String groupName) {
        return groups.containsKey(groupName);
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_UPPER_CASE)
    public static class GroupFormat extends OkaeriConfig {

        private String format;

        public GroupFormat() {
            this.format = "&7{player} &9> &7{message}";
        }

        public GroupFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }
}
