package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.domain.ScheduleResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.ScheduleModelStartJob;
import com.seater.smartmining.schedule.SmartminingScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @Description:方案控制器
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/14 0014 18:05
 */
@RestController
@RequestMapping("/api/projectProgramme")
public class ProjectProgrammeController extends BaseController {

    @Autowired
    private ProjectProgrammeServiceI projectProgrammeServiceI;
    @Autowired
    private ProjectScheduleModelServiceI projectScheduleModelServiceI;
    @Autowired
    private ScheduleMachineModelServiceI scheduleMachineModelServiceI;
    @Autowired
    private ScheduleCarModelServiceI scheduleCarModelServiceI;
    @Autowired
    private SmartminingScheduleService smartminingScheduleService;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private QuartzManager quartzManager;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectProgramme> spec = new Specification<ProjectProgramme>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectProgramme> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectProgrammeServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectProgramme projectProgramme) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        projectProgramme.setProjectId(projectId);
        projectProgramme.setStart(false);
        if (projectProgramme.getId() == null || projectProgramme.getId() == 0) {
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            projectProgramme.setCreateId(sysUser.getId());
            projectProgramme.setCreateName(sysUser.getAccount());
        }
        projectProgrammeServiceI.save(projectProgramme);
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(HttpServletRequest request, @RequestBody List<Long> ids) {
        Long projectId = CommonUtil.getProjectId(request);
        List<ProjectScheduleModel> modelList = projectScheduleModelServiceI.getAllByProjectId(projectId);
        List<String> groupCodeList = new ArrayList<>();
        for (Long id : ids) {
            quartzManager.removeJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + id));
            for (ProjectScheduleModel model : modelList) {
                if (model.getProgrammeId() == id)
                    groupCodeList.add(model.getGroupCode());
            }
        }
        scheduleCarModelServiceI.deleteByGroupCodes(groupCodeList);
        scheduleMachineModelServiceI.deleteByGroupCodes(groupCodeList);
        projectScheduleModelServiceI.deleteByGroupCodes(groupCodeList);
        projectProgrammeServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/start")
    public Result start(HttpServletRequest request, @RequestParam Long programmeId, @RequestParam Boolean start) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        ProjectProgramme projectProgramme = projectProgrammeServiceI.get(programmeId);
        List<ProjectScheduleModel> projectScheduleModelList = projectScheduleModelServiceI.getAllByProjectIdAndProgrammeId(projectId, programmeId);
        if(projectScheduleModelList == null || projectScheduleModelList.size() < 1)
            return Result.error("请设置排班模板后再启动");
        else {
            if(start) {
                quartzManager.removeJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + projectProgramme.getId()));
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("projectId", projectId);
                jobDataMap.put("programmeId", projectProgramme.getId());
                quartzManager.addJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + projectProgramme.getId()), ScheduleModelStartJob.class, projectProgramme.getScheduleTime(), jobDataMap);
            }else{
                quartzManager.removeJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + projectProgramme.getId()));
            }
            projectProgramme.setStart(start);
            projectProgrammeServiceI.save(projectProgramme);
            return Result.ok();
        }
    }

    @RequestMapping("/startNow")
    public Result startNow(HttpServletRequest request, @RequestParam Long programmeId) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        List<ProjectScheduleModel> projectScheduleModelList = projectScheduleModelServiceI.getAllByProjectIdAndProgrammeId(projectId, programmeId);
        if(projectScheduleModelList == null || projectScheduleModelList.size() < 1) {
            return Result.error("请设置排班模板后再启动");
        } else{
            List<ScheduleMachineModel> scheduleMachineModelList = scheduleMachineModelServiceI.getAllByProjectId(projectId);
            List<ScheduleCarModel> scheduleCarModelList = scheduleCarModelServiceI.getAllByProjectId(projectId);
            List<ScheduleResponse> scheduleResponseList = new ArrayList<>();
            scheduleCarServiceI.deleteByProjectId(projectId);
            scheduleMachineServiceI.deleteByProjectId(projectId);
            projectScheduleServiceI.deleteAll(projectId);
            for(ProjectScheduleModel model : projectScheduleModelList){
                String json = JSON.toJSONString(model);
                ProjectSchedule projectSchedule = JSON.parseObject(json, ProjectSchedule.class);
                ScheduleResponse response = new ScheduleResponse();
                response.setProjectSchedule(projectSchedule);
                List<ScheduleMachine> scheduleMachineList = new ArrayList<>();
                for(ScheduleMachineModel scheduleMachineModel : scheduleMachineModelList){
                    if(scheduleMachineModel.getGroupCode().equals(model.getGroupCode())){
                        String machineJson = JSON.toJSONString(scheduleMachineModel);
                        ScheduleMachine scheduleMachine = JSON.parseObject(machineJson, ScheduleMachine.class);
                        scheduleMachineList.add(scheduleMachine);
                    }
                }
                List<ScheduleCar> scheduleCarList = new ArrayList<>();
                for(ScheduleCarModel scheduleMachineModel : scheduleCarModelList){
                    if(scheduleMachineModel.getGroupCode().equals(model.getGroupCode())){
                        String carJson = JSON.toJSONString(scheduleMachineModel);
                        ScheduleCar scheduleCar = JSON.parseObject(carJson, ScheduleCar.class);
                        scheduleCarList.add(scheduleCar);
                    }
                }
                response.setScheduleCarList(scheduleCarList);
                response.setScheduleMachineList(scheduleMachineList);
                scheduleResponseList.add(response);
            }
            smartminingScheduleService.saveNewSchedule(projectId, scheduleResponseList, 0);
        }
        return Result.ok();
    }
}
