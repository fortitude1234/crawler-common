package com.dianping.ssp.crawler.common.config.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础的url的设置
 */
public class CrawlerBaseInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2257821138908166856L;
    /**
     * 主地址
     * 
     * @return
     */
    private List<String> baseUrls;

    private boolean refreshRedisWhenStart = false;
    /**
     * 线程数
     */
    private Integer threadCount = 3;
    /**
     * url管理实现类
     */
    private String urlManager;
    /**
     * 结果处理类
     */
    private String resultHandler;

    private boolean exitWhenComplete = true;

    public List<String> getBaseUrls() {
        return baseUrls;
    }

    public void setBaseUrls(List<String> baseUrls) {
        this.baseUrls = baseUrls;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public String getUrlManager() {
        return urlManager;
    }

    public void setUrlManager(String urlManager) {
        this.urlManager = urlManager;
    }

    public String getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(String resultHandler) {
        this.resultHandler = resultHandler;
    }


    public boolean isRefreshRedisWhenStart() {
        return refreshRedisWhenStart;
    }

    public void setRefreshRedisWhenStart(boolean refreshRedisWhenStart) {
        this.refreshRedisWhenStart = refreshRedisWhenStart;
    }

    public boolean isExitWhenComplete() {
        return exitWhenComplete;
    }

    public void setExitWhenComplete(boolean exitWhenComplete) {
        this.exitWhenComplete = exitWhenComplete;
    }
}
