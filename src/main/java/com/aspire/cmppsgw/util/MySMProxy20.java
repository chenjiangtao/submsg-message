package com.aspire.cmppsgw.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import org.slf4j.Logger;

import com.huawei.insa2.comm.cmpp.message.CMPPDeliverMessage;
import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;
import com.huawei.smproxy.SMProxy;
import com.sr178.game.framework.context.ServiceCacheFactory;
import com.sr178.game.framework.log.LogSystem;

import cn.submsg.member.bo.MsgDeleverLog;
import cn.submsg.message.service.MessageQueueService;

public class MySMProxy20 extends SMProxy {
	private static MySMProxy20 instance;
	private static Logger logger = LogAgent.systemInfoLogger;
	private static Logger error_logger = LogAgent.ErrorLogger;
	private static Logger report_logger = LogAgent.ReportLogger;
	private static Logger mo_logger = LogAgent.MOLogger;
	public MySMProxy20(Args args) {
		super(args);
	}
	
	public  String getStatus(){
		return super.getConnState();
	}


	public static synchronized MySMProxy20 getInstance() {
		if (instance == null) {
			MySMProxy();
		}
		return instance;
	}

	private static void MySMProxy() {
		try {
			File file = new File(MySMProxy20.class.getClassLoader().getResource("SMProxy.xml").getPath());
			Args args = new Cfg(file.getAbsolutePath(), false)
					.getArgs("CMPPConnect");
			instance = new MySMProxy20(args);
			logger.info("*********login success**********");
		} catch (Exception e) {
			error_logger.error("MySMProxy error " + e.getMessage(),e);
		}
	}

	public CMPPMessage onDeliver(CMPPDeliverMessage msg) {

		try {
				ByteBuffer buffer = ByteBuffer.wrap(msg.getStatusMsgId());
				long mId = buffer.getLong();
			//状态回调
//			if (msg.getRegisteredDeliver() == 1) {
//				ByteBuffer buffer = ByteBuffer.wrap(msg.getStatusMsgId());
//				long mId = buffer.getLong();
//				report_logger.info(mId + "," + msg.getStat() + ","
//						+ msg.getDestnationId());

//			} else {
//				mo_logger.info(msg.getSrcterminalId() + ","
//						+ new String(msg.getMsgContent()) + ","
//						+ msg.getDestnationId());
//			}
			MessageQueueService queueService = ServiceCacheFactory.getService(MessageQueueService.class);
			MsgDeleverLog msgDeleverLog = new MsgDeleverLog();
			msgDeleverLog.setCreatedTime(new Date());
			msgDeleverLog.setDestnationId(msg.getDestnationId());
			msgDeleverLog.setMsgId(mId+"");
			msgDeleverLog.setStat(msg.getStat());
			queueService.pushDeleverReqMsg(msgDeleverLog);
		} catch (Exception e) {
			LogSystem.error(e, "");
		}
		return super.onDeliver(msg);
	}
	
}
