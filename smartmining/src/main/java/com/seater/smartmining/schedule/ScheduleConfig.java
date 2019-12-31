package com.seater.smartmining.schedule;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectScheduleLog;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.enums.ScheduleEnum;
import com.seater.smartmining.enums.TimeTypeEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.constant.ScheduleConstant;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.date.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Component
@Configuration
public class ScheduleConfig {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    //存放所有任务调度对象
    private HashMap<String, ScheduledFuture<?>> scheduleMap = new HashMap<>();

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * @param //machineList
     * @param project
     * @param date
     */
    public void startCron(Project project, Date date) {
        try {
            threadPoolTaskScheduler.setPoolSize(20);
            threadPoolTaskScheduler.setErrorHandler(new ScheduleErrorHandler());
            String scheduleDate = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            Calendar nowCalendar = Calendar.getInstance();
            nowCalendar.setTime(date);
            nowCalendar.add(Calendar.DATE, 1);
            Date nextDate = nowCalendar.getTime();
            String nextScheduleDate = DateUtils.formatDateByPattern(nextDate, SmartminingConstant.YEARMONTHDAUFORMAT);
            //遍历所有库中动态数据，根据库中class取出所属的定时任务对象进行关闭，每次都会把之前所有的定时任务都关闭，根据新的状态重新启用一次，达到最新配置
            //获取到存储的对应的线程
            //日报表
            ScheduledFuture<?> scheduledFuture = scheduleMap.get(ScheduleConstant.SCHEDULESUFFIXDAY + project.getId() + scheduleDate);
            //一定判空否则出现空指针异常
            if (scheduledFuture != null)
                scheduledFuture.cancel(true);
            //月报表
            ScheduledFuture<?> scheduledFutureMonth = scheduleMap.get(ScheduleConstant.SCHEDULESUFFIXMONTH + project.getId() + scheduleDate);
            if (scheduledFutureMonth != null)
                scheduledFutureMonth.cancel(true);
            //挖机上下班时间
            ScheduledFuture<?> scheduledFutureByMachineByEarly = scheduleMap.get(ScheduleConstant.MACHINEDIGGINGEARLY + project.getId() + scheduleDate);
            if (scheduledFutureByMachineByEarly != null)
                scheduledFutureByMachineByEarly.cancel(true);

            ScheduledFuture<?> nextScheduledFuture = scheduleMap.get(ScheduleConstant.SCHEDULESUFFIXDAY + project.getId() + nextScheduleDate);
            //一定判空否则出现空指针异常
            if (nextScheduledFuture != null)
                nextScheduledFuture.cancel(true);
            //月报表
            ScheduledFuture<?> nextScheduledFutureMonth = scheduleMap.get(ScheduleConstant.SCHEDULESUFFIXMONTH + project.getId() + nextScheduleDate);
            if (nextScheduledFutureMonth != null)
                nextScheduledFutureMonth.cancel(true);
            //挖机上下班时间
            ScheduledFuture<?> nextScheduledFutureByMachineByEarly = scheduleMap.get(ScheduleConstant.MACHINEDIGGINGEARLY + project.getId() + nextScheduleDate);
            if (nextScheduledFutureByMachineByEarly != null)
                nextScheduledFutureByMachineByEarly.cancel(true);
            //因为下边存储的是新的定时任务对象，以前的定时任务对象已经都停用了，所以旧的数据没用清除掉，这步可以不处理，因为可以是不可重复要被覆盖
            //scheduleMap.clear();
            //遍历库中数据，之前已经把之前所有的定时任务都停用了，现在判断库中如果是启用的重新启用并读取新的数据，把开启的数据对象保存到定时任务对象中以便下次停用
            System.out.println("循环时间:" + project.getNightEndTime());
            if (project.getEarlyEndTime() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(project.getEarlyEndTime());
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date);
                calendar1.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar1.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendar1.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
                Date earlyDate = calendar1.getTime();
                //挖机早班下班调度时间
                String machineByEarlyEnd = DateUtils.getCronTime(earlyDate);

                //获取早班开始时间
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(project.getEarlyStartTime());
                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(date);
                calendar3.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
                calendar3.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
                calendar3.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
                Date earlyStartDate = calendar3.getTime();
                //获取日期标识
                Date date1 = new Date();
                /*if(date1.getTime() < earlyStartDate.getTime())
                    date1 = DateUtils.getAddDate(date1, -1);
                Date dateIdentification = DateUtils.createReportDateByMonth(date1);*/
                //开启挖机早班调度
                ScheduledFuture<?> futureByMachineEarly = threadPoolTaskScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //业务逻辑代码的调用
                        try {
                            System.out.println("挖机早班自动下班开始执行");
                            ScheduleService.getNotEndWorkMachineInfo(project.getId(), earlyDate, date, machineByEarlyEnd);
                            ScheduleService.matchingDegreeReport(project.getId(), earlyStartDate, earlyDate, TimeTypeEnum.DAY, machineByEarlyEnd);
                            //ScheduleService.modifyWorkInfoValid(project.getId(), Shift.Night, earlyDate, machineByEarlyEnd);
                            ScheduleService.stopSlagCar(project.getId(), machineByEarlyEnd);
                            ScheduleService.workExceptionReport(project.getId(), Shift.Early, date1, machineByEarlyEnd);
                            ScheduleService.totalCountCarReport(project.getId(), Shift.Early, date1, machineByEarlyEnd);
                            //startCron(project, nextDate);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new CronTrigger(machineByEarlyEnd));
                scheduleMap.put(ScheduleConstant.MACHINEDIGGINGEARLY + project.getId() + scheduleDate, futureByMachineEarly);
            }
            if (project.getNightEndTime() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(project.getNightEndTime());
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date);
                calendar1.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar1.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendar1.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
                Date dayStart = calendar1.getTime();
                Date lastDay = DateUtils.getEndDate(date);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(lastDay);
                calendar2.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendar2.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendar2.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
                Date monthStart = calendar2.getTime();
                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(dayStart);
                Calendar calendar4 = Calendar.getInstance();
                calendar4.setTime(monthStart);
                //判断当前对象是否是当天 如果是明天 则获取到后一天的cronStr字符串
                System.out.println("判断是否是今天：" + project.getNightEndPoint().equals(ProjectWorkTimePoint.Tomorrow));
                if (project.getNightEndPoint().equals(ProjectWorkTimePoint.Tomorrow)) {
                    calendar3.add(Calendar.DATE, 1);
                    calendar4.add(Calendar.DATE, 1);
                }
                Date reportDay = calendar3.getTime();
                Date reportMonth = calendar4.getTime();
                System.out.println("日报表任务调度时间：" + DateUtils.formatDateByPattern(reportDay, "yyyy-MM-dd HH:mm:ss"));
                System.out.println("月报表任务调度时间：" + DateUtils.formatDateByPattern(reportMonth, SmartminingConstant.DATEFORMAT));
                //日报表调度时间
                String cronStr = DateUtils.getCronTime(reportDay);
                //月报表调度时间
                //todo 设置自定义统计月报表的时间 calendar4.set(Calendar.DATE, project.getReportDay());
                String monthCronStr = DateUtils.getCronTime(reportMonth);

                //获取晚班开始时间
                Calendar calendar5 = Calendar.getInstance();
                calendar5.setTime(project.getNightStartTime());
                Calendar calendar6 = Calendar.getInstance();
                calendar6.setTime(date);
                calendar6.set(Calendar.HOUR_OF_DAY, calendar5.get(Calendar.HOUR_OF_DAY));
                calendar6.set(Calendar.MINUTE, calendar5.get(Calendar.MINUTE));
                calendar6.set(Calendar.SECOND, calendar5.get(Calendar.SECOND));
                Date nightStartDate = calendar6.getTime();
                String nightStartCron = DateUtils.getCronTime(nightStartDate);
                System.out.println("<<<<<<<<<< <<<<<<<<<< <<<<<<<<<<");
                System.out.println("晚班开始时间: " + DateUtil.formatDateTime(nightStartDate));
                System.out.println("晚班开始Cron任务字符串: " + nightStartCron);
                System.out.println(">>>>>>>>>> >>>>>>>>>> >>>>>>>>>>");

                //获取早班开始时间
                Calendar calendar7 = Calendar.getInstance();
                calendar7.setTime(project.getEarlyStartTime());
                Calendar calendar8 = Calendar.getInstance();
                calendar8.setTime(date);
                calendar8.set(Calendar.HOUR_OF_DAY, calendar7.get(Calendar.HOUR_OF_DAY));
                calendar8.set(Calendar.MINUTE, calendar7.get(Calendar.MINUTE));
                calendar8.set(Calendar.SECOND, calendar7.get(Calendar.SECOND));
                Date earlyStartTime = calendar8.getTime();
                //获取日期标识
                Date date1 = new Date();
                /*if(date1.getTime() < earlyStartTime.getTime())
                    date1 = DateUtils.getAddDate(date1, -1);
                Date dateIdentification = DateUtils.createReportDateByMonth(date1);*/

                String earlyStartCron = DateUtils.getCronTime(earlyStartTime);
                System.out.println("<<<<<<<<<< <<<<<<<<<< <<<<<<<<<<");
                System.out.println("早班开始时间: " + DateUtil.formatDateTime(earlyStartTime));
                System.out.println("早班开始Cron任务字符串: " + earlyStartCron);
                System.out.println(">>>>>>>>>> >>>>>>>>>> >>>>>>>>>>");
                //开启一个统计日报表的任务 日报
                ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //业务逻辑代码的调用
                        try {
                            startCron(project, nextDate);
                            System.out.println("日报表任务调度开始执行！ ---------------------");
                            ScheduleService.scheduleCarReport(project, dayStart,  cronStr);
                            ScheduleService.scheduleDiggingReport(project.getId(), dayStart,  cronStr);
                            ScheduleService.scheduleCostAccounting(project.getId(), dayStart,  cronStr);
                            //自动统计渣场车数
                            ScheduleService.slagSiteCarReport(project.getId(), dayStart, cronStr);
                            //自动统计挖机平台的工作信息
                            ScheduleService.reportDiggingByPlace(project.getId(), dayStart, cronStr);
                            //自动统计挖机物料的工作信息
                            ScheduleService.reportDiggingByMaterial(project.getId(), dayStart, cronStr);
                            System.out.println("挖机晚班自动下班开始执行");
                            ScheduleService.getNotEndWorkMachineInfo(project.getId(), reportDay, date, cronStr);
                            ScheduleService.matchingDegreeReport(project.getId(), nightStartDate, reportDay, TimeTypeEnum.DAY,  cronStr);
                            //ScheduleService.modifyWorkInfoValid(project.getId(), Shift.Early, reportDay, cronStr);
                            ScheduleService.stopSlagCar(project.getId(), cronStr);
                            ScheduleService.workExceptionReport(project.getId(), Shift.Night, date1, cronStr);
                            ScheduleService.totalCountCarReport(project.getId(), Shift.Night, date1, cronStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SmartminingProjectException e) {
                            e.printStackTrace();
                        }
                    }
                }, new CronTrigger(cronStr));

                //开启月报调度
                ScheduledFuture<?> futureMonth = threadPoolTaskScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //业务逻辑代码的调用
                        try {
                            System.out.println("月报表任务调度开始执行！ ---------------------");
                            ScheduleService.scheduleCarReport(project, monthStart,  monthCronStr);
                            ScheduleService.scheduleDiggingReport(project.getId(), monthStart,  monthCronStr);
                            //startCron(project, nextMonth);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SmartminingProjectException e) {
                            e.printStackTrace();
                        }
                    }
                }, new CronTrigger(monthCronStr));
                //这一步非常重要，之前直接停用，只停用掉了最后启动的定时任务，前边启用的都没办法停止，所以把每次的对象存到map中可以根据key停用自己想要停用的
                scheduleMap.put(ScheduleConstant.SCHEDULESUFFIXDAY + project.getId() + scheduleDate, future);
                scheduleMap.put(ScheduleConstant.SCHEDULESUFFIXMONTH + project.getId() + scheduleDate, futureMonth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
