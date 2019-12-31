package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.schedule.ScheduleConfig;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import com.seater.smartmining.service.ProjectOtherDeviceServiceI;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.entity.repository.SysUserProjectRoleRepository;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 微信小程序的项目管理
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/3 9:16
 */
@Slf4j
@RestController
@RequestMapping("/api/project")
public class ProjectWxController {

    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private SysUserProjectRoleRepository sysUserProjectRoleRepository;

    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    //任务调度类的对象
    ApplicationContext context = new AnnotationConfigApplicationContext(ScheduleConfig.class);
    ScheduleConfig config = (ScheduleConfig) context.getBean("scheduleConfig");

    //    @RequiresPermissions(PermissionConstants.PROJECT_ADD)   //  测试版先放开权限
    @RequestMapping("/saveWx")
    @Transactional
    public Object saveWx(@RequestBody(required = true) JSONObject jsonObject) throws IOException {

        try {
            Long userId = Long.parseLong(jsonObject.get("userId").toString());
            Project project = JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("project")), Project.class);
            Project project1 = projectServiceI.save(project);
            //判断是添加还是修改 如果是修改 则调用任务调度的方法
            if (project.getId() != null) {
                List<Project> projectList = new ArrayList<>();
                projectList.add(project1);

                config.startCron(project1, new Date());
                System.out.println("小程序端修改操作：任务调度重新执行,项目id:" + project1.getId());
            }

            //  项目管理员权限
            if (sysUserProjectRoleRepository.findByUserIdAndProjectIdAndValidIsTrue(userId, project1.getId()) == null) {
                //  新增项目时填充关系
                SysUserProjectRole sysUserProjectRole = new SysUserProjectRole();
                sysUserProjectRole.setProjectId(project1.getId());
                sysUserProjectRole.setUserId(userId);
                sysUserProjectRole.setValid(true);
                sysUserProjectRole.setRoleId(Constants.WX_APP_DEFAULT_ROLE_IN_PROJECT);
                try {
                    sysUserProjectRoleRepository.save(sysUserProjectRole);
                } catch (Exception e) {
                    log.warn("创建项目,新增默认项目角色信息失败,用户id:{},项目id:{}", userId, project1.getId());
                }
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
     * 假删,状态改为失效
     *
     * @param userId    用户id
     * @param projectId 项目id
     * @return
     */
    @RequestMapping("/inValidWx")
    @Transactional
    public Object inValidWx(Long userId, Long projectId) {
        try {
            //  失效关系表和失效项目
            Project project = projectServiceI.get(projectId);
            project.setStatus(ProjectStatus.Stop);
            projectServiceI.save(project);
            int i = sysUserProjectRoleRepository.inValidProjectByUserIdAndProjectId(userId, projectId);
            return new HashMap<String, Object>() {{
                put("msg", "操作成功");
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 真删,删除数据 先不用
     *
     * @param userId    用户id
     * @param projectId 项目id
     * @return
     */
//    @RequestMapping("/deleteWx")
    @Transactional
    public Object deleteWx(Long userId, Long projectId) {
        try {
//            projectServiceI.delete(projectId);
//            sysUserProjectRoleRepository.deleteByUserIdAndProjectId(userId, projectId);
            return new HashMap<String, Object>() {{
                put("msg", "操作成功");
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @PostMapping("/queryWx")
    public Object queryWx(@RequestParam(value = "time", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String name, @RequestParam(name = "userId", required = true) Long userId) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<Project> spec = new Specification<Project>() {
            List<Predicate> list = new ArrayList<>();
            List<Predicate> list2 = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<SysUserProjectRole> rootR = subquery.from(SysUserProjectRole.class);
                list2.add(cb.equal(rootR.get("userId").as(Long.class), userId));
                subquery.where(cb.and(list2.toArray(new Predicate[list2.size()])));
                subquery.select(rootR.get("projectId").as(Long.class));
                list.add(cb.in(root.get("id").as(Long.class)).value(subquery));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<Project> projectList = projectServiceI.query(spec, PageRequest.of(cur, page)).getContent();
        JSONArray content = new JSONArray();
        for (Project project : projectList) {
//            JSONObject element = new JSONObject();
            JSONObject projectJSON = JSONObject.parseObject(JSONObject.toJSONString(project));
//            element.put("project", project);
            projectJSON.put("carCount", projectCarServiceI.getCarsCountByProjectId(project.getId()).get("count"));
            projectJSON.put("oilCarCount", projectOtherDeviceServiceI.getByProjectIdAndCarTypeIs(project.getId(), CarType.OilCar).size());
            projectJSON.put("machineCount", projectDiggingMachineServiceI.getAllCountByProjectId(project.getId()).get("count"));
            content.add(projectJSON);
        }
        JSONObject result = new JSONObject();
        result.put("content", content);
        result.put("totalElements", content.size());
        return result;
    }

    /**
     * 查询项目中的设备数目(渣车,挖机,其他车)
     *
     * @param request
     * @return
     */
    @PostMapping("/deviceCount")
    public Object deviceCount(HttpServletRequest request) {
        try {
            long projectId = Long.parseLong(request.getHeader("projectId").toString());
            Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    //  项目设备数
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    //  已检查的车
                    list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                    //  有效的车
                    list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            Specification<ProjectDiggingMachine> spec1 = new Specification<ProjectDiggingMachine>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    //  项目设备数
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    //  已检查的车
                    list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                    //  有效的车
                    list.add(cb.equal(root.get("isVaild").as(Boolean.class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("projectCarCount", projectCarServiceI.query(spec, PageRequest.of(1, 10)).getTotalElements());
            jsonObject.put("projectDiggingMachineCount", projectDiggingMachineServiceI.query(spec1, PageRequest.of(1, 10)).getTotalElements());
            return new HashMap<String, Object>() {{
                put("msg", jsonObject);
                put("status", "true");
            }};
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("msg", e.getMessage());
                put("status", "true");
            }};
        }
    }
}
