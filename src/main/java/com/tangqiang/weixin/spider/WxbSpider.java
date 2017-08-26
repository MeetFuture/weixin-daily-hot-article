package com.tangqiang.weixin.spider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangqiang.db.sqlite.SqliteDBUtil;
import com.tangqiang.weixin.App;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

/**
 * 微信排行榜
 *
 * @author Tom
 * @date 2017年8月3日 上午9:06:53
 *
 * @version 1.0 2017年8月3日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WxbSpider {
	private Logger logger = LoggerFactory.getLogger(getClass());
	List<Map<String, Object>> list;

	public WxbSpider(List<Map<String, Object>> list) {
		this.list = list;
	}

	public void startSpider() {
		List<Request> listUrl = new ArrayList<Request>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String detail_url = map.get("detail_url").toString();
			Request request = new Request(detail_url);
			request.setExtras(map);
			listUrl.add(request);
		}

		Spider spider = Spider.create(new WxbSpiderProcessor())//
				.addRequest(listUrl.toArray(new Request[0]))//
				.addPipeline(new WxbSpiderPipeline())//
				.thread(10);// 10
		spider.run();
	}


	// Set<Cookie> cookies = wSpider.getSiteCookies();
	// String web = "http://data.wxb.com/rank/month/2017-07/-1?sort=top_read_num_avg+desc&page=1&page_size=20";
	// Request req = new Request(web);
	// for (Cookie cookie : cookies) {
	// if (cookie.getName().startsWith("Hm_lpvt_")) {
	// int time = (int) (System.currentTimeMillis() / 1000);
	// req.addCookie(cookie.getName(), ""+time);
	// }else{
	// req.addCookie(cookie.getName(), cookie.getValue());
	// }
	// }
	private Set<Cookie> getSiteCookies() throws Exception {
		WebDriver driver = new PhantomJSDriver();
		driver.get("http://data.wxb.com/rank");
		Set<Cookie> cookies = driver.manage().getCookies();
		logger.info("cookies:" + cookies);
		driver.close();
		driver.quit();
		return cookies;
	}

}
