package com.tangqiang.db.sqlite;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteConfig;

/**
 * 测试
 *
 * @author Tom
 * @date 2017年8月4日 下午11:37:53
 *
 * @version 1.0 2017年8月4日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class JdbcTest extends Thread {
	private static JdbcTemplate jdbcTemplate = new JdbcTemplate(SqliteJdbcFactory.getDataSource());

	public static void main(String[] args) throws Exception {
		String[][] keys = new String[][] { { "qr_code", "VARCHAR(256)" }, { "code", "VARCHAR(128)" }, { "name", "VARCHAR(128)" }, { "detail_url", "VARCHAR(256)" } };
		SqliteDBUtil.createTable("wxb_rank_201707", keys);

		List<String> listColumns = SqliteDBUtil.getTableColumns("wxb_rank_201707");
		System.out.println(listColumns);
	}

	private void getTableMsg() throws Exception {
		DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
		// 获取所有表
		ResultSet tableSet = databaseMetaData.getTables(null, "%", "%", new String[] { "TABLE" });
		System.out.println(tableSet);
		while (tableSet.next()) {
			String tableName = tableSet.getString("TABLE_NAME");
			String tableComment = tableSet.getString("REMARKS");
			System.out.println("Table Name : " + tableName);
			ResultSet columnSet = databaseMetaData.getColumns(null, "%", tableName, "%");
			while (columnSet.next()) {
				String columnName = columnSet.getString("COLUMN_NAME");
				String columnComment = columnSet.getString("REMARKS");
				String sqlType = columnSet.getString("DATA_TYPE");
				System.out.println("column : " + columnName + "	" + columnComment + "	" + sqlType);
			}
		}
	}
}
