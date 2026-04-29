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
    public void clearPlayerData(UUID uuid) {
        dataConfig.set("players." + uuid.toString() + ".active_id", null);
        dataConfig.set("players." + uuid.toString() + ".is_custom", null);
        save();
    }

    @Override
    public void saveCustomGradient(UUID uuid, com.gradientcolor.namegradient.model.Gradient gradient) {
        String path = "custom_gradients." + uuid.toString() + "." + gradient.getId();
        dataConfig.set(path + ".name", gradient.getName());
        dataConfig.set(path + ".start", gradient.getStartColour());
        dataConfig.set(path + ".end", gradient.getEndColour());
        save();
    }

    @Override
    public Map<Integer, com.gradientcolor.namegradient.model.Gradient> loadCustomGradients(UUID uuid) {
        Map<Integer, com.gradientcolor.namegradient.model.Gradient> customGradients = new HashMap<>();
        String path = "custom_gradients." + uuid.toString();
        if (dataConfig.getConfigurationSection(path) != null) {
            for (String idString : dataConfig.getConfigurationSection(path).getKeys(false)) {
                try {
                    int id = Integer.parseInt(idString);
                    String name = dataConfig.getString(path + "." + idString + ".name");
                    String start = dataConfig.getString(path + "." + idString + ".start");
                    String end = dataConfig.getString(path + "." + idString + ".end");
                    customGradients.put(id, new com.gradientcolor.namegradient.model.Gradient(id, name, start, end, null, null, 0));
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid custom gradient ID for " + uuid + ": " + idString);
                }
            }
        }
        return customGradients;
    }

    @Override
    public void deleteCustomGradient(UUID uuid, int gradientId) {
        dataConfig.set("custom_gradients." + uuid.toString() + "." + gradientId, null);
        save();
    }

    @Override
    public void setActiveGradientIsCustom(UUID uuid, boolean isCustom) {
        dataConfig.set("players." + uuid.toString() + ".is_custom", isCustom);
        save();
    }

    @Override
    public boolean isActiveGradientCustom(UUID uuid) {
        return dataConfig.getBoolean("players." + uuid.toString() + ".is_custom", false);
    }

    @Override
    public Integer loadPlayerData(UUID uuid) {
        if (dataConfig.contains("players." + uuid.toString() + ".active_id")) {
            return dataConfig.getInt("players." + uuid.toString() + ".active_id");
        }
        // Fallback for old data format
        if (dataConfig.contains("players." + uuid.toString()) && !dataConfig.isConfigurationSection("players." + uuid.toString())) {
            return dataConfig.getInt("players." + uuid.toString());
        }
        return null;
    }

    @Override
    public void savePlayerData(UUID uuid, int gradientId) {
        dataConfig.set("players." + uuid.toString() + ".active_id", gradientId);
        save();
    }

    @Override
    public Map<UUID, Integer> loadAllPlayerData() {
        Map<UUID, Integer> playerGradients = new HashMap<>();
        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    Integer gradientId = loadPlayerData(uuid);
                    if (gradientId != null) {
                        playerGradients.put(uuid, gradientId);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
                }
            }
        }
        return playerGradients;
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
