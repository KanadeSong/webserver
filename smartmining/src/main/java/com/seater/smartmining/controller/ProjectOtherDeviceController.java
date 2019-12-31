package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.service.ProjectCarFillMeterReadingLogServiceI;
import com.seater.smartmining.service.ProjectHourPriceServiceI;
import com.seater.smartmining.service.ProjectOilCarUserServiceI;
import com.seater.smartmining.service.ProjectOtherDeviceServiceI;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/projectOtherDevice")
public class ProjectOtherDeviceController {
    @Autowired
    private ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    private ProjectCarFillMeterReadingLogServiceI projectCarFillMeterReadingLogServiceI;

    @Autowired
    private ProjectUtils projectUtils;

    @Autowired
    private ProjectOilCarUserServiceI projectOilCarUserServiceI;

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectOtherDevice projectOtherDevice, @RequestBody JSONArray managers, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            Specification<ProjectOtherDevice> spec = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (projectOtherDevice.getId() != null && projectOtherDevice.getId() != 0L) {
                        list.add(cb.notEqual(root.get("id").as(Long.class), projectOtherDevice.getId()));
                    }
                    list.add(cb.equal(root.get("code").as(String.class), projectOtherDevice.getCode()));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectOtherDevice> deviceList = projectOtherDeviceServiceI.queryWx(spec);
            if (deviceList.size() > 0) {
                return Result.error("无法保存,项目中已存在该车号:" + projectOtherDevice.getCode());
            }

            projectOtherDevice.setProjectId(projectId);
            ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.save(projectOtherDevice);
            projectOilCarUserServiceI.deleteByOilCarId(otherDevice.getId());
                /*//  如果类型是油车 创建默认抄表记录 两个端口
                if (projectOtherDevice.getId() == 0L) { //没带id进来的话就是新增
                    for (int i = 0; i < 2; i++) {
                        ProjectCarFillMeterReadingLog projectCarFillMeterReadingLog = new ProjectCarFillMeterReadingLog();
                        projectCarFillMeterReadingLog.setOilCarCode(otherDevice.getCode());
                        projectCarFillMeterReadingLog.setOilCarId(otherDevice.getId());
                        projectCarFillMeterReadingLog.setProjectId(Long.parseLong(request.getHeader("projectId")));
                        projectCarFillMeterReadingLog.setPort(i + 1);   // 端口1 端口2
                        projectCarFillMeterReadingLogServiceI.save(projectCarFillMeterReadingLog);
                    }
                    //  添加对讲机账号
//                    JSONObject interPhoneAccount = projectUtils.createTalkBackUserAccount(Long.parseLong(request.getHeader("projectId")), otherDevice.getId(), UserObjectType.OilCar,otherDevice.getCode());
//                    otherDevice.setInterPhoneAccount(interPhoneAccount.getString("account"));
//                    otherDevice.setInterPhoneAccountId(interPhoneAccount.getString("accountId"));
//                    projectOtherDeviceServiceI.save(otherDevice);
                }*/
            //  添加对讲机账号 end


            List<Long> managerIds = new ArrayList<>();
            List<String> managerNames = new ArrayList<>();
            //关系表插入
            for (int i = 0; i < managers.size(); i++) {

                JSONObject manager = managers.getJSONObject(i);
                ProjectOilCarUser projectOilCarUser = new ProjectOilCarUser();
                projectOilCarUser.setId(null);
                projectOilCarUser.setManagerId(manager.getLong("managerId"));
                projectOilCarUser.setManagerName(manager.getString("managerName"));
                projectOilCarUser.setOilCarId(otherDevice.getId());
                projectOilCarUser.setProjectId(projectId);
                projectOilCarUser.setUpdateTime(new Date());
                projectOilCarUserServiceI.save(projectOilCarUser);
                managerIds.add(manager.getLong("managerId"));
                managerNames.add(manager.getString("managerName"));
            }

