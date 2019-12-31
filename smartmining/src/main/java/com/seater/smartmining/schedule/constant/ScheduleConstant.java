package com.seater.smartmining.schedule.constant;

public class ScheduleConstant {
    //线程挖机日报表名前缀
    public static final String SCHEDULESUFFIXDAY = "smartmining_day";
    //线程渣车日报表名前缀
    public static final String SCHEDULESUFFIXMONTH = "smartmining_month";
    //挖机早班前缀
    public static final String MACHINEDIGGINGEARLY = "smartmining_machine_early";
    //挖机晚班前缀
    public static final String MACHINEDIGGINGNIGHT = "smartmining_machine_night";
    //挖机匹配效率早班前缀
    public static final String MATCHINGDEGREEDAY = "matching_degree_early";
    //挖机匹配效率晚班前缀
    public static final String MATCHINGDEGREENIGHT = "matching_degree_night";
    //cron格式
    public static final String SCHEDULECRON = "ss mm HH dd MM ?";
    //cron循环格式
    public static final String SCHEDULE_CRON_LOOP = "ss mm HH * * ?";
}
