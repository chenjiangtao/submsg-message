package com.yunpian.sdk;


import com.sr178.game.framework.log.LogSystem;
import com.yunpian.sdk.model.ResultDO;
import com.yunpian.sdk.model.SendSingleSmsInfo;
import com.yunpian.sdk.service.SmsOperator;
import com.yunpian.sdk.service.YunpianRestClient;


public class YPSendUtils {
	
	
	private static final YunpianRestClient client = new YunpianRestClient();
	
	public static String[] sendSms(String mobile,String content)  {
		LogSystem.info("使用云片进行发送,mobile=["+mobile+"],content=["+content+"]");
		try {
			 SmsOperator smsOperator = client.getSmsOperator();
		        // 单条发送
		        ResultDO<SendSingleSmsInfo> r1 =
		            smsOperator.singleSend(mobile, content);
		        String[] result = new String[2];
		        if(r1!=null){
		        	result[0] = r1.getData().getSid();
		        	result[1] = r1.getData().getCode()+"";
		        }
		        LogSystem.info("使用云片进行发送,结果为:"+r1);
		        return result;
		} catch (Exception e) {
			LogSystem.error(e, "");
			return null;
		}
       
//        System.out.println(r1);
        // 批量发送
//        ResultDO<SendBatchSmsInfo> r2 =
//            smsOperator.batchSend("13012312316,13112312312,123321,333,111", "【云片网】您的验证码是1234");
//        System.out.println(r2);

//        List<String> mobile =
//            Arrays.asList("13012312321,13012312322,13012312323,130123123".split(","));
//        List<String> text = Arrays.asList(
//            "【云片网】您的验证码是1234,【云片网】您的验证码是1234,【云片网】您的验证码是1234,【云片网】您的验证码是1234"
//                .split(","));
//        // 个性化发送
//        ResultDO<SendBatchSmsInfo> r3 = smsOperator.multiSend(mobile, text);
//        System.out.println(r3);

//        （不推荐使用）
//        String tpl_value = URLEncoder.encode("#code#", Config.ENCODING) + "=" + URLEncoder
//            .encode("1234", Config.ENCODING) + "&" + URLEncoder.encode("#company#", Config.ENCODING)
//            + "=" + URLEncoder.encode("云片网", Config.ENCODING);
//        // tpl batch send
//        ResultDO<SendBatchSmsInfo> r4 =
//            smsOperator.tplBatchSend("13200000000,13212312312,123321,333,111", "1", tpl_value);
//        System.out.println(r4);
//        // tpl single send
//        ResultDO<SendSingleSmsInfo> r5 =
//            smsOperator.tplSingleSend("15404450000", "1", tpl_value);
//        System.out.println(r5);
//        System.out.println(smsOperator.getRecord(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),"","",""));
//        System.out.println(smsOperator
//            .getRecord(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()),"","",""));

    }
	
	public static void main(String[] args) {
//		String content = "【积分游戏大平台】尊敬的zwy6688a1 玩家：您正在进行确认收款操作，验证码为：407011 ，如非本人操作请忽略";
//		String content = "【积分游戏大平台】您卖出的一币已打款，请在48小时内登录用户中心确认,否则将扣除您一颗信誉星。5天后未进行操作将暂封您的一币卖出功能！";
		//String content = "【积分游戏大平台】尊敬的胡先生/女士：恭喜您成为幸福100的理财客户，您所注册的账户资金为20000元(贰万圆整)，用户名为：hjy5218b1，祝福您跨入了财富自由的大门。[幸福100 2016/11/12]";
//		String content = "【幸福100】验证码：156872，用于注册操作（有效期30分钟）。";
//		YPSendUtils.sendSms("15919820372", content);
	}
}
