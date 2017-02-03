package com.dianping.ssp.crawler.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.lang.reflect.Type;


public class JsonUtil {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		// 过滤对象的null属性.
		MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public static <T> T fromJson(String content, Class<T> clazz){
		Gson gson = new Gson();
		return gson.fromJson(content,clazz);
	}

	public static String toJson(Object obj){
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	public static <T> T fromJson(String content, Type type){
		Gson gson = new Gson();
		return gson.fromJson(content, type);
	}

	/**
	 * 过滤空值
	 * @return
	 */
	public static String toJsonFilterNull(Object obj) throws JsonProcessingException {
		return  MAPPER.writeValueAsString(obj);
	}

}
