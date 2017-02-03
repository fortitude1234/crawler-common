package com.dianping.ssp.crawler.common.config.dto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by iClaod on 10/12/16.
 */
public class PageProcessorConfig {

    private List<UrlFilterConfig> urlFilterConfigs = null;

    private List<PageParserConfig> pageParserConfigs = Lists.newArrayList();

    private List<String> preSubProcessor = Lists.newArrayList();
    private List<String> midSubProcessor = Lists.newArrayList();
    private List<String> afterSubProcessor = Lists.newArrayList();

    public List<UrlFilterConfig> getUrlFilterConfigs() {
        return urlFilterConfigs;
    }

    public void setUrlFilterConfigs(List<UrlFilterConfig> urlFilterConfigs) {
        this.urlFilterConfigs = urlFilterConfigs;
    }

    public List<PageParserConfig> getPageParserConfigs() {
        return pageParserConfigs;
    }

    public void setPageParserConfigs(List<PageParserConfig> pageParserConfigs) {
        this.pageParserConfigs = pageParserConfigs;
    }


    public List<String> getPreSubProcessor() {
        return preSubProcessor;
    }

    public void setPreSubProcessor(List<String> preSubProcessor) {
        this.preSubProcessor = preSubProcessor;
    }

    public List<String> getMidSubProcessor() {
        return midSubProcessor;
    }

    public void setMidSubProcessor(List<String> midSubProcessor) {
        this.midSubProcessor = midSubProcessor;
    }

    public List<String> getAfterSubProcessor() {
        return afterSubProcessor;
    }

    public void setAfterSubProcessor(List<String> afterSubProcessor) {
        this.afterSubProcessor = afterSubProcessor;
    }
}
