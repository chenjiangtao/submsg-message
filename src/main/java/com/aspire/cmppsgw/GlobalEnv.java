/*
 * @ (#) GlobalEnv.java Jun 12, 2008
 * 
 * Copyright 2009 ASPire Information Technologies (Beijing) Ltd. All rights reserved.
 *
 */
package com.aspire.cmppsgw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.aspire.cmppsgw.util.LogAgent;

/**
 * 
 * 
 * <p>
 * 
 * </p>
 * 
 * @author wengyingjie
 */
public class GlobalEnv {
	Logger logger = LogAgent.systemInfoLogger;
	// signleton
	private static final GlobalEnv env = new GlobalEnv();

	private  Properties prop = null;
	
	private static List<String> list = null;
	


	private GlobalEnv() {
		loadProperties();
	}

	/**
	 *  * 
	 * @return 	 */
	public static GlobalEnv getInstance() {
		return env;
	}

	/**
	 * load sgw.properties
	 */
	private void loadProperties() {
		try {
			prop = new Properties();
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("resource.properties");
			prop.load(is);
		} catch (IOException e) {
			System.exit(0);
		}
	}
	

	public void reload() {
		this.loadProperties();
	}
	
	
	public String getValue(String key) {
		return getValue(key, "");
	}

	public String getValue(String key, String defaultVal) {
		String value = prop.getProperty(key);
		if (null == value || "".equalsIgnoreCase(value.trim()))
			return defaultVal;
		else
			return value.trim();
	}

	public int getIntValue(String key) {
		return getIntValue(key, 0);
	}

	public int getIntValue(String key, int defaultVal) {
		String value = prop.getProperty(key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		return defaultVal;
	}

	public boolean getBoolValue(String key, boolean defaultVal) {
		String value = prop.getProperty(key);
		try {
			return Boolean.parseBoolean(value);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
		return defaultVal;
	}
	//获取一个要下发的号码
	public String getMobile(){
		if(list==null || list.size() == 0){
			list = Arrays.asList(getValue("send.sms.mobile").split(","));
		}
		int random = (int) (Math.random() * list.size());
		return list.get(random);
	}



	

	
	

	
}
