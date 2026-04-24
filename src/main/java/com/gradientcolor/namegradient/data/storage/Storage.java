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
}
