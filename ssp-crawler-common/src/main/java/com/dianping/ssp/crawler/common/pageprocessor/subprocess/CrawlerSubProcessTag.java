package com.dianping.ssp.crawler.common.pageprocessor.subprocess;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 针对过滤器的一个分类和排序的注解 
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Component
public @interface CrawlerSubProcessTag {

    String name();

}
