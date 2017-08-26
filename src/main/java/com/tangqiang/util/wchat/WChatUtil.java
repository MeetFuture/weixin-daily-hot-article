package com.tangqiang.util.wchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息发送
 *
 * @author Tom
 * @date 2017年8月2日 下午3:08:30
 *
 * @version 1.0 2017年8月2日 Tom create
 * 
 * @copyright Copyright © 2017-???? 广电运通 All rights reserved.
 */
public class WChatUtil {
	private static Logger logger = LoggerFactory.getLogger(WChatUtil.class);
	private static Map<String, Object> RET = JSONObject.parseObject("{\"-1\":\"系统繁忙\",\"0\":\"请求成功\",\"40001\":\"验证失败\",\"40002\":\"不合法的凭证类型\",\"40003\":\"不合法的OpenID\",\"40004\":\"不合法的媒体文件类型\","
			+ "\"40005\":\"不合法的文件类型\",\"40006\":\"不合法的文件大小\",\"40007\":\"不合法的媒体文件id\",\"40008\":\"不合法的消息类型\",\"40009\":\"不合法的图片文件大小\","
			+ "\"40010\":\"不合法的语音文件大小\",\"40011\":\"不合法的视频文件大小\",\"40012\":\"不合法的缩略图文件大小\",\"40013\":\"不合法的APPID\",\"41001\":\"缺少access_token参数\","
			+ "\"41002\":\"缺少appid参数\",\"41003\":\"缺少refresh_token参数\",\"41004\":\"缺少secret参数\",\"41005\":\"缺少多媒体文件数据\",\"41006\":\"access_token超时\","
			+ "\"42001\":\"需要GET请求\",\"43002\":\"需要POST请求\",\"43003\":\"需要HTTPS请求\",\"44001\":\"多媒体文件为空\",\"44002\":\"POST的数据包为空\",\"44003\":\"图文消息内容为空\","
			+ "\"45001\":\"多媒体文件大小超过限制\",\"45002\":\"消息内容超过限制\",\"45003\":\"标题字段超过限制\",\"45004\":\"描述字段超过限制\",\"45005\":\"链接字段超过限制\","
			+ "\"45006\":\"图片链接字段超过限制\",\"45007\":\"语音播放时间超过限制\",\"45008\":\"图文消息超过限制\",\"45009\":\"接口调用超过限制\",\"46001\":\"不存在媒体数据\",\"47001\":\"解析JSON/XML内容错误\"}");

	/** 获取用户 https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s */
	public static Map<String, Object> getUsers(String urlUserGet, String token) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		String url = String.format(urlUserGet, token);
		HttpMethod get = new GetMethod(url);
		int code = new HttpClient().executeMethod(get);
		if (code == 200) {
			String result = get.getResponseBodyAsString();
			JSONObject jsonObject = JSONObject.parseObject(result);
			mapResult.putAll(jsonObject);
			logger.info("WChat getUsers:[" + RET.get(mapResult.getOrDefault("errcode", "-1")) + "]" + mapResult);
		} else {
			logger.error("WChat getUsers:" + code + "	" + get.getResponseBodyAsString());
		}
		return mapResult;
	}

	/**
	 * 
	 * @param urlTemplateMsg
	 *            https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s
	 * @param token
	 *            token
	 * @param templateId
	 *            template_id
	 * @param urlTo
	 *            urlTo
	 * @param data
	 *            { 'keyword1': { 'value': a, 'color': '#173177' }, 'keyword2': { 'value': b, 'color': '#173177' }
	 * @param users
	 *            openid
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> sendTemplateMessage(String urlTemplateMsg, String token, String templateId, String urlTo, Map<String, Object> data, String user) throws Exception {
		String url = String.format(urlTemplateMsg, token);
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Map<String, Object> mapSend = new HashMap<String, Object>();
		mapSend.put("touser", user);
		mapSend.put("template_id", templateId);
		mapSend.put("url", urlTo);
		mapSend.put("data", data);

		RequestEntity requestEntity = new StringRequestEntity(JSONObject.toJSONString(mapSend), "application/json", "UTF-8");
		PostMethod post = new PostMethod(url);
		post.setRequestEntity(requestEntity);

		int code = new HttpClient().executeMethod(post);
		if (code == 200) {
			String result = post.getResponseBodyAsString();
			JSONObject jsonObject = JSONObject.parseObject(result);
			mapResult.putAll(jsonObject);
			logger.info("WChat sendTemplateMessage:[" + RET.get(mapResult.getOrDefault("errcode", "-1").toString()) + "]" + mapResult);
		} else {
			logger.error("WChat sendTemplateMessage:" + code + "	" + post.getResponseBodyAsString());
		}
		return mapResult;
	}

	public static Map<String, Object> sendTextMessage(String urlTextMsg, String token, String message, String[] users) throws Exception {
		String url = String.format(urlTextMsg, token);
		Map<String, Object> mapResult = new HashMap<String, Object>();

		Map<String, Object> msg_content = new HashMap<String, Object>();
		msg_content.put("content", message);
		Map<String, Object> mapSend = new HashMap<String, Object>();
		mapSend.put("touser", users);
		mapSend.put("toparty", "");
		mapSend.put("msgtype", "text");
		mapSend.put("agentid", 9);
		mapSend.put("safe", 0);
		mapSend.put("text", msg_content);

		RequestEntity requestEntity = new StringRequestEntity(JSONObject.toJSONString(mapSend), "application/json", "UTF-8");

		PostMethod post = new PostMethod(url);
		post.setRequestEntity(requestEntity);

		int code = new HttpClient().executeMethod(post);
		if (code == 200) {
			String result = post.getResponseBodyAsString();
			JSONObject jsonObject = JSONObject.parseObject(result);
			mapResult.putAll(jsonObject);
			logger.info("WChat sendTextMessage:[" + RET.get(mapResult.getOrDefault("errcode", "-1")) + "]" + mapResult);
		} else {
			logger.error("WChat sendTextMessage:" + code + "	" + post.getResponseBodyAsString());
		}
		return mapResult;
	}

	/** 获取AccessToken https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s */
	public static Map<String, Object> getAccessToken(String urlToken, String appId, String secret) throws Exception {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		mapResult.put("date", new Date().getTime());
		String url = String.format(urlToken, appId, secret);
		HttpMethod get = new GetMethod(url);
		int code = new HttpClient().executeMethod(get);
		if (code == 200) {
			String result = get.getResponseBodyAsString();
			JSONObject jsonObject = JSONObject.parseObject(result);
			mapResult.putAll(jsonObject);
			logger.info("WChat getAccessToken:" + mapResult);
		} else {
			logger.error("WChat getAccessToken:" + code + "	" + get.getResponseBodyAsString());
		}
		return mapResult;
	}

}
