package com.dianping.ssp.crawler.common.pageprocessor.fieldhandler.converter;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Component
@Documented
public @interface CrawlerConverterTag {
    String name();
}
