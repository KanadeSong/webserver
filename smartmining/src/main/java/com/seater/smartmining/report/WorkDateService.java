package com.seater.smartmining.report;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/2/21 0021 14:45
 */
@Service
public class WorkDateService {

    @Autowired
    private ProjectServiceI projectServiceI;

    /**
     * 根据统计日期 获取白班上班时间
     *
     * @param projectId
     * @param reportDate
     * @return
     * @throws IOException
     */
    public Map<String, Date> getWorkTime(Long projectId, Date reportDate) throws IOException {
        Project project = projectServiceI.get(projectId);
        //统计时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        //根据当前的年月日 获取白班上班时间
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(project.getEarlyStartTime());
        calendar.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar1.get(Calendar.SECOND));
        Date start = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDay = calendar.getTime();
        //根据当前的年月日  获取白班下班时间
        calendar.setTime(reportDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(project.getEarlyEndTime());
        calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
        Date earlyEnd = calendar.getTime();
        //设置晚班上班时间
        calendar.setTime(reportDate);
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(project.getNightStartTime());
        calendar.set(Calendar.HOUR_OF_DAY, calendar3.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar3.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar3.get(Calendar.SECOND));
        Date nightStart = calendar.getTime();
        //设置晚班下班时间
        calendar1.setTime(project.getNightEndTime());
        calendar.setTime(reportDate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar1.get(Calendar.SECOND));
        if (project.getNightEndPoint() == ProjectWorkTimePoint.Tomorrow) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Date end = calendar.getTime();
        Map<String, Date> result = new HashMap<>();
        //早班开始时间
        result.put("start", start);
        //早班结束时间
        result.put("earlyEnd", earlyEnd);
        //月初早班开始时间
        result.put("startDay", startDay);
        //晚班开始时间
        result.put("nightStart", nightStart);
        //晚班结束时间
        result.put("end", end);
        return result;
    }


