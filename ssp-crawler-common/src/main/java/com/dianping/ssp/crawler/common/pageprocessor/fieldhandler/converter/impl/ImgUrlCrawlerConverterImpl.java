package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.impl;

import java.util.Map;

import com.dianping.avatar.log.AvatarLogger;
import com.dianping.avatar.log.AvatarLoggerFactory;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.CrawlerConverterTag;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import com.dianping.ssp.crawler.common.util.ImgUploadUtil;

/**
 * 针对图片的一个转换器
 */
@CrawlerConverterTag(name = "imgConverter")
public class ImgUrlCrawlerConverterImpl implements ICrawlerConverter {
    private static final AvatarLogger LOGGER = AvatarLoggerFactory.getLogger(ImgUrlCrawlerConverterImpl.class);

    @Override
    public Object converter(Object sourceData, Object params) {
        if (null != sourceData) {
            String imgUrl = (String) sourceData;
            boolean isThumbnail=false;
            if (params!=null && params instanceof Map){
            	Map<String,Object> paraMap=(Map)params;
            	isThumbnail=(Boolean)paraMap.get("isThumbnail");
            }
            if (isLegalUrl(imgUrl)) {
                return ImgUploadUtil.getLocalPath(imgUrl,isThumbnail);
            }
        }
        return null;
    }


    private boolean isLegalUrl(String url) {
        return url.matches("http://([^']+(?:jpg|gif|png|bmp|jpeg))(\\S+)?");
    }


}
