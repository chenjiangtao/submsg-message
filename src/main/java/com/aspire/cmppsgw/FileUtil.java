package com.aspire.cmppsgw;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getFileLineCount(GlobalEnv.getInstance().getValue("file.path"));
	}
	
	public static int getFileLineCount(String filepath){
    	try {
			if(System.getProperty("file.separator").equals("/")){
				Process process = Runtime.getRuntime().exec("wc -l "+filepath);
				int status = process.waitFor();
				InputStream in = process.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				String result = read.readLine();
				String[] strs = result.split(" ");			
				System.out.println("status:"+status+",line count:"+strs[0]);
				return Integer.parseInt(strs[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 500;
    }

}
