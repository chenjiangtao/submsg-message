package com.aspire.cmppsgw.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.aspire.cmppsgw.GlobalEnv;
import com.aspire.cmppsgw.util.LogAgent;
import com.aspire.cmppsgw.util.MySMProxy20;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPSubmitRepMessage;
import com.huawei.insa2.util.TypeConvert;

/**
 * @desc 定时发送信息给指定人员
 * @author majiangtao@aspirehld.com
 * @date 2012-5-2
 * 
 */

public class SendTestSMSJob implements Job {
	
	public static Logger monitorInfoLogger = LogAgent.monitorInfoLogger;
	public static Logger mt_Logger = LogAgent.MTLogger;
	
	public static int tp_Pid =  1;
	public static int tp_Udhi = 6;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		monitorInfoLogger.info("start execute send test sms job...");
		try {
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sfd.format(new Date());
			String content = "【" + dateStr + "】" + GlobalEnv.getInstance().getValue("send.sms.content");
			String mobile = GlobalEnv.getInstance().getMobile();	
				String[] ms = {mobile};
				monitorInfoLogger.info("send mobile " +mobile + " send content " + content);
				MySMProxy20 mySMProxy = MySMProxy20.getInstance();
				if(mySMProxy == null){
					return;
				}
				CMPPSubmitMessage csm = new CMPPSubmitMessage(1, 1, 1, 5, GlobalEnv.getInstance().getValue("send.sms.serviceid"), 3,
						mobile, 0, 0, 15, GlobalEnv.getInstance().getValue("send.sms.spid"), "01", "0", null, null,
						GlobalEnv.getInstance().getValue("send.sms.srcid"),ms,content.getBytes(), "");
				
				
				CMPPMessage submitRepMsg = mySMProxy.send(csm);
				CMPPSubmitRepMessage crm = (CMPPSubmitRepMessage) submitRepMsg;
				long msgId = TypeConvert.byte2long(crm.getMsgId());
				//记录下发日志,不用进行重发
				mt_Logger.info(mobile+"," + msgId + ",result:" + crm.getResult());	
		} catch (Exception e) {
			monitorInfoLogger.error("发送测试短信失败", e);
		}
		monitorInfoLogger.info("end execute send test sms job...");
	}
	
}
