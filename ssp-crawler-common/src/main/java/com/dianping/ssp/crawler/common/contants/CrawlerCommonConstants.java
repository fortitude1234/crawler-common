package com.dianping.ssp.crawler.common.contants;

public class CrawlerCommonConstants {

    public static class SubProcessType {

        public static final int PRE_SUB_PROCESS = 1;

        public static final int MID_SUB_PROCESS = 2;

        public static final int AFTER_SUB_PROCESS = 3;

    }

    public static class ProxyBaseConstants {
        public static final long PERIOD = 10800;
        public static final int SCORE = 1;
    }
    public static class JavaScriptConstant {
        public static final String PARAM_PAGE = "page";
        public static final String PARAM_METHODNAME = "methodName";
        public static final String PARAM_DATA = "data";
        public static final String DEFAULT_METHODNAME = "dataConverter";
    }

    public static class ProcessorContextConstant {
        public static final String BASE_URL = "baseUrl";
        public static final String CURRENT_URL = "currentUrl";
        public static final String DOMAIN_TAG = "domainTag";
        public static final String TARGET_URL = "targetUrl";
        public static final String RETRY_TIME = "retryTime";
    }

    public static final String URL_FILED_NAME_PATTERN = ".*?[u|U][r|R][l|L].*";

    public static final class PageFieldKeys {
        public static final String ORIGIN_URL = "originUrl";
        public static final String CRAWL_TIME = "crawlTime";
        public static final String THUMBNAIL = "thumbnail";
        public static final String CONTENT = "content";
        public static final String AUTHOR = "author";
        public static final String SOURCE = "source";
        public static final String TITLE = "title";
        public static final String ORIGINAL_TIME = "originalTime";
        public static final String CITY_ID= "cityId";
        public static final String CITY_NAME= "cityName";
    }


}
