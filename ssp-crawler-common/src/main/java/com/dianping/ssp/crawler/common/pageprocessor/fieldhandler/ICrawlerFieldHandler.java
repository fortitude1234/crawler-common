package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler;

import com.dianping.ssp.crawler.common.config.dto.PageParserConfig;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * 输出处理器
 */
public interface ICrawlerFieldHandler {

    ProStatus parseField(Page page, List<PageParserConfig> fieldBuilders);

}
