package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.combiz.util.DateUtils;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrawlerConverterTag(name = "dateConverter")
public class DateCrawlerConverterImpl implements ICrawlerConverter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(DateCrawlerConverterImpl.class);

    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd";

    @Override
    public Object converter(Object sourceData, Object params) {
        String dateFormat = DEFAULT_DATEFORMAT;
        if (null != params) {
            dateFormat = (String) params;
        }
        DateFormat dateFormater = new SimpleDateFormat(dateFormat);
        if (null != sourceData) {
            String dateStr = (String) sourceData;
            if (StringUtils.isNotBlank(dateStr)) {
                try {
                    Date date = DateUtils.parse(dateStr, dateFormater);
                    return date;
                } catch (Exception e) {
                    LOGGER.error("parse date error", e);
                } finally {
                    LOGGER.info("to parse dateStr:" + dateStr);
                }
            }
        }
        return null;
    }
}
