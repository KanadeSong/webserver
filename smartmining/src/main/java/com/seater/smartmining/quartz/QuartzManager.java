package com.seater.smartmining.quartz;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.schedule.constant.ScheduleConstant;
import com.seater.smartmining.service.ProjectServiceI;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 封装的quartz管理器
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/18 15:01
 */
@Slf4j
@Component
public class QuartzManager {

    private static final String TAG = "QuartzManager:";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ProjectServiceI projectServiceI;

    // 默认的job group
    private final static String DEFAULT_JOB_GROUP = "DEFAULT_QUARTZ_JOB_GROUP";
    // 默认的trigger group
    private final static String DEFAULT_TRIGGER_GROUP = "DEFAULT_QUARTZ_TRIGGER_GROUP";

//    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();


    public QuartzManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 根据给定项目创建抄表任务名称(早班)
     *
     * @return
     */
    public static String createJobNameDay(Project project) {
        return QuartzConstant.TASK_METER_READING + project.getId();
    }

    /**
     * 根据给定项目创建mqtt车数统计任务名称(早班)
     *
     * @return
     */
    public static String createJobNameDayByMqtt(Project project) {
        return QuartzConstant.TASK_MQTT_READING_DAY + project.getId();
    }

    /**
     * 根据给定项目创建抄表任务名称(晚班)
     *
     * @return
     */
    public static String createJobNameNight(Project project) {
        return QuartzConstant.TASK_METER_READING_NIGHT + project.getId();
    }

    /**
     * 根据给定项目创建mqtt车数统计任务名称(晚班)
     *
     * @return
     */
    public static String createJobNameNightByMqtt(Project project) {
        return QuartzConstant.TASK_MQTT_READING_NIGHT + project.getId();
    }

    /**
     * 根据挖机ID创建mqtt上下机重发任务调度名称
     * @param machineId
     * @return
     */
    public static String createJobNameMachineWork(Long machineId){
        return QuartzConstant.TASK_MACHINE_WORK + machineId;
    }

    /**
     * 根据渣车编号创建mqtt上下机重发任务调度名称
     * @param carId
     * @return
     */
    public static String createJobNameSlagCarWork(Long carId){
        return QuartzConstant.TASK_SLAG_CAR_WORK + carId;
    }

    /**
     * 根据其它设备ID创建mqtt上下机重发任务调度名称
     * @param deviceId
     * @return
     */
    public static String createJobNameOtherDeviceWork(Long deviceId){
        return QuartzConstant.TASK_OTHER_DEVICE_WORK + deviceId;
    }

    /**
     * 根据挖机ID创建mqtt排班重发任务调度名称
     * @param uid
     * @return
     */
    public static String createJobNameScheduleMachine(String uid){
        return QuartzConstant.TASK_MACHINE_SCHEDULE + uid;
    }

    /**
     * 根据渣车ID创建mqtt排班重发任务调度名称
     * @param uid
     * @return
     */
    public static String createJobNameScheduleSlagSiteCar(String uid){
        return QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + uid;
    }

    /**
     * 根据渣车ID创建mqtt渣场定位重发任务调度名称
     * @param uid
     * @return
     */
    public static String createJobNameScheduleSlagSitePosition(String uid){
        return QuartzConstant.TASK_SLAG_SITE_CAR_POSITION + uid;
    }


    /**
     * 根据UID创建mqtt渣场定位重发任务调度名称
     * @param uid
     * @return
     */
    public static String createJobNameSlagSitePosition(String uid){
        return QuartzConstant.TASK_SLAG_SITE_POSITION + uid;
    }

    /**
     * 根据Key创建调度模板任务调度名称
     * @param key
     * @return
     */
    public static String createJobNameTaskScheduleModel(String key){
        return QuartzConstant.TASK_SCHEDULE_MODEL_WORK + key;
    }

