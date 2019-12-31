package com.seater.smartmining.utils.api;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.entity.ProjectDeviceStatus;
import com.seater.smartmining.enums.ProjectDeviceType;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.OtherDeviceStatusJob;
import com.seater.smartmining.service.ProjectDeviceServiceI;
import com.seater.smartmining.utils.params.Result;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 17:31
 */
@Service
public class WorkStartOrEndService {

    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private Long count = 0L;

    public void sendMessageToDevice(CarType carType, Long projectId, String deviceCode, Long deviceId, Integer returnStatus) {
        ProjectDeviceType projectDeviceType = ProjectDeviceType.Unknown;
        if (carType.compareTo(CarType.Forklift) == 0)
            projectDeviceType = ProjectDeviceType.ForkliftDevice;
        else if (carType.compareTo(CarType.Roller) == 0)
            projectDeviceType = ProjectDeviceType.RollerDevice;
        else if (carType.compareTo(CarType.GunHammer) == 0)
            projectDeviceType = ProjectDeviceType.GunHammerDevice;
        else if (carType.compareTo(CarType.SingleHook) == 0)
            projectDeviceType = ProjectDeviceType.SingleHookDevice;
        else if (carType.compareTo(CarType.WateringCar) == 0)
            projectDeviceType = ProjectDeviceType.WateringCarDevice;
        else if (carType.compareTo(CarType.Scraper) == 0)
            projectDeviceType = ProjectDeviceType.ScraperDevice;
        ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, deviceCode, projectDeviceType.getAlian());
        if (projectDevice == null)
            throw new SmartminingProjectException("未查询到对应设备的终端编号，终端与云端同步失败");
        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
            String cmdInd = "onOff";
            String method = "request";
            String replytopic = "smartmining/otherdevice/cloud/" + projectDevice.getUid() + "/" + method;
            Long pktID = count;
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("cmdInd", cmdInd);
            jobDataMap.put("topic", replytopic);
            jobDataMap.put("pktId", pktID);
            jobDataMap.put("otherDeviceId", deviceId);
            jobDataMap.put("carType", carType.getValue());
            jobDataMap.put("status", returnStatus);
            jobDataMap.put("projectId", projectId);
            jobDataMap.put("deviceId", projectDevice.getUid());
            jobDataMap.put("choose", 0);
            String cron = QuartzConstant.MQTT_REPLY_CRON;
            quartzManager.addJob(QuartzManager.createJobNameOtherDeviceWork(deviceId), OtherDeviceStatusJob.class, cron, jobDataMap);
            Integer requestCount = 0;
            stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_OTHER_DEVICE_WORK + deviceId, requestCount.toString());
            count++;
        }else{
            throw new SmartminingProjectException("终端已离线，请上线后再审核。");
        }
    }
}
