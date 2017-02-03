package com.dianping.ssp.crawler.common.pageprocessor;

import com.dianping.ssp.crawler.common.config.dto.PageProcessorConfig;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.ICrawlerFieldHandler;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.ICrawlerSubProcess;
import com.dianping.ssp.crawler.common.pageprocessor.urlhandler.ICrawlerUrlHandler;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.impl.BaseCrawlerFieldHandlerImpl;
import com.dianping.ssp.crawler.common.pageprocessor.urlhandler.impl.BaseCrawlerUrlHandler;
import com.dianping.ssp.crawler.common.config.dto.CrawlerConfig;
import com.dianping.ssp.crawler.common.entity.ProStatus;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.dianping.ssp.crawler.common.pageprocessor.subprocess.CrawlerSubProcessFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * 一个公用的page Processor
 */
public class CommonPageProcessorImpl implements PageProcessor {

    private PageProcessorConfig pageProcessorConfig;
    private Site site;
    private String domainTag;
    /**
     * 刚处理时的过滤
     */
    private List<ICrawlerSubProcess> preCrawlerSubProcesses;
    /**
     * url处理和field处理之间的过滤
     */
    private List<ICrawlerSubProcess> middleCrawlerSubProcesses;
    /**
     * 处理完成后的过滤
     */
    private List<ICrawlerSubProcess> afterCrawlerSubProcesses;

    private ICrawlerFieldHandler fieldHandler = new BaseCrawlerFieldHandlerImpl();

    private ICrawlerUrlHandler urlHandler = new BaseCrawlerUrlHandler();

    public CommonPageProcessorImpl(CrawlerConfig config) {
        Assert.notNull(config, "config is null");
        this.domainTag = config.getDomainTag();
        this.pageProcessorConfig = config.getPageProcessorConfig();
        this.site = config.getSite().parse();
        setPreCrawlerSubProcesses(initSubProcess(this.pageProcessorConfig.getPreSubProcessor()));
        setMiddleCrawlerSubProcesses(initSubProcess(this.pageProcessorConfig.getMidSubProcessor()));
        setAfterCrawlerSubProcesses(initSubProcess(this.pageProcessorConfig.getAfterSubProcessor()));
    }



    private List<ICrawlerSubProcess> initSubProcess(List<String> subProcessNames) {
        List<ICrawlerSubProcess> subProcesses = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(subProcessNames)) {
            for (String subProcessName : subProcessNames) {
                subProcesses.add(CrawlerSubProcessFactory.getSubProcess(subProcessName));
            }
        }
        return subProcesses;
    }

    public List<ICrawlerSubProcess> getPreCrawlerSubProcesses() {
        return preCrawlerSubProcesses;
    }

    public void setPreCrawlerSubProcesses(List<ICrawlerSubProcess> preCrawlerSubProcesses) {
        this.preCrawlerSubProcesses = preCrawlerSubProcesses;
    }

    public List<ICrawlerSubProcess> getMiddleCrawlerSubProcesses() {
        return middleCrawlerSubProcesses;
    }

    public void setMiddleCrawlerSubProcesses(List<ICrawlerSubProcess> middleCrawlerSubProcesses) {
        this.middleCrawlerSubProcesses = middleCrawlerSubProcesses;
    }

    public List<ICrawlerSubProcess> getAfterCrawlerSubProcesses() {
        return afterCrawlerSubProcesses;
    }

    public void setAfterCrawlerSubProcesses(List<ICrawlerSubProcess> afterCrawlerSubProcesses) {
        this.afterCrawlerSubProcesses = afterCrawlerSubProcesses;
    }

    public ICrawlerFieldHandler getFieldHandler() {
        return fieldHandler;
    }

    public void setFieldHandler(ICrawlerFieldHandler fieldHandler) {
        this.fieldHandler = fieldHandler;
    }

    public ICrawlerUrlHandler getUrlHandler() {
        return urlHandler;
    }

    public void setUrlHandler(ICrawlerUrlHandler urlHandler) {
        this.urlHandler = urlHandler;
    }


    @Override
    public void process(Page page) {
        ProcessorContext.getContext(page)
        .addParam(CrawlerCommonConstants.ProcessorContextConstant.CURRENT_URL, page.getRequest().getUrl())
        .addParam(CrawlerCommonConstants.ProcessorContextConstant.DOMAIN_TAG, this.domainTag);

        ProStatus proStatus = ProStatus.success();


        if (proStatus.isSuccess()) {
            proStatus = doSubProcess(CrawlerCommonConstants.SubProcessType.PRE_SUB_PROCESS, page);
        }
        if (proStatus.isSuccess()) {
            // 页面field提取
            proStatus = fieldHandler.parseField(page, pageProcessorConfig.getPageParserConfigs());
        }
        if (proStatus.isSuccess()) {
            // 页前过滤
            proStatus = doSubProcess(CrawlerCommonConstants.SubProcessType.MID_SUB_PROCESS, page);
        }
        // 进行地址过滤,生成我们需要的url
        if (proStatus.isSuccess()) {
            // 地址处理
            proStatus = urlHandler.handler(page, pageProcessorConfig.getUrlFilterConfigs());
        }
        if (proStatus.isSuccess()) {
            // 处理后的过滤
            proStatus = doSubProcess(CrawlerCommonConstants.SubProcessType.AFTER_SUB_PROCESS, page);
        }

        ProcessorContext.getContext(page).setProStatus(proStatus);

    }

    private ProStatus doSubProcess(int subProcessType, Page page) {
        List<ICrawlerSubProcess> subProcessorList = null;
        ProStatus proStatus = null;
        if (subProcessType == CrawlerCommonConstants.SubProcessType.PRE_SUB_PROCESS) {
            subProcessorList = preCrawlerSubProcesses;
        } else if (subProcessType == CrawlerCommonConstants.SubProcessType.MID_SUB_PROCESS) {
            subProcessorList = middleCrawlerSubProcesses;
        } else if (subProcessType == CrawlerCommonConstants.SubProcessType.AFTER_SUB_PROCESS) {
            subProcessorList = afterCrawlerSubProcesses;
        }
        if (CollectionUtils.isNotEmpty(subProcessorList)) {
            for (ICrawlerSubProcess subProcess : subProcessorList) {
                proStatus = subProcess.process(page);
                if (!proStatus.isSuccess()) {
                    break;
                }
            }
        } else {
            proStatus = ProStatus.success();
        }
        return proStatus;
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
