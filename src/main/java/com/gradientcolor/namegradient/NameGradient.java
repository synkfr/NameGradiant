package com.gradientcolor.namegradient;

import com.gradientcolor.namegradient.commands.GradientCommand;
import com.gradientcolor.namegradient.commands.NameGradientCommand;
import com.gradientcolor.namegradient.config.okaeri.ChatFormatConfig;
import com.gradientcolor.namegradient.config.okaeri.GradientsConfig;
import com.gradientcolor.namegradient.config.okaeri.MessagesConfig;
import com.gradientcolor.namegradient.config.okaeri.PluginConfig;
import com.gradientcolor.namegradient.data.PlayerDataManager;
import com.gradientcolor.namegradient.hooks.LuckPermsHook;
import com.gradientcolor.namegradient.listeners.ChatListener;
import com.gradientcolor.namegradient.listeners.MenuListener;
import com.gradientcolor.namegradient.listeners.PlayerListener;
import com.gradientcolor.namegradient.placeholders.NameGradientExpansion;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NameGradient extends JavaPlugin {

    private static NameGradient instance;
    private PluginConfig pluginConfig;
    private GradientsConfig gradientsConfig;
    private MessagesConfig messagesConfig;
    private ChatFormatConfig chatFormatConfig;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Create data folder if needed
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize Okaeri configs
        loadConfigs();

        // Initialize player data manager
        this.playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadPlayerData();

        // Initialize LuckPerms hook
        LuckPermsHook.init(getLogger());

        // Register commands
        getCommand("gradient").setExecutor(new GradientCommand(this));
        getCommand("namegradient").setExecutor(new NameGradientCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Register chat listener if chat formatting is enabled
        if (chatFormatConfig.isEnable()) {
            getServer().getPluginManager().registerEvents(new ChatListener(this), this);
            getLogger().info("Chat formatting enabled.");
        }

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

    /**
     * Load all Okaeri config files
     */
    private void loadConfigs() {
        try {
            this.pluginConfig = ConfigManager.create(PluginConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File(getDataFolder(), "config.yml"));
                config.withRemoveOrphans(false);
                config.saveDefaults();
                config.load(true);
            });

            this.gradientsConfig = ConfigManager.create(GradientsConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File(getDataFolder(), "gradients.yml"));
                config.withRemoveOrphans(false);
                config.saveDefaults();
                config.load(true);
            });

            this.messagesConfig = ConfigManager.create(MessagesConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File(getDataFolder(), "messages.yml"));
                config.withRemoveOrphans(false);
                config.saveDefaults();
                config.load(true);
            });

            this.chatFormatConfig = ConfigManager.create(ChatFormatConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File(getDataFolder(), "chat-formats.yml"));
                config.withRemoveOrphans(false);
                config.saveDefaults();
                config.load(true);
            });

            getLogger().info("Loaded all configuration files successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to load configuration files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static NameGradient getInstance() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public GradientsConfig getGradientsConfig() {
        return gradientsConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public ChatFormatConfig getChatFormatConfig() {
        return chatFormatConfig;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    /**
     * Reload all configuration files
     */
    public void reload() {
        try {
            pluginConfig.load(true);
            gradientsConfig.load(true);
            messagesConfig.load(true);
            chatFormatConfig.load(true);
            getLogger().info("Configuration files reloaded successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to reload configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
