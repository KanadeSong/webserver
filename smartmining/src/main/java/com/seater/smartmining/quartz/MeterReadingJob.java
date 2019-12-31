package com.seater.smartmining.quartz;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectServiceI;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description 执行调度任务逻辑
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/18 12:19
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class MeterReadingJob extends QuartzJobBean {

    @Override
    public void executeInternal(JobExecutionContext context) {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Project project = (Project) jobDataMap.get(QuartzConstant.PROJECT);
            ScheduleService.doProjectCarFillMeterReadingTask(project, new Date());
            log.info("项目:{},执行抄表任务完成", project.getName());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("执行任务出错.....");
        }
    }


}
