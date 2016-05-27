package com.aspire.cmppsgw;

import org.apache.log4j.Logger;

/**
 * 
 *  ganhua 2012-07-04  
 *      增加属性longSmsSeq
 *      新增方法priavate synchronized static int getNextSmsSeq
 *      重载方法enCodeBytes 
 *      修改方法getMultiSMSBinaryData/ fillHead
 *
 */

public class LongSMSUtil {

	private static final Logger log = Logger.getLogger(LongSMSUtil.class);

	// ucs2编码
	private static final String CHARSET_UCS2 = "UTF-16BE";

	// 最后一条短信的长度（预留企业签名的长度）
	protected static final int SMSMAXLENGTH_LAST = 139-15;
	// 每条短信的最大字节数
	protected static final int SMSMAXLENGTH = 140;
	// private static final int SMSMAXLENGTH = 140;

	// 消息头
	//private static final byte[] MSG_HEAD = { 0x05, 0x00, 0x03, 0x00, 0x00, 0x00 };
	 private static final byte[] MSG_HEAD = { 0x05, 0x00, 0x03, 0x09, 0x00, 0x01 };

	// 消息头长度
	private static final int LENGTH_MSG_HEAD = MSG_HEAD.length;
	// private static final int LENGTH_MSG_HEAD = 6;

	protected static final int MaxExtBodyLen = SMSMAXLENGTH - LENGTH_MSG_HEAD;
	
	protected static final int MaxExtBodyLen_last = SMSMAXLENGTH_LAST - LENGTH_MSG_HEAD;

	// 信息格式 0：ASCII串 3：短信写卡操作 4：二进制信息8：UCS2编码15：含GB汉字
	private static final int Msg_Fmt = 8;

	
	// 接收短信的用户的号码类型，0：真实号码；1：伪码
	private static final int Dest_Terminal_Type = 0;

	// 被计费用户的号码类型 0：真实号码；1：伪码
	private static final int Fee_Terminal_Type = 0;

	// 点播业务使用的LinkID，非点播类业务的MT流程不使用该字段
	private static final String LinkID = "";

	/**
	 * 长短信序列号
	 */
	private static int longSmsSeq = 0;




