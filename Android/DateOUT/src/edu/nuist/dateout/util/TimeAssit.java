package edu.nuist.dateout.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhang 该类来自于MyXMPP程序，作用是转换日期
 */
public class TimeAssit
{
    /**
     * 返回指定格式日期时间
     * 
     * @param format 输入的日期格式
     * @return 指定的日期时间格式
     */
    public static String getDate(String format)
    {
        SimpleDateFormat formatBuilder = new SimpleDateFormat(format);
        return formatBuilder.format(new Date());
    }
    
    /**
     * 返回默认格式日期时间
     * 
     * @return "yyyy/MM/dd HH:mm:ss.SSS"格式的当前时间
     */
    public static String getDate()
    {
        return getDate("yyyy/MM/dd HH:mm:ss.SSS");
    }
    
    /**
     * 将Date类型转换为该程序标准时间,即"yyyy/MM/dd HH:mm:ss.SSS"格式
     * 
     * @param date 输入Java Date默认格式的日期
     * @return "yyyy/MM/dd HH:mm:ss.SSS"格式的日期
     */
    public static String formatDate(Date date)
    {
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        // TODO 处理消息时间显示错误
        return formatBuilder.format(date.toGMTString());
    }
    
    /**
     * 把时间变为用户友好的格式,比如09:16,昨天,星期三,15/03/23
     * 
     * @param msgDateAndTime "yyyy/MM/dd HH:mm:ss.SSS"格式的日期时间
     * @return 用户友好的简略时间日期
     */
    public static String toBriefFormat(String msgDateAndTime)
    {
        String nowString = getDate();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String result = "";
        
        // 今天的零点时间
        Calendar calToday = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calMsg = Calendar.getInstance();
        
        try
        {
            calToday.setTime(df.parse(nowString.substring(0, 10) + " 00:00:00.000"));
            calNow.setTime(df.parse(nowString));
            calMsg.setTime(df.parse(msgDateAndTime));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // Log.v("Dateout", "时间格式不正确");
        }
        
        long diff = calNow.getTimeInMillis() - calMsg.getTimeInMillis();
        long diff2 = calNow.getTimeInMillis() - calToday.getTimeInMillis();
        
        final long day = 24 * 60 * 60 * 1000;
        final long week = 7 * day;
        
        if (diff < 0)
        {
            return "消息时间格式错误";
        }
        else if (diff <= diff2)
        {
            // 今天0点之后发的消息,形如 14:22
            result = msgDateAndTime.substring(11, 16);
        }
        else if (diff <= diff2 + day)
        {
            // 昨天0点之后发的,形如 昨天
            result = "昨天";
        }
        else if (diff <= diff2 + week)
        {
            // 一周以内发的,形如 星期三
            switch (calMsg.get(Calendar.DAY_OF_WEEK))
            {
                case Calendar.MONDAY:
                    result = "星期一";
                    break;
                case Calendar.TUESDAY:
                    result = "星期二";
                    break;
                case Calendar.WEDNESDAY:
                    result = "星期三";
                    break;
                case Calendar.THURSDAY:
                    result = "星期四";
                    break;
                case Calendar.FRIDAY:
                    result = "星期五";
                    break;
                case Calendar.SATURDAY:
                    result = "星期六";
                    break;
                case Calendar.SUNDAY:
                    result = "星期日";
                    break;
                default:
                    break;
            }
        }
        else
        {
            // 一周以前发的,形如15/03/13
            result = msgDateAndTime.substring(2, 10);
        }
        return result;
    }
    
