package com.seater.smartmining.quartz.job;

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

import java.io.IOException;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/20 0020 15:11
 */
@Slf4j
@Component
@EnableScheduling
@Configuration
public class SlagSitePositionJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        /*DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String cmdInd = jobDataMap.getString("cmdInd");
        String topic = jobDataMap.getString("topic");
        Long pktId = jobDataMap.getLong("pktId");
        Long slagCarId = jobDataMap.getLong("slagCarId");
        Long projectId = jobDataMap.getLong("projectId");
        handler.handleSlagSitePosition(projectId, topic, cmdInd, pktId, slagCarId);*/
    }
}
