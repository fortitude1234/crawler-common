package com.dianping.ssp.crawler.common.trigger;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpiderTriggerFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(SpiderTriggerFactory.class);

    private Map<String, ICrawlerSpiderTrigger> cache = new HashMap<String, ICrawlerSpiderTrigger>();

    public Map<String, ICrawlerSpiderTrigger> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerSpiderTrigger> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerSpiderTrigger> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerSpiderTrigger.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    ICrawlerSpiderTrigger trigger = result.get(beanName);
                    CrawlerTriggerTag triggerTag = trigger.getClass().getAnnotation(CrawlerTriggerTag.class);
                    if (null != triggerTag) {
                        String name = triggerTag.name();
                        cache.put(name, trigger);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static SpiderTriggerFactory factory = new SpiderTriggerFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static SpiderTriggerFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerSpiderTrigger> getCache() {
            return getInstance().getCache();
        }
    }

    public static ICrawlerSpiderTrigger getSpiderTrigger(String triggerName) {
        return SingleHolder.getCache().get(triggerName);
    }
}
