package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.config.dto.PageParserConfig;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.FieldParserTool;
import com.dianping.ssp.crawler.common.config.dto.FieldParserConfig;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.ICrawlerFieldHandler;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;

import java.util.List;
import java.util.Map;

/**
 * 基本的field处理器
 */
public class BaseCrawlerFieldHandlerImpl implements ICrawlerFieldHandler {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.FIELD_BUILDER_LOGGER.getValue());

    @Override
    public ProStatus parseField(Page page, List<PageParserConfig> pageParserConfigs) {
        ProcessorContext context = ProcessorContext.getContext(page);
        if (CollectionUtils.isNotEmpty(pageParserConfigs)) {
            for (PageParserConfig pageParserConfig : pageParserConfigs) {
                if (page.getRequest().getUrl().matches(pageParserConfig.getTargetUrlPattern()) && CollectionUtils.isNotEmpty(pageParserConfig.getFieldParserConfigs())) {
                    //解析request的extra中需要的部分
                    if (CollectionUtils.isNotEmpty(pageParserConfig.getFieldFromRequest())) {
                        Map<String, Object> requestExtras = page.getRequest().getExtras();
                        for (String field: pageParserConfig.getFieldFromRequest()) {
                            if (requestExtras.containsKey(field)) {
                                page.putField(field, requestExtras.get(field));
                            }
                        }
                    }

                    //解析页面上需要的部分
                    for (FieldParserConfig fieldParserConfig : pageParserConfig.getFieldParserConfigs()) {
                        String fieldName = fieldParserConfig.getFieldName();
                        Object result = null;
                        try {
                            if (PageParserConfig.PageTypeConstants.HTML.equalsIgnoreCase(pageParserConfig.getPageType())) {
                                result = FieldParserTool.parse(page.getHtml(), fieldParserConfig, context);
                            } else if (PageParserConfig.PageTypeConstants.JSON.equalsIgnoreCase(pageParserConfig.getPageType())) {
                                result = FieldParserTool.parse(page.getJson(), fieldParserConfig, context);
                            }
                            page.putField(fieldName, result);
                        } catch (Exception e) {
                            LOGGER.error("error when parsing field, field: " + fieldParserConfig.getFieldName() + ", url: " + page.getRequest().getUrl(), e);
                        }
                    }
                }
            }
        }
        return ProStatus.success();
    }

}