	public static byte[][] enCodeBytes(String longSMS) {

		byte dlDataByte[] = null;
		try {
			dlDataByte = longSMS.getBytes(CHARSET_UCS2);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return getMultiSMSBinaryData(dlDataByte,0);
	}
	
	/**
	 * 重载方法enCodeBytes
	 * ganhua 2012-07-04 
	 * @param longSMS
	 * @return
	 */
	public static byte[][] enCodeBytes(String longSMS,boolean seqFlag) {

		byte dlDataByte[] = null;
		try {
			dlDataByte = longSMS.getBytes(CHARSET_UCS2);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		int seq = 0;
		if(seqFlag){
			seq = getNextSmsSeq();
		}
		
		return getMultiSMSBinaryData(dlDataByte,seq);
	}
	
	/**
	 * 取得长短信总编号
	 * ganhua 2012-07-04
	 * @return
	 */
	private synchronized static int getNextSmsSeq(){
		if(longSmsSeq==255){
			longSmsSeq = 0;
		}
		return ++longSmsSeq;
	}

	/**
	 * 分拆一条cmpp二进制数据为符合长度要求的若干条二进制数据
	 * 
	 * @return 若干条二进制数据
	 */
	private static byte[][] getMultiSMSBinaryData(byte[] oneBigSMS,int smsSeq) {
		log.debug("***************getMultiSMSBinaryData begin*************");
		// byte[] oneBigSMS = GetSMSBinaryData();
		log.debug("oneBigSMS.length  = " + oneBigSMS.length);
		byte[][] results;
		// 需要对刚好整除情况处理，所以用ceil（）表示上约，注意ceil（）需要传入double参数才能得到正确结果
		int totalSMSAmount = (oneBigSMS.length <= SMSMAXLENGTH) ? 1 : ((int) Math.ceil((double) (oneBigSMS.length)
				/ MaxExtBodyLen));
		// ((oneBigSMS.length-UDH_LENGTH) / (SMSMAXLENGTH-UDH_EXT_LENGTH) + 1);
		 //判断最后一条短信的长度，如果>SMSMAXLENGTH_LAST则需拆分为两条短信
        boolean isSplit = false;
        if(oneBigSMS.length - MaxExtBodyLen*(totalSMSAmount -1 )>MaxExtBodyLen_last)
        {
        	totalSMSAmount++;
        	isSplit = true;
        }
		log.debug("totalSMSAmount  = " + totalSMSAmount);
		results = new byte[totalSMSAmount][]; // a SMS per row
		// 单包
		if (totalSMSAmount == 1) {
			log.debug("Single SMS Packet, packet length is : " + oneBigSMS.length);
			results[0] = new byte[oneBigSMS.length + LENGTH_MSG_HEAD];
			// results[0] = oneBigSMS;
			fillHead(results[0], 1, 1,smsSeq);
			System.arraycopy(oneBigSMS, 0, results[0], LENGTH_MSG_HEAD, oneBigSMS.length);
			// fillBody(oneBigSMS, 0, results[0], 0, oneBigSMS.length);
			log.debug("*************Single SMS Packet finished ************");
			return results;
		}
		// 多包
		for (int j = 0; j < totalSMSAmount; j++) {
            int length = 0;
        	
        	//如果为分拆，则倒数第二条长度为SMSMAXLENGTH_LAST，最后一条的算法也需要变动
        	if(isSplit)
        	{
        		if(j==totalSMSAmount -2)
        		{
        			length = SMSMAXLENGTH_LAST;
        		}else if(j==totalSMSAmount -1)
        		{
        			length = oneBigSMS.length - MaxExtBodyLen * (j-1) - MaxExtBodyLen_last  + LENGTH_MSG_HEAD;
        		}else {
					length = SMSMAXLENGTH;
				}
        	}
        	else{
                // 根据是否最后一个包判定包长度
                length = (j != (totalSMSAmount - 1)) ? SMSMAXLENGTH
                    : (oneBigSMS.length - MaxExtBodyLen * j + LENGTH_MSG_HEAD);
        	}
			log.debug("length of SMS packet [ " + j + " ] is " + length);
			results[j] = new byte[length];
			fillHead(results[j], totalSMSAmount, j + 1,smsSeq);
			 //如果为分拆模式，则计算数组位置要注意，倒数第一条短信长度为MaxExtBodyLen_last
            if(isSplit&&j==totalSMSAmount-1)
            {
            	System.arraycopy(oneBigSMS, MaxExtBodyLen * (j-1)+MaxExtBodyLen_last, results[j],
                        LENGTH_MSG_HEAD, length - LENGTH_MSG_HEAD);
            }else {
            	System.arraycopy(oneBigSMS, MaxExtBodyLen * j, results[j],
                        LENGTH_MSG_HEAD, length - LENGTH_MSG_HEAD);
			}
			// ByteTool.printHex(results[j], log);
		}
		log.debug("*******************getMultiSMSBinaryData finished.(Multiple SMS packet) ****************");
		return results;
	}

	private static void fillHead(byte[] result, int totalSMSAmount, int number,int smsSeq) {
		log.debug("*******************fillUDH begin*******************");
		if ((number > totalSMSAmount) || (number <= 0)) {
			log.error("can't generate UDH correctly.");
		}
		System.arraycopy(MSG_HEAD, 0, result, 0, LENGTH_MSG_HEAD); // 固定的长短信消息头
		
		if(smsSeq>0){
			result[LENGTH_MSG_HEAD - 3] = (byte) smsSeq; // 长短信总编号
		}
		
		result[LENGTH_MSG_HEAD - 2] = (byte) totalSMSAmount; // 长短信消息总条数
		result[LENGTH_MSG_HEAD - 1] = (byte) number; // 长短信消息序号
		log.debug("*******************fillUDH finished*******************");
	}

}
