package com.dianping.ssp.crawler.common.trigger;


import com.dianping.ssp.crawler.common.config.dto.CrawlerTrigger;

/**
 * 调度触发器
 */
public interface ICrawlerSpiderTrigger {

    /**
     * 一般trigger为null表示关闭
     * 
     * @param domainTag
     * @param crawlerTrigger
     */
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger);
}
