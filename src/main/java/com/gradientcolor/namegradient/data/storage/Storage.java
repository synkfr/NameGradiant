package com.gradientcolor.namegradient.data.storage;

import java.util.Map;
import java.util.UUID;

public interface Storage {

    void init();

    void shutdown();

    Map<UUID, Integer> loadAllPlayerData();

    Integer loadPlayerData(UUID uuid);

    void savePlayerData(UUID uuid, int gradientId);

    void clearPlayerData(UUID uuid);

    void saveCustomGradient(UUID uuid, com.gradientcolor.namegradient.model.Gradient gradient);

    Map<Integer, com.gradientcolor.namegradient.model.Gradient> loadCustomGradients(UUID uuid);

    void deleteCustomGradient(UUID uuid, int gradientId);

    void setActiveGradientIsCustom(UUID uuid, boolean isCustom);

    boolean isActiveGradientCustom(UUID uuid);
}
