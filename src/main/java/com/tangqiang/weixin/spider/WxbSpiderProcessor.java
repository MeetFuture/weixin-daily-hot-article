package com.tangqiang.weixin.spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 *
 * @author acer-pc
 * @date 2017年5月18日 上午9:55:33
 * 
 * @version 1.0 2017年5月18日 acer-pc create
 *
 */
public class WxbSpiderProcessor implements PageProcessor {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void process(Page page) {
		try {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			String url = page.getUrl().get();
			Html html = page.getHtml();
			List<Selectable> trs = html.xpath("//tbody[@class='ant-table-tbody']/tr[@class='ant-table-row']").nodes();
			for (int i = 0; i < trs.size(); i++) {
				Selectable htmlTr = trs.get(i);
				String name = htmlTr.xpath("//div[@class='near-article-title']/a/text()").get();
				String href = htmlTr.xpath("//div[@class='near-article-title']/a/@href").get();
				String time = htmlTr.xpath("//td[2]/text()").get();
				String desc = htmlTr.xpath("//td[3]/text()").get();
				String read = htmlTr.xpath("//td[4]/text()").get();
				read = read.replaceAll("万\\+", "0000");
				String praise = htmlTr.xpath("//td[5]/text()").get();
				String exponential = htmlTr.xpath("//td[6]/span/text()").get();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("title", name);
				map.put("time", time);
				map.put("desc", desc);
				map.put("read", read);
				map.put("praise", praise);
				map.put("href", href);
				map.put("parent_url", url);
				map.put("exponential", exponential);
				list.add(map);
			}
			page.getResultItems().put("info", page.getRequest().getExtras());
			page.getResultItems().put("detailList", list);
		} catch (Exception e) {
			logger.error("WxbSpiderProcessor error !", e);
		}
	}

	public Site getSite() {
		Site site = Site.me().setCycleRetryTimes(1)//
				.setRetryTimes(5)//
				.setTimeOut(3 * 60 * 1000)//
				.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")//
				.addHeader("Accept-Encoding", "gzip, deflate, br")// /
				.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")//
				.addHeader("Connection", "keep-alive")//
				.addHeader("Upgrade-Insecure-Requests", "1")//
				.addHeader("Pragma", "no-cache")//
				.addHeader("Cache-Control", "no-cache")//
				.setCharset("UTF-8");//
		return site;
	}

}
