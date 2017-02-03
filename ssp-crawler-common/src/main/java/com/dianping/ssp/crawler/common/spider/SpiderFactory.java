package com.dianping.ssp.crawler.common.spider;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.config.ICrawlerConfigParser;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.config.dto.DownloaderConfig;
import com.dianping.ssp.crawler.common.config.impl.JsonCrawlerConfigParser;
import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderFactory;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.pageprocessor.CommonPageProcessorImpl;
import com.dianping.ssp.crawler.common.pipeline.CrawlerPipelineFactory;
import com.dianping.ssp.crawler.common.spider.spiderlistener.DpSpiderListener;
import com.dianping.ssp.crawler.common.scheduler.DpRedisScheduler;
import com.dianping.ssp.crawler.common.trigger.CrawlerTriggerController;
import com.dianping.ssp.crawler.common.util.ThreadPoolUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 爬虫构建工厂，能够根据一个文件初始化好所有的爬虫
 */
public class SpiderFactory {
    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.SPIDER_FACTORY.getValue());

    private static Map<String, DpSpider> spiderCache = new HashMap<String, DpSpider>();

    public static void initSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_INIT", domainTag);
        try {
            DpSpider dpSpider = initSpider(domainTag, new JsonCrawlerConfigParser());
            if (dpSpider != null) {
                spiderCache.put(domainTag, dpSpider);
                initCrawlerTrigger(domainTag);
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    /**
     * 停止spider并且删除缓存
     *
     * @param domainTag
     */
    public static void destroySpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_DESTROY", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_STOP_EVENT", domainTag);
            spiderCache.get(domainTag).destroySpider();
            spiderCache.remove(domainTag);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }


    public static void stopSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_STOP", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_STOP_EVENT", domainTag);
            spiderCache.get(domainTag).stopSpider();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    public static void startSpider(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
        try {
            Cat.getProducer().logEvent("SPIDER_START_EVENT", domainTag);
            spiderCache.get(domainTag).startSpider();
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    public static void initCrawlerTrigger(String domainTag) {
        Transaction transaction = Cat.getProducer().newTransaction("SPIDER_TRIGGER", domainTag);
        try {
            CrawlerConfig config = new JsonCrawlerConfigParser().parser(domainTag);
            CrawlerTriggerController.registerTrigger(domainTag, config.getCrawlerTrigger());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    private static DpSpider initSpider(String domainTag, ICrawlerConfigParser configParser) {
        LOGGER.info("initSpider starting...,domainTag=[" + domainTag + "]");
        try {
            Assert.notNull(configParser, "configParser can not be null");
            CrawlerConfig config = configParser.parser(domainTag);
            Assert.notNull(config, "crawlerConfig parser error");
            Site site = config.getSite().parse();
            LOGGER.info("parser config success");
            // processor 定义公用执行器
            CommonPageProcessorImpl processor = new CommonPageProcessorImpl(config);
            Assert.notNull(processor, "processor init error");
            LOGGER.info("parser processor success");

            LOGGER.info("parser basePipeLine success");
            // scheduler 公用调度器
            DpRedisScheduler scheduler = new DpRedisScheduler(domainTag, config.getSchedulerConfig());
            Assert.notNull(scheduler, "scheduler init error");
            LOGGER.info("parser scheduler success");

            List<Pipeline> pipelines = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(config.getPipelines())) {
                for (String pipelineName : config.getPipelines()) {
                    Pipeline pipeline = CrawlerPipelineFactory.getPipeline(pipelineName);
                    pipelines.add(pipeline);
                }
            }

            // spider 爬虫初始化,顺序不能替换
            DpSpider dpSpider = DpSpider

                    .create(processor)

                    .domainTag(domainTag)

                    .thread(config.getCrawlerBaseInfo().getThreadCount())

                    .setRefreshSchedulerWhenRestart(config.getCrawlerBaseInfo().isRefreshRedisWhenStart())

                    .setScheduler(scheduler)

                    .setUUID(domainTag)

                    .setPipelines(pipelines)

                    .setExecutorService(ThreadPoolUtils.getExecutorService())

                    .startUrls(config.getCrawlerBaseInfo().getBaseUrls())

                    .setExitWhenComplete(config.getCrawlerBaseInfo().isExitWhenComplete());


            // spider下载器注册
            if (CollectionUtils.isNotEmpty(config.getDownloaders())) {
                for (DownloaderConfig downloaderConfig : config.getDownloaders()) {
                    dpSpider.addDownloaderPlugin(downloaderConfig.getUrlPattern(), CrawlerDownloaderFactory.getDownloader(downloaderConfig.getDownloader()));
                }
            } else {
                DownloaderConfig downloaderConfig = new DownloaderConfig();
                dpSpider.addDownloaderPlugin(downloaderConfig.getUrlPattern(), CrawlerDownloaderFactory.getDownloader(downloaderConfig.getDownloader()));
            }

            // spider监听器注册
            SpiderListener listener = new DpSpiderListener(scheduler, dpSpider);
            dpSpider = dpSpider.addSpiderListener(listener);

            LOGGER.info("init dpSpider all success");
            return dpSpider;
        } catch (Exception e) {
            LOGGER.error("initSpider error:" + e.getMessage(), e);
            LOGGER.info("initSpider error,domainTag=[" + domainTag + "]");
        }
        return null;
    }
}
