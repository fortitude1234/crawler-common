package com.dianping.ssp.crawler.common.config.dto;

import java.util.Map;

public class CrawlerUrl {

    private String url;

    private String baseUrl;

    private Integer urlStatus;

    private Boolean isUpdate;

    private Integer messageCode;

    private Map<String, Object> extra;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getUrlStatus() {
		return urlStatus;
	}

	public void setUrlStatus(Integer urlStatus) {
		this.urlStatus = urlStatus;
	}

	public Boolean getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public Integer getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(Integer messageCode) {
        this.messageCode = messageCode;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

}
