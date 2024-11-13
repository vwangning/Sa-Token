/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.dao.alone;

import cn.dev33.satoken.dao.*;
import cn.dev33.satoken.dao.alone.redis.CtgCacheProperties;
import cn.dev33.satoken.dao.alone.redis.CtgDataResourceEnv;
import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolConfig;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import redis.clients.jedis.HostAndPort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 为 SaToken 单独设置 Redis 连接信息，使权限缓存与业务缓存分离
 *
 * <p>
 *     使用方式：在引入 sa-token redis 集成相关包的前提下，继续引入当前依赖 <br> <br>
 *     注意事项：目前本依赖仅对以下插件有 Redis 分离效果： <br>
 *     sa-token-redis  <br>
 *     sa-token-redis-jackson  <br>
 *     sa-token-redis-fastjson  <br>
 *     sa-token-redis-fastjson2 <br>
 * </p>
 *
 *
 * @author click33
 * @since 1.21.0
 */
@Configuration
public class SaAloneRedisInject implements EnvironmentAware{

	/**
	 * 配置信息的前缀 
	 */
	public static final String ALONE_PREFIX = "sa-token.alone-redis";
	
	/**
	 * Sa-Token 持久层接口 
	 */

	@Autowired(required = false)
	public SaTokenDao  saTokenDao;


	private CtgJedisPool pool;

