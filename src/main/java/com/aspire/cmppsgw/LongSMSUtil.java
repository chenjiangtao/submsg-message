package com.aspire.cmppsgw;

import org.apache.log4j.Logger;

/**
 * 
 *  ganhua 2012-07-04  
 *      ��������longSmsSeq
 *      ��������priavate synchronized static int getNextSmsSeq
 *      ���ط���enCodeBytes 
 *      �޸ķ���getMultiSMSBinaryData/ fillHead
 *
 */

public class LongSMSUtil {

	private static final Logger log = Logger.getLogger(LongSMSUtil.class);

	// ucs2����
	private static final String CHARSET_UCS2 = "UTF-16BE";

	// ���һ�����ŵĳ��ȣ�Ԥ����ҵǩ���ĳ��ȣ�
	protected static final int SMSMAXLENGTH_LAST = 139-15;
	// ÿ�����ŵ�����ֽ���
	protected static final int SMSMAXLENGTH = 140;
	// private static final int SMSMAXLENGTH = 140;

	// ��Ϣͷ
	//private static final byte[] MSG_HEAD = { 0x05, 0x00, 0x03, 0x00, 0x00, 0x00 };
	 private static final byte[] MSG_HEAD = { 0x05, 0x00, 0x03, 0x09, 0x00, 0x01 };

	// ��Ϣͷ����
	private static final int LENGTH_MSG_HEAD = MSG_HEAD.length;
	// private static final int LENGTH_MSG_HEAD = 6;

	protected static final int MaxExtBodyLen = SMSMAXLENGTH - LENGTH_MSG_HEAD;
	
	protected static final int MaxExtBodyLen_last = SMSMAXLENGTH_LAST - LENGTH_MSG_HEAD;

	// ��Ϣ��ʽ 0��ASCII�� 3������д������ 4����������Ϣ8��UCS2����15����GB����
	private static final int Msg_Fmt = 8;

	
	// ���ն��ŵ��û��ĺ������ͣ�0����ʵ���룻1��α��
	private static final int Dest_Terminal_Type = 0;

	// ���Ʒ��û��ĺ������� 0����ʵ���룻1��α��
	private static final int Fee_Terminal_Type = 0;

	// �㲥ҵ��ʹ�õ�LinkID���ǵ㲥��ҵ���MT���̲�ʹ�ø��ֶ�
	private static final String LinkID = "";

	/**
	 * ���������к�
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
	 * ���ط���enCodeBytes
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
	 * ȡ�ó������ܱ��
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
	 * �ֲ�һ��cmpp����������Ϊ���ϳ���Ҫ�������������������
	 * 
	 * @return ����������������
	 */
	private static byte[][] getMultiSMSBinaryData(byte[] oneBigSMS,int smsSeq) {
		log.debug("***************getMultiSMSBinaryData begin*************");
		// byte[] oneBigSMS = GetSMSBinaryData();
		log.debug("oneBigSMS.length  = " + oneBigSMS.length);
		byte[][] results;
		// ��Ҫ�Ըպ������������������ceil������ʾ��Լ��ע��ceil������Ҫ����double�������ܵõ���ȷ���
		int totalSMSAmount = (oneBigSMS.length <= SMSMAXLENGTH) ? 1 : ((int) Math.ceil((double) (oneBigSMS.length)
				/ MaxExtBodyLen));
		// ((oneBigSMS.length-UDH_LENGTH) / (SMSMAXLENGTH-UDH_EXT_LENGTH) + 1);
		 //�ж����һ�����ŵĳ��ȣ����>SMSMAXLENGTH_LAST������Ϊ��������
        boolean isSplit = false;
        if(oneBigSMS.length - MaxExtBodyLen*(totalSMSAmount -1 )>MaxExtBodyLen_last)
        {
        	totalSMSAmount++;
        	isSplit = true;
        }
		log.debug("totalSMSAmount  = " + totalSMSAmount);
		results = new byte[totalSMSAmount][]; // a SMS per row
		// ����
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
		// ���
		for (int j = 0; j < totalSMSAmount; j++) {
            int length = 0;
        	
        	//���Ϊ�ֲ������ڶ�������ΪSMSMAXLENGTH_LAST�����һ�����㷨Ҳ��Ҫ�䶯
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
                // �����Ƿ����һ�����ж�������
                length = (j != (totalSMSAmount - 1)) ? SMSMAXLENGTH
                    : (oneBigSMS.length - MaxExtBodyLen * j + LENGTH_MSG_HEAD);
        	}
			log.debug("length of SMS packet [ " + j + " ] is " + length);
			results[j] = new byte[length];
			fillHead(results[j], totalSMSAmount, j + 1,smsSeq);
			 //���Ϊ�ֲ�ģʽ�����������λ��Ҫע�⣬������һ�����ų���ΪMaxExtBodyLen_last
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
		System.arraycopy(MSG_HEAD, 0, result, 0, LENGTH_MSG_HEAD); // �̶��ĳ�������Ϣͷ
		
		if(smsSeq>0){
			result[LENGTH_MSG_HEAD - 3] = (byte) smsSeq; // �������ܱ��
		}
		
		result[LENGTH_MSG_HEAD - 2] = (byte) totalSMSAmount; // ��������Ϣ������
		result[LENGTH_MSG_HEAD - 1] = (byte) number; // ��������Ϣ���
		log.debug("*******************fillUDH finished*******************");
	}

}
