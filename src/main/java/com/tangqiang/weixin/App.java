package com.tangqiang.weixin;

import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.tangqiang.weixin.push.WxbPushToUser;
import com.tangqiang.weixin.rank.WxbRankList;
import com.tangqiang.weixin.spider.WxbSpider;

/**
 * 微信排行榜扫描
 *
 * @author Tom
 * @date 2017年8月7日 下午6:36:14
 *
 * @version 1.0 2017年8月7日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class App {
	public static String TABLE_DETAIL_LIST_PRE = "wxb_detail_list_";
	public static String TABLE_RANK_PRE = "wxb_rank_";

	public static void main(String[] args) {
		try {
			WxbRankList wSpider = new WxbRankList();
			List<Map<String, Object>> list = wSpider.get();

			WxbSpider wxbSpider = new WxbSpider(list);
			wxbSpider.startSpider();

			WxbPushToUser push = new WxbPushToUser();
			push.execute(-1);
			
//			SchedulerFactory schedulerfactory = new StdSchedulerFactory("quartz/quartz.properties");
//			Scheduler scheduler = schedulerfactory.getScheduler();
//			scheduler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}