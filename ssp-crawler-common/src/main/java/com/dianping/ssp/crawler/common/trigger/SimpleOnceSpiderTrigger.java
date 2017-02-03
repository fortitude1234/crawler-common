package com.dianping.ssp.crawler.common.trigger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.ssp.crawler.common.config.dto.CrawlerTrigger;
import com.dianping.ssp.crawler.common.spider.SpiderFactory;

/**
 * 在启动的时候只执行一次
 */
@CrawlerTriggerTag(name = "simple_once")
public class SimpleOnceSpiderTrigger implements ICrawlerSpiderTrigger {

    @Override
    public void registerTrigger(String domainTag, CrawlerTrigger crawlerTrigger) {
        if (null != crawlerTrigger) {
            Transaction transaction = Cat.getProducer().newTransaction("SPIDER_START", domainTag);
            try {
                Boolean runIfAfterInit = crawlerTrigger.getValue() == null ? null : (Boolean) crawlerTrigger.getValue();
                if (null != crawlerTrigger.getValue()) {
                    if (runIfAfterInit) {
                        Cat.getProducer().logEvent("SPIDER_START_EVENT", domainTag, Event.SUCCESS, "type=" + "simple_once");
                        SpiderFactory.startSpider(domainTag);
                    }
                }
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }
    }

}
