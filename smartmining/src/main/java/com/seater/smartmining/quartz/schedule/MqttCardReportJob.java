package com.seater.smartmining.quartz.schedule;

import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ScheduleMachine;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.utils.date.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/3 0003 15:01
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class MqttCardReportJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        /*try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Long projectId = jobDataMap.getLong("projectId");
            Shift shift = (Shift) jobDataMap.get("shift");
            ScheduleService.cardCountReport(projectId, shift);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
