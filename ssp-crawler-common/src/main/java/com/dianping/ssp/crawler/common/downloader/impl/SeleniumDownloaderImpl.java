package com.dianping.ssp.crawler.common.downloader.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderTag;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * 使用Selenium调用浏览器进行渲染。基于phantomJS。<br>
 * 需要下载Selenium driver支持。<br>
 *
 * @author code4crafter@gmail.com <br>
 *         Date: 13-7-26 <br>
 *         Time: 下午1:37 <br>
 */
@CrawlerDownloaderTag(name = "seleniumDownloader")
public class SeleniumDownloaderImpl extends AbstractDownloader implements Closeable{

    private volatile SeleniumWebDriverPool webDriverPool;

    private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.SELENIUM_DOWNLOADER.getValue());

    private int sleepTime = 5000;

    private int poolSize = 1;

//	/**
//	 * 新建
//	 *
//	 * @param chromeDriverPath chromeDriverPath
//	 */
//	public SeleniumDownloader(String chromeDriverPath) {
//		System.getProperties().setProperty("webdriver.chrome.driver",
//				chromeDriverPath);
//	}

    /**
     * Constructor without any filed. Construct PhantomJS browser
     *
     * @author bob.li.0718@gmail.com
     */
    public SeleniumDownloaderImpl() {
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime sleepTime
     * @return this
     */
    public SeleniumDownloaderImpl setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    @Override
    public Page download(Request request, Task task) {
        checkInit();
        WebDriver webDriver = null;
        Page page = null;
        try {
            webDriver = webDriverPool.get();
        } catch (Exception e) {
            logger.error("interrupted", e);
            if (task.getSite().getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, task.getSite());
            }
            return null;
        }
        try {
            logger.info("downloading page: " + request.getUrl());
            webDriver.get(request.getUrl());
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error("interrupted", e);
            }
            //webDriver.getWindowHandle();
            WebDriver.Options manage = webDriver.manage();
            Site site = task.getSite();
            if (site.getCookies() != null) {
                for (Map.Entry<String, String> cookieEntry : site.getCookies()
                        .entrySet()) {
                    Cookie cookie = new Cookie(cookieEntry.getKey(),
                            cookieEntry.getValue());
                    manage.addCookie(cookie);
                }
            }
            //webDriver.
            /*
             * TODO You can add mouse event or other processes
             *
             * @author: bob.li.0718@gmail.com
             */

            WebElement webElement = webDriver.findElement(By.xpath("/html"));
            String content = webElement.getAttribute("outerHTML");
            page = new Page();
            page.setRawText(content);
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);

        } catch (Exception e) {
            logger.error("interrupted", e);
            if (task.getSite().getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, task.getSite());
            }
            return null;
        } finally {
            webDriverPool.returnToPool(webDriver);
        }
        return page;
    }

    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this) {
                webDriverPool = new SeleniumWebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }

}