    /**
     * 把时间变为用户友好的格式,比如09:16,昨天 09:16,星期三 09:16,15/03/14 09:16
     * 
     * @param msgDateAndTime "yyyy/MM/dd HH:mm:ss.SSS"格式的日期时间
     * @return 用户友好的简略时间日期,用于聊天页面
     */
    public static String toBriefFormatStyle2(String msgDateAndTime)
    {
        String nowString = getDate();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String result = "";
        
        // 今天的零点时间
        Calendar calToday = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        Calendar calMsg = Calendar.getInstance();
        
        try
        {
            calToday.setTime(df.parse(nowString.substring(0, 10) + " 00:00:00.000"));
            calNow.setTime(df.parse(nowString));
            calMsg.setTime(df.parse(msgDateAndTime));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("时间格式不正确");
        }
        
        long diff = calNow.getTimeInMillis() - calMsg.getTimeInMillis();
        long diff2 = calNow.getTimeInMillis() - calToday.getTimeInMillis();
        
        final long day = 24 * 60 * 60 * 1000;
        final long week = 7 * day;
        
        if (diff < 0)
        {
            return "消息时间格式错误";
        }
        else if (diff <= diff2)
        {
            // 今天0点之后发的消息,形如 14:22
            result = msgDateAndTime.substring(11, 16);
        }
        else if (diff <= diff2 + day)
        {
            // 昨天0点之后发的,形如 昨天 14:22
            result = "昨天 " + msgDateAndTime.substring(11, 16);
        }
        else if (diff <= diff2 + week)
        {
            // 一周以内发的,形如 星期三
            switch (calMsg.get(Calendar.DAY_OF_WEEK))
            {
                case Calendar.MONDAY:
                    result = "星期一 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.TUESDAY:
                    result = "星期二 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.WEDNESDAY:
                    result = "星期三 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.THURSDAY:
                    result = "星期四 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.FRIDAY:
                    result = "星期五 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.SATURDAY:
                    result = "星期六 " + msgDateAndTime.substring(11, 16);
                    break;
                case Calendar.SUNDAY:
                    result = "星期日 " + msgDateAndTime.substring(11, 16);
                    break;
                default:
                    break;
            }
        }
        else
        {
            // 一周以前发的,形如15/03/13
            result = msgDateAndTime.substring(2, 10) + " " + msgDateAndTime.substring(11, 16);
        }
        return result;
    }
    
    /**
     * 将时间日期转换为易于读取的格式 用户地图上陌生人的显示
     * 
     * @param time
     * @return
     */
    public static String timestamp2FriendlyFormat(Timestamp time)
    {
        
        String result = "";
        String msgDateAndTime = timestamp2MyStardFormat(time);
        String nowString = getDate();
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        
        // 今天的零点时间
        Calendar calNow = Calendar.getInstance();
        Calendar calMsg = Calendar.getInstance();
        
        try
        {
            calNow.setTime(df.parse(nowString));
            calMsg.setTime(df.parse(msgDateAndTime));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("时间格式不正确");
        }
        
        long diff = calNow.getTimeInMillis() - calMsg.getTimeInMillis();
        
        // 转换毫秒为分钟
        long m = diff / 60000;// 单位为分钟
        
        if (m < 0)
        {
            return "消息时间格式错误";
        }
        else if (m < 5)
        {
            result = "5分钟内";
        }
        else if (m < 20)
        {
            result = "20分钟内";
        }
        else if (m < 60)
        {
            result = "1小时内";
        }
        else if (m < 60 * 3)
        {
            result = "3小时内";
        }
        else if (m < 60 * 6)
        {
            result = "6小时内";
        }
        else if (m < 60 * 12)
        {
            result = "12小时内";
        }
        else if (m < 60 * 24 * 2)
        {
            result = "2天内";
        }
        else if (m < 60 * 24 * 7)
        {
            result = "7天内";
        }
        else
        {
            result = "一周之前";
        }
        return result;
    }
    
    /**
     * 将Timestamp类型时间转换为我使用的标准时间格式
     * 
     * @param Timestamp时间对象 来源于java.sql.Timestamp包,toString后格式如yyyy-MM-dd HH:mm:ss.S
     * @return 返回yyyy/MM/dd HH:mm:ss.SSS格式的时间
     */
    public static String timestamp2MyStardFormat(Timestamp timestamp)
    {
        String beforeConvert = timestamp.toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;
        try
        {
            date = df.parse(beforeConvert);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat formatBuilder = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String afterConvert = formatBuilder.format(date);
        return afterConvert;
    }
    
    /**
     * 输入用户生日,计算得到年龄
     * 
     * @param birthday 用户出生年月日,格式如1993-05-16
     * @return 返回用户现在的年龄(要求系统时间正确)
     */
    public static int birthday2Age(String birthday)
    {
        if (birthday == null || birthday.length() < 4)
        {
            return 0;
        }
        else
        {
            String birthYear = birthday.substring(0, 3);
            String thisYear = TimeAssit.getDate().substring(0, 3);
            return Integer.parseInt(thisYear) - Integer.parseInt(birthYear);
        }
    }
}
