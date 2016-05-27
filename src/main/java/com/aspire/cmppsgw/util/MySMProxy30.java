package com.aspire.cmppsgw.util;

import java.io.File;
import java.nio.ByteBuffer;

import org.slf4j.Logger;

import com.huawei.insa2.comm.cmpp.message.CMPPMessage;
import com.huawei.insa2.comm.cmpp30.message.CMPP30DeliverMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;
import com.huawei.smproxy.SMProxy30;

public class MySMProxy30 extends SMProxy30 {
	private static MySMProxy30 instance;
	private static Logger logger = LogAgent.systemInfoLogger;
	private static Logger error_logger = LogAgent.ErrorLogger;
	private static Logger report_logger = LogAgent.ReportLogger;
	private static Logger mo_logger = LogAgent.MOLogger;
	public MySMProxy30(Args args) {
		super(args);
	}
	
	public  String getStatus(){
		return super.getConnState();
	}


	public static synchronized MySMProxy30 getInstance() {
		if (instance == null) {
			MySMProxy();
		}
		return instance;
	}

	private static void MySMProxy() {
		try {
			File file = new File(MySMProxy30.class.getClassLoader().getResource("SMProxy.xml").getPath());
			Args args = new Cfg(file.getAbsolutePath(), false)
					.getArgs("CMPPConnect");
			instance = new MySMProxy30(args);
			logger.info("*********login success**********");
		} catch (Exception e) {
			error_logger.error("MySMProxy error " + e.getMessage(),e);
		}
	}

	public CMPPMessage onDeliver(CMPP30DeliverMessage msg) {

		try {
			// ×´Ì¬±¨¸æ
			if (msg.getRegisteredDeliver() == 1) {
				ByteBuffer buffer = ByteBuffer.wrap(msg.getStatusMsgId());
				long mId = buffer.getLong();
				report_logger.info(mId + "," + msg.getStat() + ","
						+ msg.getDestnationId());

			} else {
				mo_logger.info(msg.getSrcterminalId() + ","
						+ new String(msg.getMsgContent()) + ","
						+ msg.getDestnationId());
			}
		} catch (Exception e) {
			error_logger.error(e.getMessage(),e);
		}
		return super.onDeliver(msg);
	}
	
}
