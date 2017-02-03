package com.dianping.ssp.crawler.common.spider;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.pigeon.util.CollectionUtils;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.enums.ProMessageCode;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.scheduler.DpRedisScheduler;
import org.apache.commons.collections.MapUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 对spider的重写，添加一些新功能
 */
public class DpSpider extends Spider {
    private static final EDLogger LOGGER_ERROR = LoggerManager.getLogger(CrawlerCommonLogEnum.SPIDER_ERROR.getValue());
    private static final EDLogger LOGGER_INFO = LoggerManager.getLogger(CrawlerCommonLogEnum.SPIDER_INFO.getValue());
    protected String domainTag;

    protected String baseUrl;

    protected boolean refreshSchedulerWhenRestart = false;

    /**
     * 用以存储针对特定的URL正则去匹配特定的downloader
     */
    protected Map<String, Downloader> downloaderPlugin = new ConcurrentHashMap<String, Downloader>();

    public String getDomainTag() {
        return domainTag;
    }

    public void setDomainTag(String domainTag) {
        this.domainTag = domainTag;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, Downloader> getDownloaderPlugin() {
        return downloaderPlugin;
    }

    private final AtomicLong pageCount = new AtomicLong(0);

    public void setDownloaderPlugin(Map<String, Downloader> downloaderPlugin) {
        this.downloaderPlugin = downloaderPlugin;
    }

    public DpSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
        // TODO Auto-generated constructor stub
    }

    /**
     * create a spider with pageProcessor.
     * 
     * @param pageProcessor
     * @return new spider
     * @see PageProcessor
     */
    public static DpSpider create(PageProcessor pageProcessor) {
        return new DpSpider(pageProcessor);
    }

    public DpSpider destroyWhenExit(Boolean status) {
        this.destroyWhenExit = status;
        return this;
    }

    public DpSpider domainTag(String domainTag) {
        this.domainTag = domainTag;
        return this;
    }

