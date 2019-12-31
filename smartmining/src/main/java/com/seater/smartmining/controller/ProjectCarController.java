package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONArray;
import com.seater.helpers.DateEditor;
import com.seater.helpers.JsonHelper;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectCar")
public class ProjectCarController {
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectCarLoadMaterialSetServiceI projectCarLoadMaterialSetServiceI;
    @Autowired
    private ProjectScheduledServiceI projectScheduledServiceI;
    @Autowired
    private ProjectUtils projectUtils;
    @Autowired
    private ProjectCarCostAccountingServiceI projectCarCostAccountingServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    private ProjectDayReportServiceI projectDayReportServiceI;
    @Autowired
    private ProjectDiggingCostAccountingServiceI projectDiggingCostAccountingServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_CAR_SAVE)
    public Object save(ProjectCar projectCar, String materialJson, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            projectCar.setProjectId(projectId);
//            ProjectCar projectCarSaved = projectCarServiceI.save(projectCar);
//            if (projectCar.getId() == 0L){
//                //  添加对讲机账号
//                if (StringUtils.isEmpty(projectCar.getInterPhoneAccount()) || StringUtils.isEmpty(projectCar.getInterPhoneAccountId())){
//                    JSONObject interPhoneAccount = projectUtils.createTalkBackUserAccount(Long.parseLong(request.getHeader("projectId")), projectCarSaved.getId(), UserObjectType.SlagCar,projectCar.getCode());
//                    projectCarSaved.setInterPhoneAccount(interPhoneAccount.getString("account"));
//                    projectCarSaved.setInterPhoneAccountId(interPhoneAccount.getString("accountId"));
//                    projectCarServiceI.save(projectCarSaved);
//                }
//                //  添加对讲机账号 end
//            }
            Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (projectCar.getId() != null && projectCar.getId() != 0L) {
                        list.add(cb.notEqual(root.get("id").as(Long.class), projectCar.getId()));
                    }
                    list.add(cb.equal(root.get("code").as(String.class), projectCar.getCode()));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCar> deviceList = projectCarServiceI.queryWx(spec);
            if (deviceList.size() > 0) {
                return Result.error("无法保存,项目中已存在该车号:" + projectCar.getCode());
            }
            Long newid = projectCarServiceI.save(projectCar).getId();
            ProjectCarLoadMaterialSet projectCarLoadMaterialSetArray[] = JsonHelper.jsonStringToObject(materialJson, ProjectCarLoadMaterialSet[].class);

            for (ProjectCarLoadMaterialSet projectCarLoadMaterialSet : projectCarLoadMaterialSetArray) {
                projectCarLoadMaterialSet.setProjectId(Long.parseLong(request.getHeader("projectId")));
                projectCarLoadMaterialSet.setCarID(newid);
                projectCarLoadMaterialSetServiceI.save(projectCarLoadMaterialSet);
            }

            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @RequestMapping(value = "/batchSave", produces = "application/json")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_CAR_SAVE)
    public Result batchSave(@RequestBody List<ProjectCar> carList){
        projectCarServiceI.batchSave(carList);
        return Result.ok();
    }

    @RequestMapping("/delete")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_CAR_DELETE)
    public Object delete(Long id) {
        try {
            projectCarServiceI.delete(id);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/deleteAll")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_CAR_DELETE)
    public Object delete(@RequestBody List<Long> ids){
        projectCarServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/query")
//    @RequiresPermissions(PermissionConstants.PROJECT_CAR_QUERY)
    public Object query(Integer current, Integer pageSize, String code, String checkStatus, HttpServletRequest request, @RequestParam(value = "exclude", required = false) ArrayList<Long> exclude, String icCardNumber, Boolean isAll, Boolean isVaild) {
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
                    List<ProjectCar> resultList = new ArrayList<>();
                    List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));
                    for (ProjectCar projectCar : projectCarList) {
                        if (projectCar.getCheckStatus() == CheckStatus.Checked) {
                            resultList.add(projectCar);
                        }
                    }
                    return resultList;
                }

                Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        if (icCardNumber != null && !icCardNumber.isEmpty())
                            list.add(cb.equal(root.get("icCardNumber").as(String.class), icCardNumber));

                        if (code != null && !code.isEmpty())
                            list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));

                        if (exclude != null && exclude.size() > 0)
                            list.add(root.get("id").as(Long.class).in(exclude).not());

                        if (CheckStatus.UnCheck.getValue().equals(checkStatus)) {
                            list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                        } else {
                            list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                        }
                        if (isAll == null) {
                            list.add(cb.isTrue(root.get("isVaild")));
                        }
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));

                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                return projectCarServiceI.query(spec, PageRequest.of(cur, page));
            }else{
                Specification<ScheduleCar> spec = new Specification<ScheduleCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();
                    @Override
                    public Predicate toPredicate(Root<ScheduleCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        if (code != null && !code.isEmpty())
                            list.add(criteriaBuilder.like(root.get("carCode").as(String.class), "%" + code + "%"));
                        list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                        query.orderBy(criteriaBuilder.asc(root.get("id").as(Long.class)));
                        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByQuery(spec);
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
                List<ScheduleCar> scheduleCars = new ArrayList<>();
                for (ScheduleCar car : scheduleCarList) {
                    if (groupCodeList.contains(car.getGroupCode()))
                        scheduleCars.add(car);
                }
                List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdOrderById(projectId);
                //创建渣车索引
                Map<String, Integer> carIndex = new HashMap<>();
                for (int i = 0; i < scheduleCars.size(); i++) {
                    carIndex.put(scheduleCars.get(i).getCarCode(), i);
                }
                List<ProjectCar> responseList = new ArrayList<>();
                for (ProjectCar car : projectCarList) {
                    Integer index = carIndex.get(car.getCode());
                    if (index != null)
                        responseList.add(car);
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
    public Result queryByPlat(Integer current, Integer pageSize, Long projectId, String code, String name, String ownerName, Long brandId, Long modelId, String driverName, Date startTime, Date endTime){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<ProjectCar> spec = new Specification<ProjectCar>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(projectId != null && projectId != 0)
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                if(StringUtils.isNotEmpty(code))
                    list.add(criteriaBuilder.like(root.get("code").as(String.class), "%" + code + "%"));
                if(StringUtils.isNotEmpty(ownerName))
                    list.add(criteriaBuilder.like(root.get("ownerName").as(String.class), "%" + ownerName + "%"));
                if(brandId != null && brandId != 0)
                    list.add(criteriaBuilder.equal(root.get("brandId").as(Long.class), brandId));
                if(modelId != null && modelId != 0)
                    list.add(criteriaBuilder.equal(root.get("modelId").as(Long.class), modelId));
                if(StringUtils.isNotEmpty(driverName))
                    list.add(criteriaBuilder.like(root.get("driverName").as(String.class), "%" + driverName + "%"));
                if(startTime != null && endTime != null)
                    list.add(criteriaBuilder.between(root.get("addTime").as(Date.class), startTime, endTime));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectCarServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/queryBySelected")
//    @RequiresPermissions(PermissionConstants.PROJECT_CAR_QUERY)
    public Result query(HttpServletRequest request, Boolean selected) {
        /*try {*/
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if(selected == null)
                selected = false;
            //查询未绑定排班的车辆
            List<ProjectCar> projectCarList = projectCarServiceI.getAllByProjectIdAndSeleted(projectId, selected);
            //判断是查询全部还是筛选
            /*boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("该用户没有任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            if (flag) {
                return Result.ok(projectCarList);
            } else {
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectId(projectId);
                List<ScheduleCar> scheduleCars = new ArrayList<>();
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                String params = "\"" + sysUser.getId() + "\"";
                List<ProjectSchedule> projectScheduleList = projectScheduleServiceI.getAllByProjectIdAndManagerIdOrderById(projectId, params);
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : projectScheduleList) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                for (ScheduleCar car : scheduleCarList) {
                    if (groupCodeList.contains(car.getGroupCode()))
                        scheduleCars.add(car);
                }
                //创建有权限的挖机索引
                Map<String, Integer> machineIndex = new HashMap<>();
                for (int i = 0; i < scheduleCars.size(); i++) {
                    machineIndex.put(scheduleCars.get(i).getCarCode(), i);
                }
                List<ProjectCar> responseList = new ArrayList<>();
                for (ProjectCar car : projectCarList) {
                    Integer index = machineIndex.get(car.getCode());
                    if (index != null)
                        responseList.add(car);
                }*/
                return Result.ok(projectCarList);
            /*}*/
        /*} catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMsg());
        }*/
    }

    @RequestMapping("/queryByValid")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, Boolean valid, String code){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<ProjectCar> spec = new Specification<ProjectCar>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (StringUtils.isNotEmpty(code))
                    list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                if(valid != null)
                    list.add(cb.equal(root.get("isVaild").as(Boolean.class), valid));
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));

                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectCarServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/setICCard")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_CAR_SAVE)
    public Object setIcCard(Long carId, String icCardNumber, Boolean icCardStatus) {
        try {
            projectCarServiceI.setICCardByCarId(carId, icCardNumber, icCardStatus);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping(value = "/valid", produces = "application/json")
    public Result valid(HttpServletRequest request, @RequestBody List<Long> ids) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            List<ProjectCar> carList = new ArrayList<>();
            for (Long id : ids) {
                ProjectCar car = projectCarServiceI.get(id);
                if (car != null) {
                    car.setVaild(false);
                    car.setSeleted(false);
                    carList.add(car);
                    ScheduleCar scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarCode(projectId, car.getCode());
                    if(scheduleCar != null) {
                        scheduleCar.setIsVaild(false);
                        scheduleCarServiceI.save(scheduleCar);
                    }
                }
            }
            projectCarServiceI.batchSave(carList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    /**
     * 排渣金额 用油金额 油耗比 加油量 总车数 总方量
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/amount")
    public Result carAmount(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            }
            else if(choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            }
            else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<Map> resultList = new ArrayList<>();
        List<Map> carAmountList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                carAmountList = projectCarCostAccountingServiceI.getCarAmountReport(projectId, startTime, endTime);
                break;
            case 2:
                carAmountList = projectCarCostAccountingServiceI.getCarAmountReportMonth(projectId, startTime, endTime);
                break;
            case 3:
                startTime = new Date(0);
                carAmountList = projectCarCostAccountingServiceI.getCarAmountReportMonth(projectId, startTime, endTime);
                break;
        }
        Map<String, Integer> carAmountMap = new HashMap<>();
        for(int i = 0; i < carAmountList.size(); i++){
            Date date = null;
            String key = null;
            if(choose == 1) {
                date = DateUtils.stringFormatDate(carAmountList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(carAmountList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            carAmountMap.put(key, i);
        }
        for(int i = 0; i < dateList.size(); i++){
            String key = dateList.get(i);
            Integer carIndex = carAmountMap.get(key);
            //总车数
            Long totalCount = carIndex != null ? Long.parseLong(carAmountList.get(carIndex).get("totalCount").toString()) : 0L;
            //总油量
            BigDecimal totalFill = carIndex != null ? new BigDecimal((float)Long.parseLong(carAmountList.get(carIndex).get("fillCount").toString()) / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //总方量
            BigDecimal totalCubic = carIndex != null ? new BigDecimal((float)Long.parseLong(carAmountList.get(carIndex).get("totalCubic").toString()) / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //金额
            BigDecimal amount = carIndex != null ? new BigDecimal(((float)Long.parseLong(carAmountList.get(carIndex).get("amount").toString())) / 100L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //用油金额
            BigDecimal amountByFill = carIndex != null ? new BigDecimal(((float)Long.parseLong(carAmountList.get(carIndex).get("amountByFill").toString())) / 100).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //todo 油耗比 油耗不在这里进行展示
            BigDecimal oilConsumption = amount != null && amount.compareTo(BigDecimal.ZERO) != 0 ? amountByFill.divide(amount, 4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.MONTHDAYFORMAT);
            }
            Map map = new HashMap();
            map.put("totalAmount", amount);
            map.put("amountByFill", amountByFill);
            map.put("totalCount", totalCount);
            map.put("totalFill", totalFill);
            map.put("totalCubic", totalCubic);
            map.put("oilConsumption", oilConsumption);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 出勤数 车数 出勤率
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/attendance")
    public Result carAttendance(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();

        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        //总注册数
        Integer count = projectCarServiceI.getCountByProjectId(projectId);
        for(int i = 0; i < dateList.size(); i++){
            List<Map> workInfoList = new ArrayList<>();
            String key = dateList.get(i);
            Date date = null;
            if(choose == 1) {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT);
                workInfoList = projectCarWorkInfoServiceI.getCarAttendanceReport(projectId, date);
            }else if(choose == 2) {
                date = DateUtils.stringFormatDate(key, SmartminingConstant.MONTHDAYFORMAT);
                Date start = DateUtils.getStartDate(date);
                Date end = DateUtils.getEndDate(date);
                workInfoList = projectCarWorkInfoServiceI.getCarAttendanceReportMonth(projectId, start, end);
            }
            Map map = new HashMap();
            BigDecimal attendancePercent = count != 0 ? new BigDecimal((float) workInfoList.size() / count).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            map.put("date", date);
            map.put("attendance", workInfoList.size());
            map.put("totalCount", count);
            map.put("attendancePercent", attendancePercent);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 异常数量
     * 合格率 不合格率
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/errorCars")
    public Result errorCount(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> resultList = new ArrayList<>();
        List<Map> errorList = new ArrayList<>();
        List<Map> countList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                errorList = projectCarWorkInfoServiceI.getQualificationCarReport(projectId, startTime, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDate(projectId, startTime, endTime);
                break;
            case 2:
                errorList = projectCarWorkInfoServiceI.getQualificationCarReportMonth(projectId, startTime, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDateMonth(projectId, startTime, endTime);
                break;
            case 3:
                startTime = new Date(0);
                errorList = projectCarWorkInfoServiceI.getQualificationCarReportMonth(projectId, startTime, endTime);
                countList = projectCarWorkInfoServiceI.getCarsCountByDateMonth(projectId, startTime, endTime);
                break;
        }
        //异常车数索引
        Map<String, Integer> errorMapIndex = new HashMap<>();
        for(int i = 0; i < errorList.size(); i++){
            Date date = null;
            String key = null;
            if(choose == 1) {
                date = DateUtils.stringFormatDate(errorList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(errorList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            errorMapIndex.put(key, i);
        }
        //总车数索引
        Map<String, Integer> countMapIndex = new HashMap<>();
        for(int i = 0; i < countList.size(); i++){
            Date date = null;
            String key = null;
            if(choose == 1) {
                date = DateUtils.stringFormatDate(countList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(countList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            countMapIndex.put(key, i);
        }
        for(int i = 0; i < dateList.size(); i++){
            String key = dateList.get(i);
            Integer errorIndex = errorMapIndex.get(key);
            Integer countIndex = countMapIndex.get(key);
            //异常车数
            Long errorCount = errorIndex != null ? Long.parseLong(errorList.get(errorIndex).get("count").toString()) : 0L;
            //总车数
            Long totalCount = countIndex != null ? Long.parseLong(countList.get(countIndex).get("count").toString()) : 0L;
            //及格车数
            Long passCount = totalCount - errorCount;
            //及格率
            BigDecimal passPercent = totalCount != 0L ? new BigDecimal((float)passCount / totalCount).setScale(4, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //不及格率
            BigDecimal errorPercent = totalCount != 0L ? new BigDecimal((float)errorCount / totalCount).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("errorCount", errorCount);
            map.put("totalCount", totalCount);
            map.put("errorPercent", errorPercent);
            map.put("passPercent", passPercent);
            map.put("date", dateList.get(i));
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 排渣方量 里程 车数 用油量
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/cubicInfo")
    public Result cubicInfo(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> carCubicInfoList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                carCubicInfoList = projectCarWorkInfoServiceI.getCarCubicInfo(projectId, startTime, endTime);
                break;
            case 2:
                carCubicInfoList = projectCarWorkInfoServiceI.getCarCubicInfoMonth(projectId, startTime, endTime);
                break;
            case 3:
                carCubicInfoList = projectCarWorkInfoServiceI.getCarCubicInfoMonth(projectId, startTime, endTime);
                break;
        }
        //生成索引
        Map<String, Integer> carCubicMapIndex = new HashMap<>();
        for(int i = 0; i < carCubicInfoList.size(); i++){
            Date date = null;
            String key = "";
            if (choose == 1) {
                date = DateUtils.stringFormatDate(carCubicInfoList.get(i).get("date_identification").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(carCubicInfoList.get(i).get("date_identification").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            carCubicMapIndex.put(key, i);
        }
        for(int i = 0; i < dateList.size(); i++){
            String key = dateList.get(i);
            Integer carCubicIndex = carCubicMapIndex.get(key);
            //总车数
            Long count = carCubicIndex != null ? Long.parseLong(carCubicInfoList.get(carCubicIndex).get("count").toString()) : 0L;
            //总方量
            BigDecimal cubic = carCubicIndex != null ? new BigDecimal(((float)Long.parseLong(carCubicInfoList.get(i).get("cubic").toString())) / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            //总运距
            BigDecimal mileage = carCubicIndex != null ? new BigDecimal(((float)Long.parseLong(carCubicInfoList.get(i).get("mileage").toString())) / 100000L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            List<Map> fillList = null;
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
                fillList = projectCarFillLogServiceI.getFillLogOnCar(projectId, date, CarType.SlagCar.getValue());
            }else{
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
                Date startDate = DateUtils.getStartDate(date);
                Date endDate = DateUtils.getEndDate(date);
                fillList = projectCarFillLogServiceI.getFillLogOnCarMonth(projectId, startDate, endDate, CarType.SlagCar.getValue());
            }
            BigDecimal fillCount = new BigDecimal(0);
            if (fillList != null && fillList.size() > 0)
                fillCount = new BigDecimal(((float)Long.parseLong(fillList.get(0).get("volumn").toString())) / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Map map = new HashMap();
            map.put("count", count);
            map.put("cubic", cubic);
            map.put("mileage", mileage);
            map.put("fill", fillCount);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 渣车单位成本 毛利 每车用油 平均里程 油耗
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/avgCarInfo")
    public Result avgCarInfo(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> carInfoList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                carInfoList = projectDayReportServiceI.getAvgCarInfo(projectId, startTime, endTime);
                break;
            case 2:
                carInfoList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
            case 3:
                carInfoList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
        }
        //生成索引你
        Map<String, Integer> carInfoMapIndex= new HashMap<>();
        for(int i = 0; i < carInfoList.size(); i++){
            Date date = null;
            String key = "";
            if (choose == 1) {
                date = DateUtils.stringFormatDate(carInfoList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(carInfoList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
                key = DateUtils.formatDateByPattern(date, SmartminingConstant.MONTHDAYFORMAT);
            }
            carInfoMapIndex.put(key, i);
        }
        for(int i = 0; i < dateList.size(); i++){
            String key = dateList.get(i);
            Date date = null;
            if(choose == 1)
                date = DateUtils.stringFormatDate(key, SmartminingConstant.YEARMONTHDAUFORMAT);
            else
                date = DateUtils.stringFormatDate(key, SmartminingConstant.MONTHDAYFORMAT);
            Integer carInfoIndex = carInfoMapIndex.get(key);
            Long totalCubic = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("totalCubic").toString()) : 0L;
            Long totalCount = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("totalCount").toString()) : 0L;
            Long totalFill = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("totalFill").toString()) : 0L;
            Long mileage = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("mileage").toString()) : 0L;
            Long totalAmount = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("totalAmount").toString()) : 0L;
            //用油金额
            Long totalAmounByFill = carInfoIndex != null ? Long.parseLong(carInfoList.get(carInfoIndex).get("totalAmountFill").toString()) : 0L;
            //单位成本 分/立方
            Long costByMin = totalCubic / 1000000L != 0L ? totalAmount / (totalCubic / 1000000L) : 0L;
            BigDecimal cost = new BigDecimal((float)costByMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //毛利润   分/车
            Long grossProfitByMin = totalCount != 0L ? (totalAmount - totalAmounByFill) / totalCount : 0L;
            BigDecimal grossProfit = new BigDecimal((float)grossProfitByMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //每车用油 毫升/车
            Long useFillByMl = totalCount != 0L ? totalFill / totalCount : 0L;
            BigDecimal useFill = new BigDecimal((float)useFillByMl / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP);
            //平均里程 厘米
            Long avgDistanceCm = totalCount != 0L ? mileage / totalCount : 0L;
            BigDecimal avgDistance = new BigDecimal((float) avgDistanceCm / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal oilConsumption = totalAmount != 0 ? new BigDecimal((float)totalAmounByFill / totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("cost", cost);
            map.put("grossProfit", grossProfit);
            map.put("useFill", useFill);
            map.put("avgDistance", avgDistance);
            map.put("oilConsumption", oilConsumption);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 排渣效率
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/efficiency")
    public Result carEfficiency(HttpServletRequest request, Date startTime, Date endTime, @RequestParam Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> carEfficiencyList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                carEfficiencyList = projectDayReportServiceI.getAvgCarInfo(projectId, startTime, endTime);
                break;
            case 2:
                carEfficiencyList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
            case 3:
                carEfficiencyList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
        }
        //生成索引
        Map<Long, Integer> carEffiMapIndex = new HashMap<>();
        for(int i = 0; i < carEfficiencyList.size(); i++){
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(carEfficiencyList.get(i).get("report_date").toString(), SmartminingConstant.YEARMONTHDAUFORMAT);
            } else {
                date = DateUtils.stringFormatDate(carEfficiencyList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            carEffiMapIndex.put(date.getTime(), i);
        }
        for(int i = 0; i < dateList.size(); i++){
            Date date = null;
            if(choose == 1)
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
            else
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
            Long key = date.getTime();
            Integer carEffiIndex = carEffiMapIndex.get(key);
            Long totalCount = carEffiIndex != null ? Long.parseLong(carEfficiencyList.get(carEffiIndex).get("totalCount").toString()) : 0L;
            Long onDutyCount = carEffiIndex != null ? Long.parseLong(carEfficiencyList.get(carEffiIndex).get("onDutyCount").toString()) : 0L;
            //运距
            Long distance = carEffiIndex != null ? Long.parseLong(carEfficiencyList.get(carEffiIndex).get("mileage").toString()) : 0L;
            //平均里程
            BigDecimal avgDistance = totalCount != null && totalCount != 0 ? new BigDecimal((float) distance / totalCount).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            avgDistance = avgDistance.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            //每天 趟/车
            BigDecimal avgCountsPerCarPerDay = onDutyCount != 0L ? new BigDecimal((float) totalCount / onDutyCount).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            Map map = new HashMap();
            map.put("avgCountsPerCarPerDay", avgCountsPerCarPerDay);
            map.put("avgDistance", avgDistance);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 总金额
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param choose
     * @return
     */
    @RequestMapping("/totalAmount")
    public Result getTotalAmount(HttpServletRequest request, Date startTime, Date endTime, Integer choose) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> amountList = new ArrayList<>();
        List<Map> resultList = new ArrayList<>();
        List<String> dateList = DateUtils.getWeekAgoList(choose, endTime);
        switch (choose) {
            case 1:
                amountList = projectDayReportServiceI.getAvgCarInfo(projectId, startTime, endTime);
                break;
            case 2:
                amountList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
            case 3:
                amountList = projectDayReportServiceI.getAvgCarInfoMonth(projectId, startTime, endTime);
                break;
        }
        //生成索引
        Map<Long, Integer> amountMapIndex = new HashMap<>();
        for(int i = 0; i < amountList.size(); i++){
            Date date = null;
            if (choose == 1) {
                date = DateUtils.stringFormatDate(amountList.get(i).get("report_date").toString(), SmartminingConstant.DATEFORMAT);
            } else {
                date = DateUtils.stringFormatDate(amountList.get(i).get("report_date").toString(), SmartminingConstant.MONTHDAYFORMAT);
            }
            amountMapIndex.put(date.getTime(), i);
        }
        for(int i = 0; i < dateList.size(); i++){
            Date date = null;
            if(choose == 1)
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.YEARMONTHDAUFORMAT);
            else
                date = DateUtils.stringFormatDate(dateList.get(i), SmartminingConstant.MONTHDAYFORMAT);
            Long key = date.getTime();
            Integer amountIndex = amountMapIndex.get(key);
            Long totalAmountMin = amountIndex != null ? Long.parseLong(amountList.get(amountIndex).get("totalAmount").toString()) : 0L;
            BigDecimal totalAmount = new BigDecimal((float)totalAmountMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
            Map map = new HashMap();
            map.put("totalAmount", totalAmount);
            map.put("date", date);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }

    /**
     * 渣车费用 挖机费用 总占比
     *
     * @param request
     * @return
     */
    @RequestMapping("/historyAmount")
    public Result historyAmount(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<Map> resultList = new ArrayList<>();
        if (endTime == null || endTime.getTime() == 0){
            endTime = DateUtils.subtractionOneDay(new Date());
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.subtractionOneDay(endTime);
            } else if(choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        List<Map> carAmountList = new ArrayList<>();
        List<Map> diggingAmountList = new ArrayList<>();
        switch (choose){
            case 1:
                carAmountList = projectCarCostAccountingServiceI.getHistoryAmount(projectId, startTime, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryAmount(projectId, startTime, endTime);
                break;
            case 2:
                carAmountList = projectCarCostAccountingServiceI.getHistoryAmount(projectId, startTime, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryAmount(projectId, startTime, endTime);
                break;
            case 3:
                carAmountList = projectCarCostAccountingServiceI.getHistoryAmountHistory(projectId, endTime);
                diggingAmountList = projectDiggingCostAccountingServiceI.getHistoryAmountHistory(projectId, endTime);
                break;
        }
        //渣车费用
        Long amountByCarByMin = 0L;
        //渣车加油费用
        Long fillAmountByCarByMin = 0L;
        if(carAmountList != null && carAmountList.size() > 0) {
            amountByCarByMin = carAmountList.get(0).get("amount") != null ? Long.parseLong(carAmountList.get(0).get("amount").toString()) : 0L;
            fillAmountByCarByMin = carAmountList.get(0).get("amount_by_fill") != null ? Long.parseLong(carAmountList.get(0).get("amount_by_fill").toString()) : 0L;
        }
        BigDecimal amountByCar = new BigDecimal((float)amountByCarByMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
        //挖机费用
        Long amountByDiggingByMin = 0L;
        //挖机计时加油费用
        Long fillAmountByDiggingByTimerMin = 0L;
        //挖机计方加油费用
        Long fillAmountByDiggingByCubicMin = 0L;
        if(diggingAmountList != null && diggingAmountList.size() > 0) {
            amountByDiggingByMin = diggingAmountList.get(0).get("total_amount") != null ? Long.parseLong(diggingAmountList.get(0).get("total_amount").toString()) : 0L;
            fillAmountByDiggingByTimerMin = diggingAmountList.get(0).get("amount_by_fill_by_timer") != null ? Long.parseLong(diggingAmountList.get(0).get("amount_by_fill_by_timer").toString()) : 0L;
            fillAmountByDiggingByCubicMin = diggingAmountList.get(0).get("amount_by_fill_by_cubic") != null ? Long.parseLong(diggingAmountList.get(0).get("amount_by_fill_by_cubic").toString()) : 0L;
        }
        //挖机加油费用
        Long fillAmountByDiggingByMin = fillAmountByDiggingByTimerMin + fillAmountByDiggingByCubicMin;
        BigDecimal amountByDigging = new BigDecimal((float)amountByDiggingByMin / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal fillAmount = new BigDecimal((float)(fillAmountByCarByMin + fillAmountByDiggingByMin) / 100L).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalAmount = amountByCar.add(amountByDigging).add(fillAmount);
        Map map = new HashMap();
        map.put("amountByCar", amountByCar);
        map.put("amountByDigging", amountByDigging);
        map.put("fillAmount", fillAmount);
        map.put("totalAmount", totalAmount);
        resultList.add(map);
        return Result.ok(resultList);
    }

    /**
     * 车主添加车辆进去
     * @param projectCar
     * @return
     */
    public Result save(ProjectCar projectCar){
        projectCar.setProjectId(null);
        projectCar.setVaild(false);
        return Result.ok();
    }

    @RequestMapping("/unValidCount")
    public Result unValidCountReport(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime){
        Long projectId = CommonUtil.getProjectId(request);
        if (endTime == null || endTime.getTime() == 0){
            endTime = new Date();
            endTime = DateUtils.getAddDate(endTime, -1);
        }
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.getWeekAgo(endTime);
                startTime = DateUtils.getEndDateByNow(startTime);
                //startTime = DateUtils.subtractionOneDay(startTime);
            } else if(choose == 2) {
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
        List<Map> resultList = new ArrayList<>();
        switch (choose) {
            case 1:
                resultList = projectMqttCardReportServiceI.getUnValidCountByProjectIdAndDateIdentification(projectId, startTime, endTime);
                break;
            case 2:
                resultList = projectMqttCardReportServiceI.getUnValidCountMonthByProjectIdAndDateIdentification(projectId, startTime, endTime);
                break;
            case 3:
                resultList = projectMqttCardReportServiceI.getUnValidCountMonthByProjectIdAndDateIdentification(projectId, startTime, endTime);
                break;
        }
        return Result.ok(resultList);
    }
}
