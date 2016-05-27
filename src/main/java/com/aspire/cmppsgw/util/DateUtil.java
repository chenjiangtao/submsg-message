package com.aspire.cmppsgw.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
	
	
	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

		String result = sdf.format(new Date());

		return result;
	}
	
	public static long getCurrentFullTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		String result = sdf.format(new Date());

		return Long.valueOf(result);
	}

    /**
     * 得到当前日期
     *
     * @return String 当前日期 yyyy-MM-dd HH:mm:ss格式
     * @author kevin
     */
    public static String getCurDateTime() {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        //String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(now.getTime()));
    }
    
    /**
     * 得到当前日期
     *
     * @return String 当前日期 yyyyMMdd格式
     * @author kevin
     */
    public static String getCurDateYYYYMMDD() {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        String DATE_FORMAT = "yyyyMMdd";
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        return (sdf.format(now.getTime()));
    }

    /**
     * 是否是今天
     *
     * @param strDate yyyy-MM-dd
     * @return
     */
    public static boolean isCurrentDay(String strDate) {
        boolean bRet = false;
        try {
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            //String DATE_FORMAT = "yyyy-MM-dd";
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getDefault());
            Date date1 = sdf.parse(strDate);
            String strDate1 = sdf.format(date1);
            String strDate2 = sdf.format(now.getTime());
            bRet = strDate1.equalsIgnoreCase(strDate2);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return bRet;
    }
    
    /**
     * 是否是今天
     *
     * @param strDate yyyy-MM-dd
     * @return
     */
    public static boolean isCurrentDay(Date dt) {
        boolean bRet = false;
        Calendar now = Calendar.getInstance(TimeZone.getDefault());

		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getDefault());
		String strDate1 = sdf.format(dt);
		String strDate2 = sdf.format(now.getTime());
		bRet = strDate1.equalsIgnoreCase(strDate2);
        return bRet;
    }

    /**
     * 获取几小时后的时间
     *
     * @param hour   小时
     * @param format hh:mm:ss
     * @return HH:MM:SS
     */
    public static String getAfterDateTime(int hour, String format) {
        long lTime = new Date().getTime() + hour * 60 * 60 * 1000;
        Calendar calendar = new GregorianCalendar();
        java.util.Date date_now = new java.util.Date(lTime);
        calendar.setTime(date_now);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        java.util.Date date = calendar.getTime();
        return sdf.format(date);
    }

    /**
     * 得到当前日期
     *
     * @param format日期格式
     * @return String 当前日期  format日期格式
     * @author kevin
     */
    public static String getCurDateTime(String format) {
        try {
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getDefault());
            return (sdf.format(now.getTime()));
        } catch (Exception e) {
            return getCurDateTime(); // 如果无法转化，则返回默认格式的时间。
        }
    }
    
    /**
     * 得到时间戳
     *
     * @param null
     * @return String 当前日期时间戳(yyyyMMddHHmmssSSSS)
     * @author kevin
     */
    public static String getTimeStamp() {
        try {
            Calendar now = Calendar.getInstance(TimeZone.getDefault());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            sdf.setTimeZone(TimeZone.getDefault());
            return (sdf.format(now.getTime()));
        } catch (Exception e) {
            return getCurDateTime(); // 如果无法转化，则返回默认格式的时间。
        }
    }

    /**
     * 日期转字符串
     *
     * @return String
     * @author kevin
     */
    public static String parseDateToString(Date thedate, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(thedate.getTime());
    }

    //parseDateToString(Date thedate, String format)的重载方法
    public static String parseDateToString(Date thedate) {
        //String format = "yyyy-MM-dd";
        return parseDateToString(thedate, "yyyy-MM-dd");
    }

    /**
     * 字符串转日期
     *
     * @return Date
     * @author kevin
     */
    public static Date parseStringToDate(String thedate, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        Date dd1 = null;
        try {
            dd1 = sdf.parse(thedate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dd1;
    }

    /**
     * 由String型日期转成format形式String
     *
     * @param format1原先格式
     * @param format2转化格式
     * @return String
     * @author kevin
     */
    public static String changeFormatDateString(String format1, String format2, String strDate) {
        if (strDate == null) return "";
        if (strDate.length() >= format1.length() && format1.length() >= format2.length()) {
            return parseDateToString(parseStringToDate(strDate, format1), format2);
        }
        return strDate;
    }

    /**
     * 得到N天后的日期
     *
     * @param theDate某日期 格式 yyyy-MM-dd
     * @param nDayNum    N天
     * @return String N天后的日期
     * @author kevin
     */
    public static String afterNDaysDate(String theDate, String nDayNum, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dd1 = sdf.parse(theDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dd1);
            cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(nDayNum));
            sdf.setTimeZone(TimeZone.getDefault());
            return (sdf.format(cal.getTime()));
        } catch (Exception e) {
            return getCurDateTime(format); // 如果无法转化，则返回默认格式的时间。
        }
    }

    /**
     * 得到N小时后的日期
     *
     * @param theDate某日期 格式传入传出格式都是 format格式
     * @param nDayNum    N小时
     * @return String N小时后的日期
     * @author kevin
     */
    public static String afterNHoursDate(String theDate, String nHourNum, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dd1 = sdf.parse(theDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dd1);
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(nHourNum));
            sdf.setTimeZone(TimeZone.getDefault());
            return (sdf.format(cal.getTime()));
        } catch (Exception e) {
            return getCurDateTime(format); // 如果无法转化，则返回默认格式的时间。
        }
    }

    /**
     * 得到N分钟后的日期
     *
     * @param theDate某日期 格式 yyyy-MM-dd
     * @param nDayNum    N分钟
     * @return String N分钟后的日期
     * @author kevin
     */
    public static String afterNMinsDate(String theDate, String nMinNum, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date dd1 = sdf.parse(theDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dd1);
            cal.add(Calendar.MINUTE, Integer.parseInt(nMinNum));
            sdf.setTimeZone(TimeZone.getDefault());
            return (sdf.format(cal.getTime()));
        } catch (Exception e) {
            return getCurDateTime(format); // 如果无法转化，则返回默认格式的时间。
        }
    }

    //比较两个字符串格式日期大小,带格式的日期
    public static boolean isBefore(String strdat1, String strdat2, String format) {
        try {
            Date dat1 = parseStringToDate(strdat1, format);
            Date dat2 = parseStringToDate(strdat2, format);
            return dat1.before(dat2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //比较两个字符串格式日期大小,默认转换格式:yyyy-MM-dd
    public static boolean isBefore(String strdat1, String strdat2) {
        //String format = "yyyy-MM-dd";
        return isBefore(strdat1, strdat2, "yyyy-MM-dd");
    }

    /**
     * 获取休息时间
     *
     * @param strTime strTime=" 3:00:00"; 需要休息的时间，注意有空格
     * @return 需要休息的时间
     * @throws ParseException
     */
    public static long getSleepTime(String strTime) throws ParseException {
        long p = 1;
        long l_date = System.currentTimeMillis();
        java.util.Date date_now = new java.util.Date(l_date);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = fmt.format(date_now) + strTime;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (date_now.before(df.parse(strDate)))
            p = (df.parse(strDate)).getTime() - l_date;
        else {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date_now);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            java.util.Date date = calendar.getTime();
            strDate = fmt.format(date) + strTime;
            p = (df.parse(strDate)).getTime() - l_date;
        }
        return p;
    }
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
      }

	public static String getHourMinute(String fullTime) {

		return fullTime.substring(11, 16);
	}
	//得到日期数组中最小日期
	public static String getMinDateOfArray(String dateArray[]){
		String dateTmp="";
		if(dateArray!=null){
			dateTmp=dateArray[0];
			for(int i=1;i<dateArray.length;i++){
				if(isBefore(dateArray[i],dateTmp,"yyyy-MM-dd")){
					dateTmp=dateArray[i];
				}
			}
		}
		return dateTmp;
	}
	//得到日期数组中最大日期
	public static String getMaxDateOfArray(String dateArray[]){
		String dateTmp="";
		if(dateArray!=null){
			dateTmp=dateArray[0];
			for(int i=1;i<dateArray.length;i++){
				if(isBefore(dateTmp,dateArray[i],"yyyy-MM-dd")){
					dateTmp=dateArray[i];
				}
			}
		}
		return dateTmp;
	}
	
	public static boolean hasNextDayInArray(String dateArray[],String dateTmp){
		int index=0;
		if(dateArray!=null){
			Arrays.sort(dateArray);
			for(int i=0;i<dateArray.length;i++){
				if(dateArray[i].equals(dateTmp)){
					index=i;
				}
			}
			System.out.println(index);
			return index+1!=dateArray.length;
		}
		return false;
	}
	
	public static boolean hasPreviousDayInArray(String dateArray[],String dateTmp){
		int index=0;
		if(dateArray!=null){
			Arrays.sort(dateArray);
			for(int i=0;i<dateArray.length;i++){
				if(dateArray[i].equals(dateTmp)){
					index=i;
				}
			}
			return index!=0;
		}
		return false;
	}
	/*
	 * 得到上一个月或者下一个月的日期
	 */
	public static String getDayafterMonth(String theDate,int month,String formatStr){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		 Date dat1 = parseStringToDate(theDate, formatStr);
		 now.setTime(dat1);
        sdf.setTimeZone(TimeZone.getDefault());
        now.add(Calendar.MONTH,month);
        return sdf.format(now.getTime());
	}
	
	/**
	 * 取指定日期上个月的最后一天
	 * @param yyyy-mm-dd
	 * @return
	 */
	 public static String getLastDayOfMonth(String yyyymmdd,String format) { 
		  int year=Integer.parseInt(yyyymmdd.substring(0, 4));
		  int month=Integer.parseInt(yyyymmdd.substring(5, 7));
		  System.out.println(year+","+month);
	       Calendar cal = Calendar.getInstance(); 
	        cal.set(Calendar.YEAR, year);// 年 
	        cal.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1 
	        cal.set(Calendar.DATE, 1);// 日，设为一号 
	        cal.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号 
	        cal.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天 
	        SimpleDateFormat sdf = new SimpleDateFormat(format);
	        return sdf.format(cal.getTime());// 获得月末是几号 
	    }
	 
		/**
		 * 得到上个月第一天的yyyy-mm-dd
		 * 
		 * @return
		 */
		public static String getFirstDayofLastMonth() {
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			cal.add(Calendar.MONTH, -1);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
			sdf.setTimeZone(TimeZone.getDefault());
			return (sdf.format(cal.getTime()));
		}
		
		
		/**
		 * 得到上个月最后一天的yyyy-MM-dd
		 * 
		 * @return
		 */
		public static String getLastDayOfLastMonth() {
			int year = Calendar.getInstance().get(Calendar.YEAR);

			//
			int month = Calendar.getInstance().get(Calendar.MONTH);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(getLastDayOfMonth(year, month));
		}
		
		
		/**
		 * 去上个月的月份yyyy-MM
		 * 
		 * @return
		 */
		public static String getLastMonth() {
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			cal.add(Calendar.MONTH, -1);
			DateFormat sdf = new SimpleDateFormat("yyyy-MM");
			sdf.setTimeZone(TimeZone.getDefault());
			return (sdf.format(cal.getTime()));
		}
		
		
		/**
		 * 取得某月的的最后一天
		 * 
		 * @param year
		 * @param month
		 *            (1~12)
		 * @return
		 */
		public static Date getLastDayOfMonth(int year, int month) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);// 年
			cal.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1
			// 设置该月实际的最后一天
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			return cal.getTime();// 获得月末是几号
		}
	 
	 
    public static void main(String args[]) {
//        DateUtil.isBefore("2006-09-15 18:55:00", "2006-09-15 19:24:49");
//        System.out.println(afterNDaysDate(DateUtil.getCurDateTime("yyyy-MM-dd"), "1", "yyyy-MM-dd"));
//        System.out.println(DateUtil.getCurDateTime("yyyy-MM-dd") + " 00:00:00");
//        System.out.println(afterNMinsDate("2006-08-23 00:33:15", "-60", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(parseStringToDate("2006-08-23 14:33:15","yyyy-MM-dd HH:mm:ss"));
//        System.out.println(changeFormatDateString("yyyy-MM-dd HH:mm:ss", "HH:mm", "2006-09-03 10:52:00.0"));
//		System.out.println(getCurDateTime("HH:mm"));
        //String arr[]={"2006-11-29","2006-11-22","2006-11-18","2006-11-24"};
    	System.out.println(getCurDateTime("yyyy-MM-dd"));
    	System.out.println(afterNDaysDate("2010-10-20","365","yyyy-MM-dd"));
        System.out.println(getLastDayOfMonth(afterNDaysDate(getCurDateTime("yyyy-MM-dd"),"365","yyyy-MM-dd"),"yyyy-MM-dd"));
    }
}