            otherDevice.setManagerIds(managerIds.toString());
            otherDevice.setManagerNames(managerNames.toString());
            projectOtherDeviceServiceI.save(otherDevice);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @PostMapping("/savePlat")
    @Transactional
    public Object savePlat(@RequestBody List<JSONObject> objectList, HttpServletRequest request) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);

        List<String> exitCodes = new ArrayList<>();
        for (JSONObject jsonObject : objectList) {
            ProjectOtherDevice projectOtherDevice = jsonObject.getJSONObject("projectOtherDevice").toJavaObject(ProjectOtherDevice.class);
            Specification<ProjectOtherDevice> spec = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (projectOtherDevice.getId() != null && projectOtherDevice.getId() != 0L) {
                        list.add(cb.notEqual(root.get("id").as(Long.class), projectOtherDevice.getId()));
                    }
                    list.add(cb.equal(root.get("code").as(String.class), projectOtherDevice.getCode()));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectOtherDevice> deviceList = projectOtherDeviceServiceI.queryWx(spec);
            if (deviceList.size() > 0) {
                exitCodes.add(projectOtherDevice.getCode());
            }
        }
        if (exitCodes.size() > 0) {
            return Result.error("无法保存,项目中已存在车号:" + exitCodes);
        }

        for (JSONObject jsonObject : objectList) {
            ProjectOtherDevice projectOtherDevice = jsonObject.getJSONObject("projectOtherDevice").toJavaObject(ProjectOtherDevice.class);

            JSONArray managers = jsonObject.getJSONArray("managers");
            ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.save(projectOtherDevice);
            projectOilCarUserServiceI.deleteByOilCarId(otherDevice.getId());

            List<Long> managerIds = new ArrayList<>();
            List<String> managerNames = new ArrayList<>();
            //关系表插入
            for (int i = 0; i < managers.size(); i++) {
                JSONObject manager = managers.getJSONObject(i);
                ProjectOilCarUser projectOilCarUser = new ProjectOilCarUser();
                projectOilCarUser.setId(null);
                projectOilCarUser.setManagerId(manager.getLong("managerId"));
                projectOilCarUser.setManagerName(manager.getString("managerName"));
                projectOilCarUser.setOilCarId(otherDevice.getId());
                projectOilCarUser.setProjectId(otherDevice.getProjectId());
                projectOilCarUser.setUpdateTime(new Date());
                projectOilCarUserServiceI.save(projectOilCarUser);
                managerIds.add(manager.getLong("managerId"));
                managerNames.add(manager.getString("managerName"));
            }

            otherDevice.setManagerIds(managerIds.toString());
            otherDevice.setManagerNames(managerNames.toString());
            projectOtherDeviceServiceI.save(otherDevice);
        }

        return "{\"status\":true}";
    }

    @RequestMapping(value = "/saveAll", produces = "application/json")
    @Transactional
    public Object save(@RequestBody List<ProjectOtherDevice> projectOtherDeviceList) {
        try {
            projectOtherDeviceServiceI.saveAll(projectOtherDeviceList);
            return "{\"status\":true}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping(value = "/deleteAll", produces = "application/json")
    @Transactional
    public Result deleteAll(@RequestBody List<Long> ids) {
        projectOtherDeviceServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {

        try {
            ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.get(id);
            if (projectOtherDevice != null) {
                projectOilCarUserServiceI.deleteByOilCarId(projectOtherDevice.getId());
                projectOtherDeviceServiceI.delete(id);
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/valid")
    @Transactional
    public Object valid(Long id) {
        try {
            ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.get(id);
            if (projectOtherDevice != null) {
                projectOilCarUserServiceI.deleteByOilCarId(projectOtherDevice.getId());
                projectOtherDevice.setVaild(!projectOtherDevice.getVaild());
                projectOtherDeviceServiceI.save(projectOtherDevice);
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }


    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String code, HttpServletRequest request, CarType carType, Boolean valid) {
        try {
            Long projectId = null;
            if(StringUtils.isNotEmpty(request.getHeader("projectId")))
                projectId = Long.parseLong(request.getHeader("projectId"));
            Long projectIds = projectId;
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<ProjectOtherDevice> spec = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                    if(valid != null)
                        list.add(cb.equal(root.get("isVaild").as(Boolean.class), valid));
                    if (!ObjectUtils.isEmpty(carType))
                        list.add(cb.equal(root.get("carType").as(CarType.class), carType));
                    if(projectIds != null)
                        list.add(cb.equal(root.get("projectId").as(Long.class),projectIds));
                    list.add(cb.notEqual(root.get("carType").as(CarType.class), CarType.OilCar));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectOtherDeviceServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/newQuery")
    public Object queryByOilCar(Integer current, Integer pageSize, String code, HttpServletRequest request, Boolean valid) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectOtherDevice> spec = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();
                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                    if(valid != null)
                        list.add(cb.equal(root.get("isVaild").as(Boolean.class), valid));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(cb.equal(root.get("carType").as(CarType.class), CarType.OilCar));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectOtherDeviceServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/queryOilCar")
    public Object newQuery(String code, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            SysUser sysUser = JSONObject.parseObject(JSONObject.toJSONString(SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO)), SysUser.class);
            Specification<ProjectOtherDevice> spec = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();
                List<Predicate> list2 = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));

                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<ProjectOilCarUser> rootR = subquery.from(ProjectOilCarUser.class);
                    list2.add(cb.equal(rootR.get("managerId").as(Long.class), sysUser.getId()));
                    list2.add(cb.equal(rootR.get("isValid").as(Boolean.class), true));
                    list2.add(cb.equal(rootR.get("projectId").as(Long.class), projectId));
                    subquery.where(cb.and(list2.toArray(new Predicate[list2.size()])));
                    subquery.select(rootR.get("oilCarId").as(Long.class));

                    list.add(cb.in(root.get("id").as(Long.class)).value(subquery));

                    list.add(cb.equal(root.get("carType").as(CarType.class), CarType.OilCar));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectOtherDeviceServiceI.queryWx(spec);
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/validCode")
    public Result validCode(HttpServletRequest request, @RequestParam String code, @RequestParam CarType carType){
        Long projectId = CommonUtil.getProjectId(request);
        ProjectOtherDevice device = projectOtherDeviceServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType);
        if(device != null)
            return Result.error("该编号已经存在，请重新输入");
        return Result.ok();
    }
}
