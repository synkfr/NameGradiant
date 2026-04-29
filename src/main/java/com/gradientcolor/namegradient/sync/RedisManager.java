package com.gradientcolor.namegradient.sync;

import com.gradientcolor.namegradient.NameGradient;
import com.gradientcolor.namegradient.config.okaeri.PluginConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisManager {

    private final NameGradient plugin;
    private JedisPool jedisPool;
    private String channel;
    private JedisPubSub pubSub;

    public RedisManager(NameGradient plugin) {
        this.plugin = plugin;
    }

    public void init() {
        PluginConfig config = plugin.getPluginConfig();
        if (!config.isRedisEnable()) return;

        this.channel = config.getRedisChannel();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        
        String user = config.getRedisUsername();
        String password = config.getRedisPassword();

        if (password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, config.getRedisHost(), config.getRedisPort());
        } else if (user.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, config.getRedisHost(), config.getRedisPort(), 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, config.getRedisHost(), config.getRedisPort(), 2000, user, password);
        }

        startListening();
    }

    private void startListening() {
        pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                // Message format: UUID:gradientId:isCustom (or UUID:clear)
                try {
                    String[] parts = message.split(":");
                    UUID uuid = UUID.fromString(parts[0]);
                    String action = parts[1];

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (action.equals("clear")) {
                            plugin.getPlayerDataManager().handleRemoteUpdate(uuid, null, false);
                        } else {
                            int gradientId = Integer.parseInt(action);
                            boolean isCustom = parts.length > 2 && Boolean.parseBoolean(parts[2]);
                            plugin.getPlayerDataManager().handleRemoteUpdate(uuid, gradientId, isCustom);
                        }
                    });
                } catch (Exception e) {
                    plugin.getLogger().warning("Error parsing Redis message: " + message);
                }
            }
        };

        new Thread(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(pubSub, channel);
            } catch (Exception e) {
                plugin.getLogger().severe("Redis subscription error!");
                e.printStackTrace();
            }
        }, "NameGradient-Redis").start();
    }

    public void publishUpdate(UUID uuid, Integer gradientId, boolean isCustom) {
        if (jedisPool == null) return;

        String message = uuid.toString() + ":" + (gradientId == null ? "clear" : gradientId + ":" + isCustom);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(channel, message);
            } catch (Exception e) {
                plugin.getLogger().severe("Error publishing to Redis!");
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        if (pubSub != null) {
            pubSub.unsubscribe();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
