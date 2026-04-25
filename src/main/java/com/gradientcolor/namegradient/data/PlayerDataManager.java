package com.gradientcolor.namegradient.data;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.data.storage.MySQLStorage;
import com.gradientcolor.namegradient.data.storage.Storage;
import com.gradientcolor.namegradient.data.storage.YamlStorage;
import com.gradientcolor.namegradient.sync.RedisManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final NameGradient plugin;
    private Storage storage;
    private RedisManager redisManager;
    private final Map<UUID, Integer> playerGradients = new HashMap<>();

    public PlayerDataManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void loadPlayerData() {
        String type = plugin.getPluginConfig().getStorageType();
        boolean mysqlFailed = false;

        if (type.equalsIgnoreCase("MYSQL")) {
            try {
                storage = new MySQLStorage(plugin);
                storage.init();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to initialize MySQL storage! Falling back to YAML.");
                plugin.getLogger().severe("Error: " + e.getMessage());
                mysqlFailed = true;
            }
        }

        if (mysqlFailed || !type.equalsIgnoreCase("MYSQL")) {
            storage = new YamlStorage(plugin);
            storage.init();
            if (mysqlFailed) {
                type = "YAML (Fallback)";
            }
        }
        
        // Initialize Redis if enabled
        if (plugin.getPluginConfig().isRedisEnable()) {
            try {
                redisManager = new RedisManager(plugin);
                redisManager.init();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to initialize Redis! Cross-server synchronization will be disabled.");
                plugin.getLogger().severe("Error: " + e.getMessage());
                redisManager = null;
            }
        }

        playerGradients.clear();
        playerGradients.putAll(storage.loadAllPlayerData());

        plugin.getLogger().info("Loaded " + playerGradients.size() + " player gradients using " + type + " storage.");
    }

    public void savePlayerData() {
        // With database storage, we save immediately on change.
        // This method is mainly for YAML or bulk saving if needed.
        if (storage instanceof YamlStorage) {
            for (Map.Entry<UUID, Integer> entry : playerGradients.entrySet()) {
                storage.savePlayerData(entry.getKey(), entry.getValue());
            }
        }
    }

    public void shutdown() {
        if (storage != null) {
            storage.shutdown();
        }
        if (redisManager != null) {
            redisManager.shutdown();
        }
    }

    public void setPlayerGradient(UUID uuid, int gradientId) {
        playerGradients.put(uuid, gradientId);
        storage.savePlayerData(uuid, gradientId);
        
        if (redisManager != null) {
            redisManager.publishUpdate(uuid, gradientId);
        }
    }

    public void clearPlayerGradient(UUID uuid) {
        playerGradients.remove(uuid);
        storage.clearPlayerData(uuid);
        
        if (redisManager != null) {
            redisManager.publishUpdate(uuid, null);
        }
    }

    public Integer getPlayerGradient(UUID uuid) {
        return playerGradients.get(uuid);
    }

    public boolean hasGradient(UUID uuid) {
        return playerGradients.containsKey(uuid);
    }

    /**
     * Handle update from another server via Redis
     */
    public void handleRemoteUpdate(UUID uuid, Integer gradientId) {
        if (gradientId == null) {
            playerGradients.remove(uuid);
        } else {
            playerGradients.put(uuid, gradientId);
        }
        // No need to save to storage as the origin server already did that.
    }
}
