package cn.dev33.satoken.dao.tcg;

import com.ctg.itrdc.cache.vjedis.jedis.JedisPoolConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.HostAndPort;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CtgConfiguration {

    @Value("${ctg.cache.type:#{null}}")
    private String type;

    @Value("${ctg.cache.url:#{null}}")
    private String url;

    @Value("${ctg.cache.ip:#{null}}")
    private String ip;

    @Value("${ctg.cache.port:#{null}}")
    private String port;

    @Value("${ctg.cache.pool.config.maxTotal:#{null}}")
    private String maxTotal;

    @Value("${ctg.cache.pool.config.maxIdle:#{null}}")
    private String maxIdle;

    @Value("${ctg.cache.pool.config.minIdle:#{null}}")
    private String minIdle;

    @Value("${ctg.cache.instanceName:#{null}}")
    private String instanceName;

    @Value("${ctg.cache.username:#{null}}")
    private String username;

    @Value("${ctg.cache.password:#{null}}")
    private String password;

    @Value("${ctg.cache.pool.config.database:#{null}}")
    private String database;

    @Value("${ctg.cache.pool.config.period:#{null}}")
    private String period;

    @Value("${ctg.cache.pool.config.monitorTimeout:#{null}}")
    private String monitorTimeout;

    CtgDataResourceEnv ctgDataResource;

    CtgClient ctgClient;

    public CtgConfiguration() {
    }

    @Bean(name = "ctgDataResource")
    public CtgDataResourceEnv loadCtgDataResource() {

        if (ctgDataResource != null) {
            return ctgDataResource;
        }

        try {
            List<HostAndPort> hostAndPorts = new ArrayList<>();
            if ("hosts".equals(type)) {
                if (url == null) {
                    System.out.println("未找到CTG-CACHE连接地址!");
                }
                String[] hosts = url.split(",");
                for (String host : hosts) {
                    String[] strs = host.split(":");
                    if (strs.length == 1) {
                        HostAndPort hostAndPort = new HostAndPort(strs[0], 80);
                        hostAndPorts.add(hostAndPort);
                    } else if (strs.length == 2) {
                        try {
                            int port = Integer.parseInt(strs[1]);
                            HostAndPort hostAndPort = new HostAndPort(strs[0], port);
                            hostAndPorts.add(hostAndPort);
                        } catch (NumberFormatException e) {
                            System.out.println(strs[1] + "非合法端口");

                        }
                    } else {
                        System.out.println("CTG-CACHE连接地址有误!");
                    }
                }
            } else {
                if (ip == null) {
                    System.out.println("未找到CTG-CACHE连接IP!");
                }
                if (port == null) {
                    System.out.println("未找到CTG-CACHE连接端口!");
                }
                String[] ips = ip.split(",");
                for (String ipAddr : ips) {
                    try {
                        int ipPort = Integer.parseInt(port);
                        HostAndPort hostAndPort = new HostAndPort(ipAddr, ipPort);
                        hostAndPorts.add(hostAndPort);
                    } catch (NumberFormatException e) {
                        System.out.println(port + "非合法端口");
                    }
                }
            }

            GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
            if (StringUtils.isNotEmpty(maxTotal)) {
                try {
                    poolConfig.setMaxTotal(Integer.parseInt(maxTotal));
                } catch (NumberFormatException e) {
                }
            }

            if (StringUtils.isNotEmpty(maxIdle)) {
                try {
                    poolConfig.setMaxIdle(Integer.parseInt(maxIdle));
                } catch (NumberFormatException e) {
                }
            }

            if (StringUtils.isNotEmpty(minIdle)) {
                try {
                    poolConfig.setMinIdle(Integer.parseInt(minIdle));
                } catch (NumberFormatException e) {
                }
            }

            poolConfig.setMaxWaitMillis(3000);
            if (StringUtils.isEmpty(instanceName)) {
                System.out.println("未找到CTG-CACHE实例名!");
            }
            if (StringUtils.isEmpty(password)) {
                System.out.println("未找到CTG-CACHE密码!");
            }
            ctgDataResource = new CtgDataResourceEnv(hostAndPorts, instanceName, password, poolConfig);

            if (StringUtils.isEmpty(username)) {
                System.out.println("未找到CTG-CACHE用户名!");
            }
            ctgDataResource.setUsername(username);

            if (StringUtils.isNotEmpty(database)) {
                try {
                    ctgDataResource.setDataBase(database);
                } catch (NumberFormatException e) {
                }
            }
            if (StringUtils.isNotEmpty(period)) {
                try {
                    ctgDataResource.setPeriod(Integer.parseInt(period));
                } catch (NumberFormatException e) {
                }
            }
            if (StringUtils.isNotEmpty(monitorTimeout)) {
                try {
                    ctgDataResource.setMonitorTimeout(Integer.parseInt(monitorTimeout));
                } catch (NumberFormatException e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ctgDataResource;
    }

    @Bean(name = "ctgClient")
    @DependsOn("ctgDataResource")
    public CtgClient loadCtgClient() {
        if (ctgClient != null) {
            return ctgClient;
        }
        if (ctgDataResource == null) {
            return null;
        }
        CtgClient ctgClient = new CtgClient(ctgDataResource);
        return ctgClient;
    }

}
