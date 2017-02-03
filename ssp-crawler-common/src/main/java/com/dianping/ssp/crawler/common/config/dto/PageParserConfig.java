package com.dianping.ssp.crawler.common.config.dto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by iClaod on 10/12/16.
 */
public class PageParserConfig {

    private String targetUrlPattern = ".*";
    private String pageType = PageTypeConstants.HTML;
    private List<FieldParserConfig> fieldParserConfigs = Lists.newArrayList();
    private List<String> fieldFromRequest = Lists.newArrayList();


    public final class PageTypeConstants {
        public static final String HTML = "html";
        public static final String JSON = "json";
    }

    public String getTargetUrlPattern() {
        return targetUrlPattern;
    }

    public void setTargetUrlPattern(String targetUrlPattern) {
        this.targetUrlPattern = targetUrlPattern;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public List<FieldParserConfig> getFieldParserConfigs() {
        return fieldParserConfigs;
    }

    public void setFieldParserConfigs(List<FieldParserConfig> fieldParserConfigs) {
        this.fieldParserConfigs = fieldParserConfigs;
    }

    public List<String> getFieldFromRequest() {
        return fieldFromRequest;
    }

    public void setFieldFromRequest(List<String> fieldFromRequest) {
        this.fieldFromRequest = fieldFromRequest;
    }
}
