package com.dianping.ssp.crawler.common.scheduler;

import com.dianping.combiz.spring.context.SpringLocator;
import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.config.dto.SchedulerConfig;
import com.dianping.ssp.crawler.common.contants.DuplicateType;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * 之后可以换成redis支持，目前自己实现3个本地缓存
 *
 */
public class DpRedisScheduler implements MonitorableScheduler, Scheduler {
     private static final EDLogger LOGGER = LoggerManager.getLogger(CrawlerCommonLogEnum.DP_REDIS_SCHEDULER.getValue());

	private RedisRepository redisRepository;

	private String domainTag;

	private String listPageUrlPattern = ".*";

	private boolean hasDetailPage = false;

	private boolean needCheckDuplicate = true;
	private boolean needCheckDetailPageDuplicate = true;

	private String detailDomainTag = null;

	public DpRedisScheduler(String domainTag, SchedulerConfig schedulerConfig) {
		this.domainTag = domainTag;
		if (schedulerConfig != null && schedulerConfig.hasDetailPage()) {
			this.hasDetailPage = true;
			this.listPageUrlPattern = schedulerConfig.getListPageUrlPattern();
			this.detailDomainTag = schedulerConfig.getDetailPageDomainTag();
			this.needCheckDuplicate = schedulerConfig.isNeedCheckDuplicate();
			this.needCheckDetailPageDuplicate = schedulerConfig.isNeedCheckDetailPageDuplicate();
		}
		redisRepository =  (RedisRepository) SpringLocator.getApplicationContext().getBean("redisRepository");
	}

	@Override
	public int getLeftRequestsCount(Task task) {
		return (int) redisRepository.getUrlQueueSize(domainTag);
	}

	public boolean isDuplicate(String domainTag, Request request) {
		Integer duplicate=(Integer)request.getExtra("duplicate");
		if (duplicate==null || duplicate==DuplicateType.NO_DUPLICATE){
			return !redisRepository.addToUrlSet(domainTag, request.getUrl());
		}else{
			return false;
		}
	}

	public void resetDuplicateCheck(Task task) {
		redisRepository.refreshAllUrlSet(domainTag);
	}

	@Override
	public int getTotalRequestsCount(Task task) {
		return (int) redisRepository.getAllUrlSetSize(domainTag);
	}

	private void addIfNotDuplicate(String domainTag, Request request, boolean checkDuplicate) {
		if (!checkDuplicate || request.getExtra(Request.CYCLE_TRIED_TIMES) != null || !isDuplicate(domainTag, request)) {
			boolean addSuccess = redisRepository.addToUrlQueue(domainTag, request);
			if (!addSuccess) {
				LOGGER.error("error to add url to queue, domainTag: " + domainTag + ", url: " + request.getUrl());
			} else {
				LOGGER.info("success to add url to queue, domainTag: " + domainTag + ", url: " + request.getUrl());
			}
		}
	}

	@Override
	public void push(Request request, Task task) {
		if (!hasDetailPage) {
			addIfNotDuplicate(domainTag, request, needCheckDuplicate);
		} else {
			if (isListUrl(request.getUrl())) {
				addIfNotDuplicate(domainTag, request, needCheckDuplicate);
			} else {
				addIfNotDuplicate(detailDomainTag, request, needCheckDetailPageDuplicate);
			}
		}
	}

	@Override
	public Request poll(Task task) {
		try {
			return redisRepository.getUrlFromQueue(domainTag);
		} catch (Exception e) {
			//需要监控,并可能需要修复数据!! cause:对象从队列中取出,但因为网络超时导致客户端没有取到
			LOGGER.error("exception to get url from queue, domainTag: " + domainTag, e);
		}
		return null;
	}

	private boolean isListUrl(String url) {
		return url.matches(listPageUrlPattern);
	}

	public String getDetailDomainTag() {
		if (!hasDetailPage) {
			return null;
		}
		if (StringUtils.isEmpty(detailDomainTag)) {
			return domainTag + "_detail";
		}
		return detailDomainTag;
	}

}
