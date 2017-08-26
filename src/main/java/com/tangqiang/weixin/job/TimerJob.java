package com.tangqiang.weixin.job;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间输出
 *
 * @author Tom
 * @date 2017年8月11日 上午9:58:28
 *
 * @version 1.0 2017年8月11日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class TimerJob implements Job {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Quartz " + new DateTime());
	}

}
