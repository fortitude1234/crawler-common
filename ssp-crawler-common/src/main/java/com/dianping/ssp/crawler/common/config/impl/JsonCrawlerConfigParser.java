package com.dianping.ssp.crawler.common.config.impl;

import com.alibaba.fastjson.JSON;
import com.dianping.ssp.crawler.common.config.ICrawlerConfigParser;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.util.DomainTagUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 通过json去配置
 */
public class JsonCrawlerConfigParser implements ICrawlerConfigParser {
    @Override
    public CrawlerConfig parser(String domainTag) {
        String jsonStr = DomainTagUtils.getForDomainTag(domainTag);
        if (StringUtils.isNotBlank(jsonStr)) {
            CrawlerConfig config = JSON.parseObject(jsonStr, CrawlerConfig.class);
            // 设置domainTag
            if (StringUtils.isBlank(config.getDomainTag())) {
                config.setDomainTag(domainTag);
            }
            return config;
        }
        return null;
    }
}
