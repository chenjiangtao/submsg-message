package com.aspire.cmppsgw.util;

public class HexUtil {

	public static byte[] strToHexByte(String hexString){
//		hexString = hexString.replaceAll(" ", "");
//		if((hexString.length()%2)!=0){
//			hexString+="";
//		}
		byte[] returnBytes = new byte[hexString.length() / 2];
		for (int i = 0; i < returnBytes.length; i++) {
			returnBytes[i] = (byte) Integer.parseInt(
					hexString.substring(2 * i, 2 * i + 2), 16);
		}
		return returnBytes;
	}
}
