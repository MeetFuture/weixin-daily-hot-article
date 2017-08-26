package com.tangqiang.weixin.spider;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import com.tangqiang.db.sqlite.SqliteDBUtil;
import com.tangqiang.weixin.App;

/**
 * 
 * @author Tom
 * @date 2017年8月8日 上午10:21:11
 *
 * @version 1.0 2017年8月8日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WxbSpiderPipeline implements Pipeline {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static Set<String> tables = new HashSet<String>();

	public void process(ResultItems resultItems, Task task) {
		try {
			List<Map<String, Object>> list = resultItems.get("detailList");
			Map<String, Object> info = resultItems.get("info");
			for (Map<String, Object> map : list) {
				String wx_name = info.get("name").toString();
				String wx_code = info.get("code").toString();
				map.put("wx_name", wx_name);
				map.put("wx_code", wx_code);

				String time = map.get("time").toString();

				String href = map.getOrDefault("href", wx_name + time).toString();
				String id = DigestUtils.md5Hex(href);
				map.put("id", id);
				
				DateTime dateTime = DateTime.parse(time, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"));
				String tableName = App.TABLE_DETAIL_LIST_PRE + dateTime.toString("yyyyMM");
				createTable(tableName);

				SqliteDBUtil.updateInsert(tableName, map, new String[] { "id" });
			}
		} catch (Exception e) {
			logger.error("WxbSpiderPipeline error !", e);
		}

	}

	/** 创建表 */
	private void createTable(String tableName) {
		if (!tables.contains(tableName)) {
			String[][] keys = new String[][] { { "wx_name", "VARCHAR(256)" }, { "wx_code", "VARCHAR(256)" }, { "title", "VARCHAR(256)" }, { "time", "VARCHAR(32)" }, { "desc", "VARCHAR(256)" },
					{ "read", "INTEGER" }, { "praise", "INTEGER" }, { "href", "VARCHAR(512)" }, { "parent_url", "VARCHAR(256)" }, { "exponential", "DOUBLE" } };
			boolean bCreate = SqliteDBUtil.createTable(tableName, keys);
			if (bCreate) {
				tables.add(tableName);
			}
		}
	}
}
