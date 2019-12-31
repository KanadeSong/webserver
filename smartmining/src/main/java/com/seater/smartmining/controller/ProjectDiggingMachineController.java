package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.StatisticsTypeEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.poi.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectDiggingMachine")
public class ProjectDiggingMachineController {
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ProjectUtils projectUtils;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private ProjectDiggingCostAccountingServiceI projectDiggingCostAccountingServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectDiggingMachineEfficiencyServiceI projectDiggingMachineEfficiencyServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_DIGGING_MACHINE_SAVE)
    public Object save(ProjectDiggingMachine projectDiggingMachine, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (projectDiggingMachine.getId() != null && projectDiggingMachine.getId() != 0L) {
                        list.add(cb.notEqual(root.get("id").as(Long.class), projectDiggingMachine.getId()));
                    }
                    list.add(cb.equal(root.get("code").as(String.class), projectDiggingMachine.getCode()));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectDiggingMachine> deviceList = projectDiggingMachineServiceI.queryWx(spec);
            if (deviceList.size() > 0)
                return Result.error("无法保存,项目中已存在该车号:" + projectDiggingMachine.getCode());
            projectDiggingMachine.setProjectId(projectId);
            projectDiggingMachineServiceI.save(projectDiggingMachine);
//            ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.save(projectDiggingMachine);
//            if (projectDiggingMachine.getId() == 0L){
//                //  添加对讲机账号
//                JSONObject interPhoneAccount = projectUtils.createTalkBackUserAccount(Long.parseLong(request.getHeader("projectId")), diggingMachine.getId(), UserObjectType.DiggingMachine,diggingMachine.getCode());
//                diggingMachine.setInterPhoneAccount(interPhoneAccount.getString("account"));
//                diggingMachine.setInterPhoneAccountId(interPhoneAccount.getString("accountId"));
//                projectDiggingMachineServiceI.save(diggingMachine);
//                //  添加对讲机账号 end
//            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @RequestMapping(value = "/batchSave", produces = "application/json")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_DIGGING_MACHINE_SAVE)
    public Result batchSave(@RequestBody List<ProjectDiggingMachine> projectDiggingMachineList) {
        projectDiggingMachineServiceI.batchSave(projectDiggingMachineList);
        return Result.ok();
    }

    @RequestMapping("/delete")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_DIGGING_MACHINE_DELETE)
    public Object delete(Long id) {
        try {
            projectDiggingMachineServiceI.delete(id);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @Transactional
    @RequestMapping(value = "/deleteAll", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids) {
        projectDiggingMachineServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String code, String checkStatus, HttpServletRequest request, @RequestParam(value = "exclude", required = false) ArrayList<Long> exclude, Boolean isAll, Boolean isUsed, Boolean isValid) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //判断是查询全部还是筛选
            boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("该用户没有任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            if (flag) {
                if (isAll != null && isAll) {
                    List<ProjectDiggingMachine> machineList = projectDiggingMachineServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));
                    List<ProjectDiggingMachine> resultList = new ArrayList<>();
                    for (ProjectDiggingMachine machine : machineList) {
                        if (machine.getCheckStatus() == CheckStatus.Checked) {
                            resultList.add(machine);
                        }
                    }
                    return resultList;
                }

                Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        if (code != null && !code.isEmpty())
                            list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                        if (exclude != null && exclude.size() > 0)
                            list.add(root.get("id").as(Long.class).in(exclude).not());
                        list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                        if (CheckStatus.UnCheck.getValue().equals(checkStatus)) {
                            list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                        } else {
                            list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                        }
                        if (isUsed != null) {
                            Subquery querySub = query.subquery(Long.class);
                            Root queryRoot = querySub.from(ProjectScheduled.class);
                            querySub.select(queryRoot.get("diggingMachineId").as(Long.class));
                            if (isUsed) list.add(root.get("id").as(Long.class).in(querySub));
                            else list.add(root.get("id").as(Long.class).in(querySub).not());
                        }
                        list.add(cb.isTrue(root.get("isVaild")));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                Page<ProjectDiggingMachine> rt = projectDiggingMachineServiceI.query(spec, PageRequest.of(cur, page));
                HashMap<String, Object> rtm = new HashMap<>();
                List<ProjectDiggingMachine> content = rt.getContent();
                HashMap<Long, ProjectDiggingMachineEfficiency> effMap = new HashMap<>();
                for (ProjectDiggingMachine machine : content) {
                    List<ProjectDiggingMachineEfficiency> pe = projectDiggingMachineEfficiencyServiceI.queryPage(
                            0,
                            1,
                            machine.getId(),
                            null,
                            machine.getProjectId()
                    ).getContent();
                    if(!pe.isEmpty()) {
                        effMap.put(
                                machine.getId(),
                                pe.get(0)
                        );
                    }
                }
                rtm.put("content", content);
                rtm.put("effMap", effMap);
                rtm.put("empty", rt.isEmpty());
                rtm.put("first", rt.isFirst());
                rtm.put("last", rt.isLast());
                rtm.put("number", rt.getNumber());
                rtm.put("numberOfElements", rt.getNumberOfElements());
                rtm.put("pageable", rt.getPageable());
                rtm.put("size", rt.getSize());
                rtm.put("sort", rt.getSort());
                rtm.put("totalElements", rt.getTotalElements());
                rtm.put("totalPages", rt.getTotalPages());
                return rtm;
            } else {
                Specification<ScheduleMachine> spec = new Specification<ScheduleMachine>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ScheduleMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        if (code != null && !code.isEmpty())
                            list.add(cb.like(root.get("machineCode").as(String.class), "%" + code + "%"));
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByQuery(spec);
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
                Page<ProjectSchedule> schedulePage = projectScheduleServiceI.query(specification, PageRequest.of(cur, page));
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : schedulePage.getContent()) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                List<ScheduleMachine> scheduleMachines = new ArrayList<>();
                for (ScheduleMachine machine : scheduleMachineList) {
                    if (groupCodeList.contains(machine.getGroupCode()))
                        scheduleMachines.add(machine);
                }
                List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
                //创建挖机索引
                Map<String, Integer> machineIndex = new HashMap<>();
                for (int i = 0; i < scheduleMachines.size(); i++) {
                    machineIndex.put(scheduleMachines.get(i).getMachineCode(), i);
                }
                List<ProjectDiggingMachine> responseList = new ArrayList<>();
                List<ProjectDiggingMachineEfficiency> lastHourCarNums = new ArrayList<>();
                for (ProjectDiggingMachine machine : projectDiggingMachineList) {
                    Integer index = machineIndex.get(machine.getCode());
                    if (index != null) {
                        responseList.add(machine);
                        List<ProjectDiggingMachineEfficiency> pe = projectDiggingMachineEfficiencyServiceI.queryPage(
                                0,
                                1,
                                machine.getProjectId(),
                                null,
                                machine.getId()
                        ).getContent();
                        if(!pe.isEmpty()) {
                            lastHourCarNums.add(
                                    pe.get(0)
                            );
                        }
                    }
                }
                Map result = new HashMap();
                result.put("content", responseList);
                result.put("totalElements", responseList.size());
                return result;
            }
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/queryByPlat")
    public Result queryByPlat(Integer current, Integer pageSize, Long projectId, String code, String name, String ownerName, Long brandId, Long modelId, String driverName, Date startTime, Date endTime) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (projectId != null && projectId != 0)
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                if (StringUtils.isNotEmpty(code))
                    list.add(criteriaBuilder.like(root.get("code").as(String.class), "%" + code + "%"));
                if (StringUtils.isNotEmpty(ownerName))
                    list.add(criteriaBuilder.like(root.get("ownerName").as(String.class), "%" + ownerName + "%"));
                if (brandId != null && brandId != 0)
                    list.add(criteriaBuilder.equal(root.get("brandId").as(Long.class), brandId));
                if (modelId != null && modelId != 0)
                    list.add(criteriaBuilder.equal(root.get("modelId").as(Long.class), modelId));
                if (StringUtils.isNotEmpty(driverName))
                    list.add(criteriaBuilder.like(root.get("driverName").as(String.class), "%" + driverName + "%"));
                if (startTime != null && endTime != null)
                    list.add(criteriaBuilder.between(root.get("addTime").as(Date.class), startTime, endTime));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectDiggingMachineServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/queryByValid")
    public Result queryByValid(HttpServletRequest request, Integer current, Integer pageSize, Boolean valid, String code){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (projectId != null && projectId != 0)
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                if (StringUtils.isNotEmpty(code))
                    list.add(criteriaBuilder.like(root.get("code").as(String.class), "%" + code + "%"));
                if(valid != null)
                    list.add(criteriaBuilder.equal(root.get("isVaild"), valid));
                list.add(criteriaBuilder.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectDiggingMachineServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/setICCard")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_DIGGING_MACHINE_SAVE)
    public Result setICCardByCarId(Long diggingMachineId, String icCardNumber, Boolean icCardStatus) {
        try {
            projectDiggingMachineServiceI.setICCardByDiggingMachineId(diggingMachineId, icCardNumber, icCardStatus);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/queryBySelected")
    public Result queryBySelected(HttpServletRequest request, Boolean selected) {
        /*try {*/
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if(selected == null)
                selected = false;
            List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getAllByProjectIdAndIsVaildAndSelected(projectId, selected);
            //判断是查询全部还是筛选
            /*boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("该用户没有任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            if (flag) {
                return Result.ok(projectDiggingMachineList);
            } else {
                List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectId(projectId);
                List<ScheduleMachine> scheduleMachines = new ArrayList<>();
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                String params = "\"" + sysUser.getId() + "\"";
                List<ProjectSchedule> projectScheduleList = projectScheduleServiceI.getAllByProjectIdAndManagerIdOrderById(projectId, params);
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : projectScheduleList) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                for (ScheduleMachine machine : scheduleMachineList) {
                    if (groupCodeList.contains(machine.getGroupCode()))
                        scheduleMachines.add(machine);
                }
                //创建有权限的挖机索引
                Map<String, Integer> machineIndex = new HashMap<>();
                for (int i = 0; i < scheduleMachines.size(); i++) {
                    machineIndex.put(scheduleMachines.get(i).getMachineCode(), i);
                }
                List<ProjectDiggingMachine> responseList = new ArrayList<>();
                for (ProjectDiggingMachine machine : projectDiggingMachineList) {
                    Integer index = machineIndex.get(machine.getCode());
                    if (index != null)
                        responseList.add(machine);
                }*/
                return Result.ok(projectDiggingMachineList);
        /*    }
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }*/
    }

    @RequestMapping("/valid")
    @Transactional
    public Result valid(HttpServletRequest request, @RequestBody List<Long> ids) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            List<ProjectDiggingMachine> machineList = new ArrayList<>();
            for (Long id : ids) {
                ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(id);
                if (machine != null) {
                    machineList.add(machine);
                    machine.setVaild(false);
                    machine.setSelected(false);
                    ScheduleMachine scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, machine.getCode());
                    if (scheduleMachine != null) {
                        scheduleMachine.setIsVaild(false);
                        scheduleMachineServiceI.save(scheduleMachine);
                    }
                }
            }
            projectDiggingMachineServiceI.batchSave(machineList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 出勤台时 装载总车数 耗油
     *
     * @param request
     * @param choose    1-月   2-年   3-历史
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @RequestMapping("/report")
    public Result machineEcharts(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Map resultMap = new HashMap();
        List<Map> workList = null;
        List<Map> timeList = null;
        List<Map> fillList = null;
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                workList = projectCarWorkInfoServiceI.getDiggingWorkReport(projectId, startTime, endTime);
                timeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReport(projectId, startTime, endTime);
                fillList = projectCarFillLogServiceI.getFillLogReport(projectId, startTime, endTime, CarType.DiggingMachine.getValue());
                break;
            case 2:
                workList = projectCarWorkInfoServiceI.getDiggingWorkReportMonth(projectId, startTime, endTime);
                timeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReportByMonth(projectId, startTime, endTime);
                fillList = projectCarFillLogServiceI.getFillLogReportMonth(projectId, startTime, endTime, CarType.DiggingMachine.getValue());
                break;
            case 3:
                workList = projectCarWorkInfoServiceI.getDiggingWorkReportHistory(projectId, endTime);
                timeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReportByHistory(projectId, endTime);
                fillList = projectCarFillLogServiceI.getFillLogReportHistory(projectId, endTime, CarType.DiggingMachine.getValue());
                break;
        }
        //工作信息索引
        Map<String, Integer> workMapIndex = new HashMap();
        //工作时间索引
        Map<String, Integer> timeMapIndex = new HashMap();
        //加油索引
        Map<String, Integer> fillMapIndex = new HashMap();
        if (choose != 3) {
            for (int i = 0; i < workList.size(); i++) {
                ;
                Date date = DateUtils.stringFormatDate(workList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                String key = null;
                if (choose == 1)
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
                else
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
                workMapIndex.put(key, i);
            }
            for (int i = 0; i < timeList.size(); i++) {
                Date date = DateUtils.stringFormatDate(timeList.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                String key = null;
                if (choose == 1)
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
                else
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
                timeMapIndex.put(key, i);
            }
            for (int i = 0; i < fillList.size(); i++) {
                Date date = DateUtils.stringFormatDate(fillList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                String key = null;
                if (choose == 1)
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
                else
                    key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
                fillMapIndex.put(key, i);
            }
            for (int i = 0; i < dateList.size(); i++) {
                String key = dateList.get(i);
                Integer workIndex = workMapIndex.get(key);
                if (workIndex == null) {
                    Map map = new HashMap();
                    map.put("count", 0);
                    if (choose == 1)
                        map.put("date_identification", DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT));
                    else
                        map.put("date_identification", key);
                    map.put("pricing_type", 0);
                    workList.add(map);
                }
                Integer timeIndex = timeMapIndex.get(key);
                if (timeIndex == null) {
                    Map map = new HashMap();
                    map.put("workTime", 0);
                    if (choose == 1)
                        map.put("time", DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT));
                    else
                        map.put("time", key);
                    map.put("pricing_type_enums", 0);
                    timeList.add(map);
                }
                Integer fillIndex = fillMapIndex.get(key);
                if (fillIndex == null) {
                    Map map = new HashMap();
                    map.put("volumn", 0);
                    map.put("pricing_type_enums", 0);
                    if (choose == 1)
                        map.put("date_identification", DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT));
                    else
                        map.put("date_identification", key);
                    fillList.add(map);
                }
            }
        }
        resultMap.put("work", workList);
        resultMap.put("time", timeList);
        resultMap.put("fill", fillList);
        return Result.ok(resultMap);
    }

    /**
     * 挖机单位成本 挖机毛利率
     *
     * @param request
     * @param choose    1-月    2-年   3-历史
     * @param startTime
     * @param endTime
     * @return
     */
    /*@RequestMapping("/profit")
    public Result profit(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> accountingList = new ArrayList<>();
        List<Map> totalList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1)
                startTime = DateUtils.getWeekAgo(endTime);
            else if (choose == 2)
                startTime = DateUtils.getHalfYearAgo(endTime);
            else
                startTime = new Date();
        }
        startTime = DateUtils.getEndDateByNow(startTime);
        endTime = DateUtils.getEndDateByNow(endTime);
        switch (choose) {
            case 1:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTime(projectId, startTime, endTime);
                break;
            case 2:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
                break;
            case 3:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeHistory(projectId, endTime);
                break;
        }
        for (int i = 0; i < totalList.size(); i++) {
            //计时总金额
            Long totalAmountByTimer = Long.parseLong(totalList.get(i).get("totalAmountByTimer").toString());
            //计方总金额
            Long totalAmountByCubic = Long.parseLong(totalList.get(i).get("totalAmountByCubic").toString());
            //计时总车数
            Long totalCountByTimer = Long.parseLong(totalList.get(i).get("totalCountByTimer").toString());
            //计方总车数
            Long totalCountByCubic = Long.parseLong(totalList.get(i).get("totalCountByCubic").toString());
            //计时总方量
            Long totalCubicByTimer = Long.parseLong(totalList.get(i).get("totalCubicByTimer").toString());
            //计方总方量
            Long totalCubicByCubic = Long.parseLong(totalList.get(i).get("totalCubicByCubic").toString());
            //统计类型
            StatisticsTypeEnums statisticsType = StatisticsTypeEnums.convert(Integer.valueOf(totalList.get(i).get("statistics_type").toString()));
            //日期
            Date reportDate = null;
            if (choose == 1) {
                reportDate = DateUtils.stringFormatDate(totalList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
            } else {
                reportDate = DateUtils.stringFormatDate(totalList.get(i).get("reportDate").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            //挖机计时单位成本（分/方）
            Long unitCostByTimerMin = totalCubicByTimer / 1000000L != 0 ? totalAmountByTimer / (totalCubicByTimer / 1000000L) : 0L;
            BigDecimal unitCostByTimer = new BigDecimal((float) unitCostByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //挖机包方单位成本
            Long unitCostByCubicMin = totalCubicByCubic / 1000000L != 0 ? totalAmountByCubic / (totalCubicByCubic / 1000000L) : 0L;
            BigDecimal unitCostByCubic = new BigDecimal((float) unitCostByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //挖机计时毛利润
            Long grossProfitByTimerMin = totalCountByTimer != 0 ? totalAmountByTimer / totalCountByTimer : 0L;
            BigDecimal grossProfitByTimer = new BigDecimal((float) grossProfitByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //挖机包方毛利润
            Long grossProfitByCubicMin = totalCountByCubic != 0 ? totalAmountByCubic / totalCountByCubic : 0L;
            BigDecimal grossProfitByCubic = new BigDecimal((float) grossProfitByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Map map = new HashMap();
            map.put("unit_cost_by_timer", unitCostByTimer);
            map.put("unit_cost_by_cubic", unitCostByCubic);
            map.put("gross_profit_by_timer", grossProfitByTimer);
            map.put("gross_profit_by_cubic", grossProfitByCubic);
            map.put("statistics_type", statisticsType);
            map.put("report_date", reportDate);
            accountingList.add(map);
        }
        return Result.ok(accountingList);
    }*/

    /**
     * 油耗 总金额 用油金额
     *
     * @param request
     * @param choose
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping("/amount")
    public Result amountReport(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> accountingList = new ArrayList<>();
        List<Map> totalList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTime(projectId, startTime, endTime);
                break;
            case 2:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
                break;
            case 3:
                totalList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeHistory(projectId, endTime);
                break;
        }
        //生成索引
        Map<String, Integer> totalMapIndex = new HashMap<>();
        for (int i = 0; i < totalList.size(); i++) {
            //日期
            Date reportDate = null;
            String key = null;
            if (choose == 1) {
                reportDate = DateUtils.stringFormatDate(totalList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                reportDate = DateUtils.stringFormatDate(totalList.get(i).get("reportDate").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalMapIndex.put(key, i);
        }
        for (int i = 0; i < dateList.size(); i++) {
            String date = dateList.get(i);
            Integer totalIndex = totalMapIndex.get(date);
            //计时总用油
            Long fillCountByTimerMl = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("fillCountByTimer").toString()) : 0L;
            BigDecimal fillCountByTimer = new BigDecimal((float) fillCountByTimerMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方总用油
            Long fillCountByCubicMl = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("fillCountByCubic").toString()) : 0L;
            BigDecimal fillCountByCubic = new BigDecimal((float) fillCountByCubicMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计时用油总金额
            Long amountByFillByTimerMin = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("amountByFillByTimer").toString()) : 0L;
            BigDecimal amountByFillByTimer = new BigDecimal((float) amountByFillByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //包方用油总金额
            Long amountByFillByCubicMin = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("amountByFillByCubic").toString()) : 0L;
            BigDecimal amountByFillByCubic = new BigDecimal((float) amountByFillByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计时总金额
            Long totalAmountByTimerMin = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("totalAmountByTimer").toString()) : 0L;
            BigDecimal totalAmountByTimer = new BigDecimal((float) totalAmountByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方总金额
            Long totalAmountByCubicMin = totalIndex != null ? Long.parseLong(totalList.get(totalIndex).get("totalAmountByCubic").toString()) : 0L;
            BigDecimal totalAmountByCubic = new BigDecimal((float) totalAmountByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Map map = new HashMap();
            map.put("totalAmountByTimer", totalAmountByTimer);
            map.put("totalAmountByCubic", totalAmountByCubic);
            map.put("totalAmount", totalAmountByTimer.add(totalAmountByCubic));
            map.put("amountByFillByTimer", amountByFillByTimer);
            map.put("amountByFillByCubic", amountByFillByCubic);
            map.put("amountByFill", amountByFillByTimer.add(amountByFillByCubic));
            map.put("fillCountByTimer", fillCountByTimer);
            map.put("fillCountByCubic", fillCountByCubic);
            map.put("fillCount", fillCountByTimer.add(fillCountByCubic));
            map.put("reportDate", date);
            accountingList.add(map);
        }
        return Result.ok(accountingList);
    }

    /**
     * 装载效率
     * 计时平均装了多少车
     * 计方平均装了多少车
     * 总共装了多少车
     *
     * @param request
     * @param choose
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping("/qualification")
    public Result qualification(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        //计时总车数
        List<Map> totalCountByTimer = new ArrayList<>();
        //计方总车数
        List<Map> totalCountByCubic = new ArrayList<>();
        //总车数
        List<Map> totalCountList = new ArrayList<>();
        //计时总台时
        List<Map> totalTimeByTimer = new ArrayList<>();
        //计方总台时
        List<Map> totalTimeByCubic = new ArrayList<>();
        //总台时
        List<Map> totalTimeList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        //日期集合
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                totalCountByTimer = projectCarWorkInfoServiceI.getTotalCountByTimer(projectId, startTime, endTime, PricingTypeEnums.Hour.getValue());
                totalCountByCubic = projectCarWorkInfoServiceI.getTotalCountByTimer(projectId, startTime, endTime, PricingTypeEnums.Cube.getValue());
                totalCountList = projectCarWorkInfoServiceI.getTotalCount(projectId, startTime, endTime);
                totalTimeByTimer = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingType(projectId, startTime, endTime, PricingTypeEnums.Hour.getValue());
                totalTimeByCubic = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingType(projectId, startTime, endTime, PricingTypeEnums.Cube.getValue());
                totalTimeList = projectWorkTimeByDiggingServiceI.getTotalDiggingTimeReport(projectId, startTime, endTime);
                break;
            case 2:
                totalCountByTimer = projectCarWorkInfoServiceI.getTotalCountByTimerMonth(projectId, startTime, endTime, PricingTypeEnums.Hour.getValue());
                totalCountByCubic = projectCarWorkInfoServiceI.getTotalCountByTimerMonth(projectId, startTime, endTime, PricingTypeEnums.Cube.getValue());
                totalCountList = projectCarWorkInfoServiceI.getTotalCountMonth(projectId, startTime, endTime);
                totalTimeByTimer = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingTypeMonth(projectId, startTime, endTime, PricingTypeEnums.Hour.getValue());
                totalTimeByCubic = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingTypeMonth(projectId, startTime, endTime, PricingTypeEnums.Cube.getValue());
                totalTimeList = projectWorkTimeByDiggingServiceI.getTotalDiggingTimeReportByMonth(projectId, startTime, endTime);
                break;
            case 3:
                totalCountByTimer = projectCarWorkInfoServiceI.getTotalCountByTimerHistory(projectId, endTime, PricingTypeEnums.Hour.getValue());
                totalCountByCubic = projectCarWorkInfoServiceI.getTotalCountByTimerHistory(projectId, endTime, PricingTypeEnums.Cube.getValue());
                totalCountList = projectCarWorkInfoServiceI.getTotalCountHistory(projectId, startTime, endTime);
                totalTimeByTimer = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingTypeHistory(projectId, endTime, PricingTypeEnums.Hour.getValue());
                totalTimeByCubic = projectWorkTimeByDiggingServiceI.getWorkTimeByPricingTypeHistory(projectId, endTime, PricingTypeEnums.Cube.getValue());
                totalTimeList = projectWorkTimeByDiggingServiceI.getTotalDiggingTimeReportByHistory(projectId, endTime);
                break;
        }
        //计时总车数索引
        Map<String, Integer> totalCountByTimerMap = new HashMap<>();
        for (int i = 0; i < totalCountByTimer.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalCountByTimer.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalCountByTimer.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalCountByTimerMap.put(key, i);
        }
        //计方总车数索引
        Map<String, Integer> totalCountByCubicMap = new HashMap<>();
        for (int i = 0; i < totalCountByCubic.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalCountByCubic.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalCountByCubic.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalCountByCubicMap.put(key, i);
        }
        //总车数索引
        Map<String, Integer> totalCountMap = new HashMap<>();
        for (int i = 0; i < totalCountList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalCountList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalCountList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalCountMap.put(key, i);
        }
        //计时 总台时索引
        Map<String, Integer> totalTimeByTimerMap = new HashMap<>();
        for (int i = 0; i < totalTimeByTimer.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalTimeByTimer.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalTimeByTimer.get(i).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalTimeByTimerMap.put(key, i);
        }
        //计方 总台时索引
        Map<String, Integer> totalTimeByCubicMap = new HashMap<>();
        for (int i = 0; i < totalTimeByCubic.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalTimeByCubic.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalTimeByCubic.get(i).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalTimeByCubicMap.put(key, i);
        }
        //总台时索引
        Map<String, Integer> totalTimeMap = new HashMap<>();
        for (int i = 0; i < totalTimeList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(totalTimeList.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(totalTimeList.get(i).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            totalTimeMap.put(key, i);
        }
        for (int i = 0; i < dateList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            }else {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            //计时总车数下标
            Integer totalCountByTimerIndex = totalCountByTimerMap.get(key);
            //计时总车数
            Long countByTimer = totalCountByTimerIndex != null ? Long.parseLong(totalCountByTimer.get(totalCountByTimerIndex).get("count").toString()) : 0L;
            //计方总车数下标
            Integer totalCountByCubicIndex = totalCountByCubicMap.get(key);
            //计方总车数
            Long countByCubic = totalCountByCubicIndex != null ? Long.parseLong(totalCountByCubic.get(totalCountByCubicIndex).get("count").toString()) : 0L;
            //总车数下标
            Integer totalCountIndex = totalCountMap.get(key);
            //总车数
            Long totalCount = totalCountIndex != null ? Long.parseLong(totalCountList.get(totalCountIndex).get("count").toString()) : 0L;
            //计时总台时下标
            Integer totalTimeByTimerIndex = totalTimeByTimerMap.get(key);
            //计时总台时
            BigDecimal timeByTimer = totalTimeByTimerIndex != null ? new BigDecimal(totalTimeByTimer.get(totalTimeByTimerIndex).get("workTime").toString()) : new BigDecimal(0);
            //计方总台时下标
            Integer totalTimeByCubicIndex = totalTimeByCubicMap.get(key);
            //计方总台时
            BigDecimal timeByCubic = totalTimeByCubicIndex != null ? new BigDecimal(totalTimeByCubic.get(totalTimeByCubicIndex).get("workTime").toString()) : new BigDecimal(0);
            //总台时下标
            Integer totalTimeIndex = totalTimeMap.get(key);
            //总台时
            BigDecimal totalTime = totalTimeIndex != null ? new BigDecimal(totalTimeList.get(totalTimeIndex).get("workTime").toString()) : new BigDecimal(0);

            //计时效率
            BigDecimal qualByTimer = timeByTimer.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(countByTimer).divide(timeByTimer, 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //计方效率
            BigDecimal qualByCubic = timeByCubic.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(countByCubic).divide(timeByCubic, 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //总效率
            BigDecimal qualification = totalTime.compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal(totalCount).divide(totalTime, 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("date", date);
            map.put("qualByTimer", qualByTimer);
            map.put("qualByCubic", qualByCubic);
            map.put("qualification", qualification);
            //计时总车数
            map.put("carsCountByTimer", countByTimer);
            //计方总车数
            map.put("carsCountByCubic", countByCubic);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 合格率
     *
     * @param request
     * @param choose
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping("/passPercent")
    public Result passPercent(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> passList = new ArrayList<>();
        List<Map> countList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReport(projectId, startTime, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDate(projectId, startTime, endTime);
                break;
            case 2:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReportMonth(projectId, startTime, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDateMonth(projectId, startTime, endTime);
                break;
            case 3:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReportHistory(projectId, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDateHistory(projectId, endTime);
                break;
        }
        //合格车数索引
        Map<String, Integer> passCountMap = new HashMap<>();
        for (int i = 0; i < passList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            passCountMap.put(key, i);
        }
        //总车数索引
        Map<String, Integer> countMap = new HashMap<>();
        for (int i = 0; i < countList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(countList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(countList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            countMap.put(key, i);
        }
        for (int i = 0; i < dateList.size(); i++) {
            Date date = null;
            String key = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            Integer passIndex = passCountMap.get(key);
            Integer countIndex = countMap.get(key);
            //合格车数
            Long passCount = passIndex != null ? Long.parseLong(passList.get(passIndex).get("count").toString()) : 0L;
            //全部车数
            Long totalCount = countIndex != null ? Long.parseLong(countList.get(countIndex).get("count").toString()) : 0L;
            //合格率
            BigDecimal passPercent = totalCount != 0L ? new BigDecimal((float) passCount / totalCount).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("pass", passPercent);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }


    /*@RequestMapping("/qualification")
    public Result qualification(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        List<Map> passList = new ArrayList<>();
        List<Map> unPassList = new ArrayList<>();
        List<Map> workTimeList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1)
                startTime = DateUtils.getWeekAgo(endTime);
            else if (choose == 2)
                startTime = DateUtils.getHalfYearAgo(endTime);
            else
                startTime = new Date();
        }
        startTime = DateUtils.getEndDateByNow(startTime);
        endTime = DateUtils.getEndDateByNow(endTime);
        switch (choose) {
            case 1:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReport(projectId, startTime, endTime);
                unPassList = projectCarWorkInfoServiceI.getQualificationReport(projectId, startTime, endTime);
                workTimeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReport(projectId, startTime, endTime);
                break;
            case 2:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReportMonth(projectId, startTime, endTime);
                unPassList = projectCarWorkInfoServiceI.getQualificationReportMonth(projectId, startTime, endTime);
                workTimeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReportByMonth(projectId, startTime, endTime);
                break;
            case 3:
                passList = projectCarWorkInfoServiceI.getDiggingWorkReportHistory(projectId, endTime);
                unPassList = projectCarWorkInfoServiceI.getQualificationReportHistory(projectId, endTime);
                workTimeList = projectWorkTimeByDiggingServiceI.getDiggingTimeReportByHistory(projectId, endTime);
                break;
        }
        //生成合格总数的索引
        Map<String, Integer> unPassIndex = new HashMap<>();
        for (int i = 0; i < unPassList.size(); i++) {
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.DATEFORMAT);
            } else {
                date = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            Integer pricingType = Integer.valueOf(passList.get(i).get("pricing_type").toString());
            unPassIndex.put(String.valueOf(date.getTime()) + pricingType, i);
        }
        //生成工作时间的索引
        Map<String, Integer> workTimeIndex = new HashMap<>();
        for (int i = 0; i < workTimeList.size(); i++) {
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(workTimeList.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(workTimeList.get(i).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            Integer pricingType = Integer.valueOf(workTimeList.get(i).get("pricing_type_enums").toString());
            workTimeIndex.put(String.valueOf(date.getTime() + pricingType), i);
        }
        for (int i = 0; i < passList.size(); i++) {
            //总数量
            Long countPass = Long.parseLong(passList.get(i).get("count").toString());
            //日期
            Date datePass = null;
            if (choose == 1) {
                datePass = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.DATEFORMAT);
            } else {
                datePass = DateUtils.stringFormatDate(passList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            //计价方式
            Integer pricingType = Integer.valueOf(passList.get(i).get("pricing_type").toString());
            String key = String.valueOf(datePass.getTime()) + pricingType;
            Integer index = unPassIndex.get(key);
            //不合格数量
            Long countUnPass = index != null ? Long.parseLong(unPassList.get(index).get("count").toString()) : 0L;
            //当天工作时间索引
            Integer workIndex = workTimeIndex.get(key);
            //工作总时间
            Long workTime = workIndex != null ? Long.parseLong(workTimeList.get(workIndex).get("workTime").toString()) : 0L;
            //合格率
            BigDecimal pass = countPass != 0L ? new BigDecimal((float) countUnPass / countPass).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //装载效率
            BigDecimal efficiency = workTime / 3600L != 0L ? new BigDecimal(countPass / ((float) workTime / 3600L)).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("pass", pass);
            map.put("efficiency", efficiency);
            map.put("pricingType", pricingType);
            map.put("date", datePass);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }*/

    /**
     * 单位油耗
     * 耗油 计时单位成本 计方单位成本 总单位成本 挖机毛利率（不含油）
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/oilConsumption")
    public Result oilConsumption(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        List<Map> accountingList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTime(projectId, startTime, endTime);
                break;
            case 2:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
                break;
            case 3:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeHistory(projectId, endTime);
                break;
        }
        Map<String, Integer> accountingMapIndex = new HashMap<>();
        for (int i = 0; i < accountingList.size(); i++) {
            Date reportDate = null;
            String key = null;
            if (choose == 1) {
                reportDate = DateUtils.stringFormatDate(accountingList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
                key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                reportDate = DateUtils.stringFormatDate(accountingList.get(i).get("reportDate").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.MONTHDAYFORMAT);
            }
            accountingMapIndex.put(key, i);
        }
        for (int i = 0; i < dateList.size(); i++) {
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
            }
            String key = dateList.get(i);
            Integer accountingIndex = accountingMapIndex.get(key);
            //计时总金额
            Long totalAmountByTimerMin = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalAmountByTimer").toString()) : 0L;
            BigDecimal totalAmountByTimer = new BigDecimal((float) totalAmountByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方总金额
            Long totalAmountByCubicMin = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalAmountByCubic").toString()) : 0L;
            BigDecimal totalAmountByCubic = new BigDecimal((float) totalAmountByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计时用油金额
            Long amountByFillByTimerMin = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("amountByFillByTimer").toString()) : 0L;
            BigDecimal amountByFillByTimer = new BigDecimal((float) amountByFillByTimerMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方用油金额
            Long amountByFillByCubicMin = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("amountByFillByCubic").toString()) : 0L;
            BigDecimal amountByFillByCubic = new BigDecimal((float) amountByFillByCubicMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //总金额
            BigDecimal totalAmount = totalAmountByTimer.add(totalAmountByCubic);
            //不含油总金额
            BigDecimal totalAmountWithOutFill = totalAmount.subtract(amountByFillByTimer).subtract(amountByFillByCubic);
            //计时台时
            BigDecimal workTimeByTimer = accountingIndex != null ? new BigDecimal(accountingList.get(accountingIndex).get("workTimeByTimer").toString()) : new BigDecimal(0);
            //BigDecimal workTimeByTimer = new BigDecimal((float) workTimeByTimerSec / 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方台时
            BigDecimal workTimeByCubic = accountingIndex != null ? new BigDecimal(accountingList.get(accountingIndex).get("workTimeByCubic").toString()) : new BigDecimal(0);
            //BigDecimal workTimeByCubic = new BigDecimal((float) workTimeByCubicSec / 3600L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //总台时
            BigDecimal workTime = workTimeByTimer.add(workTimeByCubic);
            //计时总车数
            Long totalCountByTimer = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalCountByTimer").toString()) : 0L;
            //包方总车数
            Long totalCountByCubic = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalCountByCubic").toString()) : 0L;
            //总车数
            Long totalCount = totalCountByTimer + totalCountByCubic;
            //计时油量
            Long fillCountByTimerMl = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("fillCountByTimer").toString()) : 0L;
            BigDecimal fillCountByTimer = new BigDecimal((float) fillCountByTimerMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方油量
            Long fillCountByCubicMl = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("fillCountByCubic").toString()) : 0L;
            BigDecimal fillCountByCubic = new BigDecimal((float) fillCountByCubicMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //总油量
            BigDecimal fillCount = fillCountByTimer.add(fillCountByCubic);
            //计时方量
            Long totalCubicByTimerCm = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalCubicByTimer").toString()) : 0L;
            BigDecimal totalCubicByTimer = new BigDecimal((float) totalCubicByTimerCm / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方方量
            Long totalCubicByCubicCm = accountingIndex != null ? Long.parseLong(accountingList.get(accountingIndex).get("totalCubicByCubic").toString()) : 0L;
            BigDecimal totalCubicByCubic = new BigDecimal((float) totalCubicByCubicCm / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //总方量
            BigDecimal totalCubic = totalCubicByTimer.add(totalCubicByCubic);
            //耗油
            BigDecimal oysterSauce = workTime.compareTo(BigDecimal.ZERO) != 0 ? fillCount.divide(workTime, 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //计时单位成本
            BigDecimal costByTimer = totalCubicByTimer.compareTo(BigDecimal.ZERO) != 0 ? totalAmountByTimer.divide(totalCubicByTimer, 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //计方单位成本
            BigDecimal costByCubic = totalCubicByCubic.compareTo(BigDecimal.ZERO) != 0 ? totalAmountByCubic.divide(totalCubicByCubic, 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //总单位成本
            BigDecimal cost = totalCubic.compareTo(BigDecimal.ZERO) != 0 ? totalAmount.divide(totalCubic, 2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //挖机毛利率
            BigDecimal gross = totalCount != 0 ? totalAmountWithOutFill.divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);


            Map map = new HashMap();
            map.put("oysterSauce", oysterSauce);
            map.put("costByTimer", costByTimer);
            map.put("costByCubic", costByCubic);
            map.put("cost", cost);
            map.put("gross", gross);
            map.put("reportDate", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }
    /*@RequestMapping("/oilConsumption")
    public Result oilConsumption(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        List<Map> accountingList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1)
                startTime = DateUtils.getWeekAgo(endTime);
            else if (choose == 2)
                startTime = DateUtils.getHalfYearAgo(endTime);
            else
                startTime = new Date();
        }
        startTime = DateUtils.getEndDateByNow(startTime);
        endTime = DateUtils.getEndDateByNow(endTime);
        switch (choose) {
            case 1:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTime(projectId, startTime, endTime);
                break;
            case 2:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeMonth(projectId, startTime, endTime);
                break;
            case 3:
                accountingList = projectDiggingCostAccountingServiceI.getAllByProjectIdAndTimeHistory(projectId, endTime);
                break;
        }
        for (int i = 0; i < accountingList.size(); i++) {
            //计时总车数
            Long totalCountByTimer = Long.parseLong(accountingList.get(i).get("totalCountByTimer").toString());
            //包方总车数
            Long totalCountByCubic = Long.parseLong(accountingList.get(i).get("totalCountByCubic").toString());
            //计时油量
            Long fillCountByTimer = Long.parseLong(accountingList.get(i).get("fillCountByTimer").toString());
            //计方油量
            Long fillCountByCubic = Long.parseLong(accountingList.get(i).get("fillCountByCubic").toString());
            //计时方量
            Long totalCubicByTimer = Long.parseLong(accountingList.get(i).get("totalCubicByTimer").toString());
            //计方计方
            Long totalCubicByCubic = Long.parseLong(accountingList.get(i).get("totalCubicByCubic").toString());
            //计时 毫升/车
            Long avgCarsByTimerFillByMl = fillCountByTimer != 0L ? totalCountByTimer / fillCountByTimer : 0L;
            BigDecimal avgCarsByTimerFill = new BigDecimal((float) avgCarsByTimerFillByMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计方 毫升/车
            Long avgCarsByCubicFillByMl = fillCountByCubic != 0L ? totalCountByCubic / fillCountByCubic : 0L;
            BigDecimal avgCarsByCubicFill = new BigDecimal((float) avgCarsByCubicFillByMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //计时 毫升/方
            Long avgCubicByTimerFillByMl = totalCubicByTimer / 1000000L != 0L ? fillCountByTimer / (totalCubicByTimer / 1000000L) : 0L;
            BigDecimal avgCubicByTimerFill = new BigDecimal((float) avgCubicByTimerFillByMl / 1000L).setScale(2, BigDecimal.ROUND_CEILING);
            //计方 毫升/方
            Long avgCubicByCubicFillByMl = totalCubicByCubic / 1000000L != 0L ? fillCountByCubic / (totalCubicByCubic / 1000000L) : 0L;
            BigDecimal avgCubicByCubicFill = new BigDecimal((float) avgCubicByCubicFillByMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Date reportDate = null;
            if (choose == 1) {
                reportDate = DateUtils.stringFormatDate(accountingList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
            } else {
                reportDate = DateUtils.stringFormatDate(accountingList.get(i).get("reportDate").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            Map map = new HashMap();
            map.put("avgCarsByTimerFill", avgCarsByTimerFill);
            map.put("avgCarsByCubicFill", avgCarsByCubicFill);
            map.put("avgCubicByTimerFill", avgCubicByTimerFill);
            map.put("avgCubicByCubicFill", avgCubicByCubicFill);
            map.put("reportDate", reportDate);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }*/

    /**
     * 出勤数 车数 出勤率
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    /*@RequestMapping("/carInfo")
    public Result carInfo(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        List<Map> carInfoList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0) {
            if (choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
            } else if (choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        switch (choose) {
            case 1:
                carInfoList = projectWorkTimeByDiggingServiceI.getDiggingTimeInfo(projectId, startTime, endTime);
                break;
            case 2:
                carInfoList = projectWorkTimeByDiggingServiceI.getDiggingTimeInfoMonth(projectId, startTime, endTime);
                break;
            case 3:
                carInfoList = projectWorkTimeByDiggingServiceI.getDiggingTimeInfoHistory(projectId, endTime);
                break;
        }
        //获取当前项目的总车辆数
        Map countMap = projectDiggingMachineServiceI.getAllCountByProjectId(projectId);
        Long carsCount = Long.parseLong(countMap.get("count").toString());
        //出勤数
        Long count = 1L;
        for (int i = 0; i < carInfoList.size(); i++) {
            //计价方式
            Integer pricingType = Integer.valueOf(carInfoList.get(i).get("pricing_type_enums").toString());
            PricingTypeEnums pricingTypeEnums = PricingTypeEnums.convert(pricingType);
            //日期
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(carInfoList.get(i).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(carInfoList.get(i).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            if (i < carInfoList.size() - 1) {
                Date nextDate = new Date(0);
                if (choose == 1) {
                    nextDate = DateUtils.stringFormatDate(carInfoList.get(i + 1).get("time").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                } else {
                    nextDate = DateUtils.stringFormatDate(carInfoList.get(i + 1).get("time").toString(), SmartminingConstant.MONTHDAYFORMAT);
                }
                //计价方式
                Integer nextPricingType = Integer.valueOf(carInfoList.get(i + 1).get("pricing_type_enums").toString());
                if (pricingType == nextPricingType && date.getTime() == nextDate.getTime()) {
                    count++;
                    continue;
                }
            }
            //出勤率
            BigDecimal attendancePercent = carsCount != null && carsCount != 0 ? new BigDecimal((float) count / carsCount).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("attendance", attendancePercent);
            map.put("totalCount", carsCount);
            map.put("count", count);
            map.put("pricingType", pricingTypeEnums);
            map.put("date", date);
            resultList.add(map);
            count = 1L;
        }
        return Result.ok(resultList);
    }*/
    @RequestMapping("/carInfo")
    public Result carInfo(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0) {
            endTime = new Date();
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        //获取当前项目的总车辆数
        Map countMap = projectDiggingMachineServiceI.getAllCountByProjectId(projectId);
        Long carsCount = Long.parseLong(countMap.get("count").toString());
        for (int i = 0; i < dateList.size(); i++) {
            List<Map> workInfoList = new ArrayList<>();
            String key = dateList.get(i);
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT);
                workInfoList = projectWorkTimeByDiggingServiceI.getAttendanceByTime(projectId, date);
            } else if (choose == 2) {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.MONTHDAYFORMAT);
                Date start = DateUtils.getStartDate(date);
                Date end = DateUtils.getEndDate(date);
                workInfoList = projectWorkTimeByDiggingServiceI.getAttendanceByTimeMonth(projectId, start, end);
            }
            Map map = new HashMap();
            BigDecimal attendancePercent = carsCount != 0 ? new BigDecimal((float) workInfoList.size() / carsCount).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            map.put("date", date);
            map.put("attendance", workInfoList.size());
            map.put("totalCount", carsCount);
            map.put("attendancePercent", attendancePercent);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }
}
