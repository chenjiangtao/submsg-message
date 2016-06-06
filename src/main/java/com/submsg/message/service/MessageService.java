package com.submsg.message.service;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.submsg.member.dao.MsgSendLogDao;

public class MessageService {

	
	@Autowired
	private MsgSendLogDao msgSendLogDao;
	/**
	 * 短信发送
	 * @param merchantID  商户id
	 * @param projectId   项目id
	 * @param modeId      模板id
	 * @param targetPhone  目标手机
	 * @return
	 * @throws IOException 
	 */
	public void sendMessage(int merchantID,int projectId,int modeId,String targetPhone,Map<String,String> param) {
		
		String content = "测试短信，验证码为:1111【积分游戏大平台】";
		
 
		
		return;
	}
}
