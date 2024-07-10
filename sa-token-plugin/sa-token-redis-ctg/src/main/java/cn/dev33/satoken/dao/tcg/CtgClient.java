package cn.dev33.satoken.dao.tcg;

import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolConfig;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;


public class CtgClient {

    private CtgDataResourceEnv ctgDataResource;

    private CtgJedisPool pool;

    public CtgClient(CtgDataResourceEnv ctgDataResource) {
        this.ctgDataResource = ctgDataResource;
        CtgJedisPoolConfig config = new CtgJedisPoolConfig(ctgDataResource.getHostAndPorts());
        config.setDatabase(ctgDataResource.getDataBase())//255
                .setPassword(ctgDataResource.getUsername() + "#" + ctgDataResource.getPassword()) // 实例名#密码
                .setPoolConfig(ctgDataResource.getPoolConfig())
                .setPeriod(ctgDataResource.getPeriod())
                .setMonitorTimeout(ctgDataResource.getMonitorTimeout())
                .setMonitorLog(false);
        try {
            pool = new CtgJedisPool(config);
        } catch (CtgJedisPoolException e) {
            e.printStackTrace();
        }
    }

    public CtgJedisPool getCtgJedisPool() throws CtgJedisPoolException {
        return pool;
    }

    public CtgDataResourceEnv getCtgDataResource() {
        return ctgDataResource;
    }
}
