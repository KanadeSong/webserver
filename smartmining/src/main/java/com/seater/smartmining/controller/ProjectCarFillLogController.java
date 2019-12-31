package com.seater.smartmining.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectCarFillLog")
public class ProjectCarFillLogController {
    @Autowired
    ProjectCarFillLogServiceI projectCarFillLogServiceI;

    @Autowired
    ProjectServiceI projectServiceI;

    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    ProjectCarServiceI projectCarServiceI;

    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    WorkDateService workDateService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, String code, CarType carType, String oilCarCode, Date dateIdentification, ShiftsEnums shifts) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("carCode").as(String.class), "%" + code + "%"));

                    if (!ObjectUtils.isEmpty(carType)) {
                        list.add(cb.equal(root.get("carType").as(CarType.class), carType));
                    }
                    if (!StringUtils.isEmpty(oilCarCode)) {
                        list.add(cb.equal(root.get("oilCarCode").as(String.class), oilCarCode));
                    }
                    if (!ObjectUtils.isEmpty(dateIdentification)) {
                        list.add(cb.equal(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(dateIdentification)));
                    }
                    if (!ObjectUtils.isEmpty(shifts)) {
                        list.add(cb.equal(root.get("shifts").as(ShiftsEnums.class), shifts));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));

                    query.orderBy(cb.desc(root.get("date").as(Date.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectCarFillLogServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    /**
     * 带数据权限的查询
     *
     * @param current
     * @param pageSize
     * @param request
     * @param code
     * @param carType
     * @param oilCarCode
     * @param dateIdentification
     * @param shifts
     * @return
     */
    @PostMapping("/queryRelate")
    public Object queryRelate(Integer current, Integer pageSize, HttpServletRequest request, String code, CarType carType, String oilCarCode, Date dateIdentification, ShiftsEnums shifts) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            List<String> carCodeList = new ArrayList<>();
            //判断是查询全部还是筛选
            boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("当前用户无任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            if (!flag) {
                List<ProjectSchedule> projectScheduleList = projectScheduleServiceI.getAllByProjectId(projectId);
                List<ProjectSchedule> scheduleList = projectScheduleList;
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                for (ProjectSchedule schedule : projectScheduleList) {
                    JSONArray array = JSONArray.parseArray(schedule.getManagerId());
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            if (Long.parseLong(array.get(i).toString()) == sysUser.getId())
                                scheduleList.add(schedule);
                        }
                    }
                }
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : scheduleList) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectId(projectId);
                for (ScheduleCar car : scheduleCarList) {
                    if (groupCodeList.contains(car.getGroupCode()))
                        carCodeList.add(car.getCarCode());
                }
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectId(projectId);
                for (ScheduleMachine machine : scheduleMachineList) {
                    if (groupCodeList.contains(machine.getGroupCode()))
                        carCodeList.add(machine.getMachineCode());
                }
            }
            Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("carCode").as(String.class), "%" + code + "%"));

                    if (!ObjectUtils.isEmpty(carType)) {
                        list.add(cb.equal(root.get("carType").as(CarType.class), carType));
                    }
                    if (!StringUtils.isEmpty(oilCarCode)) {
                        list.add(cb.equal(root.get("oilCarCode").as(String.class), oilCarCode));
                    }
                    if (carCodeList != null && carCodeList.size() > 0) {
                        Expression<String> exp = root.<String>get("carCode");
                        list.add(exp.in(carCodeList));
                    }
                    if (!ObjectUtils.isEmpty(dateIdentification)) {
                        list.add(cb.equal(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(dateIdentification)));
                    }
                    if (!ObjectUtils.isEmpty(shifts)) {
                        list.add(cb.equal(root.get("shifts").as(ShiftsEnums.class), shifts));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    query.orderBy(cb.desc(root.get("date").as(Date.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectCarFillLogServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }


    @PostMapping("/totalFillToday")
    public Object totalFillToday(HttpServletRequest request, String oilCarCode, Long oilCarId) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);

            Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(cb.equal(root.get("managerId").as(Long.class), sysUser.getId()));
                    ShiftsEnums shift = ShiftsEnums.DAYSHIFT;
                    try {
                        shift = workDateService.getTargetDateShift(new Date(), projectId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    list.add(cb.equal(root.get("oilCarId").as(Long.class), oilCarId));
                    list.add(cb.equal(root.get("shifts").as(ShiftsEnums.class), shift));
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date())));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            List<ProjectCarFillLog> fillLogList = projectCarFillLogServiceI.queryWx(spec);
            Long totalFillToday = 0L;
            for (ProjectCarFillLog fillLog : fillLogList) {
                totalFillToday = totalFillToday + fillLog.getVolumn();
            }
            return Result.ok(totalFillToday);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    /*@RequestMapping(value = "/save", produces = "application/json")
    @Transactional
    public Object save(@RequestBody List<ProjectCarFillLog> projectCarFillLog, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            for(ProjectCarFillLog log : projectCarFillLog){
                log.setProjectId(projectId);
                log.setAmount(projectServiceI.get(projectId).getOilPirce() * log.getVolumn() / 1000L);
                projectCarFillLogServiceI.save(log);
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }*/

    @RequestMapping(value = "/save")
    @Transactional
    public Object save(ProjectCarFillLog projectCarFillLog, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //计价方式
            if (projectCarFillLog.getCarType().equals(CarType.DiggingMachine)) {
                ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, projectCarFillLog.getCarCode());
                if (null != scheduleMachine) {
                    projectCarFillLog.setPricingTypeEnums(scheduleMachine.getPricingType());
                }
            }
            projectCarFillLog.setShift(workDateService.getTargetDateShift(projectCarFillLog.getDate(), projectId));
            projectCarFillLog.setDateIdentification(workDateService.getTargetDateIdentification(projectCarFillLog.getDate(), projectId));
            projectCarFillLog.setProjectId(projectId);
            projectCarFillLog.setAmount(projectServiceI.get(projectId).getOilPirce() * projectCarFillLog.getVolumn() / 1000L);
            projectCarFillLogServiceI.save(projectCarFillLog);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }


    @RequestMapping(value = "/newSave")
    @Transactional
    public synchronized Object newSave(@RequestBody List<ProjectCarFillLog> projectCarFillLogList, HttpServletRequest request) {
        try {
            if (projectCarFillLogList.size() == 0) {
                return Result.error("上传数据为空,请检查");
            }

            SysUser sysUser = JSONObject.parseObject(JSONObject.toJSONString(SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO)), SysUser.class);
            if (StringUtils.isEmpty(sysUser.getName())) {
                return Result.error("操作失败,管理员信息错误");
            }
            Long projectId = Long.parseLong(request.getHeader("projectId"));

            //  按登陆人(油车管理员)查出其管理的油车(管理员-油车 1对1)
            Specification<ProjectOtherDevice> specManager = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                    if (!StringUtils.isEmpty(projectCarFillLog.getOilCarCode())){
//                        list.add(cb.equal(root.get("oilCarCode").as(String.class), projectCarFillLog.getOilCarCode()));
//                    }
                    list.add(cb.equal(root.get("carType").as(CarType.class), CarType.OilCar));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(cb.equal(root.get("id").as(Long.class), projectCarFillLogList.get(0).getOilCarId()));
                    list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectOtherDevice> projectOtherDevice = projectOtherDeviceServiceI.queryWx(specManager);

            if (projectOtherDevice.size() == 0) {
                return Result.error("项目中不存在该油车");
            }

            List<ProjectCarFillLog> projectCarFillLogList_d = ProjectUtils.removeDuplicateCase(projectCarFillLogList);

            List<ProjectCarFillLog> projectCarFillLogListFinal = new ArrayList<>();

            //判断数据库是否已存在
            for (ProjectCarFillLog projectCarFillLog : projectCarFillLogList_d) {
                Specification<ProjectCarFillLog> specEvent = new Specification<ProjectCarFillLog>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        list.add(cb.equal(root.get("eventId").as(String.class), projectCarFillLog.getEventId()));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillLog> fillLogs = projectCarFillLogServiceI.queryWx(specEvent);
                if (fillLogs.size() == 0) {
                    projectCarFillLogListFinal.add(projectCarFillLog);
                }
            }


            for (ProjectCarFillLog projectCarFillLog : projectCarFillLogListFinal) {

                if (projectCarFillLog.getCarType().equals(CarType.SlagCar)) {
                    //  校验进来的参数中是否有被加油车在库中,用车编号来查
                    Specification<ProjectCar> specCheckProjectCar = new Specification<ProjectCar>() {
                        List<Predicate> list = new ArrayList<Predicate>();

                        @Override
                        public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            list.add(cb.equal(root.get("code").as(String.class), projectCarFillLog.getCarCode()));
                            list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                            list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                            //  *****必须倒叙,因为要取最近一条*****
                            query.orderBy(cb.desc(root.get("id").as(Long.class)));
                            return cb.and(list.toArray(new Predicate[list.size()]));
                        }
                    };
                    //  项目中没有这台渣车
                    List<ProjectCar> projectCars = projectCarServiceI.queryWx(specCheckProjectCar);
                    if (projectCars.size() == 0 || projectCars.size() != 1) {
                        //  备注该车不存在项目中
                        projectCarFillLog.setRemark("项目中不存在该车辆,该车辆信息异常");
                        //  车id设0
                        projectCarFillLog.setCarId(0L);
                    } else {
                        projectCarFillLog.setCarId(projectCars.get(0).getId());
                    }
                } else if (projectCarFillLog.getCarType().equals(CarType.DiggingMachine)) {
                    //  校验进来的参数中是否有被加油车在库中,用车编号来查
                    Specification<ProjectDiggingMachine> specCheckDiggingMachine = new Specification<ProjectDiggingMachine>() {
                        List<Predicate> list = new ArrayList<Predicate>();

                        @Override
                        public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            list.add(cb.equal(root.get("code").as(String.class), projectCarFillLog.getCarCode()));
                            list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                            list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                            //  *****必须倒叙,因为要取最近一条*****
                            query.orderBy(cb.desc(root.get("id").as(Long.class)));
                            return cb.and(list.toArray(new Predicate[list.size()]));
                        }
                    };
                    //  项目中没有这台渣车
                    Page<ProjectDiggingMachine> projectCars = projectDiggingMachineServiceI.query(specCheckDiggingMachine);
                    if (projectCars.getContent().size() == 0 || projectCars.getContent().size() != 1) {
                        //  备注该车不存在项目中
                        projectCarFillLog.setRemark("项目中不存在该挖机,该挖机信息异常");
                        //  车id设0
                        projectCarFillLog.setCarId(0L);
                    } else {
                        projectCarFillLog.setCarId(projectCars.getContent().get(0).getId());
                    }
                    //计价方式
                    ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, projectCarFillLog.getCarCode());
                    if (null != scheduleMachine) {
                        projectCarFillLog.setPricingTypeEnums(scheduleMachine.getPricingType());
                    }
                }

                projectCarFillLog.setProjectId(projectId);
                projectCarFillLog.setAmount(projectServiceI.get(projectId).getOilPirce() * projectCarFillLog.getVolumn() / 1000L);
                if (ObjectUtils.isEmpty(projectCarFillLog.getDate())) {
                    projectCarFillLog.setDate(new Date());
                }
                projectCarFillLog.setManagerId(sysUser.getId());
                projectCarFillLog.setManagerName(sysUser.getName());
                //  默认设定终止读数是加油量
                projectCarFillLog.setEndOilMeter(projectCarFillLog.getVolumn());


                //  按登陆人找该油车最新一条
                Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("oilCarId").as(Long.class), projectOtherDevice.get(0).getId()));
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        //  *****必须倒叙,因为要取最近一条*****
                        query.orderBy(cb.desc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillLog> projectCarFillLogs = projectCarFillLogServiceI.queryWx(spec);
                //  如果有上一次这个油车的记录
                if (projectCarFillLogs.size() > 0) {
                    //  下一次加油开始量 = 上一次加油结束量
                    projectCarFillLog.setStartOilMeter(projectCarFillLogs.get(0).getEndOilMeter());
                    //  下一次加油结束量 = 上一次加油开始量 + 下一次加油的加油量
                    projectCarFillLog.setEndOilMeter(projectCarFillLogs.get(0).getEndOilMeter() + projectCarFillLog.getVolumn());

                }
                //  如果没有上一次这个油车的记录
                else {
                    //  开始设0
                    projectCarFillLog.setStartOilMeter(0L);
                    //  结束量 = 开始量 + 加油量
                    projectCarFillLog.setEndOilMeter(projectCarFillLog.getStartOilMeter() + projectCarFillLog.getVolumn());
                }

                projectCarFillLog.setOilCarCode(projectOtherDevice.get(0).getCode());
                projectCarFillLog.setOilCarId(projectOtherDevice.get(0).getId());
                //  根据加油时间判断班次
                projectCarFillLog.setShift(workDateService.getTargetDateShift(projectCarFillLog.getDate(), projectId));
                projectCarFillLog.setDateIdentification(workDateService.getTargetDateIdentification(projectCarFillLog.getDate(), projectId));
                projectCarFillLogServiceI.save(projectCarFillLog);
            }
            return Result.ok("操作成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            projectCarFillLogServiceI.delete(id);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    /**
     * 油枪合计
     *
     * @param nozzleId
     * @param addOilDate
     * @param request
     * @return
     */
    @PostMapping("/total")
    public Object total(Long nozzleId, Date addOilDate, HttpServletRequest request) {
        //  参数: 某个油枪 + 日期

        //  当日油表初始数 取昨日最后一条
        Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (!ObjectUtils.isEmpty(nozzleId)) {
                    list.add(cb.equal(root.get("nozzleId").as(Long.class), nozzleId));
                }
                //  给定时间偏移到昨天
                DateTime yesterday = DateUtil.offsetDay(new DateTime(addOilDate), -1);
                //  昨天最后一条
                list.add(cb.between(root.get("date").as(Date.class), DateUtil.beginOfDay(yesterday), DateUtil.endOfDay(yesterday)));
                list.add(cb.lessThan(root.get("date").as(Date.class), DateUtil.endOfDay(yesterday)));
                list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarFillLog> projectCarFillLogs = projectCarFillLogServiceI.queryWx(spec);

        //  当日油表终止数
        Specification<ProjectCarFillLog> spec2 = new Specification<ProjectCarFillLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (!ObjectUtils.isEmpty(nozzleId)) {
                    list.add(cb.equal(root.get("nozzleId").as(Long.class), nozzleId));
                }
                list.add(cb.lessThan(root.get("date").as(Date.class), DateUtil.endOfDay(addOilDate)));
                list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarFillLog> projectCarFillLogs2 = projectCarFillLogServiceI.queryWx(spec2);

        //  当月加油升数小计
        Specification<ProjectCarFillLog> spec3 = new Specification<ProjectCarFillLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (!ObjectUtils.isEmpty(nozzleId)) {
                    list.add(cb.equal(root.get("nozzleId").as(Long.class), nozzleId));
                }
                list.add(cb.between(root.get("date").as(Date.class), DateUtil.beginOfMonth(addOilDate), DateUtil.endOfMonth(addOilDate)));
                list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarFillLog> projectCarFillLogs3 = projectCarFillLogServiceI.queryWx(spec3);
        Long monthTotal = 0L;
        for (ProjectCarFillLog projectCarFillLog : projectCarFillLogs3) {
            monthTotal = monthTotal + projectCarFillLog.getVolumn();
        }

        //  历史累计加油升数
        Specification<ProjectCarFillLog> spec4 = new Specification<ProjectCarFillLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (!ObjectUtils.isEmpty(nozzleId)) {
                    list.add(cb.equal(root.get("nozzleId").as(Long.class), nozzleId));
                }
                list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<ProjectCarFillLog> projectCarFillLogs4 = projectCarFillLogServiceI.queryWx(spec4);
        Long historyTotal = 0L;
        for (ProjectCarFillLog projectCarFillLog : projectCarFillLogs4) {
            historyTotal = historyTotal + projectCarFillLog.getVolumn();
        }


        //  当日油表初始数
        Long startOilMeterToday = ObjectUtils.isEmpty(projectCarFillLogs) ? 0 : projectCarFillLogs.get(0).getEndOilMeter();

        //  当日油表终止数
        Long endOilMeterToday = ObjectUtils.isEmpty(projectCarFillLogs2) ? 0 : projectCarFillLogs2.get(0).getEndOilMeter();

        JSONObject jsonObject = new JSONObject();

        //  当日油表初始数
        jsonObject.put("startOilMeterToday", startOilMeterToday);
        //  当日油表终止数
        jsonObject.put("endOilMeterToday", endOilMeterToday);
        //  当日加油升数 = 当日油表终止数 - 当日油表初始数
        jsonObject.put("addOilMeterTodayTotal", endOilMeterToday - startOilMeterToday);
        //  当月加油升数小计
        jsonObject.put("addOilMeterInMonthTotal", monthTotal);
        //  历史累计加油升数
        jsonObject.put("addOilMeterHistoryTotal", historyTotal);
        return jsonObject;
    }
}
