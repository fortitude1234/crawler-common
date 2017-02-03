package com.dianping.ssp.crawler.common.downloader.impl;

import com.dianping.ed.logger.EDLogger;
import com.dianping.ed.logger.LoggerManager;
import com.dianping.ssp.crawler.common.log.CrawlerCommonLogEnum;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为了减少WebDriver创建和销毁的开销, 引入WebDriverPool来统一管理资源
 * note: for ssp, only support phantomjs now. 2016-09-30
 */
public class SeleniumWebDriverPool {
	private static final EDLogger logger = LoggerManager.getLogger(CrawlerCommonLogEnum.WEB_DRIVER_POOL.getValue());

	private final static int DEFAULT_CAPACITY = 5;

	private final int capacity;

	private final static int STAT_RUNNING = 1;

	private final static int STAT_CLODED = 2;

	private AtomicInteger stat = new AtomicInteger(STAT_RUNNING);

	/*
	 * new fields for configuring phantomJS
	 */
	private WebDriver mDriver = null;
	private boolean mAutoQuitDriver = true;

	private static final String CONFIG_FILE = "classpath*:config/webDriver.ini";
	private static final String PHANTOMJS_EXEC_PATH_4MACOS = "classpath*:PhantomJS/phantomjs-2.1.1-macosx/bin/phantomjs";
	private static final String PHANTOMJS_EXEC_PATH_4LINUX = "classpath*:PhantomJS/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";
	private static final String DRIVER_FIREFOX = "firefox";
	private static final String DRIVER_CHROME = "chrome";
	private static final String DRIVER_PHANTOMJS = "phantomjs";

	protected static Properties sConfig;
	protected static DesiredCapabilities sCaps;

	/**
	 * Configure the GhostDriver, and initialize a WebDriver instance. This part
	 * of code comes from GhostDriver.
	 * https://github.com/detro/ghostdriver/tree/master/test/java/src/test/java/ghostdriver
	 * 
	 * @author bob.li.0718@gmail.com
	 * @throws IOException
	 */
	public void configure() throws IOException {
		// Read config file
		sConfig = new Properties();
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources(CONFIG_FILE);
		sConfig.load(resources[0].getInputStream());

		// Prepare capabilities
		sCaps = new DesiredCapabilities();
		sCaps.setJavascriptEnabled(true);
		sCaps.setCapability("takesScreenshot", false);

		String driver = sConfig.getProperty("driver", DRIVER_PHANTOMJS);

		// Fetch PhantomJS-specific configuration parameters
		if (driver.equals(DRIVER_PHANTOMJS)) {
			// "phantomjs_exec_path"
			String os = System.getProperty("os.name");
			Resource[] phantomExecResources = null;
			if (os.startsWith("Mac")) {
				logger.info("current system is: " + os + ", now loading: " + PHANTOMJS_EXEC_PATH_4MACOS);
				phantomExecResources = patternResolver.getResources(PHANTOMJS_EXEC_PATH_4MACOS);
			} else {
				logger.info("current system is: " + os + ", now loading: " + PHANTOMJS_EXEC_PATH_4LINUX);
				phantomExecResources = patternResolver.getResources(PHANTOMJS_EXEC_PATH_4LINUX);
			}
			if (phantomExecResources != null && phantomExecResources.length > 0) {
				sCaps.setCapability(
						PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
						phantomExecResources[0].getFile().getAbsolutePath());
			} else {
				throw new IOException(
						String.format(
								"Property '%s' not set!",
								PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY));
			}
			// "phantomjs_driver_path"
//			Resource[] phantomDriverResources = patternResolver.getResources("classpath:PhantomJS/crawler.js");
//			if (phantomDriverResources != null && phantomDriverResources.length > 0) {
//				System.out.println("Test will use an external GhostDriver");
//				sCaps.setCapability(
//						PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_PATH_PROPERTY,
//						phantomDriverResources[0].getFile().getAbsolutePath());
//			} else {
//				System.out
//						.println("Test will use PhantomJS internal GhostDriver");
//			}
		}

		// Disable "web-security", enable all possible "ssl-protocols" and
		// "ignore-ssl-errors" for PhantomJSDriver
		// sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new
		// String[] {
		// "--web-security=false",
		// "--ssl-protocol=any",
		// "--ignore-ssl-errors=true"
		// });

		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--web-security=false");
		cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--ignore-ssl-errors=true");
		sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
				cliArgsCap);

		// Control LogLevel for GhostDriver, via CLI arguments
		sCaps.setCapability(
				PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS,
				new String[] { "--logLevel="
						+ (sConfig.getProperty("phantomjs_driver_loglevel") != null ? sConfig
								.getProperty("phantomjs_driver_loglevel")
								: "INFO") });


		// Start appropriate Driver
		if (isUrl(driver)) {
			sCaps.setBrowserName("phantomjs");
			mDriver = new RemoteWebDriver(new URL(driver), sCaps);
		} else if (driver.equals(DRIVER_FIREFOX)) {
			mDriver = new FirefoxDriver(sCaps);
		} else if (driver.equals(DRIVER_CHROME)) {
			mDriver = new ChromeDriver(sCaps);
		} else if (driver.equals(DRIVER_PHANTOMJS)) {
			mDriver = new PhantomJSDriver(sCaps);
		}
	}

	/**
	 * check whether input is a valid URL
	 * 
	 * @author bob.li.0718@gmail.com
	 * @param urlString urlString
	 * @return true means yes, otherwise no.
	 */
	private boolean isUrl(String urlString) {
		try {
			new URL(urlString);
			return true;
		} catch (MalformedURLException mue) {
			logger.error("error", mue);
			return false;
		}
	}

	/**
	 * store webDrivers created
	 */
	private List<WebDriver> webDriverList = Collections
			.synchronizedList(new ArrayList<WebDriver>());

	/**
	 * store webDrivers available
	 */
	private BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>();

	public SeleniumWebDriverPool(int capacity) {
		this.capacity = capacity;
	}

	public SeleniumWebDriverPool() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public WebDriver get() throws InterruptedException {
		checkRunning();
		WebDriver poll = innerQueue.poll();
		if (poll != null) {
			return poll;
		}
		if (webDriverList.size() < capacity) {
			synchronized (webDriverList) {
				if (webDriverList.size() < capacity) {

					// add new WebDriver instance into pool
					try {
						configure();
						innerQueue.add(mDriver);
						webDriverList.add(mDriver);
					} catch (IOException e) {
						logger.error("error", e);
					}

					// ChromeDriver e = new ChromeDriver();
					// WebDriver e = getWebDriver();
					// innerQueue.add(e);
					// webDriverList.add(e);
				}
			}

		}
		return innerQueue.take();
	}

	public void returnToPool(WebDriver webDriver) {
		checkRunning();
		innerQueue.add(webDriver);
	}

	protected void checkRunning() {
		if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
			throw new IllegalStateException("Already closed!");
		}
	}

	public void closeAll() {
		boolean b = stat.compareAndSet(STAT_RUNNING, STAT_CLODED);
		if (!b) {
			throw new IllegalStateException("Already closed!");
		}
		for (WebDriver webDriver : webDriverList) {
			logger.info("Quit webDriver" + webDriver);
			webDriver.quit();
			webDriver = null;
		}
	}

}
