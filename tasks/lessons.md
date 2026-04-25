# Lessons Learned

## Database Initialization
- **Problem**: Plugin crashes during `onEnable` if MySQL initialization fails (e.g., communications link failure).
- **Solution**: Always wrap storage and external service initialization (MySQL, Redis, etc.) in try-catch blocks. Implement a fallback mechanism (e.g., local YAML storage) to ensure the plugin remains functional even if the primary database is unreachable.
- **Pattern**:
    ```java
    try {
        storage.init();
    } catch (Exception e) {
        logSevere("Failed to init primary storage, falling back...");
        storage = new FallbackStorage();
        storage.init();
    }
    ```
