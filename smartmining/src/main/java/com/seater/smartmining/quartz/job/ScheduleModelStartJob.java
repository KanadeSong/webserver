package com.seater.smartmining.quartz.job;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.domain.ScheduleModelResponse;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.schedule.SmartminingScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/16 0016 16:24
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class ScheduleModelStartJob extends QuartzJobBean {
    @Autowired
    private ProjectScheduleModelServiceI projectScheduleModelServiceI;
    @Autowired
    private ScheduleMachineModelServiceI scheduleMachineModelServiceI;
    @Autowired
    private ScheduleCarModelServiceI scheduleCarModelServiceI;
    @Autowired
    private SmartminingScheduleService smartminingScheduleService;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            String message = stringRedisTemplate.opsForValue().get("scheduleTask");
            stringRedisTemplate.opsForValue().set("scheduleTask", "allReady", 2 * 60, TimeUnit.SECONDS);
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            Long projectId = jobDataMap.getLong("projectId");
            Long programmeId = jobDataMap.getLong("programmeId");
            List<ProjectScheduleModel> projectScheduleModelList = projectScheduleModelServiceI.getAllByProjectIdAndProgrammeId(projectId, programmeId);
            List<ScheduleMachineModel> scheduleMachineModelList = scheduleMachineModelServiceI.getAllByProjectId(projectId);
            List<ScheduleCarModel> scheduleCarModelList = scheduleCarModelServiceI.getAllByProjectId(projectId);
            List<ScheduleResponse> scheduleResponseList = new ArrayList<>();
            scheduleCarServiceI.deleteByProjectId(projectId);
            scheduleMachineServiceI.deleteByProjectId(projectId);
            projectScheduleServiceI.deleteAll(projectId);
            for(ProjectScheduleModel model : projectScheduleModelList) {
                String json = JSON.toJSONString(model);
                ProjectSchedule projectSchedule = JSON.parseObject(json, ProjectSchedule.class);
                ScheduleResponse response = new ScheduleResponse();
                response.setProjectSchedule(projectSchedule);
                List<ScheduleMachine> scheduleMachineList = new ArrayList<>();
                for (ScheduleMachineModel scheduleMachineModel : scheduleMachineModelList) {
                    if (scheduleMachineModel.getGroupCode().equals(model.getGroupCode())) {
                        String machineJson = JSON.toJSONString(scheduleMachineModel);
                        ScheduleMachine scheduleMachine = JSON.parseObject(machineJson, ScheduleMachine.class);
                        scheduleMachineList.add(scheduleMachine);
                    }
                }
                List<ScheduleCar> scheduleCarList = new ArrayList<>();
                for (ScheduleCarModel scheduleMachineModel : scheduleCarModelList) {
                    if (scheduleMachineModel.getGroupCode().equals(model.getGroupCode())) {
                        String carJson = JSON.toJSONString(scheduleMachineModel);
                        ScheduleCar scheduleCar = JSON.parseObject(carJson, ScheduleCar.class);
                        scheduleCarList.add(scheduleCar);
                    }
                }
                response.setScheduleCarList(scheduleCarList);
                response.setScheduleMachineList(scheduleMachineList);
                scheduleResponseList.add(response);
            }
            if(StringUtils.isNotEmpty(message))
                Thread.sleep(4000L);
            smartminingScheduleService.saveNewSchedule(projectId, scheduleResponseList, 0);
            stringRedisTemplate.delete("scheduleTask");
        } catch (IOException e) {
            e.printStackTrace();
            stringRedisTemplate.delete("scheduleTask");
        } catch (InterruptedException e) {
            e.printStackTrace();
            stringRedisTemplate.delete("scheduleTask");
        }
    }
}
