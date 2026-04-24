package com.gradientcolor.namegradient.data.storage;

import com.gradientcolor.namegradient.NameGradient;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YamlStorage implements Storage {

    private final NameGradient plugin;
    private FileConfiguration dataConfig;
    private File dataFile;

    public YamlStorage(NameGradient plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
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
    }

    @Override
    public void shutdown() {
        // Nothing special for YAML
    }

    @Override
    public Map<UUID, Integer> loadAllPlayerData() {
        Map<UUID, Integer> playerGradients = new HashMap<>();
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
        return playerGradients;
    }

    @Override
    public Integer loadPlayerData(UUID uuid) {
        if (dataConfig.contains("players." + uuid.toString())) {
            return dataConfig.getInt("players." + uuid.toString());
        }
        return null;
    }

    @Override
    public void savePlayerData(UUID uuid, int gradientId) {
        dataConfig.set("players." + uuid.toString(), gradientId);
        save();
    }

    @Override
    public void clearPlayerData(UUID uuid) {
        dataConfig.set("players." + uuid.toString(), null);
        save();
    }

    private void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
    }
}
