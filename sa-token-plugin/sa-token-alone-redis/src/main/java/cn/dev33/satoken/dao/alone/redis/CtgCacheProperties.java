package cn.dev33.satoken.dao.alone.redis;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ctg.cache")
public class CtgCacheProperties implements RedisConfiguration {
    private String type;
    private String url;
    private String ip;
    private String port;
    private String instanceName;
    private String username;
    private String password;

    private PoolConfigProperties pool = new PoolConfigProperties();

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PoolConfigProperties getPool() {
        return pool;
    }

    public void setPool(PoolConfigProperties pool) {
        this.pool = pool;
    }

    // Inner class for pool configuration


    public static class PoolConfigProperties {
        private PoolConfig config = new PoolConfig();

        public PoolConfig getConfig() {
            return config;
        }
        public void setConfig(PoolConfig config) {
            this.config = config;
        }

    }

    public static class PoolConfig {
        private int maxTotal;
        private int minIdle;
        private int maxIdle;
        private String database;
        private int period;
        private int monitorTimeout;

        // Getters and setters

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public int getPeriod() {
            return period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }

        public int getMonitorTimeout() {
            return monitorTimeout;
        }

        public void setMonitorTimeout(int monitorTimeout) {
            this.monitorTimeout = monitorTimeout;
        }
    }
}