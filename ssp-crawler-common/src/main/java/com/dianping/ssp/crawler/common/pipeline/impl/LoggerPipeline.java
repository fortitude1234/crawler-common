package com.dianping.ssp.crawler.common.pipeline.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.pipeline.CrawlerPipelineTag;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 直接日志输出
 */
@CrawlerPipelineTag(name = "loggerPipeline")
public class LoggerPipeline implements Pipeline {
    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.LOGGER_PIPELINE.getValue());

    @Override
    public void process(ResultItems resultItems, Task task) {
        logger.info("get page: " + resultItems.getRequest().getUrl());
    }
}
