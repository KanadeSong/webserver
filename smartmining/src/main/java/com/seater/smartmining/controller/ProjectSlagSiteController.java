package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.entity.ProjectDeviceStatus;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.enums.ProjectDeviceType;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.manager.ProjectSlagSiteManager;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.SlagSitePositionJob;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectDeviceServiceI;
import com.seater.smartmining.service.ProjectSlagSiteServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projectSlagSite")
public class ProjectSlagSiteController {
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ProjectSlagSiteManager projectSlagSiteManager;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    Long count = 0L;

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, String name) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("该用户没有任何权限");
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            String params = "\"" + sysUser.getId() + "\"";
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectSlagSite> spec = new Specification<ProjectSlagSite>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectSlagSite> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    if (name != null && !name.isEmpty())
                        list.add(cb.like(root.get("code").as(String.class), "%" + name + "%"));

                    if (!jsonArray.contains(SmartminingConstant.ALLDATASITE))
                        list.add(cb.like(root.get("managerId").as(String.class), "%" + params + "%"));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return projectSlagSiteServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectSlagSite projectSlagSite, HttpServletRequest request) {
        try {
            projectSlagSite.setProjectId(Long.parseLong(request.getHeader("projectId")));
            ProjectSlagSite siteOld = new ProjectSlagSite();
            String temp = "";
            if (null != projectSlagSite.getId() && 0L != projectSlagSite.getId()) {
                siteOld = projectSlagSiteServiceI.get(projectSlagSite.getId());
            }
            temp = JSONObject.toJSONString(siteOld);
            ProjectSlagSite siteNew = projectSlagSiteServiceI.save(projectSlagSite);
            Long projectId = siteNew.getProjectId();
            projectSlagSiteManager.logModifyProjectSlagSite(JSONObject.parseObject(temp, ProjectSlagSite.class), siteNew, ModifyEnum.MODIFY, Long.parseLong(request.getHeader("projectId")));

            //todo 将修改后的数据发送给mqtt
            List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByProjectIdAndDeviceType(siteNew.getProjectId(), ProjectDeviceType.SlagTruckDevice.getAlian());
            for (ProjectDevice device : projectDeviceList) {
                if (device.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0) {
                    String cmdInd = "position";
                    String replyTopic = "smartmining/excavator/cloud/" + device.getUid() + "/request";
                    Long pktId = count;
                    Long slagSiteId = siteNew.getId();
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("cmdInd", cmdInd);
                    jobDataMap.put("pktId", pktId);
                    jobDataMap.put("topic", replyTopic);
                    jobDataMap.put("slagCarId", slagSiteId);
                    jobDataMap.put("projectId", projectId);
                    jobDataMap.put("deviceId", device.getUid());
                    String cron = QuartzConstant.MQTT_REPLY_CRON;
                    quartzManager.addJob(QuartzManager.createJobNameSlagSitePosition(device.getUid()), SlagSitePositionJob.class, cron, jobDataMap);
                    Integer requestCount = 0;
                    stringRedisTemplate.opsForValue().set(QuartzConstant.TASK_SLAG_SITE_POSITION + device.getUid(), requestCount.toString());
                    count++;
                }
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @RequestMapping("/validate")
    public Result queryByCode(HttpServletRequest request, @RequestParam String slagSiteCode) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.getAllByProjectIdAndSlagSiteCode(projectId, slagSiteCode);
        if (projectSlagSite != null)
            return Result.error("该渣场编号已经存在");
        else
            return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @Transactional
    public Object delete(@RequestBody List<Long> ids) {
        try {
            for(Long id : ids) {
                ProjectSlagSite siteOld = projectSlagSiteServiceI.get(id);
                projectSlagSiteServiceI.delete(id);
                projectSlagSiteManager.logModifyProjectSlagSite(siteOld, new ProjectSlagSite(), ModifyEnum.DELETE, siteOld.getProjectId());
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }
}
