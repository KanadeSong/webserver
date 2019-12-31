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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Time;
import java.util.*;

/**
 * @Description 微信小程序, 车主选渣车加入项目
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/3 11:13
 */
@RestController
@RequestMapping("/api/projectCarWx")
public class ProjectCarWxController {

    @Autowired
    private ProjectCarServiceI projectCarServiceI;

    @Autowired
    private SlagCarServiceI slagCarServiceI;

    @Autowired
    private ProjectUtils projectUtils;

    @Autowired
    private ProjectDayReportServiceI projectDayReportServiceI;
    @Autowired
    private ProjectDayReportPartCarServiceI projectDayReportPartCarServiceI;
    @Autowired
    private ProjectDayReportPartDistanceServiceI projectDayReportPartDistanceServiceI;

    @Autowired
    private ProjectMonthReportServiceI projectMonthReportServiceI;
    @Autowired
    private ProjectMonthReportTotalServiceI projectMonthReportTotalServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /**
     * 车主选车进入项目
     * Long ownerId, Long projectId, ArrayList<Long> slagCarId
     *
     * @return
     */
    @PostMapping("/saveWx")
    @Transactional
    public Object saveWx(@RequestBody JSONObject jsonObject) {
        try {
            long ownerId = Long.parseLong(jsonObject.get("ownerId").toString());
            long projectId = Long.parseLong(jsonObject.get("projectId").toString());
            List<String> slagCarIdsStr = (List<String>) jsonObject.get("slagCarIds");
            List<Long> slagCarIds = new ArrayList<>();

            //  数组验空 转Long类型
            for (String str : slagCarIdsStr) {
                if (StringUtils.isEmpty(str)) {
                    return new HashMap<String, Object>() {{
                        put("status", "false");
                        put("msg", "所选车辆id为空,保存失败");
                    }};
                }
                slagCarIds.add(Long.parseLong(str));
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

            for (Long slagCarId : slagCarIds) {
                Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        //  项目id
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        //  车主id
                        list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
//                    //  司机id  可以没有司机
//                    list.add(cb.equal(root.get("driverId").as(Long.class), slagCar.getDriverId()));
                        list.add(cb.equal(root.get("slagCarId").as(Long.class), slagCarId));
                        //  不管是否检查
//                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCar> projectCars = projectCarServiceI.queryWx(spec);

                //  只要有一条存在就返回
                if (projectCars.size() >= 1) {
                    return new HashMap<String, Object>() {{
                        put("status", "false");
                        put("msg", "该车已存在项目,车辆 " + slagCarServiceI.get(slagCarId).getCarName() + " 添加失败");
                    }};
                }

            }

            //  可以没有司机
//            for (Long slagCarId : slagCarIds){
//                SlagCar slagCar = slagCarServiceI.get(slagCarId);
//                if (StringUtils.isEmpty(slagCar.getDriverId())){
//                    return new HashMap<String, Object>() {{
//                        put("status", "false");
//                        put("msg", "车辆 " + slagCar.getCarName() + " 未绑定司机,无法加入项目");
//                    }};
//                }
//            }

            Project project = projectServiceI.get(projectId);
            for (Long slagCarId : slagCarIds) {
                SlagCar slagCar = slagCarServiceI.get(slagCarId);
                //  创建项目中的渣车
                ProjectCar projectCar = new ProjectCar();
                BeanUtils.copyProperties(slagCar, projectCar);
                projectCar.setId(null);
                projectCar.setProjectId(projectId);
                //  生成随机的车编号 后台检查的时候再修改成自定义编号就行
                projectCar.setCode(projectId + UUID.randomUUID().toString().substring(0, 8));
                //  后台检查完车状态才是生效的和已检查的
                //  未生效
                projectCar.setVaild(false);
                //  未检查
                projectCar.setCheckStatus(CheckStatus.UnCheck);
                projectCar.setSlagCarId(slagCarId);
                ProjectCar car = projectCarServiceI.save(projectCar);

//                slagCar.setCheckStatus(CheckStatus.UnCheck);
//                slagCar.setValid(true);
                //  项目中车id回绑
                slagCar.setProjectCarId(car.getId());
                slagCar.setProjectId(projectId);
                if (project != null) {
                    slagCar.setProjectName(project.getName());
                }
                slagCarServiceI.save(slagCar);
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
     *
     * @return
     */
    @PostMapping("/refuse")
    public Object refuse(Long id, Long slagCarId) {
        try {
            ProjectCar projectCar = projectCarServiceI.get(id);
            if (projectCar.getSlagCarId().equals(slagCarId)) {
                //执行删除操作
                projectCarServiceI.delete(id);
                return Result.ok("操作成功");
            } else {
                return Result.error("车辆信息异常,操作失败");
            }
        } catch (Exception e) {
            return Result.error("操作失败" + e.getMessage());
        }
    }

    /**
     * 同意该车加入项目
     *
     * @return
     */
    @PostMapping("/agree")
    public Object agree(@RequestBody ProjectCar projectCar, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if (projectCar != null) {
                if (StringUtils.isEmpty(projectCar.getCode())) {
                    return Result.error("车辆编号不能为空,操作失败");
                }

                ProjectCar car = projectCarServiceI.getByProjectIdAndCode(projectId, projectCar.getCode());
                if (car != null) {
                    return Result.error("项目中已存在编号" + projectCar.getCode() + ",请修改编号");
                }

                //执行通过检查操作
                projectCar.setCheckStatus(CheckStatus.Checked);
                projectCar.setVaild(true);
                projectCarServiceI.save(projectCar);
                //回插
                SlagCar slagCar = slagCarServiceI.get(projectCar.getSlagCarId());
                slagCar.setCodeInProject(projectCar.getCode());
                slagCarServiceI.save(slagCar);
//                ProjectCar save = projectCarServiceI.save(projectCar);
//                String interPhoneResult = "";
//                if (StringUtils.isEmpty(save.getInterPhoneAccount()) || StringUtils.isEmpty(save.getInterPhoneAccountId())){
//                    //  新增车辆时
//                    JSONObject talkBackUserAccount = projectUtils.createTalkBackUserAccount(projectId, save.getId(), UserObjectType.SlagCar, save.getCode());
//                    if (talkBackUserAccount != null){
//                        save.setInterPhoneAccountId(talkBackUserAccount.getString("accountId"));
//                        save.setInterPhoneAccount(talkBackUserAccount.getString("account"));
//                        projectCarServiceI.save(save);
//                        interPhoneResult = "新增对讲账号成功";
//                    }
//                }
//                return Result.ok("操作成功 " + interPhoneResult);
                return Result.ok("操作成功");
            } else {
                return Result.error("车辆信息异常,操作失败");
            }
        } catch (Exception e) {
            return Result.error("操作失败" + e.getMessage());
        }
    }

    /**
     * 生/失效 ProjectCar
     *
     * @param id
     * @return
     */
    @PostMapping("/invalidWx")
    @Transactional
    public Object invalidWx(Long id) {
        try {
            ProjectCar projectCar = projectCarServiceI.get(id);
            if (!StringUtils.isEmpty(projectCar)) {
                projectCar.setVaild(!projectCar.getVaild());
                projectCarServiceI.save(projectCar);
            } else {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "渣车不存在,操作失败");
                }};
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
     * 渣车日报(项目,车主,车)
     *
     * @param projectId
     * @param ownerId
     * @param carId
     * @param reportDate
     * @return
     */
    @PostMapping("/carDayReportWx")
    public Object carDayReportWx(Long projectId, Long ownerId, Long carId, Date reportDate) {
        try {
            boolean reportPublish = ProjectUtils.isReportNotPublish(projectId, reportDate, ReportEnum.CarDayReport);
            if (reportPublish) {
                return Result.error("当日报表未发布,暂不可查看");
            }
            ProjectCar projectCar = projectCarServiceI.get(carId);
            //  项目日报
            ProjectDayReport body = projectDayReportServiceI.getByProjectIdAndReportDate(projectId == null ? projectCar.getProjectId() : projectId, reportDate);
            //  每日每车
            Specification<ProjectDayReportPartCar> spec = new Specification<ProjectDayReportPartCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDayReportPartCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    if (!StringUtils.isEmpty(ownerId)) {
                        list.add(cb.equal(root.get("carOwnerId").as(Long.class), ownerId));
                    }
                    if (!StringUtils.isEmpty(carId)) {
                        list.add(cb.equal(root.get("carId").as(Long.class), carId));
                    }
                    if (!ObjectUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    }
                    if (!ObjectUtils.isEmpty(projectCar) && !ObjectUtils.isEmpty(projectCar.getProjectId())) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectCar.getProjectId()));
                    }
                    list.add(cb.equal(root.get("reportDate").as(Date.class), reportDate));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectDayReportPartCar> cars = projectDayReportPartCarServiceI.queryWx(spec);

            //  每日运距
            List<ProjectDayReportPartDistance> distances = null;
            if (null != body) {
                distances = projectDayReportPartDistanceServiceI.getByReportIdOrderByDistance(body.getId());
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("body", body);
            hashMap.put("cars", cars);
            hashMap.put("distances", distances);
            return Result.ok(hashMap);
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }


    /**
     * 渣车月报(项目主,车主)
     *
     * @param ownerId
     * @param projectId
     * @param reportDate
     * @return
     */
    @RequestMapping("/carMonthReportWx")
    public Result carMonthReportWx(Long ownerId, Long projectId, Date reportDate) {
        /*boolean reportPublish = ProjectUtils.isReportNotPublish(projectId, reportDate, ReportEnum.CarMonthReport);
        if (reportPublish) {
            return Result.error("当月报表未发布,暂不可查看");
        }*/
        //  车主在项目中的车
        Specification<ProjectCar> spec = new Specification<ProjectCar>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (!StringUtils.isEmpty(ownerId)) {
                    list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
                }

                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));

                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        List<ProjectCar> projectCars = projectCarServiceI.queryWx(spec);
        List<Long> carIds = new ArrayList<>();      //  车主的车id列表
        ProjectMonthReportTotal total = null;
        List<ProjectMonthReport> monthReportList = null;
        if (projectCars.size() >= 1) {
            for (ProjectCar projectCar : projectCars) {
                carIds.add(projectCar.getId());
            }

            reportDate = DateUtils.getEndDate(reportDate);
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            List<ProjectMonthReportTotal> totalList = projectMonthReportTotalServiceI.getByProjectIdAndReportDate(projectId, reportDate);
            if (totalList.size() > 0) {
                total = totalList.get(0);
                if (!StringUtils.isEmpty(ownerId)) {
                    //  车主
                    monthReportList = projectMonthReportServiceI.getByTotalIdAndCarIdIn(total.getId(), carIds);
                } else {
                    //  项目主
                    monthReportList = projectMonthReportServiceI.getByTotalId(total.getId());
                }

            }
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("detail", monthReportList);
        hashMap.put("total", total);
        return Result.ok(hashMap);
    }


    /**
     * 项目内的渣车(项目主,车主)
     *
     * @param current
     * @param pageSize
     * @param code         车辆编号
     * @param ownerId      车主id
     * @param request
     * @param exclude
     * @param icCardNumber
     * @return
     */
    @PostMapping("/queryWx")
    public Object query(Integer current, Integer pageSize, String code, String checkStatus, String ownerId, HttpServletRequest request, @RequestParam(value = "exclude", required = false) ArrayList<Long> exclude, String icCardNumber) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(icCardNumber))
                        list.add(cb.equal(root.get("icCardNumber").as(String.class), icCardNumber));
                    if (!StringUtils.isEmpty(ownerId)) {
                        list.add(cb.equal(root.get("ownerId").as(Long.class), ownerId));
                    }

                    if (!StringUtils.isEmpty(code))
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));

                    if (CheckStatus.UnCheck.getValue().equals(checkStatus)) {
                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                    } else {
                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
//                    list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectCarServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

}
