package cn.dev33.satoken.json;

import java.util.Map;

import cn.dev33.satoken.exception.ApiDisabledException;

/**
 * JSON 相关操作接口 
 * 
 * @author kong
 *
 */
public class SaJsonTemplateDefaultImpl implements SaJsonTemplate {

	public static final String ERROR_MESSAGE = "未实现具体的 json 转换器";
	
	/**
	 * 将任意对象转换为 json 字符串 
	 */
	@Override
	public String toJsonString(Object obj) {
		throw new ApiDisabledException(ERROR_MESSAGE);
	}
	
	/**
	 * 将 json 字符串解析为 Map 
	 */
	@Override
	public Map<String, Object> parseJsonToMap(String jsonStr) {
		throw new ApiDisabledException(ERROR_MESSAGE);
	};
	
}
