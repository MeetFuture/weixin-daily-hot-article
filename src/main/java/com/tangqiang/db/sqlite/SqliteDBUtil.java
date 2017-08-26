package com.tangqiang.db.sqlite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据库操作
 *
 * @author Tom
 * @date 2017年8月5日 下午12:09:36
 *
 * @version 1.0 2017年8月5日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class SqliteDBUtil {
	private static Logger logger = LoggerFactory.getLogger(SqliteDBUtil.class);
	public static JdbcTemplate jdbcTemplate = new JdbcTemplate(SqliteJdbcFactory.getDataSource());

	/** p判断表是否存在 */
	public static boolean isTableExist(String tableName) {
		String sql = "select count(*) as cou from sqlite_master where type='table' and name='" + tableName.trim() + "'";
		Map<String, Object> map = jdbcTemplate.queryForMap(sql);
		Integer cou = (Integer) map.get("cou");
		logger.info("SqliteDBUtil Table:" + tableName + ((cou == 1) ? " Exist ." : " Not Exist !!!"));
		return cou == 1;
	}

	/**
	 * 创建表<br>
	 * INT INTEGER TINYINT SMALLINT MEDIUMINT BIGINT <br>
	 * UNSIGNED BIG INT INT2 INT8 INTEGER CHARACTER(20) VARCHAR(255) <br>
	 * VARYING CHARACTER(255) NCHAR(55) NATIVE CHARACTER(70) NVARCHAR(100) TEXT CLOB <br>
	 * TEXT BLOB no datatype specified NONE REAL DOUBLE <br>
	 * DOUBLE PRECISION FLOAT REAL NUMERIC DECIMAL(10,5) <br>
	 * BOOLEAN DATE DATETIME NUMERIC <br>
	 */
	public static boolean createTable(String tableName, String[][] keys) {
		boolean result = false;
		try {
			StringBuilder sbBuilder = new StringBuilder();
			sbBuilder.append("create table if not exists " + tableName);
			sbBuilder.append("(id varchar(32) primary key not null");
			for (int i = 0; i < keys.length; i++) {
				String name = keys[i][0];
				String type = keys[i][1];
				sbBuilder.append("," + name + " " + type);
			}
			sbBuilder.append(")");
			String sql = sbBuilder.toString().toLowerCase();
			logger.info("SqliteDBUtil Create Table :" + sql);
			jdbcTemplate.execute(sql);
			result = true;
		} catch (Exception e) {
			logger.error("SqliteDBUtil Create Table Error!", e);
		}
		return result;
	}

	public static void createTable(String tableName, String[][] keys, boolean delete) {
		if (delete) {
			try {
				String sql = "delete table " + tableName;
				jdbcTemplate.execute(sql);
			} catch (Exception e) {
				logger.error("SqliteDBUtil Delete Table Error!", e);
			}
		}
		createTable(tableName, keys);
	}

	/** 获取表的所有列 */
	public static List<String> getTableColumns(String table) {
		List<String> list = new ArrayList<String>();
		String sql = "select * from sqlite_master where type='table' and name='" + table + "'";
		Map<String, Object> map = jdbcTemplate.queryForMap(sql);
		String createSql = map.get("sql").toString();
		int first = createSql.indexOf("(");
		int last = createSql.lastIndexOf(")");
		String cloumns = createSql.substring(first + 1, last);
		String[] cloumArr = cloumns.split(",");
		for (int i = 0; i < cloumArr.length; i++) {
			String[] cloumnMsg = cloumArr[i].trim().split(" ");
			String cloumn = cloumnMsg[0].toLowerCase();
			list.add(cloumn);
		}

		return list;
	}

	/** 更新 或插入 */
	public static int updateInsert(String tableName, Map<String, Object> mapData, String[] updateKeys) {
		int result = -1;
		try {
			List<String> listUpdateKeys = Arrays.asList(updateKeys);
			StringBuilder updateSql = new StringBuilder("update " + tableName + " set ");
			List<Object> objList = new ArrayList<Object>();

			Set<String> keyset = mapData.keySet();
			for (String key : keyset) {
				if (!listUpdateKeys.contains(key)) {
					updateSql.append(key + "=?,");
					objList.add(mapData.get(key));
				}
			}
			updateSql.setCharAt(updateSql.length() - 1, ' ');
			updateSql.append(" where ");
			for (String key : updateKeys) {
				updateSql.append(key + "=? and ");
				objList.add(mapData.get(key));
			}
			String insertSql = updateSql.substring(0, updateSql.length() - 4).toLowerCase();

			Object[] objArr = objList.toArray();
			result = jdbcTemplate.update(insertSql, objArr);
			if (result == 0) {
				result = insertData(tableName, mapData);
			}
		} catch (Exception e) {
			logger.error("DBCommonUtil updateInsert Table " + tableName + "  Error:", e);
		}
		return result;
	}

	/**
	 * 通用 插入数据方法
	 * 
	 * @param tableName
	 * @param mapData
	 * @return
	 */
	public static int insertData(String tableName, Map<String, Object> mapData) {
		int result = -1;
		try {
			StringBuilder sbKeys = new StringBuilder("INSERT INTO " + tableName + " (id");
			StringBuilder sbValues = new StringBuilder(" VALUES (?");
			List<Object> objList = new ArrayList<Object>();
			String id = mapData.getOrDefault("id", UUID.randomUUID().toString().replaceAll("-", "")).toString();
			objList.add(id);

			Set<String> keys = mapData.keySet();
			for (String col : keys) {
				sbKeys.append("," + col);
				sbValues.append(",?");
				objList.add(mapData.get(col));
			}
			sbKeys.append(")");
			sbValues.append(")");
			Object[] objArr = objList.toArray();

			String insertSql = sbKeys.toString().toLowerCase() + sbValues.toString().toLowerCase();
			result = jdbcTemplate.update(insertSql, objArr);
		} catch (Exception e) {
			logger.error("DBCommonUtil Add Data To Table " + tableName + "  Error:", e);
		}
		return result;
	}
}
