package com.seater.smartmining.schedule;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.DiggingMachineStatusJob;
import com.seater.smartmining.quartz.job.ReplyMachineScheduleInfoJob;
import com.seater.smartmining.quartz.job.ReplySlagCarPositionInfoJob;
import com.seater.smartmining.quartz.job.ReplySlagCarScheduleInfoJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/16 0016 16:05
 */
@Service
public class SmartminingScheduleService {

    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    private ProjectScheduleDetailServiceI projectScheduleDetailServiceI;
    @Autowired
    private ProjectModifyScheduleLogServiceI projectModifyScheduleLogServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private ProjectMqttParamsRequestServiceI projectMqttParamsRequestServiceI;
    Long count = 0L;

    /**
     * 手动修改 choose = 1   模板调度修改 choose = 0
     *
     * @param projectId
     * @param scheduleResponse
     * @param choose
     * @throws IOException
     */
    public void saveNewSchedule(Long projectId, List<ScheduleResponse> scheduleResponse, Integer choose) throws IOException {
        //获取当前项目对象
        Project project = projectServiceI.get(projectId);
        SysUser sysUser = null;
        //获取当前用户对象
        if (choose == 1) {
            sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        }
        DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
        Date date = new Date();
        Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
        Date nightStart = dateMap.get("nightStart");

        //排班集合
        List<ProjectSchedule> projectScheduleSaveList = new ArrayList<>();
        //挖机排班集合
        List<ScheduleMachine> scheduleMachineSaveList = new ArrayList<>();
        //渣车排班集合
        List<ScheduleCar> scheduleCarListSaveList = new ArrayList<>();
        //app挖机看板集合
        List<ProjectAppStatisticsByMachine> projectAppStatisticsByMachineList = new ArrayList<>();
        //获取所有渣车集合
        List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdOrderById(projectId);
        //生成渣车索引
        Map<String, Integer> carIndexMap = new HashMap<>();
        for (int i = 0; i < projectCarList.size(); i++) {
            carIndexMap.put(projectCarList.get(i).getCode(), i);
        }
        //获取所有挖机集合
        List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
        //生成挖机索引
        Map<String, Integer> machineIndexMap = new HashMap<>();
        for (int i = 0; i < projectDiggingMachineList.size(); i++) {
            machineIndexMap.put(projectDiggingMachineList.get(i).getCode(), i);
        }
        //获取渣车终端设备的集合
        List<ProjectDevice> projectDeviceCarList = projectDeviceServiceI.getAllByProjectIdAndDeviceType(projectId, ProjectDeviceType.SlagTruckDevice.getAlian());
        //生成渣车终端设备的集合
        Map<String, Integer> slagCarIndexMap = new HashMap<>();
        for (int i = 0; i < projectDeviceCarList.size(); i++) {
            slagCarIndexMap.put(projectDeviceCarList.get(i).getCode(), i);
        }

        //获取挖机终端设备的集合
        List<ProjectDevice> projectDeviceMachineList = projectDeviceServiceI.getAllByProjectIdAndDeviceType(projectId, ProjectDeviceType.DiggingMachineDevice.getAlian());
        //生成挖机终端设备的集合
        Map<String, Integer> machineDeviceIndexMap = new HashMap<>();
        for (int i = 0; i < projectDeviceMachineList.size(); i++) {
            machineDeviceIndexMap.put(projectDeviceMachineList.get(i).getCode(), i);
        }
        for (ScheduleResponse response : scheduleResponse) {
            ScheduleResponse sr = new ScheduleResponse();
            if (response.getProjectSchedule().getDeviceStartStatus().compareTo(DeviceStartStatusEnum.Only) == 0 || response.getProjectSchedule().getDeviceStartStatus().compareTo(DeviceStartStatusEnum.Check) == 0) {
                if (response.getScheduleMachineList().size() > 1)
                    throw new SmartminingProjectException("当前版本不支持混编");
            }
            //生成唯一组别编号
            String groupCode = UUID.randomUUID().toString() + projectId;
            if (response.getProjectSchedule() == null)
                throw new SmartminingProjectException("排班信息不能为空");
            if (response.getScheduleMachineList() == null || response.getScheduleMachineList().size() < 1)
                throw new SmartminingProjectException("排班信息中挖机数据不能为空");
            ProjectSchedule projectSchedule = response.getProjectSchedule();

            //获取新增或者修改的渣车集合
            List<ScheduleCar> newScheduleCarList = response.getScheduleCarList();
            //获取新增或者修改的挖机集合
            List<ScheduleMachine> newScheduleMachineList = response.getScheduleMachineList();
            //新的渣车编号集合
            List<String> newCarCodeList = new ArrayList<>();
            //新的挖机编号集合
            List<String> newMachineCodeList = new ArrayList<>();
            for (ScheduleCar car : newScheduleCarList) {
                Integer index = carIndexMap.get(car.getCarCode());
                if (index == null)
                    throw new SmartminingProjectException("渣车不存在，请检查渣车是否有效");
                ProjectCar projectCar = projectCarList.get(index);
                projectCar.setSeleted(true);
                projectCarServiceI.save(projectCar);
                newCarCodeList.add(car.getCarCode());
            }
            for (ScheduleMachine machine : newScheduleMachineList) {
                Integer index = machineIndexMap.get(machine.getMachineCode());
                if (index == null)
                    throw new SmartminingProjectException("挖机不存在，请检查挖机是否有效");
                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineList.get(index);
                projectDiggingMachine.setSelected(true);
                projectDiggingMachineServiceI.save(projectDiggingMachine);
                newMachineCodeList.add(machine.getMachineCode());
            }
            if (StringUtils.isNotEmpty(projectSchedule.getGroupCode())) {
                groupCode = projectSchedule.getGroupCode();
                ProjectSchedule oldSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, projectSchedule.getGroupCode());
                if (oldSchedule != null) {
                    projectSchedule.setCreateId(oldSchedule.getCreateId());
                    projectSchedule.setCreateName(oldSchedule.getCreateName());
                    projectSchedule.setCreateTime(oldSchedule.getCreateTime());
                }
                projectSchedule.setModifyTime(date);
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, response.getProjectSchedule().getGroupCode());
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, response.getProjectSchedule().getGroupCode());
                ProjectModifyScheduleLog log = new ProjectModifyScheduleLog();
                if (oldSchedule != null) {
                    log.setBeforeGroupCode(oldSchedule.getGroupCode());
                    log.setBeforeManagerId(oldSchedule.getManagerId());
                    log.setBeforeManagerName(oldSchedule.getManagerName());
                    log.setBeforePlaceId(oldSchedule.getPlaceId());
                    log.setBeforePlaceName(oldSchedule.getPlaceName());
                }
                if (choose == 1) {
                    log.setModifyId(sysUser.getId());
                    log.setModifyName(sysUser.getName());
                } else {
                    log.setModifyName("调度模板自动自动执行");
                }
                Date earlyStart = dateMap.get("start");
                Date dateIdentification = new Date();
                if(date.getTime() < earlyStart.getTime())
                    dateIdentification = DateUtils.getAddDate(dateIdentification, -1);
                dateIdentification = DateUtils.createReportDateByMonth(dateIdentification);
                Shift shift = workDateService.getShift(date, projectId);
                log.setModifyTime(date);
                log.setDateIdentification(dateIdentification);
                log.setShift(shift);
                log.setBeforeCarJson(JSON.toJSONString(scheduleCarList));
                log.setBeforeMachineJson(JSON.toJSONString(scheduleMachineList));
                log.setManagerId(response.getProjectSchedule().getManagerId());
                log.setManagerName(response.getProjectSchedule().getManagerName());
                log.setCarJson(JSON.toJSONString(response.getScheduleCarList()));
                log.setMachineJson(JSON.toJSONString(response.getScheduleMachineList()));
                log.setGroupCode(groupCode);
                log.setPlaceId(response.getProjectSchedule().getPlaceId());
                log.setPlaceName(response.getProjectSchedule().getPlaceName());
                if (oldSchedule != null)
                    log.setProjectId(oldSchedule.getProjectId());
                log.setModifyEnum(ModifyEnum.MODIFY);
                if (oldSchedule != null)
                    log.setBeforeScheduleJson(JSON.toJSONString(oldSchedule));
                log.setScheduleJson(JSON.toJSONString(response.getProjectSchedule()));
                projectModifyScheduleLogServiceI.save(log);
                //查询修改的挖机是否是开机中
                for (ScheduleMachine machine : response.getScheduleMachineList()) {
                    List<ScheduleMachine> oldScheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machine.getMachineId(), true);
                    ScheduleMachine oldScheduleMachine = new ScheduleMachine();
                    if (oldScheduleMachineList != null && oldScheduleMachineList.size() > 0)
                        oldScheduleMachine = oldScheduleMachineList.get(0);
                    if (oldSchedule != null) {
                        if (oldSchedule.getPlaceId() != projectSchedule.getPlaceId() || oldScheduleMachine.getPricingType().compareTo(machine.getPricingType()) != 0) {
                            List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machine.getMachineId());
                            if (diggingList != null && diggingList.size() > 0) {
                                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                                projectDiggingMachine.setEndWorkTime(date);
                                if(oldSchedule.getPlaceId() != projectSchedule.getPlaceId())
                                    projectDiggingMachine.setStatus(DiggingMachineStatus.Stop);
                                ProjectWorkTimeByDigging digging = diggingList.get(0);
                                digging.setEndTime(date);
                                digging.setStatus(DiggingMachineStatus.Stop);
                                digging.setStopMode(StopEnum.AUTOMATIC);
                                Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                                digging.setWorkTime(workTime);
                                projectWorkTimeByDiggingServiceI.save(digging);
                                projectDiggingMachine = projectDiggingMachineServiceI.save(projectDiggingMachine);

                                ProjectAppStatisticsByMachine appMachine = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, digging.getShift().getAlias(), nightStart, digging.getMaterialCode());
                                if (appMachine == null)
                                    appMachine = new ProjectAppStatisticsByMachine();
                                appMachine.setProjectId(projectId);
                                appMachine.setWorkTime(appMachine.getWorkTime() + digging.getWorkTime());
                                appMachine.setShifts(digging.getShift());
                                appMachine.setMachineCode(digging.getMaterialCode());
                                appMachine.setCreateDate(nightStart);
                                projectAppStatisticsByMachineList.add(appMachine);
                                if (oldScheduleMachine.getPricingType().compareTo(machine.getPricingType()) != 0 && oldSchedule.getPlaceId() == projectSchedule.getPlaceId()) {
                                    ProjectDiggingMachine newProjectDiggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                                    newProjectDiggingMachine.setStartWorkTime(date);
                                    newProjectDiggingMachine.setEndWorkTime(new Date(0));
                                    //newProjectDiggingMachine.setStatus(DiggingMachineStatus.Working);
                                    ProjectWorkTimeByDigging projectWorkTimeByDigging = new ProjectWorkTimeByDigging();
                                    projectWorkTimeByDigging.setProjectId(projectId);
                                    projectWorkTimeByDigging.setMaterialId(projectDiggingMachine.getId());
                                    projectWorkTimeByDigging.setMaterialCode(projectDiggingMachine.getCode());
                                    projectWorkTimeByDigging.setMaterialInfo(projectDiggingMachine.getBrandName() + projectDiggingMachine.getModelName());
                                    projectWorkTimeByDigging.setStatus(DiggingMachineStatus.Working);
                                    projectWorkTimeByDigging.setStartTime(date);
                                    projectWorkTimeByDigging.setCreateTime(date);
                                    projectWorkTimeByDigging.setStartMode(StartEnum.AUTOMATIC);
                                    projectWorkTimeByDigging.setDateIdentification(dateIdentification);
                                    projectWorkTimeByDigging.setSlagSiteId(projectSchedule.getSlagSiteId());
                                    projectWorkTimeByDigging.setSlagSiteName(projectSchedule.getSlagSiteName());
                                    projectWorkTimeByDigging.setPlaceId(projectSchedule.getPlaceId());
                                    projectWorkTimeByDigging.setPlaceName(projectSchedule.getPlaceName());
                                    projectWorkTimeByDigging.setDataId(machine.getMaterialId());
                                    projectWorkTimeByDigging.setDataName(machine.getMaterialName());
                                    if (projectWorkTimeByDigging.getStartTime().getTime() >= earlyStart.getTime() && projectWorkTimeByDigging.getStartTime().getTime() <= nightStart.getTime())
                                        projectWorkTimeByDigging.setShift(ShiftsEnums.DAYSHIFT);
                                    else
                                        projectWorkTimeByDigging.setShift(ShiftsEnums.BLACKSHIFT);
                                    if (response.getScheduleMachineList().size() < 1)
                                        throw new SmartminingProjectException("没有找到挖机对应的排班信息");
                                    PricingTypeEnums pricingType = PricingTypeEnums.Unknow;
                                    for (ScheduleMachine scheduleMachine : response.getScheduleMachineList()) {
                                        if (scheduleMachine.getMachineCode().equals(projectDiggingMachine.getCode())) {
                                            pricingType = scheduleMachine.getPricingType();
                                            break;
                                        }
                                    }
                                    projectWorkTimeByDigging.setPricingTypeEnums(pricingType);
                                    projectDiggingMachineServiceI.save(newProjectDiggingMachine);
                                    projectWorkTimeByDiggingServiceI.save(projectWorkTimeByDigging);
                                } else {
                                    Integer index = machineDeviceIndexMap.get(machine.getMachineCode());
                                    ProjectDevice projectDevice = index != null ? projectDeviceMachineList.get(index) : null;
                                    if (projectDevice != null) {
                                        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0 && projectSchedule.getDispatchMode().compareTo(ProjectDispatchMode.Auto) != 0) {
                                            String cmdInd = "onOff";
                                            String method = "request";
                                            String replytopic = "smartmining/excavator/cloud/" + projectDiggingMachine.getUid() + "/" + method;
                                            Long pktID = count;
                                            Long excavatorID = projectDiggingMachine.getId();
                                            Integer status = 0;
                                            JobDataMap jobDataMap = new JobDataMap();
                                            jobDataMap.put("cmdInd", cmdInd);
                                            jobDataMap.put("topic", replytopic);
                                            jobDataMap.put("pktId", pktID);
                                            jobDataMap.put("machineId", excavatorID);
                                            jobDataMap.put("status", status);
                                            jobDataMap.put("projectId", log.getProjectId());
                                            jobDataMap.put("deviceId", projectDiggingMachine.getUid());
                                            jobDataMap.put("choose", 1);
                                            jobDataMap.put("createId", sysUser.getId());
                                            jobDataMap.put("createName", sysUser.getAccount());
                                            String cron = QuartzConstant.MQTT_REPLY_CRON;
                                            quartzManager.addJob(QuartzManager.createJobNameMachineWork(excavatorID), DiggingMachineStatusJob.class, cron, jobDataMap);
                                            Integer requestCount = 0;
                                            stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + excavatorID, requestCount.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                                            ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                                            paramsRequest.setProjectId(projectId);
                                            paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                                            paramsRequest.setRequest("/api/projectschedule/save");
                                            paramsRequest.setRequestParams(JSON.toJSONString(machine));
                                            paramsRequest.setCreateTime(new Date());
                                            projectMqttParamsRequestServiceI.save(paramsRequest);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                List<String> oldCarCodeList = new ArrayList<>();
                List<String> oldMachineCodeList = new ArrayList<>();
                for (ScheduleCar car : scheduleCarList) {
                    oldCarCodeList.add(car.getCarCode());
                }
                for (ScheduleMachine machine : scheduleMachineList) {
                    oldMachineCodeList.add(machine.getMachineCode());
                }
                for (String code : newCarCodeList) {
                    scheduleCarServiceI.deleteByProjectIdAndCarCode(projectId, code);
                }
                for (String code : oldCarCodeList) {
                    scheduleCarServiceI.deleteByProjectIdAndCarCode(projectId, code);
                }
                for (String code : newMachineCodeList) {
                    scheduleMachineServiceI.deleteByProjectIdAndMachineCode(projectId, code);
                }
                for (String code : oldMachineCodeList) {
                    scheduleMachineServiceI.deleteByProjectIdAndMachineCode(projectId, code);
                }
            } else {
                if (newCarCodeList.size() > 0)
                    scheduleCarServiceI.deleteByProjectIdAndCarCodeList(projectId, newCarCodeList);
                if (newMachineCodeList.size() > 0)
                    scheduleMachineServiceI.deleteByProjectIdAndMachineCodeList(projectId, newMachineCodeList);
                projectSchedule.setProjectId(projectId);
                if (choose == 1) {
                    projectSchedule.setCreateId(sysUser.getId());
                    projectSchedule.setCreateName(sysUser.getName());
                } else {
                    projectSchedule.setCreateName("调度模板自动执行");
                }
                projectSchedule.setGroupCode(groupCode);
                projectSchedule.setCreateTime(new Date());
                projectSchedule.setScheduleCode(StringUtils.createCode(projectId));
            }
            if (choose == 1) {
                projectSchedule.setModifyId(sysUser.getId());
                projectSchedule.setModifyName(sysUser.getName());
            } else {
                projectSchedule.setModifyName("调度模板自动执行");
            }
            for (ScheduleMachine machine : response.getScheduleMachineList()) {
                ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                if (diggingMachine == null)
                    throw new SmartminingProjectException("挖机对象不存在");
                diggingMachine.setPlaceId(projectSchedule.getPlaceId());
                diggingMachine.setPlaceName(projectSchedule.getPlaceName());
                diggingMachine = projectDiggingMachineServiceI.save(diggingMachine);
                machine.setIsVaild(true);
                machine.setFault(false);
                machine.setGroupCode(groupCode);
                machine.setDiggingMachineStatus(diggingMachine.getStatus());
                machine.setProjectId(projectId);
                scheduleMachineSaveList.add(machine);
                Integer index = machineDeviceIndexMap.get(machine.getMachineCode());
                ProjectDevice projectDevice = index != null ? projectDeviceMachineList.get(index) : null;
                if (projectDevice != null) {
                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0 && projectSchedule.getDispatchMode().compareTo(ProjectDispatchMode.Auto) != 0) {
                        String cmdInd = "schedule";
                        Long pktID = count;
                        Long excavatorID = diggingMachine.getId();
                        String replytopic = "smartmining/excavator/cloud/" + projectDevice.getUid() + "/request";
                        JobDataMap jobDataMap = new JobDataMap();
                        jobDataMap.put("cmdInd", cmdInd);
                        jobDataMap.put("pktId", pktID);
                        jobDataMap.put("topic", replytopic);
                        jobDataMap.put("machineId", excavatorID);
                        jobDataMap.put("projectId", projectId);
                        jobDataMap.put("deviceId", projectDevice.getUid());
                        String cron = QuartzConstant.MQTT_REPLY_CRON;
                        quartzManager.addJob(QuartzManager.createJobNameScheduleMachine(projectDevice.getUid()), ReplyMachineScheduleInfoJob.class, cron, jobDataMap);
                        Integer count = 0;
                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_SCHEDULE + projectDevice.getUid(), count.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                        ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                        paramsRequest.setProjectId(projectId);
                        paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                        paramsRequest.setRequest("/api/projectschedule/save");
                        paramsRequest.setRequestParams(JSON.toJSONString(machine));
                        paramsRequest.setCreateTime(new Date());
                        projectMqttParamsRequestServiceI.save(paramsRequest);
                    }
                }
            }
            sr.setScheduleMachineList(scheduleMachineSaveList);
            for (ScheduleCar car : response.getScheduleCarList()) {
                car.setFault(false);
                car.setIsVaild(true);
                car.setGroupCode(groupCode);
                car.setProjectId(projectId);
                scheduleCarListSaveList.add(car);
                Integer index = slagCarIndexMap.get(car.getCarCode());
                ProjectDevice projectDevice = index != null ? projectDeviceCarList.get(index) : null;
                if (projectDevice != null) {
                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0 && projectSchedule.getDispatchMode().compareTo(ProjectDispatchMode.Auto) != 0) {
                        //发送数据到设备
                        String cmdInd = "schedule";
                        Long pktID = count;
                        Long slagcarID = car.getCarId();
                        String replytopic = "smartmining/slagcar/cloud/" + projectDevice.getUid() + "/request";
                        JobDataMap jobDataMap = new JobDataMap();
                        jobDataMap.put("cmdInd", cmdInd);
                        jobDataMap.put("pktId", pktID);
                        jobDataMap.put("topic", replytopic);
                        jobDataMap.put("slagCarId", slagcarID);
                        jobDataMap.put("projectId", projectId);
                        jobDataMap.put("deviceId", projectDevice.getUid());
                        String cron = QuartzConstant.MQTT_REPLY_CRON;
                        quartzManager.addJob(QuartzManager.createJobNameScheduleSlagSiteCar(projectDevice.getUid()), ReplySlagCarScheduleInfoJob.class, cron, jobDataMap);
                        Integer count = 0;
                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_CAR_SCHEDULE + projectDevice.getUid(), count.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                        //渣场定位发送
                        cmdInd = "position";
                        jobDataMap.put("cmdInd", cmdInd);
                        quartzManager.addJob(QuartzManager.createJobNameScheduleSlagSitePosition(projectDevice.getUid()), ReplySlagCarPositionInfoJob.class, cron, jobDataMap);
                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_CAR_POSITION + projectDevice.getUid(), count.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                        ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                        paramsRequest.setProjectId(projectId);
                        paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                        paramsRequest.setRequest("/api/projectschedule/save");
                        paramsRequest.setRequestParams(JSON.toJSONString(car));
                        paramsRequest.setCreateTime(new Date());
                        projectMqttParamsRequestServiceI.save(paramsRequest);
                    }
                }
            }
            projectScheduleSaveList.add(projectSchedule);
            sr.setScheduleCarList(scheduleCarListSaveList);
            sr.setProjectSchedule(projectSchedule);
            //发送数据给APP
            String cmdInd = "schedule";
            Long pktID = count;
            String json = JSON.toJSONString(sr);
            String replytopic = "smartmining/app/cloud/" + SmartminingConstant.APP_SCHEDULE_IDENTIFICATION + "/request";
            handler.handleAndroidApp(cmdInd, replytopic, pktID, projectId, json);
            count++;
            //----------------
            if (project != null && project.getDispatchMode() == ProjectDispatchMode.Auto) {
                projectScheduleDetailServiceI.reset(groupCode, projectId);
            }
        }
        scheduleCarServiceI.batchSave(scheduleCarListSaveList);
        scheduleMachineServiceI.batchSave(scheduleMachineSaveList);
        projectScheduleServiceI.batchSave(projectScheduleSaveList);
        ScheduleService.deleteSchedule();
        List<String> allScheduleCarList = scheduleCarServiceI.getAllByProjectIdAndIsVaild(projectId, true);
        List<String> carCodeList = projectCarServiceI.getAllByProjectIdAndVaild(projectId, true);
        //生成渣车索引
        Map<String, Integer> carCodeIndexMap = new HashMap<>();
        for (int i = 0; i < allScheduleCarList.size(); i++) {
            String carCode = allScheduleCarList.get(i);
            carCodeIndexMap.put(carCode, i);
        }
        List<String> updateCodeListByFalse = new ArrayList<>();
        List<String> updateCodeListByTrue = new ArrayList<>();
        for (int i = 0; i < carCodeList.size(); i++) {
            String carCode = carCodeList.get(i);
            Integer carCodeIndex = carCodeIndexMap.get(carCode);
            if (carCodeIndex == null)
                updateCodeListByFalse.add(carCode);
            else
                updateCodeListByTrue.add(carCode);
        }
        if (updateCodeListByFalse.size() > 0)
            projectCarServiceI.updateSeleted(false, updateCodeListByFalse);
        if (updateCodeListByTrue.size() > 0)
            projectCarServiceI.updateSeleted(true, updateCodeListByTrue);
        List<String> allScheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndIsVaild(projectId, true);
        List<String> machineCodeList = projectDiggingMachineServiceI.getAllByProjectIdAndIsVaild(projectId, true);
        List<String> updateMachineListByFalse = new ArrayList<>();
        List<String> updateMachineListByTrue = new ArrayList<>();
        //生成挖机索引
        Map<String, Integer> machineCodeIndexMap = new HashMap<>();
        for (int i = 0; i < allScheduleMachineList.size(); i++) {
            String machineCode = allScheduleMachineList.get(i);
            machineCodeIndexMap.put(machineCode, i);
        }
        for (int i = 0; i < machineCodeList.size(); i++) {
            String machineCode = machineCodeList.get(i);
            Integer machineCodeIndex = machineCodeIndexMap.get(machineCode);
            if (machineCodeIndex == null)
                updateMachineListByFalse.add(machineCode);
            else
                updateMachineListByTrue.add(machineCode);

        }
        if (updateMachineListByFalse.size() > 0)
            projectDiggingMachineServiceI.updateSeleted(false, updateMachineListByFalse);
        if (updateMachineListByTrue.size() > 0)
            projectDiggingMachineServiceI.updateSeleted(true, updateMachineListByTrue);
    }
}
