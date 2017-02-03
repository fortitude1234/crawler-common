package com.dianping.ssp.crawler.common.config;


import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;

/**
 * 针对domainTag生成一个配置项
 * 
 * 针对一个crawler配置的parser
 */
public interface ICrawlerConfigParser {
    /**
     * 返回一个crawlerConfig
     * 
     * @return
     */
    public CrawlerConfig parser(String domainTag);
}
