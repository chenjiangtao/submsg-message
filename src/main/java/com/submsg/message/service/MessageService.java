package com.submsg.message.service;

import java.util.Map;

public class MessageService {

	/**
	 * 发送短信接口  
	 * @param merchantID  商户id
	 * @param projectId   项目id
	 * @param modeId      短信模板id
	 * @param targetPhone  目标手机
	 * @return
	 */
	public boolean sendMessage(int merchantID,int projectId,int modeId,String targetPhone,Map<String,String> param){
		
		return true;
	}
}
