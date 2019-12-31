package com.seater.smartmining.quartz.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 12:42
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class ReplyMachineScheduleInfoJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try{
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String cmdInd = jobDataMap.getString("cmdInd");
            String topic = jobDataMap.getString("topic");
            Long pktId = jobDataMap.getLong("pktId");
            Long machineId = jobDataMap.getLong("machineId");
            Long projectId = jobDataMap.getLong("projectId");
            String deviceId = jobDataMap.getString("deviceId");
            handler.handleMessageSchedule(cmdInd, topic, pktId, projectId, machineId, deviceId, "云端主动请求");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
