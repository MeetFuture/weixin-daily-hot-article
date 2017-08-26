package com.tangqiang.db.sqlite;

import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Sqlite 数据源获取
 *
 * @author Tom
 * @date 2017年8月4日 下午11:37:02
 *
 * @version 1.0 2017年8月4日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class SqliteJdbcFactory {
	private static Logger logger = LoggerFactory.getLogger(SqliteJdbcFactory.class);
	private static DataSource cpds = null;

	static {
		try {
			InputStream inputStream = SqliteJdbcFactory.class.getResourceAsStream("/sqlite.properties");
			Properties props = new Properties();
			props.load(inputStream);
			logger.info("SqliteJdbcFactory init config:" + props);
			cpds = new DriverManagerDataSource(props.getProperty("url"), props);
		} catch (Exception e) {
			logger.error("SqliteJdbcFactory init data source error !", e);
		}
	}

	public static DataSource getDataSource() {
		return cpds;
	}
}
