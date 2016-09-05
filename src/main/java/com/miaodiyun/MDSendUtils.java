package com.miaodiyun;

import com.miaodiyun.httpApiDemo.IndustrySMS;

public class MDSendUtils {

	public static String[] sendMsg(String to ,String content){
		 return IndustrySMS.sendMsg(to, content);
	}
	
	public static void main(String[] args) {
//		String smsContent1 = "【积分游戏大平台】您卖出的一币已打款，请在48小时内登录用户中心确认,否则将扣除您一颗信誉星。5天后未进行操作将暂封您的一币卖出功能！";
		String smsContent1 = "【积分游戏大平台】尊敬的559502玩家：您正在进行确认收款操作，验证码为：492769 ，如非本人操作请忽略";
//		String smsContent1 = "【积分游戏大平台】尊敬的相先生/女士：恭喜您成为幸福100的理财客户，您所注册的账户资金为50000元(伍万圆整)，用户名为：xgr5588a，祝福您跨入了财富自由的大门。[幸福100 2016/09/05]";
//		String smsContent1 = "";
		String to = "15919820372";
		MDSendUtils.sendMsg(to, smsContent1);
	}
}
