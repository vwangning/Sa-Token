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
package cn.dev33.satoken.dao;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.dao.tcg.CtgRedisService;
import cn.dev33.satoken.util.SaFoxUtil;
import com.ctg.itrdc.cache.pool.CtgJedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * Sa-Token 持久层实现 [ Redis存储、Jackson序列化 ]
 *
 * @author click33
 * @since 1.34.0
 */
@Component
public class SaTokenDaoRedisCTG implements SaTokenDao {

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
	public boolean isInit;

	@Autowired
	CtgRedisService ctgRedisService;


	public void init(CtgJedisPool ctgJedisPool) {
		if(isInit == true) {
			return;
		}
		isInit = true;
	}

	@Override
	public String get(String key) {
		return ctgRedisService.getCacheObject(key);
	}

	@Override
	public void set(String key, String value, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			ctgRedisService.setCacheObject(key, value);
		}else {
			ctgRedisService.setCacheObject(key, value, timeout, TimeUnit.SECONDS);
		}

	}
	/**
	 * 修改指定key-value键值对 (过期时间不变)
	 */
	@Override
	public void update(String key, String value) {
		long expire = getTimeout(key);
		// -2 = 无此键
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.set(key, value, expire);
	}
	/**
	 * 删除Value
	 */
	@Override
	public void delete(String key) {
		ctgRedisService.deleteObject(key);
	}


	/**
	 * 获取Value的剩余存活时间 (单位: 秒)
	 */
	@Override
	public long getTimeout(String key) {
		return ctgRedisService.getExpire(key);
	}

	@Override
	public void updateTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.set(key, this.get(key), timeout);
			}
			return;
		}
		ctgRedisService.expire(key, timeout, TimeUnit.SECONDS);
	}


	/**
	 * 获取Object，如无返空
	 */
	@Override
	public Object getObject(String key) {
		return ctgRedisService.type(key);
	}

	@Override
	public void setObject(String key, Object object, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			ctgRedisService.setCacheObject(key, object);
		}else {
			ctgRedisService.setCacheObject(key, object, timeout, TimeUnit.SECONDS);
		}
	}

	@Override
	public void updateObject(String key, Object object) {
		long expire = getTimeout(key);
		// -2 = 无此键
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.setObject(key, object, expire);
	}

	@Override
	public void deleteObject(String key) {
		ctgRedisService.deleteObject(key);
	}

	@Override
	public long getObjectTimeout(String key) {
		 return ctgRedisService.getCacheObject(key);
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {
		// 判断是否想要设置为永久
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			long expire = getObjectTimeout(key);
			if(expire == SaTokenDao.NEVER_EXPIRE) {
				// 如果其已经被设置为永久，则不作任何处理
			} else {
				// 如果尚未被设置为永久，那么再次set一次
				this.setObject(key, this.getObject(key), timeout);
			}
			return;
		}
		ctgRedisService.expire(key, timeout, TimeUnit.SECONDS);
	}

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		Set<String> keys = ctgRedisService.keys(prefix + "*" + keyword + "*");
		List<String> list = new ArrayList<>(keys);
		return SaFoxUtil.searchList(list, start, size, sortType);
	}
}
