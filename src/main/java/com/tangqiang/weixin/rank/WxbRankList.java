package com.tangqiang.weixin.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangqiang.db.sqlite.SqliteDBUtil;
import com.tangqiang.util.webdriver.WebElementUtil;
import com.tangqiang.weixin.App;

/**
 * 获取微信排行榜
 *
 * @author Tom
 * @date 2017年8月4日 下午6:49:03
 *
 * @version 1.0 2017年8月4日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WxbRankList {
	private Logger logger = LoggerFactory.getLogger(getClass());

	static {
		System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\driver\\chromedriver.exe");
		System.setProperty("phantomjs.binary.path", "src\\main\\resources\\driver\\phantomjs.exe");
	}

	public static void main(String[] args) {
		new WxbRankList().get();
	}

	/** 获取排行榜 */
	public List<Map<String, Object>> get() {
		List<Map<String, Object>> listResult = null;
		try {
			DateTime nowTime = new DateTime();
			DateTime preMonth = nowTime.plusMonths(-1);
			String month = preMonth.toString("yyyyMM");
			String tableName = App.TABLE_RANK_PRE + month;

			boolean bool = SqliteDBUtil.isTableExist(tableName);
			if (!bool) {
				getDataList(tableName);
			}

			String sql = "select * from " + tableName + " order by rank limit 0,200";
			listResult = SqliteDBUtil.jdbcTemplate.queryForList(sql);
			logger.info("WxbRankList get [ " + listResult.size() + " ] Success:" + listResult.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listResult;
	}

	/** 从页面获取排行榜 */
	private void getDataList(String tableName) {
		String[][] keys = new String[][] { { "rank", "INTEGER" }, { "name", "VARCHAR(128)" }, { "code", "VARCHAR(128)" }, { "qr_code", "VARCHAR(256)" }, { "detail_url", "VARCHAR(256)" } };
		SqliteDBUtil.createTable(tableName, keys);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("window-size=1200,200");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		try {
			driver.get("http://data.wxb.com/rank");

			WebElement elementMonth = driver.findElement(By.className("datenav-ctn")).findElements(By.className("datenav-date-ctn")).get(2).findElement(By.tagName("button"));
			elementMonth.click();
			Thread.sleep(1000);

			WebElement elementNext = WebElementUtil.waitForElement(driver, 50, By.className("ant-pagination-next"));
			if (elementNext == null) {
				return;
			}
			Thread.sleep(1000);
			while (elementNext != null) {
				getPageData(tableName, driver);

				elementNext = driver.findElement(By.className("ant-pagination-next"));
				String cssValue = elementNext.getCssValue("cursor");
				if ("not-allowed".equals(cssValue)) {
					break;
				}
				elementNext.click();
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			logger.error(" getListPublic error !", e);
		} finally {
			driver.close();
			driver.quit();
		}
	}

	/** 获取一页数据 */
	private void getPageData(String tableName, WebDriver driver) {
		long startTime = System.currentTimeMillis();
		try {

			List<WebElement> list = driver.findElements(By.className("ant-table-row"));
			for (WebElement element : list) {
				WebElement elementRank = element.findElements(By.tagName("td")).get(0).findElements(By.tagName("span")).get(1);
				String rankString = elementRank.getAttribute("innerHTML");
				String rank = rankString.indexOf("data-first") > 0 ? "1" : rankString.indexOf("data-second") > 0 ? "2" : rankString.indexOf("data-third") > 0 ? "3" : rankString;
				if (rank.length() > 1) {
					int begin = rankString.indexOf("-->", 0);
					int end = rankString.indexOf("<!--", 3);
					rank = rankString.substring(begin + 3, end);
				}

				String qr_code = element.findElement(By.className("wxb-avatar-img")).findElement(By.tagName("a")).findElement(By.tagName("img")).getAttribute("src");
				WebElement elementDetail = element.findElement(By.className("wxb-avatar-text")).findElement(By.className("wxb-avatar-name")).findElement(By.tagName("a"));
				String avatarName = elementDetail.getAttribute("innerHTML");
				String detailUrl = elementDetail.getAttribute("href");
				String code = element.findElement(By.className("wxb-avatar-subtitle")).getAttribute("innerHTML");

				// logger.info("rankString[" + rank + "]:" + rankString + "	AvatarName:" + avatarName + " [" + code + "]	qr_code:" + qr_code + "	detailUrl:" + detailUrl);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("rank", rank);
				map.put("qr_code", qr_code);
				map.put("code", code);
				map.put("name", avatarName);
				map.put("detail_url", detailUrl);

				SqliteDBUtil.insertData(tableName, map);
			}
		} catch (Exception e) {
			logger.error("WxbRankList getPage error !", e);
		}
		long endTime = System.currentTimeMillis();
		logger.info("WxbRankList getPageData over,Time used:" + (endTime - startTime) + "/ms");
	}

}