    /**
     * 给定 projectId 和 sourceDate 计算 sourceDate 的班次时间
     *
     * @param projectId
     * @param sourceDate
     * @return
     */
    public JSONObject calWorkTime(Long projectId, Date sourceDate) throws IOException {
        Project project = projectServiceI.get(projectId);

        //当班日期标识
        Date dateIdentification = getTargetDateIdentification(sourceDate, projectId);

        /*//跨日问题
        if (project.getNightEndPoint().equals(ProjectWorkTimePoint.Tomorrow)) {
            //如果当前小时数小于晚班结束小时数  就往前退一天
            int hour1 = DateUtil.hour(sourceDate, true);
            int hour2 = DateUtil.hour(project.getNightEndTime(), true);
            // 当前小时数 < 晚班结束小时数,就是跨日了, 当hour1 == hour2 时,就是明天的早班开始了
            if (hour1 < hour2) {
                dateIdentification = DateUtil.offsetDay(sourceDate, -1);
            }
        }
*/
        //早班开始时间(当班)
        String prefix = DateUtil.formatDate(dateIdentification);
        String suffix = DateUtil.formatTime(project.getEarlyStartTime());
        DateTime earlyStart = DateUtil.parse(prefix + " " + suffix, DatePattern.NORM_DATETIME_PATTERN);

        //早班结束时间(当班)
        suffix = DateUtil.formatTime(project.getEarlyEndTime());
        DateTime earlyEnd = DateUtil.parse(prefix + " " + suffix, DatePattern.NORM_DATETIME_PATTERN);

        //晚班开始时间(当班)
        suffix = DateUtil.formatTime(project.getNightStartTime());
        DateTime nightStart = DateUtil.parse(prefix + " " + suffix, DatePattern.NORM_DATETIME_PATTERN);

        //晚班结束时间(当班)
        suffix = DateUtil.formatTime(project.getNightEndTime());
        DateTime nightEnd = DateUtil.parse(prefix + " " + suffix, DatePattern.NORM_DATETIME_PATTERN);
        if (project.getNightEndPoint().equals(ProjectWorkTimePoint.Tomorrow)) {
            nightEnd = DateUtil.offsetDay(nightEnd, 1);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("earlyStart", earlyStart);
        jsonObject.put("earlyEnd", earlyEnd);
        jsonObject.put("nightStart", nightStart);
        jsonObject.put("nightEnd", nightEnd);
        return jsonObject;
    }

    /**
     * 根据给定目标时间和项目返回给定时间对该项目的班次
     * 例如项目的晚班结束时间是明天05:59:59 , 传9月9日 01:50:50 ==> 返回9月9日的晚班
     *
     * @param sourceDate
     * @param projectId
     * @return
     */
    public ShiftsEnums getTargetDateShift(Date sourceDate, Long projectId) throws IOException {
        JSONObject workTime = calWorkTime(projectId, sourceDate);
        //给定日期和早班开始(当班)到早班结束(当班)的时间段比较
        if (sourceDate.getTime() >= workTime.getDate("earlyStart").getTime() && sourceDate.getTime() <= workTime.getDate("earlyEnd").getTime()) {
            return ShiftsEnums.DAYSHIFT;
        }
        //给定日期和晚班开始(当班)到晚班结束(当班)的时间段比较
        if (sourceDate.getTime() >= workTime.getDate("nightStart").getTime() && sourceDate.getTime() <= workTime.getDate("nightEnd").getTime()) {
            return ShiftsEnums.BLACKSHIFT;
        } else {
            return ShiftsEnums.UNKNOW;
        }
    }

    /*public ShiftsEnums getTargetDateShift(Date sourceDate, Long projectId) throws IOException {
        Map<String, Date> workTime = getWorkTime(projectId, sourceDate);
        Date startEarly = workTime.get("start");
        Date startEnd = workTime.get("earlyEnd");
        Date nightStart = workTime.get("nightStart");
        Date nightEnd = workTime.get("end");
        if(startEarly.getTime() > sourceDate.getTime()) {
            startEarly = DateUtils.subtractionOneDay(startEarly);
            startEnd = DateUtils.subtractionOneDay(startEnd);
            nightStart = DateUtils.subtractionOneDay(nightStart);
            nightEnd = DateUtils.subtractionOneDay(nightEnd);
        }
        if (sourceDate.getTime() >= startEarly.getTime() && sourceDate.getTime() <= startEnd.getTime()) {
            return ShiftsEnums.DAYSHIFT;
        }
        if (sourceDate.getTime() >= nightStart.getTime() && sourceDate.getTime() <= nightEnd.getTime()) {
            return ShiftsEnums.BLACKSHIFT;
        } else {
            return ShiftsEnums.UNKNOW;
        }
    }*/

    public Shift getShift(Date sourceDate, Long projectId) throws IOException {
        Map<String, Date> workTime = getWorkTime(projectId, sourceDate);
        Date startEarly = workTime.get("start");
        Date startEnd = workTime.get("earlyEnd");
        Date nightStart = workTime.get("nightStart");
        Date nightEnd = workTime.get("end");
        if(startEarly.getTime() > sourceDate.getTime()) {
            startEarly = DateUtils.subtractionOneDay(startEarly);
            startEnd = DateUtils.subtractionOneDay(startEnd);
            nightStart = DateUtils.subtractionOneDay(nightStart);
            nightEnd = DateUtils.subtractionOneDay(nightEnd);
        }
        if (sourceDate.getTime() >= startEarly.getTime() && sourceDate.getTime() <= startEnd.getTime()) {
            return Shift.Early;
        }
        if (sourceDate.getTime() >= nightStart.getTime() && sourceDate.getTime() <= nightEnd.getTime()) {
            return Shift.Night;
        } else {
            return Shift.Unknown;
        }
    }

    /**
     * 根据给定目标时间和项目返回给定时间对该项目的当班日期(当前班次对应日期)
     *
     * @param sourceDate
     * @param projectId
     * @return
     */
    public Date getTargetDateIdentification(Date sourceDate, Long projectId) throws IOException {

        Project project = projectServiceI.get(projectId);
        //当班日期标识,默认取sourceDate的开始时间
        Date dateIdentification = DateUtil.beginOfDay(sourceDate);

        //跨日问题
        if (project.getNightEndPoint().equals(ProjectWorkTimePoint.Tomorrow)) {
            //如果当前小时数小于晚班结束小时数  就往前退一天
            int hour1 = DateUtil.hour(sourceDate, true);
            int hour2 = DateUtil.hour(project.getNightEndTime(), true);
            // 当前小时数 < 晚班结束小时数,就是跨日了, 当hour1 == hour2 时,就是明天的早班开始了
            if (hour1 < hour2) {
                dateIdentification = DateUtil.beginOfDay(DateUtil.offsetDay(sourceDate, -1));
            }
        }
        return dateIdentification;
    }
}
