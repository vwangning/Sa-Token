package cn.dev33.satoken.application;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;

/**
 * Application Model，全局作用域的读取值对象 
 * <p> 在应用全局范围内: 存值、取值
 * 
 * @author kong
 * @since: 2022-8-17
 */
public class SaApplication implements SaSetValueInterface {

	/**
	 * 默认实例 
	 */
	public static SaApplication defaultInstance = new SaApplication();

	// ---- 实现接口存取值方法 

	/** 取值 */
	@Override
	public Object get(String key) {
		return SaManager.getSaTokenDao().getObject(splicingDataKey(key));
	}

	/** 写值 */
	@Override
	public SaApplication set(String key, Object value) {
		return set(key, value, SaTokenDao.NEVER_EXPIRE);
	}

	/** 删值 */
	@Override
	public SaApplication delete(String key) {
		SaManager.getSaTokenDao().deleteObject(splicingDataKey(key));
		return this;
	}

	
	// ---- 其它方法 

	/**
	 * 写值
	 * @param key   名称
	 * @param value 值
	 * @param ttl 有效时间（单位：秒）
	 * @return 对象自身
	 */
	public SaApplication set(String key, Object value, long ttl) {
		SaManager.getSaTokenDao().setObject(splicingDataKey(key), value, ttl);
		return this;
	}

	/**
	 * 返回存入的所有 key 
	 * @return / 
	 */
	public List<String> keys() {
		// 查出来
		String prefix = splicingDataKey("");
		List<String> list = SaManager.getSaTokenDao().searchData(prefix, "", -1, 0, true);
		
		// 裁减掉固定前缀 
		int prefixLength = prefix.length();
		List<String> list2 = new ArrayList<>();
		if(list != null) {
			for (String key : list) {
				list2.add(key.substring(prefixLength));
			}
		}
		
		// 返回 
		return list2;
	}
	
	/**
	 * 清空存入的所有 key 
	 */
	public void clear() {
		List<String> keys = keys();
		for (String key : keys) {
			delete(key);
		}
	}

	/**  
	 * 拼接key：变量存储时使用的key 
	 * @param key 原始 key 
	 * @return 拼接后的 key 值 
	 */
	public String splicingDataKey(String key) {
		return SaManager.getConfig().getTokenName() + ":var:" + key;
	}
	
}
