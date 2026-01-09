package com.gradientcolor.namegradient;

import com.gradientcolor.namegradient.commands.GradientCommand;
import com.gradientcolor.namegradient.commands.NameGradientCommand;
import com.gradientcolor.namegradient.config.ConfigManager;
import com.gradientcolor.namegradient.config.GradientManager;
import com.gradientcolor.namegradient.config.MessageManager;
import com.gradientcolor.namegradient.data.PlayerDataManager;
import com.gradientcolor.namegradient.listeners.MenuListener;
import com.gradientcolor.namegradient.listeners.PlayerListener;
import com.gradientcolor.namegradient.placeholders.NameGradientExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NameGradient extends JavaPlugin {

    private static NameGradient instance;
    private ConfigManager configManager;
    private GradientManager gradientManager;
    private MessageManager messageManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.gradientManager = new GradientManager(this);
        this.messageManager = new MessageManager(this);
        this.playerDataManager = new PlayerDataManager(this);

        // Load configurations
        configManager.loadConfig();
        gradientManager.loadGradients();
        messageManager.loadMessages();
        playerDataManager.loadPlayerData();

        // Register commands
        getCommand("gradient").setExecutor(new GradientCommand(this));
        getCommand("namegradient").setExecutor(new NameGradientCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NameGradientExpansion(this).register();
            getLogger().info("PlaceholderAPI found! Registered placeholder expansion.");
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholder support disabled.");
        }

        getLogger().info("NameGradient has been enabled!");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.savePlayerData();
        }
        getLogger().info("NameGradient has been disabled!");
    }

    public static NameGradient getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GradientManager getGradientManager() {
        return gradientManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public void reload() {
        configManager.loadConfig();
        gradientManager.loadGradients();
        messageManager.loadMessages();
    }
}
