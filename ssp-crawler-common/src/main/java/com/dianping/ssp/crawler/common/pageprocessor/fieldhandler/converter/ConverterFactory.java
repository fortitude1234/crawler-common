package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * converterFactory
 */
public class ConverterFactory {
	private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.CONVERTER_FACTORY_ERROR.getValue());
	
    public Map<String, ICrawlerConverter> converterMap = new HashMap<String, ICrawlerConverter>();

    public void init() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ICrawlerConverter> result = SpringLocator.getApplicationContext().getBeansOfType(ICrawlerConverter.class);
            for (String beanName : result.keySet()) {
                ICrawlerConverter converter = result.get(beanName);
                CrawlerConverterTag converterTag = converter.getClass().getAnnotation(CrawlerConverterTag.class);
                if (null != converterTag) {
                    String name = converterTag.name();
                    converterMap.put(name, converter);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public ICrawlerConverter get(String coverterName) {
        return converterMap.get(coverterName);
    }

    private static class SingleHolder {
        private static ConverterFactory factory = new ConverterFactory();
        private static AtomicBoolean isInit = new AtomicBoolean(false);

        public static ConverterFactory getInstance() {
            if (!isInit.get()) {
                factory.init();
                isInit.set(true);
            }
            return factory;
        }
    }

    public static ICrawlerConverter getConverter(String converterName) {
        ConverterFactory factory = SingleHolder.getInstance();
        return factory.get(converterName);
    }
}
