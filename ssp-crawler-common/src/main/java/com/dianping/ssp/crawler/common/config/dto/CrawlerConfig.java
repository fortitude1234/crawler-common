package com.dianping.ssp.crawler.common.config.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class CrawlerConfig implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7796710549377294662L;
    /**
     * 标注是哪个网站,默认为该json文件的文件名
     */
    private String domainTag;

    private SchedulerConfig scheduler = new SchedulerConfig();

    private PageProcessorConfig pageProcessor = new PageProcessorConfig();

    private List<String> pipelines = Lists.newArrayList();

    private SiteConfig site = new SiteConfig();

    private CrawlerBaseInfo crawlerBaseInfo;

    private List<DownloaderConfig> downloaders = Lists.newArrayList();

    private List<CrawlerTrigger> crawlerTrigger;


    public String getDomainTag() {
        return domainTag;
    }

    public void setDomainTag(String domainTag) {
        this.domainTag = domainTag;
    }

    public CrawlerBaseInfo getCrawlerBaseInfo() {
        return crawlerBaseInfo;
    }

    public void setCrawlerBaseInfo(CrawlerBaseInfo crawlerBaseInfo) {
        this.crawlerBaseInfo = crawlerBaseInfo;
    }

    public SiteConfig getSite() {
        return site;
    }

    public void setSite(SiteConfig site) {
        this.site = site;
    }

    public List<CrawlerTrigger> getCrawlerTrigger() {
        return crawlerTrigger;
    }

    public void setCrawlerTrigger(List<CrawlerTrigger> crawlerTrigger) {
        this.crawlerTrigger = crawlerTrigger;
    }

    public SchedulerConfig getScheduler() {
        return scheduler;
    }

    public SchedulerConfig getSchedulerConfig() {
        return scheduler;
    }

    public void setScheduler(SchedulerConfig scheduler) {
        this.scheduler = scheduler;
    }

    public List<String> getPipelines() {
        return pipelines;
    }

    public List<String> getPipelinesConfig() {
        return pipelines;
    }

    public void setPipelines(List<String> pipelines) {
        this.pipelines = pipelines;
    }

    public PageProcessorConfig getPageProcessor() {
        return pageProcessor;
    }

    public PageProcessorConfig getPageProcessorConfig() {
        return pageProcessor;
    }

    public void setPageProcessor(PageProcessorConfig pageProcessor) {
        this.pageProcessor = pageProcessor;
    }

    public List<DownloaderConfig> getDownloaders() {
        return downloaders;
    }

    public void setDownloaders(List<DownloaderConfig> downloaders) {
        this.downloaders = downloaders;
    }
}
