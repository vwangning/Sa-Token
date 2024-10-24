package com.pj.test;

import cn.dev33.satoken.dao.tcg.CtgRedisService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 测试专用Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/test/")
public class TestController {

//	@Autowired
//	StringRedisTemplate stringRedisTemplate;


	@Autowired
	CtgRedisService ctgRedisService;
	// 测试Sa-Token缓存， 浏览器访问： http://localhost:8081/test/login
	@RequestMapping("login")
	public AjaxJson login(@RequestParam(defaultValue="10001") String id) {
		System.out.println("--------------- 测试Sa-Token缓存");
		StpUtil.login(id);	
		return AjaxJson.getSuccess();
	}
	
	// 测试业务缓存   浏览器访问： http://localhost:8081/test/test
	@RequestMapping("test")
	public AjaxJson test() {
//		User user = new User();
//		user.setName("wangn");
//		ctgRedisService.setCacheObject("user",user);
//		User cacheObject = ctgRedisService.getCacheObject("user");
//
//		ctgRedisService.deleteObject("token:B:session:1543837863788879871");

		Object cacheObject = ctgRedisService.getCacheObject("Cache:sys-org:all-rule");
		return AjaxJson.getSuccess().setData(cacheObject);
	}


}
