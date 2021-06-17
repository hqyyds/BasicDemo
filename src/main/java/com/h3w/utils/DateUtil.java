package com.h3w.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {


	public static Date addDay(Date date,Integer day){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR,cal.get(Calendar.DAY_OF_YEAR)+day);
		return cal.getTime();
	}
	/**
	 * 获取天数
	 * @param date
	 * @return
     */
	public static Integer getDay(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day=c.get(Calendar.DATE);
		return day;
	}

	public static Integer getMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month=c.get(Calendar.MONTH);
		return month + 1;
	}

	public static Integer getYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year=c.get(Calendar.YEAR);
		return year;
	}
	/**
	 * 转换成数字格式
	 * @param date
	 * @return
     */
	public static String formatDateToNumber(Date date) {
		String szTemp = "";
		SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		szTemp = oSimpleDateFormat.format(date);
		return szTemp;
	}

	/**
	 * 获取后一天
	 * @param specifiedDay
	 * @return
	 */
	public static Date getNextDay(Date specifiedDay){
		Calendar c = Calendar.getInstance();
		c.setTime(specifiedDay);
		int day=c.get(Calendar.DATE);
		c.set(Calendar.DATE,day+1);
		return c.getTime();
	}

	/**
	 * 当前年的开始时间，即2012-01-01 00:00:00
	 *
	 * @return
	 */
	public  static Date getCurrentYearStartTime(Date t) {
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = new GregorianCalendar();
//		Calendar c = Calendar.getInstance();
		c.setTime(t);
		Date now = null;
		try {
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			now = shortSdf.parse(shortSdf.format(c.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 按格式转换成时间
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date parseDate(String date,String format){
        
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
/**
 * 将字符串转换成时间
 * @param date
 * @return
 */
	public static Date parseDate(String date){
        
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	public static String formatDate(Date date){
        if(date==null)return null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	public static String formatDate(Date date,String format){
		if(date==null)return null;
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	/**
	 * 获得当前时间，格式yyyy-MM-dd hh:mm:ss
	 * @param format
	 * @return
	 */
	public static String getCurrentDate(){
		return getCurrentDate("yyyy-MM-dd hh:mm:ss");
	}
	/**
	 * 获得当前时间，格式自定义
	 * @param format
	 * @return
	 */
	public static String getCurrentDate(String format){
		Calendar day=Calendar.getInstance(); 
		day.add(Calendar.DATE,0); 
		SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
		String date = sdf.format(day.getTime());
		return date;
	}
	/**
	 * 获得昨天时间，格式自定义
	 * @param format
	 * @return
	 */
	public static String getYesterdayDate(String format){
		Calendar day=Calendar.getInstance(); 
		day.add(Calendar.DATE,-1); 
		SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
		String date = sdf.format(day.getTime());
		return date;
	}
	 /**  
      * @param date1 需要比较的时间 不能为空(null),需要正确的日期格式 ,如：2009-09-12 
      * @param date2 被比较的时间  为空(null)则为当前时间  
      * @param stype 返回值类型   0为多少天，1为多少个月，2为多少年  
      * @return  
      * 举例：
      * compareDate("2009-09-12", null, 0);//比较天
      * compareDate("2009-09-12", null, 1);//比较月
      * compareDate("2009-09-12", null, 2);//比较年
      */ 
     public static int compareDate(String startDay,String endDay,int stype){  
         int n = 0;  
         String[] u = {"天","月","年"};  
         String formatStyle = stype==1?"yyyy-MM":"yyyy-MM-dd";  
           
         endDay = endDay==null?getCurrentDate("yyyy-MM-dd"):endDay;  
           
         DateFormat df = new SimpleDateFormat(formatStyle);  
         Calendar c1 = Calendar.getInstance();  
         Calendar c2 = Calendar.getInstance();  
         try {  
             c1.setTime(df.parse(startDay));  
             c2.setTime(df.parse(endDay));
         } catch (Exception e3) {  
             System.out.println("wrong occured");  
         }  
         //List list = new ArrayList();  
         while (!c1.after(c2)) {                   // 循环对比，直到相等，n 就是所要的结果  
             //list.add(df.format(c1.getTime()));    // 这里可以把间隔的日期存到数组中 打印出来  
             n++;  
             if(stype==1){  
                 c1.add(Calendar.MONTH, 1);          // 比较月份，月份+1  
             }  
             else{  
                 c1.add(Calendar.DATE, 1);           // 比较天数，日期+1  
             }  
         }  
         n = n-1;  
         if(stype==2){  
             n = n /365;
         }     
//         System.out.println(startDay+" -- "+endDay+" 相差多少"+u[stype]+":"+n);        
         return n;  
     }
     /**
      * 判断时间是否符合时间格式
      */
	public static boolean isDate(String date, String dateFormat) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			format.setLenient(false);
			try {
				format.format(format.parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return true;
		}
		return false;
	}
	 /**
	  * 实现给定某日期，判断是星期几
	  * date:必须yyyy-MM-dd格式
	  */
	 public static String getWeekday(String date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");  
     SimpleDateFormat sdw = new SimpleDateFormat("E");  
     Date d = null;  
     try {  
         d = sd.parse(date);  
     } catch (ParseException e) {  
         e.printStackTrace();  
     }
     return sdw.format(d);
	 }
	 /**
	  * 用来全局控制 上一周，本周，下一周的周数变化
	  */
	private static int weeks = 0;
	/**
	 * 获得当前日期与本周一相差的天数
	 */
	 private static int getMondayPlus() {
	     Calendar cd = Calendar.getInstance();
	     // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
	     int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
	     if (dayOfWeek == 1) {
	         return -6;
	     } else {
	         return 2 - dayOfWeek;
	     }
	 }
	 /**
	  * 获得本周星期一的日期
	  */
	 public static String getCurrentMonday(String format) {
	     weeks = 0;
	     int mondayPlus = getMondayPlus();
	     Calendar currentDate=Calendar.getInstance();
	     currentDate.add(Calendar.DATE, mondayPlus);
	     SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
	     String date = sdf.format(currentDate.getTime());
	     return date;
	 }
	 /**
	  * 获得上周星期一的日期
	  */
	 public static String getPreviousMonday(String format) {
	     weeks--;
	     int mondayPlus = getMondayPlus();
	     Calendar currentDate=Calendar.getInstance();
	     currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
	     SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
	     String date = sdf.format(currentDate.getTime());
	     return date;
	 }
	 /**
	  * 获得下周星期一的日期
	  */
	 public static String getNextMonday(String format) {
	     weeks++;
	     int mondayPlus = getMondayPlus();
	//     GregorianCalendar currentDate = new GregorianCalendar();
	     Calendar currentDate=Calendar.getInstance();
	     currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
	     SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
	     String date = sdf.format(currentDate.getTime());
	     return date;
	 }
	 /**
	  * 获得相应周的周日的日期
	  * 此方法必须写在getCurrentMonday，getPreviousMonday或getNextMonday方法之后
	  */
	 public static String getSunday(String format) {
	     int mondayPlus = getMondayPlus();
	     Calendar currentDate=Calendar.getInstance();
	     currentDate.add(Calendar.DATE, mondayPlus + 7 * weeks + 6);
	     SimpleDateFormat sdf=new SimpleDateFormat(format);//"yyyy-MM-dd"
	     String date = sdf.format(currentDate.getTime());
	     return date;
	 }


/**
	  *method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
	  *@param dateString 需要转换为timestamp的字符串
	  *@return dataTime timestamp
	  */
	public final static Timestamp string2Time(String dateString) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);// 设定格式
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		java.sql.Timestamp dateTime = new java.sql.Timestamp(date.getTime());
		return new Timestamp(date.getTime());// Timestamp类型,timeDate.getTime()返回一个long型
	}

	/**
	 *method 将字符串类型的日期转换为一个Date（java.sql.Date）
	 * 
	 * @param dateString
	 *            需要转换为Date的字符串
	 *@return dataTime Date
	 */
	public final static java.sql.Date string2Date(String dateString) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		java.sql.Date dateTime = new java.sql.Date(date.getTime());// sql类型
		return new java.sql.Date(date.getTime());
	}
	public final static Timestamp string2TimeStamp(String dateString) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		dateString=df.format(dateString);
		Timestamp ts = Timestamp.valueOf(dateString);
		return ts;
	}
	
	public final static java.sql.Date string2Date(String dateString,String format) {
		DateFormat dateFormat;
		dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		java.sql.Date dateTime = new java.sql.Date(date.getTime());// sql类型
		return new java.sql.Date(date.getTime());
	}

//记录考勤， 记录迟到、早退时间
public static String getState() {   
        String state = "正常";   
        DateFormat df = new SimpleDateFormat("HH:mm:ss");   
        Date d = new Date();   
        try {   
            Date d1 = df.parse("08:00:00");   
            Date d2 = df.parse(df.format(d));   
            Date d3 = df.parse("18:00:00");   
  
            int t1 = (int) d1.getTime();   
            int t2 = (int) d2.getTime();   
            int t3 = (int) d3.getTime();   
            if (t2 < t1) {   
  
                long between = (t1 - t2) / 1000;// 除以1000是为了转换成秒   
                long hour1 = between % (24 * 3600) / 3600;   
                long minute1 = between % 3600 / 60;   
  
                state = "迟到 ：" + hour1 + "时" + minute1 + "分";   
  
            } else if (t2 < t3) {   
                long between = (t3 - t2) / 1000;// 除以1000是为了转换成秒   
                long hour1 = between % (24 * 3600) / 3600;   
                long minute1 = between % 3600 / 60;   
                state = "早退 ：" + hour1 + "时" + minute1 + "分";   
            }   
            return state;   
        } catch (Exception e) {   
            return state;   
        }   
  
    } 

/**
 * 是否是今天
 * 
 * @param date
 * @return
 */
public static boolean isToday(final Date date) {
        return isTheDay(date, new Date());
}
/**
 * 是否是指定日期
 * 
 * @param date
 * @param day
 * @return
 */
public static boolean isTheDay(final Date date, final Date day) {
		if(date==null || day==null)return false;
        return date.getTime() >= DateUtil.dayBegin(day).getTime()
                        && date.getTime() <= DateUtil.dayEnd(day).getTime();
}
/**
 * 获取指定时间的那天 00:00:00.000 的时间
 * 
 * @param date
 * @return
 */
public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
}
/**
 * 获取指定时间的那天 23:59:59.999 的时间
 * 
 * @param date
 * @return
 */
public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
}

	/**
	 * 获得该月第一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getFirstDayOfMonth(int year, int month){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		//格式化日期
		return cal.getTime();
	}

	/**
	 * 获得该月最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(int year, int month){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		//格式化日期
		return cal.getTime();
	}
	/**
 * 获取某月最后一天
 * @param date
 * @return
 */
public static Date getLastDayOfMonth(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	// 设置日期为本月最大日期  
	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
	return calendar.getTime();
}
/**
 * 获取某月第一天
 * @param date
 * @return
 */
public static Date getFirstDayOfMonth(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	// 设置日期为本月最小日期  
	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
	return calendar.getTime();
}
/**
 * 获取某周最后一天
 * @param date
 * @return
 */
public static Date getLastDayOfWeek(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	// 设置日期为本月最大日期  
	calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); 
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
	return calendar.getTime();
}
/**
 * 获取某周第一天
 * @param date
 * @return
 */
public static Date getFirstDayOfWeek(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	// 设置日期为本周最小日期  
	calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
	return calendar.getTime();
}
/**
 * 获取某天最后时间
 * @param date
 * @return
 */
public static Date getLastTimeOfDay(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
	return calendar.getTime();
}
/**
 * 获取某天开始时间
 * @param date
 * @return
 */
public static Date getFirstTimeOfDay(Date date){
	if(date == null)date = new Date();
	Calendar calendar = Calendar.getInstance();  
	// 设置时间,当前时间不用设置  
	calendar.setTime(date);  
	calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
	calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
	calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
	return calendar.getTime();
}


	/**
	 * 获取上个月
	 * @param specifiedDay
	 * @return
	 */
	public static Date getLastMonth(Date specifiedDay){
		Calendar c = Calendar.getInstance();
		c.setTime(specifiedDay);
		c.add(Calendar.MONTH,-1);
		return c.getTime();
	}
	/**
	 * 获取下个月
	 * @param specifiedDay
	 * @return
	 */
	public static Date getNextMonth(Date specifiedDay){
		Calendar c = Calendar.getInstance();
		c.setTime(specifiedDay);
		c.add(Calendar.MONTH,1);
		return c.getTime();
	}
	/**
	 * 获取年度的第一天
	 * @param year
	 * @return
	 */
	public static Date getFirstDayOfYear(int year){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
		//获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		//格式化日期
		return cal.getTime();
	}

	/**
	 * 获取年度的最后一天
	 * @param year
	 * @return
	 */
	public static Date getLastDayOfYear(int year){
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR,year);
		//设置月份
		cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		//格式化日期
		return cal.getTime();
	}

	/**
	 * 当前季度的开始时间，即2012-01-1 00:00:00
	 *
	 * @return
	 */
	public  static Date getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 4);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			return c.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前季度的结束时间，即2012-03-31 23:59:59
	 *
	 * @return
	 */
	public static  Date getCurrentQuarterEndTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3) {
				c.set(Calendar.MONTH, 2);
				c.set(Calendar.DATE, 31);
			} else if (currentMonth >= 4 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 7 && currentMonth <= 9) {
				c.set(Calendar.MONTH,8);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 10 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
			return c.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        
        //System.out.println(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		String date = "2015-4-31";
		Date xx = DateUtil.parseDate(date,"yyyy-MM-dd");
		System.out.println(DateUtil.formatDate(xx));
	}

}

