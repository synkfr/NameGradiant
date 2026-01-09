package com.gradientcolor.namegradient.data;

import com.gradientcolor.namegradient.NameGradient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final NameGradient plugin;
    private FileConfiguration dataConfig;
    private File dataFile;
    private final Map<UUID, Integer> playerGradients = new HashMap<>();

    public PlayerDataManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void loadPlayerData() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        playerGradients.clear();

        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    int gradientId = dataConfig.getInt("players." + uuidString);
                    playerGradients.put(uuid, gradientId);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
                }
            }
        }

        plugin.getLogger().info("Loaded " + playerGradients.size() + " player gradients.");
    }

    public void savePlayerData() {
        dataConfig.set("players", null);
        
        for (Map.Entry<UUID, Integer> entry : playerGradients.entrySet()) {
            dataConfig.set("players." + entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
    }

    public void setPlayerGradient(UUID uuid, int gradientId) {
        playerGradients.put(uuid, gradientId);
        savePlayerData();
    }

    public void clearPlayerGradient(UUID uuid) {
        playerGradients.remove(uuid);
        savePlayerData();
    }

    public Integer getPlayerGradient(UUID uuid) {
        return playerGradients.get(uuid);
    }

    public boolean hasGradient(UUID uuid) {
        return playerGradients.containsKey(uuid);
    }
}
