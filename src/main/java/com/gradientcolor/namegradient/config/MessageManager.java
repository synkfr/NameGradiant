package com.gradientcolor.namegradient.config;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.util.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final NameGradient plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final Map<String, String> messages = new HashMap<>();

    public MessageManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        messages.clear();

        // Load all messages from lang section
        if (messagesConfig.getConfigurationSection("lang") != null) {
            for (String key : messagesConfig.getConfigurationSection("lang").getKeys(false)) {
                messages.put(key, messagesConfig.getString("lang." + key, ""));
            }
        }

        plugin.getLogger().info("Loaded " + messages.size() + " messages.");
    }

    public void saveMessages() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml!");
            e.printStackTrace();
        }
    }

    public String getMessage(String key) {
        String message = messages.getOrDefault(key, "&cMessage not found: " + key);
        return ColorUtil.colorize(message);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = messages.getOrDefault(key, "&cMessage not found: " + key);
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        
        return ColorUtil.colorize(message);
    }

    public String getRawMessage(String key) {
        return messages.getOrDefault(key, "&cMessage not found: " + key);
    }
}
