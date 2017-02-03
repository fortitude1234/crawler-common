package com.dianping.ssp.crawler.common.spider;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.util.DomainTagUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * Created by iClaod on 10/12/16.
 */
public class Starter implements ApplicationListener {

    private static final EDLogger LOGGER_INFO = LoggerManager.getLogger(CrawlerCommonLogEnum.SPIDER_INFO.getValue());

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            List<String> allDomainTags = DomainTagUtils.getAllDomainTags();
            if (CollectionUtils.isNotEmpty(allDomainTags)) {
                for (String domainTag : allDomainTags) {
                    SpiderFactory.initSpider(domainTag);
                    LOGGER_INFO.info("init all spider success");
                }
            }
        }
    }
}
