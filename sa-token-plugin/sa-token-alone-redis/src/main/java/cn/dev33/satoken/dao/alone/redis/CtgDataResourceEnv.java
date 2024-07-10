package cn.dev33.satoken.dao.alone.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;

import java.util.List;

public class CtgDataResourceEnv {

    private List<HostAndPort> hostAndPorts;

    private String instanceName;

    private String username;

    private String password;

    private String dataBase;

    private long period = 3000;

    private int monitorTimeout = 20000;

    GenericObjectPoolConfig poolConfig;

    public CtgDataResourceEnv() {
    }

    public CtgDataResourceEnv(List<HostAndPort> hostAndPorts, String instanceName, String password, GenericObjectPoolConfig poolConfig) {
        this.hostAndPorts = hostAndPorts;
        this.instanceName = instanceName;
        this.password = password;
        this.poolConfig = poolConfig;
    }

    public List<HostAndPort> getHostAndPorts() {
        return hostAndPorts;
    }

    public void setHostAndPorts(List<HostAndPort> hostAndPorts) {
        this.hostAndPorts = hostAndPorts;
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

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public int getMonitorTimeout() {
        return monitorTimeout;
    }

    public void setMonitorTimeout(int monitorTimeout) {
        this.monitorTimeout = monitorTimeout;
    }

    public GenericObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(GenericObjectPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
}
