package com.dianping.ssp.crawler.common.pageprocessor.urlhandler;

import com.dianping.ssp.crawler.common.config.dto.UrlFilterConfig;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * url处理器
 */
public interface ICrawlerUrlHandler {
    public ProStatus handler(Page page, List<UrlFilterConfig> urlFilterConfigs);
}
