package com.gradientcolor.namegradient.config;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.model.Gradient;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GradientManager {

    private final NameGradient plugin;
    private FileConfiguration gradientsConfig;
    private File gradientsFile;
    private final Map<Integer, Gradient> gradients = new HashMap<>();

    public GradientManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void loadGradients() {
        gradientsFile = new File(plugin.getDataFolder(), "gradients.yml");
        if (!gradientsFile.exists()) {
            plugin.saveResource("gradients.yml", false);
        }
        gradientsConfig = YamlConfiguration.loadConfiguration(gradientsFile);

        gradients.clear();

        ConfigurationSection gradientsSection = gradientsConfig.getConfigurationSection("gradients");
        if (gradientsSection == null) {
            plugin.getLogger().warning("No gradients section found in gradients.yml!");
            return;
        }

        for (String key : gradientsSection.getKeys(false)) {
            try {
                int id = Integer.parseInt(key);
                ConfigurationSection gradientSection = gradientsSection.getConfigurationSection(key);
                
                if (gradientSection == null) continue;

                String name = gradientSection.getString("name", "Unknown Gradient");
                String startColour = gradientSection.getString("startColour", "#FFFFFF");
                String endColour = gradientSection.getString("endColour", "#000000");
                String alternatePermission = gradientSection.getString("alternatePermission", null);
                String overridePermission = gradientSection.getString("overridePermission", null);
                int overrideWeight = gradientSection.getInt("overrideWeight", 0);

                Gradient gradient = new Gradient(id, name, startColour, endColour, 
                        alternatePermission, overridePermission, overrideWeight);
                gradients.put(id, gradient);

            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid gradient ID: " + key + " (must be an integer)");
            }
        }

        plugin.getLogger().info("Loaded " + gradients.size() + " gradients.");
    }

    public void saveGradients() {
        try {
            gradientsConfig.save(gradientsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save gradients.yml!");
            e.printStackTrace();
        }
    }

    public Gradient getGradient(int id) {
        return gradients.get(id);
    }

    public Map<Integer, Gradient> getAllGradients() {
        return new HashMap<>(gradients);
    }

    public boolean gradientExists(int id) {
        return gradients.containsKey(id);
    }
}
