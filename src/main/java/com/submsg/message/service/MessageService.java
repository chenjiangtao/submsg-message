package com.submsg.message.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aspire.cmppsgw.GlobalEnv;
import com.aspire.cmppsgw.LongSMSUtil;
import com.aspire.cmppsgw.util.MySMProxy20;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;
import com.huawei.insa2.util.TypeConvert;
import com.sr178.game.framework.log.LogSystem;

public class MessageService {

	public static int tp_Pid =  GlobalEnv.getInstance().getIntValue("tp_Pid");
	public static int tp_Udhi =  GlobalEnv.getInstance().getIntValue("tp_Udhi");
	public static int fmt =  GlobalEnv.getInstance().getIntValue("fmt");
	public static int fee_UserType =  GlobalEnv.getInstance().getIntValue("fee_UserType");
	/**
	 * 短信发送
	 * @param merchantID  商户id
	 * @param projectId   项目id
	 * @param modeId      模板id
	 * @param targetPhone  目标手机
	 * @return
	 * @throws IOException 
	 */
	public void sendMessage(int merchantID,int projectId,int modeId,String targetPhone,Map<String,String> param) throws IOException{
		
		
		String content = "测试短信，验证码为:1111【积分游戏大平台】";
		
		
		MySMProxy20 mySMProxy = MySMProxy20.getInstance();
		if(mySMProxy == null){
			return;
		}
		String[] ms = {targetPhone};
		LogSystem.info("tp_Pid:"+tp_Pid+",tp_Udhi:"+tp_Udhi+",fmt:"+fmt);
		byte[][] smschars = LongSMSUtil.enCodeBytes(content,true);
		List<CMPPMessage> list = new ArrayList<CMPPMessage>();
		for (int i = 0; i < smschars.length; i++) {
			CMPPSubmitMessage csm = new CMPPSubmitMessage(1, 1, 1, 5, GlobalEnv.getInstance().getValue("send.sms.serviceid"), fee_UserType,
					targetPhone, tp_Pid, tp_Udhi, fmt, GlobalEnv.getInstance().getValue("send.sms.spid"), "01", "0", null, null,
					GlobalEnv.getInstance().getValue("send.sms.srcid"),ms,smschars[i], "");
			list.add(csm);
		}
		LogSystem.info("消息数量="+list.size());
		for(CMPPMessage csm:list){
			CMPPMessage submitRepMsg = mySMProxy.send(csm);
			CMPPSubmitRepMessage crm = (CMPPSubmitRepMessage) submitRepMsg;
			long msgId = TypeConvert.byte2long(crm.getMsgId());
			LogSystem.info(targetPhone+"," + msgId + ",result:" + crm.getResult());	
		}
		
		return;
	}
}
