package com.dianping.ssp.crawler.common.downloader.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderTag;
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

@CrawlerDownloaderTag(name = "htmlUnitDownloader")
public class HtmlUnitDownloaderImpl extends HttpClientDownloader {
    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.HTML_DOWNLOADER.getValue());

    private static WebClient initWebClient() {
        // 创建一个webclient
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(60 * 1000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getCookieManager().setCookiesEnabled(true);
        return webClient;
    }

    protected void doHandlerWebClient(WebClient webClient) {
        int count = Integer.MAX_VALUE;
        while (count != 0) {
            count = webClient.waitForBackgroundJavaScript(60 * 1000);
        }
    }

    @Override
    public Page download(Request request, Task task) {

    	WebClient webClient = initWebClient();
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        logger.info("downloading page : " + request.getUrl());
        WebResponse webResponse = null;
        int statusCode = 0;
        try {
            WebRequest webRequest = getWebRequest(request, site, headers);
            webRequest.setCharset(charset);

            doHandlerWebClient(webClient);
            HtmlPage htmlUnitPage = webClient.getPage(webRequest);
            webResponse = htmlUnitPage.getWebResponse();
            statusCode = webResponse.getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, htmlUnitPage);
                onSuccess(request);
                return page;
            } else {
                logger.info("code error " + statusCode + "\t" + request.getUrl());
                return null;
            }
        } catch (IOException e) {
            logger.info("download page " + request.getUrl() + " error", e);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                webClient.close();
            } catch (Exception e) {
                logger.error("close response fail", e);
            }
        }
    }

    protected Page handleResponse(Request request, HtmlPage htmlPage) throws IOException {
        Page page = new Page();
        page.setRawText(htmlPage.asXml());
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode((Integer) request.getExtra(Request.STATUS_CODE));
        return page;
    }

    protected String getContent(String charset, WebResponse webResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(webResponse.getContentAsStream());

            String htmlCharset = webResponse.getContentCharsetOrNull();
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.info("Charset autodetect failed, use " + Charset.defaultCharset() + " as charset. Please specify charset in Site.setCharset()");
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(webResponse.getContentAsStream(), charset);
        }
    }

    protected WebRequest getWebRequest(Request request, Site site, Map<String, String> headers) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(request.getUrl()));
        selectRequestMethod(webRequest, request);
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                webRequest.setAdditionalHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        if (site.getHttpProxyPool().isEnable()) {
            HttpHost host = site.getHttpProxyFromPool();
            webRequest.setProxyHost(host.getHostName());
            webRequest.setProxyPort(host.getPort());
            request.putExtra(Request.PROXY, host);

        }
        webRequest.setCharset(site.getCharset());
        return webRequest;
    }

    protected WebRequest selectRequestMethod(WebRequest webRequest, Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            // default get
            webRequest.setHttpMethod(HttpMethod.GET);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair.length > 0) {
                webRequest.setRequestParameters(toHtmlUnit(nameValuePair));
            }
            webRequest.setHttpMethod(HttpMethod.POST);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            webRequest.setHttpMethod(HttpMethod.HEAD);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            webRequest.setHttpMethod(HttpMethod.PUT);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            webRequest.setHttpMethod(HttpMethod.DELETE);
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            webRequest.setHttpMethod(HttpMethod.TRACE);
        }
        return webRequest;
    }

    public static List<com.gargoylesoftware.htmlunit.util.NameValuePair> toHtmlUnit(final NameValuePair[] pairs) {
        final List<com.gargoylesoftware.htmlunit.util.NameValuePair> pairs2 = new ArrayList<com.gargoylesoftware.htmlunit.util.NameValuePair>();
        for (int i = 0; i < pairs.length; i++) {
            final NameValuePair pair = pairs[i];
            com.gargoylesoftware.htmlunit.util.NameValuePair pair2 = new com.gargoylesoftware.htmlunit.util.NameValuePair(pair.getName(), pair.getValue());
            pairs2.add(pair2);
        }
        return pairs2;
    }
}
