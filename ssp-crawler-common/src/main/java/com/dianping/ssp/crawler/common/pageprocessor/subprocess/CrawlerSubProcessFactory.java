package com.dianping.ssp.crawler.common.pageprocessor.subprocess;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import org.apache.commons.collections.MapUtils;
import us.codecraft.webmagic.downloader.Downloader;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 过滤器工厂,将所有的过滤器都加载到内存中
 */
public class CrawlerSubProcessFactory {
    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.SUB_PROCESSOR_FACTORY_ERROR.getValue());

    private Map<String, ICrawlerSubProcess> cache = new HashMap<String, ICrawlerSubProcess>();

    public Map<String, ICrawlerSubProcess> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerSubProcess> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerSubProcess> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerSubProcess.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    ICrawlerSubProcess subProcess = result.get(beanName);
                    CrawlerSubProcessTag subProcessTag = subProcess.getClass().getAnnotation(CrawlerSubProcessTag.class);
                    if (null != subProcess) {
                        String name = subProcessTag.name();
                        cache.put(name, subProcess);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerSubProcessFactory factory = new CrawlerSubProcessFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerSubProcessFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerSubProcess> getCache() {
            return getInstance().getCache();
        }

    }

    public static ICrawlerSubProcess getSubProcess(String subProcessName) {
        return SingleHolder.getCache().get(subProcessName);
    }
}
