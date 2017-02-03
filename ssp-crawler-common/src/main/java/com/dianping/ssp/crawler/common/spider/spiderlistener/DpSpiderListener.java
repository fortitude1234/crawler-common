package com.dianping.ssp.crawler.common.spider.spiderlistener;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.entity.RequestExtraAttr;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.spider.DpSpider;
import com.dianping.ssp.crawler.common.util.JsonUtil;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * 针对起始错误进行容错处理
 */
public class DpSpiderListener implements SpiderListener {

    private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.SPIDER_LISTENER.getValue());

    private Scheduler scheduler;

    private DpSpider spider;

    public DpSpiderListener(Scheduler scheduler, DpSpider spider) {
        super();
        this.scheduler = scheduler;
        this.spider = spider;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public DpSpider getSpider() {
        return spider;
    }

    public void setSpider(DpSpider spider) {
        this.spider = spider;
    }

    @Override
    public void onSuccess(Request request) {
        // TODO Auto-generated method stub

    }

    // 处理失败后，需要将请求重新塞回请求链路中
    @Override
    public void onError(Request request) {
        ProStatus proStatus = (ProStatus)request.getExtra("proStatus");
        if (proStatus != null) {
            LOGGER.info(String.format("process request error, code: %d, proMessage: %s, url: %s.", proStatus.getMessageCode(), proStatus.getMessage(), request.getUrl()));
        } else {
            LOGGER.info("process request error");
        }
        Integer retryTimes = (Integer) request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (retryTimes == null) {
            request.putExtra(Request.CYCLE_TRIED_TIMES, 1);
            scheduler.push(request, spider);
        } else {
            if (retryTimes < 10) {
                retryTimes++;
                request.putExtra(Request.CYCLE_TRIED_TIMES, retryTimes);
                scheduler.push(request, spider);
            } else {
                LOGGER.error("drop request after retry for 10 times, request: " + JsonUtil.toJson(request));
            }
        }

    }
}
