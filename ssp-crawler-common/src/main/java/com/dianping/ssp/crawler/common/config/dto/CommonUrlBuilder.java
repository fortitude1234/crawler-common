package com.dianping.ssp.crawler.common.config.dto;

public class CommonUrlBuilder extends UrlFilterConfig {

    /**
     * 
     */
    private static final long serialVersionUID = 9179410274578657081L;
    /**
     * 当前地址
     */
    public String currentUrl;
    /**
     * 当前爬取深度
     */
    public Integer currentDeep;



    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public Integer getCurrentDeep() {
        return currentDeep;
    }

    public void setCurrentDeep(Integer currentDeep) {
        this.currentDeep = currentDeep;
    }

}
