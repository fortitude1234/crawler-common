package com.dianping.ssp.crawler.common.log;


import com.dianping.ed.logger.LoggerConfig;
import com.dianping.ed.logger.LoggerLevel;

/**
 * Description
 * Created by yuxiang.cao on 16/5/17.
 */
public enum CrawlerCommonLogEnum {
	TODAY_CRAWLER_RESULT_ERROR("today_new_crawler_result", "mainflow", true, LoggerLevel.ERROR),
	DOWNLOADER_FACTORY_ERROR("downloader_factory", "mainflow", true, LoggerLevel.ERROR),
	SUB_PROCESSOR_FACTORY_ERROR("sub_processor_factory", "mainflow", true, LoggerLevel.ERROR),
	PIPELINE_FACTORY_ERROR("pipeline_factory", "mainflow", true, LoggerLevel.ERROR),
	CONVERTER_FACTORY_ERROR("converter_factory", "mainflow", true, LoggerLevel.ERROR),
	REDIS_REPOSITORY("redis_repository", "mainflow", false, LoggerLevel.INFO),

	//spider,
	SPIDER_ERROR("spider_error", "mainflow", true, LoggerLevel.ERROR),
	SPIDER_INFO("spider", "mainflow", false, LoggerLevel.INFO),
	SPIDER_FACTORY("spider_factory", "mainflow", false, LoggerLevel.INFO),

	//scheduler
	DP_REDIS_SCHEDULER("dp_redis_scheduler", "mainflow", false, LoggerLevel.INFO),


	//downloader
	HTML_DOWNLOADER("html_downloader", "downloader", false, LoggerLevel.INFO),
	S3_DOWNLOADER("s3_downloader", "downloader", false, LoggerLevel.INFO),
	MT_DOWNLOADER("mt_downloader", "downloader", false, LoggerLevel.INFO),
	SELENIUM_DOWNLOADER("selenium_downloader", "downloader", false, LoggerLevel.INFO),


	//pipeline,
	LOGGER_PIPELINE("logger_pipeline", "mainflow", false, LoggerLevel.INFO),

	//pageProcessor,
	FIELD_BUILDER_LOGGER("field_builder", "mainflow", false, LoggerLevel.INFO),
	URL_FILTER_LOGGER("url_filter_logger", "mainflow", false, LoggerLevel.INFO),
	RAW_TEXT_LOGGER("raw_text_logger", "mainflow", false, LoggerLevel.INFO),

	//plugins
	WEB_DRIVER_POOL("web_driver_pool", "mainflow", false, LoggerLevel.INFO),
	PROXY_ERROR("proxy_factory", "mainflow", true, LoggerLevel.ERROR),
	SPIDER_LISTENER("spider_listener", "mainflow", false, LoggerLevel.INFO),
	QUARTZ_TRIGGER("trigger", "quartz", false, LoggerLevel.INFO),

	USELESS("useless", "useless", false, LoggerLevel.INFO)

	;

	CrawlerCommonLogEnum(String name, String category, boolean isError, LoggerLevel level) {
		loggerConfig = new LoggerConfig();
		loggerConfig.setApp(APP_NAME);
		loggerConfig.setCategory(category);
		loggerConfig.setName(name);
		loggerConfig.setLevel(level);
		loggerConfig.setDaily(true);
		loggerConfig.setPerm(false);
		loggerConfig.setError(isError);
	}

	private static final String APP_NAME = "ssp-crawler-common";

	private LoggerConfig loggerConfig;

	public LoggerConfig getValue() {
		return loggerConfig;
	}
}
