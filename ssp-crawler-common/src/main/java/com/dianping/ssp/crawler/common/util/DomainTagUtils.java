package com.dianping.ssp.crawler.common.util;

import com.dianping.ssp.crawler.common.config.jsonparser.CrawlerJsonParserFactory;
import com.dianping.ssp.crawler.common.config.jsonparser.ICrawlerJsonParser;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DomainTagUtils {
    private static Map<String, String> domainTagCache = new ConcurrentHashMap<String, String>();

    public static synchronized void init() {
        List<ICrawlerJsonParser> parsers = CrawlerJsonParserFactory.getAllParsers();
        if (CollectionUtils.isNotEmpty(parsers)) {
            for (ICrawlerJsonParser parser : parsers) {
                parser.init();
            }
        }
    }

    public static void register(String domainTag, String config) {
        domainTagCache.put(domainTag, config);
    }

    public static String getForDomainTag(String domainTag) {
        if (MapUtils.isEmpty(domainTagCache)) {
            init();
        }
        return domainTagCache.get(domainTag);
    }

    public static List<String> getAllDomainTags() {
        if (MapUtils.isEmpty(domainTagCache)) {
            init();
        }
        return Lists.newArrayList(domainTagCache.keySet());
    }
}
