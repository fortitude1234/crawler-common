package com.dianping.ssp.crawler.common.config.dto;

/**
 * Created by iClaod on 10/11/16.
 */
public class SchedulerConfig {

    private boolean hasDetailPage = false;

    private String detailPageDomainTag = null;

    private String listPageUrlPattern = ".*";

    private boolean needCheckDuplicate = true;
    private boolean needCheckDetailPageDuplicate = true;


    public boolean isHasDetailPage() {
        return hasDetailPage;
    }

    public boolean hasDetailPage() {
        return hasDetailPage;
    }

    public void setHasDetailPage(boolean hasDetailPage) {
        this.hasDetailPage = hasDetailPage;
    }

    public String getDetailPageDomainTag() {
        return detailPageDomainTag;
    }

    public void setDetailPageDomainTag(String detailPageDomainTag) {
        this.detailPageDomainTag = detailPageDomainTag;
    }

    public String getListPageUrlPattern() {
        return listPageUrlPattern;
    }

    public void setListPageUrlPattern(String listPageUrlPattern) {
        this.listPageUrlPattern = listPageUrlPattern;
    }

    public boolean isNeedCheckDuplicate() {
        return needCheckDuplicate;
    }

    public void setNeedCheckDuplicate(boolean needCheckDuplicate) {
        this.needCheckDuplicate = needCheckDuplicate;
    }

    public boolean isNeedCheckDetailPageDuplicate() {
        return needCheckDetailPageDuplicate;
    }

    public void setNeedCheckDetailPageDuplicate(boolean needCheckDetailPageDuplicate) {
        this.needCheckDetailPageDuplicate = needCheckDetailPageDuplicate;
    }
}
