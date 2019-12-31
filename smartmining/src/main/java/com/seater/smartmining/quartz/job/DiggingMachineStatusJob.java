package com.seater.smartmining.quartz.job;

import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 10:03
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class DiggingMachineStatusJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String cmdInd = jobDataMap.getString("cmdInd");
            String topic = jobDataMap.getString("topic");
            Long pktId = jobDataMap.getLong("pktId");
            Long machineId = jobDataMap.getLong("machineId");
            Integer status = jobDataMap.getInt("status");
            Long projectId = jobDataMap.getLong("projectId");
            String deviceId = jobDataMap.getString("deviceId");
            //Integer choose = jobDataMap.getInt("choose");
            Long createId = jobDataMap.getLong("createId");
            String createName = jobDataMap.getString("createName");
            handler.handleMessageOnOff(cmdInd, topic, pktId, projectId, machineId, status,  deviceId, "定时重发", createId, createName, false);
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
        }
    }
}
