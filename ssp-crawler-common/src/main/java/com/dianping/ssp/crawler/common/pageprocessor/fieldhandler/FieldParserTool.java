package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler;

import com.dianping.ssp.crawler.common.config.dto.FieldParserConfig;
import com.dianping.ssp.crawler.common.contants.CrawlerCommonConstants;
import com.dianping.ssp.crawler.common.entity.ProcessorContext;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ConverterFactory;
import com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter.ICrawlerConverter;
import com.dianping.ssp.crawler.common.util.ContextUtil;
import com.dianping.ssp.crawler.common.util.JavaScriptExecutorUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.HtmlNode;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iClaod on 10/12/16.
 */
public class FieldParserTool {



    public static Object parse(HtmlNode html, FieldParserConfig fieldParserConfig, ProcessorContext context) {
        Object result = null;
        Selectable selectable = null;
        if (StringUtils.isNotBlank(fieldParserConfig.getCssSelector())) {
            if (StringUtils.isBlank(fieldParserConfig.getCssSelectorAttrName())) {
                selectable = html.$(fieldParserConfig.getCssSelector());
            } else {
                selectable = html.$(fieldParserConfig.getCssSelector(), fieldParserConfig.getCssSelectorAttrName());
            }
        }
        if (StringUtils.isNotBlank(fieldParserConfig.getXpathSelector())) {
            selectable = html.xpath(fieldParserConfig.getXpathSelector());
        }
//        if (StringUtils.isNotBlank(fieldParserConfig.getXpath2Selector())) {
//            Xpath2Selector xpath2Selector = new Xpath2Selector(fieldParserConfig.getXpath2Selector());
//            selectable = html.selectList(xpath2Selector);
//        }
//        if (StringUtils.isNotBlank(fieldParserConfig.getRegex())) {
//            selectable = selectable.regex(fieldParserConfig.getRegex());
//        }
        return postOperation(selectable, fieldParserConfig, context);
    }

    public static Map<String, Object> parseChildren(FieldParserConfig fieldParserConfig, Selectable selectable, ProcessorContext context) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (fieldParserConfig.isList()) {
            List<Object> childrenList = Lists.newArrayList();
            for (Selectable child : selectable.nodes()) {
                Map<String, Object> childResultMap = Maps.newHashMap();
                boolean isTargetUrl = false;
                for (FieldParserConfig childFieldParser: fieldParserConfig.getChildrenFieldParsers()) {
                    Object childrenResult = null;
                    if (child instanceof HtmlNode) {
                        childrenResult = parse((HtmlNode) child, childFieldParser, context);
                    } else {
                        childrenResult = parse(new Json(child.get()), childFieldParser, context);
                    }
                    childResultMap.put(childFieldParser.getFieldName(), childrenResult);
                    if (childFieldParser.getFieldName().matches(CrawlerCommonConstants.URL_FILED_NAME_PATTERN)) {
                        isTargetUrl = true;
                    }
                }
                childrenList.add(childResultMap);
                if (isTargetUrl) {
                    ContextUtil.addMapAsTargetUrlToContext(childResultMap, context);
                }
            }
            ((Map)result).put(fieldParserConfig.getFieldName(), childrenList);
        } else {
            for (FieldParserConfig childFieldParser: fieldParserConfig.getChildrenFieldParsers()) {
                Object childrenResult = null;
                if (selectable instanceof HtmlNode) {
                    childrenResult = parse((HtmlNode) selectable.nodes().get(0), childFieldParser, context);
                } else {
                    childrenResult = parse(new Json(selectable.get()), childFieldParser, context);
                }
                ((Map) result).put(fieldParserConfig.getFieldName(), childrenResult);
            }
        }
        return result;
    }

    public static Object parse(Json json, FieldParserConfig fieldParserConfig, ProcessorContext context) {
        Object result = null;
        Selectable selectable = null;
        if (StringUtils.isNotBlank(fieldParserConfig.getJsonSelector())) {
            selectable = json.jsonPath(fieldParserConfig.getJsonSelector());
        }
        return postOperation(selectable, fieldParserConfig, context);
    }

    private static Object postOperation(Selectable selectable, FieldParserConfig fieldParserConfig, ProcessorContext context) {
        Object result = null;
        if (null != selectable) {
            if (fieldParserConfig.hasChildrenFiledParsers()) {
                return parseChildren(fieldParserConfig, selectable, context);
            } else {
                if (fieldParserConfig.isList()) {
                    result = selectable.all();
                } else {
                    result = selectable.get();
                }
            }
        }
        if (StringUtils.isNotBlank(fieldParserConfig.getScript())) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_DATA, result);
            params.put(CrawlerCommonConstants.JavaScriptConstant.PARAM_METHODNAME, CrawlerCommonConstants.JavaScriptConstant.DEFAULT_METHODNAME);
            Object targetData = JavaScriptExecutorUtils.eval(fieldParserConfig.getScript(), params);
            result = targetData;
        }

        if(CollectionUtils.isNotEmpty(fieldParserConfig.getConverterList())){
            Object converterParam = fieldParserConfig.getConverterParam();
            for (String convertName : fieldParserConfig.getConverterList()) {
                ICrawlerConverter converter = ConverterFactory.getConverter(convertName);
                if (null == converter) continue;
                result = converter.converter(result, converterParam);
            }
        }else if (StringUtils.isNotBlank(fieldParserConfig.getConverter())) {
            Object converterParam = fieldParserConfig.getConverterParam();
            ICrawlerConverter converter = ConverterFactory.getConverter(fieldParserConfig.getConverter());
            result = converter.converter(result, converterParam);
        }
        //设置默认值
        if (StringUtils.isNotBlank(fieldParserConfig.getDefaultValue())) {
            if (null == result) {
                result = fieldParserConfig.getDefaultValue();
            }
            if (result instanceof String) {
                if (StringUtils.isBlank((String) result)) {
                    result = fieldParserConfig.getDefaultValue();
                }
            }
        }
        return result;
    }

}
