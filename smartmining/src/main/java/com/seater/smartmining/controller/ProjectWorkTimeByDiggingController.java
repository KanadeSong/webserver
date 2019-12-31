package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.WorkTimeModifyByMachine;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.quartz.MeterReadingJob;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.DiggingMachineStatusJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 14:04
 */
@RestController
@RequestMapping("/api/projectworktimebydigging")
public class ProjectWorkTimeByDiggingController {
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private ProjectScheduledServiceI projectScheduledServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private ProjectDiggingWorkLogServiceI projectDiggingWorkLogServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }

    Long count = 0L;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/getByChoose")
    public Result getByMaterialIdAndDate(HttpServletRequest request, Long materialId, Date date) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Map<String, Date> resultMap = workDateService.getWorkTime(projectId, date);
            Date start = resultMap.get("start");
            Date end = resultMap.get("end");
            List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdAndTime(projectId, materialId, start, end);
            return Result.ok(diggingList);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /*@RequestMapping("/examine")
    @Transactional(rollbackFor = Exception.class)
    public Result examine(Long id, Integer workInfvalue) throws IOException {
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        ProjectWorkTimeByDigging log = projectWorkTimeByDiggingServiceI.get(id);
        ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(log.getMaterialId());

        if (diggingMachine != null) {
            //DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            ProjectDevice projectDevice = projectDeviceServiceI.getByProjectIdAndUid(diggingMachine.getProjectId(), diggingMachine.getUid());
            if(projectDevice != null) {
                if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                    String cmdInd = "onOff";
                    String method = "request";
                    String replytopic = "smartmining/excavator/cloud/" + diggingMachine.getUid() + "/" + method;
                    Long pktID = count;
                    Long excavatorID = log.getMaterialId();
                    Integer status = null;
                    if (workInfvalue == 2) {
                        status = 1;
                    } else if (workInfvalue == 4) {
                        status = 0;
                    }
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("cmdInd", cmdInd);
                    jobDataMap.put("topic", replytopic);
                    jobDataMap.put("pktId", pktID);
                    jobDataMap.put("machineId", excavatorID);
                    jobDataMap.put("status", status);
                    jobDataMap.put("projectId", log.getProjectId());
                    jobDataMap.put("deviceId", diggingMachine.getUid());
                    jobDataMap.put("choose", 0);
                    jobDataMap.put("createId", sysUser.getId());
                    jobDataMap.put("createName", sysUser.getAccount());
                    String cron = QuartzConstant.MQTT_REPLY_CRON;
                    quartzManager.addJob(QuartzManager.createJobNameMachineWork(excavatorID), DiggingMachineStatusJob.class, cron, jobDataMap);
                    Integer requestCount = 0;
                    stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + excavatorID, requestCount.toString());
                    count++;
                }else{
                    return Result.error("终端已离线，请上线后再审核。");
                }
            }else{
                return Result.error("挖机不存在，请确认挖机编号");
            }
        }
        return Result.ok();
    }*/

    @RequestMapping("/examine")
    @Transactional(rollbackFor = Exception.class)
    public Result examine(HttpServletRequest request, Long id, Integer workInfvalue){
        String remark = "请求成功";
        Boolean success = true;
        SysUser sysUser = null;
        ProjectWorkTimeByDigging digging = null;
        ProjectDiggingMachine diggingMachine = null;
        DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
        Long projectId = CommonUtil.getProjectId(request);
        try {
            sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            digging = projectWorkTimeByDiggingServiceI.get(id);
            diggingMachine = projectDiggingMachineServiceI.get(digging.getMaterialId());
            Date date = new Date();
            String message = stringRedisTemplate.opsForValue().get(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            if (StringUtils.isEmpty(message)) {
                stringRedisTemplate.opsForValue().set(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD, "allReady", 10, TimeUnit.SECONDS);
                ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, diggingMachine.getCode());
                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
                Date start = dateMap.get("start");
                Date end = dateMap.get("earlyEnd");
                Date nightStart = dateMap.get("nightStart");
                if (date.getTime() < start.getTime()) {
                    start = DateUtils.subtractionOneDay(start);
                    end = DateUtils.subtractionOneDay(end);
                    nightStart = DateUtils.subtractionOneDay(nightStart);
                }
                if (workInfvalue == 2) {
                    if (scheduleMachine == null)
                        throw new SmartminingProjectException("挖机排班不存在" + diggingMachine.getCode());
                    ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
                    if (projectSchedule == null)
                        throw new SmartminingProjectException("挖机对应排班不存在" + diggingMachine.getCode());
                    digging.setProjectId(projectId);
                    digging.setMaterialId(diggingMachine.getId());
                    digging.setMaterialCode(diggingMachine.getCode());
                    digging.setMaterialInfo(diggingMachine.getBrandName() + diggingMachine.getModelName());
                    digging.setStatus(DiggingMachineStatus.Working);
                    digging.setStartTime(date);
                    digging.setCreateTime(date);
                    digging.setStopStatus(DiggingMachineStopStatus.Normal);
                    diggingMachine.setStartWorkTime(date);
                    diggingMachine.setStopStatus(DiggingMachineStopStatus.Normal);
                    diggingMachine.setEndWorkTime(new Date(0));
                    diggingMachine.setStatus(DiggingMachineStatus.Working);
                    if (digging.getStartTime().getTime() >= start.getTime() && digging.getStartTime().getTime() <= end.getTime()) {
                        digging.setShift(ShiftsEnums.DAYSHIFT);
                    } else {
                        digging.setShift(ShiftsEnums.BLACKSHIFT);
                    }
                    List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, diggingMachine.getId(), true);
                    if (scheduleMachineList.size() < 1)
                        throw new SmartminingProjectException("没有找到挖机对应的排班信息" + diggingMachine.getCode());
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
                    if (digging.getStartTime() == null) {
                        diggingMachine.setStatus(DiggingMachineStatus.Stop);
                        projectDiggingMachineServiceI.save(diggingMachine);
                        throw new SmartminingProjectException("该挖机上班信息为空！" + diggingMachine.getCode());
                    }
                    digging.setEndTime(date);
                    digging.setStatus(DiggingMachineStatus.Stop);
                    Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                    digging.setWorkTime(workTime);
                    diggingMachine.setEndWorkTime(date);
                    diggingMachine.setStatus(DiggingMachineStatus.Stop);
                    if(scheduleMachine != null)
                        scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Stop);
                }
                ProjectDevice projectDevice = projectDeviceServiceI.getByProjectIdAndUid(diggingMachine.getProjectId(), diggingMachine.getUid());
                if (projectDevice != null) {
                    if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                        String cmdInd = "onOff";
                        String method = "request";
                        String replytopic = "smartmining/excavator/cloud/" + diggingMachine.getUid() + "/" + method;
                        Long pktID = count;
                        Long excavatorID = digging.getMaterialId();
                        Integer status = null;
                        if (workInfvalue == 2) {
                            status = 1;
                        } else if (workInfvalue == 4) {
                            status = 0;
                        }
                        JobDataMap jobDataMap = new JobDataMap();
                        jobDataMap.put("cmdInd", cmdInd);
                        jobDataMap.put("topic", replytopic);
                        jobDataMap.put("pktId", pktID);
                        jobDataMap.put("machineId", excavatorID);
                        jobDataMap.put("status", status);
                        jobDataMap.put("projectId", projectId);
                        jobDataMap.put("deviceId", diggingMachine.getUid());
                        //jobDataMap.put("choose", 0);
                        jobDataMap.put("createId", sysUser.getId());
                        jobDataMap.put("createName", sysUser.getAccount());
                        String cron = QuartzConstant.MQTT_REPLY_CRON;
                        quartzManager.addJob(QuartzManager.createJobNameMachineWork(excavatorID), DiggingMachineStatusJob.class, cron, jobDataMap);
                        Integer requestCount = 0;
                        stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + excavatorID, requestCount.toString());
                        count++;
                    } else {
                        return Result.error("终端已离线，请上线后再审核。");
                    }
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
                projectDiggingMachineServiceI.save(diggingMachine);
                projectWorkTimeByDiggingServiceI.save(digging);
                scheduleMachineServiceI.save(scheduleMachine);
                stringRedisTemplate.delete(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            } else {
                throw new SmartminingProjectException("redis不为空");
            }
            return Result.ok();
        } catch (SmartminingProjectException e){
            remark = e.getMsg();
            success = false;
            return Result.error(e.getMsg());
        } catch (JsonProcessingException e) {
            remark = e.getMessage();
            success = false;
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            remark = e.getMessage();
            success = false;
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (Exception e){
            remark = e.getMessage();
            success = false;
            e.printStackTrace();
            return Result.error(e.getMessage());
        } finally {
            DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
            if (workInfvalue == 2)
                doStatus = DeviceDoStatusEnum.StartExamine;
            else
                doStatus = DeviceDoStatusEnum.StopExamine;
            Long machineId = 0L;
            String machineCode = "";
            if(diggingMachine != null){
                machineId = diggingMachine.getId();
                machineCode = diggingMachine.getCode();
            }
            Long createId = 0L;
            String createName = "";
            if(sysUser != null){
                createId = sysUser.getId();
                createName = sysUser.getAccount();
            }
            handler.saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, machineCode, doStatus, createId, createName, success, remark);
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(HttpServletRequest request, ProjectWorkTimeByDigging log) {
        //获取到项目编号
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(log.getMaterialId());
            diggingMachine.setStatus(DiggingMachineStatus.Stop);
            diggingMachine.setStartWorkTime(log.getStartTime());
            diggingMachine.setEndWorkTime(log.getEndTime());
            log.setProjectId(projectId);
            log.setMaterialInfo(diggingMachine.getBrandName() + diggingMachine.getModelName());
            log.setStatus(DiggingMachineStatus.Stop);
            log.setCreateTime(new Date());
            Long second = DateUtils.calculationHour(log.getStartTime(), log.getEndTime());
            log.setWorkTime(second);
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, log.getStartTime());
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            Date nightStartTime = dateMap.get("nightStart");
            Date nightEndTime = dateMap.get("end");
            ShiftsEnums shift = ShiftsEnums.UNKNOW;
            if (log.getStartTime().getTime() >= earlyStartTime.getTime() && log.getStartTime().getTime() <= earlyEndTime.getTime()) {
                shift = ShiftsEnums.DAYSHIFT;
            } else if (log.getStartTime().getTime() >= nightStartTime.getTime() && log.getStartTime().getTime() <= nightEndTime.getTime()) {
                shift = ShiftsEnums.BLACKSHIFT;
            }
            log.setShift(shift);
            Date dateIdentification = DateUtils.createReportDateByMonth(earlyStartTime);
            ProjectAppStatisticsByMachine appMachine = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, log.getShift().getAlias(), nightStartTime, log.getMaterialCode());
            if (appMachine == null)
                appMachine = new ProjectAppStatisticsByMachine();
            appMachine.setProjectId(projectId);
            appMachine.setWorkTime(appMachine.getWorkTime() + log.getWorkTime());
            appMachine.setShifts(log.getShift());
            appMachine.setMachineCode(log.getMaterialCode());
            appMachine.setCreateDate(nightStartTime);

            //挖机排班对象
            ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, diggingMachine.getCode());
            ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
            //工作平台和物料 方便后面的统计
            log.setPlaceId(projectSchedule.getPlaceId());
            log.setPlaceName(projectSchedule.getPlaceName());
            log.setDataId(scheduleMachine.getMaterialId());
            log.setDataName(scheduleMachine.getMaterialName());
            log.setDateIdentification(dateIdentification);
            log.setSlagSiteId(projectSchedule.getSlagSiteId());
            log.setSlagSiteName(projectSchedule.getSlagSiteName());
            scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Stop);
            projectAppStatisticsByMachineServiceI.save(appMachine);
            projectDiggingMachineServiceI.save(diggingMachine);
            projectWorkTimeByDiggingServiceI.save(log);
            scheduleMachineServiceI.save(scheduleMachine);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping("/modify")
    public Result modify(HttpServletRequest request, ProjectWorkTimeByDigging log) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, log.getStartTime());
            Date earlyStartTime = dateMap.get("start");
            Date nightStartTime = dateMap.get("nightStart");
            ShiftsEnums shift = null;
            if (log.getStartTime().getTime() >= earlyStartTime.getTime()) {
                shift = ShiftsEnums.DAYSHIFT;
            } else if (log.getStartTime().getTime() >= nightStartTime.getTime()) {
                shift = ShiftsEnums.BLACKSHIFT;
            } else {
                shift = ShiftsEnums.UNKNOW;
            }
            log.setShift(shift);
            log.setProjectId(projectId);
            projectWorkTimeByDiggingServiceI.save(log);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    @Transactional
    public Object query(Integer current, Integer pageSize, String code, HttpServletRequest request, @RequestParam(value = "time", required = false) ArrayList<String> reangePickerValue, ShiftsEnums shifts) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectWorkTimeByDigging> spec = new Specification<ProjectWorkTimeByDigging>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectWorkTimeByDigging> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    Long projectId = Long.parseLong(request.getHeader("projectId"));
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    if (reangePickerValue != null && reangePickerValue.size() == 2) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                            Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                            Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                            list.add(criteriaBuilder.between(root.get("createDate").as(Date.class), startTime, endTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if (StringUtils.isNotEmpty(code))
                        list.add(criteriaBuilder.like(root.get("materialCode").as(String.class), code));
                    if (shifts != null)
                        list.add(criteriaBuilder.equal(root.get("shift").as(ShiftsEnums.class), shifts));
                    query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return Result.ok(projectWorkTimeByDiggingServiceI.query(spec, PageRequest.of(cur, page)));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping("/delete")
    @Transactional
    public Object delete(@RequestParam(value = "ids") List<Long> ids) {
        try {
            projectWorkTimeByDiggingServiceI.delete(ids);
            return Result.ok();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/stop", produces = "application/json")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result stopOrModify(HttpServletRequest request, @RequestBody List<WorkTimeModifyByMachine> ids) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Long machineId = 0L;
        Integer status = 0;
        ProjectDiggingMachine diggingMachine = null;
        try {
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Date date = new Date();
            for (WorkTimeModifyByMachine machine : ids) {
                machineId = machine.getMachineId();
                status = machine.getStatus();
                diggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
                if(status == 1)
                    doStatus = DeviceDoStatusEnum.Start;
                else
                    doStatus = DeviceDoStatusEnum.Stop;
                String message = stringRedisTemplate.opsForValue().get(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
                if (StringUtils.isEmpty(message)) {
                    stringRedisTemplate.opsForValue().set(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD, "allReady", 10, TimeUnit.SECONDS);
                    ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, diggingMachine.getCode(), 2);
                    ProjectWorkTimeByDigging digging = null;
                    Map<String, Date> dateMap = workDateService.getWorkTime(projectId, new Date());
                    Date start = dateMap.get("start");
                    Date end = dateMap.get("earlyEnd");
                    Date nightStart = dateMap.get("nightStart");
                    if (date.getTime() < start.getTime()) {
                        start = DateUtils.subtractionOneDay(start);
                        end = DateUtils.subtractionOneDay(end);
                        nightStart = DateUtils.subtractionOneDay(nightStart);
                    }
                    Date dateIdentification = DateUtils.createReportDateByMonth(start);
                    //挖机排班
                    List<ScheduleMachine> machineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machine.getMachineId(), true);
                    if (machine.getStatus() == 1) {
                        List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdAdd(projectId, machine.getMachineId());
                        if (diggingList.size() > 0) {
                            digging = diggingList.get(0);
                        } else {
                            digging = new ProjectWorkTimeByDigging();
                            digging.setPlaceId(diggingMachine.getPlaceId());
                            digging.setPlaceName(diggingMachine.getPlaceName());
                        }
                        if (machineList == null || machineList.size() == 0) {
                            stringRedisTemplate.delete(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
                            handler.saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, diggingMachine.getCode(), doStatus, sysUser.getId(), sysUser.getAccount(), false, "未找到该挖机对应的排班信息");
                            throw new SmartminingProjectException("未找到该挖机对应的排班信息");
                        }
                        ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, machineList.get(0).getGroupCode());

                        if (diggingMachine != null) {
                            if (projectDevice != null) {
                                if (StringUtils.isEmpty(diggingMachine.getUid()) && StringUtils.isNotEmpty(projectDevice.getUid()))
                                    diggingMachine.setUid(projectDevice.getUid());
                            }

                            PricingTypeEnums pricingType = machineList.get(0).getPricingType();
                            digging.setDataId(machineList.get(0).getMaterialId());
                            digging.setDataName(machineList.get(0).getMaterialName());
                            digging.setSlagSiteId(projectSchedule.getSlagSiteId());
                            digging.setSlagSiteName(projectSchedule.getSlagSiteName());
                            digging.setPricingTypeEnums(pricingType);
                            digging.setMaterialId(diggingMachine.getId());
                            digging.setMaterialCode(diggingMachine.getCode());
                            digging.setProjectId(projectId);
                            digging.setMaterialInfo(diggingMachine.getBrandName() + diggingMachine.getModelName());
                            digging.setCreateTime(date);
                            digging.setStartTime(date);
                            if (digging.getStartTime().getTime() >= start.getTime() && digging.getStartTime().getTime() <= end.getTime()) {
                                digging.setShift(ShiftsEnums.DAYSHIFT);
                            } else {
                                digging.setShift(ShiftsEnums.BLACKSHIFT);
                            }
                            digging.setStatus(DiggingMachineStatus.Working);
                            digging.setStartMode(StartEnum.FORCE);
                            digging.setDateIdentification(dateIdentification);
                            digging.setStopStatus(DiggingMachineStopStatus.Normal);
                            diggingMachine.setStartWorkTime(date);
                            diggingMachine.setEndWorkTime(new Date(0));
                            diggingMachine.setStatus(DiggingMachineStatus.Working);
                            diggingMachine.setStartMode(StartEnum.FORCE);
                            diggingMachine.setStopStatus(DiggingMachineStopStatus.Normal);
                            machineList.get(0).setDiggingMachineStatus(DiggingMachineStatus.Working);
                            machineList.get(0).setFault(false);
                            handler.saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, diggingMachine.getCode(), doStatus, sysUser.getId(), sysUser.getAccount(), true, "请求成功");
                        } else {
                            stringRedisTemplate.delete(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
                            handler.saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, diggingMachine.getCode(), doStatus, sysUser.getId(), sysUser.getAccount(), false, "没有找到对应挖机信息");
                            throw new SmartminingProjectException("没有找到对应挖机的信息");
                        }
                    } else {
                        List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machine.getMachineId());
                        if (diggingList.size() > 0) {
                            digging = diggingList.get(0);
                            digging.setEndTime(new Date());
                            Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                            digging.setWorkTime(workTime);
                            digging.setStatus(DiggingMachineStatus.Stop);
                            digging.setStopMode(StopEnum.FORCE);
                        }
                        diggingMachine.setEndWorkTime(date);
                        diggingMachine.setStatus(DiggingMachineStatus.Stop);
                        diggingMachine.setStopMode(StopEnum.FORCE);
                        if (machineList != null && machineList.size() > 0)
                            machineList.get(0).setDiggingMachineStatus(DiggingMachineStatus.Stop);
                        handler.saveWorkLog(projectId, ProjectDeviceType.DiggingMachineDevice, machineId, diggingMachine.getCode(), doStatus, sysUser.getId(), sysUser.getAccount(), true, "请求成功");
                    }
                    projectDiggingMachineServiceI.save(diggingMachine);
                    if(digging != null) {
                        ProjectAppStatisticsByMachine appMachine = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, digging.getShift().getAlias(), nightStart, digging.getMaterialCode());
                        if (appMachine == null)
                            appMachine = new ProjectAppStatisticsByMachine();
                        appMachine.setProjectId(projectId);
                        appMachine.setWorkTime(appMachine.getWorkTime() + digging.getWorkTime());
                        appMachine.setShifts(digging.getShift());
                        appMachine.setMachineCode(digging.getMaterialCode());
                        appMachine.setCreateDate(nightStart);
                        projectAppStatisticsByMachineServiceI.save(appMachine);
                        projectWorkTimeByDiggingServiceI.save(digging);
                    }
                    if (machineList != null && machineList.size() > 0)
                        scheduleMachineServiceI.save(machineList.get(0));
                    if (projectDevice != null) {
                        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                            String cmdInd = "onOff";
                            String method = "request";
                            String replytopic = "smartmining/excavator/cloud/" + diggingMachine.getUid() + "/" + method;
                            Long pktID = count;
                            Long excavatorID = diggingMachine.getId();
                            JobDataMap jobDataMap = new JobDataMap();
                            jobDataMap.put("cmdInd", cmdInd);
                            jobDataMap.put("topic", replytopic);
                            jobDataMap.put("pktId", pktID);
                            jobDataMap.put("machineId", excavatorID);
                            jobDataMap.put("status", status);
                            jobDataMap.put("projectId", diggingMachine.getProjectId());
                            jobDataMap.put("deviceId", diggingMachine.getUid());
                            jobDataMap.put("choose", 1);
                            jobDataMap.put("createId", sysUser.getId());
                            jobDataMap.put("createName", sysUser.getAccount());
                            String cron = QuartzConstant.MQTT_REPLY_CRON;
                            quartzManager.addJob(QuartzManager.createJobNameMachineWork(excavatorID), DiggingMachineStatusJob.class, cron, jobDataMap);
                            Integer requestCount = 0;
                            stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + excavatorID, requestCount.toString());
                            count++;
                        }
                    }
                }
                stringRedisTemplate.delete(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            if(diggingMachine != null)
                stringRedisTemplate.delete(diggingMachine.getCode() + projectId + SmartminingConstant.DIGGING_MACHINE_KEY_WORD);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ProjectDiggingWorkLog log = new ProjectDiggingWorkLog();
            log.setProjectId(projectId);
            log.setMachineId(machineId);
            log.setMessage(e.getMessage());
            log.setCreateTime(new Date());
            log.setStatus(status);
            log.setDetailMessage(JSON.toJSONString(e.getStackTrace()));
            try {
                projectDiggingWorkLogServiceI.save(log);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/stopByFault", produces = "application/json")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result stopByFault(HttpServletRequest request, @RequestBody List<WorkTimeModifyByMachine> ids) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Date date = new Date();
            for (WorkTimeModifyByMachine machine : ids) {
                ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, diggingMachine.getCode(), 2);
                /*if(machine.getStatus() == 0) {
                    ProjectWorkTimeByDigging digging = projectWorkTimeByDiggingServiceI.get(machine.getId());
                    diggingMachine = projectDiggingMachineServiceI.get(digging.getMaterialId());
                }else{
                    diggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                }
                projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, diggingMachine.getCode(), 2);*/
                ProjectWorkTimeByDigging digging = null;
                List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machine.getMachineId());
                if (diggingList.size() > 0) {
                    digging = diggingList.get(0);
                } else {
                    digging = new ProjectWorkTimeByDigging();
                    digging.setPlaceId(diggingMachine.getPlaceId());
                    digging.setPlaceName(diggingMachine.getPlaceName());
                }
                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, new Date());
                Date start = dateMap.get("start");
                Date end = dateMap.get("earlyEnd");
                Date nightStart = dateMap.get("nightStart");
                Date dateIdentification = DateUtils.createReportDateByMonth(start);
                //挖机排班
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, diggingMachine.getDiggingMachineId(), true);
                if (scheduleMachineList == null || scheduleMachineList.size() == 0)
                    throw new SmartminingProjectException("未找到该挖机对应的排班信息");
                ScheduleMachine scheduleMachine = scheduleMachineList.get(0);
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleMachine.getGroupCode());
                if (machine.getStatus() == 1) {
                    /*ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());*/
                    if (diggingMachine != null) {
                        if (projectDevice != null) {
                            if (StringUtils.isEmpty(diggingMachine.getUid()) && StringUtils.isNotEmpty(projectDevice.getUid()))
                                diggingMachine.setUid(projectDevice.getUid());
                        }
                        digging.setDataId(scheduleMachine.getMaterialId());
                        digging.setDataName(scheduleMachine.getMaterialName());
                        digging.setSlagSiteId(projectSchedule.getSlagSiteId());
                        digging.setSlagSiteName(projectSchedule.getSlagSiteName());
                        digging.setPricingTypeEnums(machine.getPricingType());
                        digging.setMaterialId(diggingMachine.getId());
                        digging.setMaterialCode(diggingMachine.getCode());
                        digging.setProjectId(projectId);
                        digging.setMaterialInfo(diggingMachine.getBrandName() + diggingMachine.getModelName());
                        digging.setCreateTime(date);
                        digging.setStartTime(date);
                        digging.setDateIdentification(dateIdentification);
                        if (digging.getStartTime().getTime() >= start.getTime() && digging.getStartTime().getTime() <= end.getTime()) {
                            digging.setShift(ShiftsEnums.DAYSHIFT);
                        } else {
                            digging.setShift(ShiftsEnums.BLACKSHIFT);
                        }
                        digging.setStatus(DiggingMachineStatus.Working);
                        diggingMachine.setStartWorkTime(date);
                        diggingMachine.setEndWorkTime(null);
                        diggingMachine.setStatus(DiggingMachineStatus.Working);
                        scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Working);
                    } else {
                        throw new SmartminingProjectException("没有找到对应挖机的信息");
                    }
                } else {
                    /*List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machine.getMachineId());*/
                    if (diggingList.size() > 0) {
                        /*digging = diggingList.get(0);*/
                        digging.setEndTime(new Date());
                        Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                        digging.setWorkTime(workTime);
                        digging.setStatus(DiggingMachineStatus.Stop);
                        diggingMachine.setEndWorkTime(date);
                        diggingMachine.setStatus(DiggingMachineStatus.Stop);
                        scheduleMachine.setDiggingMachineStatus(DiggingMachineStatus.Stop);
                    } else {
                        throw new SmartminingProjectException("没有找到该挖机对应的班次信息");
                    }
                }
                diggingMachine.setStopStatus(DiggingMachineStopStatus.Fault);
                digging.setStopStatus(DiggingMachineStopStatus.Fault);
                projectDiggingMachineServiceI.save(diggingMachine);
                projectWorkTimeByDiggingServiceI.save(digging);
                scheduleMachineServiceI.save(scheduleMachine);
                if (projectDevice != null) {
                    DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
                    String cmdInd = "onOff";
                    String method = "request";
                    String replytopic = "smartmining/excavator/cloud/" + projectDevice.getUid() + "/" + method;
                    Long pktID = count;
                    Long excavatorID = diggingMachine.getId();
                    //handler.handleMessageOnOff(cmdInd, replytopic, pktID, projectId, excavatorID, machine.getStatus(), false, projectDevice.getUid(), 1, "终端主动发送");
                } else {
                    ProjectAppStatisticsByMachine appMachine = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, digging.getShift().getAlias(), nightStart, digging.getMaterialCode());
                    if (appMachine == null)
                        appMachine = new ProjectAppStatisticsByMachine();
                    appMachine.setProjectId(projectId);
                    appMachine.setWorkTime(appMachine.getWorkTime() + digging.getWorkTime());
                    appMachine.setShifts(digging.getShift());
                    appMachine.setMachineCode(digging.getMaterialCode());
                    appMachine.setCreateDate(nightStart);
                    projectAppStatisticsByMachineServiceI.save(appMachine);
                }
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/setDateIdentification")
    public Result setDateIdentification(HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getAllByProjectId(projectId);
            for (ProjectWorkTimeByDigging digging : diggingList) {
                if (digging.getDateIdentification() == null || digging.getDateIdentification().getTime() == 0) {
                    if (digging.getStartTime() != null) {
                        Date dateIdentification = DateUtils.createReportDateByMonth(digging.getStartTime());
                        digging.setDateIdentification(dateIdentification);
                        projectWorkTimeByDiggingServiceI.save(digging);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/test")
    public Result test(Integer status){
        String deviceId = "1990a0a8";
        String replytopic = "smartmining/excavator/cloud/" + deviceId + "/reply";
        String cmdInd = "onOff";
        Long pktID = 63L;
        Long projectId = 1L;
        Long machineId = 82L;
        boolean flag = true;
        Integer choose = 0;
        DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
        //handler.handleMessageOnOff(cmdInd, replytopic, pktID, projectId, machineId, status, flag, deviceId, choose, "终端主动发送");
        return Result.ok();
    }
}
