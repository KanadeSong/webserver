package com.seater.smartmining.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.enums.VaildEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.ReportService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.SpringSchedule;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/12 0012 18:08
 */
@RestController
@RequestMapping("/api/projectAppStatisticsLog")
public class ProjectAppStatisticsLogController {

    @Autowired
    private ProjectAppStatisticsLogServiceI projectAppStatisticsLogServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectAppStatisticsByCarServiceI projectAppStatisticsByCarServiceI;
    @Autowired
    private ProjectAppStatisticsByMachineServiceI projectAppStatisticsByMachineServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    SpringSchedule springSchedule;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }


    @PostConstruct
    @Transactional
    public void init() {
        try {
            reportService.appInit();
            /*Date reportDate = new Date();
            List<Project> projectList = projectServiceI.getAll();
            for (Project project : projectList) {
                Map<String, Date> dateMap = workDateService.getWorkTime(project.getId(), reportDate);
                Date nightStartTime = dateMap.get("nightStart");
                projectAppStatisticsByCarServiceI.deleteByCreateDate(nightStartTime, project.getId());
                projectAppStatisticsByMachineServiceI.deleteByCreateDate(nightStartTime, project.getId());
                //所有渣车的工作信息 根据编号和班次分组
                List<Map> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAppDiggingInfoByProjectIdAndDate(project.getId(), nightStartTime);
                for (int i = 0; i < projectCarWorkInfoList.size(); i++) {
                    ProjectAppStatisticsByCar appByCar = new ProjectAppStatisticsByCar();
                    Long cubic = Long.parseLong(projectCarWorkInfoList.get(i).get("cubic").toString());
                    Integer count = Integer.valueOf(projectCarWorkInfoList.get(i).get("count").toString());
                    String carCode = projectCarWorkInfoList.get(i).get("car_code").toString();
                    Integer value = Integer.valueOf(projectCarWorkInfoList.get(i).get("shift").toString());
                    ShiftsEnums shift = ShiftsEnums.converShift(value);
                    appByCar.setCarCode(carCode);
                    appByCar.setCarCount(count);
                    appByCar.setCubic(cubic);
                    appByCar.setProjectId(project.getId());
                    appByCar.setShift(shift);
                    appByCar.setCreateDate(nightStartTime);
                    projectAppStatisticsByCarServiceI.save(appByCar);
                }
                //所有挖机的工作信息 根据编号和班次分组
                List<Map> projectWorkTimeByDiggingList = projectWorkTimeByDiggingServiceI.getTotalTimeByProjectIdAndDate(project.getId(), nightStartTime);
                for (int i = 0; i < projectWorkTimeByDiggingList.size(); i++) {
                    ProjectAppStatisticsByMachine appByMachine = new ProjectAppStatisticsByMachine();
                    String machineCode = projectWorkTimeByDiggingList.get(i).get("material_code").toString();
                    Integer value = Integer.valueOf(projectWorkTimeByDiggingList.get(i).get("shift").toString());
                    ShiftsEnums shift = ShiftsEnums.converShift(value);
                    Long workTime = Long.parseLong(projectWorkTimeByDiggingList.get(i).get("workTime").toString());
                    appByMachine.setMachineCode(machineCode);
                    appByMachine.setShifts(shift);
                    appByMachine.setProjectId(project.getId());
                    appByMachine.setWorkTime(workTime);
                    appByMachine.setCreateDate(nightStartTime);
                    projectAppStatisticsByMachineServiceI.save(appByMachine);
                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Date reportDate = new Date();
            //判断是查询全部还是筛选
            boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("当前用户无任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, reportDate);
            Date earlyStartTime = dateMap.get("start");
            Date earlyEndTime = dateMap.get("earlyEnd");
            Date nightStartTime = dateMap.get("nightStart");
            Date nightEndTime = dateMap.get("end");
            if(reportDate.getTime() < earlyStartTime.getTime()){
                earlyStartTime = DateUtils.subtractionOneDay(earlyStartTime);
                earlyEndTime = DateUtils.subtractionOneDay(earlyEndTime);
                nightStartTime = DateUtils.subtractionOneDay(nightStartTime);
                nightEndTime = DateUtils.subtractionOneDay(nightEndTime);
            }
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
            //总时长
            Long totalTime = 0L;
            if (!flag) {
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                String params = "\"" + sysUser.getId() + "\"";
                Specification<ProjectSchedule> specification = new Specification<ProjectSchedule>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectSchedule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        if (!jsonArray.contains(SmartminingConstant.ALLDATA))
                            list.add(cb.like(root.get("managerId").as(String.class), "%" + params + "%"));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectSchedule> projectScheduleList = projectScheduleServiceI.getAllByQuery(specification);
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : projectScheduleList) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                Specification<ScheduleCar> spec = new Specification<ScheduleCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ScheduleCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                        if (groupCodeList.size() > 0) {
                            Expression<String> exp = root.get("groupCode").as(String.class);
                            list.add(exp.in(groupCodeList));
                        }
                        query.orderBy(criteriaBuilder.asc(root.get("id").as(Long.class)));
                        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByQuery(spec);
                //生成渣车编号索引
                Map<String, Integer> carMapIndex = new HashMap<>();
                for (int i = 0; i < scheduleCarList.size(); i++) {
                    String carCode = scheduleCarList.get(i).getCarCode();
                    if (countCarCodeList.contains(carCode))
                        log.setCarAttendanceCount(log.getCarAttendanceCount() + 1);
                    carMapIndex.put(carCode, i);

                }
                Specification<ScheduleMachine> specificationMachine = new Specification<ScheduleMachine>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ScheduleMachine> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                        if (groupCodeList.size() > 0) {
                            Expression<String> exp = root.get("groupCode").as(String.class);
                            list.add(exp.in(groupCodeList));
                        }
                        query.orderBy(criteriaBuilder.asc(root.get("id").as(Long.class)));
                        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByQuery(specificationMachine);
                //生成挖机编号索引
                Map<String, Integer> machineMapIndex = new HashMap<>();
                for (int i = 0; i < scheduleMachineList.size(); i++) {
                    machineMapIndex.put(scheduleMachineList.get(i).getMachineCode(), i);
                }
                for (ProjectAppStatisticsByCar car : projectAppStatisticsByCars) {
                    Integer index = carMapIndex.get(car.getCarCode());
                    if (index != null) {
                        log.setTotalCubic(log.getTotalCubic() + car.getCubic());
                        log.setWorkOnCount(log.getWorkOnCount() + car.getCarCount());
                    }
                }
                for (ProjectAppStatisticsByMachine machine : projectAppStatisticsByMachines) {
                    Integer index = machineMapIndex.get(machine.getMachineCode());
                    if (index != null)
                        totalTime = totalTime + machine.getWorkTime();
                }
                for (ProjectWorkTimeByDigging digging : projectWorkTimeByDiggingList) {
                    Integer index = machineMapIndex.get(digging.getMaterialCode());
                    if (index != null) {
                        Long time = DateUtils.calculationHour(digging.getStartTime(), new Date());
                        totalTime = totalTime + time;
                    }
                }
                Integer unpass = 0;
                for (ProjectMqttCardReport info : unPassList) {
                    Integer index = carMapIndex.get(info.getCarCode());
                    if (index != null)
                        unpass++;
                }
                log.setCarOnLineCount(scheduleCarList.size());
                log.setExceptionCount(unpass);
                log.setDiggingMachineAttendanceCount(projectAppStatisticsByMachines.size());
            } else {
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
                log.setExceptionCount(unPassList.size());
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
            BigDecimal time = new BigDecimal((float)totalTime / 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
            //装载/车
            BigDecimal avgCars = time != null && time.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(log.getWorkOnCount()).divide(time, 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //平均装载/小时
            BigDecimal avgCubicB = time != null && time.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(log.getTotalCubic() / 1000000L).divide(time, 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0L);
            Long avgCubic = avgCubicB.longValue();
            log.setAvgCubic(avgCubic);
            log.setAvgCars(avgCars);
            log.setCreateDate(new Date());
            log.setTotalTime(totalTime);
            reportDate = DateUtils.createReportDateByMonth(earlyStartTime);
            log.setReportDate(reportDate);
            return Result.ok(log);
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/newQuery")
    public Result newQuery(HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            String redisJosn = stringRedisTemplate.opsForValue().get(SmartminingConstant.CAL_SHIFT_STATIC_DATA + ":" + projectId);
            if (StringUtils.isNotEmpty(redisJosn)) {
                ProjectAppStatisticsLog log = JSONObject.parseObject(redisJosn, ProjectAppStatisticsLog.class);
                return Result.ok(log);
            } else {
                return springSchedule.calShiftStaticData(projectServiceI.get(projectId));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
