package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl;

import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import org.codehaus.plexus.util.StringUtils;

import java.util.Arrays;

@CrawlerConverterTag(name = "stringSplitConverter")
public class StringSpiltCrawlerConverterImpl implements ICrawlerConverter {

    private static final String DEFAULT_SPLIT = ",";

    @Override
    public Object converter(Object sourceData, Object params) {
        String splitStr = null;
        if (null == params) {
            splitStr = DEFAULT_SPLIT;
        } else {
            splitStr = (String) params;
        }
        if (null != sourceData) {
            String sourceDataStr = (String) sourceData;
            if (StringUtils.isNotBlank(sourceDataStr)) {
                String[] resultStrs = StringUtils.split(sourceDataStr, splitStr);
                if (null != resultStrs) {
                    return Arrays.asList(resultStrs);
                }
            }
        }
        return null;
    }

}
