package com.dianping.ssp.crawler.common.downloader.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicNameValuePair;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.utils.HttpConstant;

import com.dianping.ssp.crawler.common.downloader.CrawlerDownloaderTag;
import com.google.common.collect.Lists;

/**
 * Created by iClaod on 10/19/16.
 */
@CrawlerDownloaderTag(name = "httpClientDownloader")
public class HttpClientDownloaderImpl extends HttpClientDownloader{

    protected RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            String cookieFromRequest = (String) request.getExtra("Cookie");
            RequestBuilder requestBuilder = RequestBuilder.get();
            if (StringUtils.isNotEmpty(cookieFromRequest)) {
                requestBuilder.addHeader("Cookie", cookieFromRequest);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            Map<String,String> params=(Map)request.getExtra("params");
            if (!MapUtils.isEmpty(params)){
            	for (Entry<String,String> kv:params.entrySet()){
            		String key=kv.getKey();
            		if (key!=null){
            			NameValuePair nvp=new BasicNameValuePair(key,kv.getValue());
            			requestBuilder.addParameter(nvp);
            		}
            	}
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

}