    /**
     * 注册一个baseurl
     * 
     * @param baseUrl
     * @return
     */
    public DpSpider registerBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this.addUrl(baseUrl);
    }

    /**
     * Set startUrls of DpSpider.<br>
     * Prior to startUrls of Site.
     * 
     * @param startUrls
     * @return this
     */
    public DpSpider startUrls(List<String> startUrls) {
        super.startUrls(startUrls);
        return this;
    }

    /**
     * Set startUrls of DpSpider.<br>
     * Prior to startUrls of Site.
     * 
     * @param startRequests
     * @return this
     */
    public DpSpider startRequest(List<Request> startRequests) {
        super.startRequest(startRequests);
        return this;
    }

    /**
     * Set an uuid for spider.<br>
     * Default uuid is domain of site.<br>
     * 
     * @param uuid
     * @return this
     */
    public DpSpider setUUID(String uuid) {
        super.setUUID(uuid);
        return this;
    }

    /**
     * set scheduler for DpSpider
     * 
     * @param scheduler
     * @return this
     * @Deprecated
     * @see #setScheduler(Scheduler)
     */
    public DpSpider scheduler(DpRedisScheduler scheduler) {
        return setScheduler(scheduler);
    }

    /**
     * set scheduler for DpSpider
     *
     * @param scheduler
     * @return this
     * @see Scheduler
     * @since 0.2.1
     */
    public DpSpider setScheduler(DpRedisScheduler scheduler) {
        super.setScheduler(scheduler);
        return this;
    }

    /**
     * add a pipeline for DpSpider
     *
     * @param pipeline
     * @return this
     * @see #addPipeline(Pipeline)
     * @deprecated
     */
    public DpSpider pipeline(Pipeline pipeline) {
        return addPipeline(pipeline);
    }

    /**
     * add a pipeline for DpSpider
     *
     * @param pipeline
     * @return this
     * @see Pipeline
     * @since 0.2.1
     */
    public DpSpider addPipeline(Pipeline pipeline) {
        super.addPipeline(pipeline);
        return this;
    }

    /**
     * set pipelines for DpSpider
     *
     * @param pipelines
     * @return this
     * @see Pipeline
     * @since 0.4.1
     */
    public DpSpider setPipelines(List<Pipeline> pipelines) {
        super.setPipelines(pipelines);
        return this;
    }

    /**
     * clear the pipelines set
     *
     * @return this
     */
    public DpSpider clearPipeline() {
        super.clearPipeline();
        return this;
    }

    public DpSpider startUrls(String... startUrls) {
        checkIfRunning();
        this.startRequests = UrlUtils.convertToRequests(Arrays.asList(startUrls));
        return this;
    }

    /**
     * set the downloader of spider
     *
     * @param downloader
     * @return this
     * @see #setDownloader(Downloader)
     * @deprecated
     */
    public DpSpider downloader(Downloader downloader) {
        return setDownloader(downloader);
    }

    public DpSpider addDownloaderPlugin(String urlRegex, Downloader downloader) {
        downloaderPlugin.put(urlRegex, downloader);
        return this;
    }

    /**
     * set the downloader of spider
     * 
     * @param downloader
     * @return this
     * @see Downloader
     */
    public DpSpider setDownloader(Downloader downloader) {
        super.setDownloader(downloader);
        return this;
    }

    /**
     * Add urls to crawl. <br/>
     * 
     * @param urls
     * @return
     */
    public DpSpider addUrl(String... urls) {
        for (String url : urls) {
            addRequest(new Request(url));
        }
        return this;
    }

    /**
     * Add urls with information to crawl.<br/>
     * 
     * @param requests
     * @return
     */
    public DpSpider addRequest(Request... requests) {
        for (Request request : requests) {
            addRequest(request);
        }
        return this;
    }

    private void addRequest(Request request) {
//        if (site.getDomain() == null && request != null && request.getUrl() != null) {
//            site.setDomain(UrlUtils.getDomain(request.getUrl()));
//        }
        scheduler.push(request, this);
    }

    /**
     * start with more than one threads
     * 
     * @param threadNum
     * @return this
     */
    public DpSpider thread(int threadNum) {
        super.thread(threadNum);
        return this;
    }

    /**
     * start with more than one threads
     * 
     * @param threadNum
     * @return this
     */
    public DpSpider thread(ExecutorService executorService, int threadNum) {
        super.thread(executorService, threadNum);
        return this;
    }

    /**
     * Exit when complete. <br/>
     * True: exit when all url of the site is downloaded. <br/>
     * False: not exit until call stop() manually.<br/>
     * 
     * @param exitWhenComplete
     * @return
     */
    public DpSpider setExitWhenComplete(boolean exitWhenComplete) {
        super.setExitWhenComplete(exitWhenComplete);
        return this;
    }

    /**
     * Whether add urls extracted to download.<br>
     * Add urls to download when it is true, and just download seed urls when it is false. <br>
     * DO NOT set it unless you know what it means!
     * 
     * @param spawnUrl
     * @return
     * @since 0.4.0
     */
    public DpSpider setSpawnUrl(boolean spawnUrl) {
        super.setSpawnUrl(spawnUrl);
        return this;
    }

    public DpSpider setExecutorService(ExecutorService executorService) {
        super.setExecutorService(executorService);
        return this;
    }

    public DpSpider setSpiderListeners(List<SpiderListener> spiderListeners) {
        super.setSpiderListeners(spiderListeners);
        return this;
    }

    public DpSpider addSpiderListener(SpiderListener... spiderListeners) {
        List<SpiderListener> listenerList = null;
        if (CollectionUtils.isEmpty(super.getSpiderListeners())) {
            listenerList = new ArrayList<SpiderListener>();
        } else {
            listenerList = super.getSpiderListeners();
        }
        for (SpiderListener listener : spiderListeners) {
            if (!listenerList.contains(listener)) {
                listenerList.add(listener);
            }
        }
        super.setSpiderListeners(listenerList);
        return this;
    }

    public void startSpider() {
        this.start();
    }

    public void stopSpider() {
        if (spiderIsRun()) {
            this.stop();
            LOGGER_INFO.info("spider is stopped,domainTag:" + domainTag);
        }
    }

    public void destroySpider() {
        stopSpider();
        this.close();
        LOGGER_INFO.info("spider is destroy,domainTag:" + domainTag);
    }

    /**
     * 判断spider是否在运行
     * 
     * @return
     */
    public boolean spiderIsRun() {
        return stat.get() == STAT_RUNNING;
    }

    @Override
    public void run() {
        checkRunningStat();
        initComponent();
        LOGGER_INFO.info("Spider " + getUUID() + " started!");
        while (!Thread.currentThread().isInterrupted() && stat.get() == STAT_RUNNING) {
            Request request = scheduler.poll(this);
            if (request == null) {
                //LOGGER_INFO.info("no request found for domainTag: " + domainTag);
                if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                    LOGGER_INFO.info("Spider: " + getUUID()  + "exit");
                    break;
                }
                // wait until new url added
                waitNewUrl();
            } else {
                final Request requestFinal = request;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processRequest(requestFinal);
                            onSuccess(requestFinal);
                        } catch (Throwable e) {
                            onError(requestFinal);
                            LOGGER_ERROR.error("process request " + requestFinal + " error", e);
                        } finally {
                            pageCount.incrementAndGet();
                        }
                    }
                });
            }
        }
        stat.set(STAT_STOPPED);
        // release some resources
        if (destroyWhenExit) {
            close();
        }
    }

    // 方法重写
    protected void processRequest(Request request) {
        // 添加下载器的正则获取    	
        Downloader realDownloader = downloader;
        if (MapUtils.isNotEmpty(downloaderPlugin)) {
            for (String urlRegex : downloaderPlugin.keySet()) {
                if (request.getUrl().matches(urlRegex)) {
                    realDownloader = downloaderPlugin.get(urlRegex);
                    break;
                }
            }
        }
        LOGGER_INFO.info("now downloading: " + request.getUrl());
        Page page = realDownloader.download(request, this);
        LOGGER_INFO.info("success downloading: " + request.getUrl());
        if (page == null) {
            sleep(site.getSleepTime());
            // 如果为空，则默认是成功状态
            request.putExtra("proStatus", ProStatus.fail(ProMessageCode.DOWNLOAD_ERROR.getCode()));
            onError(request);
            return;
        }
        // for cycle retry
        if (page.isNeedCycleRetry()) {
            extractAndAddRequests(page, true);
            sleep(site.getSleepTime());
            return;
        }
        LOGGER_INFO.info("now processing: " + request.getUrl());
        pageProcessor.process(page);
        LOGGER_INFO.info("success processing: " + request.getUrl());
        extractAndAddRequests(page, spawnUrl);
        if (!page.getResultItems().isSkip()) {
            for (Pipeline pipeline : pipelines) {
                pipeline.process(page.getResultItems(), this);
            }
        }
        sleep(site.getSleepTime());
    }

    private void checkRunningStat() {
        while (true) {
            int statNow = stat.get();
            if (statNow == STAT_RUNNING) {
                throw new IllegalStateException("Spider is already running!");
            }
            if (stat.compareAndSet(statNow, STAT_RUNNING)) {
                break;
            }
        }
    }

    private void waitNewUrl() {
        try {
            //double check
            if (threadPool.getThreadAlive() == 0 && exitWhenComplete) {
                return;
            }
            Thread.sleep(10000);
        } catch (Exception e) {
            LOGGER_ERROR.error("waitNewUrl - interrupted, error {}", e);
        }
    }

    public boolean isRefreshSchedulerWhenRestart() {
        return refreshSchedulerWhenRestart;
    }

    public DpSpider setRefreshSchedulerWhenRestart(boolean refreshSchedulerWhenRestart) {
        this.refreshSchedulerWhenRestart = refreshSchedulerWhenRestart;
        return this;
    }
}
