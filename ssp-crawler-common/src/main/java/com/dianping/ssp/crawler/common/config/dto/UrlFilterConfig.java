package com.dianping.ssp.crawler.common.config.dto;

import com.google.common.collect.Lists;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * url处理器,必须序列化，存redis
 */
public class UrlFilterConfig implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1179466109783320140L;
    /**
     * 请求的方法，默认为GET请求，可以设置为POST请求
     */
    public String method = HttpConstant.Method.GET;

    /**
     * 某个url的过滤器
     */
    public String baseUrlPattern = ".*";

    public List<String> targetUrlPatterns = Lists.newArrayList();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBaseUrlPattern() {
        return baseUrlPattern;
    }

    public void setBaseUrlPattern(String baseUrlPattern) {
        this.baseUrlPattern = baseUrlPattern;
    }

    public List<String> getTargetUrlPatterns() {
        return targetUrlPatterns;
    }

    public void setTargetUrlPatterns(List<String> targetUrlPatterns) {
        this.targetUrlPatterns = targetUrlPatterns;
    }
}
