package com.tangqiang.weixin.job;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tangqiang.weixin.push.WxbPushToUser;
import com.tangqiang.weixin.rank.WxbRankList;
import com.tangqiang.weixin.spider.WxbSpider;

/**
 *
 * @author Tom
 * @date 2017年8月10日 下午4:37:59
 *
 * @version 1.0 2017年8月10日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class AppJob implements Job {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Quartz " + new DateTime());
	}

	
	
}
