package com.tangqiang.weixin.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.tangqiang.db.sqlite.SqliteDBUtil;
import com.tangqiang.util.wchat.WChatUtil;
import com.tangqiang.weixin.App;

/**
 * 推送至用户
 *
 * @author Tom
 * @date 2017年8月8日 下午1:56:02
 *
 * @version 1.0 2017年8月8日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WxbPushToUser {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle("wchat");

	public static void main(String[] args) {
		WxbPushToUser push = new WxbPushToUser();
		push.execute(-1);
	}

	public void execute(int days) {
		List<Map<String, Object>> list = getPushData(days);
		Map<String, Object> params = getTokenAndUsers();

		String[] users = (String[]) params.get("users");
		for (int i = list.size(); i > 0; i--) {
			Map<String, Object> data = list.get(i - 1);
			data.put("index", i);
			for (int j = 0; j < users.length; j++) {
				String user = users[j];
				params.put("user", user);

				Map<String, Object> result = pushMsg(params, data);
				logger.info("WxbPushToUser Msg[" + i + "] User[" + user + "] result:" + result);
			}

		}
	}

	/** 查询数据 */
	private List<Map<String, Object>> getPushData(int days) {
		DateTime dateTime = new DateTime().plusDays(days);
		String tableName = App.TABLE_DETAIL_LIST_PRE + dateTime.toString("yyyyMM");
		String date = dateTime.toString("yyyy-MM-dd ");
		String sql = "select * from " + tableName + " where time like '" + date + "%' order by praise desc limit 0,20";
		List<Map<String, Object>> list = SqliteDBUtil.jdbcTemplate.queryForList(sql);
		logger.info("WxbPushToUser getPushData size:" + list.size() + "	" + list.get(0));
		return list;
	}

	/** 获取微信token */
	private Map<String, Object> getTokenAndUsers() {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String urlToken = bundle.getString("url_token");
			String appId = bundle.getString("appId");
			String secret = bundle.getString("secret");
			Map<String, Object> mapToken = WChatUtil.getAccessToken(urlToken, appId, secret);
			String token = mapToken.get("access_token").toString();

			String urlUserGet = bundle.getString("url_user_get");
			Map<String, Object> users = WChatUtil.getUsers(urlUserGet, token);
			Map<String, Object> data = (Map<String, Object>) users.get("data");
			JSONArray openids = (JSONArray) data.get("openid");
			String[] userArr = openids.toArray(new String[0]);
			result.put("users", userArr);

			result.put("token", token);
		} catch (Exception e) {
			logger.info("WxbPushToUser getTokenAndUsers error!", e);
		}
		return result;
	}

	private Map<String, Object> pushMsg(Map<String, Object> params, Map<String, Object> data) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String token = params.getOrDefault("token", "").toString();
			String user = params.getOrDefault("user", "").toString();
			String urlTemplateMsg = bundle.getString("url_msg_template");
			String templateId = bundle.getString("template_daily_hot_article");
			String urlTo = data.getOrDefault("href", "").toString();

			Map<String, Object> tmpData = new HashMap<String, Object>();
			String index = data.getOrDefault("index", "0").toString();
			index = index + "、";
			String title = data.getOrDefault("title", "0").toString();
			String wx_name = data.getOrDefault("wx_name", "0").toString();
			String time = data.getOrDefault("time", "0").toString();
			String read = data.getOrDefault("read", "0").toString();
			read = read + ("100000".equals(read) ? "+" : "");
			String praise = data.getOrDefault("praise", "0").toString();

			Map<String, Object> tmpDataIndex = new HashMap<String, Object>();
			Map<String, Object> tmpDataTitle = new HashMap<String, Object>();
			Map<String, Object> tmpDataWxName = new HashMap<String, Object>();
			Map<String, Object> tmpDataTime = new HashMap<String, Object>();
			Map<String, Object> tmpDataRead = new HashMap<String, Object>();
			Map<String, Object> tmpDataPraise = new HashMap<String, Object>();
			tmpData.put("index", tmpDataIndex);
			tmpData.put("title", tmpDataTitle);
			tmpData.put("wx_name", tmpDataWxName);
			tmpData.put("time", tmpDataTime);
			tmpData.put("read", tmpDataRead);
			tmpData.put("praise", tmpDataPraise);

			tmpDataIndex.put("value", index);
			tmpDataIndex.put("color", "#333333");
			tmpDataIndex.put("fontsize", "16");

			tmpDataTitle.put("value", title);
			tmpDataTitle.put("color", "#333333");

			tmpDataWxName.put("value", wx_name);
			tmpDataWxName.put("color", "#4395F5");

			tmpDataTime.put("value", time);
			tmpDataTime.put("color", "#999999");

			tmpDataRead.put("value", read);
			tmpDataRead.put("color", "#5eb1f3");

			tmpDataPraise.put("value", praise);
			tmpDataPraise.put("color", "#47c68c");

			Map<String, Object> resultTmp = WChatUtil.sendTemplateMessage(urlTemplateMsg, token, templateId, urlTo, tmpData, user);
			result.putAll(resultTmp);

		} catch (Exception e) {
			logger.info("WxbPushToUser pushMsg error!", e);
		}
		return result;
	}

	/** 创建消息 */
	private String buildMsg(List<Map<String, Object>> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<style>");
		sb.append(".dataintable {margin-top: 15px;    border-collapse: collapse;    border: 1px solid #aaa;    width: 100%;}");
		sb.append(".dataintable th {   vertical-align: baseline;   padding: 5px 15px 5px 6px;    background-color: #3F3F3F;    border: 1px solid #3F3F3F;    text-align: left;    color: #fff;}");
		sb.append(".dataintable td {    vertical-align: text-top;    padding: 6px 15px 6px 6px;    border: 1px solid #aaa;}");
		sb.append(".dataintable tr:nth-child(odd) {    background-color: #F5F5F5;}");
		sb.append(".dataintable tr:nth-child(even) { background-color: #fff;}");
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<table class='dataintable'>");
		sb.append("<tbody>");
		sb.append("<tr><th>Name</th><th>Title</th><th>Desc</th><th>Praise</th><th>href</th></tr>");

		for (Map<String, Object> map : list) {
			sb.append("<tr>");
			sb.append("<td>").append(map.get("wx_name")).append("</td>");
			sb.append("<td>").append(map.get("title")).append("</td>");
			sb.append("<td>").append(map.get("desc")).append("</td>");
			sb.append("<td>").append(map.get("praise")).append("</td>");
			sb.append("<td>").append(map.get("href")).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</tbody>");
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		String msg = sb.toString();
		logger.info("WxbPushToUser buildMsg:" + msg);
		return msg;
	}
}
