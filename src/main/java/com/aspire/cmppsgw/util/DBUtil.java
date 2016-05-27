package com.aspire.cmppsgw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

import com.aspire.cmppsgw.GlobalEnv;



/**
 * @desc
 * @author majiangtao@aspirehld.com
 * @date 2011-10-8
 * 
 */

public class DBUtil {
	private static Logger logger = LogAgent.systemInfoLogger;
	public static final String defaultDBType = "oracle";
	/**
	 * @param args
	 */
	public static void main(String[] r) {
		try {
			Connection conn = getConnection();
			close(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static boolean load = false;
	
	static void load(String DBType){
		String driverClassName="com.mysql.jdbc.Driver";
		if(DBType.equals("oracle")){
			driverClassName = "oracle.jdbc.driver.OracleDriver";
		}
		try {
			Class.forName(driverClassName);
			load = true;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static Connection getConnection() throws SQLException{
		return getConnection(GlobalEnv.getInstance().getValue("jdbc.url"),GlobalEnv.getInstance().getValue("jdbc.user"),GlobalEnv.getInstance().getValue("jdbc.pwd"));
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String url,String userName,String pwd) throws SQLException{
		return getConnection(defaultDBType, url, userName, pwd);
	}
	
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(String DBType,String url,String userName,String pwd) throws SQLException{
		
		if(!load){
			load(DBType);
		}
		try {
			return DriverManager.getConnection(url,userName,pwd);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
		
	}
	
	
	
	public static void close(ResultSet rs,Statement sta,Connection con){
		if(rs!=null) try{rs.close();}catch(Exception e){}
		if(sta!=null) try{sta.close();}catch(Exception e){}
		if(con!=null) try{con.close();}catch(Exception e){}
	}
	public static void close(Object o){
		try {
			if(o instanceof ResultSet){
				((ResultSet)o).close();
			}else if(o instanceof Statement){
				((Statement)o).close();
			}else if(o instanceof Connection){
				((Connection)o).close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void  printResultSet(ResultSet rs){
		StringBuffer sb=new 	StringBuffer();
		try {
			ResultSetMetaData rsmd=rs.getMetaData();
			int columnCount =rsmd.getColumnCount();
			for(int i=1;i<=columnCount;i++){
				sb.append(rsmd.getColumnName(i)+"\t");
			}
			sb.append("\n");
			while(rs.next()){
				for(int i=1;i<=columnCount;i++){
					sb.append(rs.getString(i)+"\t");
				}
				sb.append("\n");
			}	
			System.out.println(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
