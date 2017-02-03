package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;

@CrawlerConverterTag(name = "dateTimeConverter")
public class DateTimeCrawlerConverterImpl implements ICrawlerConverter {

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null == sourceData) {
            return null;
        }
        Long data = null;
        if ((sourceData instanceof String && StringUtils.isNumeric((String) sourceData))) {
            data = Long.parseLong((String) sourceData);
        }
        if (sourceData instanceof Number) {
            data = ((Number)sourceData).longValue();
        }
        if (data != null) {
            if (data < 100000000000L) {
                data = data * 1000;
            }
            Date date = new Date(data);
            return date;
        }
        if (sourceData instanceof String && params!=null && params instanceof String){
        	String dateStr=(String) sourceData;
        	String patten=(String)params;
        	DateTimeFormatter format= DateTimeFormat.forPattern(patten);
        	return DateTime.parse(dateStr,format).toDate();
        }
        return null;
    }

}