	private 	CtgJedisPoolConfig config;
	/**
	 * 开始注入 
	 */
	@Override
	public void setEnvironment(Environment environment) {
		try {
			// 如果 saTokenDao 为空或者为默认实现，则不进行任何操作
			if(saTokenDao == null || saTokenDao instanceof SaTokenDaoDefaultImpl) {
				return;
			}
			
			// ------------------- 开始注入 
			
			// 获取cfg对象，解析开发者配置的 sa-token.alone-redis 相关信息
//

			// 1. Redis配置
			RedisConfiguration redisAloneConfig;
			String pattern = environment.getProperty(ALONE_PREFIX + ".pattern", "single");

			if (pattern.equals("single")) {
				// 单体模式
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
				redisConfig.setHostName(cfg.getHost());
				redisConfig.setPort(cfg.getPort());
				redisConfig.setDatabase(cfg.getDatabase());
				redisConfig.setPassword(RedisPassword.of(cfg.getPassword()));
				redisConfig.setDatabase(cfg.getDatabase());
				// 低版本没有 username 属性，捕获异常给个提示即可，无需退出程序
				try {
					redisConfig.setUsername(cfg.getUsername());
				} catch (NoSuchMethodError e){
					System.err.println(e.getMessage());
				}
				redisAloneConfig = redisConfig;

			} else if (pattern.equals("cluster")){
				// 普通集群模式
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration();
				// 低版本没有 username 属性，捕获异常给个提示即可，无需退出程序
				try {
					redisClusterConfig.setUsername(cfg.getUsername());
				} catch (NoSuchMethodError e){
					System.err.println(e.getMessage());
				}
				redisClusterConfig.setPassword(RedisPassword.of(cfg.getPassword()));

				RedisProperties.Cluster cluster = cfg.getCluster();
				List<RedisNode> serverList = cluster.getNodes().stream().map(node -> {
					String[] ipAndPort = node.split(":");
					return new RedisNode(ipAndPort[0].trim(), Integer.parseInt(ipAndPort[1]));
				}).collect(Collectors.toList());
				redisClusterConfig.setClusterNodes(serverList);
				redisClusterConfig.setMaxRedirects(cluster.getMaxRedirects());

				redisAloneConfig = redisClusterConfig;
			} else if (pattern.equals("sentinel")) {
				// 哨兵集群模式
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
				redisSentinelConfiguration.setDatabase(cfg.getDatabase());
				// 低版本没有 username 属性，捕获异常给个提示即可，无需退出程序
				try {
					redisSentinelConfiguration.setUsername(cfg.getUsername());
				} catch (NoSuchMethodError e){
					System.err.println(e.getMessage());
				}
				redisSentinelConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));

				RedisProperties.Sentinel sentinel = cfg.getSentinel();
				redisSentinelConfiguration.setMaster(sentinel.getMaster());
				redisSentinelConfiguration.setSentinelPassword(sentinel.getPassword());
				List<RedisNode> serverList = sentinel.getNodes().stream().map(node -> {
					String[] ipAndPort = node.split(":");
					return new RedisNode(ipAndPort[0].trim(), Integer.parseInt(ipAndPort[1]));
				}).collect(Collectors.toList());
				redisSentinelConfiguration.setSentinels(serverList);

				redisAloneConfig = redisSentinelConfiguration;
			} else if (pattern.equals("socket")) {
				// socket 连接单体 Redis
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				RedisSocketConfiguration redisSocketConfiguration = new RedisSocketConfiguration();
				redisSocketConfiguration.setDatabase(cfg.getDatabase());
				// 低版本没有 username 属性，捕获异常给个提示即可，无需退出程序
				try {
					redisSocketConfiguration.setUsername(cfg.getUsername());
				} catch (NoSuchMethodError e){
					System.err.println(e.getMessage());
				}
				redisSocketConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));
				String socket = environment.getProperty(ALONE_PREFIX + ".socket", "");
				redisSocketConfiguration.setSocket(socket);

				redisAloneConfig = redisSocketConfiguration;
			} else if (pattern.equals("aws")) {
				// AWS ElastiCache
				// AWS Redis 远程主机地址: String hoseName = "****.***.****.****.cache.amazonaws.com";
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				String hostName = cfg.getHost();
				int port = cfg.getPort();
				RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(hostName, port);
				redisStaticMasterReplicaConfiguration.setDatabase(cfg.getDatabase());
				// 低版本没有 username 属性，捕获异常给个提示即可，无需退出程序
				try {
					redisStaticMasterReplicaConfiguration.setUsername(cfg.getUsername());
				} catch (NoSuchMethodError e){
					System.err.println(e.getMessage());
				}
				redisStaticMasterReplicaConfiguration.setPassword(RedisPassword.of(cfg.getPassword()));

				redisAloneConfig = redisStaticMasterReplicaConfiguration;
			}else if (pattern.equals("ctg-cache")){
				System.out.println("开始初始化 : ctg-cache");
				// 获取cfg对象，解析开发者配置的 sa-token.alone-redis 相关信息
				CtgCacheProperties ctg_cfg = Binder.get(environment).bind("ctg.cache", CtgCacheProperties.class).get();
				redisAloneConfig=ctg_cfg;
				// 如果是电信的ctg 走jedis连接池

				String url = ctg_cfg.getUrl();
				// 创建ctgDataResourceEnv对象
				List<HostAndPort> hostAndPorts = new ArrayList<>();

				// 按逗号分隔字符串
				String[] hosts = url.split(",");
				for (String host : hosts) {

					// 按冒号分隔主机名和端口
					String[] parts = host.split(":");
					if (parts.length == 2) {
						String hostname = parts[0];
						int port = Integer.parseInt(parts[1]);
						HostAndPort hostAndPort = new HostAndPort(hostname, port);
						hostAndPorts.add(hostAndPort);
					}
				}
				// 初始化 poolConfig
				GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

				poolConfig.setMaxIdle(ctg_cfg.getPool().getConfig().getMaxIdle());
				poolConfig.setMaxTotal(ctg_cfg.getPool().getConfig().getMaxTotal());
				poolConfig.setMinIdle(ctg_cfg.getPool().getConfig().getMinIdle());

				 config = new CtgJedisPoolConfig(hostAndPorts);

				config.setDatabase(ctg_cfg.getPool().getConfig().getDatabase())
						.setPassword(ctg_cfg.getUsername() + "#" + ctg_cfg.getPassword())
						.setPoolConfig(poolConfig)
						.setPeriod(ctg_cfg.getPool().getConfig().getPeriod())
						.setMonitorTimeout(ctg_cfg.getPool().getConfig().getMonitorTimeout())
						.setMonitorLog(false);
			} else {
				return;
			}

			if (pattern.equals("ctg-cache")){
				try {
					pool = new CtgJedisPool(config);
					// 初始化链接池
					SaTokenDaoRedisCTG dao = (SaTokenDaoRedisCTG) saTokenDao;
					dao.isInit = false;
					dao.init(pool);
				} catch (CtgJedisPoolException e) {
					e.printStackTrace();
				}
				return;
			}
				// 2. 连接池配置
				GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
				// pool配置
				RedisProperties cfg = Binder.get(environment).bind(ALONE_PREFIX, RedisProperties.class).get();
				Lettuce lettuce = cfg.getLettuce();
				if (lettuce.getPool() != null) {
					RedisProperties.Pool pool = cfg.getLettuce().getPool();
					// 连接池最大连接数
					poolConfig.setMaxTotal(pool.getMaxActive());
					// 连接池中的最大空闲连接
					poolConfig.setMaxIdle(pool.getMaxIdle());
					// 连接池中的最小空闲连接
					poolConfig.setMinIdle(pool.getMinIdle());
					// 连接池最大阻塞等待时间（使用负值表示没有限制）
					poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
				}
				LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
				// timeout
				if (cfg.getTimeout() != null) {
					builder.commandTimeout(cfg.getTimeout());
				}
				// shutdownTimeout
				if (lettuce.getShutdownTimeout() != null) {
					builder.shutdownTimeout(lettuce.getShutdownTimeout());
				}
				// 创建Factory对象
				LettuceClientConfiguration clientConfig = builder.poolConfig(poolConfig).build();
				LettuceConnectionFactory factory = new LettuceConnectionFactory(redisAloneConfig, clientConfig);
				factory.afterPropertiesSet();
				// 3. 开始初始化 SaTokenDao ，此处需要依次判断开发者引入的是哪个 redis 库

				// 如果开发者引入的是：sa-token-redis
				try {
					Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedis");
					SaTokenDaoRedis dao = (SaTokenDaoRedis) saTokenDao;
					dao.isInit = false;
					dao.init(factory);
					return;
				} catch (ClassNotFoundException ignored) {
				}
				// 如果开发者引入的是：sa-token-redis-jackson
				try {
					Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedisJackson");
					SaTokenDaoRedisJackson dao = (SaTokenDaoRedisJackson) saTokenDao;
					dao.isInit = false;
					dao.init(factory);

					return;
				} catch (ClassNotFoundException ignored) {
				}
				// 如果开发者引入的是：sa-token-redis-fastjson
				try {
					Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedisFastjson");
					SaTokenDaoRedisFastjson dao = (SaTokenDaoRedisFastjson) saTokenDao;
					dao.isInit = false;
					dao.init(factory);
					return;
				} catch (ClassNotFoundException ignored) {
				}
				// 如果开发者引入的是：sa-token-redis-fastjson2
				try {
					Class.forName("cn.dev33.satoken.dao.SaTokenDaoRedisFastjson2");
					SaTokenDaoRedisFastjson2 dao = (SaTokenDaoRedisFastjson2) saTokenDao;
					dao.isInit = false;
					dao.init(factory);
					return;
				} catch (ClassNotFoundException ignored) {
				}

			// 至此，说明开发者一个 redis 插件也没引入，或者引入的 redis 插件不在 sa-token-alone-redis 的支持范围内

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 骗过编辑器，增加配置文件代码提示 
	 * @return 配置对象
	 */
	@ConfigurationProperties(prefix = ALONE_PREFIX)
	public RedisProperties getSaAloneRedisConfig() {
		return new RedisProperties();
	}
	
}
