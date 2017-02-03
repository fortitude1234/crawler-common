package com.dianping.ssp.crawler.common.config.jsonparser;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * json配置文件获取类
 */
public class CrawlerJsonParserFactory {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(CrawlerCommonLogEnum.class);
    private Map<String, ICrawlerJsonParser> cache = new HashMap<String, ICrawlerJsonParser>();

    public Map<String, ICrawlerJsonParser> getCache() {
        return cache;
    }

    public void setCache(Map<String, ICrawlerJsonParser> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerJsonParser> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerJsonParser.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    cache.put(beanName, result.get(beanName));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerJsonParserFactory factory = new CrawlerJsonParserFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerJsonParserFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, ICrawlerJsonParser> getCache() {
            return getInstance().getCache();
        }
    }

    public static List<ICrawlerJsonParser> getAllParsers() {
        Map<String, ICrawlerJsonParser> cache = SingleHolder.getCache();
        if (MapUtils.isNotEmpty(cache)) {
            return Lists.newArrayList(cache.values());
        }
        return null;
    }
}
