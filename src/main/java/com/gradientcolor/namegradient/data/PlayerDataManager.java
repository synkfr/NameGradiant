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
    private final Map<UUID, Map<Integer, com.gradientcolor.namegradient.model.Gradient>> playerCustomGradients = new HashMap<>();
    private final Map<UUID, Boolean> activeGradientsIsCustom = new HashMap<>();

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
        
        // Load custom gradients for online players or during bulk load
        // Note: For large datasets, we might want to load custom gradients lazily on join
        for (UUID uuid : playerGradients.keySet()) {
            playerCustomGradients.put(uuid, storage.loadCustomGradients(uuid));
            activeGradientsIsCustom.put(uuid, storage.isActiveGradientCustom(uuid));
        }

        plugin.getLogger().info("Loaded " + playerGradients.size() + " player gradients using " + type + " storage.");
    }

    public void savePlayerData() {
        // With database storage, we save immediately on change.
        // This method is mainly for YAML or bulk saving if needed.
        if (storage instanceof YamlStorage) {
            for (Map.Entry<UUID, Integer> entry : playerGradients.entrySet()) {
                storage.savePlayerData(entry.getKey(), entry.getValue());
                storage.setActiveGradientIsCustom(entry.getKey(), isActiveGradientCustom(entry.getKey()));
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

    public void setPlayerGradient(UUID uuid, int gradientId, boolean isCustom) {
        playerGradients.put(uuid, gradientId);
        activeGradientsIsCustom.put(uuid, isCustom);
        storage.savePlayerData(uuid, gradientId);
        storage.setActiveGradientIsCustom(uuid, isCustom);
        
        if (redisManager != null) {
            redisManager.publishUpdate(uuid, gradientId, isCustom);
        }
    }

    public void clearPlayerGradient(UUID uuid) {
        playerGradients.remove(uuid);
        activeGradientsIsCustom.remove(uuid);
        storage.clearPlayerData(uuid);
        
        if (redisManager != null) {
            redisManager.publishUpdate(uuid, null, false);
        }
    }

    public Integer getPlayerGradient(UUID uuid) {
        return playerGradients.get(uuid);
    }

    public boolean isActiveGradientCustom(UUID uuid) {
        return activeGradientsIsCustom.getOrDefault(uuid, false);
    }

    public void addCustomGradient(UUID uuid, com.gradientcolor.namegradient.model.Gradient gradient) {
        Map<Integer, com.gradientcolor.namegradient.model.Gradient> custom = playerCustomGradients.computeIfAbsent(uuid, k -> new HashMap<>());
        custom.put(gradient.getId(), gradient);
        storage.saveCustomGradient(uuid, gradient);
    }

    public Map<Integer, com.gradientcolor.namegradient.model.Gradient> getCustomGradients(UUID uuid) {
        // Load from storage if not in cache (useful if player joined after loadPlayerData)
        return playerCustomGradients.computeIfAbsent(uuid, k -> storage.loadCustomGradients(uuid));
    }

    public com.gradientcolor.namegradient.model.Gradient getCustomGradient(UUID uuid, int id) {
        return getCustomGradients(uuid).get(id);
    }

    public boolean hasGradient(UUID uuid) {
        return playerGradients.containsKey(uuid);
    }

    /**
     * Handle update from another server via Redis
     */
    public void handleRemoteUpdate(UUID uuid, Integer gradientId, boolean isCustom) {
        if (gradientId == null) {
            playerGradients.remove(uuid);
            activeGradientsIsCustom.remove(uuid);
        } else {
            playerGradients.put(uuid, gradientId);
            activeGradientsIsCustom.put(uuid, isCustom);
        }
    }
}
