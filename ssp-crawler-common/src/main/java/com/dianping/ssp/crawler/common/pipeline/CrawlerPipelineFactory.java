package com.dianping.ssp.crawler.common.pipeline;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.pipeline.impl.LoggerPipeline;
import org.apache.commons.collections.MapUtils;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrawlerPipelineFactory {
    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.PIPELINE_FACTORY_ERROR.getValue());


    private Map<String, Pipeline> cache = new HashMap<String, Pipeline>();

    public Map<String, Pipeline> getCache() {
        return cache;
    }

    public void setCache(Map<String, Pipeline> cache) {
        this.cache = cache;
    }

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Pipeline> result = SpringLocator.getApplicationContext().getBeansOfType(Pipeline.class);
            if (MapUtils.isNotEmpty(result)) {
                for (String beanName : result.keySet()) {
                    Pipeline pipeline = result.get(beanName);
                    CrawlerPipelineTag pipelineTag = pipeline.getClass().getAnnotation(CrawlerPipelineTag.class);
                    if (null != pipeline) {
                        String name = pipelineTag.name();
                        cache.put(name, pipeline);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static class SingleHolder {
        private static CrawlerPipelineFactory factory = new CrawlerPipelineFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);
        private static Object lock = new Object();

        private static CrawlerPipelineFactory getInstance() {
            synchronized (lock) {
                if (!isInit.get()) {
                    factory.init();
                    isInit.set(true);
                }
                return factory;
            }
        }

        public static Map<String, Pipeline> getCache() {
            return getInstance().getCache();
        }
    }

    public static Pipeline getPipeline(String pipelineName) {
        Pipeline pipeline = SingleHolder.getCache().get(pipelineName);
        if (null == pipeline) {
            pipeline = new LoggerPipeline();
        }
        return pipeline;
    }
}
