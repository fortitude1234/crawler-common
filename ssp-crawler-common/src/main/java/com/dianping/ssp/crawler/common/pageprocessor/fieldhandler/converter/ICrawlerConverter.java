package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter;

/**
 * 转换器
 */
public interface ICrawlerConverter {
    public Object converter(Object sourceData, Object params);
}
