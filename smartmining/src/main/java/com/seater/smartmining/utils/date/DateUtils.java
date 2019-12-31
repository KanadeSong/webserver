package com.seater.smartmining.utils.date;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.schedule.constant.ScheduleConstant;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {


    private static long nd = 1000 * 24 * 60 * 60;
    private static long nh = 1000 * 60 * 60;
    private static long nm = 1000 * 60;

    /**
     * 根据日期和格式类型进行日期转换
     * @param date 日期
     * @param dateFormat 转换类型
     * @return
     */
    public static String formatDateByPattern(Date date, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if(date != null){
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /**
     *  将日期转换成Cron格式
     * @param date
     * @return
     */
    public static String getCronTime(Date date){
        //Cron日期格式
        String dateFormat = ScheduleConstant.SCHEDULECRON;
        return formatDateByPattern(date, dateFormat);
    }

    /**
     * 将日期转换成Cron格式(循环时间)
     *
     * @param date
     * @return
     */
    public static String getCronTimeLoop(Date date) {
        //Cron日期格式
        String dateFormat = ScheduleConstant.SCHEDULE_CRON_LOOP;
        return formatDateByPattern(date, dateFormat);
    }

    /**
     *  将字符串类型转成Date类型
     * @param dateStr
     * @param format
     * @return
     */
    public static Date stringFormatDate(String dateStr, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *  计算两个日期之间的相差的秒数
     * @param start
     * @param end
     * @return
     */
    public static Long calculationHour(Date start, Date end){
        if(start != null && end != null) {
            long diff = end.getTime() - start.getTime();
            Long result = diff / 1000L;
            return result;
        }
        return 0L;
    }

    /**
     *  获取当月第一天的日期
     * @param date
     * @return
     */
    public static Date getStartDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDay = calendar.getTime();
        return startDay;
    }

    /**
     *  获取当月最后一天
     * @param date
     * @return
     */
    public static Date getEndDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDay = calendar.getTime();
        return endDay;
    }

    /**
     *  获取指定日期的年份
     * @param date
     * @return
     */
    public static int getYeatByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     *  获取指定日期的月份
     * @param date
     * @return
     */
    public static int getMonthByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 获取指定日期的号数
     * @param date
     * @return
     */
    public static int getDayByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        return day;
    }

    public static Date getEndDateByNow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        date = calendar.getTime();
        return date;
    }

    public static Date getStartDateByNow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    /**
     * 生成报表统计日期
     * @param reportDate 要统计的日期
     * @return
     */
    public static Date createReportDateByMonth(Date reportDate){
        //生成统计报表日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date report = calendar.getTime();
        return report;
    }

    /**
     * 舍去毫秒 默认为000
     * @param time
     * @return
     */
    public static Date convertDate(Long time){
        Long convertTime = time / 1000L;
        convertTime = convertTime * 1000L;
        Date date = new Date(convertTime);
        return date;
    }

    //获取当前年份第一个月的第一天
    public static Date getFirstDayByNow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        String firstStr = year + "-01-01 00:00:00";
        Date resultDate = DateUtils.stringFormatDate(firstStr, SmartminingConstant.DATEFORMAT);
        return resultDate;
    }

    //获取当前年份最后一个月的最后一天
    public static Date getLastDayByNow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        String lastStr = year + "-12-31 23:59:59";
        Date resultDate = DateUtils.stringFormatDate(lastStr, SmartminingConstant.DATEFORMAT);
        return resultDate;
    }

    //获取一个礼拜前的日期
    public static Date getWeekAgo(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -8);
        Date agoDate = calendar.getTime();
        return agoDate;
    }

    //获取当前日期增加指定天数后的日期
    public static Date getAddDate(Date date, Integer addDay){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, addDay);
        Date result = calendar.getTime();
        return result;
    }

    //获取指定日期增加指定秒数后的日期
    public static Date getAddSecondDate(Date date, Integer second){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        Date result = calendar.getTime();
        return result;
    }

    //获取半年前的日期
    public static Date getHalfYearAgo(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -5);
        Date agoDate = calendar.getTime();
        return agoDate;
    }

    //在当前时间减一天
    public static Date subtractionOneDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        Date result = calendar.getTime();
        return result;
    }

  public static List<String> getWeekAgoList(Integer choose, Date endTime){
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        int length = 0;
        if(choose == 1){
            length = 7;
        }else if(choose == 2){
            length = 6;
        }
        for (int i = 0; i < length; i++) {
            Date date = null;
            String dateStr = "";
            if (length == 7) {
                calendar.add(Calendar.DATE, -1);
                date = calendar.getTime();
                dateStr = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else{
                calendar.add(Calendar.MONTH, -1);
                date = calendar.getTime();
                dateStr = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            dateList.add(dateStr);
        }
        return dateList;
    }
}
