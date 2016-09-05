package com.submsg.message.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.miaodiyun.MDSendUtils;
import com.sr178.game.framework.log.LogSystem;
import com.sr178.game.framework.plugin.IAppPlugin;
import com.sr178.module.sms.entity.SubMailResult;
import com.sr178.module.sms.util.SubMailSendUtils;
import com.submsg.message.utils.MsgSendUtils;

import cn.submsg.member.bo.MsgSendLog;
import cn.submsg.message.bean.MsgBean;
import cn.submsg.message.service.MessageQueueService;
import cn.submsg.message.utils.MsgContentUtils;

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
						Thread.sleep(5000);
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
				 String[] result = null;
				 
				 //国内短信
				 if(to.indexOf("+")==-1){
					 if(msgBean.getSendType()==MsgContentUtils.SENDTYPE_SUBMAIL){
						 result = sendMsgBySubMail(to, msgBean.getTempId(), msgBean.getVars());
					 }else if(msgBean.getSendType()==MsgContentUtils.SENDTYPE_ZW){
						 result = MsgSendUtils.sendMessage(to,content,signNum);
					 }else if(msgBean.getSendType()==MsgContentUtils.SENDTYPE_MD){
						 result = MDSendUtils.sendMsg(to, content);
					 }
					 else{
						 throw new RuntimeException("不支持的短信发送渠道");
					 }
				 }else{//国外短信
					 throw new RuntimeException("暂时不支持的国际短信的发送");
				 }
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
	
	
	/**
	 * 直接通过submail来发送国内短信
	 * @param to
	 * @param tempId
	 * @param param
	 * @return
	 */
	private String[] sendMsgBySubMail(String to,String tempId,String vars){
		 Map<String,String> param = new HashMap<String,String>();
		 if(!Strings.isNullOrEmpty(vars)){
			 JSONObject jsonObject = JSON.parseObject(vars);
			 Set<Entry<String, Object>> sets = jsonObject.entrySet();
			 for (Entry<String, Object> entry : sets) {
				 param.put(entry.getKey(), entry.getValue().toString());
			 }
		 }
		SubMailResult result =  SubMailSendUtils.sendMessageForResult(to, tempId, param);
	    if(result!=null){
	    	return new String[]{result.getSend_id(),result.getStatus()};
	    }else{
	    	return null;
	    }
	}
	
	@Override
	public int cpOrder() {
		return 0;
	}

	@Override
	public void shutdown() throws Exception {
	}
}
