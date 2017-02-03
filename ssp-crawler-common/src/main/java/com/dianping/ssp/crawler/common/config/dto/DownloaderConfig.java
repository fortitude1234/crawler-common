package com.dianping.ssp.crawler.common.config.dto;

/**
 * Created by iClaod on 10/12/16.
 */
public class DownloaderConfig {

    private String urlPattern = ".*";

    private String downloader = "htmlDownloader";

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getDownloader() {
        return downloader;
    }

    public void setDownloader(String downloader) {
        this.downloader = downloader;
    }
}
