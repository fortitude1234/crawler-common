package com.dianping.ssp.crawler.common.pageprocessor.urlhandler.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.contants.DuplicateType;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.pageprocessor.urlhandler.ICrawlerUrlHandler;
import com.dianping.ssp.crawler.common.config.dto.UrlFilterConfig;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.dianping.ssp.crawler.common.util.JsonUtil;

import org.apache.commons.collections.CollectionUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本的url处理器
 */
public class BaseCrawlerUrlHandler implements ICrawlerUrlHandler {
    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.URL_FILTER_LOGGER.getValue());
    private static final EDLogger rawLogger = LoggerManager.getLogger(CrawlerCommonLogEnum.RAW_TEXT_LOGGER.getValue());

    @Override
    public ProStatus handler(Page page, List<UrlFilterConfig> urlFilterConfigs) {
        if (CollectionUtils.isEmpty(urlFilterConfigs)) {
            return ProStatus.success();
        }
        ProcessorContext context = ProcessorContext.getContext(page);

        UrlFilterConfig currentUrlFilterConfig = null;
        for (UrlFilterConfig urlFilterConfig : urlFilterConfigs) {
            if (page.getRequest().getUrl().matches(urlFilterConfig.getBaseUrlPattern())) {
                currentUrlFilterConfig = urlFilterConfig;
                break;
            }
        }
        if (currentUrlFilterConfig == null) {
            return ProStatus.success();
        }

        String referer = page.getRequest().getUrl();

        //过滤页面发现的url
        List<Request> requestsFromPage = (List<Request>) context.getParam(CrawlerCommonConstants.ProcessorContextConstant.TARGET_URL);
        Request parentRequest=page.getRequest();
        Integer duplicate=(Integer)parentRequest.getExtra("duplicate");
        if (CollectionUtils.isNotEmpty(requestsFromPage)) {
            logger.info("domainTag: " + context.getParam(CrawlerCommonConstants.ProcessorContextConstant.DOMAIN_TAG) + ", url: " + page.getRequest().getUrl() + ", request: " + JsonUtil.toJson(requestsFromPage));
            for (Request requestFromPage : requestsFromPage) {
                if (CollectionUtils.isNotEmpty(urlFilterConfigs)) {
                    for (String targetUrl : currentUrlFilterConfig.getTargetUrlPatterns()) {
                        if (requestFromPage.getUrl().matches(targetUrl)) {
                        	requestFromPage.putExtra("referer", referer);
                        	if (duplicate!=null && duplicate==DuplicateType.NEED_DUPLICATE){
                        		requestFromPage.putExtra("duplicate", DuplicateType.NEED_DUPLICATE);
                        	}
                        	page.addTargetRequest(requestFromPage);
                        }
                    }
                }
            }
        } else {
            logger.info("0 requests found from url: " + page.getRequest().getUrl());
            rawLogger.info("url: " + page.getRequest().getUrl() + ", rawText: " + page.getRawText());
        }

        return ProStatus.success();
    }

}
