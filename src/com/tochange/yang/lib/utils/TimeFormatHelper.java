package com.tochange.yang.lib.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.tochange.yang.lib.Utils;

/**
 * 日期格式转换类
 */
public class TimeFormatHelper {
    
    public static String getCurTimeToString(int i, int n)
    {
        Calendar c = Calendar.getInstance();
        if (c == null)
            return null;
        String time;
        String s1 = "-", s2 = ":", s3 = " ";
        c.add(Calendar.DATE, n);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int mSecond = c.get(Calendar.SECOND);

        time = "" + mYear;
        if (1 == i)
            time += s1;
        int mon = mMonth + 1;
        if (mon < 10)
            time = time + 0 + mon;
        else
            time += mon;
        if (1 == i)
            time += s1;
        if (mDay < 10)
            time = time + 0 + mDay;
        else
            time += mDay;
        if (1 == i)
            time += s3;
        if (mHour < 10)
            time = time + 0 + mHour;
        else
            time += mHour;
        if (1 == i)
            time += s2;
        if (mMinute < 10)
            time = time + 0 + mMinute;
        else
            time += mMinute;
        if (1 == i)
            time += s2;
        if (mSecond < 10)
            time = time + 0 + mSecond;
        else
            time += mSecond;

        time = time.replace(" ", "_").replace(":", ".");
        time = "_" + time;
        return time;
    }
    
    
	// 获取当前系统日期并按照 “案件20010602153221”格式转换作为返回值

	/**
	 * 将Calendar对象格式的时间日期，进行加减天数处理，返回新的时间日期字符串
	 * 
	 * @param c
	 *            转化为日程对象时间值(通过Calendar.setTimeInMillis进行转换)
	 * @param i
	 *            i=0表示返回去除- ：的时间字符串形式， i=1表示返回带有- ：的时间字符串形式
	 * @param n
	 *            正数表示当前时间的后n天，负数表示当前时间的前n天
	 * @return 转换后的时间字符串
	 */

	/**
	 * 传入时间日期字符串，进行加减天数处理，返回新的时间日期字符串
	 * 
	 * @param c
	 *            日期时间字符串
	 * @param i
	 *            i=0表示返回去除- ：的时间字符串形式， i=1表示返回带有- ：的时间字符串形式
	 * @param n
	 *            正数表示当前时间的后n天，负数表示当前时间的前n天
	 * @return 处理后的时间日期字符串
	 */
	public static String getCurTimeToString(String date, int i, int n) {
		// 将字符串转换为时间格式
		String return_date = date;
		// 时间类型处理
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date dt = null;
		try {
			dt = sdf.parse(date);
			if(c != null && dt != null){
				c.setTime(dt);
				return_date = getCurTimeToString(i, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return return_date;
	}

	// 获取当前系统日期并按照 “案件20010602153221”格式转换作为返回值

	/**
	 * 判断某一个日期是否在开始日期和结束日期之间
	 * 
	 * @param begdate
	 *            开始日期，格式为YYYY-MM-DD,或YYYY-MM-DD HH:MM:SS，但仅取日期部分进行比较
	 * @param enddate
	 *            结束日期，格式为YYYY-MM-DD,或YYYY-MM-DD HH:MM:SS，但仅取日期部分进行比较
	 * @param date
	 *            用于比较的日期，格式为YYYY-MM-DD,或YYYY-MM-DD HH:MM:SS，但仅取日期部分进行比较
	 * @return -1:用于比较的日期在开始日期之前 1:用于比较的日期在结束日期之后 0:在开始日期和结束日期之间
	 */
	public static int IsBetween(String begdate, String enddate, String date) {
		int bSure = 0;
		// 将字符串转换为时间格式
		begdate = begdate.substring(0, 11) + "00:00:00";
		enddate = enddate.substring(0, 11) + "23:59:59";
		date = date.substring(0, 11) + "00:00:00";
		// System.out.println(begdate+"\n"+ enddate+"\n"+ date);
		// 时间类型处理
		Calendar begc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date begdt = null;
		Date enddt = null;
		Date dt = null;
		try {
			begdt = sdf.parse(begdate);
			enddt = sdf.parse(enddate);
			dt = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		begc.setTime(begdt);
		endc.setTime(enddt);
		c.setTime(dt);
		// || c.compareTo(begc) == 0
		if (c.compareTo(begc) == -1) {
			bSure = -1;
		}// || c.compareTo(endc) == 0
		else if (c.compareTo(endc) == 1) {
			bSure = 1;
		} else {
			bSure = 0;
		}
		return bSure;
	}

	// 获取当前系统日期并按照 “案件20010602153221”格式转换作为返回值

	/**
	 * 开始日期和结束日期相差的天数
	 * 
	 * @param begdate
	 *            开始日期，格式为YYYY-MM-DD,或YYYY-MM-DD HH:MM:SS，但仅取日期部分进行计算
	 * @param enddate
	 *            结束日期，格式为YYYY-MM-DD,或YYYY-MM-DD HH:MM:SS，但仅取日期部分进行计算
	 * @return -1:表示传入参数不正确，结束日期早于开始日期 0:结束日期等于开始日期 >0:相差的天数
	 */
	public static int GetDateOffset(String begdate, String enddate) {
		int offset = 0;

		// 将字符串转换为时间格式
		begdate = begdate.substring(0, 10) + " 00:00:00";
		enddate = enddate.substring(0, 10) + " 00:00:00";

		// 时间类型处理
		Calendar begc = Calendar.getInstance();
		Calendar endc = Calendar.getInstance();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date begdt = null;
		Date enddt = null;
		try {
			begdt = sdf.parse(begdate);
			enddt = sdf.parse(enddate);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

		begc.setTime(begdt);
		endc.setTime(enddt);

		if (begc.compareTo(endc) == 1) {
			offset = -1;
		} else if (begc.compareTo(endc) == 0) {
			offset = 0;
		} else if (begc.compareTo(endc) == -1) {
			/*while (endc.compareTo(begc) != 0) {
				begc.add(Calendar.DATE, 1);// 增加1天
				offset++;
			}*/
			offset = (int)((enddt.getTime() - begdt.getTime())/86400000);
		}
		// System.out.println("日期差值：" + offset);
		return offset;
	}
}