package com.dianping.ssp.crawler.common.util;

import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import us.codecraft.webmagic.Request;

import java.util.List;
import java.util.Map;

/**
 * Created by iClaod on 10/14/16.
 */
public class ContextUtil {


    public static void addMapAsTargetUrlToContext(Map<String, Object> map, ProcessorContext context) {
        List<Request> requests = (List<Request>) context.getParam(CrawlerCommonConstants.ProcessorContextConstant.TARGET_URL);
        if (requests == null) {
            requests = Lists.newArrayList();
        }
        Request request = new Request();
        for (Map.Entry<String, Object> entry: map.entrySet()) {
            String key = entry.getKey();
            if (key.matches(CrawlerCommonConstants.URL_FILED_NAME_PATTERN)) {
                request.setUrl((String)entry.getValue());
            } else {
                request.putExtra(entry.getKey(), entry.getValue());
            }
        }
        requests.add(request);
        context.addParam(CrawlerCommonConstants.ProcessorContextConstant.TARGET_URL, requests);
    }

    public static void addStringAsRequestToContext(String url, ProcessorContext context) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("url", url);
        addMapAsTargetUrlToContext(map, context);
    }

}
