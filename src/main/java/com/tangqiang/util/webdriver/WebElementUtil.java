package com.tangqiang.util.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具
 *
 * @author Tom
 * @date 2017年8月8日 上午10:08:15
 *
 * @version 1.0 2017年8月8日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WebElementUtil {
	private static Logger logger = LoggerFactory.getLogger(WebElementUtil.class);

	/**
	 * 在给定的时间内去查找元素，如果没找到则超时，抛出异常
	 */
	public static Boolean existElement(WebDriver driver, int timeOut, final By by) {
		boolean result = false;
		try {
			WebDriverWait driverWait = new WebDriverWait(driver, timeOut);
			result = driverWait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					WebElement element = driver.findElement(by);
					logger.info("WebElement find : " + element);
					return element != null;
				}
			});
		} catch (Exception e) {
			logger.error("Driver waitForElement [" + by + "] error !" + e.getMessage());
		}
		logger.info("Driver waitForElement [" + by + "] timeOut:" + timeOut + " result is " + result);
		return result;
	}

	public static WebElement waitForElement(WebDriver driver, int timeOut, final By by) {
		WebElement result = null;
		try {
			WebDriverWait driverWait = new WebDriverWait(driver, timeOut);
			driverWait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					WebElement element = driver.findElement(by);
					logger.info("WebElement find : " + element);
					return element != null;
				}
			});
			result = driver.findElement(by);
		} catch (Exception e) {
			logger.error("Driver waitForElement [" + by + "] error !" + e.getMessage());
		}
		logger.info("Driver waitForElement [" + by + "] timeOut:" + timeOut + " result is " + result);
		return result;
	}

}
