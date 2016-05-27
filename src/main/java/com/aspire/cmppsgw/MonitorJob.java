package com.aspire.cmppsgw;

import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;

import com.aspire.cmppsgw.job.SendTestSMSJob;
import com.aspire.cmppsgw.util.LogAgent;

public class MonitorJob {
	private Logger monitorInfoLogger = LogAgent.monitorInfoLogger;
	private Scheduler scheduler;
	private static MonitorJob instance = new MonitorJob();

	public static MonitorJob getInstance(){
		return instance;
	}

	/**
	 * ��ʼ����
	 */
	public void start() {
		try {
			startJob();
		} catch (Exception e) {
			monitorInfoLogger.error(e.getMessage(), e);
		}
	}

	/**
	 * ��ʼҵ��
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void startJob() throws Exception {
		scheduler = StdSchedulerFactory.getDefaultScheduler();
		monitorInfoLogger.info("start scheduler...");
		JobDetail sendsmsJobDetail = new JobDetailImpl("SendSMSJob",
				Scheduler.DEFAULT_GROUP, SendTestSMSJob.class);
		scheduler.scheduleJob(sendsmsJobDetail, initCronTrigger(GlobalEnv.getInstance().getValue("send.sms.cron"),"sendsms"));
		scheduler.start();
	}

	/**
	 * ��ͣҵ��
	 */
	public void stopJob() {
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				monitorInfoLogger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * ��ʼ���̶�ʱ����ҵ������
	 * 
	 * @param interval
	 *            ����ҵ��Ĺ̶�ʱ��
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Trigger initCronTrigger(String time,String name) throws Exception {
		if(name == null){
			CronTriggerImpl cronTrigger = new CronTriggerImpl("cronTrigger");
			monitorInfoLogger.info("set cron trigger time is:" + time);
			CronExpression cexp = new CronExpression(time);
			cronTrigger.setCronExpression(cexp);
			return cronTrigger;
		}else{
			CronTriggerImpl cronTrigger = new CronTriggerImpl(name);
			monitorInfoLogger.info("set cron trigger time is:" + time);
			CronExpression cexp = new CronExpression(time);
			cronTrigger.setCronExpression(cexp);
			return cronTrigger;
		}
	}
	
}
