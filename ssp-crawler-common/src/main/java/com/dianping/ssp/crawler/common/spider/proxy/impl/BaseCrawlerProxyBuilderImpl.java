package com.dianping.ssp.crawler.common.spider.proxy.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.spider.proxy.ICrawlerProxyBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class BaseCrawlerProxyBuilderImpl implements ICrawlerProxyBuilder {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.PROXY_ERROR.getValue());
//    @Resource
//    private HttpProxyExportService httpProxyExportService;


    @Override
    public List<String[]> builder(Integer count) {
        List<String[]> resultProxyHosts = new ArrayList<String[]>();
//        try {
//            List<String> proxyHosts = httpProxyExportService.queryHttpProxyInfo(CrawlerCommonConstants.ProxyBaseConstants.PERIOD, CrawlerCommonConstants.ProxyBaseConstants.SCORE, count);
//            if (CollectionUtils.isNotEmpty(proxyHosts)) {
//                for (String proxyHost : proxyHosts) {
//                    String[] proxyHostPortStr = StringUtils.split(proxyHost, ":");
//                    resultProxyHosts.add(proxyHostPortStr);
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error("error to builder proxy ,count:" + count, e);
//        }
        return resultProxyHosts;
    }
}
