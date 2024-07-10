package cn.dev33.satoken.dao.tcg;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn("ctgClient")
public class CtgRedisService {

    @Resource(name = "ctgClient")
    CtgClient ctgClient;

    public void setCtgClient(CtgClient ctgClient) {
        this.ctgClient = ctgClient;
    }

    private ProxyJedis getJedis() throws CtgJedisPoolException {
        return ctgClient.getCtgJedisPool().getResource();
    }

    public void getAllKeys() {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            Set<String> keys = jedis.keys("*");
            for (String ss : keys) {
                System.out.println("keys=====" + ss);
            }
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Set<String> keys(String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            Set<String> keys = jedis.keys(key);

            return keys;
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 尝试获取锁，尝试时间单位毫秒
     *
     * @param key
     * @param tryTime
     * @return
     */
    public boolean tryLock(String key, Long tryTime, Long lifeTime) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < tryTime) {

            Boolean success = setIfAbsent(key, 1);
            if (BooleanUtils.isTrue(success)) {
                expire(key, (int) (lifeTime / 1000));
                return true;
            }
            Thread.sleep(100L);
        }
        return false;
    }

    /**
     * 解锁
     *
     * @param key
     */
    public void unLock(String key) {
        deleteObject(key);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     */
    public Long increment(final String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.incr(key);
//            jedis.set(s, JsonUtil.toJson(t), SetParams.setParams().ex(i));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public void set(String s, Object t, int i) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.set(s, (String) t, SetParams.setParams().ex(i));
//            jedis.set(s, JsonUtil.toJson(t), SetParams.setParams().ex(i));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setCacheObject(String s, Object t) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.set(s, String.valueOf(t));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 秒
     *
     * @param s
     * @param t
     * @param timeOut
     */
    public void setCacheObject(String s, Object t, Long timeOut, TimeUnit timeUnit) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.set(s, String.valueOf(t));
            jedis.expire(s, (int) TimeUnit.SECONDS.convert(timeOut, timeUnit));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean setIfAbsent(String s, Object t) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            Long setnx = jedis.setnx(s, String.valueOf(t));
            return 1L == setnx;
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public void sadd(byte[] s, byte[]... t) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.sadd(s, t);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void hmset(String key, Map<String, String> hash) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.hmset(key, hash);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> hmget(String key, String field) {
        ProxyJedis jedis = null;
        List<String> l = new ArrayList<>();
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            l = jedis.hmget(key, field);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
            return l;
        }
    }


    public Boolean deleteObject(String s) {
        ProxyJedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            result = jedis.del(s);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return result != null && result.intValue() == 1;
    }

    public Long deleteObject(Collection<String> keys) {
        ProxyJedis jedis = null;
        Long l = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            l = jedis.del(keys.toArray(new String[keys.size()]));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return l;
    }

    public void clearAll() {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            jedis.flushAll();
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();

        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> T getCacheObject(String s) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            String result = jedis.get(s);
            if (StringUtils.isEmpty(result)) {
                return null;
            }
            return (T) result;
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean containKey(String s) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.exists(s);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean containHmKey(String key, String field) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.hexists(key, field);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long expire(String key, int seconds) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.expire(key, seconds);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long expire(String key, Long timeOut, TimeUnit timeUnit) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.expire(key, (int) TimeUnit.SECONDS.convert(timeOut, timeUnit));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long getExpire(String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.ttl(key);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long srem(String key, String... member) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.srem(key, member);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long persist(String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.persist(key);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public String type(String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.type(key);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Object randomKey() {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.randomKey();
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long decrement(String key) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.decr(key);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> multiGet(Collection<String> keys) {
        ProxyJedis jedis = null;
        try {
            jedis = getJedis();  //获取连接，可能抛出异常
            return jedis.mget(keys.toArray(new String[keys.size()]));
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Long hdel(String key, String... hashKeys) {
        ProxyJedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            result = jedis.hdel(key, hashKeys);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public Long put(String key, String hashKey, String value) {
        ProxyJedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            result = jedis.hset(key, hashKey, value);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public Long lpush(String key, String value) {
        ProxyJedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            result = jedis.lpush(key, value);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    public String rpop(String key) {
        ProxyJedis jedis = null;
        Long result = null;
        try {
            jedis = getJedis(); //获取连接，可能抛出异常
            return jedis.rpop(key);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
            throw new RuntimeException("缓存系统异常，请联系管理员");
        } finally {
            //保证连接空出
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
