package com.seater.smartmining.schedule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.constant.WechatConstant;
import com.seater.smartmining.domain.SendModelMessage;
import com.seater.smartmining.domain.SendServiceMessage;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.enums.TimeTypeEnum;
import com.seater.smartmining.enums.VaildEnums;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.manager.InterPhoneManager;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.other.WechatService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/12 0012 17:49
 */
@Component
@Slf4j
public class SpringSchedule {

    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private ProjectAppStatisticsLogServiceI projectAppStatisticsLogServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    private ProjectAppStatisticsByCarServiceI projectAppStatisticsByCarServiceI;
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private MatchingDegreeServiceI matchingDegreeServiceI;
    @Autowired
    private SlagCarServiceI slagCarServiceI;
    @Autowired
    ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;
    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;
    @Autowired
    ProjectDeviceStatusLogServiceI projectDeviceStatusLogServiceI;
    @Autowired
    ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    WechatService wechatService;
    @Autowired
    SysUserServiceI sysUserServiceI;
    @Autowired
    InterPhoneManager interPhoneManager;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private SmartminingExceptionService smartminingExceptionService;
    @Autowired
    private ProjectRunningTrajectoryLogServiceI projectRunningTrajectoryLogServiceI;
    @Autowired
    private ProjectUserTrajectoryLogServiceI projectUserTrajectoryLogServiceI;

