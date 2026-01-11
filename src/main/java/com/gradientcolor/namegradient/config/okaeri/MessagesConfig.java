package com.gradientcolor.namegradient.config.okaeri;

import com.gradientcolor.namegradient.util.ColorUtil;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Header("## LANGUAGE SETTINGS")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessagesConfig extends OkaeriConfig {

    @Comment("Plugin messages")
    private Map<String, String> lang = createDefaultMessages();

    private static Map<String, String> createDefaultMessages() {
        Map<String, String> messages = new HashMap<>();
        messages.put("apply", "§7≫ §eA new gradient has been applied. You're now '{GRADIENT}§e'.");
        messages.put("clear", "§7≫ §eYour current gradient has been cleared.");
        messages.put("same_gradient", "§7≫ §cYou already have this gradient active.");
        messages.put("no_permission_gradient", "§7≫ §cYou don't have permission to use this gradient.");
        messages.put("no_permission", "§7≫ §cYou don't have permission to perform this action.");
        messages.put("no_gradient", "§7≫ §cYou don't currently have an active gradient.");
        messages.put("has_override_gradient", "§7≫ §cYou currently have an override gradient active.");
        messages.put("has_coloured_source",
                "§7≫ §cYou currently have a coloured gradient source which cannot be overridden.");
        messages.put("args_incorrect", "§7≫ §cYou entered the incorrect number or type of arguments.");
        messages.put("gradient_nonexistent", "§7≫ §cA gradient with this ID doesn't exist.");
        messages.put("attempt_change_own", "§7≫ §cPlease use /gradient to change your own gradient.");
        messages.put("attempt_clear_own", "§7≫ §cPlease use /gradient to clear your own gradient.");
        messages.put("target_offline", "§7≫ §cThis player is not currently online.");
        messages.put("target_has_override_gradient", "§7≫ §cThis player currently has an override gradient active.");
        messages.put("target_coloured_source",
                "§7≫ §cThis player currently has a coloured gradient source which cannot be overridden.");
        messages.put("change_success", "§7≫ §eYou've updated this player's gradient.");
        messages.put("change_target_success", "§7≫ §eYour gradient has been changed by another player.");
        messages.put("clear_success", "§7≫ §eYou've cleared this player's gradient.");
        messages.put("clear_target_success", "§7≫ §eYour gradient has been cleared by another player.");
        messages.put("target_permission_error", "§7≫ §cThis player doesn't have permission to use this gradient.");
        messages.put("config_reload_success", "§7≫ §eThe configuration was reloaded successfully.");
        messages.put("config_reload_error", "§7≫ §cAn error occurred during reloading, keeping config in memory.");
        return messages;
    }

    /**
     * Get a message by key with color codes processed
     */
    public String getMessage(String key) {
        String message = lang.getOrDefault(key, "§cMessage not found: " + key);
        return ColorUtil.colorize(message);
    }

    /**
     * Get a message by key with placeholders replaced
     */
    public String getMessage(String key, Map<String, String> placeholders) {
        String message = lang.getOrDefault(key, "§cMessage not found: " + key);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return ColorUtil.colorize(message);
    }

    /**
     * Get raw message without color processing
     */
    public String getRawMessage(String key) {
        return lang.getOrDefault(key, "§cMessage not found: " + key);
    }

    public Map<String, String> getLang() {
        return lang;
    }
}
