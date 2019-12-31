package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.enums.ReportEnum;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.BeanUtils;
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
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectDiggingMachineWx")
public class ProjectDiggingMachineWxController {

    @Autowired
    DiggingMachineServiceI diggingMachineServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    private ProjectDiggingDayReportTotalServiceI projectDiggingDayReportTotalServiceI;
    @Autowired
    private ProjectDiggingMonthReportServiceI projectDiggingMonthReportServiceI;
    @Autowired
    private ProjectDigginggMonthReportTotalServiceI projectDigginggMonthReportTotalServiceI;
    @Autowired
    private ProjectUtils projectUtils;
    @Autowired
    private ReportPublishServiceI reportPublishServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /**
     * Long ownerId, Long projectId, Long diggingMachineId
     * 车主选挖机进入项目
     *
     * @return
     */
    @PostMapping("/saveWx")
    @Transactional
    public Object saveWx(@RequestBody JSONObject jsonObject) {
        try {
            long ownerId = Long.parseLong(jsonObject.get("ownerId").toString());
            long projectId = Long.parseLong(jsonObject.get("projectId").toString());
            List<String> diggingMachineIdsStr = (List<String>) jsonObject.get("diggingMachineIds");
            List<Long> diggingMachineIds = new ArrayList<>();

            //  数组验空 转Long类型
            for (String str : diggingMachineIdsStr) {
                if (StringUtils.isEmpty(str)) {
                    return new HashMap<String, Object>() {{
                        put("status", "false");
                        put("msg", "所选车辆id为空,保存失败");
                    }};
                }
                diggingMachineIds.add(Long.parseLong(str));
            }
            if (StringUtils.isEmpty(ownerId)) {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "车主id为空,保存失败");
                }};
            }
            if (StringUtils.isEmpty(projectId)) {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "项目id为空,保存失败");
                }};
            }

            for (Long diggingMachineId : diggingMachineIds) {
                DiggingMachine diggingMachine = diggingMachineServiceI.get(diggingMachineId);
                Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        //  项目id
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        //  车主id
                        list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
//                    //  司机id  可以没有司机
//                    list.add(cb.equal(root.get("driverId").as(Long.class), slagCar.getDriverId()));
                        list.add(cb.equal(root.get("diggingMachineId").as(Long.class), diggingMachine.getId()));
                        //不管是否已检查
//                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                Page<ProjectDiggingMachine> projectDiggingMachines = projectDiggingMachineServiceI.query(spec);
                //  只要有存在就返回
                if (projectDiggingMachines.getTotalElements() >= 1) {
                    return new HashMap<String, Object>() {{
                        put("status", "false");
                        put("msg", "该车已存在项目,挖机 " + diggingMachineServiceI.get(diggingMachineId).getMachineName() + " 添加失败");
                    }};
                }
            }

            for (Long diggingMachineId : diggingMachineIds){
                DiggingMachine diggingMachine = diggingMachineServiceI.get(diggingMachineId);
                if (StringUtils.isEmpty(diggingMachine.getDriverId())){
                    return new HashMap<String, Object>() {{
                        put("status", "false");
                        put("msg", "挖机 " + diggingMachine.getMachineName() + " 未绑定司机,无法加入项目");
                    }};
                }
            }

            for (Long diggingMachineId : diggingMachineIds) {
                DiggingMachine diggingMachine = diggingMachineServiceI.get(diggingMachineId);
                //  创建项目中的车
                ProjectDiggingMachine projectDiggingMachine = new ProjectDiggingMachine();
                BeanUtils.copyProperties(diggingMachine, projectDiggingMachine);
                projectDiggingMachine.setProjectId(projectId);
                projectDiggingMachine.setCode(projectId + UUID.randomUUID().toString().substring(0,8));
                projectDiggingMachine.setCheckStatus(CheckStatus.UnCheck);  //  未检查状态
                projectDiggingMachine.setVaild(false);  //  检查完状态才是有效
                projectDiggingMachine.setDiggingMachineId(diggingMachine.getId());
                ProjectDiggingMachine machine = projectDiggingMachineServiceI.save(projectDiggingMachine);
                //  回绑
                diggingMachine.setProjectDiggingMachineId(machine.getId());
                diggingMachineServiceI.save(diggingMachine);
            }
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }};
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }

    }


    /**
     * 拒绝该车加入项目
     * @return
     */
    @PostMapping("/refuse")
    public Object refuse(Long id, Long diggingMachineId){
        try{
            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(id);
            if (projectDiggingMachine.getDiggingMachineId().equals(diggingMachineId)){
                //执行删除操作
                projectDiggingMachineServiceI.delete(id);
                return Result.ok("操作成功");
            }
            else{
                return Result.error("车辆信息异常,操作失败");
            }
        }catch (Exception e){
            return Result.error("操作失败" + e.getMessage());
        }
    }

    /**
     * 同意该车加入项目
     * @return
     */
    @PostMapping("/agree")
    public Object agree(@RequestBody ProjectDiggingMachine projectDiggingMachine, HttpServletRequest request){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if (projectDiggingMachine != null){
                if (StringUtils.isEmpty(projectDiggingMachine.getCode())) {
                    return Result.error("挖机编号不能为空,操作失败");
                }

                ProjectDiggingMachine machine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, projectDiggingMachine.getCode());
                if (machine != null) {
                    return Result.error("项目中已存在编号" + machine.getCode() + ",请修改编号");
                }

                //执行通过检查操作
                projectDiggingMachine.setCheckStatus(CheckStatus.Checked);
                projectDiggingMachine.setVaild(true);
                projectDiggingMachineServiceI.save(projectDiggingMachine);
//                ProjectDiggingMachine save = projectDiggingMachineServiceI.save(projectDiggingMachine);
//                String interPhoneResult = "";
//                if (StringUtils.isEmpty(save.getInterPhoneAccount()) || StringUtils.isEmpty(save.getInterPhoneAccountId())){
//                    //  新增车辆时
//                    JSONObject talkBackUserAccount = projectUtils.createTalkBackUserAccount(projectId, save.getId(), UserObjectType.DiggingMachine, save.getCode());
//                    if (talkBackUserAccount != null){
//                        save.setInterPhoneAccountId(talkBackUserAccount.getString("accountId"));
//                        save.setInterPhoneAccount(talkBackUserAccount.getString("account"));
//                        projectDiggingMachineServiceI.save(save);
//                        interPhoneResult = "新增对讲账号成功";
//                    }
//                }
//                return Result.ok("操作成功 " + interPhoneResult);
                return Result.ok("操作成功");
            }
            else{
                return Result.error("车辆信息异常,操作失败");
            }
        }catch (Exception e){
            return Result.error("操作失败" + e.getMessage());
        }
    }

    @RequestMapping("delete")
    @Transactional
    public Object delete(Long id) {
        try {
            projectDiggingMachineServiceI.delete(id);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    /**
     * 项目内的车
     *
     * @param current
     * @param pageSize
     * @param ownerId
     * @param code
     * @param request
     * @param exclude
     * @param isAll
     * @return
     */
    @PostMapping("/queryWx")
    public Object query(Integer current, Integer pageSize, Long ownerId, String code, HttpServletRequest request, @RequestParam(value = "exclude", required = false) ArrayList<Long> exclude, Boolean isAll) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            if (isAll != null && isAll)
                return projectDiggingMachineServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(code))
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                    if (!StringUtils.isEmpty(ownerId))
                        list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectDiggingMachineServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }


    /**
     * 挖机日报(项目,车主,车)
     *
     * @param projectId 项目id
     * @param ownerId   车主id
     * @return
     */
    @PostMapping("/diggingMachineDayReportWx")
    public Object diggingMachineDayReportWx(Long projectId, Long ownerId, Long machineId, Date reportDate) {
        try {
            boolean reportPublish = ProjectUtils.isReportNotPublish(projectId, reportDate, ReportEnum.MachineDayReport);
            if (reportPublish) {
                return Result.error("当日报表未发布,暂不可查看");
            }

            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(machineId);
            //  每日每车
            Specification<ProjectDiggingDayReport> spec = new Specification<ProjectDiggingDayReport>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDiggingDayReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    if (!StringUtils.isEmpty(ownerId)) {
                        list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
                    }
                    if (!StringUtils.isEmpty(machineId)) {
                        list.add(cb.equal(root.get("machineId").as(Long.class), machineId));
                    }
                    if (!ObjectUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    }
                    if (!ObjectUtils.isEmpty(projectDiggingMachine) && !ObjectUtils.isEmpty(projectDiggingMachine.getProjectId())) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectDiggingMachine.getProjectId()));
                    }
                    list.add(cb.equal(root.get("reportDate").as(Date.class), reportDate));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectDiggingDayReport> projectDiggingDayReports = projectDiggingDayReportServiceI.queryWx(spec);
            List<ProjectDiggingDayReportTotal> dayReportTotalList = projectDiggingDayReportTotalServiceI.getByProjectIdAndReportDate(projectId == null ? projectDiggingMachine.getProjectId() : projectId, reportDate);
            ProjectDiggingDayReportTotal total = null;
            if(dayReportTotalList.size() > 0 ){
                total = dayReportTotalList.get(0);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("total", total);
            map.put("detail", projectDiggingDayReports);
            return Result.ok(map);

        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 挖机月报(项目,车主)
     *
     * @param projectId 项目id
     * @param ownerId   车主id
     * @return
     */
    @PostMapping("/diggingMachineMonthReportWx")
    public Object diggingMachineMonthReportWx(Long projectId, Long ownerId, Date reportDate, Integer current, Integer pageSize) {
        /*boolean reportPublish = ProjectUtils.isReportNotPublish(projectId, reportDate, ReportEnum.MachineMonthReport);
        if (reportPublish) {
            return Result.error("当月报表未发布,暂不可查看");
        }*/
        Map<String, Object> map = new HashMap<>();
        try {
            //生成查询日期
            reportDate = DateUtils.getEndDate(reportDate);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            //获取月报表合计信息
            List<ProjectDiggingMonthReportTotal> projectDiggingMonthReportTotals = projectDigginggMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            ProjectDiggingMonthReportTotal total = null;
            List<ProjectDiggingMonthReport> projectDiggingMonthReportList = null;
            if (projectDiggingMonthReportTotals.size() > 0) {
                total = projectDiggingMonthReportTotals.get(0);
                //  项目主
                if (StringUtils.isEmpty(ownerId) && !StringUtils.isEmpty(projectId)) {
                    projectDiggingMonthReportList = projectDiggingMonthReportServiceI.getByTotalId(total.getId());
                }
                //  车主
                if (!StringUtils.isEmpty(ownerId)) {
                    projectDiggingMonthReportList = projectDiggingMonthReportServiceI.getByTotalIdAndOwnerId(total.getId(), ownerId);
                }
            }
            map.put("total", total);
            map.put("detail", projectDiggingMonthReportList);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(map);
    }
}
