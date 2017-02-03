package com.dianping.ssp.crawler.common.scheduler;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.squirrel.client.StoreKey;
import com.dianping.squirrel.client.impl.redis.RedisStoreClient;
import com.dianping.squirrel.common.exception.StoreException;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.similarity.simhash.Fingerprint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import us.codecraft.webmagic.Request;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by iClaod on 10/11/16.
 */
@Repository
public class RedisRepository {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.REDIS_REPOSITORY.getValue());

    @Autowired
    @Qualifier("redisClient")
    private RedisStoreClient storeClient;

    private static final String CATEGORY_ALL_URL_SET = "crawler_all_url_set";

    private static final String CATEGORY_URL_QUEUE = "crawler_wait_queue";
    
    private static final String CATEGORY_ALL_ARTICLE_FINGERPRINT_SET = "crawler_articles_fp";

    private static final int DEFAULT_RETRY_TIMES = 3;

//    public RedisRepository() {
//        storeClient = (RedisStoreClient) SpringLocator.getApplicationContext().getBean("redisClient");
//    }



    public boolean addToFingerprintSet(Fingerprint fingerprint) {
        boolean isSuccess = false;
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                Long result = storeClient.sadd(buildAllFingerprintStoreKey(), fingerprint);
                if (result != null && result > 0L) {
                    isSuccess = true;
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("fail to add ALL_ARTICLE_FINGERPRINT_SET key: " + CATEGORY_ALL_ARTICLE_FINGERPRINT_SET + ", fingerprint: " + fingerprint, e);
            }
        }
        return isSuccess;
    }
    public Set<Fingerprint> loadAllFingerprintSet() {
        Set<Fingerprint> result = null;
        try {
            result = storeClient.smembers(buildAllFingerprintStoreKey());
        } catch (Exception e) {
            LOGGER.error("fail to query ALL_ARTICLE_FINGERPRINT_SET, key: " + CATEGORY_ALL_ARTICLE_FINGERPRINT_SET, e);
        }
        return result==null?new HashSet<Fingerprint>() : result;
    }
    public void refreshAllUrlSet(String domainTag) {
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                storeClient.delete(buildAllUrlStoreKey(domainTag));
                break;
            } catch (StoreException e) {
                LOGGER.error("fail to delete ALL_URL_SET key: " + domainTag, e);
            }
        }
    }

    public long getAllUrlSetSize(String domainTag) {
        Long result = null;
        try {
            result = storeClient.scard(buildAllUrlStoreKey(domainTag));
        } catch (Exception e) {
            LOGGER.error("fail to query ALL_URL_SET size, key: " + domainTag, e);
        }
        return result == null ? 0L : result;
    }

    public long getUrlQueueSize(String domainTag) {
        Long result = null;
        try {
            result = storeClient.llen(buildUrlQueueStoreKey(domainTag));
        } catch (Exception e) {
            LOGGER.error("fail to query URL_QUEUE size, key: " + domainTag, e);
        }
        return result == null ? 0L : result;
    }

    public boolean addToUrlSet(String domainTag, String url) {
        boolean isSuccess = false;
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                Long result = storeClient.sadd(buildAllUrlStoreKey(domainTag), url);
                if (result != null && result > 0L) {
                    isSuccess = true;
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("fail to add ALL_URL_SET key: " + domainTag + ", url: " + url, e);
            }
        }
        return isSuccess;
    }

    public boolean addToUrlQueue(String domainTag, Request request) {
        boolean isSuccess = false;
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                Long result = storeClient.rpush(buildUrlQueueStoreKey(domainTag), request);
                if (result != null && result > 0L) {
                    isSuccess = true;
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("fail to add URL_QUEUE key: " + domainTag + ", url: " + request, e);
            }
        }
        return isSuccess;
    }

    public Request getUrlFromQueue(String domainTag) throws Exception {
        Object result = storeClient.lpop(buildUrlQueueStoreKey(domainTag));
        if (result == null) {
            return null;
        }
        return (Request) result;
    }

    private StoreKey buildAllFingerprintStoreKey() {
        return new StoreKey(CATEGORY_ALL_ARTICLE_FINGERPRINT_SET);
    }

    private StoreKey buildAllUrlStoreKey(String str) {
        return new StoreKey(CATEGORY_ALL_URL_SET, str);
    }

    private StoreKey buildUrlQueueStoreKey(String str) {
        return new StoreKey(CATEGORY_URL_QUEUE, str);
    }
}
