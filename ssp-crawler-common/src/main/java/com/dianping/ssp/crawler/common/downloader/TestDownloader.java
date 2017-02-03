package com.dianping.ssp.crawler.common.downloader;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by iClaod on 11/24/16.
 */
public class TestDownloader {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.USELESS.getValue());

    public static String downloadStr(String uri) {
        CloseableHttpClient client = HttpClientBuilder.create().setConnectionTimeToLive(30, TimeUnit.SECONDS).build();
        HttpGet get = new HttpGet(uri);
        for (int i = 0; i < 3; i++) {
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return EntityUtils.toString(entity);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("error", e);
            } finally {
                try {
                    client.close();
                } catch (Exception e1) {
                    LOGGER.error("error", e1);
                }
            }
        }
        return null;
    }
}
