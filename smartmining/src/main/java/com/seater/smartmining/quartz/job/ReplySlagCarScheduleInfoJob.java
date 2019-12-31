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
 * @Date 2019/9/19 0019 13:03
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class ReplySlagCarScheduleInfoJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String cmdInd = jobDataMap.getString("cmdInd");
            String topic = jobDataMap.getString("topic");
            Long pktId = jobDataMap.getLong("pktId");
            Long slagCarId = jobDataMap.getLong("slagCarId");
            Long projectId = jobDataMap.getLong("projectId");
            String deviceId = jobDataMap.getString("deviceId");
            handler.handleMessageScheduleAllByCar("scheduleall", topic, pktId, projectId, slagCarId, deviceId, "云端主动请求");
            Thread.sleep(5 * 1000);
            handler.handleMessageScheduleByCar(cmdInd, topic, pktId, projectId, slagCarId, deviceId, "云端主动请求");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
