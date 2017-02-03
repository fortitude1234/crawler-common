package com.dianping.ssp.crawler.common.pageprocessor.subprocess;

import com.dianping.ssp.crawler.common.entity.ProStatus;
import us.codecraft.webmagic.Page;

/**
 * 过滤器，对于爬取的内容进行过滤,相当于一个拦截器的功能
 */
public interface ICrawlerSubProcess {
    /**
     * 针对页面分析的一些情况进行过滤
     * 
     * @param url
     * @return
     */
    ProStatus process(Page page);
}
