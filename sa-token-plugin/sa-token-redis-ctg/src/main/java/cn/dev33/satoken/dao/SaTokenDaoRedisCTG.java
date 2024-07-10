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
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.dev33.satoken.dao.tcg.CtgRedisService;
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
		System.out.println("get:"+key);
		return ctgRedisService.getCacheObject(key);
	}

	@Override
	public void set(String key, String value, long timeout) {
		System.out.println("set:"+key);
		ctgRedisService.setCacheObject(key, value,timeout, TimeUnit.SECONDS);
		String cacheObject = ctgRedisService.getCacheObject(key);
		System.out.println("get:"+cacheObject);
	}

	@Override
	public void update(String key, String value) {

	}

	@Override
	public void delete(String key) {

	}

	@Override
	public long getTimeout(String key) {
		return 0;
	}

	@Override
	public void updateTimeout(String key, long timeout) {

	}

	@Override
	public Object getObject(String key) {
		return null;
	}

	@Override
	public void setObject(String key, Object object, long timeout) {

	}

	@Override
	public void updateObject(String key, Object object) {

	}

	@Override
	public void deleteObject(String key) {

	}

	@Override
	public long getObjectTimeout(String key) {
		return 0;
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {

	}

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		return null;
	}
}
