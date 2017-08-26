package com.tangqiang.weixin.rank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tangqiang.db.sqlite.SqliteDBUtil;

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
public class WxbRankList_Bak {
	private Logger logger = LoggerFactory.getLogger(getClass());

	static {
		System.out.println("System setProperty ..................");
		System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\driver\\chromedriver.exe");
		System.setProperty("phantomjs.binary.path", "src\\main\\resources\\driver\\phantomjs.exe");
	}

	public static void main(String[] args) {
		new WxbRankList_Bak().getList();
	}

	/** 获取排行榜 */
	public List<Map<String, Object>> get() {
		List<Map<String, Object>> listResult = null;
		try {
			DateTime nowTime = new DateTime();
			nowTime.plusMonths(-1);
			String month = nowTime.toString("yyyyMM");
			String tableName = "wxb_rank_list_" + month;

			boolean bool = SqliteDBUtil.isTableExist(tableName);
			if (!bool) {
				listResult = getList();

				String[][] keys = new String[][] { { "rank", "INTEGER" }, { "name", "VARCHAR(128)" }, { "code", "VARCHAR(128)" }, { "qr_code", "VARCHAR(256)" }, { "detail_url", "VARCHAR(256)" } };
				SqliteDBUtil.createTable("wxb_rank_201707", keys);
				writeDBCache(tableName, listResult);
			} else {
				listResult = readDBCache(tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listResult;
	}

	/** 从页面获取排行榜 */
	private List<Map<String, Object>> getList() {
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("window-size=1200,100");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		try {
			driver.get("http://data.wxb.com/rank");

			WebElement elementMonth = driver.findElement(By.className("datenav-ctn")).findElements(By.className("datenav-date-ctn")).get(2).findElement(By.tagName("button"));
			elementMonth.click();
			
			WebElement elementNext = waitForElement(driver, 50, By.className("ant-pagination-next"));
			if (elementNext == null) {
				return listResult;
			}
			
			Thread.sleep(2000);
			
			while (elementNext != null) {
				List<Map<String, Object>> one = getPage(driver);
				listResult.addAll(one);

				elementNext = driver.findElement(By.className("ant-pagination-next"));
				String cssValue = elementNext.getCssValue("cursor");
				if ("not-allowed".equals(cssValue)) {
					break;
				}
				elementNext.click();
				// Thread.sleep(2000);
				break;
			}
		} catch (Exception e) {
			logger.error(" getListPublic error !", e);
		} finally {
			driver.close();
			driver.quit();
		}
		logger.info("WxbSpider getListPublic over ... result:" + listResult);
		return listResult;
	}

	/** 获取一页数据 */
	private List<Map<String, Object>> getPage(WebDriver driver) {
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
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
				listResult.add(map);
			}
		} catch (Exception e) {
			logger.error("WxbRankList getPage error !", e);
		}
		logger.info("WxbRankList getPage:" + listResult);
		return listResult;
	}

	private WebElement waitForElement(WebDriver driver, int timeOut, final By by) {
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

	/** 读取缓存 */
	private List<Map<String, Object>> readCache(File file) {
		logger.info("Begin Read Cache:" + file.getAbsolutePath());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			String cache = null;
			while ((cache = bReader.readLine()) != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject json = JSONObject.parseObject(cache);
				map.putAll(json);
				list.add(map);
			}
			bReader.close();
		} catch (Exception e) {
			logger.info("Read Cache File Error !", e);
		}
		logger.info("Read Cache [ " + list.size() + " ] Success:" + list.get(0));
		return list;
	}

	/** 读取缓存 */
	private List<Map<String, Object>> readDBCache(String tableName) {
		logger.info("Begin Read Data:" + tableName);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			String sql = "select * from " + tableName;
			list = SqliteDBUtil.jdbcTemplate.queryForList(sql);
		} catch (Exception e) {
			logger.info("Read Table Data Error !", e);
		}
		logger.info("Read Data [ " + list.size() + " ] Success:" + list.get(0));
		return list;
	}

	/** 写入缓存 */
	private void writeDBCache(String tableName, List<Map<String, Object>> list) {
		logger.info("Begin write to DB ,Size:" + list.size());
		try {
			for (Map<String, Object> mapData : list) {
				SqliteDBUtil.insertData(tableName, mapData);
			}
		} catch (Exception e) {
			logger.info("Write to DB Error !", e);
		}
	}

	/** 写入缓存 */
	private void writeCache(File file, List<Map<String, Object>> list) {
		logger.info("Begin write Cache:" + file.getAbsolutePath() + "  Size:" + list.size());
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(file));
			for (Map<String, Object> map : list) {
				bWriter.write(JSONObject.toJSONString(map) + "\r\n");
			}
			bWriter.flush();
			bWriter.close();
		} catch (Exception e) {
			logger.info("Write Cache File Error !", e);
		}
	}
}
