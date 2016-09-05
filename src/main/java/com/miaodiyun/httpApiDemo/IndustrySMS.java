package com.miaodiyun.httpApiDemo;

import com.miaodiyun.httpApiDemo.common.Config;
import com.miaodiyun.httpApiDemo.common.HttpUtil;
import com.miaodiyun.huiDiao.RespCode;
import com.sr178.game.framework.log.LogSystem;

import net.sf.json.JSONObject;

/**
 * 验证码通知短信接口
 * 
 * @ClassName: IndustrySMS
 * @Description: 验证码通知短信接口
 *
 */
public class IndustrySMS
{
	private static String operation = "/industrySMS/sendSMS";

	private static String accountSid = Config.ACCOUNT_SID;
//	private static String to = "1361305****";
//	private static String smsContent = "【秒嘀科技】您的验证码是345678，30分钟输入有效。";

	/**
	 * 验证码通知短信
	 */
//	public static void execute()
//	{
//		String url = Config.BASE_URL + operation;
//		String body = "accountSid=" + accountSid + "&to=" + to + "&smsContent=" + smsContent
//				+ HttpUtil.createCommonParam();
//
//		// 提交请求
//		String result = HttpUtil.post(url, body);
//		System.out.println("result:" + System.lineSeparator() + result);
//	}
	
	public static String[] sendMsg(String to,String content){
		LogSystem.info("发送方式为秒滴！");
		String[] result = new String[2];
		String url = Config.BASE_URL + operation;
		String body = "accountSid=" + accountSid + "&to=" + to + "&smsContent=" + content
				+ HttpUtil.createCommonParam();
		String resultStr = HttpUtil.post(url, body);
		JSONObject json = JSONObject.fromObject(resultStr);
		LogSystem.info("result:" + System.lineSeparator() + resultStr);
		if(json.getString("respCode").equals(RespCode.SUCCESS)){
			result[0] = json.getString("smsId");
			result[1] = RespCode.SUCCESS;
			return result;
		}else{
			return null;
		}
	}
}