    /**
     * 添加一个任务 使用DEFAULT_JOB_GROUP和DEFAULT_TRIGGER_GROUP
     * 打印格式: [{任务组},{任务名称}]
     *
     * @param jobName    任务名称
     * @param jobClass   QuartzJobBean子类或者Job子类或者相关子类
     * @param cron       时间表达式
     * @param jobDataMap 可以携带一些用到的业务数据
     */
    @SuppressWarnings("unchecked")
    public void addJob(String jobName, Class jobClass, String cron, JobDataMap jobDataMap) {
        try {
            // 先删除
            removeJob(jobName);

//            Scheduler scheduler = schedulerFactory.getScheduler();
            // 任务名，任务组，任务执行类
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, DEFAULT_JOB_GROUP).storeDurably().setJobData(jobDataMap).build();
            // 触发器名,触发器组  
            CronTriggerImpl trigger = new CronTriggerImpl();
            trigger.setCronExpression(cron);
            // 用任务名称充当触发器名称
            trigger.setName(jobName);
            trigger.setGroup(DEFAULT_TRIGGER_GROUP);
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动  
            if (!scheduler.isShutdown()) {
                scheduler.start();
                log.info(TAG + "启动[{},{}]任务成功,启动时间为[{}],执行时间为[{}]",
                        DEFAULT_JOB_GROUP,
                        jobName,
                        DateUtil.format(trigger.getStartTime(), DatePattern.NORM_DATETIME_PATTERN),
                        DateUtil.format(trigger.getNextFireTime(), DatePattern.NORM_DATETIME_PATTERN));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个任务,自定义任务组和触发器组
     *
     * @param jobName          任务名称
     * @param jobGroupName     自定义任务组名称
     * @param triggerName      触发器名称
     * @param triggerGroupName 自定义触发器组名称
     * @param jobClass         QuartzJobBean子类或者Job子类或者相关子类
     * @param cron             时间表达式
     */
    @SuppressWarnings("unchecked")
    public void addJob(String jobName, String jobGroupName,
                       String triggerName, String triggerGroupName, Class jobClass,
                       String cron) {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).storeDurably().build();
//            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName).forJob(jobDetail).build();
            CronTriggerImpl trigger = new CronTriggerImpl();
            trigger.setCronExpression(cron);
            trigger.setName(triggerName);
            trigger.setGroup(triggerGroupName);
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改在默认任务组的触发任务时间(默认触发器)
     *
     * @param jobName    任务名称
     * @param cron       时间表达式
     * @param jobDataMap 可以携带一些用到的业务数据
     */
    @SuppressWarnings("unchecked")
    public void modifyJobTime(String jobName, String cron, JobDataMap jobDataMap) {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(jobName, DEFAULT_TRIGGER_GROUP));
            if (trigger == null) {
                log.error(TAG + "未找到目标trigger:[{}],无法修改[{}]的任务触发时间", jobName, jobName);
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, DEFAULT_JOB_GROUP));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(jobName, objJobClass, cron, jobDataMap);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除一个在默认任务组里面的任务
     *
     * @param jobName
     */
    public void removeJob(String jobName) {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            // 停止触发器  
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, DEFAULT_TRIGGER_GROUP));
            log.info(TAG + "停止[{},{}]触发器", DEFAULT_TRIGGER_GROUP, jobName);
            // 移除触发器 
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, DEFAULT_TRIGGER_GROUP));
            log.info(TAG + "移除[{},{}]触发器", DEFAULT_TRIGGER_GROUP, jobName);
            // 删除任务 
            scheduler.deleteJob(JobKey.jobKey(jobName, DEFAULT_JOB_GROUP));
            log.info(TAG + "删除[{},{}]任务成功", DEFAULT_JOB_GROUP, jobName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除一个在自定义任务组里面的任务
     *
     * @param jobName          任务名称
     * @param jobGroupName     任务组名称
     * @param triggerName      触发器名称
     * @param triggerGroupName 触发器组名称
     */
    public void removeJob(String jobName, String jobGroupName,
                          String triggerName, String triggerGroupName) {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));// 停止触发器  
            scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));// 移除触发器  
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务  
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始全部任务
     */
    public void startJobs() {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭全部任务
     */
    public void shutdownJobs() {
        try {
//            Scheduler scheduler = schedulerFactory.getScheduler();
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
