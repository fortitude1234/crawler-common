package com.dianping.ssp.crawler.common.spider.proxy;

import java.util.List;

/**
 * 构建代理接口
 */
public interface ICrawlerProxyBuilder {
    /**
     * 构建一个代理
     * 
     * @return
     */
    public List<String[]> builder(Integer count);
}
