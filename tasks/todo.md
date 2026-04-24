# Tasks

- [x] Add dependencies to `pom.xml` (HikariCP, MySQL, Jedis)
- [x] Update `PluginConfig.java` with storage and Redis settings
- [x] Create `Storage` interface and implementations
    - [x] `Storage.java`
    - [x] `YamlStorage.java`
    - [x] `MySQLStorage.java`
- [x] Create `RedisManager.java` for cross-server sync
- [x] Refactor `PlayerDataManager.java` to use new storage/sync layers
- [x] Update `NameGradient.java` to initialize new components
- [x] Verify implementation (YAML, MySQL, Redis)

## Review
- [ ] Backward compatibility with existing YAML data
- [ ] Connection pooling performance
- [ ] Cross-server update latency
