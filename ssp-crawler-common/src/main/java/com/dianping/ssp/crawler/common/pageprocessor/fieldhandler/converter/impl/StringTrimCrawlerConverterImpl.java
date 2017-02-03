package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl;


import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrawlerConverterTag(name = "stringTrimConverter")
public class StringTrimCrawlerConverterImpl implements ICrawlerConverter {

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null == sourceData) {
            return null;
        }
        String data = (String) sourceData;
        return replaceBlank(data);
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
