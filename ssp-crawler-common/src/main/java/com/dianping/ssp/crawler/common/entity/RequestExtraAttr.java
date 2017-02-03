package com.dianping.ssp.crawler.common.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iClaod on 10/12/16.
 */
public class RequestExtraAttr {

    public static final String REQUEST_EXTRA_ATTR = "request_extra_attr";

    private Map<String, String> extra = new HashMap<String, String>();

    public String get(String name) {
        return extra.get(name);
    }

    public Map set(String name, String value) {
        extra.put(name, value);
        return extra;
    }
}
