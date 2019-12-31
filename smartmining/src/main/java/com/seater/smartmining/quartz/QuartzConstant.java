package com.seater.smartmining.quartz;

/**
 * @Description Quartz用到的常量
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/23 14:33
 */
public class QuartzConstant {

    // 项目
    public static final String PROJECT = "project";
    // 早班调度任务
    public static final String TASK_METER_READING = "task_meter_reading_day_shift_start";
    // 晚班调度任务
    public static final String TASK_METER_READING_NIGHT = "task_meter_reading_night_shift_start";

    public static final String DEVICE = "device";
    //挖机上下机重复发送
    public static final String TASK_MACHINE_WORK = "task_machine_work";
    //渣车上下机重复发送
    public static final String TASK_SLAG_CAR_WORK = "task_slag_car_work";
    //挖机排班重复发送
    public static final String TASK_MACHINE_SCHEDULE = "task_machine_schedule";
    // 早班调度任务 mqtt车数汇总
    public static final String TASK_MQTT_READING_DAY = "task_mqtt_report_day_shift_start";
    // 晚班调度任务 mqtt车数汇总
    public static final String TASK_MQTT_READING_NIGHT = "task_mqtt_report_night_shift_start";
    //渣车排班重复发送
    public static final String TASK_SLAG_SITE_CAR_SCHEDULE = "task_slag_site_car_schedule";
    //渣车 渣场定位重复发送
    public static final String TASK_SLAG_SITE_CAR_POSITION = "task_slag_site_car_position";
    //渣场定位重复发送
    public static final String TASK_SLAG_SITE_POSITION = "task_slag_site_position";
    //其它设备上下机重复发送
    public static final String TASK_OTHER_DEVICE_WORK = "task_other_device_work";
    //调度模板下发
    public static final String TASK_SCHEDULE_MODEL_WORK = "task_schedule_model_work";
    //mqtt重复发送的时间间隔 cron
    public static final String MQTT_REPLY_CRON = "*/5 * * * * ?";
}
