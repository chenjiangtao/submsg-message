package com.aspire.cmppsgw;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import com.aspire.cmppsgw.util.LogAgent;
import com.aspire.cmppsgw.util.MySMProxy30;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30SubmitRepMessage;
import com.huawei.insa2.util.TypeConvert;

/**
 * @desc 定时发送信息给指定人员
 * @author majiangtao@aspirehld.com
 * @date 2012-5-2
 * 
 */

public class SendLongSMS30 {
	
	public static Logger monitorInfoLogger = LogAgent.monitorInfoLogger;
	public static Logger mt_Logger = LogAgent.MTLogger;
	
	public static int tp_Pid =  GlobalEnv.getInstance().getIntValue("tp_Pid");
	public static int tp_Udhi =  GlobalEnv.getInstance().getIntValue("tp_Udhi");
	public static int fmt =  GlobalEnv.getInstance().getIntValue("fmt");
	

	public static void execute() throws Exception {
		monitorInfoLogger.info("start execute send test sms job...");
		try {
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sfd.format(new Date());
			String content = "【" + dateStr + "】" + GlobalEnv.getInstance().getValue("send.sms.content");
			String mobile = GlobalEnv.getInstance().getMobile();	
				String[] ms = {mobile};
				
				
				MySMProxy30 mySMProxy = MySMProxy30.getInstance();
				if(mySMProxy == null){
					monitorInfoLogger.info("登录不成功！不进行下发操作");
					return;
				}
				monitorInfoLogger.info("send mobile " +mobile + " send content " + content);
				monitorInfoLogger.info("tp_Pid："+tp_Pid+",tp_Udhi:"+tp_Udhi+",fmt:"+fmt);
				byte[][] smschars = LongSMSUtil.enCodeBytes(content,true);
				List<CMPPMessage> list = new ArrayList<CMPPMessage>();
				for (int i = 0; i < smschars.length; i++) {
					CMPP30SubmitMessage csm = new CMPP30SubmitMessage(1, 1, 1, 5, GlobalEnv.getInstance().getValue("send.sms.serviceid"), 1,
							mobile, 1,tp_Pid, tp_Udhi, fmt, GlobalEnv.getInstance().getValue("send.sms.spid"), "01", "0", null, null,
							GlobalEnv.getInstance().getValue("send.sms.srcid"),ms,0,smschars[i], "");
					list.add(csm);
				}
				monitorInfoLogger.info("list："+list.size());
				for(CMPPMessage csm:list){
					CMPPMessage submitRepMsg = mySMProxy.send(csm);
					CMPP30SubmitRepMessage crm = (CMPP30SubmitRepMessage) submitRepMsg;
					long msgId = TypeConvert.byte2long(crm.getMsgId());
					//记录下发日志,不用进行重发
					mt_Logger.info(mobile+"," + msgId + ",result:" + crm.getResult());	
				}
				
		} catch (Exception e) {
			monitorInfoLogger.error("发送测试短信失败", e);
		}
		monitorInfoLogger.info("end execute send test sms job...");
	}
	
	public static void main(String[] args) throws Exception {
		execute();
		Thread.sleep(60*60*1000);
	}
	
}