    ValueOperations<String, String> valueOps = null;

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }

    Long count = 0L;

    /**
     * todo 待测试
     * 检查车辆是否已经快要到期。
     */
    /*@Scheduled(cron = "0 0 7 * * ?")
    public void checkDeviceExpireDate() throws IOException {
        List<Project> projectList = projectServiceI.getAll();
        for(Project project : projectList) {
            List<SlagCar> slagCarList = slagCarServiceI.getAllByProjectId(project.getId());
            Date date = new Date();
            for (SlagCar car : slagCarList) {
                Date expireDate = car.getExpireDate();
                Long second = DateUtils.calculationHour(date, expireDate);
                Long day = second / (60 * 60 * 24);
                if (day.compareTo(0L) == 0 || day.compareTo(1L) == 0 || day.compareTo(3L) == 0 || day.compareTo(7L) == 0 || day.compareTo(15L) == 0 || day.compareTo(30L) == 0) {
                    SysUser user = sysUserServiceI.get(car.getOwnerId());
                    SendServiceMessage sendServiceMessage = new SendServiceMessage();
                    SendModelMessage message = new SendModelMessage();
                    sendServiceMessage.setTouser(user.getOpenId());
                    if (StringUtils.isNotEmpty(car.getPrepayId()))
                        message.setForm_id(car.getPrepayId());
                    message.setTemplate_id(WechatConstant.WX_RENEW_MODEL_NO);
                    //Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
                    Map<String, Map<String, String>> keyWord = new HashMap<>();
                    Map<String, String> value01 = new HashMap<>();
                    value01.put("value", user.getName());
                    keyWord.put("keyword1", value01);
                    Map<String, String> value02 = new HashMap<>();
                    value02.put("value", "车辆编号：" + car.getCode() + "使用服务");
                    keyWord.put("keyword2", value02);
                    Map<String, String> value03 = new HashMap<>();
                    value03.put("value", DateUtils.formatDateByPattern(car.getExpireDate(), SmartminingConstant.DATEFORMAT));
                    keyWord.put("keyword3", value03);
                    Map<String, String> value04 = new HashMap<>();
                    value04.put("value", "未避免您的正常使用，请在到期前及时续费");
                    keyWord.put("keyword4", value04);
                    //data.put("data", keyWord);
                    //String json = JSON.toJSONString(data);
                    message.setData(keyWord);
                    sendServiceMessage.setWeapp_template_msg(message);
                    wechatService.sendServiceMessage(sendServiceMessage);
                }
                if(second <= 0){
                    car.setDeducted(false);
                    projectCarServiceI.save(car);
                }
            }
        }
    }*/
    @Scheduled(cron = "0 0 7 * * ?")
    public void checkDeviceExpireDate() throws IOException {
        List<Project> projectList = projectServiceI.getAll();
        for (Project project : projectList) {
            List<SlagCar> slagCarList = slagCarServiceI.getAllByProjectId(project.getId());
            Date date = new Date();
            for (SlagCar car : slagCarList) {
                if (StringUtils.isEmpty(car.getCodeInProject()))
                    continue;
                Date expireDate = car.getExpireDate();
                Long second = DateUtils.calculationHour(date, expireDate);
                Long day = second / (60 * 60 * 24);
                if (day.compareTo(0L) == 0 || day.compareTo(1L) == 0 || day.compareTo(3L) == 0 || day.compareTo(7L) == 0 || day.compareTo(15L) == 0 || day.compareTo(30L) == 0) {
                    SysUser user = sysUserServiceI.get(car.getOwnerId());
                    SendServiceMessage sendServiceMessage = new SendServiceMessage();
                    SendModelMessage message = new SendModelMessage();
                    sendServiceMessage.setTouser(user.getOpenId());
                    if (StringUtils.isNotEmpty(car.getPrepayId()))
                        message.setForm_id(car.getPrepayId());
                    message.setTemplate_id(WechatConstant.WX_RENEW_MODEL_NO);
                    //Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
                    Map<String, Map<String, String>> keyWord = new HashMap<>();
                    Map<String, String> value01 = new HashMap<>();
                    value01.put("value", user.getName());
                    keyWord.put("keyword1", value01);
                    Map<String, String> value02 = new HashMap<>();
                    value02.put("value", "车辆编号：" + car.getCodeInProject() + "使用服务");
                    keyWord.put("keyword2", value02);
                    Map<String, String> value03 = new HashMap<>();
                    value03.put("value", DateUtils.formatDateByPattern(car.getExpireDate(), SmartminingConstant.DATEFORMAT));
                    keyWord.put("keyword3", value03);
                    Map<String, String> value04 = new HashMap<>();
                    value04.put("value", "未避免您的正常使用，请在到期前及时续费");
                    keyWord.put("keyword4", value04);
                    //data.put("data", keyWord);
                    //String json = JSON.toJSONString(data);
                    message.setData(keyWord);
                    sendServiceMessage.setWeapp_template_msg(message);
                    wechatService.sendServiceMessage(sendServiceMessage);
                }
                if (second <= 0) {
                    car.setDeducted(false);
                    slagCarServiceI.save(car);
                }
            }
            /*List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdAndIsVaild(project.getId(), true);
            for (ProjectCar car : projectCarList) {
                Date expireDate = car.getExpireDate();
                Long second = DateUtils.calculationHour(date, expireDate);
                Long day = second / (60 * 60 * 24);
                if (day.compareTo(0L) == 0 || day.compareTo(1L) == 0 || day.compareTo(3L) == 0 || day.compareTo(7L) == 0 || day.compareTo(15L) == 0 || day.compareTo(30L) == 0) {
                    SysUser user = sysUserServiceI.get(car.getOwnerId());
                    SendServiceMessage sendServiceMessage = new SendServiceMessage();
                    SendModelMessage message = new SendModelMessage();
                    sendServiceMessage.setTouser(user.getOpenId());
                    if (StringUtils.isNotEmpty(car.getPrepayId()))
                        message.setForm_id(car.getPrepayId());
                    message.setTemplate_id(WechatConstant.WX_RENEW_MODEL_NO);
                    //Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
                    Map<String, Map<String, String>> keyWord = new HashMap<>();
                    Map<String, String> value01 = new HashMap<>();
                    value01.put("value", user.getName());
                    keyWord.put("keyword1", value01);
                    Map<String, String> value02 = new HashMap<>();
                    value02.put("value", "车辆编号：" + car.getCode() + "使用服务");
                    keyWord.put("keyword2", value02);
                    Map<String, String> value03 = new HashMap<>();
                    value03.put("value", DateUtils.formatDateByPattern(car.getExpireDate(), SmartminingConstant.DATEFORMAT));
                    keyWord.put("keyword3", value03);
                    Map<String, String> value04 = new HashMap<>();
                    value04.put("value", "未避免您的正常使用，请在到期前及时续费");
                    keyWord.put("keyword4", value04);
                    //data.put("data", keyWord);
                    //String json = JSON.toJSONString(data);
                    message.setData(keyWord);
                    sendServiceMessage.setWeapp_template_msg(message);
                    wechatService.sendServiceMessage(sendServiceMessage);
                }
                if(second <= 0){
                    car.setDeducted(false);
                    projectCarServiceI.save(car);
                }
            }*/
        }
    }


    @Scheduled(fixedRate = 1000 * 60L)
    public void loopProject() {
        List<Project> projectList = projectServiceI.getAll();
        for (Project project : projectList) {
            Callable callable = () -> {
                try {
                    calShiftStaticData(project);
                    return Constants.THREAD_RUN_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("线程执行出错..,{}", Thread.currentThread().getName());
                    return Constants.THREAD_RUN_FAILED;
                }
            };
            ThreadUtil.execAsync(callable);
        }
    }

    @Scheduled(fixedRate = 1000 * 60L * 2)
    public void loopProject2Minute() {
        List<Project> projectList = projectServiceI.getAll();
        for (Project project : projectList) {
            Callable callable = () -> {
                try {
                    log.info("计算项目:{}   >>>>>   id:{}", project.getName(), project.getId());

                    Result diggingMachineRankDay = diggingMachineRank(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), SmartminingConstant.EN_DAY, project.getId());
                    log.debug("diggingMachineRankDay   计算并缓存当天:{}", JSONObject.toJSONString(diggingMachineRankDay));

                    Result diggingMachineRankWeek = diggingMachineRank(DateUtil.beginOfWeek(new Date()), DateUtil.endOfWeek(new Date()), SmartminingConstant.EN_WEEK, project.getId());
                    log.debug("diggingMachineRankWeek   计算并缓存本周:{}", JSONObject.toJSONString(diggingMachineRankWeek));

                    Result diggingMachineRankMonth = diggingMachineRank(DateUtil.beginOfMonth(new Date()), DateUtil.endOfMonth(new Date()), SmartminingConstant.EN_MONTH, project.getId());
                    log.debug("diggingMachineRankMonth   计算并缓存当月:{}", JSONObject.toJSONString(diggingMachineRankMonth));


                    Result projectCarRankDay = projectCarRank(DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()), SmartminingConstant.EN_DAY, project.getId());
                    log.debug("projectCarRankDay   计算并缓存当天:{}", JSONObject.toJSONString(projectCarRankDay));


                    Result projectCarRankWeek = projectCarRank(DateUtil.beginOfWeek(new Date()), DateUtil.endOfWeek(new Date()), SmartminingConstant.EN_WEEK, project.getId());
                    log.debug("projectCarRankWeek   计算并缓存本周:{}", JSONObject.toJSONString(projectCarRankWeek));


                    Result projectCarRankMonth = projectCarRank(DateUtil.beginOfMonth(new Date()), DateUtil.endOfMonth(new Date()), SmartminingConstant.EN_MONTH, project.getId());
                    log.debug("projectCarRankMonth   计算并缓存当月:{}", JSONObject.toJSONString(projectCarRankMonth));
                    log.info("项目:{}     >>>>>   已完成计算...", project.getName());
                    return Constants.THREAD_RUN_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("线程执行出错..,{}", Thread.currentThread().getName());
                    return Constants.THREAD_RUN_FAILED;
                }
            };
            ThreadUtil.execAsync(callable);
        }
    }

    /**
     * 计算项目的当班即时数据
     *
     * @param project
     * @return
     */
    public synchronized Result calShiftStaticData(Project project) {
        try {
            Long projectId = project.getId();
            Date reportDate = new Date();
            /*String redisJosn = stringRedisTemplate.opsForValue().get("app:statisticslogInfo:" + projectId);
            /*if (StringUtils.isNotEmpty(redisJosn)) {
                ProjectAppStatisticsLog log = JSONObject.parseObject(redisJosn, ProjectAppStatisticsLog.class);
                return Result.ok(log);
            }*/
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, reportDate);
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            Date nightStartTime = dateMap.get("nightStart");
            Date nightEndTime = dateMap.get("end");
            if (reportDate.getTime() < earlyStartTime.getTime()) {
                earlyStartTime = DateUtils.subtractionOneDay(earlyStartTime);
                earlyEndTime = DateUtils.subtractionOneDay(earlyEndTime);
                nightStartTime = DateUtils.subtractionOneDay(nightStartTime);
                nightEndTime = DateUtils.subtractionOneDay(nightEndTime);
            }
            Date earlyStart = earlyStartTime;
            Date earlyEnd = earlyEndTime;
            ShiftsEnums shifts = ShiftsEnums.UNKNOW;
            if (reportDate.getTime() >= earlyStartTime.getTime() && reportDate.getTime() <= earlyEndTime.getTime()) {
                shifts = ShiftsEnums.DAYSHIFT;
            } else {
                shifts = ShiftsEnums.BLACKSHIFT;
            }
            //查询当前班次出勤的所有挖机信息
            Integer value = ShiftsEnums.getValue(shifts);
            //获取所有渣车信息
            List<ProjectAppStatisticsByCar> projectAppStatisticsByCars = projectAppStatisticsByCarServiceI.getAllByProjectIdAndShiftAndCreateDate(projectId, value, nightStartTime);
            //获取所有挖机信息
            List<ProjectAppStatisticsByMachine> projectAppStatisticsByMachines = projectAppStatisticsByMachineServiceI.getAllByProjectIdAndShiftsAndCreateDate(projectId, value, nightStartTime);
            //获取正在工作中的所有挖机信息
            List<ProjectWorkTimeByDigging> projectWorkTimeByDiggingList = projectWorkTimeByDiggingServiceI.getByProjectIdByQuery(projectId, nightStartTime);
            //获取当前班次有异常数据的信息
            List<ProjectMqttCardReport> unPassList = projectMqttCardReportServiceI.getAllByProjectIdAndDateIdentificationAndShift(projectId, nightStartTime, value);
            //获取当前班次渣车出勤数量
            List<Map> countList = projectCarWorkInfoServiceI.getCountByProjectIdAndShiftAndDate(projectId, value, nightStartTime);
            List<String> countCarCodeList = new ArrayList<>();
            for (int i = 0; i < countList.size(); i++) {
                countCarCodeList.add(countList.get(i).get("car_code").toString());
            }
            ProjectAppStatisticsLog log = new ProjectAppStatisticsLog();
            log.setProjectId(projectId);
            log.setShifts(shifts);
            log.setExceptionCount(unPassList.size());
            //总时长
            Long totalTime = 0L;
            if (true) {
                //渣车总注册数
                Map carsCountMap = projectCarServiceI.getCarsCountByProjectId(projectId);
                //挖机总注册数
                Map diggingCountMap = projectDiggingMachineServiceI.getAllCountByProjectId(projectId);
                for (ProjectAppStatisticsByCar car : projectAppStatisticsByCars) {
                    //总方量
                    log.setTotalCubic(log.getTotalCubic() + car.getCubic());
                    //总车数
                    log.setWorkOnCount(log.getWorkOnCount() + car.getCarCount());
                }
                log.setDiggingMachineOnLineCount(Integer.valueOf(diggingCountMap.get("count").toString()));
                log.setCarOnLineCount(Integer.valueOf(carsCountMap.get("count").toString()));
                for (ProjectAppStatisticsByMachine machine : projectAppStatisticsByMachines) {
                    totalTime = totalTime + machine.getWorkTime();
                }
                for (ProjectWorkTimeByDigging digging : projectWorkTimeByDiggingList) {
                    if (digging.getStartTime() != null) {
                        Long time = DateUtils.calculationHour(digging.getStartTime(), new Date());
                        totalTime = totalTime + time;
                    }
                }
                //渣车出勤数
                log.setCarAttendanceCount(countCarCodeList.size());
                //挖机出勤数
                log.setDiggingMachineAttendanceCount(projectAppStatisticsByMachines.size());
            }
            BigDecimal carPercent = new BigDecimal(0);
            if (log.getCarOnLineCount() != 0) {
                carPercent = new BigDecimal((float) log.getCarAttendanceCount() / log.getCarOnLineCount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            log.setCarPercent(carPercent);
            //挖机开工率
            BigDecimal diggingMachinePercent = new BigDecimal(0);
            if (log.getDiggingMachineOnLineCount() != 0) {
                diggingMachinePercent = new BigDecimal((float) log.getDiggingMachineAttendanceCount() / log.getDiggingMachineOnLineCount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            log.setDiggingMachinePercent(diggingMachinePercent);
            //装载/车
            BigDecimal avgCars = totalTime != null && totalTime != 0 ? new BigDecimal((float) log.getWorkOnCount() / ((float) totalTime / 3600)).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //平均装载/小时
            BigDecimal avgCubicB = totalTime != null && totalTime != 0 ? new BigDecimal(((float) log.getTotalCubic() / 1000000L) / ((float) totalTime / 3600)) : new BigDecimal(0L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Long avgCubic = avgCubicB.longValue();
            log.setAvgCubic(avgCubic);
            log.setAvgCars(avgCars);
            log.setCreateDate(new Date());
            log.setTotalTime(totalTime);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            log.setReportDate(reportDate);

            Long startTime = System.currentTimeMillis();
            System.out.println("开始:" + startTime);
            Date start = DateUtil.beginOfDay(reportDate);
            Date end = DateUtil.endOfDay(reportDate);
            //查出当天全部的渣车工作信息
            Specification<ProjectCarWorkInfo> specWorkInfo = new Specification<ProjectCarWorkInfo>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    Shift shifts = Shift.Unknown;
                    if (new Date().getTime() >= earlyStart.getTime() && new Date().getTime() <= earlyEnd.getTime()) {
                        shifts = Shift.Early;
                    } else {
                        shifts = Shift.Night;
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(cb.equal(root.get("shift").as(Shift.class), shifts));
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), start, end));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.queryAllByParams(specWorkInfo);

            //项目材料
            List<ProjectMaterial> materialList = projectMaterialServiceI.getByProjectIdOrderById(projectId);

            //未卸载
            Integer unLoadCount = 0;
            //待检验
            Integer toCheckCount = 0;
            //完成车数
            Integer finishCount = 0;
            //里程数 取完成的车状态和有效数据
            Long totalDistance = 0L;
            //装载材料车数/方数（根据材料）
            List<JSONObject> materialLoadList = new ArrayList<>();

            for (ProjectCarWorkInfo workInfo : workInfoList) {
                if (workInfo.getStatus().equals(ProjectCarWorkStatus.UnUnload)) {
                    unLoadCount = unLoadCount + 1;
                }
                if (workInfo.getStatus().equals(ProjectCarWorkStatus.UnCheck)) {
                    toCheckCount = toCheckCount + 1;
                }
                if (workInfo.getStatus().equals(ProjectCarWorkStatus.Finish)) {
                    finishCount = finishCount + 1;
                    totalDistance = totalDistance + workInfo.getPayableDistance();

                    JSONObject materialLoad = new JSONObject();
                    for (ProjectMaterial material : materialList) {
                        if (null != workInfo.getMaterialId() && workInfo.getMaterialId().equals(material.getId())) {
                            materialLoad.put("materialId", workInfo.getMaterialId());
                            materialLoad.put("perMaterial", workInfo.getCubic());
                            materialLoad.put("carId", workInfo.getCarId());
                            materialLoad.put("carCode", workInfo.getCarCode());
                            materialLoadList.add(materialLoad);
                        }
                    }
                }
            }

            List<JSONObject> resultLoadList = new ArrayList<>();
            for (ProjectMaterial materialObject : materialList) {
                JSONObject resultLoad = new JSONObject();
                //材料方数
                Long perMaterial = 0L;
                //车数
                Integer totalCount = 0;
                for (JSONObject material : materialLoadList) {
                    if (materialObject.getId().equals(material.getLong("materialId"))) {
                        perMaterial = perMaterial + material.getLong("perMaterial");
                        totalCount = totalCount + 1;
                    }
                }
                resultLoad.put("materialId", materialObject.getId());
                resultLoad.put("materialName", materialObject.getName());
                resultLoad.put("perMaterial", perMaterial);
                resultLoad.put("totalCount", totalCount);
                //装载材料车数/方数（根据材料）
                BigDecimal carPerCube = new BigDecimal(0);
                if (perMaterial != 0L) {
                    carPerCube = new BigDecimal(totalCount).divide(new BigDecimal(perMaterial).divide(new BigDecimal(1000000), 9, RoundingMode.HALF_UP), 9, RoundingMode.HALF_UP);
                }
                resultLoad.put("carPerCube", carPerCube);
                resultLoadList.add(resultLoad);
            }

            log.setCarPerCube(resultLoadList);
            log.setUnLoadCount(unLoadCount);
            log.setToCheckCount(toCheckCount);
            log.setFinishCount(finishCount);
            log.setMileCount(totalDistance);

            BigDecimal passPercent = new BigDecimal(0);
            if (log.getWorkOnCount() != 0L) {
                //合格率 = 1-(异常车数/装载车数)
                passPercent = new BigDecimal(1).subtract(new BigDecimal(log.getExceptionCount()).divide(new BigDecimal(log.getWorkOnCount()), 9, RoundingMode.HALF_UP));
            }
            log.setPassPercent(passPercent);

            //当班加油量
            Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    ShiftsEnums shiftsLog = ShiftsEnums.UNKNOW;
                    if (new Date().getTime() >= earlyStart.getTime() && new Date().getTime() <= earlyEnd.getTime()) {
                        shiftsLog = ShiftsEnums.DAYSHIFT;
                    } else {
                        shiftsLog = ShiftsEnums.BLACKSHIFT;
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(cb.equal(root.get("shifts").as(ShiftsEnums.class), shiftsLog));
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), start, end));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarFillLog> fillLogList = projectCarFillLogServiceI.queryWx(spec);
            Long fillTotal = 0L;
            for (ProjectCarFillLog fillLog : fillLogList) {
                fillTotal = fillTotal + fillLog.getVolumn();
            }
            log.setShiftFill(fillTotal);
            System.out.println("结束:" + (System.currentTimeMillis() - startTime));
            stringRedisTemplate.opsForValue().set(SmartminingConstant.CAL_SHIFT_STATIC_DATA + ":" + projectId, JSONObject.toJSONString(log), 60, TimeUnit.SECONDS);
            return Result.ok(log);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 渣车排行榜
     *
     * @param startDate
     * @param endDate
     * @param type
     * @param projectId
     * @return
     */
    public synchronized Result projectCarRank(Date startDate, Date endDate, String type, Long projectId) {


        //项目中所有有效渣车
        Specification<ProjectCar> specProjectCar = new Specification<ProjectCar>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCar> projectCarList = projectCarServiceI.queryWx(specProjectCar);

        //时间段内渣车工作信息
        Specification<ProjectCarWorkInfo> spec = new Specification<ProjectCarWorkInfo>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                //合格
                list.add(cb.equal(root.get("pass").as(Score.class), Score.Pass));
                //不是渣车无效
                list.add(cb.notEqual(root.get("isVaild").as(VaildEnums.class), VaildEnums.NOTVAILDBYCAR));
                //不是渣车和挖机无效
                list.add(cb.notEqual(root.get("isVaild").as(VaildEnums.class), VaildEnums.BOTHNOTVALID));
                list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.queryAllByParams(spec);

        //时间段内给渣车的加油历史
        Specification<ProjectCarFillLog> specFillLog = new Specification<ProjectCarFillLog>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("carType").as(CarType.class), CarType.SlagCar));
                list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarFillLog> fillLogList = projectCarFillLogServiceI.queryWx(specFillLog);

        //**********计算渣车装载车数排行榜**********
        List<JSONObject> loadRankList = new ArrayList<>();
        //**********渣车油耗比排行榜**********
        List<JSONObject> oilConsumptionRankList = new ArrayList<>();
        for (ProjectCar projectCar : projectCarList) {
            JSONObject loadRank = new JSONObject();
            loadRank.put("code", projectCar.getCode());
            loadRank.put("owner", projectCar.getOwnerName());
            //装载车数
            Integer count = 0;


            JSONObject oilConsumptionRank = new JSONObject();
            oilConsumptionRank.put("code", projectCar.getCode());
            oilConsumptionRank.put("owner", projectCar.getOwnerName());
            //总金额
            Long totalAmount = 0L;
            //总加油金额
            Long totalFillAmount = 0L;

            for (ProjectCarWorkInfo projectCarWorkInfo : workInfoList) {
                if (null != projectCarWorkInfo.getCarId() && projectCarWorkInfo.getCarId().equals(projectCar.getId())) {
                    count++;
                    totalAmount = totalAmount + projectCarWorkInfo.getAmount();
                }
            }
            loadRank.put("totalCount", count);
            loadRankList.add(loadRank);


            //计算总加油金额
            for (ProjectCarFillLog fillLog : fillLogList) {
                if (null != fillLog.getCarId() && fillLog.getCarId().equals(projectCar.getId())) {
                    totalFillAmount = totalFillAmount + fillLog.getAmount();
                }
            }

            BigDecimal decimalTotalAmount = new BigDecimal(totalAmount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal decimalTotalFillAmount = new BigDecimal(totalFillAmount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal oilConsumption = new BigDecimal(0L);
            oilConsumptionRank.put("totalAmount", decimalTotalAmount);
            oilConsumptionRank.put("totalFillAmount", decimalTotalFillAmount);
            if (decimalTotalAmount.doubleValue() != new BigDecimal(0).doubleValue()) {
                //计算百分比
                oilConsumption = decimalTotalFillAmount.divide(decimalTotalAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            oilConsumptionRank.put("oilConsumption", oilConsumption);
            oilConsumptionRankList.add(oilConsumptionRank);

        }
        //渣车装载车数排行榜end


        //排序
        //渣车装载车数排行榜排序
        Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getLong("totalCount").compareTo(o1.getLong("totalCount"));
            }
        };
        loadRankList = CollUtil.sortPageAll(0, loadRankList.size(), comparator, loadRankList);

        //渣车油耗排行榜排序
        Comparator<JSONObject> comparatorOilConsumption = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getBigDecimal("oilConsumption").compareTo(o1.getBigDecimal("oilConsumption"));
            }
        };
        oilConsumptionRankList = CollUtil.sortPageAll(0, oilConsumptionRankList.size(), comparatorOilConsumption, oilConsumptionRankList);
        //排序end

        JSONObject result = new JSONObject();
        result.put("loadRankList", loadRankList);
        result.put("oilConsumptionRankList", oilConsumptionRankList);

        String key = ProjectUtils.cacheKeyByStartAndEnd(startDate, endDate);
        stringRedisTemplate.opsForValue().set(SmartminingConstant.PROJECT_CAR_RANK + ":" + key + ":" + projectId, JSONObject.toJSONString(Result.ok(result)), SmartminingConstant.WORK_INFO_RANK_REDIS_TIME_OUT, TimeUnit.MILLISECONDS);
        return Result.ok(result);
    }

    /**
     * 计算挖机工作信息排行榜
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public synchronized Result diggingMachineRank(Date startDate, Date endDate, String type, Long projectId) {

//        Long projectId = Long.parseLong(request.getHeader("projectId"));

        //挖机装载车数排行榜
        List<JSONObject> loadRankList = new ArrayList<>();

        //挖机装载效率排行榜
        //计时
        List<JSONObject> effectHourRankList = new ArrayList<>();
        //计方
        List<JSONObject> effectCubeRankList = new ArrayList<>();

//        long search = System.currentTimeMillis();
        // 项目中所有挖机
        Specification<ProjectDiggingMachine> machineSpec = new Specification<ProjectDiggingMachine>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectDiggingMachine> diggingMachineList = projectDiggingMachineServiceI.queryWx(machineSpec);
//        System.out.println("查询所有挖机      >>>>>   耗时:" + (System.currentTimeMillis() - search) + "ms");

        //给定时间内所有的渣车工作信息 计时和计方混合
        Specification<ProjectCarWorkInfo> spec = new Specification<ProjectCarWorkInfo>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                //合格
                list.add(cb.equal(root.get("pass").as(Score.class), Score.Pass));
                //不是挖机无效
                list.add(cb.notEqual(root.get("isVaild").as(VaildEnums.class), VaildEnums.NOTVAILDBYDIGGING));
                //不是渣车和挖机无效
                list.add(cb.notEqual(root.get("isVaild").as(VaildEnums.class), VaildEnums.BOTHNOTVALID));
                list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.queryAllByParams(spec);
//        System.out.println("查询给定时间内所有的*渣车*工作信息      >>>>>   耗时:" + (System.currentTimeMillis() - search) + "ms");

        //给定时间内所有的渣车工作信息 计时
        List<ProjectCarWorkInfo> workInfoListByHour = new ArrayList<>();
        //给定时间内所有的渣车工作信息 计方
        List<ProjectCarWorkInfo> workInfoListByCube = new ArrayList<>();
        for (ProjectCarWorkInfo workInfo : workInfoList) {
            if (null != workInfo.getPricingType() && workInfo.getPricingType().equals(PricingTypeEnums.Hour)) {
                workInfoListByHour.add(workInfo);
            }
            if (null != workInfo.getPricingType() && workInfo.getPricingType().equals(PricingTypeEnums.Cube)) {
                workInfoListByCube.add(workInfo);
            }
        }

        //给定时间内所有挖机的工作信息
        Specification<ProjectWorkTimeByDigging> specMachine = new Specification<ProjectWorkTimeByDigging>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectWorkTimeByDigging> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectWorkTimeByDigging> workTimeByDiggingList = projectWorkTimeByDiggingServiceI.queryAllByParams(specMachine);
//        System.out.println("查询给定时间内所有的*挖机*的工作信息      >>>>>   耗时:" + (System.currentTimeMillis() - search) + "ms");

        //********************************************调试
        List<JSONObject> cubeList = calOilConsumptionMachine(diggingMachineList, projectId, startDate, endDate, workTimeByDiggingList, workInfoListByCube, PricingTypeEnums.Cube);
//        System.out.println("计方  >>>>>   " + JSONObject.toJSONString(cubeList));
        List<JSONObject> hourList = calOilConsumptionMachine(diggingMachineList, projectId, startDate, endDate, workTimeByDiggingList, workInfoListByHour, PricingTypeEnums.Hour);
//        System.out.println("计时  >>>>>   " + JSONObject.toJSONString(hourList));

        //挖机油耗比排行榜
        List<JSONObject> oilConsumptionRankList = new ArrayList<>();
        for (JSONObject cube : cubeList) {
            Long totalAmount = cube.getLong("totalAmount") == null ? 0L : cube.getLong("totalAmount");
            Long totalFillAmount = cube.getLong("totalFillAmount") == null ? 0L : cube.getLong("totalFillAmount");
            if (hourList.size() > 0) {

                for (JSONObject hour : hourList) {
                    if (cube.getString("code").equals(hour.getString("code"))) {
                        totalAmount = cube.getLong("totalAmount") + (hour.getLong("totalAmount") == null ? 0L : hour.getLong("totalAmount"));
                        totalFillAmount = cube.getLong("totalFillAmount") + (hour.getLong("totalFillAmount") == null ? 0L : hour.getLong("totalFillAmount"));
                    }
                }

            }
            cube.put("totalAmount", totalAmount);
            cube.put("totalFillAmount", totalFillAmount);
            //油耗比 = 时间段加油总额 / 时间段内(总金额)赚到的钱
            BigDecimal oilConsumption = new BigDecimal(0L);
            if (!totalAmount.equals(0L)) {
                oilConsumption = new BigDecimal(totalFillAmount).divide(new BigDecimal(totalAmount), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            }
            cube.put("oilConsumption", oilConsumption);
            oilConsumptionRankList.add(cube);
        }

        //********************************************调试end

//        long start = System.currentTimeMillis();
        for (ProjectDiggingMachine diggingMachine : diggingMachineList) {

            //**********计算挖机装载车数排行榜**********
            JSONObject loadRank = new JSONObject();
            loadRank.put("code", diggingMachine.getCode());
            loadRank.put("owner", diggingMachine.getOwnerName());
            Integer countHour = 0;
            Integer countCube = 0;
            for (ProjectCarWorkInfo workInfo : workInfoList) {
                if (null != workInfo.getDiggingMachineId() && workInfo.getDiggingMachineId().equals(diggingMachine.getId())) {
                    switch (workInfo.getPricingType()) {
                        case Hour:
                            countHour++;
                            break;
                        case Cube:
                            countCube++;
                            break;
                        case Unknow:
                            break;
                        default:
                            break;
                    }
                }
            }
            //总车数(计时 + 计方)
            loadRank.put("totalCount", countHour + countCube);
            //计时
            loadRank.put("totalCountHour", countHour);
            //计方
            loadRank.put("totalCountCube", countCube);
            loadRankList.add(loadRank);
            //计算挖机装载车数排行榜 end

            //  分解成 计时和计方
            //修改修改修改修改修改修改修改修改修改修改**********计算挖机效率排行榜（按照车/小时）**********
            JSONObject effectHourRank = new JSONObject();
            JSONObject effectCubeRank = new JSONObject();

            effectHourRank.put("code", diggingMachine.getCode());
            effectCubeRank.put("code", diggingMachine.getCode());

            effectHourRank.put("owner", diggingMachine.getOwnerName());
            effectCubeRank.put("owner", diggingMachine.getOwnerName());

            long workTimeTotalHour = 0L;
            long workTimeTotalCube = 0L;
            for (ProjectWorkTimeByDigging workTimeByDigging : workTimeByDiggingList) {
                //workTimeByDigging.getMaterialId() 这个是挖机id
                if (null != workTimeByDigging.getMaterialId() && workTimeByDigging.getMaterialId().equals(diggingMachine.getId())) {

                    switch (workTimeByDigging.getPricingTypeEnums()) {
                        case Hour:
                            workTimeTotalHour = workTimeTotalHour + (null == workTimeByDigging.getWorkTime() ? 0L : workTimeByDigging.getWorkTime());
                            break;
                        case Cube:
                            workTimeTotalCube = workTimeTotalCube + (null == workTimeByDigging.getWorkTime() ? 0L : workTimeByDigging.getWorkTime());
                            break;
                        case Unknow:
                            break;
                        default:
                            break;
                    }
                }
            }

            //计时
            BigDecimal effectHour = new BigDecimal(workTimeTotalHour).divide(new BigDecimal(3600), 1, RoundingMode.HALF_UP);
            effectHourRank.put("totalHour_hour", effectHour);
            effectHourRank.put("totalCountHour", loadRank.getInteger("totalCountHour"));
            if (effectHour.doubleValue() >= 0.1) {
                effectHour = new BigDecimal(loadRank.getInteger("totalCountHour")).divide(effectHour, 1, RoundingMode.HALF_UP);
            } else {
                effectHour = new BigDecimal(0);
            }

            effectHourRank.put("effectHour", effectHour);
            effectHourRankList.add(effectHourRank);

            //计方
            BigDecimal effectCube = new BigDecimal(workTimeTotalCube).divide(new BigDecimal(3600), 1, RoundingMode.HALF_UP);
            effectCubeRank.put("totalHour_cube", effectCube);
            effectCubeRank.put("totalCountCube", loadRank.getInteger("totalCountCube"));
            if (effectCube.doubleValue() >= 0.1) {
                effectCube = new BigDecimal(loadRank.getInteger("totalCountCube")).divide(effectCube, 1, RoundingMode.HALF_UP);
            } else {
                effectCube = new BigDecimal(0);
            }
            effectCubeRank.put("effectCube", effectCube);
            effectCubeRankList.add(effectCubeRank);

            //修改修改修改修改修改修改修改修改修改修改  计算挖机效率排行榜（按照车/小时）end
        }

        //排序
        //挖机装载车数排行榜排序
        Comparator<JSONObject> comparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getLong("totalCount").compareTo(o1.getLong("totalCount"));
            }
        };
        loadRankList = CollUtil.sortPageAll(0, loadRankList.size(), comparator, loadRankList);

        //挖机效率排行榜   计时
        Comparator<JSONObject> effectHourComparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getBigDecimal("effectHour").compareTo(o1.getBigDecimal("effectHour"));
            }
        };
        effectHourRankList = CollUtil.sortPageAll(0, effectHourRankList.size(), effectHourComparator, effectHourRankList);

        //挖机效率排行榜   计方
        Comparator<JSONObject> effectCubeComparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getBigDecimal("effectCube").compareTo(o1.getBigDecimal("effectCube"));
            }
        };
        effectCubeRankList = CollUtil.sortPageAll(0, effectCubeRankList.size(), effectCubeComparator, effectCubeRankList);

        //挖机油耗比排行榜
        Comparator<JSONObject> oilConsumptionComparator = new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                return o2.getBigDecimal("oilConsumption").compareTo(o1.getBigDecimal("oilConsumption"));
            }
        };
        oilConsumptionRankList = CollUtil.sortPageAll(0, oilConsumptionRankList.size(), oilConsumptionComparator, oilConsumptionRankList);
        //排序end

        //说明

        JSONObject result = new JSONObject();
        result.put("loadRankList", loadRankList);
        result.put("effectHourRankList", effectHourRankList);
        result.put("effectCubeRankList", effectCubeRankList);
        result.put("oilConsumptionRankList", oilConsumptionRankList);
        result.put("detail", detailInsert());

        String key = ProjectUtils.cacheKeyByStartAndEnd(startDate, endDate);
        stringRedisTemplate.opsForValue().set(SmartminingConstant.DIGGING_MACHINE_RANK + ":" + key + ":" + projectId, JSONObject.toJSONString(Result.ok(result)), SmartminingConstant.WORK_INFO_RANK_REDIS_TIME_OUT, TimeUnit.MILLISECONDS);
        return Result.ok(result);
    }

    private JSONObject detailInsert() {
        JSONObject detail = new JSONObject();

        detail.put("effectHourRankList", "装载效率(计时)");
        detail.put("effectCubeRankList", "装载效率(计方)");

        detail.put("oilConsumptionRankList", "油耗排行");

        detail.put("loadRankList", "装载总车数(计方+计时)");
        detail.put("totalCountCube", "装载总车数(计方)");
        detail.put("totalCountHour", "装载总车数(计时)");

        detail.put("totalHour_cube", "工作总时长(秒,计方)");
        detail.put("totalHour_hour", "工作总时长(秒,计时)");

        detail.put("effectCube", "工作效率(计方,工作效率 = 计方总车数/ 计方工作总时长(只计算时长>0.1小时的挖机工作数据) )");
        detail.put("effectHour", "工作效率(计时,工作效率 = 计时总车数/ 计时工作总时长(只计算时长>0.1小时的挖机工作数据) )");

        return detail;
    }

    /**
     * 计算挖机油耗比排行榜
     *
     * @param diggingMachineList 项目挖机列表
     * @param projectId          项目id
     * @param startDate          给定开始时间
     * @param endDate            给定结束时间
     * @param timeByDiggingList  挖机开停机工作信息
     * @param workInfoList       渣车工作信息
     * @param pricingTypeEnums   计价方式
     * @return 油耗比排行
     */
    private List<JSONObject> calOilConsumptionMachine(List<ProjectDiggingMachine> diggingMachineList,
                                                      Long projectId,
                                                      Date startDate,
                                                      Date endDate,
                                                      List<ProjectWorkTimeByDigging> timeByDiggingList,
                                                      List<ProjectCarWorkInfo> workInfoList,
                                                      PricingTypeEnums pricingTypeEnums) {

        //油耗比 = 时间段加油总金额 / 时间段总金额(注意有计时还是计方,计方还要分物料价格)
        List<JSONObject> oilConsumptionRankList = new ArrayList<>();


        //根据计价方式计算
        switch (pricingTypeEnums) {
            //计方 计算方式跟装载的材料相关(ProjectDiggingMachineMaterial)
            case Cube:

                //时间段内给挖机加油的计方方式加油历史
                Specification<ProjectCarFillLog> specByCube = new Specification<ProjectCarFillLog>() {
                    List<Predicate> list = new ArrayList<>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        list.add(cb.equal(root.get("carType").as(CarType.class), CarType.DiggingMachine));
                        list.add(cb.equal(root.get("pricingTypeEnums").as(PricingTypeEnums.class), PricingTypeEnums.Cube));
                        list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillLog> fillLogList = projectCarFillLogServiceI.queryWx(specByCube);

                //物料-单价关系
                List<ProjectDiggingMachineMaterial> machineMaterialList = projectDiggingMachineMaterialServiceI.getByProjectIdOrderById(projectId);

                //统计渣车工作信息表中的相应挖机的方量,并且根据物料区分计算金额
                for (ProjectDiggingMachine machine : diggingMachineList) {
                    JSONObject oilConsumptionRank = new JSONObject();
                    oilConsumptionRank.put("code", machine.getCode());
                    oilConsumptionRank.put("owner", machine.getOwnerName());
                    //计算总金额
                    Long totalAmount = 0L;
                    for (ProjectCarWorkInfo workInfo : workInfoList) {
                        if (null != workInfo.getDiggingMachineId() && workInfo.getDiggingMachineId().equals(machine.getId())) {
                            for (ProjectDiggingMachineMaterial machineMaterial : machineMaterialList) {
                                if (workInfo.getMaterialId().equals(machineMaterial.getMaterialId())) {
                                    totalAmount = totalAmount +
                                            workInfo.getCubic() * machineMaterial.getPrice();
                                }
                            }
                        }
                    }

                    //计算加油历史金额
                    Long totalFillAmount = 0L;
                    for (ProjectCarFillLog fillLog : fillLogList) {
                        if (null != fillLog.getCarId() && fillLog.getCarId().equals(machine.getId())) {
                            totalFillAmount = totalFillAmount + fillLog.getAmount();
                        }
                    }

                    //换算总金额
                    //总金额: 分=>元
                    BigDecimal totalAmountCube = new BigDecimal(totalAmount).divide(new BigDecimal(100000000), 2, RoundingMode.HALF_UP);
                    //换算总加油金额
                    //加油金额: 分=>元
                    BigDecimal totalFillAmountRMB = new BigDecimal(totalFillAmount).divide(new BigDecimal(100), 1, RoundingMode.HALF_UP);
                    oilConsumptionRank.put("totalAmount", totalAmountCube);
                    oilConsumptionRank.put("totalFillAmount", totalFillAmountRMB);
                    oilConsumptionRankList.add(oilConsumptionRank);
                }
                break;
            //计时 计算方式跟挖机型号相关(ProjectHourPrice)
            case Hour:
                //时间段内给挖机加油的计方方式加油历史
                Specification<ProjectCarFillLog> specByHour = new Specification<ProjectCarFillLog>() {
                    List<Predicate> list = new ArrayList<>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        list.add(cb.equal(root.get("carType").as(CarType.class), CarType.DiggingMachine));
                        list.add(cb.equal(root.get("pricingTypeEnums").as(PricingTypeEnums.class), PricingTypeEnums.Hour));
                        list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillLog> fillLogListByHour = projectCarFillLogServiceI.queryWx(specByHour);

                //挖机-时薪关系
                List<ProjectHourPrice> hourPriceList = projectHourPriceServiceI.getAllByProjectId(projectId);

                for (ProjectDiggingMachine machine : diggingMachineList) {
                    JSONObject oilConsumptionRank = new JSONObject();
                    oilConsumptionRank.put("code", machine.getCode());
                    //计算总工作时长
                    Long totalTime = 0L;
                    //挖机对应时薪
                    Long price = 0L;
                    for (ProjectWorkTimeByDigging timeByDigging : timeByDiggingList) {
                        if (null != timeByDigging.getMaterialId() && timeByDigging.getMaterialId().equals(machine.getId())) {
                            totalTime = totalTime + timeByDigging.getWorkTime();
                            for (ProjectHourPrice hourPrice : hourPriceList) {
                                if (hourPrice.getBrandId().equals(machine.getBrandId()) && hourPrice.getModelId().equals(machine.getModelId())) {
                                    price = hourPrice.getPrice();
                                }
                            }
                        }
                    }

                    //计算加油历史金额
                    Long totalFillAmount = 0L;
                    for (ProjectCarFillLog fillLog : fillLogListByHour) {
                        if (null != fillLog.getCarId() && fillLog.getCarId().equals(machine.getId())) {
                            totalFillAmount = totalFillAmount + fillLog.getAmount();
                        }
                    }

                    //换算总金额
                    //工作时长: 秒=>小时
                    BigDecimal totalHour = new BigDecimal(totalTime).divide(new BigDecimal(3600), 1, RoundingMode.HALF_UP);
                    //工作时薪: 分=>元
                    BigDecimal priceRMB = new BigDecimal(price).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    oilConsumptionRank.put("totalAmount", totalHour.multiply(priceRMB));
                    //换算总加油金额
                    //加油金额: 分=>元
                    BigDecimal totalFillAmountRMB = new BigDecimal(totalFillAmount).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                    oilConsumptionRank.put("totalFillAmount", totalFillAmountRMB);
                    oilConsumptionRankList.add(oilConsumptionRank);
                }
                break;
            default:
                break;
        }
        return oilConsumptionRankList;
    }

    @Scheduled(cron = "00 00 00 ? * MON")
    public void matchingDegreeReportByWeek() {
        try {
            Date now = new Date();
            List<Project> projectList = projectServiceI.getAll();
            for (Project project : projectList) {
                Map<String, Date> dateMap = workDateService.getWorkTime(project.getId(), now);
                //早班开始时间
                Date earlyStart = dateMap.get("start");
                Calendar earlyStartCalendar = Calendar.getInstance();
                earlyStartCalendar.setTime(earlyStart);
                earlyStartCalendar.add(Calendar.DATE, -7);
                earlyStart = earlyStartCalendar.getTime();
                //晚班结束时间
                Date nightEnd = dateMap.get("end");
                Calendar nightEndCalendar = Calendar.getInstance();
                nightEndCalendar.setTime(nightEnd);
                nightEndCalendar.add(Calendar.DATE, -1);
                nightEnd = nightEndCalendar.getTime();
                List<Map> matchingDegreeList = matchingDegreeServiceI.getAllByProjectIdAndStartTimeAndEndTimeByWeek(project.getId(), earlyStart, nightEnd);
                List<MatchingDegree> matchingDegrees = new ArrayList<>();
                for (int i = 0; i < matchingDegreeList.size(); i++) {
                    //渣车ID
                    Long carId = Long.parseLong(matchingDegreeList.get(i).get("car_id").toString());
                    //渣车编号
                    String carCode = matchingDegreeList.get(i).get("car_code").toString();
                    //渣场ID
                    Long slagSiteId = Long.parseLong(matchingDegreeList.get(i).get("slag_site_id").toString());
                    //渣场名称
                    String slagSiteName = matchingDegreeList.get(i).get("slag_site_name").toString();
                    //项目ID
                    Long projectId = Long.parseLong(matchingDegreeList.get(i).get("project_id").toString());
                    //完成车数
                    Long finishCount = Long.parseLong(matchingDegreeList.get(i).get("finish_count").toString());
                    //挖机上传车数
                    Long uploadCountByMachine = Long.parseLong(matchingDegreeList.get(i).get("upload_count_by_machine").toString());
                    //渣车上传车数 带装载时间
                    Long uploadCountByCar = Long.parseLong(matchingDegreeList.get(i).get("upload_count_by_car").toString());
                    //渣车上传总车数
                    Long uploadTotalCountByCar = Long.parseLong(matchingDegreeList.get(i).get("upload_total_count_by_car").toString());
                    //班次
                    ShiftsEnums shifts = ShiftsEnums.converShift(Integer.valueOf(matchingDegreeList.get(i).get("shifts").toString()));
                    //上传无效车数
                    Long unValidCount = Long.parseLong(matchingDegreeList.get(i).get("un_valid_count").toString());
                    MatchingDegree degree = new MatchingDegree();
                    degree.setProjectId(projectId);
                    degree.setTimeType(TimeTypeEnum.WEEK);
                    degree.setCarId(carId);
                    degree.setCarCode(carCode);
                    degree.setFinishCount(finishCount);
                    degree.setUploadCountByMachine(uploadCountByMachine);
                    degree.setUploadCountByCar(uploadCountByCar);
                    degree.setUploadTotalCountByCar(uploadTotalCountByCar);
                    //匹配率
                    BigDecimal degreePercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getUploadCountByMachine() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
                    degree.setDegreePercent(degreePercent);
                    //写卡成功率
                    BigDecimal writeCardPercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getUploadCountByCar() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
                    degree.setWriteCardPercent(writeCardPercent);
                    //完成率
                    BigDecimal finishPercent = degree.getUploadTotalCountByCar() != 0 ? new BigDecimal((float) degree.getFinishCount() / degree.getUploadTotalCountByCar()).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
                    degree.setFinishPercent(finishPercent);
                    degree.setShifts(shifts);
                    degree.setUnValidCount(unValidCount);
                    degree.setStartTime(earlyStart);
                    degree.setEndTime(nightEnd);
                    degree.setCreateTime(new Date());
                    matchingDegrees.add(degree);
                }
                matchingDegreeServiceI.batchSave(matchingDegrees);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查终端状态
     */
    @Scheduled(fixedRate = 1000 * 60L)
    public void checkDeviceStatus() {
        try {
            List<Project> projectList = projectServiceI.getAll();
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            String replyTopic = "smartmining/device/fix/pc/status";
            List<ProjectDeviceStatusLog> logList = projectDeviceStatusLogServiceI.getAllByUnlineTime();
            //生成索引
            Map<String, Integer> logMapIndex = new HashMap<>();
            for (int i = 0; i < logList.size(); i++) {
                logMapIndex.put(logList.get(i).getUid(), i);
            }
            for (Project project : projectList) {
                List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByProjectId(project.getId());
                List<ProjectOtherDevice> otherDeviceList = projectOtherDeviceServiceI.getAllByProjectId(project.getId());
                for (ProjectDevice device : projectDeviceList) {
                    String text = getValueOps().get(device.getUid());
                    if (StringUtils.isEmpty(text)) {
                        if (device.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                            Integer index = logMapIndex.get(device.getUid());
                            Date date = new Date();
                            if (index != null) {
                                ProjectDeviceStatusLog log = logList.get(index);
                                log.setUnlineTime(date);
                                projectDeviceStatusLogServiceI.save(log);
                            }
                            device.setStatus(ProjectDeviceStatus.OffLine);
                            device.setLastDate(date);
                            if (StringUtils.isNotEmpty(device.getCode()))
                                handler.handDeviceStatus(replyTopic, device.getUid(), project.getId(), device.getDeviceType(), device.getCode(), device.getStatus(), count);
                            count++;
                            projectDeviceServiceI.save(device);
                        }
                    }
                }
                for (ProjectOtherDevice device : otherDeviceList) {
                    String text = getValueOps().get(device.getUid());
                    if (StringUtils.isEmpty(text)) {
                        if (device.getDeviceStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                            Integer index = logMapIndex.get(device.getUid());
                            Date date = new Date();
                            if (index != null) {
                                ProjectDeviceStatusLog log = logList.get(index);
                                log.setUnlineTime(new Date());
                                projectDeviceStatusLogServiceI.save(log);
                            }
                            device.setLastDate(date);
                            device.setDeviceStatus(ProjectDeviceStatus.OffLine);
                            projectOtherDeviceServiceI.save(device);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测合并数据
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void mergeWorkInfo() {
        try {
            List<Project> projectList = projectServiceI.getAll();
            Date now = new Date();
            Date last = DateUtils.getAddSecondDate(now, 60 * 60);
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            for (Project project : projectList) {
                List<ProjectUnloadLog> logList = projectUnloadLogServiceI.getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(project.getId(), last, now, true, false);
                for (ProjectUnloadLog log : logList) {
                    handler.updateCarWorkInfoNew(log);
                }
            }
        } catch (IOException e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
        }
    }

    /**
     * 生成终端设备的轨迹
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void createDevicePosition() {
        try {
            List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAll();
            Date date = new Date();
            List<ProjectRunningTrajectoryLog> saveLogList = new ArrayList<>();
            for (ProjectDevice device : projectDeviceList) {
                Shift shift = workDateService.getShift(date, device.getProjectId());
                Map<String, Date> dateMap = workDateService.getWorkTime(device.getProjectId(), date);
                Date startTime = dateMap.get("start");
                Date dateIdentification = DateUtils.createReportDateByMonth(date);
                if (date.getTime() < startTime.getTime())
                    dateIdentification = DateUtils.createReportDateByMonth(DateUtils.getAddDate(date, -1));
                String positionMessage = stringRedisTemplate.opsForValue().get(device.getUid() + device.getProjectId() + dateIdentification.getTime() + shift.getAlias() + "positionMessage");
                if (StringUtils.isNotEmpty(positionMessage)) {
                    ProjectRunningTrajectoryLog log = new ProjectRunningTrajectoryLog();
                    log.setProjectId(device.getProjectId());
                    log.setDateIdentification(dateIdentification);
                    log.setShift(shift);
                    log.setDeviceType(device.getDeviceType());
                    log.setUid(device.getUid());
                    log.setRunningTrajectory(positionMessage);
                    stringRedisTemplate.delete(device.getUid() + device.getProjectId() + dateIdentification.getTime() + shift.getAlias() + "positionMessage");
                    saveLogList.add(log);
                }
            }
            projectRunningTrajectoryLogServiceI.saveAll(saveLogList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成用户的轨迹
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void createUserPosition() {
        List<SysUser> sysUserList = sysUserServiceI.getAll();
        List<ProjectUserTrajectoryLog> saveLogList = new ArrayList<>();
        for (SysUser user : sysUserList) {
            String locationMessage = stringRedisTemplate.opsForValue().get(user.getAccount() + "locationMessage");
            if (StringUtils.isNotEmpty(locationMessage)) {
                ProjectUserTrajectoryLog log = new ProjectUserTrajectoryLog();
                log.setUserId(user.getId());
                log.setUserAccount(user.getAccount());
                log.setRunningTrajectory(locationMessage);
                stringRedisTemplate.delete(user.getAccount() + "locationMessage");
                saveLogList.add(log);
            }
        }
        projectUserTrajectoryLogServiceI.saveAll(saveLogList);
    }


    public static void main(String[] args) {
        Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
        Map<String, Map<String, String>> keyWord = new HashMap<>();
        Map<String, String> value01 = new HashMap<>();
        value01.put("value", "S20191023165753001");
        keyWord.put("keyword1", value01);
        Map<String, String> value02 = new HashMap<>();
        value02.put("value", "测试商品");
        keyWord.put("keyword2", value02);
        Map<String, String> value03 = new HashMap<>();
        value03.put("value", "微信支付");
        keyWord.put("keyword3", value03);
        Map<String, String> value04 = new HashMap<>();
        value04.put("value", "500 元");
        keyWord.put("keyword4", value04);
        Map<String, String> value05 = new HashMap<>();
        value05.put("value", DateUtils.formatDateByPattern(new Date(), SmartminingConstant.DATEFORMAT) + " 至 " + DateUtils.formatDateByPattern(new Date(), SmartminingConstant.DATEFORMAT));
        keyWord.put("keyword5", value05);
        data.put("data", keyWord);
        String json = JSON.toJSONString(data);
        System.out.println(json);
    }
}
