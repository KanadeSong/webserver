package com.seater.smartmining.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.entity.repository.ProjectRepository;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.manager.InterPhoneManager;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.DiggingMachineStatusJob;
import com.seater.smartmining.quartz.job.ReplyMachineScheduleInfoJob;
import com.seater.smartmining.quartz.job.ReplySlagCarPositionInfoJob;
import com.seater.smartmining.quartz.job.ReplySlagCarScheduleInfoJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.schedule.SmartminingScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 12:20
 */
@RestController
@RequestMapping("/api/projectschedule")
public class ProjectScheduleController {

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
    private ProjectModifyScheduleLogServiceI projectModifyScheduleLogServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private InterPhoneManager interPhoneManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private SmartminingScheduleService smartminingScheduleService;
    @Autowired
    private ProjectMqttParamsRequestServiceI projectMqttParamsRequestServiceI;
    @Autowired
    private WorkDateService workDateService;
    Long count = 0L;

    /**
     * @param current
     * @param pageSize
     * @param machineCode
     * @param carCode
     * @param pricingType
     * @param materialId
     * @param placeName
     * @param userName
     * @param request
     * @param sort        machineCode 挖机编号    distance 运距  排序
     * @param asc         顺序或者逆序
     * @return
     * @throws SmartminingProjectException
     */
    @RequestMapping("/query")
    @RequiresPermissions("appWorkPlan:query")
    public Object query(Integer current, Integer pageSize, String machineCode, String carCode, PricingTypeEnums pricingType, Integer materialId, String placeName, String userName, HttpServletRequest request, String sort, boolean asc) throws SmartminingProjectException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        //判断是查询全部还是筛选
        boolean flag = false;
        JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
        if (jsonArray == null)
            throw new SmartminingProjectException("该用户没有任何权限");
        if (jsonArray.contains(SmartminingConstant.ALLDATA))
            flag = true;
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        //返回的集合
        List<ScheduleResponse> responseList = new ArrayList<>();
        //总数量
        Long totalCount = 0L;
        if (StringUtils.isNotEmpty(carCode)) {
            Specification<ScheduleCar> specificationCar = new Specification<ScheduleCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ScheduleCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if (StringUtils.isNotEmpty(carCode))
                        list.add(criteriaBuilder.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                    list.add(criteriaBuilder.isTrue(root.get("isVaild")));
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ScheduleCar> carList = scheduleCarServiceI.query(specificationCar).getContent();
            if (carList.size() > 0) {
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, carList.get(0).getGroupCode());
                ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, carList.get(0).getGroupCode());
                if (!flag) {
                    //获取当前用户对象
                    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                    String params = "\"" + sysUser.getId() + "\"";
                    List<ProjectSchedule> projectScheduleList = projectScheduleServiceI.getAllByProjectIdAndManagerIdOrderById(projectId, params);
                    if (!projectScheduleList.contains(projectSchedule))
                        return Result.ok(responseList, 0L);
                }
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, carList.get(0).getGroupCode());
                ScheduleResponse response = new ScheduleResponse();
                response.setScheduleCarList(scheduleCarList);
                response.setScheduleMachineList(scheduleMachineList);
                response.setProjectSchedule(projectSchedule);
                responseList.add(response);
                totalCount = 1L;
            }
        } else {
            Specification<ScheduleMachine> specificationMachine = new Specification<ScheduleMachine>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ScheduleMachine> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if (StringUtils.isNotEmpty(machineCode))
                        list.add(criteriaBuilder.like(root.get("machineCode").as(String.class), "%" + machineCode + "%"));
                    if (pricingType != null)
                        list.add(criteriaBuilder.equal(root.get("pricingType").as(Integer.class), pricingType.getValue()));
                    if (materialId != null)
                        list.add(criteriaBuilder.equal(root.get("materialId").as(Long.class), materialId));
                    if (StringUtils.isNotEmpty(sort)) {
                        if (asc) {
                            query.orderBy(criteriaBuilder.asc(root.get(sort).as(String.class)));
                        } else {
                            query.orderBy(criteriaBuilder.desc(root.get(sort).as(String.class)));
                        }
                    }
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(criteriaBuilder.isTrue(root.get("isVaild")));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            Page<ScheduleMachine> scheduleMachinePage = scheduleMachineServiceI.query(specificationMachine, PageRequest.of(cur, page));
            List<ScheduleMachine> machineList = scheduleMachinePage.getContent();
            Map<String, Integer> machineMap = new LinkedHashMap<>();
            for (int i = 0; i < machineList.size(); i++) {
                machineMap.put(machineList.get(i).getGroupCode(), i);
            }
            List<ProjectSchedule> projectScheduleList = null;
            if (flag) {
                Specification<ProjectSchedule> specification = new Specification<ProjectSchedule>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectSchedule> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                        if (placeName != null)
                            list.add(criteriaBuilder.like(root.get("placeName").as(String.class), "%" + placeName + "%"));
                        if (userName != null) {
                            String params = "\"" + userName + "\"";
                            list.add(criteriaBuilder.like(root.get("managerName").as(String.class), "%" + params + "%"));
                        }
                        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                projectScheduleList = projectScheduleServiceI.getAllByQuery(specification);
            } else {
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                String params = "\"" + sysUser.getId() + "\"";
                projectScheduleList = projectScheduleServiceI.getAllByProjectIdAndManagerIdOrderById(projectId, params);
            }
            //生成排班信息索引
            Map<String, Integer> scheduleIndex = new HashMap<>();
            for (int i = 0; i < projectScheduleList.size(); i++) {
                scheduleIndex.put(projectScheduleList.get(i).getGroupCode(), i);
            }
            totalCount = Long.parseLong(String.valueOf(projectScheduleList.size()));
            if (StringUtils.isNotEmpty(machineCode))
                totalCount = 1L;
            for (Map.Entry<String, Integer> entry : machineMap.entrySet()) {
                String groupCode = entry.getKey();
                Integer index = scheduleIndex.get(groupCode);
                if (index != null) {
                    ScheduleResponse response = new ScheduleResponse();
                    List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode);
                    List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode);
                    response.setScheduleCarList(scheduleCarList);
                    response.setScheduleMachineList(scheduleMachineList);
                    response.setProjectSchedule(projectScheduleList.get(index));
                    responseList.add(response);
                }
            }
        }
        return Result.ok(responseList, totalCount);
    }

    /**
     * todo 优化中
     *
     * @param request
     * @param scheduleResponse
     * @return
     */
    @RequestMapping(value = "/save", produces = "application/json")
    @Transactional(rollbackFor = Exception.class)
    public Result saveNew(HttpServletRequest request, @RequestBody List<ScheduleResponse> scheduleResponse) throws IOException, SmartminingProjectException {
        String message = stringRedisTemplate.opsForValue().get("scheduleTask");
        if(StringUtils.isEmpty(message)) {
            stringRedisTemplate.opsForValue().set("scheduleTask", "allReady", 2 * 60, TimeUnit.SECONDS);
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            smartminingScheduleService.saveNewSchedule(projectId, scheduleResponse, 1);
            stringRedisTemplate.delete("scheduleTask");
            return Result.ok();
        }else{
            return Result.error("重复请求，请稍等重试");
        }
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result delete(HttpServletRequest request, @RequestBody List<Long> ids) throws IOException {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        /*String message = stringRedisTemplate.opsForValue().get("schedule" + projectId);
        if(StringUtils.isEmpty(message)) {
            stringRedisTemplate.opsForValue().set("schedule" + projectId, "allReady", 3 * 60, TimeUnit.SECONDS);*/
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            List<String> updateFalseByCar = new ArrayList<>();
            List<String> updateTrueByMachine = new ArrayList<>();
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date earlyStart = dateMap.get("start");
            Date dateIdentification = new Date();
            if(date.getTime() < earlyStart.getTime())
                dateIdentification = DateUtils.getAddDate(dateIdentification, -1);
            Shift shift = workDateService.getShift(date, projectId);
            for (Long id : ids) {
                ProjectSchedule schedule = projectScheduleServiceI.get(id);
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, schedule.getGroupCode());
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, schedule.getGroupCode());
                ProjectModifyScheduleLog log = new ProjectModifyScheduleLog();
                log.setProjectId(projectId);
                log.setBeforePlaceId(schedule.getPlaceId());
                log.setBeforePlaceName(schedule.getPlaceName());
                log.setBeforeManagerId(schedule.getManagerId());
                log.setBeforeManagerName(schedule.getManagerName());
                log.setBeforeGroupCode(schedule.getGroupCode());
                log.setDateIdentification(dateIdentification);
                log.setShift(shift);
                log.setBeforeMachineJson(JSON.toJSONString(scheduleMachineList));
                log.setBeforeCarJson(JSON.toJSONString(scheduleCarList));
                log.setModifyId(sysUser.getId());
                log.setModifyName(sysUser.getName());
                log.setModifyTime(new Date());
                log.setModifyEnum(ModifyEnum.DELETE);
                projectModifyScheduleLogServiceI.save(log);
                for (ScheduleMachine machine : scheduleMachineList) {
                    updateTrueByMachine.add(machine.getMachineCode());
                    ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, machine.getMachineCode(), ProjectDeviceType.DiggingMachineDevice.getAlian());
                    Long pktID = count;
                    String cron = QuartzConstant.MQTT_REPLY_CRON;
                    List<ProjectWorkTimeByDigging> diggingList = projectWorkTimeByDiggingServiceI.getByProjectIdAndMaterialIdByQuery(projectId, machine.getMachineId());
                    if (diggingList != null && diggingList.size() > 0) {
                        ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machine.getMachineId());
                        projectDiggingMachine.setEndWorkTime(date);
                        projectDiggingMachine.setStatus(DiggingMachineStatus.Stop);
                        ProjectWorkTimeByDigging digging = diggingList.get(0);
                        digging.setEndTime(date);
                        digging.setStatus(DiggingMachineStatus.Stop);
                        Long workTime = DateUtils.calculationHour(digging.getStartTime(), digging.getEndTime());
                        digging.setWorkTime(workTime);
                        projectDiggingMachineServiceI.save(projectDiggingMachine);
                        projectWorkTimeByDiggingServiceI.save(digging);
                        if (projectDevice != null) {
                            if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                                String cmdInd = "onOff";
                                String replytopic = "smartmining/excavator/cloud/" + projectDevice.getUid() + "/request";
                                Long excavatorID = machine.getMachineId();
                                Integer status = 0;
                                JobDataMap jobDataMap = new JobDataMap();
                                jobDataMap.put("cmdInd", cmdInd);
                                jobDataMap.put("topic", replytopic);
                                jobDataMap.put("pktId", pktID);
                                jobDataMap.put("machineId", excavatorID);
                                jobDataMap.put("status", status);
                                jobDataMap.put("projectId", log.getProjectId());
                                jobDataMap.put("deviceId", projectDevice.getUid());
                                jobDataMap.put("choose", 1);
                                jobDataMap.put("createId", sysUser.getId());
                                jobDataMap.put("createName", sysUser.getAccount());
                                quartzManager.addJob(QuartzManager.createJobNameMachineWork(excavatorID), DiggingMachineStatusJob.class, cron, jobDataMap);
                                Integer requestCountTwo = 0;
                                stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_WORK + excavatorID, requestCountTwo.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                                ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                                paramsRequest.setProjectId(projectId);
                                paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                                paramsRequest.setRequest("/api/projectschedule/delete");
                                paramsRequest.setRequestParams(JSON.toJSONString(machine));
                                paramsRequest.setCreateTime(new Date());
                                projectMqttParamsRequestServiceI.save(paramsRequest);
                            }
                        }
                    }
                    if (projectDevice != null) {
                        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                            //发送数据到设备
                            String cmdInd = "schedule";
                            Long excavatorID = machine.getMachineId();
                            String replytopic = "smartmining/excavator/cloud/" + projectDevice.getUid() + "/request";
                            JobDataMap jobDataMap = new JobDataMap();
                            jobDataMap.put("cmdInd", cmdInd);
                            jobDataMap.put("pktId", pktID);
                            jobDataMap.put("topic", replytopic);
                            jobDataMap.put("machineId", excavatorID);
                            jobDataMap.put("projectId", projectId);
                            jobDataMap.put("deviceId", projectDevice.getUid());
                            quartzManager.addJob(QuartzManager.createJobNameScheduleMachine(projectDevice.getUid()), ReplyMachineScheduleInfoJob.class, cron, jobDataMap);
                            Integer requestCount = 0;
                            stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_MACHINE_SCHEDULE + projectDevice.getUid(), requestCount.toString(), 10 * 60 * 60, TimeUnit.SECONDS);
                            count++;
                            ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                            paramsRequest.setProjectId(projectId);
                            paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                            paramsRequest.setRequest("/api/projectschedule/delete");
                            paramsRequest.setRequestParams(JSON.toJSONString(machine));
                            paramsRequest.setCreateTime(new Date());
                            projectMqttParamsRequestServiceI.save(paramsRequest);
                        }
                    }
                }
                for (ScheduleCar car : scheduleCarList) {
                    updateFalseByCar.add(car.getCarCode());
                    ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, car.getCarCode(), ProjectDeviceType.SlagTruckDevice.getAlian());
                    if (projectDevice != null) {
                        if (projectDevice.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
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
                            count++;
                            ProjectMqttParamsRequest paramsRequest = new ProjectMqttParamsRequest();
                            paramsRequest.setProjectId(projectId);
                            paramsRequest.setMattParams(JSON.toJSONString(jobDataMap));
                            paramsRequest.setRequest("/api/projectschedule/delete");
                            paramsRequest.setRequestParams(JSON.toJSONString(car));
                            paramsRequest.setCreateTime(new Date());
                            projectMqttParamsRequestServiceI.save(paramsRequest);
                        }
                    }
                }
                scheduleMachineServiceI.deleteByProjectIdAndGroupCode(projectId, schedule.getGroupCode());
                scheduleCarServiceI.deleteByProjectIdAndGroupCode(projectId, schedule.getGroupCode());
                projectScheduleServiceI.delete(id);
            }
            if (updateTrueByMachine.size() > 0)
                projectDiggingMachineServiceI.updateSeleted(false, updateTrueByMachine);
            if (updateFalseByCar.size() > 0)
                projectCarServiceI.updateSeleted(false, updateFalseByCar);
            //对讲组同步
            for (Long id : ids) {
                ThreadUtil.execAsync(() -> interPhoneManager.deleteSchedule(id));
            }
            //stringRedisTemplate.delete("schedule" + projectId);
            return Result.ok();
        /*}else{
            return Result.error("服务器繁忙，请稍后再试");
        }*/
    }

    public static void main(String[] args) {
        List<ScheduleResponse> scheduleResponse = new ArrayList<>();
        ScheduleResponse response = new ScheduleResponse();
        ProjectSchedule projectSchedule = new ProjectSchedule();
        projectSchedule.setId(2248L);
        projectSchedule.setProjectId(1L);
        projectSchedule.setManagerId("[\"201913\"]");
        projectSchedule.setManagerName("[\"许惠芳\"]");
        projectSchedule.setPlaceId(11L);
        projectSchedule.setPlaceName("2号");
        projectSchedule.setSlagSiteId("[\"12\",\"16\"]");
        projectSchedule.setSlagSiteName("[\"lam1\",\"2号\"]");
        projectSchedule.setDeviceStartStatus(DeviceStartStatusEnum.DiggingMachine);
        projectSchedule.setDispatchMode(ProjectDispatchMode.GroupMixture);
        projectSchedule.setScheduleCode("S100001");
        projectSchedule.setGroupCode("efea1cce-4622-4485-9170-837192b226321");
        response.setProjectSchedule(projectSchedule);

        List<ScheduleMachine> scheduleMachineList = new ArrayList<>();
        ScheduleMachine scheduleMachine1 = new ScheduleMachine();
        scheduleMachine1.setProjectId(1L);
        scheduleMachine1.setMachineId(2L);
        scheduleMachine1.setMachineCode("0058");
        scheduleMachine1.setPricingType(PricingTypeEnums.Hour);
        scheduleMachine1.setMaterialId(1L);
        scheduleMachine1.setMaterialName("土");
        scheduleMachine1.setDistance(600L);
        scheduleMachine1.setDiggingMachineBrandId(1L);
        scheduleMachine1.setDiggingMachineBrandName("沃乐沃");
        scheduleMachine1.setDiggingMachineModelId(7L);
        scheduleMachine1.setDiggingMachineModelName("480");
        scheduleMachine1.setDiggingMachineOwnerId(0L);
        scheduleMachine1.setDiggingMachineOwnerName("张澎冲");
        scheduleMachine1.setGroupCode("S100001");
        scheduleMachine1.setFault(false);
        scheduleMachine1.setIsVaild(true);
        scheduleMachine1.setGroupCode("efea1cce-4622-4485-9170-837192b226321");

        ScheduleMachine scheduleMachine2 = new ScheduleMachine();
        scheduleMachine2.setProjectId(1L);
        scheduleMachine2.setMachineId(4L);
        scheduleMachine2.setMachineCode("0050");
        scheduleMachine2.setPricingType(PricingTypeEnums.Cube);
        scheduleMachine2.setMaterialId(1L);
        scheduleMachine2.setMaterialName("土");
        scheduleMachine2.setDistance(600L);
        scheduleMachine2.setDiggingMachineBrandId(1L);
        scheduleMachine2.setDiggingMachineBrandName("沃乐沃");
        scheduleMachine2.setDiggingMachineModelId(7L);
        scheduleMachine2.setDiggingMachineModelName("480");
        scheduleMachine2.setDiggingMachineOwnerId(0L);
        scheduleMachine2.setDiggingMachineOwnerName("张澎冲");
        scheduleMachine2.setGroupCode("S100001");
        scheduleMachine2.setFault(false);
        scheduleMachine2.setIsVaild(true);
        scheduleMachine2.setGroupCode("efea1cce-4622-4485-9170-837192b226321");
        scheduleMachineList.add(scheduleMachine1);
        scheduleMachineList.add(scheduleMachine2);

        List<ScheduleCar> scheduleCarList = new ArrayList<>();
        ScheduleCar scheduleCar1 = new ScheduleCar();
        scheduleCar1.setProjectId(1L);
        scheduleCar1.setCarId(9L);
        scheduleCar1.setCarCode("0370");
        scheduleCar1.setGroupCode("S100001");
        scheduleCar1.setCarBrandId(11L);
        scheduleCar1.setCarBrandName("陕汽德龙");
        scheduleCar1.setCarModelId(24L);
        scheduleCar1.setCarModelName("F300");
        scheduleCar1.setCarOwnerId(0L);
        scheduleCar1.setCarOwnerName("刘小龙");
        scheduleCar1.setIsVaild(true);
        scheduleCar1.setFault(false);
        scheduleCar1.setGroupCode("efea1cce-4622-4485-9170-837192b226321");

        ScheduleCar scheduleCar2 = new ScheduleCar();
        scheduleCar2.setProjectId(1L);
        scheduleCar2.setCarId(10L);
        scheduleCar2.setCarCode("0371");
        scheduleCar2.setGroupCode("S100001");
        scheduleCar2.setCarBrandId(11L);
        scheduleCar2.setCarBrandName("陕汽德龙");
        scheduleCar2.setCarModelId(24L);
        scheduleCar2.setCarModelName("F300");
        scheduleCar2.setCarOwnerId(0L);
        scheduleCar2.setCarOwnerName("刘小龙");
        scheduleCar2.setIsVaild(true);
        scheduleCar2.setFault(false);
        scheduleCar2.setGroupCode("efea1cce-4622-4485-9170-837192b226321");

        ScheduleCar scheduleCar3 = new ScheduleCar();
        scheduleCar3.setProjectId(1L);
        scheduleCar3.setCarId(1149651L);
        scheduleCar3.setCarCode("0156");
        scheduleCar3.setGroupCode("S100001");
        scheduleCar3.setCarBrandId(11L);
        scheduleCar3.setCarBrandName("陕汽德龙");
        scheduleCar3.setCarModelId(24L);
        scheduleCar3.setCarModelName("F300");
        scheduleCar3.setCarOwnerId(0L);
        scheduleCar3.setCarOwnerName("刘小龙");
        scheduleCar3.setIsVaild(true);
        scheduleCar3.setFault(false);
        scheduleCar3.setGroupCode("efea1cce-4622-4485-9170-837192b226321");

        scheduleCarList.add(scheduleCar1);
        scheduleCarList.add(scheduleCar2);
        scheduleCarList.add(scheduleCar3);
        response.setProjectSchedule(projectSchedule);
        response.setScheduleMachineList(scheduleMachineList);
        response.setScheduleCarList(scheduleCarList);
        scheduleResponse.add(response);
        String text = JSON.toJSONString(scheduleResponse);
        System.out.println(text);
    }
}
