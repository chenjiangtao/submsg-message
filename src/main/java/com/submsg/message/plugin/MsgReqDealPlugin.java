package com.submsg.message.plugin;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.framework.plugin.IAppPlugin;
import com.submsg.message.utils.MsgSendUtils;

import cn.submsg.member.bo.MsgSendLog;
import cn.submsg.message.bean.MsgBean;
import cn.submsg.message.service.MessageQueueService;

public class MsgReqDealPlugin implements IAppPlugin {
	@Autowired
	private ThreadPoolTaskExecutor taskExecuter;
	
    @Autowired
	private MessageQueueService messageQueueService;
    
    
    private AtomicInteger sendNum = new AtomicInteger();
    
    private AtomicBoolean isShutDown  = new AtomicBoolean(false);
    
	@Override
	public void startup() throws Exception {
		taskExecuter.setWaitForTasksToCompleteOnShutdown(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if(isShutDown.get()){
							LogSystem.warn("关闭队列监听程序！不再监听队列信息.已发送数量为"+sendNum.get());
							return;
						}
						MsgBean msgBean = messageQueueService.blockReqPopMsg();
						if (msgBean != null) {
							int num = sendNum.incrementAndGet();
							LogSystem.info("发送第["+num+"]条短信["+msgBean.getSendId()+"]");
							handlerRequest(msgBean);
						} else {
							LogSystem.info("当前没有发送短信的请求,已发送短信数量"+sendNum.get());
						}
					} catch (Exception e) {
						LogSystem.error(e, "");
						try {
							Thread.sleep(100);
						} catch (InterruptedException ie) {
							LogSystem.error(ie, "");
						}
					}
				}
			}
		}).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			  public void run() {
				  LogSystem.warn("收到关闭的消息 执行优雅关闭操作！~~~");
				  isShutDown.compareAndSet(false, true);
				  try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						 
					}
				  taskExecuter.shutdown();
				LogSystem.warn("优雅关闭成功！~~");
			  }
			});
	}
	
	/**
	 * 处理消息
	 * @param msgBean
	 */
	private void handlerRequest(final MsgBean msgBean){
		taskExecuter.execute(new Runnable() {
			@Override
			public void run() {
				try {
				 String to = msgBean.getTo();
				 String content = msgBean.getContent();
				 String signNum = msgBean.getSignNum();
				 //删除比较大的内容 优化下性能
				 msgBean.setContent(null);
				 
//				 msgBean.setStatus(MsgSendLog.ST_SEND);
//				 msgBean.setSendTime(new Date());
//				 messageQueueService.pushResMsg(msgBean);//响应发送消息
				 String[] result = MsgSendUtils.sendMessage(to,content,signNum);
				 if(result==null){
					 LogSystem.info("sendid="+msgBean.getSendId()+"发送失败！");
					 msgBean.setStatus(MsgSendLog.ST_FAIL);
					 msgBean.setResTime(new Date());
					 messageQueueService.pushResMsg(msgBean);//响应失败消息
				 }else{
					 LogSystem.info("sendid="+msgBean.getSendId()+"发送成功！网关返回的,msgid="+result[0]+",code="+result[1]);
					 msgBean.setStatus(MsgSendLog.ST_SUCCESS);
					 msgBean.setMsgId(result[0]);
					 msgBean.setResCode(result[1]);
					 msgBean.setResTime(new Date());
					 messageQueueService.pushResMsg(msgBean);//响应成功消息
				 }
				} catch (Exception e) {
					LogSystem.error(e, "处理消息发生异常");
				}
			}
		});
	}
	
	@Override
	public int cpOrder() {
		return 0;
	}

	@Override
	public void shutdown() throws Exception {
	}
}
