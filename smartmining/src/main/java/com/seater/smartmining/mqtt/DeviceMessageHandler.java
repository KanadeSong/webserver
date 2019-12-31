package com.seater.smartmining.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.entity.repository.ProjectCarRepository;
import com.seater.smartmining.entity.repository.ProjectScheduleRepository;
import com.seater.smartmining.entity.repository.ScheduleCarRepository;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.manager.InterPhoneManager;
import com.seater.smartmining.mqtt.domain.*;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.SlagCarStatusJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.JSONArraySortUtils;
import com.seater.smartmining.utils.LocationUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.dao.GlobalSet;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import lombok.extern.slf4j.Slf4j;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class DeviceMessageHandler implements MessageHandler {

    @Autowired
    MqttSender mqttSender;
    @Autowired
    ProjectCheckLogServiceI projectCheckLogServiceI;
    @Autowired
    ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    ProjectLoadLogServiceI projectLoadLogServiceI;
    @Autowired
    ProjectServiceI projectServiceI;
    @Autowired
    ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    ProjectCarLoadMaterialSetServiceI projectCarLoadMaterialSetServiceI;
    @Autowired
    ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    ProjectCarMaterialServiceI projectCarMaterialServiceI;
    @Autowired
    ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ProjectAppStatisticsByCarServiceI projectAppStatisticsByCarServiceI;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    private WorkMergeErrorLogServiceI workMergeErrorLogServiceI;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectScheduleDetailServiceI projectScheduleDetailServiceI;
    @Autowired
    private ProjectCarCountServiceI projectCarCountServiceI;
    @Autowired
    private ProjectSlagCarLogServiceI projectSlagCarLogServiceI;
    @Autowired
    private ProjectDiggingWorkLogServiceI projectDiggingWorkLogServiceI;
    @Autowired
    private ProjectOtherDeviceServiceI projectOtherDeviceServiceI;
    @Autowired
    private ProjectOtherDeviceWorkInfoServiceI projectOtherDeviceWorkInfoServiceI;
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private InterPhoneManager interPhoneManager;
    @Autowired
    private CarOrderServiceI carOrderServiceI;
    @Autowired
    private SmartminingExceptionService smartminingExceptionService;
    @Autowired
    private ProjectCarRepository projectCarRepository;
    @Autowired
    private ProjectErrorLoadLogServiceI projectErrorLoadLogServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private ProjectScheduleRepository projectScheduleRepository;
    @Autowired
    private ScheduleCarRepository scheduleCarRepository;
    @Autowired
    private ProjectMachineLocationServiceI projectMachineLocationServiceI;
    @Autowired
    private ProjectMqttUpdateExctServiceI projectMqttUpdateExctServiceI;
    @Autowired
    private ProjectWorkTimeByCarServiceI projectWorkTimeByCarServiceI;
    @Autowired
    private ProjectSystemMqttLogServiceI projectSystemMqttLogServiceI;
    @Autowired
    private ProjectDeviceStatusLogServiceI projectDeviceStatusLogServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingLogServiceI projectWorkTimeByDiggingLogServiceI;
    @Autowired
    private ProjectCarCountLogServiceI projectCarCountLogServiceI;
    @Autowired
    private ProjectDeviceElectrifyLogServiceI projectDeviceElectrifyLogServiceI;
    @Autowired
    private SysUserServiceI sysUserServiceI;
    @Autowired
    private ProjectCarEfficiencyServiceI projectCarEfficiencyServiceI;

    ValueOperations<String, String> valueOps = null;

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }

    Integer count = 0;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();

            Pattern pattern = Pattern.compile("smartmining/([^/]*)/device/([^/]*)/([^/]*)");
            /*Pattern pattern = Pattern.compile("smartmining/([^/]*)/([^/]*)/([^/]*)/([^/]*)");*/
            Matcher matcher = pattern.matcher(topic);
            if (matcher.find()) {
                String device = matcher.group(1);
                String deviceId = matcher.group(2);
                String method = matcher.group(3);

                /*String device = matcher.group(1);
                String which = matcher.group(2);
                String deviceId = matcher.group(3);
                String method = matcher.group(4);*/

                String payload = message.getPayload().toString();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = mapper.readValue(payload, Map.class).get("cmdInd");
                String cmdInd = obj == null ? "" : obj.toString();
                obj = mapper.readValue(message.getPayload().toString(), Map.class).get("pktID");
                Long pktID = obj == null ? 0L : Long.parseLong(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("projectID");
                Long projectId = obj == null ? 0L : Long.parseLong(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("excavatorID");
                Long machineId = obj == null ? 0L : Long.parseLong(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("carID");
                Long carId = obj == null ? 0L : Long.parseLong(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("status");
                Integer status = obj == null ? 0 : Integer.valueOf(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("slagcarID");
                Long slagCarId = obj == null ? 0L : Long.parseLong(obj.toString());
                //渣车编号
                obj = mapper.readValue(payload, Map.class).get("slagcarCode");
                String slagcarCode = obj == null ? "" : obj.toString();
                //终端对应的挖机排班编号
                obj = mapper.readValue(payload, Map.class).get("schexctCode");
                String scheduleCode = obj == null ? "" : obj.toString();
                // 渣车出入渣场标志
                obj = mapper.readValue(payload, Map.class).get("isEnter");
                Boolean isEnter = obj != null && Boolean.parseBoolean(obj.toString());
                // 渣场id
                obj = mapper.readValue(payload, Map.class).get("slagfieldID");
                String slagSiteId = obj == null ? "" : obj.toString();
                //挖机编号
                obj = mapper.readValue(payload, Map.class).get("carCode");
                String carCode = obj == null ? "" : obj.toString();
                obj = mapper.readValue(payload, Map.class).get("longitude");
                //经度
                BigDecimal longitude = obj == null ? BigDecimal.ZERO : new BigDecimal(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("latitude");
                BigDecimal latitude = obj == null ? BigDecimal.ZERO : new BigDecimal(obj.toString());

                //其它设备新增
                obj = mapper.readValue(payload, Map.class).get("carType");
                Integer carType = obj == null ? 0 : Integer.valueOf(obj.toString());
                obj = mapper.readValue(payload, Map.class).get("otherDeviceId");
                Long otherDeviceId = obj == null ? 0 : Long.parseLong(obj.toString());

                String replytopic = "smartmining/" + device + "/cloud/" + deviceId + "/reply";
                //Project project = projectServiceI.get(projectId);
                //将终端修改成在线状态
                handleDeviceStatus(deviceId);
                if (device.equals("detector")) {
                    switch (cmdInd) {
                        case "carLog":
                            ProjectCheckLog projectCheckLog = handleDetectorMessageCarLog(payload, replytopic, deviceId, device, pktID);
                            if (projectCheckLog != null)
                                updateCarWorkInfo(projectCheckLog);
                            break;

                        case "datetime":
                            handleMessageDatetime(cmdInd, replytopic, pktID);
                            break;

                        case "picture":
                            break;

                        case "pictureErrList":
                            break;

                        case "pictureErrData":
                            break;

                        case "highdataUp":
                            break;
                    }
                    if (method.equals("post"))
                        handleMessagePost(payload, deviceId, ProjectDeviceType.DetectionDevice);

                } else if (device.equals("excavator")) {
                    switch (cmdInd) {
                        case "carLog":
                            /*ProjectLoadLog projectLoadLog = */
                            handleExcavatorMessageCarLog(payload, replytopic, deviceId, device, pktID);
                            //todo 取消挖机终端数据上传的合并
                            /*if (project.getProjectType().compareTo(ProjectType.UpgradeVersion) == 0 || project.getProjectType().compareTo(ProjectType.CompleteVersion) == 0) {
                                if (projectLoadLog != null)
                                    updateCarWorkInfo(projectLoadLog);
                            }*/
                            break;
                        case "datetime":
                            handleMessageDatetime(cmdInd, replytopic, pktID);
                            break;
                        case "schedule":
                            if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameScheduleMachine(deviceId));
                                    log.info("发送成功！");
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_MACHINE_SCHEDULE + deviceId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 20) {
                                        quartzManager.removeJob(QuartzManager.createJobNameScheduleMachine(deviceId));
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_SCHEDULE + deviceId, count.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                                    }
                                    log.info("发送失败！");
                                }
                            } else {
                                handleMessageSchedule(cmdInd, replytopic, pktID, projectId, machineId, deviceId, payload);
                            }
                            break;
                        case "getExcavator":
                            handleMessageGetExcavator(cmdInd, replytopic, pktID, projectId, carId, deviceId, payload);
                            break;
                        case "onOff":
                            if (method.equals("reply")) {
                                if (mapper.readValue(payload, Map.class).get("cmdStatus") != null) {
                                    Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                    if (cmdStatus == 0) {
                                        /*ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
                                        if (machine.getStatus().compareTo(DiggingMachineStatus.Working) != 0 && machine.getStatus().compareTo(DiggingMachineStatus.Stop) != 0)
                                            examWorkInfoByDigging(projectId, status, machineId);*/
                                        quartzManager.removeJob(QuartzManager.createJobNameMachineWork(machineId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_MACHINE_WORK + machineId);
                                        log.info("发送成功！");
                                    } else {
                                        String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_MACHINE_WORK + machineId);
                                        Integer count = Integer.valueOf(countStr);
                                        if (count == 20) {
                                            quartzManager.removeJob(QuartzManager.createJobNameMachineWork(machineId));
                                            stringRedisTemplate.delete(QuartzConstant.TASK_MACHINE_WORK + machineId);
                                        } else {
                                            count++;
                                            stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + machineId, count.toString(), 60 * 2, TimeUnit.SECONDS);
                                        }
                                        log.info("发送失败！");
                                    }
                                } else {
                                    log.info("返回状态为空！");
                                }
                            } else {
                                //handleMessageOnOff(cmdInd, replytopic, pktID, projectId, machineId, status, true, deviceId, 0, payload, -1L, "终端主动请求", true);
                                handleMessageOnOff(cmdInd, replytopic, pktID, projectId, machineId, status, deviceId, payload, -1L, "终端主动请求", true);
                            }
                            break;
                        case "status":
                            handleStatusByMachine(cmdInd, replytopic, pktID, projectId, machineId);
                            break;
                        case "currentData":
                            handleCurrentDataByMachine(cmdInd, replytopic, projectId, machineId, pktID, payload);
                            break;
                        case "reporting":
                            handleDiggingMachineFault(cmdInd, pktID, projectId, machineId, replytopic, deviceId, method, DiggingMachineStopStatus.Fault);
                            break;
                        case "disable":
                            handleDiggingMachineFault(cmdInd, pktID, projectId, machineId, replytopic, deviceId, method, DiggingMachineStopStatus.STOP);
                            break;
                        case "pause":
                            handleMessagePause(cmdInd, replytopic, machineId, carCode, projectId, pktID, status);
                            break;
                        case "getpause":
                            handMessageGetPause(cmdInd, replytopic, machineId, carCode, projectId, pktID);
                            break;
                    }
                    if (method.equals("post"))
                        handleMessagePost(payload, deviceId, ProjectDeviceType.DiggingMachineDevice);
                } else if (device.equals("slagfield")) {
                    switch (cmdInd) {
                        case "carLog":
                            //终端请求日志
                            //ProjectSystemMqttLog projectSystemMqttLog = saveLog(payload, projectId, deviceId, ProjectMqttEnum.SlagSiteWork);
                            ProjectUnloadLog projectUnloadLog = handleSlagfieldMessageCarLog(payload, replytopic, deviceId, device, pktID/*, projectSystemMqttLog*/);
                            if (projectUnloadLog != null) {
                                ProjectCarWorkInfo projectCarWorkInfo = updateCarWorkInfoNew(projectUnloadLog);
                                if (projectCarWorkInfo != null) {
                                    if (projectCarWorkInfo.getStatus().compareTo(ProjectCarWorkStatus.Finish) == 0L) {
                                        //app即时报表展示的数据
                                        ProjectAppStatisticsByCar appCar = projectAppStatisticsByCarServiceI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, projectCarWorkInfo.getCarCode(), projectCarWorkInfo.getShift().getAlias(), projectCarWorkInfo.getDateIdentification());
                                        if (appCar == null) {
                                            appCar = new ProjectAppStatisticsByCar();
                                        }
                                        appCar.setCubic(appCar.getCubic() + projectCarWorkInfo.getCubic());
                                        appCar.setCarCount(appCar.getCarCount() + 1);
                                        appCar.setShift(ShiftsEnums.converShift(projectCarWorkInfo.getShift().getAlias()));
                                        appCar.setProjectId(projectId);
                                        appCar.setCarCode(projectCarWorkInfo.getCarCode());
                                        appCar.setCreateDate(projectCarWorkInfo.getDateIdentification());
                                        projectAppStatisticsByCarServiceI.save(appCar);
                                        if (projectCarWorkInfo.getIsVaild().compareTo(VaildEnums.NOTVAILDBYCAR) != 0)
                                            saveCarInfo(projectId, projectCarWorkInfo.getCarCode(), carId, CarType.SlagCar, projectCarWorkInfo.getShift(), projectCarWorkInfo.getDateIdentification(), projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), projectCarWorkInfo.getTimeDischarge(), projectCarWorkInfo.getPayableDistance());
                                        saveCarInfo(projectId, projectCarWorkInfo.getDiggingMachineCode(), projectCarWorkInfo.getDiggingMachineId(), CarType.DiggingMachine, projectCarWorkInfo.getShift(), projectCarWorkInfo.getDateIdentification(), projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), projectCarWorkInfo.getTimeDischarge(), projectCarWorkInfo.getPayableDistance());
                                    }
                                    cmdInd = "slagcar";
                                    replytopic = "smartmining/cloud/count/post/reply";
                                    handleAndroidAppGetStatus(cmdInd, replytopic, projectId, projectUnloadLog.getCarCode());
                                }
                            }
                            break;
                        case "datetime":
                            handleMessageDatetime(cmdInd, replytopic, pktID);
                            break;
                        case "position":
                            break;
                        case "config":
                            break;
                        case "":
                            break;
                        case "interPhone":
                            // 渣车出入渣场
                            handleSlagSiteCarInOut(cmdInd, projectId, carId, slagSiteId, isEnter, pktID, deviceId);
                            break;
                        default:
                            break;
                    }
                    if (method.equals("post"))
                        handleMessagePost(payload, deviceId, ProjectDeviceType.SlagFieldDevice);
                } else if (device.equals("slagcar")) {
                    switch (cmdInd) {
                        case "carLog":
                            ProjectSlagCarLog projectSlagCarLog = handleMessageSlagCar(payload, replytopic, deviceId, device, pktID);
                            if (projectSlagCarLog != null) {
                                ProjectCarWorkInfo projectCarWorkInfo = updateCarWorkInfo(projectSlagCarLog);
                                if (projectCarWorkInfo != null) {
                                    if (projectCarWorkInfo.getStatus().compareTo(ProjectCarWorkStatus.Finish) == 0L) {
                                        //app即时报表展示的数据
                                        ProjectAppStatisticsByCar appCar = projectAppStatisticsByCarServiceI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, projectCarWorkInfo.getCarCode(), projectCarWorkInfo.getShift().getAlias(), projectCarWorkInfo.getDateIdentification());
                                        if (appCar == null) {
                                            appCar = new ProjectAppStatisticsByCar();
                                        }
                                        appCar.setCubic(appCar.getCubic() + projectCarWorkInfo.getCubic());
                                        appCar.setCarCount(appCar.getCarCount() + 1);
                                        appCar.setShift(ShiftsEnums.converShift(projectCarWorkInfo.getShift().getAlias()));
                                        appCar.setProjectId(projectId);
                                        appCar.setCarCode(projectCarWorkInfo.getCarCode());
                                        appCar.setCreateDate(projectCarWorkInfo.getDateIdentification());
                                        projectAppStatisticsByCarServiceI.save(appCar);
                                        saveCarInfo(projectId, projectCarWorkInfo.getCarCode(), carId, CarType.SlagCar, projectCarWorkInfo.getShift(), projectCarWorkInfo.getDateIdentification(), projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), projectCarWorkInfo.getTimeDischarge(), projectCarWorkInfo.getPayableDistance());
                                        saveCarInfo(projectId, projectCarWorkInfo.getDiggingMachineCode(), projectCarWorkInfo.getDiggingMachineId(), CarType.DiggingMachine, projectCarWorkInfo.getShift(), projectCarWorkInfo.getDateIdentification(), projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), projectCarWorkInfo.getTimeDischarge(), projectCarWorkInfo.getPayableDistance());
                                    }
                                }
                            }
                            break;
                        case "datetime":
                            handleMessageDatetime(cmdInd, replytopic, pktID);
                            break;
                        case "currentData":
                            handleCurrentDataByCar(cmdInd, replytopic, projectId, slagCarId, pktID, payload);
                            break;
                        case "getexctfix":
                            handleMessageGetexctfix(cmdInd, replytopic, slagCarId, projectId, pktID, longitude, latitude, slagcarCode, scheduleCode);
                            break;
                        case "updateexct":
                            handleMessageUpdateExct(cmdInd, replytopic, slagCarId, projectId, pktID, slagcarCode, scheduleCode);
                            break;
                        case "schedule":
                            if (projectScheduleDetailServiceI.isAuto(deviceId)) {
                                quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                break;
                            }

                            if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                    stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    log.info("发送成功！");
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 20) {
                                        quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId, count.toString(), 10 * 60 * 2, TimeUnit.SECONDS);
                                    }
                                    log.info("发送失败！");
                                }
                            } else {
                                handleMessageScheduleByCar(cmdInd, replytopic, pktID, projectId, slagCarId, deviceId, payload);
                            }
                            break;
                        case "scheduleall":
                            if (projectScheduleDetailServiceI.isAuto(deviceId)) {
                                quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                break;
                            }

                            if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                    stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    log.info("发送成功！");
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 20) {
                                        quartzManager.removeJob(QuartzManager.createJobNameScheduleSlagSiteCar(deviceId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId);
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + deviceId, count.toString(), 10 * 60 * 2, TimeUnit.SECONDS);
                                    }
                                    log.info("发送失败！");
                                }
                            } else {
                                handleMessageScheduleAllByCar(cmdInd, replytopic, pktID, projectId, slagCarId, deviceId, payload);
                            }
                            break;
                        case "status":
                            handleSlagCarStatusQuery(cmdInd, pktID, projectId, slagCarId, slagcarCode, replytopic);
                            break;
                        case "onOff":
                            if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameSlagCarWork(slagCarId));
                                    stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_CAR_WORK + slagSiteId);
                                    log.info("发送成功！");
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_SLAG_CAR_WORK + deviceId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 20) {
                                        quartzManager.removeJob(QuartzManager.createJobNameSlagCarWork(slagCarId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_CAR_WORK + slagCarId);
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_CAR_WORK + deviceId, count.toString(), 10 * 60 * 2, TimeUnit.SECONDS);
                                    }
                                    log.info("发送失败！");
                                }
                            } else {
                                handleSlagCarStatus(cmdInd, replytopic, projectId, slagcarCode, status, pktID, deviceId, slagCarId);
                            }
                            break;
                        case "position":
                            /*if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameSlagSitePosition(deviceId));
                                    stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_POSITION + deviceId);
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_SLAG_SITE_POSITION + deviceId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 100) {
                                        quartzManager.removeJob(QuartzManager.createJobNameSlagSitePosition(deviceId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_SLAG_SITE_POSITION + deviceId);
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_POSITION + deviceId, count.toString(), 10 * 60 * 2, TimeUnit.SECONDS);
                                    }
                                }
                            } else {
                                handleSlagSitePosition(projectId, replytopic, cmdInd, pktID, carId);
                            }*/
                            break;
                        case "detail":
                            handleErrorMessageDetail(cmdInd, replytopic, projectId, pktID, slagcarCode);
                            break;
                    }
                    if (method.equals("post"))
                        handleMessagePost(payload, deviceId, ProjectDeviceType.SlagTruckDevice);
                } else if (device.equals("otherdevice")) {
                    switch (cmdInd) {
                        case "onOff":
                            if (method.equals("reply")) {
                                Integer cmdStatus = Integer.valueOf(mapper.readValue(payload, Map.class).get("cmdStatus").toString());
                                if (cmdStatus == 0) {
                                    quartzManager.removeJob(QuartzManager.createJobNameOtherDeviceWork(machineId));
                                    stringRedisTemplate.delete(QuartzConstant.TASK_OTHER_DEVICE_WORK + machineId);
                                    log.info("发送成功！");
                                } else {
                                    String countStr = stringRedisTemplate.opsForValue().get(QuartzConstant.TASK_OTHER_DEVICE_WORK + machineId);
                                    Integer count = Integer.valueOf(countStr);
                                    if (count == 20) {
                                        quartzManager.removeJob(QuartzManager.createJobNameOtherDeviceWork(machineId));
                                        stringRedisTemplate.delete(QuartzConstant.TASK_OTHER_DEVICE_WORK + machineId);
                                    } else {
                                        count++;
                                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_OTHER_DEVICE_WORK + machineId, count.toString(), 10 * 60 * 2, TimeUnit.SECONDS);
                                    }
                                    log.info("发送失败！");
                                }
                            } else {
                                handleMessageOtherDeviceOnOff(cmdInd, replytopic, pktID, projectId, otherDeviceId, status, carType, 1);
                            }
                            break;
                        case "synchronization":
                            synchronizationOtherDevice(projectId, otherDeviceId, deviceId, carType);
                            break;
                    }
                }
            } else {
                pattern = Pattern.compile("smartmining/device/fix/([^/]*)/([^/]*)/([^/]*)");
                matcher = pattern.matcher(topic);

                Pattern pattern2 = Pattern.compile("smartmining/device/fix/([^/]*)/([^/]*)/([^/]*)/([^/]*)");
                Matcher matcher2 = pattern2.matcher(topic);

                if (matcher2.find()) {
                    //判断终端类型
                    String device = matcher2.group(1);
                    String machineId = matcher2.group(2);
                    String deviceId = matcher2.group(3);
                    String payload = message.getPayload().toString();
                    String uid = "";
                    if (StringUtils.isNotEmpty(deviceId))
                        uid = deviceId;
                    else
                        uid = machineId;
                    handleDeviceStatus(uid);
                    //--------------
                    //projectScheduleDetailServiceI.initByFix(deviceId, machineId, payload, true);
                } else if (matcher.find()) {
                    //判断终端类型
                    String device = matcher.group(1);
                    String machineId = matcher.group(2);
                    String deviceId = matcher.group(3);
                    String payload = message.getPayload().toString();
                    //--------------
                    //projectScheduleDetailServiceI.initByFix(deviceId, machineId, payload, false);

                    ObjectMapper mapper = new ObjectMapper();
                    Object obj = mapper.readValue(message.getPayload().toString(), Map.class).get("pktID");
                    Long pktID = obj == null ? 0L : Long.parseLong(obj.toString());
                    obj = mapper.readValue(payload, Map.class).get("projectID");
                    Long projectId = obj == null ? 0L : Long.parseLong(obj.toString());
                    obj = mapper.readValue(payload, Map.class).get("latitude");
                    BigDecimal latitude = obj == null ? new BigDecimal(0) : new BigDecimal(obj.toString());
                    obj = mapper.readValue(payload, Map.class).get("longitude");
                    BigDecimal longitude = obj == null ? new BigDecimal(0) : new BigDecimal(obj.toString());
                    obj = mapper.readValue(payload, Map.class).get("altitude");
                    BigDecimal altitude = obj == null ? new BigDecimal(0) : new BigDecimal(obj.toString());
                    obj = mapper.readValue(payload, Map.class).get("devCode");
                    String devCode = obj == null ? "" : obj.toString();
                    obj = mapper.readValue(payload, Map.class).get("devID");
                    Long devId = obj == null ? 0L : Long.parseLong(obj.toString());
                    //距离
                    obj = mapper.readValue(payload, Map.class).get("distance");
                    BigDecimal distance = obj == null ? new BigDecimal(0) : new BigDecimal(obj.toString());
                    String uid = "";
                    if (StringUtils.isNotEmpty(deviceId))
                        uid = deviceId;
                    else
                        uid = machineId;
                    //判断终端是否在线
                    ProjectDevice projectDevice = handleDeviceStatus(uid);
                    //ProjectDevice projectDevice = projectDeviceServiceI.getByProjectIdAndUid(projectId, uid);
                    if (projectDevice != null) {
                        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                            Date date = new Date();
                            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
                            Date startTime = dateMap.get("start");
                            Date dateIdentification = DateUtils.createReportDateByMonth(date);
                            if(date.getTime() < startTime.getTime())
                                dateIdentification = DateUtils.createReportDateByMonth(DateUtils.getAddDate(dateIdentification, -1));
                            Shift shift = workDateService.getShift(date, projectId);
                            //todo 保存终端对应的定位坐标数据
                            String positionMessage = stringRedisTemplate.opsForValue().get(uid + projectId + dateIdentification.getTime() + shift.getAlias() + "positionMessage");
                            List<Map> positionMapList = new ArrayList<>();
                            Map map = new HashMap();
                            map.put("date", date);
                            map.put("longitude", longitude);
                            map.put("latitude", latitude);
                            if(StringUtils.isNotEmpty(positionMessage))
                                positionMapList = JSONArray.parseArray(positionMessage, Map.class);
                            positionMapList.add(map);
                            stringRedisTemplate.opsForValue().set(uid + projectId + dateIdentification.getTime() + shift.getAlias() + "positionMessage", JSON.toJSONString(positionMapList));
                            if (longitude.compareTo(BigDecimal.ZERO) != 0)
                                projectDevice.setLongitude(longitude);
                            if (latitude.compareTo(BigDecimal.ZERO) != 0)
                                projectDevice.setLatitude(latitude);
                            projectDevice.setAltitude(altitude);
                            projectDevice.setDistance(distance);
                            projectDevice.setDeviceCode(uid);
                            projectDevice.setCode(devCode);
                            projectDevice.setCarId(devId);
                            projectDeviceServiceI.save(projectDevice);
                        }
                    } else {
                        log.error("uid编号:" + uid + " 不存在");
                    }
                } else {
                    pattern = Pattern.compile("smartmining/app/([^/]*)/([^/]*)/([^/]*)");
                    matcher = pattern.matcher(topic);
                    if (matcher.find()) {
                        String first = matcher.group(1);
                        String request = matcher.group(2);
                        String payload = message.getPayload().toString();
                        ObjectMapper mapper = new ObjectMapper();
                        Object obj = mapper.readValue(payload, Map.class).get("cmdInd");
                        //cmdInd
                        String cmdInd = obj == null ? "" : obj.toString();
                        obj = mapper.readValue(payload, Map.class).get("carCode");
                        //carCode
                        String carCode = obj == null ? "" : obj.toString();
                        obj = mapper.readValue(payload, Map.class).get("projectID");
                        //projectId
                        Long projectId = obj == null ? 0L : Long.parseLong(obj.toString());
                        obj = mapper.readValue(payload, Map.class).get("status");
                        Integer status = obj == null ? 0 : Integer.valueOf(obj.toString());
                        //carId
                        obj = mapper.readValue(payload, Map.class).get("slagcarID");
                        Long carId = obj == null ? 0L : Long.parseLong(obj.toString());
                        //password
                        obj = mapper.readValue(payload, Map.class).get("password");
                        String password = obj == null ? "" : obj.toString();
                        //newPassword
                        obj = mapper.readValue(payload, Map.class).get("newPwd");
                        String newPwd = obj == null ? "" : obj.toString();
                        obj = mapper.readValue(payload, Map.class).get("excavatorID");
                        Long machineId = obj == null ? 0L : Long.parseLong(obj.toString());
                        obj = mapper.readValue(payload, Map.class).get("createId");
                        Long createId = obj == null ? 0L : Long.parseLong(obj.toString());
                        obj = mapper.readValue(payload, Map.class).get("createName");
                        String createName = obj == null ? "" : obj.toString();
                        obj = mapper.readValue(payload, Map.class).get("account");
                        String account = obj == null ? "" : obj.toString();
                        obj = mapper.readValue(payload, Map.class).get("longitude");
                        BigDecimal longitude = obj == null ? BigDecimal.ZERO : new BigDecimal(obj.toString());
                        obj = mapper.readValue(payload, Map.class).get("latitude");
                        BigDecimal latitude = obj == null ? BigDecimal.ZERO : new BigDecimal(obj.toString());
                        String replytopic = "smartmining/cloud/" + first + "/" + request + "/reply";
                        switch (cmdInd) {
                            case "getproject":
                                handleAndroidAppGetProject(cmdInd, replytopic);
                                break;
                            case "slagcar":
                                handleAndroidAppGetStatus(cmdInd, replytopic, projectId, carCode);
                                break;
                            case "slagcarstats":
                                if (createId == null || createId == 0L)
                                    createId = -2L;
                                if (StringUtils.isEmpty(createName))
                                    createName = "APP操作";
                                handleAndroidAppCarStatus(cmdInd, replytopic, projectId, carCode, status, "app", 0L, "", 0L, createId, createName);
                                break;
                            case "account":
                                handleAndroidDriverLogin(cmdInd, projectId, carCode, password);
                                break;
                            case "resetPwd":
                                handleAndroidDriverResetPwd(cmdInd, projectId, carCode, carId, password, newPwd);
                                break;
                            case "currentData":
                                handleAppCurrentData(cmdInd, replytopic, projectId, machineId);
                                break;
                            case "detail":
                                handleErrorMessageDetailByApp(cmdInd, replytopic, projectId, carCode);
                                break;
                            case "position":
                                handleUserPositionByApp(cmdInd, replytopic, account, longitude, latitude);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            //mqttSender.sendDeviceReply(replytopic, new DeviceReply("carLog", pktID, cmdStatus));
        }

    }

    /**
     * 保存车辆信息
     * todo 增加无效车数
     *
     * @param projectId          项目ID
     * @param carCode            车辆编号
     * @param carId              车辆ID
     * @param carType            车辆类型
     * @param shift              班次
     * @param dateIdentification 日期标识
     * @param materialId         物料ID
     * @param materialName       物料名称
     * @param timeDischarge      卸载时间
     * @param distance           运距 渣车必须
     */
    public void saveCarInfo(Long projectId, String carCode, Long carId, CarType carType, Shift shift, Date dateIdentification, Long materialId, String materialName, Date timeDischarge, Long distance) throws IOException {
        ProjectCarCountLog log = new ProjectCarCountLog();
        log.setProjectId(projectId);
        log.setCarId(carId);
        log.setCarCode(carCode);
        log.setCarType(carType);
        log.setShift(shift);
        log.setDateIdentification(dateIdentification);
        log.setMaterialId(materialId);
        log.setMaterialName(materialName);
        log.setTimeDischarge(timeDischarge);
        log.setDistance(distance);
        projectCarCountLogServiceI.save(log);
        ProjectCarCount projectCarCount = projectCarCountServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, carCode, dateIdentification, shift.getAlias(), carType.getValue());
        if (projectCarCount == null) {
            projectCarCount = new ProjectCarCount();
            projectCarCount.setProjectId(projectId);
            projectCarCount.setCarId(carId);
            projectCarCount.setCarCode(carCode);
            projectCarCount.setTotalCount(1L);
            projectCarCount.setCarType(carType);
            projectCarCount.setShifts(shift);
            projectCarCount.setDateIdentification(dateIdentification);
            List<Map> detailList = new ArrayList<>();
            Map map = new HashMap();
            map.put("materialId", materialId);
            map.put("materialName", materialName);
            map.put("count", 1);
            detailList.add(map);
            String detailJson = JSON.toJSONString(detailList);
            projectCarCount.setDetailJson(detailJson);
        } else {
            JSONArray jsonArray = JSONArray.parseArray(projectCarCount.getDetailJson());
            List<Long> materialIdList = new ArrayList<>();
            List<Map> detailList = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String text = jsonArray.get(i).toString();
                Map detailMap = JSON.parseObject(text, new TypeReference<Map>() {
                });
                Long detailMaterialId = Long.parseLong(detailMap.get("materialId").toString());
                String detailMaterialName = detailMap.get("materialName").toString();
                Long materialCount = Long.parseLong(detailMap.get("count").toString());
                if (detailMaterialId == materialId) {
                    Map mapNew = new HashMap();
                    materialCount++;
                    mapNew.put("materialId", detailMaterialId);
                    mapNew.put("materialName", detailMaterialName);
                    mapNew.put("count", materialCount);
                    detailList.add(mapNew);
                } else {
                    detailList.add(detailMap);
                }
                materialIdList.add(detailMaterialId);
            }
            if (!materialIdList.contains(materialId)) {
                Map map = new HashMap();
                map.put("materialId", materialId);
                map.put("materialName", materialName);
                map.put("count", 1);
                detailList.add(map);
            }
            String json = JSON.toJSONString(detailList);
            projectCarCount.setDetailJson(json);
            projectCarCount.setTotalCount(projectCarCount.getTotalCount() + 1);
        }
        ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(carId);
        ProjectCar projectCar = projectCarServiceI.get(carId);
        if (carType.compareTo(CarType.DiggingMachine) == 0) {
            Long workTime = 0L;
            if (projectDiggingMachine != null) {
                workTime = projectWorkTimeByDiggingServiceI.getAllByProjectIdAndMaterialCodeAndDateIdentificationAndShift(projectId, projectDiggingMachine.getCode(), projectCarCount.getDateIdentification(), projectCarCount.getShifts().getAlias());
                if(projectDiggingMachine.getStartWorkTime() != null && projectDiggingMachine.getStartWorkTime().getTime() != 0){
                    if(projectDiggingMachine.getEndWorkTime() == null || projectDiggingMachine.getEndWorkTime().getTime() == 0){
                        workTime = workTime + DateUtils.calculationHour(projectDiggingMachine.getStartWorkTime(), new Date());
                    }
                }
            }
            projectCarCount.setWorkTime(workTime);
        } else if (carType.compareTo(CarType.SlagCar) == 0) {
            Map unValidCountMap = projectMqttCardReportServiceI.getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, dateIdentification, shift.getAlias());
            if (unValidCountMap != null) {
                Long unValidCount = Long.parseLong(unValidCountMap.get("count").toString());
                projectCarCount.setUnValidCount(unValidCount);
            }
            projectCarCount.setDistance(distance);
        }
        BigDecimal workTime = new BigDecimal((float) projectCarCount.getWorkTime() / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal efficiency = workTime.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(projectCarCount.getTotalCount()).divide(workTime, 2, BigDecimal.ROUND_HALF_DOWN) : BigDecimal.ZERO;
        ProjectCarEfficiency carEfficiency = new ProjectCarEfficiency();
        carEfficiency.setProjectId(projectCarCount.getProjectId());
        carEfficiency.setCarCode(projectCarCount.getCarCode());
        carEfficiency.setCarId(projectCarCount.getCarId());
        carEfficiency.setCarType(projectCarCount.getCarType());
        carEfficiency.setCarCount(projectCarCount.getTotalCount());
        carEfficiency.setDateIdentification(projectCarCount.getDateIdentification());
        carEfficiency.setShift(projectCarCount.getShifts());
        carEfficiency.setWorkTime(projectCarCount.getWorkTime());
        carEfficiency.setEfficiency(efficiency);
        if(projectCar != null){
            projectCar.setEfficiency(efficiency);
            projectCarServiceI.save(projectCar);
        }else if(projectDiggingMachine != null){
            projectDiggingMachine.setEfficiency(efficiency);
            projectDiggingMachineServiceI.save(projectDiggingMachine);
        }
        projectCarEfficiencyServiceI.save(carEfficiency);
        projectCarCountServiceI.save(projectCarCount);
    }

    //检测上传日志
    @Transactional(dontRollbackOn = IOException.class)
    public ProjectCheckLog handleDetectorMessageCarLog(String payload, String replytopic, String deviceId, String device, Long pktID) {
        Long cmdStatus = 0L;
        ProjectCheckLog ret = null;

        try {
            ProjectCheckLog projectCheckLog = JSONObject.parseObject(payload, ProjectCheckLog.class);
            if (projectCheckLog != null) {
                projectCheckLog.setTimeLoad(new Date(projectCheckLog.getTimeLoad().getTime() * 1000));
                projectCheckLog.setTimeCheck(new Date(projectCheckLog.getTimeCheck().getTime() * 1000));
                projectCheckLog.setTimeDischarge(new Date(projectCheckLog.getTimeDischarge().getTime() * 1000));

                ret = projectCheckLogServiceI.save(projectCheckLog);
            }
        } catch (Exception e) {
            smartminingExceptionService.save(e, payload);
            e.printStackTrace();
            cmdStatus = 1L;
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, new DeviceReply("carLog", pktID, cmdStatus));
            } catch (Exception ex) {
                //smartminingExceptionService.save(ex);
                ex.printStackTrace();
            }
        }

        return ret;
    }

    //检测上传
    @Transactional
    public void updateCarWorkInfo(ProjectCheckLog projectCheckLog) {
        try {
            Project project = projectServiceI.get(projectCheckLog.getProjectID());
            //if (project.getStatus() == ProjectStatus.Start)   //是否开工
            //{
            Long projectId = projectCheckLog.getProjectID();
            Long carId = projectCheckLog.getCarID();
            String carCode = projectCheckLog.getCarCode();
            Long cubic = projectCarServiceI.get(carId).getModifyCapacity();
            Date timeCheck = projectCheckLog.getTimeCheck();
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
            //todo 新增代码 分组新增选项 从而判断如何合并
            ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
            Long carOwnedId = scheduleCarList.get(0).getCarOwnerId();
            String carOwnedName = scheduleCarList.get(0).getCarOwnerName();
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
            Long diggingMachineId = scheduleMachineList.get(0).getMachineId();
            String diggingMachineCode = scheduleMachineList.get(0).getMachineCode();
            //挖机计价方式
            PricingTypeEnums pricingType = scheduleMachineList.get(0).getPricingType();
            Long materialId = scheduleMachineList.get(0).getMaterialId();
            String materialName = scheduleMachineList.get(0).getMaterialName();
            Integer standardHeight = projectCarLoadMaterialSetServiceI.getByProjectIdAndCarIDAndMaterialId(projectCheckLog.getProjectID(), projectCheckLog.getCarID(), scheduleMachineList.get(0).getMaterialId()).getStandardHeight();
            Integer height = projectCheckLog.getHeightAvg();
            if (projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.Check) == 0) {
                ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.get(projectCheckLog.getSlagfieldID());
                Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeCheck(projectId, carId, timeCheck);  //在作业表中寻找数据是否已经存在
                ProjectCarWorkInfo projectCarWorkInfo;
                if (object != null) { //存在
                    projectCarWorkInfo = (ProjectCarWorkInfo) object;
                    projectCarWorkInfo.setTimeCheck(timeCheck);
                    projectCarWorkInfo.setHeight(height);
                    //因为增强版只有检测站和渣场两个设备,所以如果存在一定是渣场上传的
                    if (scheduleMachineList.size() == 1 && projectCarWorkInfo.getUnLoadUp()) {//对应的只有一台挖机
                        if (projectSlagSite.getSlagSite().getAlias() == 4 || projectSlagSite.getSlagSite().getAlias() == 5) {
                            projectCarWorkInfo.setPass(projectCheckLog.getHeightAvg() >= standardHeight ? Score.Pass : Score.UnPass);
                        } else {
                            projectCarWorkInfo.setPass(Score.Pass);
                        }
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                        //app即时报表展示的数据
                        ProjectAppStatisticsByCar appCar = projectAppStatisticsByCarServiceI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, carCode, projectCarWorkInfo.getShift().getAlias(), projectCarWorkInfo.getDateIdentification());
                        if (appCar == null) {
                            appCar = new ProjectAppStatisticsByCar();
                        }
                        appCar.setCubic(appCar.getCubic() + projectCarWorkInfo.getCubic());
                        appCar.setCarCount(appCar.getCarCount() + 1);
                        appCar.setShift(ShiftsEnums.converShift(projectCarWorkInfo.getShift().getAlias()));
                        appCar.setProjectId(projectId);
                        appCar.setCarCode(carCode);
                        appCar.setCreateDate(projectCarWorkInfo.getDateIdentification());
                        projectAppStatisticsByCarServiceI.save(appCar);
                    }
                } else { //不存在
                    projectCarWorkInfo = new ProjectCarWorkInfo();
                    projectCarWorkInfo.setProjectId(projectId);
                    projectCarWorkInfo.setCarId(carId);
                    projectCarWorkInfo.setCarCode(carCode);
                    projectCarWorkInfo.setCubic(cubic);
                    projectCarWorkInfo.setTimeCheck(timeCheck);
                    projectCarWorkInfo.setHeight(height);

                    if (scheduleMachineList.size() == 1) {                 //对应的只有一台挖机
                        projectCarWorkInfo.setCarOwnerId(carOwnedId);
                        projectCarWorkInfo.setCarOwnerName(carOwnedName);
                        projectCarWorkInfo.setDiggingMachineId(diggingMachineId);
                        projectCarWorkInfo.setDiggingMachineCode(diggingMachineCode);
                        projectCarWorkInfo.setPricingType(pricingType);
                        projectCarWorkInfo.setMaterialId(materialId);
                        projectCarWorkInfo.setMateriaName(materialName);
                        if (projectSlagSite.getSlagSite().getAlias() == 4 || projectSlagSite.getSlagSite().getAlias() == 5) {
                            projectCarWorkInfo.setPass(projectCheckLog.getHeightAvg() >= standardHeight ? Score.Pass : Score.UnPass);
                        } else {
                            projectCarWorkInfo.setPass(Score.Pass);
                        }
                        projectCarWorkInfo.setPass(projectCheckLog.getHeightAvg() >= standardHeight ? Score.Pass : Score.UnPass);
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                    } else {
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Unknown);
                        projectCarWorkInfo.setRemark("不支持混装, 请购买升级版或完整版");
                    }
                }

                projectCarWorkInfo.setCheckUp(true);
                projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            } else if (projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.All) == 0) {
                ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.get(projectCheckLog.getSlagfieldID());
                Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, projectCheckLog.getTimeLoad());  //在作业表中寻找数据是否已经存在
                ProjectCarWorkInfo projectCarWorkInfo;
                if (object != null) {//存在
                    projectCarWorkInfo = (ProjectCarWorkInfo) object;
                    projectCarWorkInfo.setHeight(height);
                    projectCarWorkInfo.setTimeCheck(timeCheck);
                    projectCarWorkInfo.setCheckUp(true);

                    if (projectCarWorkInfo.getLoadUp()) { //如果挖机已上传
                        ScheduleMachine scheduleMachine = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, projectCheckLog.getExcavatCurrent(), true).get(0);
                        standardHeight = projectCarLoadMaterialSetServiceI.getByProjectIdAndCarIDAndMaterialId(projectId, carId, scheduleMachine.getMaterialId()).getStandardHeight();
                        if (projectSlagSite.getSlagSite().getAlias() == 4 || projectSlagSite.getSlagSite().getAlias() == 5) {
                            projectCarWorkInfo.setPass(projectCheckLog.getHeightAvg() >= standardHeight ? Score.Pass : Score.UnPass);
                        } else {
                            projectCarWorkInfo.setPass(Score.Pass);
                        }
                        projectCarWorkInfo.setPass(height >= standardHeight ? Score.Pass : Score.UnPass);
                        projectCarWorkInfo.setStatus(projectCarWorkInfo.getUnLoadUp() ? ProjectCarWorkStatus.Finish : ProjectCarWorkStatus.UnUnload);
                    } else {                                                   //挖机未上传
                        projectCarWorkInfo.setStatus(projectCarWorkInfo.getUnLoadUp() ? ProjectCarWorkStatus.WaitLoadUp : ProjectCarWorkStatus.UnUnload);
                    }
                } else { //不存在
                    projectCarWorkInfo = new ProjectCarWorkInfo();
                    projectCarWorkInfo.setProjectId(projectId);
                    projectCarWorkInfo.setCarId(carId);
                    projectCarWorkInfo.setCarCode(carCode);
                    projectCarWorkInfo.setCubic(cubic);
                    projectCarWorkInfo.setTimeCheck(timeCheck);
                    projectCarWorkInfo.setTimeLoad(projectCheckLog.getTimeLoad());
                    projectCarWorkInfo.setHeight(height);
                    if (projectSlagSite.getSlagSite().getAlias() == 4 || projectSlagSite.getSlagSite().getAlias() == 5) {
                        projectCarWorkInfo.setPass(projectCheckLog.getHeightAvg() >= standardHeight ? Score.Pass : Score.UnPass);
                    } else {
                        projectCarWorkInfo.setPass(Score.Pass);
                    }

                    projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                }

                projectCarWorkInfo.setCheckUp(true);
                projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            }
        } catch (Exception ex) {
            WorkMergeErrorLog errorLog = new WorkMergeErrorLog();
            errorLog.setProjectId(projectCheckLog.getProjectID());
            errorLog.setCarCode(projectCheckLog.getCarCode());
            errorLog.setCarId(projectCheckLog.getCarID());
            errorLog.setMessage(ex.getMessage());
            errorLog.setDetailMessage(JSON.toJSONString(ex.getStackTrace()));
            errorLog.setProjectDevice(ProjectDeviceType.DetectionDevice);
            errorLog.setTimeLoad(projectCheckLog.getTimeLoad());
            errorLog.setTimeCheck(projectCheckLog.getTimeCheck());
            errorLog.setTimeDischarge(projectCheckLog.getTimeDischarge());
            errorLog.setCreateDate(new Date());
            errorLog.setUid(projectCheckLog.getUid());
            errorLog.setEventId(projectCheckLog.getEventId());
            errorLog.setPktID(projectCheckLog.getPktID());
            try {
                workMergeErrorLogServiceI.save(errorLog);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("添加失败");
            }
            System.out.println(ex.getMessage());
            System.out.println("插入作业信息异常，可能是没有对应的排班信息或装载信息");
        }
    }

    //渣场上传日志
    @Transactional(dontRollbackOn = IOException.class)
    public ProjectUnloadLog handleSlagfieldMessageCarLog(String payload, String replytopic, String deviceId, String device, Long pktID/*, ProjectSystemMqttLog projectSystemMqttLog*/) throws IOException {
        Long cmdStatus = 0L;
        try {
            ProjectUnloadLog projectUnloadLog = JSONObject.parseObject(payload, ProjectUnloadLog.class);
            if (projectUnloadLog != null) {
                projectUnloadLog.setTerminalTime(projectUnloadLog.getTimeDischarge().getTime());
                projectUnloadLog.setTimeLoad(new Date(projectUnloadLog.getTimeLoad().getTime() * 1000));
                projectUnloadLog.setTimeCheck(new Date(projectUnloadLog.getTimeCheck().getTime() * 1000));
                projectUnloadLog.setTimeDischarge(new Date(projectUnloadLog.getTimeDischarge().getTime() * 1000));
                Map<String, Date> dateMap = workDateService.getWorkTime(projectUnloadLog.getProjectID(), projectUnloadLog.getTimeDischarge());
                Date startTime = dateMap.get("start");
                if (startTime.getTime() > projectUnloadLog.getTimeDischarge().getTime())
                    startTime = DateUtils.getAddDate(startTime, -1);
                Shift shift = workDateService.getShift(projectUnloadLog.getTimeDischarge(), projectUnloadLog.getProjectID());
                Date dateIdentification = DateUtils.createReportDateByMonth(startTime);
                projectUnloadLog.setDateIdentification(dateIdentification);
                projectUnloadLog.setShift(shift);
                String slagFiledId = projectUnloadLog.getSlagfieldID().toString();
                Integer length = slagFiledId.length();
                if (length > 10) {
                    projectUnloadLog.setSlagfieldID(0L);
                }
                Date lastTimeDischarge = projectUnloadLogServiceI.getMaxUnloadDateByCarCode(projectUnloadLog.getCarCode(), projectUnloadLog.getTimeDischarge());
                ProjectSlagSite tprojectSlagSite = projectSlagSiteServiceI.getByProjectIdAndDeviceUid(projectUnloadLog.getProjectID(), projectUnloadLog.getUid());
                Long si = 0L;
                if (tprojectSlagSite != null) {
                    ProjectMaterial projectMaterial = projectMaterialServiceI.get(projectUnloadLog.getLoader());
                    if(projectMaterial != null)
                        projectUnloadLog.setLoaderName(projectMaterial.getName());
                    si = tprojectSlagSite.getSwipeIntervent();
                    projectUnloadLog.setSlagfieldID(tprojectSlagSite.getId());
                    projectUnloadLog.setSlagFieldName(tprojectSlagSite.getName());
                    if (lastTimeDischarge == null || projectUnloadLog.getTimeDischarge().getTime() >= (lastTimeDischarge.getTime() +
                            si)) {   //在限制时间内不允许多次刷卡
                        ProjectUnloadLog ret = projectUnloadLogServiceI.save(projectUnloadLog);
                        return ret;
                        /*projectSystemMqttLog.setValid(true);*/
                    } else {
                        /*projectSystemMqttLog.setValid(false);
                        projectSystemMqttLog.setRemark(projectSystemMqttLog.getRemark() + "\t渣车" + projectUnloadLog.getCarCode() + "在限制时间内刷卡.");*/
                        projectUnloadLog.setRemark("渣车" + projectUnloadLog.getCarCode() + "在限制时间内刷卡.");
                        projectUnloadLog.setIsVaild(false);
                        projectUnloadLogServiceI.save(projectUnloadLog);
                        return null;
                    }
                } else {
                    /*projectSystemMqttLog.setValid(false);
                    projectSystemMqttLog.setRemark(projectSystemMqttLog.getRemark() + "\t找不到对应渣场信息：" + projectUnloadLog.getCarCode());*/
                    projectUnloadLog.setRemark("找不到对应渣场信息：" + projectUnloadLog.getCarCode());
                    projectUnloadLog.setIsVaild(false);
                    projectUnloadLogServiceI.save(projectUnloadLog);
                    return null;
                }
            } else {
                throw new SmartminingProjectException("mq数据上传错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            cmdStatus = 1L;
            smartminingExceptionService.save(e, payload);
            return null;
            /*projectSystemMqttLog.setValid(false);
            projectSystemMqttLog.setErrorMessage(JSON.toJSONString(e.getStackTrace()));*/
        } finally {
            DeviceReply reply = new DeviceReply("carLog", pktID, cmdStatus);
            mqttSender.sendDeviceReply(replytopic, reply);
            /*projectSystemMqttLog.setResponseParams(JSON.toJSONString(reply));
            if(projectSystemMqttLog.getValid()){
                projectSystemMqttLog.setRemark(projectSystemMqttLog.getRemark() + "\t渣场终端刷卡成功，进入合并");
                if(ret != null)
                    projectSystemMqttLog.setElseInfo(JSON.toJSONString(ret));
                else
                    projectSystemMqttLog.setElseInfo("");
            }
            projectSystemMqttLogServiceI.save(projectSystemMqttLog);*/
        }
    }

    /**
     * 优化渣场作业数据合并
     *
     * @param projectUnloadLog
     */
    @Transactional
    public ProjectCarWorkInfo updateCarWorkInfoNew(ProjectUnloadLog projectUnloadLog) throws IOException {
        Integer errorCode = 0;
        String errorMessage = "";
        Integer dispatchMode = projectUnloadLog.getDispatchMode();
        ProjectDispatchMode projectDispatchMode = ProjectDispatchMode.Unknown;
        Boolean uploadByDevice = true;
        List<ScheduleCar> scheduleCarList = null;
        ProjectSchedule projectSchedule = null;
        List<ScheduleMachine> scheduleMachineList = null;
        try {
            projectDispatchMode = ProjectDispatchMode.converMode(dispatchMode);
            scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectUnloadLog.getProjectID(), projectUnloadLog.getCarID(), true);
            if (scheduleCarList != null && scheduleCarList.size() > 0) {
                projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectUnloadLog.getProjectID(), scheduleCarList.get(0).getGroupCode());
                scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectUnloadLog.getProjectID(), scheduleCarList.get(0).getGroupCode());
            }
            if (projectDispatchMode.getAlians() == 0) {
                uploadByDevice = false;
                if (projectSchedule == null) {
                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                    throw new SmartminingProjectException("排班不存在，渣车编号：" + projectUnloadLog.getCarCode());
                }
                projectDispatchMode = projectSchedule.getDispatchMode();
            }
            Project project = projectServiceI.get(projectUnloadLog.getProjectID());
            Long projectId = projectUnloadLog.getProjectID();
            Long carId = projectUnloadLog.getCarID();
            String carCode = projectUnloadLog.getCarCode();
            Long cubic = projectCarServiceI.get(carId).getModifyCapacity();
            Date timeLoad = projectUnloadLog.getTimeLoad();
            Date timeDischarge = projectUnloadLog.getTimeDischarge();
            //渣车对象
            ProjectCar projectCar = projectCarServiceI.get(carId);
            if (projectCar == null) {
                errorCode = WorkMergeFailEnum.WithoutCarCode.getValue();
                errorMessage = WorkMergeFailEnum.WithoutCarCode.getName();
                throw new SmartminingProjectException("渣车编号：" + carCode + " 不存在");
            }
            //倒渣的渣场对象
            ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.getByProjectIdAndDeviceUid(projectId, projectUnloadLog.getUid());
            if (projectSlagSite == null) {
                errorCode = WorkMergeFailEnum.WithoutSlagSiteCode.getValue();
                errorMessage = WorkMergeFailEnum.WithoutSlagSiteCode.getName();
                throw new SmartminingProjectException("UID：" + projectUnloadLog.getUid() + " 对应的渣场不存在");
            }
            //卸载的渣场信息
            Long slagSiteId = projectSlagSite.getId();
            String slagSiteName = projectSlagSite.getName();
            Calendar calendar1 = Calendar.getInstance(), calendar2 = Calendar.getInstance();
            calendar2.setTime(project.getEarlyStartTime());
            calendar1.setTime(timeDischarge);
            calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
            calendar1.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
            Date earlyStart = calendar1.getTime();
            if (earlyStart.compareTo(timeDischarge) > 0) {
                calendar1.add(Calendar.DAY_OF_MONTH, -1);
                earlyStart = calendar1.getTime();
            }
            //日期标识
            Date dateIdentification = DateUtils.createReportDateByMonth(earlyStart);
            Shift shift = workDateService.getShift(timeDischarge, projectId);
            Long intervalSecond = DateUtils.calculationHour(projectUnloadLog.getTimeLoad(), timeDischarge);
            boolean infoValid = true;
            ProjectCarWorkInfo projectCarWorkInfo = null;
            if (projectDispatchMode.compareTo(ProjectDispatchMode.ExcavatorSchedule) == 0) {
                ProjectCarWorkInfo info = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, projectUnloadLog.getTimeDischarge());
                if (info != null) {
                    Date lastTime = info.getTimeDischarge();
                    Long second = timeDischarge.getTime() - lastTime.getTime();
                    if (second < 60 * 60) {
                        Date startDate = lastTime;
                        List<ProjectCarWorkInfo> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndTimeLoadHalf(projectId, startDate, timeDischarge, ProjectCarWorkStatus.UnUnload.getAlias(), carCode);
                        if (projectCarWorkInfoList != null && projectCarWorkInfoList.size() > 0) {
                            projectCarWorkInfo = projectCarWorkInfoList.get(0);
                            if (projectCarWorkInfo.getCubic() == null || projectCarWorkInfo.getCubic() == 0)
                                projectCarWorkInfo.setCubic(cubic);
                            if (projectCarWorkInfo.getMaterialId() == null || projectCarWorkInfo.getMaterialId() == 0)
                                projectCarWorkInfo.setMaterialId(projectUnloadLog.getLoader());
                            if (StringUtils.isEmpty(projectCarWorkInfo.getMaterialName())) {
                                ProjectMaterial projectMaterial = projectMaterialServiceI.get(projectCarWorkInfo.getMaterialId());
                                if (projectMaterial != null)
                                    projectCarWorkInfo.setMaterialName(projectMaterial.getName());
                            }
                            if (projectCarWorkInfo.getPricingType() == null || projectCarWorkInfo.getPricingType().compareTo(PricingTypeEnums.Unknow) == 0) {
                                PricingTypeEnums pricingType = PricingTypeEnums.convert(projectUnloadLog.getPriceMethod());
                                projectCarWorkInfo.setPricingType(pricingType);
                            }
                            if (projectCarWorkInfo.getSlagSiteId() == null || projectCarWorkInfo.getSlagSiteId() == 0)
                                projectCarWorkInfo.setSlagSiteId(slagSiteId);
                            if (StringUtils.isEmpty(projectCarWorkInfo.getSlagSiteName()))
                                projectCarWorkInfo.setSlagSiteName(slagSiteName);
                            if (StringUtils.isEmpty(projectCarWorkInfo.getAllowSlagSites()))
                                projectCarWorkInfo.setAllowSlagSites(projectUnloadLog.getSlagSiteID());
                            if (projectCarWorkInfo.getDiggingMachineId() == null || projectCarWorkInfo.getDiggingMachineId() == 0)
                                projectCarWorkInfo.setDiggingMachineId(projectUnloadLog.getExcavatCurrent());
                            if (StringUtils.isEmpty(projectCarWorkInfo.getDiggingMachineCode())) {
                                ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(projectUnloadLog.getExcavatCurrent());
                                if (machine != null)
                                    projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                            }
                            projectCarWorkInfo.setDateIdentification(dateIdentification);
                            projectCarWorkInfo.setShift(shift);
                            projectCarWorkInfo.setTimeDischarge(timeDischarge);
                        }
                    }
                }
                if (projectCarWorkInfo == null)
                    projectCarWorkInfo = new ProjectCarWorkInfo();
                projectCarWorkInfo.setInfoValid(true);          //单渣场模式不会有超时卸载
                if (projectCarWorkInfo.getProjectId() == null || projectCarWorkInfo.getProjectId() == 0)
                    projectCarWorkInfo.setProjectId(projectId);
                if (projectCarWorkInfo.getCarId() == null || projectCarWorkInfo.getCarId() == 0)
                    projectCarWorkInfo.setCarId(carId);
                if (StringUtils.isEmpty(projectCarWorkInfo.getCarCode()))
                    projectCarWorkInfo.setCarCode(carCode);
                if (projectCarWorkInfo.getDispatchMode() == null || projectCarWorkInfo.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) == 0)
                    projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                if (projectCarWorkInfo.getCubic() == null || projectCarWorkInfo.getCubic() == 0)
                    projectCarWorkInfo.setCubic(cubic);
                if (projectCarWorkInfo.getTimeDischarge() == null || projectCarWorkInfo.getTimeDischarge().getTime() == 0)
                    projectCarWorkInfo.setTimeDischarge(timeDischarge);
                if (projectCarWorkInfo.getShift() == null || projectCarWorkInfo.getShift().compareTo(Shift.Unknown) == 0)
                    projectCarWorkInfo.setShift(shift);
                if (projectCarWorkInfo.getDateIdentification() == null || projectCarWorkInfo.getDateIdentification().getTime() == 0)
                    projectCarWorkInfo.setDateIdentification(dateIdentification);
                //可卸载渣场集合
                List<Long> slagSiteList = new ArrayList<>();
                if (StringUtils.isEmpty(projectCarWorkInfo.getAllowSlagSites())) {
                    if (projectSchedule == null) {
                        errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                        throw new SmartminingProjectException("排班不存在，渣车编号：" + projectUnloadLog.getCarCode());
                    }
                    projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                    JSONArray slagSiteArray = JSONArray.parseArray(projectSchedule.getSlagSiteId());
                    if (slagSiteArray != null) {
                        for (int i = 0; i < slagSiteArray.size(); i++) {
                            Long id = Long.parseLong(slagSiteArray.getString(i));
                            slagSiteList.add(id);
                        }
                    }
                } else {
                    String[] slagSiteArray = null;
                    if (projectCarWorkInfo.getAllowSlagSites().indexOf(",") != -1) {
                        slagSiteArray = projectCarWorkInfo.getAllowSlagSites().split(",");
                    } else {
                        slagSiteArray = new String[1];
                        slagSiteArray[0] = projectCarWorkInfo.getAllowSlagSites();
                    }
                    if (slagSiteArray != null) {
                        for (int i = 0; i < slagSiteArray.length; i++) {
                            Long id = Long.parseLong(slagSiteArray[i]);
                            slagSiteList.add(id);
                        }
                    }
                }
                if (!slagSiteList.contains(projectSlagSite.getId())) {
                    projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                    projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                    handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                }
                if (projectCarWorkInfo.getSlagSiteId() == null || projectCarWorkInfo.getSlagSiteId() == 0)
                    projectCarWorkInfo.setSlagSiteId(slagSiteId);
                if (StringUtils.isEmpty(projectCarWorkInfo.getSlagSiteName()))
                    projectCarWorkInfo.setSlagSiteName(slagSiteName);
                if (projectCarWorkInfo.getCarOwnerId() == null || projectCarWorkInfo.getCarOwnerId() == 0)
                    projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                if (StringUtils.isEmpty(projectCarWorkInfo.getCarOwnerName()))
                    projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                if (projectCarWorkInfo.getDiggingMachineId() == null || projectCarWorkInfo.getDiggingMachineId() == 0) {
                    if (scheduleMachineList == null || scheduleMachineList.size() < 1) {
                        errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                        throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                    }
                    projectCarWorkInfo.setDiggingMachineId(scheduleMachineList.get(0).getMachineId());
                }
                if (StringUtils.isEmpty(projectCarWorkInfo.getDiggingMachineCode())) {
                    if (scheduleMachineList == null || scheduleMachineList.size() < 1) {
                        errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                        throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                    }
                    projectCarWorkInfo.setDiggingMachineCode(scheduleMachineList.get(0).getMachineCode());
                }
                if (projectCarWorkInfo.getMaterialId() == null || projectCarWorkInfo.getMaterialId() == 0) {
                    if (scheduleMachineList == null || scheduleMachineList.size() < 1) {
                        errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                        throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                    }
                    projectCarWorkInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                }
                if (StringUtils.isEmpty(projectCarWorkInfo.getMaterialName())) {
                    if (scheduleMachineList == null || scheduleMachineList.size() < 1) {
                        errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                        throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                    }
                    projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                }
                Long distance = 0L;
                if (projectCarWorkInfo.getDistance() == 0)
                    distance = scheduleMachineList.get(0).getDistance() + projectSlagSite.getDistance();
                else
                    distance = projectCarWorkInfo.getDistance() + projectSlagSite.getDistance();
                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                projectCarWorkInfo.setDistance(distance);
                projectCarWorkInfo.setPayableDistance(payableDistance);
                projectCarWorkInfo.setAmount(amount);
                if (projectCarWorkInfo.getPricingType() == null || projectCarWorkInfo.getPricingType().compareTo(PricingTypeEnums.Unknow) == 0)
                    projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                projectCarWorkInfo.setPass(Score.Pass);
                projectCarWorkInfo.setUnLoadUp(true);
                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                projectCarWorkInfo.setRemark("单渣场模式");
                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            } else if (projectDispatchMode.compareTo(ProjectDispatchMode.GroupMixture) == 0 || projectDispatchMode.compareTo(ProjectDispatchMode.Auto) == 0) {
                //查询对应的终端对象
                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, carCode, ProjectDeviceType.SlagTruckDevice.getAlian());
                //先判断是否有装载时间 进行容错处理
                if (projectUnloadLog.getTimeLoad() == null || projectUnloadLog.getTimeLoad().getTime() == 0) {
                    Date startDate = DateUtils.getAddSecondDate(timeDischarge, -(60 * 60));
                    ProjectCarWorkInfo info = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, projectUnloadLog.getTimeDischarge());
                    if (info != null) {
                        Date lastTime = info.getTimeDischarge();
                        Long second = timeDischarge.getTime() - lastTime.getTime();
                        if (second < 60 * 60)
                            startDate = lastTime;
                    }
                    List<ProjectCarWorkInfo> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndTimeLoadHalf(projectId, startDate, timeDischarge, ProjectCarWorkStatus.UnUnload.getAlias(), carCode);
                    if (projectCarWorkInfoList == null || projectCarWorkInfoList.size() == 0) {
                        if (projectDevice == null) {
                            errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                            errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                            throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                        } else {
                            if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                            } else {
                                errorCode = WorkMergeFailEnum.WorkError.getValue();
                                errorMessage = WorkMergeFailEnum.WorkError.getName();
                                throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                            }
                        }
                    }
                    projectCarWorkInfo = projectCarWorkInfoList.get(0);
                    ProjectSlagCarLog projectSlagCarLog = projectSlagCarLogServiceI.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, projectCarWorkInfo.getTimeLoad().getTime());
                    if (projectSlagCarLog == null) {
                        errorCode = WorkMergeFailEnum.WithoutSlagCarDevice.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutSlagCarDevice.getName();
                        throw new SmartminingProjectException("渣车终端未上传，渣车编号" + carCode);
                    }
                    //可卸载渣场集合
                    List<Long> slagSiteList = new ArrayList<>();
                    if (StringUtils.isEmpty(projectCarWorkInfo.getAllowSlagSites())) {
                        if (projectSchedule == null) {
                            errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                            errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                            throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                        }
                        projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                        JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                        if (slagSiteArray != null) {
                            for (int i = 0; i < slagSiteArray.size(); i++) {
                                Long id = Long.parseLong(slagSiteArray.getString(i));
                                slagSiteList.add(id);
                            }
                        }
                    } else {
                        String[] slagSiteArray = null;
                        if (projectCarWorkInfo.getAllowSlagSites().indexOf(",") != -1) {
                            slagSiteArray = projectCarWorkInfo.getAllowSlagSites().split(",");
                        } else {
                            slagSiteArray = new String[1];
                            slagSiteArray[0] = projectCarWorkInfo.getAllowSlagSites();
                        }
                        if (slagSiteArray != null) {
                            for (int i = 0; i < slagSiteArray.length; i++) {
                                Long id = Long.parseLong(slagSiteArray[i]);
                                slagSiteList.add(id);
                            }
                        }
                    }
                    projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                    projectCarWorkInfo.setTimeDischarge(timeDischarge);
                    projectCarWorkInfo.setShift(shift);
                    projectCarWorkInfo.setInfoValid(infoValid);
                    projectCarWorkInfo.setDateIdentification(dateIdentification);
                    projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.AutoErrorMerge.getValue());
                    projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.AutoErrorMerge.getName());
                    projectCarWorkInfo.setRemark("手动追寻数据，根据卸载时间追寻前一个小时内的数据");
                    if (!slagSiteList.contains(projectSlagSite.getId())) {
                        projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                        projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                        handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                    }
                    projectCarWorkInfo.setSlagSiteId(slagSiteId);
                    projectCarWorkInfo.setSlagSiteName(slagSiteName);
                    projectCarWorkInfo.setPass(Score.Pass);
                    Long distance = projectCarWorkInfo.getDistance() + projectSlagSite.getDistance();
                    Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                    ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                    Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                    Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                    Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                    projectCarWorkInfo.setDistance(distance);
                    projectCarWorkInfo.setPayableDistance(payableDistance);
                    projectCarWorkInfo.setAmount(amount);
                    projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                    projectCarWorkInfo.setUnLoadUp(true);
                    projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                } else {
                    //物料对象
                    ProjectMaterial projectMaterial = projectMaterialServiceI.get(projectUnloadLog.getLoader());
                    if (projectMaterial == null) {
                        errorCode = WorkMergeFailEnum.WithoutLoader.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutLoader.getName();
                        throw new SmartminingProjectException("物料不存在，物料ID：" + projectUnloadLog.getLoader());
                    }
                    if (intervalSecond > 60 * 60 * 12)
                        infoValid = false;
                    Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, projectUnloadLog.getTimeLoad());        //在作业表中寻找数据是否已经存在
                    if (object != null) {    //存在
                        ProjectSlagCarLog projectSlagCarLog = projectSlagCarLogServiceI.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, timeLoad.getTime());
                        if (projectSlagCarLog == null) {
                            errorCode = WorkMergeFailEnum.WithoutSlagCarDevice.getValue();
                            errorMessage = WorkMergeFailEnum.WithoutSlagCarDevice.getName();
                            throw new SmartminingProjectException("渣车终端未上传，渣车编号：" + carCode);
                        }
                        projectCarWorkInfo = (ProjectCarWorkInfo) object;
                        //允许倒渣的渣场集合
                        List<Long> slagSiteList = new ArrayList<>();
                        if (StringUtils.isEmpty(projectCarWorkInfo.getAllowSlagSites())) {
                            if (projectSchedule == null) {
                                errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                            }
                            projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                            JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                            if (slagSiteArray != null) {
                                for (int i = 0; i < slagSiteArray.size(); i++) {
                                    Long id = Long.parseLong(slagSiteArray.getString(i));
                                    slagSiteList.add(id);
                                }
                            }
                        } else {
                            String[] slagSiteArray = null;
                            if (projectCarWorkInfo.getAllowSlagSites().indexOf(",") != -1) {
                                slagSiteArray = projectCarWorkInfo.getAllowSlagSites().split(",");
                            } else {
                                slagSiteArray = new String[1];
                                slagSiteArray[0] = projectCarWorkInfo.getAllowSlagSites();
                            }
                            if (slagSiteArray != null) {
                                for (int i = 0; i < slagSiteArray.length; i++) {
                                    Long id = Long.parseLong(slagSiteArray[i]);
                                    slagSiteList.add(id);
                                }
                            }
                        }
                        if (projectCarWorkInfo.getDiggingMachineId() == 0) {
                            Long machineId = projectUnloadLog.getExcavatCurrent();
                            if (machineId == 0) {
                                //未按规定装载
                                ProjectErrorLoadLog loadLog = projectErrorLoadLogServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, dateIdentification, shift.getAlias());
                                if (loadLog == null) {
                                    loadLog = new ProjectErrorLoadLog();
                                    loadLog.setProjectId(projectId);
                                    loadLog.setCarCode(carCode);
                                    loadLog.setCarId(carId);
                                    loadLog.setDateIdentification(dateIdentification);
                                    if (scheduleCarList != null && scheduleCarList.size() == 1) {
                                        Long[] machineIds = new Long[scheduleMachineList.size()];
                                        String[] machineCodes = new String[scheduleMachineList.size()];
                                        int a = 0;
                                        for (ScheduleMachine machine : scheduleMachineList) {
                                            machineIds[a] = machine.getId();
                                            machineCodes[a] = machine.getMachineCode();
                                            a++;
                                        }
                                        loadLog.setMachineId(JSON.toJSONString(machineIds));
                                        loadLog.setMachineCode(JSON.toJSONString(machineCodes));
                                    }
                                    loadLog.setTimeDischarge(timeDischarge);
                                    loadLog.setCount(1);
                                    loadLog.setCreateTime(new Date());
                                } else {
                                    loadLog.setCount(loadLog.getCount() + 1);
                                    loadLog.setModifyTime(new Date());
                                }
                                projectErrorLoadLogServiceI.save(loadLog);
                                if (projectDevice == null) {
                                    errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                                    errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                                    throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                                } else {
                                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                        errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                        errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                        throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                                    } else {
                                        errorCode = WorkMergeFailEnum.WorkError.getValue();
                                        errorMessage = WorkMergeFailEnum.WorkError.getName();
                                        throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                                    }
                                }
                            } else {
                                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machineId);
                                if (projectDiggingMachine == null)
                                    throw new SmartminingProjectException("挖机不存在，挖机ID：" + machineId);
                                projectCarWorkInfo.setDiggingMachineId(projectDiggingMachine.getId());
                                projectCarWorkInfo.setDiggingMachineCode(projectDiggingMachine.getCode());
                            }
                        }
                        projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                        projectCarWorkInfo.setTimeDischarge(timeDischarge);
                        if (projectCarWorkInfo.getTimeLoad() == null || projectCarWorkInfo.getTimeLoad().getTime() == 0)
                            projectCarWorkInfo.setTimeLoad(timeLoad);
                        projectCarWorkInfo.setShift(shift);
                        projectCarWorkInfo.setInfoValid(infoValid);
                        projectCarWorkInfo.setDateIdentification(dateIdentification);
                        if (!slagSiteList.contains(projectSlagSite.getId())) {
                            projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                            projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                            handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                        }
                        projectCarWorkInfo.setSlagSiteId(slagSiteId);
                        projectCarWorkInfo.setSlagSiteName(slagSiteName);
                        projectCarWorkInfo.setPass(Score.Pass);
                        //因为升级版只有挖机和渣场两个设备,所以如果存在一定是挖机上传的
                        if (projectCarWorkInfo.getLoadUp()) {
                            Long distance = projectCarWorkInfo.getDistance() + projectSlagSite.getDistance();
                            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                            projectCarWorkInfo.setDistance(distance);
                            projectCarWorkInfo.setPayableDistance(payableDistance);
                            projectCarWorkInfo.setAmount(amount);
                            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                            projectCarWorkInfo.setUnLoadUp(true);
                            projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                            projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                            projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                        } else {
                            errorCode = WorkMergeFailEnum.WithoutSlagCarDevice.getValue();
                            errorMessage = WorkMergeFailEnum.WithoutSlagCarDevice.getName();
                            throw new SmartminingProjectException("渣车终端未上传");
                        }
                    } else {    //不存在
                        projectCarWorkInfo = new ProjectCarWorkInfo();
                        projectCarWorkInfo.setProjectId(projectId);
                        projectCarWorkInfo.setCarId(carId);
                        projectCarWorkInfo.setCarCode(carCode);
                        projectCarWorkInfo.setCubic(cubic);
                        projectCarWorkInfo.setTimeDischarge(timeDischarge);
                        projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                        projectCarWorkInfo.setShift(shift);
                        projectCarWorkInfo.setInfoValid(infoValid);
                        projectCarWorkInfo.setDistance(projectSlagSite.getDistance());
                        projectCarWorkInfo.setDateIdentification(dateIdentification);
                        projectCarWorkInfo.setTimeLoad(projectUnloadLog.getTimeLoad());
                        projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                        projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                        projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                        projectCarWorkInfo.setMateriaName(projectMaterial.getName());
                        projectCarWorkInfo.setPass(Score.Pass);
                        projectCarWorkInfo.setSlagSiteId(slagSiteId);
                        projectCarWorkInfo.setSlagSiteName(slagSiteName);
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitLoadUp);
                        projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                        projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                        projectCarWorkInfo.setRemark("渣场先上传");
                        projectCarWorkInfo.setUnLoadUp(true);
                        if (projectUnloadLog.getExcavatCurrent() != null && projectUnloadLog.getExcavatCurrent() != 0) {
                            //允许倒渣的渣场集合
                            List<Long> slagSiteList = new ArrayList<>();
                            if (StringUtils.isEmpty(projectUnloadLog.getSlagSiteID())) {
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectSchedule.getSlagSiteId());
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                            } else {
                                projectCarWorkInfo.setAllowSlagSites(projectUnloadLog.getSlagSiteID());
                                String[] slagSiteArray = null;
                                if (projectCarWorkInfo.getAllowSlagSites().indexOf(",") != -1) {
                                    slagSiteArray = projectCarWorkInfo.getAllowSlagSites().split(",");
                                } else {
                                    slagSiteArray = new String[1];
                                    slagSiteArray[0] = projectCarWorkInfo.getAllowSlagSites();
                                }
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.length; i++) {
                                        Long id = Long.parseLong(slagSiteArray[i]);
                                        slagSiteList.add(id);
                                    }
                                }
                            }
                            if (!slagSiteList.contains(projectSlagSite.getId())) {
                                projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                            }
                            //计价方式
                            PricingTypeEnums pricingType = PricingTypeEnums.convert(projectUnloadLog.getPriceMethod());
                            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(projectUnloadLog.getExcavatCurrent());
                            projectCarWorkInfo.setDistance(projectUnloadLog.getExctDist() + projectSlagSite.getDistance());
                            Long distance = projectCarWorkInfo.getDistance();
                            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                            projectCarWorkInfo.setDiggingMachineId(machine.getId());
                            projectCarWorkInfo.setPayableDistance(payableDistance);
                            projectCarWorkInfo.setAmount(amount);
                            projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                            projectCarWorkInfo.setPricingType(pricingType);
                            projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                            projectCarWorkInfo.setMaterialName(projectMaterial.getName());
                            projectCarWorkInfo.setCreateDate(new Date());
                            projectCarWorkInfo.setLoadUp(true);
                            projectCarWorkInfo.setInfoValid(true);
                            projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SingleSlagSiteSuccessMerge.getValue());
                            projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SingleSlagSiteSuccessMerge.getName());
                            projectCarWorkInfo.setRemark("渣场先上传，且渣场所有数据都正确，非完整模式直接判定完成");
                            if (projectDispatchMode.getAlians() == 2)
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitCheckUp);
                            else
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                        }
                        projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                    }
                }
            } else if (projectDispatchMode.compareTo(ProjectDispatchMode.AutoDistinguish) == 0) {
                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, carCode, ProjectDeviceType.SlagTruckDevice.getAlian());
                //先判断是否有装载时间 进行容错处理
                if (projectUnloadLog.getTimeLoad() == null || projectUnloadLog.getTimeLoad().getTime() == 0) {
                    Date startDate = DateUtils.getAddSecondDate(timeDischarge, -(60 * 60));
                    ProjectCarWorkInfo info = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode);
                    if (info != null) {
                        Date lastTime = info.getTimeDischarge();
                        Long second = timeDischarge.getTime() - lastTime.getTime();
                        if (second < 60 * 60)
                            startDate = lastTime;
                    }
                    List<ProjectCarWorkInfo> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndTimeLoadHalf(projectId, startDate, timeDischarge, ProjectCarWorkStatus.UnUnload.getAlias(), carCode);
                    if (projectCarWorkInfoList == null || projectCarWorkInfoList.size() == 0) {
                        List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startDate, projectUnloadLog.getTimeDischarge());
                        if (locationList == null || locationList.size() < 1) {
                            if (projectDevice == null) {
                                errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                                errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                                throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                            } else {
                                if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                    errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                    errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                    throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                                } else {
                                    errorCode = WorkMergeFailEnum.WorkError.getValue();
                                    errorMessage = WorkMergeFailEnum.WorkError.getName();
                                    throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                                }
                            }
                        } else {
                            projectCarWorkInfo = new ProjectCarWorkInfo();
                            projectCarWorkInfo.setProjectId(projectId);
                            projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                            projectCarWorkInfo.setCarId(carId);
                            projectCarWorkInfo.setCarCode(carCode);
                            boolean goOut = false;
                            for (ProjectMachineLocation location : locationList) {
                                JSONArray array = JSONArray.parseArray(location.getDiggingMachineText());
                                JSONArray jsonArray = JSONArray.parseArray(JSONArraySortUtils.jsonArraySort(array, "distance"));
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Boolean unLine = jsonObject.getBoolean("unLine");
                                    if (!unLine) {
                                        Long machineIdByLocation = jsonObject.getLong("machineId");
                                        String machineCodeByLocation = jsonObject.getString("machineCode");
                                        projectCarWorkInfo.setDiggingMachineId(machineIdByLocation);
                                        projectCarWorkInfo.setDiggingMachineCode(machineCodeByLocation);
                                        projectCarWorkInfo.setTimeLoad(location.getCreateTime());
                                        goOut = true;
                                        break;
                                    }
                                }
                                if (goOut)
                                    break;
                            }
                            projectCarWorkInfo.setCubic(cubic);
                            projectCarWorkInfo.setTimeDischarge(timeDischarge);
                            projectCarWorkInfo.setShift(shift);
                            projectCarWorkInfo.setInfoValid(infoValid);
                            projectCarWorkInfo.setDateIdentification(dateIdentification);
                            List<Long> slagSiteList = new ArrayList<>();
                            if (StringUtils.isEmpty(projectCarWorkInfo.getAllowSlagSites())) {
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                            } else {
                                String[] slagSiteArray = null;
                                if (projectCarWorkInfo.getAllowSlagSites().indexOf(",") != -1) {
                                    slagSiteArray = projectCarWorkInfo.getAllowSlagSites().split(",");
                                } else {
                                    slagSiteArray = new String[1];
                                    slagSiteArray[0] = projectCarWorkInfo.getAllowSlagSites();
                                }
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.length; i++) {
                                        Long id = Long.parseLong(slagSiteArray[i]);
                                        slagSiteList.add(id);
                                    }
                                }
                            }
                            if (!slagSiteList.contains(projectSlagSite.getId())) {
                                projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                            }
                            Long distance = scheduleMachineList.get(0).getDistance() + projectSlagSite.getDistance();
                            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                            projectCarWorkInfo.setSlagSiteId(slagSiteId);
                            projectCarWorkInfo.setSlagSiteName(slagSiteName);
                            projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                            projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                            projectCarWorkInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                            projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                            projectCarWorkInfo.setDistance(distance);
                            projectCarWorkInfo.setPayableDistance(payableDistance);
                            projectCarWorkInfo.setAmount(amount);
                            projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                            projectCarWorkInfo.setPass(Score.Pass);
                            projectCarWorkInfo.setUnLoadUp(true);
                            projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.AutoErrorMerge.getValue());
                            projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.AutoErrorMerge.getName());
                            projectCarWorkInfo.setRemark("容错处理，自动识别挖机");
                            projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                        }
                    } else {
                        projectCarWorkInfo = projectCarWorkInfoList.get(0);
                        ProjectSlagCarLog projectSlagCarLog = projectSlagCarLogServiceI.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, projectCarWorkInfo.getTimeLoad().getTime());
                        if (projectSlagCarLog == null) {
                            List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startDate, projectUnloadLog.getTimeDischarge());
                            if (locationList == null || locationList.size() < 1) {
                                if (projectDevice == null) {
                                    errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                                    errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                                    throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                                } else {
                                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                        errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                        errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                        throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                                    } else {
                                        errorCode = WorkMergeFailEnum.WorkError.getValue();
                                        errorMessage = WorkMergeFailEnum.WorkError.getName();
                                        throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                                    }
                                }
                            } else {
                                projectCarWorkInfo = new ProjectCarWorkInfo();
                                projectCarWorkInfo.setProjectId(projectId);
                                projectCarWorkInfo.setCarId(carId);
                                projectCarWorkInfo.setCarCode(carCode);
                                projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                                boolean goOut = false;
                                for (ProjectMachineLocation location : locationList) {
                                    JSONArray array = JSONArray.parseArray(location.getDiggingMachineText());
                                    JSONArray jsonArray = JSONArray.parseArray(JSONArraySortUtils.jsonArraySort(array, "distance"));
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Boolean unLine = jsonObject.getBoolean("unLine");
                                        if (!unLine) {
                                            Long machineIdByLocation = jsonObject.getLong("machineId");
                                            String machineCodeByLocation = jsonObject.getString("machineCode");
                                            projectCarWorkInfo.setDiggingMachineId(machineIdByLocation);
                                            projectCarWorkInfo.setDiggingMachineCode(machineCodeByLocation);
                                            projectCarWorkInfo.setTimeLoad(location.getCreateTime());
                                            goOut = true;
                                            break;
                                        }
                                    }
                                    if (goOut)
                                        break;
                                }
                                projectCarWorkInfo.setCubic(cubic);
                                projectCarWorkInfo.setTimeDischarge(timeDischarge);
                                projectCarWorkInfo.setShift(shift);
                                projectCarWorkInfo.setInfoValid(infoValid);
                                projectCarWorkInfo.setDateIdentification(dateIdentification);
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                                List<Long> slagSiteList = new ArrayList<>();
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                                if (!slagSiteList.contains(projectSlagSite.getId())) {
                                    projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                    projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                    handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                                }
                                Long distance = scheduleMachineList.get(0).getDistance() + projectSlagSite.getDistance();
                                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                                projectCarWorkInfo.setSlagSiteId(slagSiteId);
                                projectCarWorkInfo.setSlagSiteName(slagSiteName);
                                projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                                projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                                projectCarWorkInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                                projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                                projectCarWorkInfo.setDistance(distance);
                                projectCarWorkInfo.setPayableDistance(payableDistance);
                                projectCarWorkInfo.setAmount(amount);
                                projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                                projectCarWorkInfo.setPass(Score.Pass);
                                projectCarWorkInfo.setUnLoadUp(true);
                                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                                projectCarWorkInfo.setRemark("自动识别挖机");
                                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                            }
                        } else {
                            //允许倒渣的渣场ID 多个用逗号隔开
                            String slagSiteStr = projectCarWorkInfo.getAllowSlagSites();
                            List<Long> slagSiteList = new ArrayList<>();
                            if (StringUtils.isEmpty(slagSiteStr)) {
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectSchedule.getSlagSiteId());
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                            } else {
                                String[] slagSiteArray = null;
                                if (slagSiteStr.indexOf(",") != -1) {
                                    slagSiteArray = slagSiteStr.split(",");
                                } else {
                                    slagSiteArray = new String[1];
                                    slagSiteArray[0] = slagSiteStr;
                                }
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.length; i++) {
                                        Long id = Long.parseLong(slagSiteArray[i]);
                                        slagSiteList.add(id);
                                    }
                                }
                            }
                            projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                            projectCarWorkInfo.setTimeDischarge(timeDischarge);
                            projectCarWorkInfo.setShift(shift);
                            projectCarWorkInfo.setInfoValid(infoValid);
                            projectCarWorkInfo.setDateIdentification(dateIdentification);
                            projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.AutoErrorMerge.getValue());
                            projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.AutoErrorMerge.getName());
                            projectCarWorkInfo.setRemark("手动追寻数据，根据卸载时间追寻前一个小时内的数据");
                            if (!slagSiteList.contains(projectSlagSite.getId())) {
                                projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                            }
                            projectCarWorkInfo.setSlagSiteId(slagSiteId);
                            projectCarWorkInfo.setSlagSiteName(slagSiteName);
                            projectCarWorkInfo.setPass(Score.Pass);
                            Long distance = projectCarWorkInfo.getDistance() + projectSlagSite.getDistance();
                            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                            projectCarWorkInfo.setDistance(distance);
                            projectCarWorkInfo.setPayableDistance(payableDistance);
                            projectCarWorkInfo.setAmount(amount);
                            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                            projectCarWorkInfo.setUnLoadUp(true);
                            projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                        }
                    }
                } else {
                    //物料对象
                    ProjectMaterial projectMaterial = projectMaterialServiceI.get(projectUnloadLog.getLoader());
                    if (projectMaterial == null) {
                        errorCode = WorkMergeFailEnum.WithoutLoader.getValue();
                        errorMessage = WorkMergeFailEnum.WithoutLoader.getName();
                        throw new SmartminingProjectException("物料不存在，物料ID：" + projectUnloadLog.getLoader());
                    }
                    if (intervalSecond > 60 * 60 * 12)
                        infoValid = false;
                    Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, projectUnloadLog.getTimeLoad());        //在作业表中寻找数据是否已经存在
                    if (object != null) {    //存在
                        ProjectSlagCarLog projectSlagCarLog = projectSlagCarLogServiceI.getAllByProjectIDAndCarCodeAndTerminalTime(projectId, carCode, timeLoad.getTime());
                        if (projectSlagCarLog == null) {
                            Date startDate = DateUtils.getAddSecondDate(timeDischarge, -(60 * 60));
                            ProjectCarWorkInfo info = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, projectUnloadLog.getTimeDischarge());
                            if (info != null) {
                                Date lastTime = info.getTimeDischarge();
                                Long second = timeDischarge.getTime() - lastTime.getTime();
                                if (second < 60 * 60)
                                    startDate = lastTime;
                            }
                            List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startDate, projectUnloadLog.getTimeDischarge());
                            if (locationList == null || locationList.size() < 1) {
                                if (projectDevice == null) {
                                    errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                                    errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                                    throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                                } else {
                                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                        errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                        errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                        throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                                    } else {
                                        errorCode = WorkMergeFailEnum.WorkError.getValue();
                                        errorMessage = WorkMergeFailEnum.WorkError.getName();
                                        throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                                    }
                                }
                            } else {
                                projectCarWorkInfo = new ProjectCarWorkInfo();
                                projectCarWorkInfo.setProjectId(projectId);
                                projectCarWorkInfo.setCarId(carId);
                                projectCarWorkInfo.setCarCode(carCode);
                                projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                                boolean goOut = false;
                                for (ProjectMachineLocation location : locationList) {
                                    JSONArray array = JSONArray.parseArray(location.getDiggingMachineText());
                                    JSONArray jsonArray = JSONArray.parseArray(JSONArraySortUtils.jsonArraySort(array, "distance"));
                                    for (int i = 0; i < jsonArray.size(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Boolean unLine = jsonObject.getBoolean("unLine");
                                        if (!unLine) {
                                            Long machineIdByLocation = jsonObject.getLong("machineId");
                                            String machineCodeByLocation = jsonObject.getString("machineCode");
                                            projectCarWorkInfo.setDiggingMachineId(machineIdByLocation);
                                            projectCarWorkInfo.setDiggingMachineCode(machineCodeByLocation);
                                            projectCarWorkInfo.setTimeLoad(location.getCreateTime());
                                            goOut = true;
                                            break;
                                        }
                                    }
                                    if (goOut)
                                        break;
                                }
                                projectCarWorkInfo.setCubic(cubic);
                                projectCarWorkInfo.setTimeDischarge(timeDischarge);
                                projectCarWorkInfo.setShift(shift);
                                projectCarWorkInfo.setInfoValid(infoValid);
                                projectCarWorkInfo.setDateIdentification(dateIdentification);
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                                List<Long> slagSiteList = new ArrayList<>();
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                                if (!slagSiteList.contains(projectSlagSite.getId())) {
                                    projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                    projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                    handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                                }
                                Long distance = scheduleMachineList.get(0).getDistance() + projectSlagSite.getDistance();
                                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                                projectCarWorkInfo.setSlagSiteId(slagSiteId);
                                projectCarWorkInfo.setSlagSiteName(slagSiteName);
                                projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                                projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                                projectCarWorkInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                                projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                                projectCarWorkInfo.setDistance(distance);
                                projectCarWorkInfo.setPayableDistance(payableDistance);
                                projectCarWorkInfo.setAmount(amount);
                                projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                                projectCarWorkInfo.setPass(Score.Pass);
                                projectCarWorkInfo.setUnLoadUp(true);
                                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                                projectCarWorkInfo.setRemark("自动识别挖机");
                                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                            }
                        } else {
                            projectCarWorkInfo = (ProjectCarWorkInfo) object;
                            //允许倒渣的渣场ID 多个用逗号隔开
                            String slagSiteStr = projectCarWorkInfo.getAllowSlagSites();
                            List<Long> slagSiteList = new ArrayList<>();
                            if (StringUtils.isEmpty(slagSiteStr)) {
                                if (projectSchedule == null) {
                                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                                    throw new SmartminingProjectException("排班不存在，渣车编号：" + carCode);
                                }
                                projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                            } else {
                                String[] slagSiteArray = null;
                                if (slagSiteStr.indexOf(",") != -1) {
                                    slagSiteArray = slagSiteStr.split(",");
                                } else {
                                    slagSiteArray = new String[1];
                                    slagSiteArray[0] = slagSiteStr;
                                }
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.length; i++) {
                                        Long id = Long.parseLong(slagSiteArray[i]);
                                        slagSiteList.add(id);
                                    }
                                }
                            }
                            if (projectCarWorkInfo.getDiggingMachineId() == 0) {
                                Long machineId = projectUnloadLog.getExcavatCurrent();
                                if (machineId == 0) {
                                    Date startDate = DateUtils.getAddSecondDate(timeDischarge, -(60 * 60));
                                    ProjectCarWorkInfo info = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode);
                                    if (info != null) {
                                        Date lastTime = info.getTimeDischarge();
                                        Long second = timeDischarge.getTime() - lastTime.getTime();
                                        if (second < 60 * 60)
                                            startDate = lastTime;
                                    }
                                    List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startDate, projectUnloadLog.getTimeDischarge());
                                    if (locationList == null || locationList.size() < 1) {
                                        //未按规定装载
                                        ProjectErrorLoadLog loadLog = projectErrorLoadLogServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, dateIdentification, shift.getAlias());
                                        if (loadLog == null) {
                                            loadLog = new ProjectErrorLoadLog();
                                            loadLog.setProjectId(projectId);
                                            loadLog.setCarCode(carCode);
                                            loadLog.setCarId(carId);
                                            loadLog.setDateIdentification(dateIdentification);
                                            if (scheduleCarList != null && scheduleCarList.size() == 1) {
                                                Long[] machineIds = new Long[scheduleMachineList.size()];
                                                String[] machineCodes = new String[scheduleMachineList.size()];
                                                int a = 0;
                                                for (ScheduleMachine machine : scheduleMachineList) {
                                                    machineIds[a] = machine.getId();
                                                    machineCodes[a] = machine.getMachineCode();
                                                    a++;
                                                }
                                                loadLog.setMachineId(JSON.toJSONString(machineIds));
                                                loadLog.setMachineCode(JSON.toJSONString(machineCodes));
                                            }
                                            loadLog.setTimeDischarge(timeDischarge);
                                            loadLog.setCount(1);
                                            loadLog.setCreateTime(new Date());
                                        } else {
                                            loadLog.setCount(loadLog.getCount() + 1);
                                            loadLog.setModifyTime(new Date());
                                        }
                                        projectErrorLoadLogServiceI.save(loadLog);
                                        if (projectDevice == null) {
                                            errorCode = WorkMergeFailEnum.NoHaveDevice.getValue();
                                            errorMessage = WorkMergeFailEnum.NoHaveDevice.getName();
                                            throw new SmartminingProjectException("该渣车未安装终端，渣车编号：" + carCode);
                                        } else {
                                            if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                                                errorCode = WorkMergeFailEnum.DeviceUnLineError.getValue();
                                                errorMessage = WorkMergeFailEnum.DeviceUnLineError.getName();
                                                throw new SmartminingProjectException("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                                            } else {
                                                errorCode = WorkMergeFailEnum.WorkError.getValue();
                                                errorMessage = WorkMergeFailEnum.WorkError.getName();
                                                throw new SmartminingProjectException("该渣车未按规定装载，渣车编号：" + carCode);
                                            }
                                        }
                                    } else {
                                        boolean goOut = false;
                                        for (ProjectMachineLocation location : locationList) {
                                            JSONArray array = JSONArray.parseArray(location.getDiggingMachineText());
                                            JSONArray jsonArray = JSONArray.parseArray(JSONArraySortUtils.jsonArraySort(array, "distance"));
                                            for (int i = 0; i < jsonArray.size(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                Boolean unLine = jsonObject.getBoolean("unLine");
                                                if (!unLine) {
                                                    Long machineIdByLocation = jsonObject.getLong("machineId");
                                                    String machineCodeByLocation = jsonObject.getString("machineCode");
                                                    projectCarWorkInfo.setDiggingMachineId(machineIdByLocation);
                                                    projectCarWorkInfo.setDiggingMachineCode(machineCodeByLocation);
                                                    projectCarWorkInfo.setTimeLoad(location.getCreateTime());
                                                    goOut = true;
                                                    break;
                                                }
                                            }
                                            if (goOut)
                                                break;
                                        }
                                    }
                                } else {
                                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machineId);
                                    if (projectDiggingMachine == null)
                                        throw new SmartminingProjectException("挖机不存在，挖机ID：" + machineId);
                                    projectCarWorkInfo.setDiggingMachineId(projectDiggingMachine.getId());
                                    projectCarWorkInfo.setDiggingMachineCode(projectDiggingMachine.getCode());
                                }
                            }
                            projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                            projectCarWorkInfo.setTimeDischarge(timeDischarge);
                            if (projectCarWorkInfo.getTimeLoad() == null || projectCarWorkInfo.getTimeLoad().getTime() == 0)
                                projectCarWorkInfo.setTimeLoad(timeLoad);
                            projectCarWorkInfo.setShift(shift);
                            projectCarWorkInfo.setInfoValid(infoValid);
                            projectCarWorkInfo.setDateIdentification(dateIdentification);
                            if (!slagSiteList.contains(projectSlagSite.getId())) {
                                projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                            }
                            projectCarWorkInfo.setSlagSiteId(slagSiteId);
                            projectCarWorkInfo.setSlagSiteName(slagSiteName);
                            projectCarWorkInfo.setPass(Score.Pass);
                            //因为升级版只有挖机和渣场两个设备,所以如果存在一定是挖机上传的
                            if (projectCarWorkInfo.getLoadUp()) {
                                Long distance = projectCarWorkInfo.getDistance() + projectSlagSite.getDistance();
                                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                                projectCarWorkInfo.setDistance(distance);
                                projectCarWorkInfo.setPayableDistance(payableDistance);
                                projectCarWorkInfo.setAmount(amount);
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                                projectCarWorkInfo.setUnLoadUp(true);
                                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                            } else {
                                errorCode = WorkMergeFailEnum.WithoutSlagCarDevice.getValue();
                                errorMessage = WorkMergeFailEnum.WithoutSlagCarDevice.getName();
                                throw new SmartminingProjectException("渣车终端未上传");
                            }
                        }
                    } else {    //不存在
                        projectCarWorkInfo = new ProjectCarWorkInfo();
                        projectCarWorkInfo.setProjectId(projectId);
                        projectCarWorkInfo.setCarId(carId);
                        projectCarWorkInfo.setCarCode(carCode);
                        projectCarWorkInfo.setCubic(cubic);
                        projectCarWorkInfo.setTimeDischarge(timeDischarge);
                        projectCarWorkInfo.setShift(shift);
                        projectCarWorkInfo.setInfoValid(infoValid);
                        projectCarWorkInfo.setDistance(projectSlagSite.getDistance());
                        projectCarWorkInfo.setDateIdentification(dateIdentification);
                        projectCarWorkInfo.setTimeLoad(projectUnloadLog.getTimeLoad());
                        projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                        projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                        projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                        projectCarWorkInfo.setMateriaName(projectMaterial.getName());
                        projectCarWorkInfo.setPass(Score.Pass);
                        projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                        projectCarWorkInfo.setSlagSiteId(slagSiteId);
                        projectCarWorkInfo.setSlagSiteName(slagSiteName);
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitLoadUp);
                        projectCarWorkInfo.setRemark("渣场先上传");
                        projectCarWorkInfo.setUnLoadUp(true);
                        if (projectUnloadLog.getExcavatCurrent() != null && projectUnloadLog.getExcavatCurrent() != 0) {
                            projectCarWorkInfo.setAllowSlagSites(projectUnloadLog.getSlagSiteID());
                            //允许倒渣的渣场ID 多个用逗号隔开
                            JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                            List<Long> slagSiteList = new ArrayList<>();
                            if (slagSiteArray != null) {
                                for (int i = 0; i < slagSiteArray.size(); i++) {
                                    Long id = Long.parseLong(slagSiteArray.getString(i));
                                    slagSiteList.add(id);
                                }
                            }
                            if (!slagSiteList.contains(projectSlagSite.getId())) {
                                projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                            }
                            //计价方式
                            PricingTypeEnums pricingType = PricingTypeEnums.convert(projectUnloadLog.getPriceMethod());
                            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(projectUnloadLog.getExcavatCurrent());
                            projectCarWorkInfo.setDistance(projectUnloadLog.getExctDist() + projectSlagSite.getDistance());
                            Long distance = projectCarWorkInfo.getDistance();
                            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                            projectCarWorkInfo.setDiggingMachineId(machine.getId());
                            projectCarWorkInfo.setPayableDistance(payableDistance);
                            projectCarWorkInfo.setAmount(amount);
                            projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                            projectCarWorkInfo.setPricingType(pricingType);
                            projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                            projectCarWorkInfo.setMaterialName(projectMaterial.getName());
                            projectCarWorkInfo.setCreateDate(new Date());
                            projectCarWorkInfo.setLoadUp(true);
                            projectCarWorkInfo.setInfoValid(true);
                            projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SingleSlagSiteSuccessMerge.getValue());
                            projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SingleSlagSiteSuccessMerge.getName());
                            projectCarWorkInfo.setRemark("渣场先上传，且渣场所有数据都正确，非完整模式直接判定完成");
                            if (projectDispatchMode.getAlians() == 2)
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitCheckUp);
                            else
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                        }
                        projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                    }
                }
            } else if (projectDispatchMode.compareTo(ProjectDispatchMode.Unknown) == 0) {
                errorCode = WorkMergeFailEnum.LostSchedule.getValue();
                errorMessage = WorkMergeFailEnum.LostSchedule.getName();
                throw new SmartminingProjectException("排班丢失，终端未上传，且后台未查询到，渣车编号：" + carCode);
            }
            projectUnloadLog.setDetail(true);
            projectUnloadLogServiceI.save(projectUnloadLog);
            return projectCarWorkInfo;
            // }
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            ProjectMqttCardReport report = new ProjectMqttCardReport();
            report.setErrorCode(errorCode);
            report.setErrorCodeMessage(errorMessage);
            report.setMessage(e.getMessage());
            report.setProjectId(projectUnloadLog.getProjectID());
            report.setCarCode(projectUnloadLog.getCarCode());
            report.setCarId(projectUnloadLog.getCarID());
            report.setTimeLoad(projectUnloadLog.getTimeLoad());
            report.setTimeDischarge(projectUnloadLog.getTimeDischarge());
            report.setLoaderName(projectUnloadLog.getLoaderName());
            report.setLoader(projectUnloadLog.getLoader());
            report.setMachineId(projectUnloadLog.getExcavatCurrent());
            report.setDispatchMode(projectDispatchMode);
            report.setRemark("渣场合并抛出");
            report.setMergeError(WorkMergeErrorEnum.SLAGSITEUPDLOAD);
            report.setExceptionDetails(JSON.toJSONString(e.getStackTrace()));
            report.setUploadByDevice(uploadByDevice);
            report.setLoader(projectUnloadLog.getLoader());
            report.setSlagSiteId(projectUnloadLog.getSlagfieldID());
            report.setSlagSiteName(projectUnloadLog.getSlagFieldName());
            Map<String, Date> dateMap = workDateService.getWorkTime(projectUnloadLog.getProjectID(), projectUnloadLog.getTimeDischarge());
            Date startTime = dateMap.get("start");
            if (projectUnloadLog.getTimeDischarge().getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            report.setDateIdentification(DateUtils.createReportDateByMonth(startTime));
            Shift shift = workDateService.getShift(projectUnloadLog.getTimeDischarge(), projectUnloadLog.getProjectID());
            report.setShift(shift);
            ProjectMqttCardReport reportNew = getMqttCardReport(report);
            if(reportNew != null)
                projectMqttCardReportServiceI.save(reportNew);
            projectUnloadLog.setDetail(true);
            projectUnloadLogServiceI.save(projectUnloadLog);
            return null;
        } catch (Exception ex) {
            ProjectMqttCardReport report = new ProjectMqttCardReport();
            report.setErrorCode(errorCode);
            report.setErrorCodeMessage(errorMessage);
            report.setMessage("后台异常，请联系管理员。");
            report.setProjectId(projectUnloadLog.getProjectID());
            report.setCarCode(projectUnloadLog.getCarCode());
            report.setCarId(projectUnloadLog.getCarID());
            report.setTimeLoad(projectUnloadLog.getTimeLoad());
            report.setTimeDischarge(projectUnloadLog.getTimeDischarge());
            report.setLoaderName(projectUnloadLog.getLoaderName());
            report.setLoader(projectUnloadLog.getLoader());
            report.setMachineId(projectUnloadLog.getExcavatCurrent());
            report.setDispatchMode(projectDispatchMode);
            report.setRemark("渣车合并抛出");
            report.setMergeError(WorkMergeErrorEnum.SLAGCARUPDALOAD);
            report.setUploadByDevice(uploadByDevice);
            report.setExceptionDetails(JSON.toJSONString(ex.getStackTrace()));
            report.setLoader(projectUnloadLog.getLoader());
            report.setSlagSiteId(projectUnloadLog.getSlagfieldID());
            report.setSlagSiteName(projectUnloadLog.getSlagFieldName());
            Map<String, Date> dateMap = workDateService.getWorkTime(projectUnloadLog.getProjectID(), projectUnloadLog.getTimeDischarge());
            Date startTime = dateMap.get("start");
            if (projectUnloadLog.getTimeDischarge().getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            report.setDateIdentification(DateUtils.createReportDateByMonth(startTime));
            Shift shift = workDateService.getShift(projectUnloadLog.getTimeDischarge(), projectUnloadLog.getProjectID());
            report.setShift(shift);
            projectMqttCardReportServiceI.save(report);
            projectUnloadLog.setDetail(true);
            projectUnloadLogServiceI.save(projectUnloadLog);
            return null;
        }
    }

    //挖机上传日志
    @Transactional(dontRollbackOn = IOException.class)
    public ProjectLoadLog handleExcavatorMessageCarLog(String payload, String replytopic, String deviceId, String device, Long pktID) {
        Long cmdStatus = 0L;
        ProjectLoadLog ret = null;
        try {
            ProjectLoadLog projectLoadLog = JSONObject.parseObject(payload, ProjectLoadLog.class);
            if (projectLoadLog != null) {
                projectLoadLog.setTimeLoad(new Date(projectLoadLog.getTimeLoad().getTime() * 1000));
                projectLoadLog.setTimeCheck(new Date(projectLoadLog.getTimeCheck().getTime() * 1000));
                projectLoadLog.setTimeDischarge(new Date(projectLoadLog.getTimeDischarge().getTime() * 1000));
                ret = projectLoadLogServiceI.save(projectLoadLog);
            }
        } catch (Exception e) {
            smartminingExceptionService.save(e, payload);
            e.printStackTrace();
            log.error(e.getMessage());
            cmdStatus = 1L;
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, new DeviceReply("carLog", pktID, cmdStatus));
            } catch (Exception ex) {
                //smartminingExceptionService.save(ex);
                ex.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 挖机上传
     *
     * @param projectLoadLog
     */
    @Transactional
    public void updateCarWorkInfo(ProjectLoadLog projectLoadLog) {
        try {
            Project project = projectServiceI.get(projectLoadLog.getProjectID());
            Long projectId = project.getId();
            //挖机编号
            Long diggingMachineId = projectLoadLog.getExcavatCurrent();
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(diggingMachineId);
            Long carId = projectLoadLog.getCarID();
            String carCode = projectLoadLog.getCarCode();
            Date timeLoad = projectLoadLog.getTimeLoad();
            Long materialId = projectLoadLog.getLoader();
            Long cubic = projectCarServiceI.get(carId).getModifyCapacity();
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, diggingMachineId, true);
            if (scheduleMachineList == null || scheduleMachineList.size() == 0)
                throw new SmartminingProjectException("该挖机暂未排班");
            ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachineList.get(0).getGroupCode());
            if (projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.DiggingMachine) == 0 || projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.All) == 0) {
                Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, timeLoad);
                ProjectCarWorkInfo projectCarWorkInfo;
                if (object != null) {//存在
                    projectCarWorkInfo = (ProjectCarWorkInfo) object;
                    if (projectCarWorkInfo.getTimeLoad() == null || projectCarWorkInfo.getTimeLoad().getTime() == 0)
                        return;
                    projectCarWorkInfo.setLoadUp(true);
                    projectCarWorkInfo.setCubic(cubic);
                    projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                    projectCarWorkInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                    projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                    projectCarWorkInfo.setDiggingMachineId(diggingMachineId);
                    projectCarWorkInfo.setDiggingMachineCode(scheduleMachineList.get(0).getMachineCode());
                    projectCarWorkInfo.setTimeLoad(timeLoad);
                    if (projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.All) == 0) {
                        if (projectCarWorkInfo.getCheckUp()) //如果监测站已上传
                            projectCarWorkInfo.setStatus(projectCarWorkInfo.getLoadUp() ? ProjectCarWorkStatus.Finish : ProjectCarWorkStatus.UnCheck);
                        else                                                 //监测站未上传
                            projectCarWorkInfo.setStatus(projectCarWorkInfo.getLoadUp() ? ProjectCarWorkStatus.WaitCheckUp : ProjectCarWorkStatus.UnCheck);
                    } else {
                        projectCarWorkInfo.setPass(Score.Pass);
                        if (projectCarWorkInfo.getUnLoadUp())
                            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                        else
                            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                    }
                    if (projectCarWorkInfo.getStatus().compareTo(ProjectCarWorkStatus.Finish) == 0) {
                        //app即时报表展示的数据
                        ProjectAppStatisticsByCar appCar = projectAppStatisticsByCarServiceI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, carCode, projectCarWorkInfo.getShift().getAlias(), projectCarWorkInfo.getDateIdentification());
                        if (appCar == null) {
                            appCar = new ProjectAppStatisticsByCar();
                        }
                        appCar.setCubic(appCar.getCubic() + projectCarWorkInfo.getCubic());
                        appCar.setCarCount(appCar.getCarCount() + 1);
                        appCar.setShift(ShiftsEnums.converShift(projectCarWorkInfo.getShift().getAlias()));
                        appCar.setProjectId(projectId);
                        appCar.setCarCode(carCode);
                        appCar.setCreateDate(projectCarWorkInfo.getDateIdentification());
                        projectAppStatisticsByCarServiceI.save(appCar);
                    }
                } else { //不存在
                    projectCarWorkInfo = new ProjectCarWorkInfo();
                    projectCarWorkInfo.setProjectId(projectId);
                    projectCarWorkInfo.setCarId(carId);
                    projectCarWorkInfo.setCarCode(carCode);
                    projectCarWorkInfo.setTimeLoad(timeLoad);
                    projectCarWorkInfo.setCubic(cubic);
                    projectCarWorkInfo.setDiggingMachineId(diggingMachineId);
                    projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                    projectCarWorkInfo.setPricingType(scheduleMachineList.get(0).getPricingType());
                    projectCarWorkInfo.setMaterialId(materialId);
                    projectCarWorkInfo.setMateriaName(scheduleMachineList.get(0).getMaterialName());
                    Date dateIdentification = DateUtils.createReportDateByMonth(timeLoad);
                    projectCarWorkInfo.setDateIdentification(dateIdentification);
                    projectCarWorkInfo.setCreateDate(new Date());
                    projectCarWorkInfo.setLoadUp(true);
                    if (projectSchedule.getDeviceStartStatus().compareTo(DeviceStartStatusEnum.DiggingMachine) == 0)
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                    else
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitCheckUp);
                }
                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, projectCarWorkInfo.getTimeLoad());
                Date earlyStartTime = dateMap.get("start");
                Date earlyEndTime = dateMap.get("earlyEnd");
                Date nightStartTime = dateMap.get("nightStart");
                Date nightEndTime = dateMap.get("end");
                Shift shift = Shift.Unknown;
                if (projectCarWorkInfo.getTimeLoad().getTime() >= earlyStartTime.getTime() && projectCarWorkInfo.getTimeLoad().getTime() <= earlyEndTime.getTime())
                    shift = Shift.Early;
                else if (projectCarWorkInfo.getTimeLoad().getTime() >= nightStartTime.getTime() && projectCarWorkInfo.getTimeLoad().getTime() <= nightEndTime.getTime())
                    shift = Shift.Night;
                ProjectCarCount projectCarCount = projectCarCountServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, carCode, projectCarWorkInfo.getDateIdentification(), projectCarWorkInfo.getShift().getAlias(), CarType.DiggingMachine.getValue());
                if (projectCarCount == null) {
                    projectCarCount = new ProjectCarCount();
                    projectCarCount.setProjectId(projectId);
                    projectCarCount.setCarId(diggingMachineId);
                    projectCarCount.setCarCode(machine.getCode());
                    projectCarCount.setTotalCount(1L);
                    projectCarCount.setCarType(CarType.DiggingMachine);
                    projectCarCount.setShifts(shift);
                    projectCarCount.setDateIdentification(projectCarWorkInfo.getDateIdentification());
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(projectCarCount.getDetailJson());
                    List<Long> materialIdList = new ArrayList<>();
                    List<Map> detailList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String text = jsonArray.get(i).toString();
                        Map detailMap = JSON.parseObject(text, new TypeReference<Map>() {
                        });
                        Long detailMaterialId = Long.parseLong(detailMap.get("materialId").toString());
                        String detailMaterialName = detailMap.get("materialName").toString();
                        Long materialCount = Long.parseLong(detailMap.get("count").toString());
                        if (detailMaterialId == materialId) {
                            Map mapNew = new HashMap();
                            materialCount++;
                            mapNew.put("materialId", detailMaterialId);
                            mapNew.put("materialName", detailMaterialName);
                            mapNew.put("count", materialCount);
                            detailList.add(mapNew);
                        } else {
                            detailList.add(detailMap);
                        }
                        materialIdList.add(detailMaterialId);
                    }
                    if (!materialIdList.contains(materialId)) {
                        Map map = new HashMap();
                        map.put("materialId", materialId);
                        map.put("materialName", projectCarWorkInfo.getMaterialName());
                        map.put("count", 1);
                        detailList.add(map);
                    }
                    String json = JSON.toJSONString(detailList);
                    projectCarCount.setDetailJson(json);
                }
                Map workTimeMap = projectWorkTimeByDiggingServiceI.getTotalWorkTimeByProjectIdAndDateTime(projectId, projectCarWorkInfo.getTimeLoad(), shift.getAlias());
                //已经有结束时间下班工作时长
                Long workTimeEnd = workTimeMap != null && workTimeMap.size() > 0 ? Long.parseLong(workTimeMap.get("workTime").toString()) : 0L;
                List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdByQueryAndShift(projectId, projectCarWorkInfo.getTimeLoad(), shift.getAlias());
                for (ProjectWorkTimeByDigging digging : diggingList) {
                    Date endDate = new Date();
                    Long time = DateUtils.calculationHour(digging.getStartTime(), endDate);
                    workTimeEnd = workTimeEnd + time;
                }
                projectCarCount.setWorkTime(workTimeEnd);
                projectCarCount.setTotalCount(projectCarCount.getTotalCount() + 1);
                projectCarCountServiceI.save(projectCarCount);

                projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            }
        } catch (Exception ex) {
            WorkMergeErrorLog errorLog = new WorkMergeErrorLog();
            errorLog.setParams(JSON.toJSONString(projectLoadLog));
            errorLog.setProjectId(projectLoadLog.getProjectID());
            StackTraceElement element = ex.getStackTrace()[0];
            errorLog.setLineNumber(element.getLineNumber());
            errorLog.setCarCode(projectLoadLog.getCarCode());
            errorLog.setCarId(projectLoadLog.getCarID());
            errorLog.setMessage(ex.getMessage());
            errorLog.setDetailMessage(JSON.toJSONString(ex.getStackTrace()));
            errorLog.setProjectDevice(ProjectDeviceType.DiggingMachineDevice);
            errorLog.setTimeLoad(projectLoadLog.getTimeLoad());
            errorLog.setTimeCheck(projectLoadLog.getTimeCheck());
            errorLog.setTimeDischarge(projectLoadLog.getTimeDischarge());
            errorLog.setCreateDate(new Date());
            errorLog.setUid(projectLoadLog.getUid());
            errorLog.setEventId(projectLoadLog.getEventId());
            errorLog.setPktID(projectLoadLog.getPktID());
            try {
                workMergeErrorLogServiceI.save(errorLog);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("添加失败");
            }
            log.error(ex.getMessage());
            log.error("插入作业信息异常，可能是没有对应的排班信息或装载信息");
        }
    }

    //渣车上传日志
    public ProjectSlagCarLog handleMessageSlagCar(String payload, String replytopic, String deviceId, String device, Long pktID) {
        Long cmdStatus = 0L;
        ProjectSlagCarLog ret = null;
        try {
            ProjectSlagCarLog log = JSONObject.parseObject(payload, ProjectSlagCarLog.class);
            if (log != null) {
                log.setTerminalTime(log.getTimeLoad().getTime() * 1000);
                log.setTimeLoad(new Date(log.getTimeLoad().getTime() * 1000));
                log.setTimeCheck(new Date(log.getTimeCheck().getTime() * 1000));
                log.setTimeDischarge(new Date(log.getTimeDischarge().getTime() * 1000));
                ret = projectSlagCarLogServiceI.save(log);
            }
        } catch (IOException e) {
            smartminingExceptionService.save(e, payload);
            e.printStackTrace();
            log.error(e.getMessage());
            cmdStatus = 1L;
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, new DeviceReply("carLog", pktID, cmdStatus));
            } catch (Exception ex) {
                //smartminingExceptionService.save(ex);
                ex.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 优化渣车作业上传
     *
     * @param projectSlagCarLog
     */
    public ProjectCarWorkInfo updateCarWorkInfo(ProjectSlagCarLog projectSlagCarLog) throws IOException {
        ProjectCarWorkInfo projectCarWorkInfo = null;
        Integer errorCode = 0;
        String errorMessage = "";
        Integer dispatchMode = projectSlagCarLog.getDispatchMode();
        Boolean uploadByDevice = true;
        ProjectDispatchMode projectDispatchMode = ProjectDispatchMode.converMode(dispatchMode);
        try {
            if (projectDispatchMode.getAlians() == 0) {
                uploadByDevice = false;
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectSlagCarLog.getProjectID(), projectSlagCarLog.getCarID(), true);
                if (scheduleCarList == null || scheduleCarList.size() < 1) {
                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                    throw new SmartminingProjectException("调度模式不存在，且重新获取失败");
                }
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectSlagCarLog.getProjectID(), scheduleCarList.get(0).getGroupCode());
                if (projectSchedule == null) {
                    errorCode = WorkMergeFailEnum.WithoutSchedule.getValue();
                    errorMessage = WorkMergeFailEnum.WithoutSchedule.getName();
                    throw new SmartminingProjectException("调度模式不存在，且重新获取失败");
                }
                projectDispatchMode = projectSchedule.getDispatchMode();
            }
            Project project = projectServiceI.get(projectSlagCarLog.getProjectID());
            Long projectId = project.getId();
            //渣车ID
            Long carId = projectSlagCarLog.getCarID();
            //渣车编号
            String carCode = projectSlagCarLog.getCarCode();
            //装载时间
            Date timeLoad = projectSlagCarLog.getTimeLoad();
            //渣车对象
            ProjectCar projectCar = projectCarServiceI.get(carId);
            if (projectCar == null) {
                errorCode = WorkMergeFailEnum.WithoutCarCode.getValue();
                errorMessage = WorkMergeFailEnum.WithoutCarCode.getName();
                throw new SmartminingProjectException("渣车不存在，渣车ID：" + carId);
            }
            //方量
            Long cubic = projectCar.getModifyCapacity();
            //挖机编号
            Long diggingMachineId = projectSlagCarLog.getExcavatCurrent();
            //挖机对象
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(diggingMachineId);
            if (machine == null) {
                errorCode = WorkMergeFailEnum.WithoutDiggingMachine.getValue();
                errorMessage = WorkMergeFailEnum.WithoutDiggingMachine.getName();
                throw new SmartminingProjectException("挖机不存在，挖机ID：" + diggingMachineId);
            }
            //物料ID
            Long materialId = projectSlagCarLog.getLoader();
            //物料对象
            ProjectMaterial projectMaterial = projectMaterialServiceI.get(materialId);
            if (projectMaterial == null) {
                errorCode = WorkMergeFailEnum.WithoutLoader.getValue();
                errorMessage = WorkMergeFailEnum.WithoutLoader.getName();
                throw new SmartminingProjectException("物料不存在，物料ID：" + materialId);
            }
            //计价方式
            PricingTypeEnums pricingType = PricingTypeEnums.convert(projectSlagCarLog.getPriceMethod());
            //调度模式
            Integer scheduleMode = projectSlagCarLog.getDispatchMode();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, projectSlagCarLog.getTimeLoad());
            Date earlyStartTime = dateMap.get("start");
            if (timeLoad == null || timeLoad.getTime() == 0) {
                errorCode = WorkMergeFailEnum.WorkError.getValue();
                errorMessage = WorkMergeFailEnum.WorkError.getName();
                throw new SmartminingProjectException("未按规定装载，渣车ID：" + carId);
            }
            //允许倒渣的渣场ID 多个用逗号隔开
            String slagSiteStr = projectSlagCarLog.getSlagSiteID();
            List<Long> slagSiteList = new ArrayList<>();
            if (StringUtils.isNotEmpty(slagSiteStr)) {
                String[] slagSiteArray = null;
                if (slagSiteStr.indexOf(",") != -1) {
                    slagSiteArray = slagSiteStr.split(",");
                } else {
                    slagSiteArray = new String[1];
                    slagSiteArray[0] = slagSiteStr;
                }
                if (slagSiteArray != null) {
                    for (int i = 0; i < slagSiteArray.length; i++) {
                        Long id = Long.parseLong(slagSiteArray[i]);
                        slagSiteList.add(id);
                    }
                }
            }
            Object object = projectCarWorkInfoServiceI.getByProjectIdAndCarIdAndTimeLoad(projectId, carId, timeLoad);
            if (object != null) {
                //已经存在当前装载时间对应的渣车信息
                projectCarWorkInfo = (ProjectCarWorkInfo) object;
                Long distance = projectSlagCarLog.getExctDist() + projectCarWorkInfo.getDistance();
                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
                projectCarWorkInfo.setLoadUp(true);
                projectCarWorkInfo.setCubic(cubic);
                projectCarWorkInfo.setDistance(distance);
                projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                projectCarWorkInfo.setPayableDistance(payableDistance);
                projectCarWorkInfo.setAmount(amount);
                projectCarWorkInfo.setPricingType(pricingType);
                projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                projectCarWorkInfo.setMaterialName(projectMaterial.getName());
                projectCarWorkInfo.setDiggingMachineId(machine.getId());
                projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                projectCarWorkInfo.setTimeLoad(timeLoad);
                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                projectCarWorkInfo.setTimeStay(new BigDecimal(projectSlagCarLog.getTimeStay() / 1000).divide(new BigDecimal(60), 1, BigDecimal.ROUND_CEILING));
                if (projectCarWorkInfo.getCarOwnerId() == null || projectCarWorkInfo.getCarOwnerId() == 0)
                    projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                if (StringUtils.isEmpty(projectCarWorkInfo.getCarOwnerName()))
                    projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                //判断调度类型
                if (scheduleMode == 2) {
                    if (projectCarWorkInfo.getCheckUp()) //如果监测站已上传
                        projectCarWorkInfo.setStatus(projectCarWorkInfo.getLoadUp() ? ProjectCarWorkStatus.Finish : ProjectCarWorkStatus.UnCheck);
                    else                                                 //监测站未上传
                        projectCarWorkInfo.setStatus(projectCarWorkInfo.getLoadUp() ? ProjectCarWorkStatus.WaitCheckUp : ProjectCarWorkStatus.UnCheck);
                } else {
                    projectCarWorkInfo.setPass(Score.Pass);
                    if (projectCarWorkInfo.getUnLoadUp())
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                    else
                        projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                }
                if (!slagSiteList.contains(projectCarWorkInfo.getSlagSiteId())) {
                    projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                    projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                    handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                }
                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            } else {
                projectCarWorkInfo = new ProjectCarWorkInfo();
                projectCarWorkInfo.setProjectId(projectId);
                projectCarWorkInfo.setCarId(carId);
                projectCarWorkInfo.setCarCode(carCode);
                projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                projectCarWorkInfo.setDistance(projectSlagCarLog.getExctDist());
                projectCarWorkInfo.setTimeLoad(timeLoad);
                projectCarWorkInfo.setCubic(cubic);
                projectCarWorkInfo.setDiggingMachineId(machine.getId());
                projectCarWorkInfo.setDiggingMachineCode(machine.getCode());
                projectCarWorkInfo.setPricingType(pricingType);
                projectCarWorkInfo.setMaterialId(projectMaterial.getId());
                projectCarWorkInfo.setAllowSlagSites(projectSlagCarLog.getSlagSiteID());
                projectCarWorkInfo.setTimeStay(new BigDecimal(projectSlagCarLog.getTimeStay() / 1000l).divide(new BigDecimal(60), 1, BigDecimal.ROUND_CEILING));
                projectCarWorkInfo.setMaterialName(projectMaterial.getName());
                Date dateIdentification = DateUtils.createReportDateByMonth(earlyStartTime);
                Date date = new Date();
                if (earlyStartTime.getTime() > date.getTime())
                    dateIdentification = DateUtils.subtractionOneDay(dateIdentification);
                projectCarWorkInfo.setDateIdentification(dateIdentification);
                projectCarWorkInfo.setCreateDate(date);
                projectCarWorkInfo.setLoadUp(true);
                //projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                //projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 渣车先上传");
                projectCarWorkInfo.setDispatchMode(projectDispatchMode);
                if (scheduleMode == 3 || scheduleMode == 4 || scheduleMode == 1) {
                    projectCarWorkInfo.setStatus(ProjectCarWorkStatus.UnUnload);
                } else if (scheduleMode == 2)
                    projectCarWorkInfo.setStatus(ProjectCarWorkStatus.WaitCheckUp);
                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            }
            return projectCarWorkInfo;
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            ProjectMqttCardReport report = new ProjectMqttCardReport();
            report.setErrorCode(errorCode);
            report.setErrorCodeMessage(errorMessage);
            report.setMessage(e.getMessage());
            report.setProjectId(projectSlagCarLog.getProjectID());
            report.setCarCode(projectSlagCarLog.getCarCode());
            report.setCarId(projectSlagCarLog.getCarID());
            report.setTimeLoad(projectSlagCarLog.getTimeLoad());
            //report.setTimeDischarge(projectUnloadLog.getTimeDischarge());
            report.setLoader(projectSlagCarLog.getLoader());
            report.setMachineId(projectSlagCarLog.getExcavatCurrent());
            report.setDispatchMode(projectDispatchMode);
            report.setRemark("渣车合并抛出");
            report.setMergeError(WorkMergeErrorEnum.SLAGCARUPDALOAD);
            report.setUploadByDevice(uploadByDevice);
            Map<String, Date> dateMap = workDateService.getWorkTime(projectSlagCarLog.getProjectID(), projectSlagCarLog.getTimeLoad());
            Date startTime = dateMap.get("start");
            if (projectSlagCarLog.getTimeLoad().getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            report.setDateIdentification(DateUtils.createReportDateByMonth(startTime));
            Shift shift = workDateService.getShift(projectSlagCarLog.getTimeLoad(), projectSlagCarLog.getProjectID());
            report.setShift(shift);
            projectMqttCardReportServiceI.save(report);
            return null;
        } catch (Exception ex) {
            ProjectMqttCardReport report = new ProjectMqttCardReport();
            report.setErrorCode(errorCode);
            report.setErrorCodeMessage(errorMessage);
            report.setMessage("后台异常，请联系管理员。");
            report.setProjectId(projectSlagCarLog.getProjectID());
            report.setCarCode(projectSlagCarLog.getCarCode());
            report.setCarId(projectSlagCarLog.getCarID());
            report.setTimeLoad(projectSlagCarLog.getTimeLoad());
            //report.setTimeDischarge(projectUnloadLog.getTimeDischarge());
            report.setLoader(projectSlagCarLog.getLoader());
            report.setMachineId(projectSlagCarLog.getExcavatCurrent());
            report.setDispatchMode(projectDispatchMode);
            report.setRemark("渣车合并抛出");
            report.setMergeError(WorkMergeErrorEnum.SLAGCARUPDALOAD);
            report.setUploadByDevice(uploadByDevice);
            Map<String, Date> dateMap = workDateService.getWorkTime(projectSlagCarLog.getProjectID(), projectSlagCarLog.getTimeLoad());
            Date startTime = dateMap.get("start");
            if (projectSlagCarLog.getTimeLoad().getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            report.setDateIdentification(DateUtils.createReportDateByMonth(startTime));
            Shift shift = workDateService.getShift(projectSlagCarLog.getTimeLoad(), projectSlagCarLog.getProjectID());
            report.setShift(shift);
            projectMqttCardReportServiceI.save(report);
            return null;
        }
    }

    private void handleMessageDatetime(String cmdInd, String replytopic, Long pktID) throws JsonProcessingException {
        Calendar now = Calendar.getInstance();
        DatetimeReply datetime = new DatetimeReply();
        datetime.setCmdInd(cmdInd);
        datetime.setPktID(pktID);
        datetime.setYear(now.get(Calendar.YEAR));
        datetime.setMonth(now.get(Calendar.MONTH) + 1);
        datetime.setDay(now.get(Calendar.DAY_OF_MONTH));
        datetime.setHour(now.get(Calendar.HOUR_OF_DAY));
        datetime.setMinute(now.get(Calendar.MINUTE));
        datetime.setSecond(now.get(Calendar.SECOND));
        mqttSender.sendDeviceReply(replytopic, datetime);
    }

    private class DatetimeReply implements Serializable {
        private String cmdInd = "";
        private Long pktID = 0L;
        private Integer cmdStatus = 0;
        private Integer year = 0;
        private Integer month = 0;
        private Integer day = 0;
        private Integer hour = 0;
        private Integer minute = 0;
        private Integer second = 0;

        public void setCmdInd(String cmdInd) {
            this.cmdInd = cmdInd;
        }

        public String getCmdInd() {
            return cmdInd;
        }

        public void setPktID(Long pktID) {
            this.pktID = pktID;
        }

        public Long getPktID() {
            return pktID;
        }

        public void setCmdStatus(Integer cmdStatus) {
            this.cmdStatus = cmdStatus;
        }

        public Integer getCmdStatus() {
            return cmdStatus;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getYear() {
            return year;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getMonth() {
            return month;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getDay() {
            return day;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Integer getHour() {
            return hour;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }

        public Integer getSecond() {
            return second;
        }
    }

    private class DeviceReply implements Serializable {
        private String cmdInd = "";
        private Long pktID = 0L;
        private Long cmdStatus = 0L;

        public DeviceReply(String cmdInd, Long pktID, Long cmdStatus) {
            this.cmdInd = cmdInd;
            this.pktID = pktID;
            this.cmdStatus = cmdStatus;
        }

        public String getCmdInd() {
            return cmdInd;
        }

        public void setCmdInd(String cmdInd) {
            this.cmdInd = cmdInd;
        }

        public Long getPktID() {
            return pktID;
        }

        public void setPktID(Long pktID) {
            this.pktID = pktID;
        }

        public Long getCmdStatus() {
            return cmdStatus;
        }

        public void setCmdStatus(Long cmdStatus) {
            this.cmdStatus = cmdStatus;
        }
    }

    /**
     * 挖机设备主动获取排班信息
     *
     * @param cmdInd
     * @param replytopic
     * @param pktID
     * @return
     */
    public void handleMessageSchedule(String cmdInd, String replytopic, Long pktID, Long projectId, Long
            machineId, String deviceId, String params) throws JsonProcessingException {
        ScheduledReply reply = new ScheduledReply();
        reply.setCmdInd(cmdInd);
        reply.setExcavatorID(machineId);
        reply.setPktID(pktID);
        String carsId = "";
        String carsCode = "";
        Long cmdStatus = 0L;
        try {
            System.out.println("开始获取指定挖机的排版信息");
            //自动绑定挖机对应的卡
            handleMessageCard(deviceId, projectId, machineId);
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, true);
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachineList.get(0).getGroupCode());
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachineList.get(0).getGroupCode());
                if (scheduleCarList != null && scheduleCarList.size() > 0) {
                    int i = 0;
                    for (ScheduleCar scheduled : scheduleCarList) {
                        if (scheduled.getCarId() != null && StringUtils.isNotEmpty(scheduled.getCarCode())) {
                            if (i == 0) {
                                carsId = scheduled.getCarId().toString();
                            } else {
                                carsId = scheduled.getCarId() + SmartminingConstant.COMMA + carsId;
                            }
                            if (i == 0) {
                                carsCode = scheduled.getCarCode();
                            } else {
                                carsCode = scheduled.getCarCode() + SmartminingConstant.COMMA + carsCode;
                            }
                            i++;
                        }
                    }
                    reply.setMatId(scheduleMachineList.get(0).getMaterialId());
                    reply.setMatName(scheduleMachineList.get(0).getMaterialName());
                    reply.setPmId(scheduleMachineList.get(0).getPricingType().getValue());
                    reply.setPmName(scheduleMachineList.get(0).getPricingType().getName());
                    reply.setPlaceName(projectSchedule.getPlaceName());
                    reply.setSlagSiteName(projectSchedule.getSlagSiteName());
                    reply.setDispatchMode(projectSchedule.getDispatchMode().getAlians());
                }
            }
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e, params, "挖机设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (IOException e) {
            smartminingExceptionService.save(e, params, "挖机设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (Exception e) {
            smartminingExceptionService.save(e, params, "挖机设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } finally {
            reply.setCarID(carsId);
            reply.setCarCode(carsCode);
            reply.setCmdStatus(cmdStatus);
            reply.setProjectID(projectId);
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * 渣车设备主动获取排班信息
     *
     * @param cmdInd
     * @param replytopic
     * @param pktID
     * @return
     */
    public void handleMessageScheduleByCar(String cmdInd, String replytopic, Long pktID, Long projectId, Long
            slagCarId, String deviceId, String params) throws JsonProcessingException {
        ScheduledReplyByCar reply = new ScheduledReplyByCar();
        reply.setCmdInd(cmdInd);
        reply.setSlagcarID(slagCarId);
        reply.setPktID(pktID);
        reply.setProjectID(projectId);
        String machineId = "";
        String machineCode = "";
        String pricingType = "";
        String distance = "";
        String loader = "";
        Long cmdStatus = 0L;
        try {
            System.out.println("开始获取指定渣车的排版信息");
            handleMessageCardByCar(deviceId, projectId, slagCarId);
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, slagCarId, true);
            List<ScheduleMachine> scheduleMachineList = scheduleCarList != null && scheduleCarList.size() > 0 ? scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode()) : null;
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
                if (projectSchedule != null) {
                    reply.setSchMode(projectSchedule.getDeviceStartStatus().getValue());
                    reply.setDispatchMode(projectSchedule.getDispatchMode().getAlians());
                    String slagSiteIds = projectSchedule.getSlagSiteId();
                    JSONArray slagSiteIdArray = JSONArray.parseArray(slagSiteIds);
                    List<ProjectSlagSite> projectSlagSiteList = projectSlagSiteServiceI.getAllByProjectId(projectId);
                    //生成渣场索引
                    Map<Long, Integer> slagSiteMapIndex = new HashMap<>();
                    for (int i = 0; i < projectSlagSiteList.size(); i++) {
                        slagSiteMapIndex.put(projectSlagSiteList.get(i).getId(), i);
                    }
                    if (slagSiteIdArray != null) {
                        String slagSiteID = "";
                        String position = "";
                        for (int i = 0; i < slagSiteIdArray.size(); i++) {
                            Long slagSiteId = slagSiteIdArray.getLong(i);
                            Integer index = slagSiteMapIndex.get(slagSiteId);
                            ProjectSlagSite projectSlagSite = index != null ? projectSlagSiteList.get(index) : null;
                            if (projectSlagSite != null) {
                                if (i == 0) {
                                    slagSiteID = slagSiteId.toString();
                                    position = projectSlagSite.getLongitude() + "-" + projectSlagSite.getLatitude();
                                } else {
                                    slagSiteID = slagSiteId + SmartminingConstant.COMMA + slagSiteID;
                                    position = projectSlagSite.getLongitude() + "-" + projectSlagSite.getLatitude() + SmartminingConstant.COMMA + position;
                                }
                            }
                        }
                        reply.setSlagSiteID(slagSiteID);
                        reply.setPosition(position);
                    }
                }
                int i = 0;
                for (ScheduleMachine scheduleMachine : scheduleMachineList) {
                    if (scheduleCarList.get(0).getCarId() != null && StringUtils.isNotEmpty(scheduleCarList.get(0).getCarCode())) {
                        if (i == 0) {
                            machineId = scheduleMachine.getMachineId().toString();
                            machineCode = scheduleMachine.getMachineCode();
                            pricingType = scheduleMachine.getPricingType().getValue().toString();
                            distance = scheduleMachine.getDistance().toString();
                            loader = scheduleMachine.getMaterialId().toString();
                        } else {
                            machineId = scheduleMachine.getMachineId() + SmartminingConstant.COMMA + machineId;
                            machineCode = scheduleMachine.getMachineCode() + SmartminingConstant.COMMA + machineCode;
                            pricingType = scheduleMachine.getPricingType().getValue() + SmartminingConstant.COMMA + pricingType;
                            distance = scheduleMachine.getDistance() + SmartminingConstant.COMMA + distance;
                            loader = scheduleMachine.getMaterialId() + SmartminingConstant.COMMA + loader;
                        }
                        i++;
                    }
                }
            }

            //-------
            Project p = projectServiceI.get(projectId);
            if (p != null && p.getDispatchMode() == ProjectDispatchMode.Auto) {
                List<ProjectScheduleDetail> ps = projectScheduleDetailServiceI.findByProjectCarIdAndProjectIdOrderByIdDesc(slagCarId, projectId);
                if (ps.size() > 0) {
                    machineId = "" + ps.get(0).getProjectDiggingMachineId();
                    machineCode = ps.get(0).getDiggingMachineCode();
                }
            }

        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (IOException e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (Exception e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
        } finally {
            reply.setCmdStatus(cmdStatus);
            reply.setExcavatorID(machineId);
            reply.setExcavatorCode(machineCode);
            reply.setPriceMethod(pricingType);
            reply.setExctDist(distance);
            reply.setLoader(loader);
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * 最新渣车获取排班 todo 待完善
     *
     * @param cmdInd
     * @param replytopic
     * @param pktID
     * @param projectId
     * @param slagCarId
     * @param deviceId
     * @param params
     * @throws JsonProcessingException
     */
    public void handleMessageScheduleAllByCar(String cmdInd, String replytopic, Long pktID, Long projectId, Long
            slagCarId, String deviceId, String params) throws JsonProcessingException {
        ScheduleAllReplyByCar reply = new ScheduleAllReplyByCar();
        reply.setCmdInd(cmdInd);
        reply.setSlagcarID(slagCarId);
        reply.setPktID(pktID);
        reply.setProjectID(projectId);
        reply.setExctWait("0");
        String machineId = "";
        String machineCode = "";
        String exctPrice = "";
        String exctPriceID = "";        //挖机计价方式ID
        String distance = "";
        String exctLoader = "";
        String exctLoaderID = "";       //挖机对应的物料ID
        Long cmdStatus = 0L;
        try {
            Project project = projectServiceI.get(projectId);
            reply.setProject(project.getName());
            System.out.println("开始获取指定渣车的排版信息");
            handleMessageCardByCar(deviceId, projectId, slagCarId);
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, slagCarId, true);
            List<ScheduleMachine> scheduleMachineList = scheduleCarList != null && scheduleCarList.size() > 0 ? scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode()) : null;
            //获取所有挖机
            List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdAndIsVaild(projectId, true);
            //生成索引
            Map<String, Integer> machineMapIndex = new HashMap<>();
            for (int i = 0; i < projectDiggingMachineList.size(); i++) {
                String diggingMachineCode = projectDiggingMachineList.get(i).getCode();
                machineMapIndex.put(diggingMachineCode, i);
            }
            //获取所有挖机终端
            List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByProjectIdAndDeviceType(projectId, ProjectDeviceType.DiggingMachineDevice.getAlian());
            //生成索引
            Map<String, Integer> deviceMapIndex = new HashMap<>();
            for (int i = 0; i < projectDeviceList.size(); i++) {
                String diggingMachineCode = projectDeviceList.get(i).getCode();
                deviceMapIndex.put(diggingMachineCode, i);
            }
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
                if (projectSchedule != null) {
                    reply.setSchMode(projectSchedule.getDeviceStartStatus().getValue());
                    reply.setDispatchMode(projectSchedule.getDispatchMode().getAlians());
                    String managerName = "";
                    JSONArray jsonArray = JSONArray.parseArray(projectSchedule.getManagerName());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            if (i == 0) {
                                managerName = jsonArray.getString(i);
                            } else {
                                managerName = jsonArray.getString(i) + SmartminingConstant.COMMA + managerName;
                            }
                        }
                    }
                    reply.setManager(managerName);
                    //挖机工作平台
                    reply.setExctPlace(projectSchedule.getPlaceName());
                    String slagSiteIds = projectSchedule.getSlagSiteId();
                    JSONArray slagSiteIdArray = JSONArray.parseArray(slagSiteIds);
                    List<ProjectSlagSite> projectSlagSiteList = projectSlagSiteServiceI.getAllByProjectId(projectId);
                    //生成渣场索引
                    Map<Long, Integer> slagSiteMapIndex = new HashMap<>();
                    for (int i = 0; i < projectSlagSiteList.size(); i++) {
                        slagSiteMapIndex.put(projectSlagSiteList.get(i).getId(), i);
                    }
                    if (slagSiteIdArray != null) {
                        String slagSiteID = "";
                        String slagSiteName = "";       //渣场名称
                        String slagSitePos = "";
                        for (int i = 0; i < slagSiteIdArray.size(); i++) {
                            Long slagSiteId = slagSiteIdArray.getLong(i);
                            Integer index = slagSiteMapIndex.get(slagSiteId);
                            ProjectSlagSite projectSlagSite = index != null ? projectSlagSiteList.get(index) : null;
                            if (projectSlagSite != null) {
                                if (i == 0) {
                                    slagSiteID = slagSiteId.toString();
                                    slagSiteName = projectSlagSite.getName();
                                    slagSitePos = projectSlagSite.getLongitude() + "-" + projectSlagSite.getLatitude();
                                } else {
                                    slagSiteID = slagSiteId + SmartminingConstant.COMMA + slagSiteID;
                                    slagSiteName = projectSlagSite.getName() + SmartminingConstant.COMMA + slagSiteName;
                                    slagSitePos = projectSlagSite.getLongitude() + "-" + projectSlagSite.getLatitude() + SmartminingConstant.COMMA + slagSitePos;
                                }
                            }
                        }
                        reply.setSlagSiteID(slagSiteID);
                        reply.setSlagSiteName(slagSiteName);
                        reply.setSlagSitePos(slagSitePos);
                    }
                }
                int i = 0;
                String exctStatus = "";     //挖机状态
                String exctPos = "";        //挖机坐标
                for (ScheduleMachine scheduleMachine : scheduleMachineList) {
                    if (scheduleCarList.get(0).getCarId() != null && StringUtils.isNotEmpty(scheduleCarList.get(0).getCarCode())) {
                        Integer machineIndex = machineMapIndex.get(scheduleMachine.getMachineCode());
                        Integer deviceIndex = deviceMapIndex.get(scheduleMachine.getMachineCode());
                        if (i == 0) {
                            machineId = scheduleMachine.getMachineId().toString();
                            machineCode = scheduleMachine.getMachineCode();
                            exctPriceID = scheduleMachine.getPricingType().getValue().toString();
                            exctPrice = scheduleMachine.getPricingType().getName();
                            distance = scheduleMachine.getDistance().toString();
                            exctLoaderID = scheduleMachine.getMaterialId().toString();
                            exctLoader = scheduleMachine.getMaterialName();
                            if (machineIndex != null) {
                                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineList.get(machineIndex);
                                exctStatus = projectDiggingMachine.getStatus().toString();
                            } else {
                                exctStatus = "0";
                            }
                            if (deviceIndex != null) {
                                ProjectDevice projectDevice = projectDeviceList.get(deviceIndex);
                                exctPos = projectDevice.getLongitude() + "-" + projectDevice.getLatitude();
                            } else {
                                exctPos = "0-0";
                            }
                        } else {
                            machineId = scheduleMachine.getMachineId() + SmartminingConstant.COMMA + machineId;
                            machineCode = scheduleMachine.getMachineCode() + SmartminingConstant.COMMA + machineCode;
                            exctPriceID = scheduleMachine.getPricingType().getValue() + SmartminingConstant.COMMA + exctPriceID;
                            exctPrice = scheduleMachine.getPricingType().getName() + SmartminingConstant.COMMA + exctPrice;
                            distance = scheduleMachine.getDistance() + SmartminingConstant.COMMA + distance;
                            exctLoaderID = scheduleMachine.getMaterialId() + SmartminingConstant.COMMA + exctLoaderID;
                            exctLoader = scheduleMachine.getMaterialName() + SmartminingConstant.COMMA + exctLoader;
                            if (machineIndex != null) {
                                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineList.get(machineIndex);
                                exctStatus = projectDiggingMachine.getStatus().toString() + SmartminingConstant.COMMA + exctStatus;
                            } else {
                                exctStatus = "0" + SmartminingConstant.COMMA + exctStatus;
                            }
                            if (deviceIndex != null) {
                                ProjectDevice projectDevice = projectDeviceList.get(deviceIndex);
                                exctPos = projectDevice.getLongitude() + "-" + projectDevice.getLatitude() + SmartminingConstant.COMMA + exctPos;
                            } else {
                                exctPos = "0-0" + SmartminingConstant.COMMA + exctPos;
                            }
                        }
                        i++;
                    }
                }
                reply.setExctStatus(exctStatus);
                reply.setExctPos(exctPos);
            }

            //-------
            Project p = projectServiceI.get(projectId);
            if (p != null && p.getDispatchMode() == ProjectDispatchMode.Auto) {
                List<ProjectScheduleDetail> ps = projectScheduleDetailServiceI.findByProjectCarIdAndProjectIdOrderByIdDesc(slagCarId, projectId);
                if (ps.size() > 0) {
                    machineId = "" + ps.get(0).getProjectDiggingMachineId();
                    machineCode = ps.get(0).getDiggingMachineCode();
                }
            }
            reply.setMessage("请求成功");
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
            reply.setMessage(e.getMsg());
        } catch (IOException e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
            reply.setMessage("后台异常");
        } catch (Exception e) {
            smartminingExceptionService.save(e, params, "渣车设备主动获取排班信息");
            e.printStackTrace();
            cmdStatus = 1L;
            reply.setMessage("后台异常");
        } finally {
            reply.setCmdStatus(cmdStatus);
            reply.setExctID(machineId);
            reply.setExctCode(machineCode);
            reply.setExctPriceID(exctPriceID);
            reply.setExctPrice(exctPrice);
            reply.setExctDist(distance);
            reply.setExctLoaderID(exctLoaderID);
            reply.setExctLoader(exctLoader);
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * 设备请求 获取对应渣车的挖机信息
     *
     * @param cmdInd
     * @param replytopic
     * @param pktID
     * @param projectId
     * @param carId
     * @throws JsonProcessingException
     */
    public void handleMessageGetExcavator(String cmdInd, String replytopic, Long pktID, Long projectId, Long
            carId, String deviceId, String params) throws JsonProcessingException {
        GetExcavatorReply reply = new GetExcavatorReply();
        reply.setCmdInd(cmdInd);
        reply.setPktID(pktID);
        reply.setCarID(carId);
        reply.setProjectID(projectId);
        String excavatorID = "";
        String excavatorCode = "";
        Long cmdStatus = 0L;
        try {
            System.out.println("开始获取对应渣车的挖机信息");
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
            if (scheduleCarList != null) {
                int i = 0;
                for (ScheduleMachine machine : scheduleMachineList) {
                    //自动绑定挖机对应的卡
                    handleMessageCard(deviceId, projectId, machine.getMachineId());
                    if (i == 0) {
                        excavatorID = machine.getMachineId().toString();
                    } else {
                        excavatorID = machine.getMachineId() + SmartminingConstant.COMMA + excavatorID;
                    }
                    if (i == 0) {
                        excavatorCode = machine.getMachineCode();
                    } else {
                        excavatorCode = machine.getMachineCode() + SmartminingConstant.COMMA + excavatorCode;
                    }
                    i++;
                }
            }
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (IOException e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
            cmdStatus = 1L;
        } catch (Exception e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
            cmdStatus = 1L;
        } finally {
            reply.setCmdStatus(cmdStatus);
            reply.setExcavatorID(excavatorID);
            reply.setExcavatorNo(excavatorCode);
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    public void handleMessageOnOff(String cmdInd, String replytopic, Long pktID, Long projectId, Long
            machineId, Integer status, String deviceId, String params, Long createId, String createName, Boolean orSave) {
        try {
            OnOff onOff = new OnOff();
            onOff.setCmdInd(cmdInd);
            onOff.setExcavatorID(machineId);
            onOff.setPktID(pktID);
            onOff.setProjectID(projectId);
            onOff.setStatus(status);
            String message = stringRedisTemplate.opsForValue().get("onOff" + deviceId);
            Long cmdStatus = 0L;
            if (StringUtils.isEmpty(message)) {
                stringRedisTemplate.opsForValue().set("onOff" + deviceId, "allReady", 60 * 2, TimeUnit.SECONDS);
                if (orSave) {
                    Map resultMap = saveWorkInfoByDigging(projectId, status, machineId, deviceId, createId, createName);
                    Boolean result = Boolean.valueOf(resultMap.get("request").toString());
                    String remark = resultMap.get("message").toString();
                    if (!result)
                        cmdStatus = 1L;
                    onOff.setMessage(remark);
                }
            } else {
                cmdStatus = 1L;
                onOff.setMessage("正在请求中，请勿重复请求");
            }
            onOff.setCmdStatus(cmdStatus);
            mqttSender.sendDeviceReply(replytopic, onOff);
        } catch (JsonProcessingException e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
        } catch (IOException e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
        } catch (Exception e) {
            smartminingExceptionService.save(e, params);
            e.printStackTrace();
        } finally {
            stringRedisTemplate.delete("onOff" + deviceId);
        }
    }

    /**
     * 设备上下机日志
     *
     * @param projectId
     * @param projectDeviceType
     * @param carId
     * @param carCode
     * @param status
     * @param createId
     * @param crateName
     * @return
     */
    public boolean saveWorkLog(Long projectId, ProjectDeviceType projectDeviceType, Long carId, String carCode, DeviceDoStatusEnum status, Long createId, String crateName, Boolean success, String remark) {
        try {
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date startTime = dateMap.get("start");
            Date dateIdentification = new Date();
            if (startTime.getTime() > date.getTime())
                startTime = DateUtils.getAddDate(startTime, -1);
            dateIdentification = DateUtils.createReportDateByMonth(startTime);
            Shift shift = workDateService.getShift(date, projectId);
            ProjectWorkTimeByDiggingLog log = new ProjectWorkTimeByDiggingLog();
            log.setProjectId(projectId);
            log.setCarId(carId);
            log.setCarCode(carCode);
            log.setDoStatus(status);
            log.setCreateId(createId);
            log.setCreateName(crateName);
            log.setDateIdentification(dateIdentification);
            log.setShift(shift);
            log.setProjectDeviceType(projectDeviceType);
            log.setRemark(remark);
            log.setSuccess(success);
            if (projectDeviceType.compareTo(ProjectDeviceType.SlagTruckDevice) == 0 || projectDeviceType.compareTo(ProjectDeviceType.DiggingMachineDevice) == 0) {
                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, carCode, projectDeviceType.getAlian());
                if (projectDevice != null) {
                    log.setDeviceStatus(projectDevice.getStatus());
                }
            } else {
                CarType carType = CarType.Unknow;
                if (projectDeviceType.compareTo(ProjectDeviceType.ForkliftDevice) == 0)
                    carType = CarType.Forklift;
                else if (projectDeviceType.compareTo(ProjectDeviceType.RollerDevice) == 0)
                    carType = CarType.Roller;
                else if (projectDeviceType.compareTo(ProjectDeviceType.GunHammerDevice) == 0)
                    carType = CarType.GunHammer;
                else if (projectDeviceType.compareTo(ProjectDeviceType.SingleHookDevice) == 0)
                    carType = CarType.SingleHook;
                else if (projectDeviceType.compareTo(ProjectDeviceType.WateringCarDevice) == 0)
                    carType = CarType.WateringCar;
                else if (projectDeviceType.compareTo(ProjectDeviceType.ScraperDevice) == 0)
                    carType = CarType.Scraper;
                else if (projectDeviceType.compareTo(ProjectDeviceType.PunchDevice) == 0)
                    carType = CarType.Punch;
                ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.getAllByProjectIdAndCodeAndCarType(projectId, carCode, carType);
                if (projectOtherDevice != null) {
                    log.setDeviceStatus(projectOtherDevice.getDeviceStatus());
                }
            }
            projectWorkTimeByDiggingLogServiceI.save(log);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleMessageOtherDeviceOnOff(String cmdInd, String replyTopic, Long pktID, Long projectId, Long deviceId, Integer status, Integer type, Integer choose) throws IOException {
        Long cmdStatus = 0L;
        if (choose == 1) {
            boolean flag = saveWorkInfoByOtherDevice(projectId, status, deviceId, type);
            if (!flag)
                cmdStatus = 1L;
        }
        OtherDeviceOnOff otherDeviceOnOff = new OtherDeviceOnOff();
        otherDeviceOnOff.setCmdInd(cmdInd);
        otherDeviceOnOff.setCmdStatus(cmdStatus);
        otherDeviceOnOff.setOtherDeviceID(deviceId);
        otherDeviceOnOff.setPktID(pktID);
        otherDeviceOnOff.setProjectID(projectId);
        otherDeviceOnOff.setStatus(status);
        mqttSender.sendDeviceReply(replyTopic, otherDeviceOnOff);
    }

    /**
     * 保存挖机的作业信息到数据库
     *
     * @param projectId
     * @param status
     * @param machineId
     */
    @Transactional
    public Map saveWorkInfoByDigging(Long projectId, Integer status, Long machineId, String deviceId, Long createId, String createName) throws IOException {
        String remark = "请求成功";
        Boolean success = true;
        ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
        Map resultMap = new HashMap();
        if (machine == null)
            throw new SmartminingProjectException("该挖机不存在");
        try {
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date start = dateMap.get("start");
            if (date.getTime() < start.getTime())
                start = DateUtils.subtractionOneDay(date);
            ProjectWorkTimeByDigging digging = null;
            String message = stringRedisTemplate.opsForValue().get(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            if (StringUtils.isEmpty(message)) {
                stringRedisTemplate.opsForValue().set(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD, "allReady", 10, TimeUnit.SECONDS);
                ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, machine.getCode());
                if (status == 1) {
                    if (scheduleMachine == null)
                        throw new SmartminingProjectException("未排班");
                    ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
                    if (projectSchedule == null)
                        throw new SmartminingProjectException("未排班");
                    List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdAdd(projectId, machineId);
                    digging = new ProjectWorkTimeByDigging();
                    if (diggingList != null && diggingList.size() > 0) {
                        digging = diggingList.get(0);
                        if (digging.getStatus().compareTo(DiggingMachineStatus.Working) == 0) {
                            remark = "该挖机以在上机中，未进行任何操作";
                            resultMap.put("request", success);
                            resultMap.put("message", remark);
                            return resultMap;
                        }
                    }
                    digging.setPlaceId(machine.getPlaceId());
                    digging.setPlaceName(machine.getPlaceName());
                    Date dateIdentification = DateUtils.createReportDateByMonth(start);
                    machine.setUid(deviceId);
                    machine.setStatus(DiggingMachineStatus.WoekRequest);
                    machine.setStartMode(StartEnum.REQUEST);
                    List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByCodeAndDeviceType(machine.getCode(), 2);
                    if (projectDeviceList == null || projectDeviceList.size() == 0)
                        throw new SmartminingProjectException("未绑定对应的终端");
                    ProjectDevice projectDevice = projectDeviceList.get(0);
                    projectDevice.setUid(deviceId);
                    digging.setPricingTypeEnums(scheduleMachine.getPricingType());
                    digging.setDataId(scheduleMachine.getMaterialId());
                    digging.setDataName(scheduleMachine.getMaterialName());
                    digging.setPlaceId(projectSchedule.getPlaceId());
                    digging.setPlaceName(projectSchedule.getPlaceName());
                    digging.setSlagSiteId(projectSchedule.getSlagSiteId());
                    digging.setSlagSiteName(projectSchedule.getSlagSiteName());
                    digging.setMaterialId(machineId);
                    digging.setMaterialCode(machine.getCode());
                    digging.setProjectId(projectId);
                    digging.setMaterialInfo(machine.getBrandName() + machine.getModelName());
                    digging.setCreateTime(new Date());
                    digging.setStatus(DiggingMachineStatus.WoekRequest);
                    digging.setStartMode(StartEnum.REQUEST);
                    digging.setDateIdentification(dateIdentification);
                    scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.WoekRequest);
                    projectDeviceServiceI.save(projectDevice);
                } else {
                    List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machineId);
                    if (diggingList != null || diggingList.size() > 0) {
                        if (machine.getStatus().compareTo(DiggingMachineStatus.Stop) == 0) {
                            remark = "该挖机以在上机中，未进行任何操作";
                            resultMap.put("request", success);
                            resultMap.put("message", remark);
                            return resultMap;
                        }
                        digging = diggingList.get(0);
                        digging.setStatus(DiggingMachineStatus.StopRequest);
                        machine.setStatus(DiggingMachineStatus.StopRequest);
                        digging.setStopMode(StopEnum.EXAMINE);
                        machine.setStopMode(StopEnum.EXAMINE);
                        scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.StopRequest);
                    } else {
                        remark = "未查询到该挖机的上机记录";
                    }
                }
                projectDiggingMachineServiceI.save(machine);
                if (digging != null)
                    projectWorkTimeByDiggingServiceI.save(digging);
                scheduleMachineServiceI.save(scheduleMachine);
                resultMap.put("request", success);
                resultMap.put("message", remark);
                return resultMap;
            } else {
                throw new SmartminingProjectException("请勿重复请求");
            }
        } catch (SmartminingProjectException e) {
            remark = e.getMsg();
            success = false;
            resultMap.put("request", success);
            resultMap.put("message", remark);
            return resultMap;
        } catch (Exception e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            resultMap.put("request", success);
            resultMap.put("message", "后台异常");
            e.printStackTrace();
            smartminingExceptionService.save(e);
            return resultMap;
        } finally {
            stringRedisTemplate.delete(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
            if (status == 1)
                doStatus = DeviceDoStatusEnum.StartRequest;
            else
                doStatus = DeviceDoStatusEnum.StopRequest;
            saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, machine.getCode(), doStatus, createId, createName, success, remark);
        }
    }

    @Transactional
    public boolean examWorkInfoByDigging(Long projectId, Integer status, Long machineId, Long createId, String createName, Boolean orSave) throws IOException {
        String remark = "请求成功";
        Boolean success = true;
        ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
        if (machine == null)
            throw new SmartminingProjectException("该挖机不存在" + machineId);
        try {
            ProjectWorkTimeByDigging digging = null;
            List<ProjectWorkTimeByDigging> diggingList = null;
            Date date = new Date();
            String message = stringRedisTemplate.opsForValue().get(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            if (StringUtils.isEmpty(message)) {
                stringRedisTemplate.opsForValue().set(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD, "allReady", 10, TimeUnit.SECONDS);
                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, new Date());
                Date start = dateMap.get("start");
                Date end = dateMap.get("earlyEnd");
                Date nightStart = dateMap.get("nightStart");
                if (date.getTime() < start.getTime()) {
                    start = DateUtils.subtractionOneDay(start);
                    end = DateUtils.subtractionOneDay(end);
                    nightStart = DateUtils.subtractionOneDay(nightStart);
                }
                ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, machine.getCode());
                if (status == 1) {
                    if (scheduleMachine == null)
                        throw new SmartminingProjectException("挖机排班不存在" + machine.getCode());
                    ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
                    if (projectSchedule == null)
                        throw new SmartminingProjectException("挖机对应排班不存在" + machine.getCode());
                    diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialId(projectId, machineId);
                    if (diggingList == null || diggingList.size() < 1)
                        throw new SmartminingProjectException("未查询到该挖机的开机申请" + machine.getCode());
                    digging = diggingList.get(0);
                    digging.setProjectId(projectId);
                    digging.setMaterialId(machine.getId());
                    digging.setMaterialCode(machine.getCode());
                    digging.setMaterialInfo(machine.getBrandName() + machine.getModelName());
                    digging.setStatus(DiggingMachineStatus.Working);
                    digging.setStartTime(date);
                    digging.setCreateTime(date);
                    digging.setStopStatus(DiggingMachineStopStatus.Normal);
                    machine.setStartWorkTime(date);
                    machine.setStopStatus(DiggingMachineStopStatus.Normal);
                    machine.setEndWorkTime(new Date(0));
                    machine.setStatus(DiggingMachineStatus.Working);
                    if (digging.getStartTime().getTime() >= start.getTime() && digging.getStartTime().getTime() <= end.getTime()) {
                        digging.setShift(ShiftsEnums.DAYSHIFT);
                    } else {
                        digging.setShift(ShiftsEnums.BLACKSHIFT);
                    }
                    List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, true);
                    if (scheduleMachineList.size() < 1)
                        throw new SmartminingProjectException("没有找到挖机对应的排班信息" + machine.getCode());
                    digging.setPricingTypeEnums(scheduleMachineList.get(0).getPricingType());
                    digging.setDataId(scheduleMachineList.get(0).getMaterialId());
                    digging.setDataName(scheduleMachineList.get(0).getMaterialName());
                    digging.setSlagSiteId(projectSchedule.getSlagSiteId());
                    digging.setSlagSiteName(projectSchedule.getSlagSiteName());
                    scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Working);
                    scheduleMachine.setFault(false);
                    Date dateIdentification = DateUtils.createReportDateByMonth(start);
                    digging.setDateIdentification(dateIdentification);
                } else {
                    diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machineId);
                    if (diggingList == null || diggingList.size() < 1) {
                        machine.setStatus(DiggingMachineStatus.Stop);
                        projectDiggingMachineServiceI.save(machine);
                        throw new SmartminingProjectException("该挖机上班信息为空！" + machine.getCode());
                    }
                    digging = diggingList.get(0);
                    if (digging.getStartTime() == null) {
                        machine.setStatus(DiggingMachineStatus.Stop);
                        projectDiggingMachineServiceI.save(machine);
                        throw new SmartminingProjectException("该挖机上班信息为空！" + machine.getCode());
                    }
                    digging.setEndTime(date);
                    digging.setStatus(DiggingMachineStatus.Stop);
                    Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                    digging.setWorkTime(workTime);
                    machine.setEndWorkTime(date);
                    machine.setStatus(DiggingMachineStatus.Stop);
                    scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Stop);
                }
                ProjectAppStatisticsByMachine appMachine = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, digging.getShift().getAlias(), nightStart, digging.getMaterialCode());
                if (appMachine == null)
                    appMachine = new ProjectAppStatisticsByMachine();
                appMachine.setProjectId(projectId);
                appMachine.setWorkTime(appMachine.getWorkTime() + digging.getWorkTime());
                appMachine.setShifts(digging.getShift());
                appMachine.setMachineCode(digging.getMaterialCode());
                appMachine.setCreateDate(nightStart);
                projectAppStatisticsByMachineServiceI.save(appMachine);
                projectDiggingMachineServiceI.save(machine);
                projectWorkTimeByDiggingServiceI.save(digging);
                scheduleMachineServiceI.save(scheduleMachine);
                stringRedisTemplate.delete(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
                return true;
            } else {
                throw new SmartminingProjectException("redis不为空");
            }
        } catch (SmartminingProjectException e) {
            success = false;
            remark = e.getMsg();
            return false;
        } catch (Exception e) {
            success = false;
            remark = JSON.toJSONString(e.getStackTrace());
            e.printStackTrace();
            smartminingExceptionService.save(e);
            return false;
        } finally {
            stringRedisTemplate.delete(machine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            if (orSave) {
                DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
                if (status == 1)
                    doStatus = DeviceDoStatusEnum.StartExamine;
                else
                    doStatus = DeviceDoStatusEnum.StopExamine;
                saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, machine.getCode(), doStatus, createId, createName, success, remark);
            }
        }
    }

    /**
     * 其它设备申请上机
     *
     * @param projectId
     * @param status    1-开机  2- 下机
     * @param deviceId
     * @param type      车辆类型 4-铲车 5-压路机 6-炮锤 7-单勾 8-洒水车 9-刮平机
     * @return
     * @throws IOException
     */
    @Transactional
    public boolean saveWorkInfoByOtherDevice(Long projectId, Integer status, Long deviceId, Integer type) throws IOException {
        Date date = new Date();
        ProjectOtherDeviceWorkInfo workInfo = null;
        ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.get(deviceId);
        CarType carType = convertCarType(type);
        DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
        if (status == 1)
            doStatus = DeviceDoStatusEnum.StartRequest;
        else
            doStatus = DeviceDoStatusEnum.StopRequest;
        ProjectDeviceType deviceType = ProjectDeviceType.Unknown;
        if (carType.compareTo(CarType.Forklift) == 0)
            deviceType = ProjectDeviceType.ForkliftDevice;
        else if (carType.compareTo(CarType.Roller) == 0)
            deviceType = ProjectDeviceType.RollerDevice;
        else if (carType.compareTo(CarType.GunHammer) == 0)
            deviceType = ProjectDeviceType.GunHammerDevice;
        else if (carType.compareTo(CarType.SingleHook) == 0)
            deviceType = ProjectDeviceType.SingleHookDevice;
        else if (carType.compareTo(CarType.WateringCar) == 0)
            deviceType = ProjectDeviceType.WateringCarDevice;
        else if (carType.compareTo(CarType.Scraper) == 0)
            deviceType = ProjectDeviceType.ScraperDevice;
        else if (carType.compareTo(CarType.Punch) == 0)
            deviceType = ProjectDeviceType.PunchDevice;
        if (otherDevice == null) {
            saveWorkLog(projectId, deviceType, deviceId, otherDevice.getCode(), doStatus, -1L, "终端主动请求", false, "其它设备ID" + deviceId + "不存在");
            throw new SmartminingProjectException("其它设备ID" + deviceId + "不存在");
        }
        if (status == 1) {
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date startTime = dateMap.get("start");
            if (date.getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            Date dateIdentification = DateUtils.createReportDateByMonth(startTime);
            workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, otherDevice.getCode(), carType.getValue(), ProjectOtherDeviceStatusEnum.WoekRequest.getAlias());
            if (workInfo == null)
                workInfo = new ProjectOtherDeviceWorkInfo();
            workInfo.setProjectId(projectId);
            workInfo.setDeviceId(deviceId);
            workInfo.setCode(otherDevice.getCode());
            workInfo.setDateIdentification(dateIdentification);
            workInfo.setStatus(ProjectOtherDeviceStatusEnum.WoekRequest);
            workInfo.setCreateTime(date);
            workInfo.setCarType(carType);
            workInfo.setRemark("终端手动申请上机");
            otherDevice.setStatus(ProjectOtherDeviceStatusEnum.WoekRequest);
        } else {
            workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, otherDevice.getCode(), carType.getValue(), ProjectOtherDeviceStatusEnum.Working.getAlias());
            if (workInfo == null)
                workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, otherDevice.getCode(), carType.getValue(), ProjectOtherDeviceStatusEnum.StopRequest.getAlias());
            if (workInfo == null) {
                saveWorkLog(projectId, deviceType, deviceId, otherDevice.getCode(), doStatus, -1L, "终端主动请求", false, "其它设备编号" + otherDevice.getCode() + "未开机成功， 无法下机");
                throw new SmartminingProjectException("其它设备编号" + otherDevice.getCode() + "未开机成功， 无法下机");
            }
            workInfo.setStatus(ProjectOtherDeviceStatusEnum.StopRequest);
            otherDevice.setStatus(ProjectOtherDeviceStatusEnum.StopRequest);
        }
        projectOtherDeviceWorkInfoServiceI.save(workInfo);
        projectOtherDeviceServiceI.save(otherDevice);
        saveWorkLog(projectId, deviceType, deviceId, otherDevice.getCode(), doStatus, -1L, "终端主动请求", true, "请求成功");
        return true;
    }

    /**
     * 其他设备终端与云端同步设备绑定的终端信息
     *
     * @param projectId
     * @param otherDeviceId
     * @param uid
     * @param type
     * @throws IOException
     */
    public void synchronizationOtherDevice(Long projectId, Long otherDeviceId, String uid, Integer type) throws IOException {
        CarType carType = convertCarType(type);
        ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.get(otherDeviceId);
        ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.getAllByProjectIdAndCodeAndCarType(projectId, projectOtherDevice.getCode(), carType);
        if (otherDevice != null) {
            otherDevice.setUid(uid);
            ProjectOtherDevice oldOtherDevice = projectOtherDeviceServiceI.getAllByUid(uid);
            if (oldOtherDevice != null)
                oldOtherDevice.setUid("");
            ProjectDevice device = projectDeviceServiceI.getByUid(uid);
            if (device != null)
                device.setCode(projectOtherDevice.getCode());
            ProjectDevice oldDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, projectOtherDevice.getCode(), type);
            if (oldDevice != null)
                oldDevice.setCode("");
            projectOtherDeviceServiceI.save(oldOtherDevice);
            projectOtherDeviceServiceI.save(otherDevice);
            projectDeviceServiceI.save(oldDevice);
            projectDeviceServiceI.save(device);
        }
    }

    public void handleMessagePost(String payload, String deviceId, ProjectDeviceType projectDeviceType) throws JsonProcessingException, SmartminingProjectException {
        JSONObject object = JSONObject.parseObject(payload);
        Long projectId = object.get("projectID") != null ? object.getLong("projectID") : 0L;
        ProjectDevice projectDevice = projectDeviceServiceI.getByUid(deviceId);
        if (projectDevice == null)
            throw new SmartminingProjectException("终端不存在" + deviceId);
        Long carId = object.getLong("slagcarID") != null ? object.getLong("slagcarID") : 0L;
        String carCode = object.getString("carCode") != null ? object.getString("carCode") : "";
        Integer alarmInfos = object.get("alarmInfos") != null ? object.getInteger("alarmInfos") : 0;
        Integer devStatus = object.get("devStatus") != null ? object.getInteger("devStatus") : 0;
        Integer faultInfos = object.get("faultInfos") != null ? object.getInteger("faultInfos") : 0;
        String hardVer = object.get("hardVer") != null ? object.getString("hardVer") : "";
        String softVer = object.get("softVer") != null ? object.getString("softVer") : "";
        String iccId = object.get("NetICCID") != null ? object.getString("NetICCID") : "";
        //通电时间
        String startTime = object.get("boot_dt") != null ? object.getString("boot_dt") : "";
        try {
            Date electrifyTime = DateUtils.stringFormatDate(startTime, SmartminingConstant.DATEFORMATTWO);
            if(electrifyTime == null)
                electrifyTime = DateUtils.stringFormatDate(startTime, SmartminingConstant.DATEFORMAT);
            ProjectDeviceElectrifyLog log = projectDeviceElectrifyLogServiceI.getAllByProjectIdAndUidElectrifyTime(projectId, carCode, electrifyTime, projectDeviceType.getAlian());
            if (log == null) {
                log = new ProjectDeviceElectrifyLog();
                log.setProjectId(projectId);
                log.setCarCode(carCode);
                log.setCarId(carId);
                log.setDeviceType(projectDeviceType);
                log.setElectrifyTime(electrifyTime);
                log.setUid(deviceId);
                projectDeviceElectrifyLogServiceI.save(log);
            }
            ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.getByProjectIdAndUid(projectDevice.getProjectId(), deviceId);
            if (diggingMachine != null)
                projectDevice.setCode(diggingMachine.getCode());
            projectDevice.setProjectId(projectDevice.getProjectId());
            projectDevice.setUid(deviceId);
            projectDevice.setIccid(iccId);
            projectDevice.setSoftwareVersion(softVer);
            projectDevice.setHardwareVersion(hardVer);
            projectDevice.setDeviceType(projectDeviceType);
            projectDevice.setStatus(ProjectDeviceStatus.OnLine);
            projectDevice.setFaultInfos(FaultInfoEnum.Unknow);
            projectDevice.setAlarmInfos(AlarmInfoEnum.Unknow);
            projectDeviceServiceI.save(projectDevice);
        } catch (IOException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    //判断终端在线还是离线
    @Transactional
    public ProjectDevice handleDeviceStatus(String deviceId) {
        try {
            String message = getValueOps().get(deviceId);
            ProjectDevice projectDevice = projectDeviceServiceI.getByUid(deviceId);
            if (projectDevice == null) {
                //其它终端
                ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.getAllByUid(deviceId);
                if (otherDevice != null) {
                    if(otherDevice.getDeviceStatus().compareTo(ProjectDeviceStatus.OnLine) != 0) {
                        otherDevice.setDeviceStatus(ProjectDeviceStatus.OnLine);
                        otherDevice = projectOtherDeviceServiceI.save(otherDevice);
                    }
                    //存入缓存 设置过期时间为2分钟
                    getValueOps().set(deviceId, new ObjectMapper().writeValueAsString(otherDevice), GlobalSet.mqttStatusTimeOut, TimeUnit.MILLISECONDS);
                }else
                    throw new SmartminingProjectException("终端不存在，UID：" + deviceId);
            }else{
                if(projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) != 0) {
                    projectDevice.setStatus(ProjectDeviceStatus.OnLine);
                    projectDevice = projectDeviceServiceI.save(projectDevice);
                }
                //存入缓存 设置过期时间为2分钟
                getValueOps().set(deviceId, new ObjectMapper().writeValueAsString(projectDevice), GlobalSet.mqttStatusTimeOut, TimeUnit.MILLISECONDS);
            }
            if(StringUtils.isEmpty(message)){
                ProjectDeviceStatusLog projectDeviceStatusLog = new ProjectDeviceStatusLog();
                projectDeviceStatusLog.setOnlineTime(new Date());
                projectDeviceStatusLog.setProjectDeviceType(projectDevice.getDeviceType());
                projectDeviceStatusLog.setUid(deviceId);
                projectDeviceStatusLogServiceI.save(projectDeviceStatusLog);
            }
            return projectDevice;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            smartminingExceptionService.save(e);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            smartminingExceptionService.save(e);
            return null;
        }
    }

    /**
     * 终端请求日志
     * todo 暂未使用
     *
     * @param payload
     * @param projectId
     * @param deviceId
     * @return
     */
    public ProjectSystemMqttLog saveLog(String payload, Long projectId, String deviceId, ProjectMqttEnum projectMqttEnum) throws IOException {
        ProjectDevice projectDevice = projectDeviceServiceI.getByUid(deviceId);
        ProjectSystemMqttLog log = new ProjectSystemMqttLog();
        log.setProjectId(projectId);
        log.setRequestParams(payload);
        log.setUid(deviceId);
        log.setProjectMqttEnum(projectMqttEnum);
        try {
            if (projectDevice != null) {
                log.setProjectDevice(projectDevice.getDeviceType());
                log.setValid(true);
                log.setRemark("初步请求成功");
                if (projectDevice.getDeviceType().compareTo(ProjectDeviceType.SlagFieldDevice) == 0) {
                    ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.getByProjectIdAndDeviceUid(projectId, deviceId);
                    if (projectSlagSite != null) {
                        log.setProjectCode("渣场ID：" + projectSlagSite.getId());
                    }
                }
            } else {
                log.setValid(false);
                log.setRemark("终端不存在，uid：" + deviceId);
            }
        } catch (Exception e) {

        }
        projectSystemMqttLogServiceI.save(log);
        return log;
    }

    /**
     * 校验挖机卡信息
     *
     * @param deviceId
     */
    public void handleMessageCard(String deviceId, Long projectId, Long machineId) throws IOException, SmartminingProjectException {
        ProjectDevice device = projectDeviceServiceI.getByUid(deviceId);
        if (device == null)
            throw new SmartminingProjectException("终端不存在" + deviceId);
        ProjectDiggingMachine oldMachine = projectDiggingMachineServiceI.getAllByUid(deviceId);
        if (oldMachine != null) {
            oldMachine.setUid("");
            projectDiggingMachineServiceI.save(oldMachine);
        }
        ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
        if (machine == null)
            throw new SmartminingProjectException("挖机不存在" + machineId);
        //将现在的终端对象的挖机编号赋值成现在的
        device.setCode(machine.getCode());
        machine.setUid(deviceId);
        //之前的挖机终端对象
        List<ProjectDevice> oldDevice = projectDeviceServiceI.getAllByCodeAndDeviceType(machine.getCode(), 2);
        if (oldDevice != null && oldDevice.size() > 0) {
            for (ProjectDevice projectDevice : oldDevice) {
                projectDevice.setCode("");
                projectDeviceServiceI.save(projectDevice);
            }
        }
        projectDeviceServiceI.save(device);
        projectDiggingMachineServiceI.save(machine);
    }

    /**
     * 校验渣车卡信息
     *
     * @param deviceId
     */
    public void handleMessageCardByCar(String deviceId, Long projectId, Long carId) throws IOException, SmartminingProjectException {
        ProjectDevice projectDevice = projectDeviceServiceI.getByUid(deviceId);
        if (projectDevice == null)
            throw new SmartminingProjectException("终端不存在" + deviceId);
        ProjectCar projectCar = projectCarServiceI.get(carId);
        if (projectCar == null)
            throw new SmartminingProjectException("渣车不存在" + carId);
        List<ProjectDevice> oldProjectDevice = projectDeviceServiceI.getAllByCodeAndDeviceType(projectCar.getCode(), 5);
        //if (!projectDevice.getCode().equals(projectCar.getCode())) {
        projectDevice.setCode(projectCar.getCode());
        if (oldProjectDevice != null && oldProjectDevice.size() > 0) {
            for (ProjectDevice pd : oldProjectDevice) {
                pd.setCode("");
                projectDeviceServiceI.save(pd);
            }
        }
        projectCar.setUid(deviceId);
        projectCarServiceI.save(projectCar);
        projectDeviceServiceI.save(projectDevice);
        //}
    }

    public void handleStatusByMachine(String cmdInd, String replytopic, Long pktID, Long projectId, Long machineId) {
        OnOff onOff = new OnOff();
        onOff.setCmdInd(cmdInd);
        onOff.setProjectID(projectId);
        onOff.setPktID(pktID);
        onOff.setExcavatorID(machineId);
        Long cmsStatus = 0L;
        Integer status = 0;
        try {
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
            if (machine.getStatus().compareTo(DiggingMachineStatus.Working) == 0 || machine.getStatus().compareTo(DiggingMachineStatus.StopRequest) == 0) {
                status = 1;
            }
        } catch (IOException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            cmsStatus = 1L;
        } catch (NullPointerException e) {
            smartminingExceptionService.save(e);
            System.out.println("可能是挖机对象不存在");
            cmsStatus = 1L;
        } finally {
            onOff.setStatus(status);
            onOff.setCmdStatus(cmsStatus);
            try {
                mqttSender.sendDeviceReply(replytopic, onOff);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 挖机主动查询当前班次车数及其它信息
     *
     * @param cmdInd
     * @param replytopic
     * @param projectId
     * @param machineId
     * @param pktId
     */
    public void handleCurrentDataByMachine(String cmdInd, String replytopic, Long projectId, Long machineId, Long pktId, String params) {
        CurrentDataByDigging dataByDigging = new CurrentDataByDigging();
        try {
            //获取当前时间
            Date date = new Date();
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, true);
            ProjectSchedule projectSchedule = null;
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ScheduleMachine scheduleMachine = scheduleMachineList.get(0);
                projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
            }
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            if (date.getTime() < earlyStartTime.getTime()) {
                date = DateUtils.subtractionOneDay(date);
                earlyStartTime = DateUtils.subtractionOneDay(earlyStartTime);
                earlyEndTime = DateUtils.subtractionOneDay(earlyEndTime);
            }
            Shift shift = Shift.Unknown;
            if (date.getTime() >= earlyStartTime.getTime() && date.getTime() <= earlyEndTime.getTime())
                shift = Shift.Early;
            else
                shift = Shift.Night;
            ProjectCarCount carCount = projectCarCountServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, machine.getCode(), date, shift.getAlias(), CarType.DiggingMachine.getValue());
            dataByDigging.setCmdInd(cmdInd);
            dataByDigging.setPktID(pktId);
            dataByDigging.setProjectID(projectId);
            dataByDigging.setExcavatorID(machineId);
            if (projectSchedule != null) {
                dataByDigging.setPlaceId(projectSchedule.getPlaceId());
                dataByDigging.setPlaceName(projectSchedule.getPlaceName());
            }
            if (shift.compareTo(Shift.Early) == 0)
                dataByDigging.setShift("早班");
            else
                dataByDigging.setShift("晚班");
            JSONArray jsonArray = new JSONArray();
            if (carCount != null) {
                jsonArray = JSONArray.parseArray(carCount.getDetailJson());
                dataByDigging.setNumTol(carCount.getTotalCount());
                dataByDigging.setWorkTime(carCount.getWorkTime() / 60);
            }
            String jsonDetail = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                String text = jsonArray.get(i).toString();
                Map detailMap = JSON.parseObject(text, new TypeReference<Map>() {
                });
                String detailMaterialName = detailMap.get("materialName").toString();
                Long materialCount = Long.parseLong(detailMap.get("count").toString());
                if (jsonArray.size() > 1 && i != jsonArray.size() - 1)
                    jsonDetail = detailMaterialName + "-" + materialCount;
                else
                    jsonDetail = jsonDetail + SmartminingConstant.COMMA + detailMaterialName + "-" + materialCount;
            }
            dataByDigging.setNumMat(jsonDetail);
            dataByDigging.setCmdStatus(0L);
        } catch (Exception e) {
            smartminingExceptionService.save(e, params, "挖机主动查询当前班次车数及其它信息");
            e.printStackTrace();
            dataByDigging.setCmdStatus(1L);
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, dataByDigging);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 渣车主动查询当前班次车数及其它信息
     *
     * @param cmdInd
     * @param replytopic
     * @param projectId
     * @param carId
     * @param pktId
     * @param params
     */
    public void handleCurrentDataByCar(String cmdInd, String replytopic, Long projectId, Long carId, Long pktId, String params) {
        CurrentDataByCar dataByCar = new CurrentDataByCar();
        dataByCar.setCmdInd(cmdInd);
        dataByCar.setPktID(pktId);
        dataByCar.setProjectID(projectId);
        dataByCar.setSlagcarID(carId);
        try {
            //获取当前时间
            Date date = new Date();
            ProjectCar car = projectCarServiceI.get(carId);
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
            if (scheduleCarList == null || scheduleCarList.size() < 1)
                throw new SmartminingProjectException("该渣车暂未排班");
            ScheduleCar scheduleCar = scheduleCarList.get(0);
            ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.getGroupCode());
            if (projectSchedule == null)
                throw new SmartminingProjectException("该渣车对应的分组不存在");
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            if (date.getTime() < earlyStartTime.getTime()) {
                date = DateUtils.subtractionOneDay(date);
                earlyStartTime = DateUtils.subtractionOneDay(earlyStartTime);
                earlyEndTime = DateUtils.subtractionOneDay(earlyEndTime);
            }
            Shift shift = Shift.Unknown;
            if (date.getTime() >= earlyStartTime.getTime() && date.getTime() <= earlyEndTime.getTime())
                shift = Shift.Early;
            else
                shift = Shift.Night;
            List<Map> workInfoList = projectCarWorkInfoServiceI.getTotalCountByProjectIdAndDateIdentificationAndShiftAndStatusAndCarCode(projectId, date, shift.getAlias(), 7, car.getCode());
            String jsonDetail = "";
            Long totalCount = 0L;
            Long totalDistance = 0L;
            for (int i = 0; i < workInfoList.size(); i++) {
                String materialName = workInfoList.get(i).get("material_name").toString();
                Long materialCount = Long.parseLong(workInfoList.get(i).get("count").toString());
                Long distance = Long.parseLong(workInfoList.get(i).get("distance").toString());
                if (workInfoList.size() > 1 && i != workInfoList.size() - 1)
                    jsonDetail = materialName + "-" + materialCount + SmartminingConstant.COMMA;
                else
                    jsonDetail = jsonDetail + materialName + "-" + materialCount;
                totalCount = totalCount + materialCount;
                totalDistance = totalDistance + distance;
            }
            //无效车数
            Map unValidCountMap = projectMqttCardReportServiceI.getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, car.getCode(), date, shift.getAlias());
            if (unValidCountMap != null) {
                Long unValidCount = Long.parseLong(unValidCountMap.get("count").toString());
                dataByCar.setUnValidTol(unValidCount);
            }
            //ProjectCarCount carCount = projectCarCountServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, car.getCode(), date, shift.getAlias(), CarType.SlagCar.getValue());
            dataByCar.setPlaceId(projectSchedule.getPlaceId());
            dataByCar.setPlaceName(projectSchedule.getPlaceName());
            if (shift.compareTo(Shift.Early) == 0)
                dataByCar.setShift("早班");
            else
                dataByCar.setShift("晚班");
            dataByCar.setNumTol(totalCount);
            dataByCar.setMileage(new BigDecimal((float) totalDistance / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
            dataByCar.setNumMat(jsonDetail);
            dataByCar.setCmdStatus(0L);
            dataByCar.setMessage("请求成功");
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e, params, "渣车主动查询当前班次车数及其它信息");
            dataByCar.setCmdStatus(1L);
            dataByCar.setMessage(e.getMsg());
        } catch (IOException e) {
            smartminingExceptionService.save(e, params, "渣车主动查询当前班次车数及其它信息");
            e.printStackTrace();
            dataByCar.setCmdStatus(1L);
            dataByCar.setMessage("后台异常");
        } catch (Exception e) {
            e.printStackTrace();
            dataByCar.setCmdStatus(1L);
            dataByCar.setMessage("后台异常");
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, dataByCar);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 终端请求渣车状态改变
     *
     * @param cmdInd
     * @param replytopic
     * @param projectId
     * @param carCode
     * @param status
     */
    public void handleSlagCarStatus(String cmdInd, String replytopic, Long projectId, String carCode, Integer status, Long pktID, String deviceId, Long slagCarId) {
        handleAndroidAppCarStatus(cmdInd, replytopic, projectId, carCode, status, "device", pktID, deviceId, slagCarId, -1L, "终端操作");
    }

    //app监听
    public void handleAndroidApp(String cmdInd, String replytopic, Long pktID, Long projectId, String json) throws JsonProcessingException {
        ScheduledReplyByApp app = new ScheduledReplyByApp();
        app.setCmdInd(cmdInd);
        app.setProjectID(projectId);
        app.setPktID(pktID);
        app.setCmdStatus(0L);
        app.setJson(json);
        mqttSender.sendDeviceReply(replytopic, app);
    }

    //挖机故障上报
    public void handleDiggingMachineFault(String cmdInd, Long pktID, Long projectId, Long machineId, String replyTopic, String deviceId, String method, DiggingMachineStopStatus status) {
        FaultReply reply = new FaultReply();
        reply.setCmdInd(cmdInd);
        reply.setExcavatorID(machineId);
        reply.setPktID(pktID);
        reply.setProjectID(projectId);
        String remark = "";
        String machineCode = "";
        Boolean success = true;
        try {
            List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machineId);
            Date date = new Date();
            if (diggingList != null && diggingList.size() > 0) {
                ProjectWorkTimeByDigging digging = diggingList.get(0);
                Long workTime = DateUtils.calculationHour(digging.getStartTime(), date);
                digging.setWorkTime(workTime);
                digging.setEndTime(date);
                digging.setStatus(DiggingMachineStatus.Stop);
                digging.setStopStatus(status);
                digging.setStopMode(StopEnum.FORCE);
                projectWorkTimeByDiggingServiceI.save(digging);
                String replytopic = "smartmining/excavator/cloud/" + deviceId + "/" + method;
                OnOff onOff = new OnOff();
                onOff.setCmdInd("onOff");
                onOff.setCmdStatus(0L);
                onOff.setExcavatorID(machineId);
                onOff.setPktID(pktID);
                onOff.setProjectID(projectId);
                onOff.setStatus(0);
                try {
                    mqttSender.sendDeviceReply(replytopic, onOff);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
            machineCode = machine.getCode();
            machine.setStatus(DiggingMachineStatus.Stop);
            machine.setEndWorkTime(date);
            machine.setStopStatus(status);
            machine.setStopMode(StopEnum.FORCE);
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, true);
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ScheduleMachine scheduleMachine = scheduleMachineList.get(0);
                scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Stop);
                scheduleMachine.setFault(true);
                scheduleMachineServiceI.save(scheduleMachine);
            }
            projectDiggingMachineServiceI.save(machine);
            reply.setCmdStatus(0L);
        } catch (JsonProcessingException e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setCmdStatus(1L);
        } catch (IOException e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setCmdStatus(1L);
        } catch (Exception e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setCmdStatus(1L);
        } finally {
            try {
                mqttSender.sendDeviceReply(replyTopic, reply);
                saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, machineCode, DeviceDoStatusEnum.Fault, -1L, "终端主动请求", success, remark);
            } catch (JsonProcessingException e) {
                //smartminingExceptionService.save(e);
                e.printStackTrace();
            }
        }

    }

    //获取渣场定位
    /*public void handleSlagSitePosition(Long projectId, String replyTopic, String cmdInd, Long pktID, Long carId) {
        List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
        Long cmdStatus = 0L;
        if (scheduleCarList == null && scheduleCarList.size() == 0)
            cmdStatus = 1L;
        ScheduleCar scheduleCar = scheduleCarList.get(0);
        ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.getGroupCode());
        List<Long> slagSiteIdList = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(projectSchedule.getSlagSiteId());
        for (int i = 0; i < array.size(); i++) {
            slagSiteIdList.add(Long.parseLong(array.get(i).toString()));
        }
        List<ProjectSlagSite> projectSlagSites = projectSlagSiteServiceI.getAllByProjectId(projectId);
        //生成渣场索引
        Map<Long, Integer> slagSiteIndexMap = new HashMap<>();
        for (int i = 0; i < projectSlagSites.size(); i++) {
            Long slagSiteId = projectSlagSites.get(i).getId();
            slagSiteIndexMap.put(slagSiteId, i);
        }

        String slagSiteId = "";
        String slagSiteName = "";
        String longitude = "";     //经度
        String latitude = "";      //纬度
        String radius = "";           //半径
        String radiusByPhone = "";
        int i = 0;
        for (Long id : slagSiteIdList) {
            Integer index = slagSiteIndexMap.get(id);
            if (index == null) {
                cmdStatus = 1L;
                break;
            } else {
                ProjectSlagSite projectSlagSite = projectSlagSites.get(index);
                if (i == 0) {
                    slagSiteId = projectSlagSite.getId().toString();
                    slagSiteName = projectSlagSite.getName();
                    longitude = projectSlagSite.getLongitude().toString();
                    latitude = projectSlagSite.getLatitude().toString();
                    radius = projectSlagSite.getRadius().toString();
                    radiusByPhone = projectSlagSite.getRadiusByPhone().toString();
                } else {
                    slagSiteId = projectSlagSite.getId().toString() + SmartminingConstant.COMMA + slagSiteId;
                    slagSiteName = projectSlagSite.getName() + SmartminingConstant.COMMA + slagSiteName;
                    longitude = projectSlagSite.getLongitude().toString() + SmartminingConstant.COMMA + longitude;
                    latitude = projectSlagSite.getLatitude().toString() + SmartminingConstant.COMMA + latitude;
                    radius = projectSlagSite.getRadius().toString() + SmartminingConstant.COMMA + radius;
                    radiusByPhone = projectSlagSite.getRadiusByPhone().toString() + SmartminingConstant.COMMA + radiusByPhone;
                }
            }
        }
        SlagSitePositionReply reply = new SlagSitePositionReply();
        reply.setCmdInd(cmdInd);
        reply.setProjectId(projectId);
        reply.setCmdStatus(cmdStatus);
        reply.setPktID(pktID);
        reply.setLatitude(latitude);
        reply.setLongitude(longitude);
        reply.setRadius(radius);
        reply.setRadiusByPhone(radiusByPhone);
        reply.setSlagSiteId(slagSiteId);
        reply.setSlagSiteName(slagSiteName);
        try {
            mqttSender.sendDeviceReply(replyTopic, reply);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }*/

    //app获取所有项目
    public void handleAndroidAppGetProject(String cmdInd, String replytopic) {
        List<Project> projectList = projectServiceI.getAll();
        ScheduledReplyByApp app = new ScheduledReplyByApp();
        app.setCmdInd(cmdInd);
        app.setCmdStatus(0L);
        app.setJson(JSON.toJSONString(projectList));
        try {
            mqttSender.sendDeviceReply(replytopic, app);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    //查询渣车的状态
    public void handleAndroidAppGetStatus(String cmdInd, String replytopic, Long projectId, String carCode) {
        ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
        CarStatusReply reply = new CarStatusReply();
        reply.setCmdInd(cmdInd);
        reply.setProjectId(projectId);
        reply.setCarCode(carCode);
        try {
            if (projectCar == null) {
                throw new SmartminingProjectException("渣车不存在" + carCode);
            } else {
                reply.setStatus(projectCar.getStatus().getValue());
                reply.setStatusName(projectCar.getStatus().getName());
                ScheduleCar scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarCode(projectId, carCode);
                String json = "";
                if (scheduleCar != null) {
                    List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.getGroupCode());
                    ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.getGroupCode());
                    List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.getGroupCode());
                    ScheduleResponse response = new ScheduleResponse();
                    response.setScheduleCarList(scheduleCarList);
                    response.setScheduleMachineList(scheduleMachineList);
                    response.setProjectSchedule(projectSchedule);
                    json = JSON.toJSONString(response);
                } else {
                    throw new SmartminingProjectException("该渣车未排班" + carCode);
                }
                Date date = new Date();
                //班次
                Shift shift = workDateService.getShift(date, projectId);
                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
                Date start = dateMap.get("start");
                if (date.getTime() < start.getTime())
                    start = DateUtils.subtractionOneDay(start);
                //无效车数
                Map unValidCountMap = projectMqttCardReportServiceI.getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, start, shift.getAlias());
                if (unValidCountMap != null) {
                    Long unValidCount = Long.parseLong(unValidCountMap.get("count").toString());
                    reply.setUnValidTol(unValidCount);
                }
                //合成并完成的总车数
                Long totalCount = projectCarWorkInfoServiceI.getCarsCountByProjectIdAndDateIdentificationAndCarCode(projectId, carCode, start, shift.getAlias());
                reply.setSchedule(json);
                reply.setShift(shift.getAlias());
                reply.setCarCount(totalCount);
                reply.setMessage("查询成功");
            }
        } catch (SmartminingProjectException e) {
            reply.setCmdStatus(1);
            reply.setMessage(e.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
        } catch (Exception e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, reply);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    //渣车状态改变
    public void handleAndroidAppCarStatus(String cmdInd, String replytopic, Long projectId, String carCode, Integer status, String requestFrom, Long ptkID, String deviceId, Long slagCarId, Long createId, String createName) {
        String message = stringRedisTemplate.opsForValue().get("onOff" + carCode);
        CarStatusReply reply = new CarStatusReply();
        reply.setCmdInd(cmdInd);
        reply.setProjectId(projectId);
        reply.setSlagcarID(slagCarId);
        reply.setSlagcarCode(carCode);
        reply.setCarCode(carCode);
        reply.setProjectID(projectId);
        reply.setPktID(ptkID);
        ProjectCarStatus projectCarStatus = ProjectCarStatus.converStatus(status);
        reply.setStatus(status);
        reply.setStatusName(projectCarStatus.getName());
        DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
        if (status == 1)
            doStatus = DeviceDoStatusEnum.OnLine;
        else
            doStatus = DeviceDoStatusEnum.UnLine;
        Boolean success = true;
        String messageRemark = "请求成功";
        try {
            if (StringUtils.isEmpty(message)) {
                stringRedisTemplate.opsForValue().set("onOff" + carCode, "allReady", 60, TimeUnit.SECONDS);
                ProjectCar car = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, car.getId(), true);
                List<ProjectWorkTimeByCar> projectWorkTimeByCarList = projectWorkTimeByCarServiceI.getAllByProjectIdAndCarCodeAndStatus(projectId, carCode, ProjectCarStatus.Working.getValue());
                ProjectWorkTimeByCar projectWorkTimeByCar = null;
                Date date = new Date();
                Shift shift = workDateService.getShift(date, projectId);
                if (status == 1) {
                    car.setStatus(ProjectCarStatus.Working);
                    if (projectWorkTimeByCarList != null && projectWorkTimeByCarList.size() > 0)
                        projectWorkTimeByCar = projectWorkTimeByCarList.get(0);
                    else
                        projectWorkTimeByCar = new ProjectWorkTimeByCar();
                    projectWorkTimeByCar.setProjectId(projectId);
                    projectWorkTimeByCar.setCarId(car.getId());
                    projectWorkTimeByCar.setCarCode(carCode);
                    projectWorkTimeByCar.setStartTime(date);
                    projectWorkTimeByCar.setShift(shift);
                    projectWorkTimeByCar.setStatus(ProjectCarStatus.Working);
                    if (scheduleCarList != null && scheduleCarList.size() > 0) {
                        scheduleCarList.get(0).setFault(false);
                        scheduleCarList.get(0).setStatus(ProjectCarStatus.Working);
                    }
                } else if (status == 2) {
                    car.setStatus(ProjectCarStatus.Stop);
                    if (projectWorkTimeByCarList != null && projectWorkTimeByCarList.size() > 0)
                        projectWorkTimeByCar = projectWorkTimeByCarList.get(0);
                    if (projectWorkTimeByCar != null) {
                        projectWorkTimeByCar.setEndTime(date);
                        projectWorkTimeByCar.setStatus(ProjectCarStatus.Stop);
                    }
                    if (scheduleCarList != null && scheduleCarList.size() > 0) {
                        scheduleCarList.get(0).setFault(false);
                        scheduleCarList.get(0).setStatus(ProjectCarStatus.Stop);
                    }
                } else if (status == 3) {
                    car.setStatus(ProjectCarStatus.Fault);
                    if (projectWorkTimeByCarList != null && projectWorkTimeByCarList.size() > 0)
                        projectWorkTimeByCar = projectWorkTimeByCarList.get(0);
                    if (projectWorkTimeByCar != null) {
                        projectWorkTimeByCar.setEndTime(date);
                        projectWorkTimeByCar.setStatus(ProjectCarStatus.Fault);
                    }
                    if (scheduleCarList != null && scheduleCarList.size() > 0) {
                        scheduleCarList.get(0).setFault(true);
                        scheduleCarList.get(0).setStatus(ProjectCarStatus.Fault);
                    }
                } else if (status == 4) {
                    car.setStatus(ProjectCarStatus.StopWork);
                    if (projectWorkTimeByCarList != null && projectWorkTimeByCarList.size() > 0)
                        projectWorkTimeByCar = projectWorkTimeByCarList.get(0);
                    if (projectWorkTimeByCar != null) {
                        projectWorkTimeByCar.setEndTime(date);
                        projectWorkTimeByCar.setStatus(ProjectCarStatus.Stop);
                    }
                    if (scheduleCarList != null && scheduleCarList.size() > 0) {
                        scheduleCarList.get(0).setFault(false);
                        scheduleCarList.get(0).setStatus(ProjectCarStatus.StopWork);
                    }
                } else {
                    car.setStatus(ProjectCarStatus.Unknow);
                    if (scheduleCarList != null && scheduleCarList.size() > 0) {
                        scheduleCarList.get(0).setFault(false);
                        scheduleCarList.get(0).setStatus(ProjectCarStatus.StopWork);
                    }
                }
                projectCarServiceI.save(car);
                if (scheduleCarList != null && scheduleCarList.size() > 0)
                    scheduleCarServiceI.save(scheduleCarList.get(0));
                if (projectWorkTimeByCar != null)
                    projectWorkTimeByCarServiceI.save(projectWorkTimeByCar);
                reply.setStatus(car.getStatus().getValue());
                reply.setMessage("请求成功");
                if ("app".equals(requestFrom)) {
                    cmdInd = "onOff";
                    String method = "request";
                    String requestTopic = "smartmining/slagcar/cloud/" + deviceId + "/" + method;
                    Integer pktID = count;
                    Long carId = car.getId();
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("cmdInd", cmdInd);
                    jobDataMap.put("topic", requestTopic);
                    jobDataMap.put("pktId", pktID);
                    jobDataMap.put("carId", carId);
                    jobDataMap.put("carCode", carCode);
                    jobDataMap.put("status", status);
                    jobDataMap.put("statusName", projectCarStatus.getName());
                    jobDataMap.put("projectId", projectId);
                    String cron = QuartzConstant.MQTT_REPLY_CRON;
                    quartzManager.addJob(QuartzManager.createJobNameSlagCarWork(carId), SlagCarStatusJob.class, cron, jobDataMap);
                    Integer requestCount = 0;
                    stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_CAR_WORK + carId, requestCount.toString());
                }
            } else {
                reply.setCmdStatus(1);
                reply.setMessage("线程占用中，请勿频繁请求");
                success = false;
                messageRemark = "线程占用中，请勿频繁请求";
                //saveWorkLog(projectId, ProjectDeviceType.SlagTruckDevice, slagCarId, carCode, doStatus, -1L, "mqtt订阅，无法拿到创建人", false, "线程占用中，请勿频繁请求");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
            success = false;
            messageRemark = "后台异常";
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
            success = false;
            messageRemark = JSON.toJSONString(e.getStackTrace());
        } catch (Exception e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
            success = false;
            messageRemark = JSON.toJSONString(e.getStackTrace());
        } finally {
            try {
                stringRedisTemplate.delete("onOff" + carCode);
                mqttSender.sendDeviceReply(replytopic, reply);
                saveWorkLog(projectId, ProjectDeviceType.SlagTruckDevice, slagCarId, carCode, doStatus, createId, createName, success, messageRemark);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 终端查询指定渣车的状态
     *
     * @param cmdInd
     * @param pktID
     * @param projectID
     * @param slagCarCode
     * @param replytopic
     */
    public void handleSlagCarStatusQuery(String cmdInd, Long pktID, Long projectID, Long slagCarId, String slagCarCode, String replytopic) {
        CarStatusReply reply = new CarStatusReply();
        reply.setCmdInd(cmdInd);
        reply.setSlagcarCode(slagCarCode);
        reply.setSlagcarID(slagCarId);
        reply.setProjectID(projectID);
        reply.setPktID(pktID);
        try {
            ProjectCar projectCar = projectCarServiceI.get(slagCarId);
            reply.setStatus(projectCar.getStatus().getValue());
            reply.setStatusName(projectCar.getStatus().getName());
            reply.setCmdStatus(0);
            reply.setMessage("请求成功");
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1);
            reply.setMessage("后台异常");
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, reply);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    //卸载场地错误提醒
    public void handleAndroidAppCarError(String cmdInd, Long projectId, String carCode, String message) {
        String topic = "smartmining/cloud/" + projectId + "/slagcar/" + carCode + "/error/request";
        CarStatusReply reply = new CarStatusReply();
        reply.setCmdInd(cmdInd);
        reply.setProjectId(projectId);
        reply.setCarCode(carCode);
        reply.setMessage(message);
        try {
            mqttSender.sendDeviceReply(topic, reply);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    //挖机工作装载App查询
    public void handleAppCurrentData(String cmdInd, String replytopic, Long projectId, Long machineId) {
        CurrentDataByDigging dataByDigging = new CurrentDataByDigging();
        try {
            //获取当前时间
            Date date = new Date();
            ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(machineId);
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, true);
            ProjectSchedule projectSchedule = null;
            if (scheduleMachineList != null && scheduleMachineList.size() > 0) {
                ScheduleMachine scheduleMachine = scheduleMachineList.get(0);
                projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
            }
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            if (date.getTime() < earlyStartTime.getTime()) {
                date = DateUtils.subtractionOneDay(date);
                earlyStartTime = DateUtils.subtractionOneDay(earlyStartTime);
                earlyEndTime = DateUtils.subtractionOneDay(earlyEndTime);
            }
            Shift shift = Shift.Unknown;
            if (date.getTime() >= earlyStartTime.getTime() && date.getTime() <= earlyEndTime.getTime())
                shift = Shift.Early;
            else
                shift = Shift.Night;
            ProjectCarCount carCount = projectCarCountServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(projectId, machine.getCode(), date, shift.getAlias(), CarType.DiggingMachine.getValue());
            dataByDigging.setCmdInd(cmdInd);
            dataByDigging.setProjectID(projectId);
            dataByDigging.setExcavatorID(machineId);
            if (projectSchedule != null) {
                dataByDigging.setPlaceId(projectSchedule.getPlaceId());
                dataByDigging.setPlaceName(projectSchedule.getPlaceName());
            }
            if (shift.compareTo(Shift.Early) == 0)
                dataByDigging.setShift("早班");
            else
                dataByDigging.setShift("晚班");
            JSONArray jsonArray = new JSONArray();
            if (carCount != null) {
                jsonArray = JSONArray.parseArray(carCount.getDetailJson());
                dataByDigging.setNumTol(carCount.getTotalCount());
                dataByDigging.setWorkTime(carCount.getWorkTime() / 60);
            }
            String jsonDetail = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                String text = jsonArray.get(i).toString();
                Map detailMap = JSON.parseObject(text, new TypeReference<Map>() {
                });
                String detailMaterialName = detailMap.get("materialName").toString();
                Long materialCount = Long.parseLong(detailMap.get("count").toString());
                if (jsonArray.size() > 1 && i != jsonArray.size() - 1)
                    jsonDetail = detailMaterialName + "-" + materialCount;
                else
                    jsonDetail = jsonDetail + SmartminingConstant.COMMA + detailMaterialName + "-" + materialCount;
            }
            dataByDigging.setNumMat(jsonDetail);
            dataByDigging.setCmdStatus(0L);
        } catch (Exception e) {
            smartminingExceptionService.save(e, "APP主动获取挖机班次信息");
            e.printStackTrace();
            dataByDigging.setCmdStatus(1L);
        } finally {
            try {
                mqttSender.sendDeviceReply(replytopic, dataByDigging);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 司机端app通过mqtt登陆
     *
     * @param cmdInd    指令
     * @param projectId 项目id
     * @param carCode   渣车编号
     */
    public void handleAndroidDriverLogin(String cmdInd, Long projectId, String carCode, String password) {
        try {
            String topic = "smartmining/cloud/reply/slagcar/" + carCode + "/account";
            LoginReply reply = new LoginReply();
            reply.setCmdInd(cmdInd);
            reply.setCarCode(carCode);
            reply.setPassword(password);
            reply.setProjectId(projectId);
            //成功标记
            reply = interPhoneManager.driverLoginByMqtt(reply);
            mqttSender.sendDeviceReply(topic, reply);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("com.seater.smartmining.mqtt.DeviceMessageHandler.handleAndroidAccount");
        }
    }

    /**
     * 司机端app修改密码
     *
     * @param cmdInd    指令
     * @param projectId 项目id
     * @param carCode   渣车编号
     * @param carId     渣车id
     * @param password  原始密码
     * @param newPwd    新密码
     */
    public void handleAndroidDriverResetPwd(String cmdInd, Long projectId, String carCode, Long carId, String password, String newPwd) {
        try {
            String topic = "smartmining/cloud/" + projectId + "/slagcar/" + carCode + "/account";
            LoginReply reply = new LoginReply();
            reply.setCmdInd(cmdInd);
            reply.setCarCode(carCode);
            reply.setCarId(carId);
            reply.setPassword(password);
            reply.setProjectId(projectId);
            interPhoneManager.driverResetPwdByMqtt(reply, newPwd);
            mqttSender.sendDeviceReply(topic, reply);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("com.seater.smartmining.mqtt.DeviceMessageHandler.handleAndroidAccount");
        }
    }

    /**
     * 渣车出入渣场
     *
     * @param cmdInd     指令
     * @param projectId  项目id
     * @param carId      渣车id
     * @param slagSiteId 渣场id
     * @param isEnter    是否进入渣场
     */
    public void handleSlagSiteCarInOut(String cmdInd, Long projectId, Long carId, String slagSiteId, Boolean isEnter, Long pktID, String deviceId) throws JsonProcessingException {
        String topic = "smartmining/slagfield/cloud/" + deviceId + "/reply";
        SlagSitePositionReply reply = new SlagSitePositionReply();
        reply.setCmdInd(cmdInd);
        reply.setPktID(pktID);
        reply.setProjectId(projectId);
        try {
            Long slagSiteID = Long.parseLong(slagSiteId);
            ProjectSlagSite slagSite = projectSlagSiteServiceI.get(slagSiteID);
            if (null == slagSite) {
                throw new SmartminingProjectException("渣车出入渣场调度失败, 查询不到项目渣场,渣场id:" + slagSiteID);
            }
            // 返回topic smartmining/slagfield/cloud/45efad52/reply
            reply.setCmdStatus(-1L);
            reply.setSlagSiteId(slagSite.getId().toString());
            reply.setSlagSiteName(slagSite.getName());

            List<Long> carIdList = new ArrayList<>();
            carIdList.add(carId);
            boolean dispatchSlagSite = interPhoneManager.dispatchSlagSite(projectId, slagSite.getId(), carIdList, isEnter);

            if (dispatchSlagSite) {
                reply.setCmdStatus(0L);
            }
        } catch (Exception e) {
            reply.setCmdStatus(1L);
            e.printStackTrace();
            log.error("com.seater.smartmining.mqtt.DeviceMessageHandler.handleSlagSiteCarInOut");
        } finally {
            mqttSender.sendDeviceReply(topic, reply);
        }
    }

    public CarType convertCarType(Integer type) {
        CarType carType = CarType.Unknow;
        if (type == 4)
            carType = CarType.Forklift;
        else if (type == 5)
            carType = CarType.Roller;
        else if (type == 6)
            carType = CarType.GunHammer;
        else if (type == 7)
            carType = CarType.SingleHook;
        else if (type == 8)
            carType = CarType.WateringCar;
        else if (type == 9)
            carType = CarType.Scraper;
        else
            throw new SmartminingProjectException("其它设备类型type=" + type + "不存在");
        return carType;
    }

    /**
     * 寻找20米范围内的挖机 并记录下坐标
     *
     * @param cmdInd
     * @param replyTopic
     * @param carId
     * @param projectId
     * @param pktId
     */
    public void handleMessageGetexctfix(String cmdInd, String replyTopic, Long carId, Long projectId, Long pktId, BigDecimal longitudeByCar, BigDecimal latitudeByCar, String slagcarCode, String machineCode) throws JsonProcessingException {
        Date date = new Date();
        GetexctfixReply reply = new GetexctfixReply();
        reply.setCmdInd(cmdInd);
        reply.setPktID(pktId);
        reply.setProjectID(projectId);
        reply.setSlagcarID(carId);
        try {
            ProjectCar projectCar = projectCarServiceI.get(carId);
            if (projectCar == null)
                throw new SmartminingProjectException("渣车不存在");
            List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
            Map<String, Integer> machineMapIndex = new HashMap<>();
            for (int i = 0; i < projectDiggingMachineList.size(); i++) {
                machineMapIndex.put(projectDiggingMachineList.get(i).getCode(), i);
            }
            List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByProjectIdAndDeviceType(projectId, ProjectDeviceType.DiggingMachineDevice.getAlian());
            Date dateIdentification = new Date();
            Shift shift = workDateService.getShift(dateIdentification, projectId);
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, dateIdentification);
            Date start = dateMap.get("start");
            if (dateIdentification.getTime() < start.getTime())
                dateIdentification = DateUtils.subtractionOneDay(dateIdentification);
            dateIdentification = DateUtils.createReportDateByMonth(dateIdentification);
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
            String scheduleMachineId = "";
            String scheduleMachineCode = "";
            ProjectMachineLocation location = new ProjectMachineLocation();
            if (scheduleCarList.size() > 0) {
                Map map = new HashMap();
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
                List<ScheduleMachine> scheduleMachineList = new ArrayList<>();
                if (projectSchedule != null) {
                    scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, projectSchedule.getGroupCode());
                    location.setDispatchMode(projectSchedule.getDispatchMode());
                    List<Long> scheduleMachineIdList = new ArrayList<>();
                    List<String> scheduleMachineCodeList = new ArrayList<>();
                    if (projectSchedule.getDispatchMode().compareTo(ProjectDispatchMode.Auto) == 0) {
                        Date startTime = DateUtils.getAddSecondDate(date, -(60 * 60));
                        List<CarOrder> carOrderList = carOrderServiceI.queryPage(1, 10000, projectId, projectCar.getCode(), startTime, date).getContent();
                        if (carOrderList != null && carOrderList.size() > 0) {
                            CarOrder carOrder = carOrderList.get(0);
                            scheduleMachineIdList.add(carOrder.getDiggingMachineId());
                            scheduleMachineCodeList.add(carOrder.getDiggingMachineCode());
                        }
                    } else {
                        scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, projectSchedule.getGroupCode());
                        for (ScheduleMachine scheduleMachine : scheduleMachineList) {
                            scheduleMachineIdList.add(scheduleMachine.getMachineId());
                            scheduleMachineCodeList.add(scheduleMachine.getMachineCode());
                        }
                    }
                    scheduleMachineId = JSON.toJSONString(scheduleMachineIdList);
                    scheduleMachineCode = JSON.toJSONString(scheduleMachineCodeList);
                }
                map.put("projectSchedule", projectSchedule);
                map.put("scheduleMachineList", scheduleMachineList);
                map.put("scheduleCarList", scheduleCarList);
                String scheduleText = JSON.toJSONString(map);
                location.setScheduleText(scheduleText);
            }
            List<Map> machineMapList = new ArrayList<>();
            location.setProjectId(projectId);
            location.setCarId(carId);
            location.setCarCode(projectCar.getCode());
            location.setScheduleMachineId(scheduleMachineId);
            location.setScheduleMachieCode(scheduleMachineCode);
            location.setShift(shift);
            location.setDateIdentification(dateIdentification);
            location.setLongitudeByCar(longitudeByCar);
            location.setLatitudeByCar(latitudeByCar);
            location.setScheduleMachineCodeByDevice(machineCode);
            for (ProjectDevice device : projectDeviceList) {
                Double longitudeByMachine = new Double(0);
                //经度
                if (device.getLongitude() != null)
                    longitudeByMachine = device.getLongitude().doubleValue();
                Double latitudeByMachine = new Double(0);
                Boolean unLine = false;
                //纬度
                if (device.getLatitude() != null)
                    latitudeByMachine = device.getLatitude().doubleValue();
                GlobalCoordinates source = new GlobalCoordinates(latitudeByCar.doubleValue(), longitudeByCar.doubleValue());
                GlobalCoordinates target = new GlobalCoordinates(latitudeByMachine, longitudeByMachine);
                double meter = LocationUtils.getDistanceMeter(source, target, Ellipsoid.Sphere);
                if (meter <= 25) {
                    if (StringUtils.isEmpty(device.getCode()))
                        continue;
                    Integer index = machineMapIndex.get(device.getCode());
                    if (index == null)
                        continue;
                    if (device.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0)
                        unLine = true;
                    ProjectDiggingMachine machine = projectDiggingMachineList.get(index);
                    Map map = new HashMap();
                    map.put("machineId", machine.getId());
                    map.put("machineCode", machine.getCode());
                    map.put("unLine", unLine);
                    BigDecimal longitude = BigDecimal.ZERO;
                    if (device.getLongitude() != null)
                        longitude = device.getLongitude();
                    map.put("longitude", longitude);
                    BigDecimal latitude = BigDecimal.ZERO;
                    if (device.getLatitude() != null)
                        latitude = device.getLatitude();
                    map.put("latitude", latitude);
                    map.put("distance", new BigDecimal(meter).setScale(2, BigDecimal.ROUND_CEILING));
                    machineMapList.add(map);
                }
            }
            location.setDiggingMachineText(JSON.toJSONString(machineMapList));
            if (machineMapList.size() > 0) {
                projectMachineLocationServiceI.save(location);
                reply.setMessage("操作成功");
            } else {
                reply.setMessage("未找到指定范围内的挖机信息");
            }
            reply.setCmdStatus(0L);
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e);
            reply.setMessage(e.getMessage());
            reply.setCmdStatus(1L);
        } catch (JsonProcessingException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setMessage("后台异常，请联系管理员");
            reply.setCmdStatus(1L);
        } catch (IOException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setMessage("后台异常，请联系管理员");
            reply.setCmdStatus(1L);
        } finally {
            mqttSender.sendDeviceReply(replyTopic, reply);
        }
    }

    /**
     * 挖机暂停
     *
     * @param cmdInd
     * @param replyTopic
     * @param machineId
     * @param machineCode
     * @param projectId
     * @param pktId
     * @throws JsonProcessingException
     */
    public void handleMessagePause(String cmdInd, String replyTopic, Long machineId, String machineCode, Long projectId, Long pktId, Integer status) throws JsonProcessingException {
        String remark = "";
        Boolean success = true;
        Integer cmdStatus = 0;
        MachinePauseReply reply = new MachinePauseReply();
        reply.setCmdInd(cmdInd);
        reply.setCarCode(machineCode);
        reply.setPktID(pktId);
        reply.setProjectID(projectId);
        reply.setExcavatorID(machineId);
        try {
            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machineId);
            if (projectDiggingMachine == null)
                throw new SmartminingProjectException("挖机不存在");
            String message = "";
            if (status == 1) {
                projectDiggingMachine.setStopStatus(DiggingMachineStopStatus.PAUSE);
                message = "暂停成功";
            } else {
                projectDiggingMachine.setStopStatus(DiggingMachineStopStatus.Normal);
                message = "继续启动";
            }
            projectDiggingMachineServiceI.save(projectDiggingMachine);
            reply.setMessage(message);
        } catch (SmartminingProjectException e) {
            remark = e.getMsg();
            success = false;
            smartminingExceptionService.save(e);
            cmdStatus = 1;
            reply.setMessage(e.getMessage());
        } catch (IOException e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            smartminingExceptionService.save(e);
            cmdStatus = 1;
            reply.setMessage("系统异常，请联系管理员");
            e.printStackTrace();
        } catch (Exception e) {
            remark = JSON.toJSONString(e.getStackTrace());
            success = false;
            smartminingExceptionService.save(e);
            cmdStatus = 1;
            reply.setMessage("系统异常，请联系管理员");
            e.printStackTrace();
        } finally {
            reply.setCmdStatus(cmdStatus);
            mqttSender.sendDeviceReply(replyTopic, reply);
            DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
            if (status == 1)
                doStatus = DeviceDoStatusEnum.Pause;
            else
                doStatus = DeviceDoStatusEnum.Recover;
            saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, machineCode, doStatus, -1L, "终端主动请求", success, remark);
        }
    }

    public void handMessageGetPause(String cmdInd, String replyTopic, Long machineId, String machineCode, Long projectId, Long pktId) throws JsonProcessingException {
        Integer cmdStatus = 0;
        Integer status = 0;
        MachineGetPauseReply reply = new MachineGetPauseReply();
        reply.setCarCode(machineCode);
        reply.setCmdInd(cmdInd);
        reply.setExcavatorID(machineId);
        reply.setProjectID(projectId);
        reply.setPktID(pktId);
        try {
            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machineId);
            if (projectDiggingMachine == null)
                throw new SmartminingProjectException("挖机不存在");
            if (projectDiggingMachine.getStopStatus().compareTo(DiggingMachineStopStatus.PAUSE) == 0)
                status = 1;
            reply.setMessage("获取成功");
        } catch (SmartminingProjectException e) {
            smartminingExceptionService.save(e);
            reply.setMessage(e.getMessage());
            cmdStatus = 1;
        } catch (IOException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setMessage("服务器错误，请联系管理员");
            cmdStatus = 1;
        } finally {
            reply.setStatus(status);
            reply.setCmdStatus(cmdStatus);
            mqttSender.sendDeviceReply(replyTopic, reply);
        }
    }

    public void handDeviceStatus(String replyTopic, String uid, Long projectId, ProjectDeviceType projectDeviceType, String carCode, ProjectDeviceStatus status, Long pktID) throws JsonProcessingException {
        DeviceStatusReply reply = new DeviceStatusReply();
        reply.setProjectID(projectId);
        reply.setUid(uid);
        reply.setCmdInd("deviceStatus");
        reply.setCarCode(carCode);
        reply.setStatus(status);
        reply.setPktID(pktID);
        reply.setProjectDeviceType(projectDeviceType);
        mqttSender.sendDeviceReply(replyTopic, reply);
    }

    public void handleMessageUpdateExct(String cmdInd, String replytopic, Long slagCarId, Long projectId, Long pktID, String slagcarCode, String scheduleCode) throws JsonProcessingException {
        OnOff reply = new OnOff();
        reply.setCmdInd(cmdInd);
        reply.setPktID(pktID);
        reply.setProjectID(projectId);
        try {
            ProjectMqttUpdateExct exct = new ProjectMqttUpdateExct();
            exct.setCmdInd(cmdInd);
            exct.setPktID(pktID);
            exct.setSlagcarCode(slagcarCode);
            exct.setSlagcarID(slagCarId);
            exct.setProjectID(projectId);
            exct.setSchexctCode(scheduleCode);
            projectMqttUpdateExctServiceI.save(exct);
            reply.setCmdStatus(0L);
        } catch (JsonProcessingException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            reply.setCmdStatus(1L);
        } finally {
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * 生成异常明细
     * @param report
     * @return
     */
    public ProjectMqttCardReport getMqttCardReport(ProjectMqttCardReport report) {
        Date createTime = report.getTimeDischarge();
        Long projectId = report.getProjectId();
        String carCode = report.getCarCode();
        Date startDate = DateUtils.getAddSecondDate(createTime, -(60 * 60));
        ProjectCarWorkInfo projectCarWorkInfo = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode, createTime);
        if (projectCarWorkInfo != null) {
            Date lastTime = projectCarWorkInfo.getTimeDischarge();
            Long second = createTime.getTime() - lastTime.getTime();
            if (second < 60 * 60)
                startDate = lastTime;
            report.setSecond(second);
        }
        report.setStartTime(startDate);
        report.setEndTime(createTime);
        if (projectCarWorkInfo != null)
            report.setLastWork(JSON.toJSONString(projectCarWorkInfo));
        List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, report.getCarCode(), startDate, createTime);
        if (locationList != null && locationList.size() > 0) {
            ProjectMachineLocation projectMachineLocation = locationList.get(0);
            String seletedCode = projectMachineLocation.getScheduleMachineCodeByDevice();
            report.setLocationText(JSON.toJSONString(projectMachineLocation));
            report.setMachineMayBe(seletedCode);
            if (report.getErrorCode() == 4L) {
                String machineInfo = projectMachineLocation.getDiggingMachineText();
                JSONArray jsonArray = JSONArray.parseArray(machineInfo);
                String result = JSONArraySortUtils.jsonArraySort(jsonArray, "distance");
                JSONArray arrayNew = JSONArray.parseArray(result);
                JSONObject jsonObject = arrayNew.getJSONObject(0);
                String likeMachineCode = jsonObject.getString("machineCode");
                if (seletedCode.equals(likeMachineCode)) {
                    try {
                        //卸载的渣场
                        ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.get(report.getSlagSiteId());
                        if(projectSlagSite == null) {
                            report.setErrorCode(WorkMergeFailEnum.WithoutSlagSiteCode.getValue());
                            report.setErrorCodeMessage(WorkMergeFailEnum.WithoutSlagSiteCode.getName());
                            report.setMessage("渣场不存在，渣车编号：" + carCode);
                        }else {
                            //排班信息
                            String scheduleText = projectMachineLocation.getScheduleText();
                            Map map = JSON.parseObject(scheduleText, Map.class);
                            String scheduleStr = map.get("projectSchedule").toString();
                            String scheduleMachineListStr = map.get("scheduleMachineList").toString();
                            ProjectSchedule projectSchedule = JSON.parseObject(scheduleStr, ProjectSchedule.class);
                            List<ScheduleMachine> scheduleMachineList = JSONArray.parseArray(scheduleMachineListStr, ScheduleMachine.class);
                            ScheduleMachine scheduleMachine = null;
                            for (ScheduleMachine machine : scheduleMachineList) {
                                if (machine.getMachineCode().equals(seletedCode)) {
                                    scheduleMachine = machine;
                                    break;
                                }
                            }
                            if (projectSchedule != null && scheduleMachine != null) {
                                ProjectCar projectCar = projectCarServiceI.get(report.getCarId());
                                projectCarWorkInfo = new ProjectCarWorkInfo();
                                projectCarWorkInfo.setProjectId(projectId);
                                projectCarWorkInfo.setCarId(report.getCarId());
                                projectCarWorkInfo.setCarCode(carCode);
                                projectCarWorkInfo.setDispatchMode(report.getDispatchMode());
                                boolean goOut = false;
                                for (ProjectMachineLocation location : locationList) {
                                    JSONArray array = JSONArray.parseArray(location.getDiggingMachineText());
                                    JSONArray jsonArrayTwo = JSONArray.parseArray(JSONArraySortUtils.jsonArraySort(array, "distance"));
                                    for (int i = 0; i < jsonArrayTwo.size(); i++) {
                                        JSONObject jsonObjectTwo = jsonArray.getJSONObject(i);
                                        Boolean unLine = jsonObjectTwo.getBoolean("unLine");
                                        if (!unLine) {
                                            Long machineIdByLocation = jsonObject.getLong("machineId");
                                            String machineCodeByLocation = jsonObject.getString("machineCode");
                                            projectCarWorkInfo.setDiggingMachineId(machineIdByLocation);
                                            projectCarWorkInfo.setDiggingMachineCode(machineCodeByLocation);
                                            projectCarWorkInfo.setTimeLoad(location.getCreateTime());
                                            goOut = true;
                                            break;
                                        }
                                    }
                                    if (goOut)
                                        break;
                                }
                                projectCarWorkInfo.setCubic(projectCar.getModifyCapacity());
                                projectCarWorkInfo.setTimeDischarge(createTime);
                                projectCarWorkInfo.setShift(report.getShift());
                                projectCarWorkInfo.setInfoValid(true);
                                projectCarWorkInfo.setDateIdentification(report.getDateIdentification());
                                if (StringUtils.isNotEmpty(projectSchedule.getSlagSiteId()))
                                    projectCarWorkInfo.setAllowSlagSites(projectSchedule.getSlagSiteId());
                                JSONArray slagSiteArray = JSONArray.parseArray(projectCarWorkInfo.getAllowSlagSites());
                                List<Long> slagSiteList = new ArrayList<>();
                                if (slagSiteArray != null) {
                                    for (int i = 0; i < slagSiteArray.size(); i++) {
                                        Long id = Long.parseLong(slagSiteArray.getString(i));
                                        slagSiteList.add(id);
                                    }
                                }
                                if (!slagSiteList.contains(report.getSlagSiteId())) {
                                    projectCarWorkInfo.setIsVaild(VaildEnums.NOTVAILDBYCAR);
                                    projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 卸载场地错误，可卸载渣场ID数组：" + JSON.toJSONString(slagSiteList));
                                    handleAndroidAppCarError("carError", projectId, carCode, projectCarWorkInfo.getRemark());
                                }
                                Long distance = scheduleMachine.getDistance() + projectSlagSite.getDistance();
                                Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
                                ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
                                Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
                                Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
                                Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (projectCar.getModifyCapacity() / 1000000L); //精确到分
                                projectCarWorkInfo.setSlagSiteId(projectSlagSite.getId());
                                projectCarWorkInfo.setSlagSiteName(projectSlagSite.getName());
                                projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
                                projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
                                projectCarWorkInfo.setMaterialId(scheduleMachine.getMaterialId());
                                projectCarWorkInfo.setMateriaName(scheduleMachine.getMaterialName());
                                projectCarWorkInfo.setDistance(distance);
                                projectCarWorkInfo.setPayableDistance(payableDistance);
                                projectCarWorkInfo.setAmount(amount);
                                projectCarWorkInfo.setPricingType(scheduleMachine.getPricingType());
                                projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
                                projectCarWorkInfo.setPass(Score.Pass);
                                projectCarWorkInfo.setUnLoadUp(true);
                                projectCarWorkInfo.setMergeCode(WorkMergeSuccessEnum.SuccessMerge.getValue());
                                projectCarWorkInfo.setMergeMessage(WorkMergeSuccessEnum.SuccessMerge.getName());
                                projectCarWorkInfo.setRemark("选定挖机与疑似装载一致，自动容错");
                                projectCarWorkInfo = projectCarWorkInfoServiceI.save(projectCarWorkInfo);
                                if(projectCarWorkInfo != null)
                                    report = null;
                            }
                        }
                    } catch (IOException e) {
                        report.setErrorCode(WorkMergeFailEnum.BackStageError.getValue());
                        report.setErrorCodeMessage(WorkMergeFailEnum.BackStageError.getName());
                        report.setMessage("后台异常，渣车编号：" + carCode);
                        report.setExceptionDetails(JSON.toJSONString(e.getStackTrace()));
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (report.getErrorCode() == 4L) {
                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, carCode, ProjectDeviceType.SlagTruckDevice.getAlian());
                if (projectDevice == null) {
                    report.setErrorCode(WorkMergeFailEnum.NoHaveDevice.getValue());
                    report.setErrorCodeMessage(WorkMergeFailEnum.NoHaveDevice.getName());
                    report.setMessage("该渣车未安装终端，渣车编号：" + carCode);
                } else {
                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OffLine) == 0) {
                        report.setErrorCode(WorkMergeFailEnum.DeviceUnLineError.getValue());
                        report.setErrorCodeMessage(WorkMergeFailEnum.DeviceUnLineError.getName());
                        report.setMessage("疑似渣车对应的终端已离线，渣车编号：" + carCode);
                    } else {
                        report.setErrorCode(WorkMergeFailEnum.DeviceErrorLike.getValue());
                        report.setErrorCodeMessage(WorkMergeFailEnum.DeviceErrorLike.getName());
                        report.setMessage("疑似终端异常，未查到任何疑似装载信息" + carCode);
                    }
                }
            }
        }
        return report;
    }

    /**
     * 异常详情
     *
     * @param cmdInd
     * @param replytopic
     * @param projectId
     * @param pktID
     * @param carCode
     * @throws JsonProcessingException
     */
    public void handleErrorMessageDetail(String cmdInd, String replytopic, Long projectId, Long pktID, String carCode) throws JsonProcessingException {
        SlagCarErrorDetailReply reply = new SlagCarErrorDetailReply();
        reply.setProjectID(projectId);
        reply.setPktID(pktID);
        reply.setCarCode(carCode);
        reply.setCmdInd(cmdInd);
        try {
            Date date = new Date();
            Shift shift = workDateService.getShift(date, projectId);
            //Shift shift = Shift.Early;
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStart = dateMap.get("start");
            if (earlyStart.getTime() > date.getTime())
                date = DateUtils.getAddDate(date, -1);
            date = DateUtils.createReportDateByMonth(date);
            List<ProjectMqttCardReport> reportList = projectMqttCardReportServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift.getAlias());
            int i = 0;
            String timeDischarge = "";
            String message = "";
            String loaderName = "";
            String errorInfo = "";
            String slagSiteName = "";
            for (ProjectMqttCardReport report : reportList) {
                if (i == 0) {
                    timeDischarge = DateUtils.formatDateByPattern(report.getTimeDischarge(), SmartminingConstant.DATEFORMAT);
                    message = report.getErrorCodeMessage();
                    loaderName = report.getLoaderName();
                    slagSiteName = report.getSlagSiteName();
                    if(StringUtils.isNotEmpty(report.getLocationText())){
                        JSONObject object = JSON.parseObject(report.getLocationText());
                        String machineText = object.getString("diggingMachineText");
                        errorInfo = machineText;
                    }
                } else {
                    timeDischarge = DateUtils.formatDateByPattern(report.getTimeDischarge(), SmartminingConstant.DATEFORMAT) + SmartminingConstant.COMMA + timeDischarge;
                    message = report.getErrorCodeMessage() + SmartminingConstant.COMMA + message;
                    loaderName = report.getLoaderName() + SmartminingConstant.COMMA + loaderName;
                    slagSiteName = report.getSlagSiteName() + SmartminingConstant.COMMA + slagSiteName;
                    if(StringUtils.isNotEmpty(report.getLocationText())){
                        JSONObject object = JSON.parseObject(report.getLocationText());
                        String machineText = object.getString("diggingMachineText");
                        errorInfo = machineText + SmartminingConstant.COMMA + errorInfo;
                    }
                }
                i++;
            }
            reply.setCmdStatus(0L);
            reply.setMessage(message);
            reply.setTimeDischarge(timeDischarge);
            reply.setLoaderName(loaderName);
            reply.setSlagSiteName(slagSiteName);
            reply.setErrorInfo(errorInfo);
            reply.setRemark("请求成功");
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1L);
            reply.setRemark("后台异常");
        } finally {
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * App司机端请求异常明细
     *
     * @param cmdInd
     * @param replytopic
     * @param projectId
     * @param carCode
     * @throws JsonProcessingException
     */
    public void handleErrorMessageDetailByApp(String cmdInd, String replytopic, Long projectId, String carCode) throws JsonProcessingException {
        SlagCarErrorDetailReply reply = new SlagCarErrorDetailReply();
        reply.setProjectID(projectId);
        reply.setCarCode(carCode);
        reply.setCmdInd(cmdInd);
        try {
            Date date = new Date();
            Shift shift = workDateService.getShift(date, projectId);
            //Shift shift = Shift.Early;
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStart = dateMap.get("start");
            if (earlyStart.getTime() > date.getTime())
                date = DateUtils.getAddDate(date, -1);
            date = DateUtils.createReportDateByMonth(date);
            List<ProjectMqttCardReport> reportList = projectMqttCardReportServiceI.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift.getAlias());
            List<ProjectMqttCardReport> resultList = new ArrayList<>();
            for(ProjectMqttCardReport report : reportList){
                report.setExceptionDetails("");
                resultList.add(report);
            }
            String message = JSON.toJSONString(reportList);
            reply.setCmdStatus(0L);
            reply.setMessage(message);
            reply.setRemark("请求成功");
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1L);
            reply.setRemark("后台异常");
        } finally {
            mqttSender.sendDeviceReply(replytopic, reply);
        }
    }

    /**
     * App发送用户定位信息
     * @param cmdInd
     * @param replyTopic
     * @param account
     */
    public void handleUserPositionByApp(String cmdInd, String replyTopic, String account, BigDecimal longitude, BigDecimal latitude) throws JsonProcessingException {
        UserPositionReply reply = new UserPositionReply();
        reply.setCmdInd(cmdInd);
        reply.setAccount(account);
        try {
            SysUser sysUser = sysUserServiceI.getByAccount(account);
            sysUser.setLongitude(longitude);
            sysUser.setLatitude(latitude);
            sysUserServiceI.save(sysUser);
            reply.setCmdStatus(0L);
            reply.setMessage("请求成功");
            Date date = new Date();
            String locationMessage = stringRedisTemplate.opsForValue().get(sysUser.getAccount() + "locationMessage");
            List<Map> mapList = new ArrayList<>();
            if(StringUtils.isNotEmpty(locationMessage))
                mapList = JSONArray.parseArray(locationMessage, Map.class);
            Map map = new HashMap();
            map.put("longitude", longitude);
            map.put("latitude", latitude);
            map.put("date", date);
            mapList.add(map);
            stringRedisTemplate.opsForValue().set(sysUser.getAccount() + "locationMessage", JSON.toJSONString(mapList));
        } catch (IOException e) {
            e.printStackTrace();
            reply.setCmdStatus(1L);
            reply.setMessage("请求失败");
        } finally {
            mqttSender.sendDeviceReply(replyTopic, reply);
        }
    }

    /**
     * 大屏 现场信息  一旦生成数据 便以mq形式发给前端
     *
     * @param cmdInd
     * @param map
     */
    public void handleMessageSceneInfo(String cmdInd, Map map) {
        if ("schedule".equals(cmdInd)) {

        } else if ("workTime".equals("cmdInd")) {

        } else {

        }
    }
}
