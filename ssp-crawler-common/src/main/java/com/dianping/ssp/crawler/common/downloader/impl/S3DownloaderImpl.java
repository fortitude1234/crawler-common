package com.dianping.ssp.crawler.common.downloader.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderTag;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.file.download.SSPDownload;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrawlerDownloaderTag(name = "s3Downloader")
public class S3DownloaderImpl extends HttpClientDownloader {
    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.HTML_DOWNLOADER.getValue());



    @Override
    public Page download(Request request, Task task) {
        Page page = new Page();
        try {
            String content = SSPDownload.MssS3.downloadPlainFileContent(request.getUrl());
            page.setRawText(content);
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.setStatusCode(200);
        } catch (Exception e) {
            page.setStatusCode(500);
            logger.error("download from s3 error, url: " + request.getUrl(), e);
        }
        return page;
    }

}
