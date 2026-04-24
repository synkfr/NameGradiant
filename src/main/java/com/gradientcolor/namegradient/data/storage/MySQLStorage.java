package com.gradientcolor.namegradient.data.storage;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.config.okaeri.PluginConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLStorage implements Storage {

    private final NameGradient plugin;
    private HikariDataSource dataSource;

    public MySQLStorage(NameGradient plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        PluginConfig config = plugin.getPluginConfig();
        HikariConfig hikariConfig = new HikariConfig();
        
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=%b",
                config.getMysqlHost(), config.getMysqlPort(), config.getMysqlDatabase(), config.isMysqlSsl());
        
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.getMysqlUsername());
        hikariConfig.setPassword(config.getMysqlPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTimeout(5000);

        dataSource = new HikariDataSource(hikariConfig);

        createTable();
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS namegradient_player_data (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "gradient_id INT NOT NULL" +
                    ")");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create MySQL table!");
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public Map<UUID, Integer> loadAllPlayerData() {
        Map<UUID, Integer> playerGradients = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT uuid, gradient_id FROM namegradient_player_data")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                int gradientId = rs.getInt("gradient_id");
                playerGradients.put(uuid, gradientId);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not load all player data from MySQL!");
            e.printStackTrace();
        }
        return playerGradients;
    }

    @Override
    public Integer loadPlayerData(UUID uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT gradient_id FROM namegradient_player_data WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("gradient_id");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not load player data for " + uuid + " from MySQL!");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void savePlayerData(UUID uuid, int gradientId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO namegradient_player_data (uuid, gradient_id) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE gradient_id = ?")) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, gradientId);
            ps.setInt(3, gradientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not save player data for " + uuid + " to MySQL!");
            e.printStackTrace();
        }
    }

    @Override
    public void clearPlayerData(UUID uuid) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM namegradient_player_data WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not clear player data for " + uuid + " in MySQL!");
            e.printStackTrace();
        }
    }
}
