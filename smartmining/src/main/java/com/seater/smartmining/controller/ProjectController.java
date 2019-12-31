package com.seater.smartmining.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.quartz.MeterReadingJob;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.ScheduleModelStartJob;
import com.seater.smartmining.quartz.schedule.MqttCardReportJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.ScheduleConfig;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResultArr;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.entity.UseType;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectScheduledServiceI projectScheduledServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ProjectCarMaterialServiceI projectCarMaterialServiceI;
    @Autowired
    private ProjectModifyLogServiceI projectModifyLogServiceI;
    @Autowired
    private InterPhoneUtil interPhoneUtil;
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private SysRoleServiceI sysRoleServiceI;
    @Autowired
    private SysUserProjectRoleServiceI sysUserProjectRoleServiceI;
    @Autowired
    private SysUserServiceI sysUserServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectProgrammeServiceI projectProgrammeServiceI;

    //任务调度类的对象
    ApplicationContext context = new AnnotationConfigApplicationContext(ScheduleConfig.class);
    ScheduleConfig config = (ScheduleConfig) context.getBean("scheduleConfig");

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    /**
     * 项目启动时调用 查询出所有的需要任务调度的对象
     * 并获取到任务调度类的对象 服务器仅调用一次此方法
     */
    @PostConstruct
    public void scheduleInit() {
        //获取到所有的Project对象
        try {
            List<Project> projectList = projectServiceI.getAll();
            Date date = new Date();
            System.out.println("初始化操作：任务调度开始执行");
            for (Project project : projectList) {
                Map<String, Date> dateMap = workDateService.getWorkTime(project.getId(), date);
                Date start = dateMap.get("start");
                if (date.getTime() < start.getTime())
                    date = DateUtils.subtractionOneDay(date);
                config.startCron(project, date);
                startQuartz(project);
                //startCardReport(project);
            }
            List<ProjectProgramme> projectProgrammeList = projectProgrammeServiceI.getAll();
            //查询已经启动的排班方案 并开启
            for(ProjectProgramme programme : projectProgrammeList){
                Long projectId = programme.getProjectId();
                Long programmeId = programme.getId();
                if(programme.getStart()){
                    quartzManager.removeJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + programmeId));
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("projectId", projectId);
                    jobDataMap.put("programmeId", programmeId);
                    quartzManager.addJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + programmeId), ScheduleModelStartJob.class, programme.getScheduleTime(), jobDataMap);
                }else{
                    quartzManager.removeJob(QuartzManager.createJobNameTaskScheduleModel("projectId" + projectId + "programmeId" + programmeId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行quartz任务调度
     *
     * @param project 目标项目
     */
    private void startQuartz(Project project) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("项目:{}", JSONObject.toJSONString(project));
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(QuartzConstant.PROJECT, project);
        String earlyStartCron = DateUtils.getCronTimeLoop(DateUtil.parse(DateUtil.format(project.getEarlyStartTime(), DatePattern.NORM_TIME_PATTERN)));
        String nightStartCron = DateUtils.getCronTimeLoop(DateUtil.parse(DateUtil.format(project.getNightStartTime(), DatePattern.NORM_TIME_PATTERN)));
        //早班开始
        quartzManager.addJob(QuartzManager.createJobNameDay(project), MeterReadingJob.class, earlyStartCron, jobDataMap);
        //晚班开始
        quartzManager.addJob(QuartzManager.createJobNameNight(project), MeterReadingJob.class, nightStartCron, jobDataMap);
    }

    private void startCardReport(Project project){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(QuartzConstant.PROJECT, project.getId());
        jobDataMap.put("shift", Shift.Early);
        String earlyStartCron = DateUtils.getCronTimeLoop(DateUtil.parse(DateUtil.format(project.getEarlyStartTime(), DatePattern.NORM_TIME_PATTERN)));
        String nightStartCron = DateUtils.getCronTimeLoop(DateUtil.parse(DateUtil.format(project.getNightStartTime(), DatePattern.NORM_TIME_PATTERN)));
        //早班开始
        quartzManager.addJob(QuartzManager.createJobNameDayByMqtt(project), MqttCardReportJob.class, earlyStartCron, jobDataMap);
        jobDataMap.put("shift", Shift.Night);
        //晚班开始
        quartzManager.addJob(QuartzManager.createJobNameNightByMqtt(project), MqttCardReportJob.class, nightStartCron, jobDataMap);
    }

    /**
     * 保存项目时修改quartz任务调度
     *
     * @param project 目标项目
     */
    private void modifyQuartz(Project project) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(QuartzConstant.PROJECT, project);
        //早班
        quartzManager.modifyJobTime(QuartzManager.createJobNameDay(project), DateUtils.getCronTimeLoop(project.getEarlyStartTime()), jobDataMap);
        //晚班
        quartzManager.modifyJobTime(QuartzManager.createJobNameNight(project), DateUtils.getCronTimeLoop(project.getNightStartTime()), jobDataMap);
    }

    private void saveDept(Project projectOld, Project projectNew) {
        //  创建项目时创建对讲机相应的部门
        try {
            // 新增
            if (!projectOld.getId().equals(projectNew.getId())) {
                // 查询一次,如果没有部门就新增
                JSONObject checkObj = new JSONObject();
                checkObj.put("projectId", projectNew.getId().toString());
                Object check = interPhoneUtil.departmentFindByGroupId(checkObj);
                if (check == null) {
                    JSONObject deptObj = new JSONObject();
                    deptObj.put("projectId", projectNew.getId());
                    deptObj.put("projectName", projectNew.getName());
                    interPhoneUtil.createDepartment(deptObj);
                }
            } else {
                //修改
                JSONObject deptObj = new JSONObject();
                deptObj.put("projectId", projectNew.getId().toString());
                Object o = interPhoneUtil.departmentFindByGroupId(deptObj);
                if (null != o) {
                    InterPhoneResultArr dept = (InterPhoneResultArr) o;
                    String id = dept.getData().getJSONObject(0).getString("id");
                    deptObj.put("id", id);
                    deptObj.put("name", projectNew.getName());
                    interPhoneUtil.updateDepartment(deptObj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(Project project, HttpServletRequest request) {
        try {
            if (project.getId() != null && project.getId() != 0) {
                Project projectOld = projectServiceI.get(project.getId());
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                ProjectModifyLog log = new ProjectModifyLog();
                log.setUserId(sysUser.getId());
                log.setUserName(sysUser.getAccount());
                log.setNotEnd(project.getEnd());
                log.setBeforeDispatchMode(projectOld.getDispatchMode());
                log.setBeforeEarlyEndTime(projectOld.getEarlyEndTime());
                log.setBeforeEarlyStartTime(projectOld.getEarlyStartTime());
                log.setBeforeNightStartTime(projectOld.getNightStartTime());
                log.setBeforeNightEndTime(projectOld.getNightEndTime());
                log.setBeforeOilPrice(projectOld.getOilPirce());
                log.setBeforeProjectType(projectOld.getProjectType());
                log.setBeforeStatus(projectOld.getStatus());
                log.setBeforeEarlyEndPoint(projectOld.getEarlyEndPoint());
                log.setBeforeNightStartPoint(projectOld.getNightStartPoint());
                log.setBeforeNightEndPoint(projectOld.getNightEndPoint());
                if (project.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) != 0)
                    log.setDispatchMode(project.getDispatchMode());
                else
                    log.setDispatchMode(projectOld.getDispatchMode());

                if (project.getEarlyEndTime().getTime() != 35999000L)
                    log.setEarlyEndTime(project.getEarlyEndTime());
                else
                    log.setEarlyEndTime(projectOld.getEarlyEndTime());

                if (project.getEarlyStartTime().getTime() != -7200000L)
                    log.setEarlyStartTime(project.getEarlyStartTime());
                else
                    log.setEarlyStartTime(projectOld.getEarlyStartTime());

                if (project.getEarlyStartTime().getTime() != 36000000L)
                    log.setNightStartTime(project.getNightStartTime());
                else
                    log.setNightStartTime(projectOld.getNightStartTime());

                if (project.getNightEndTime().getTime() != -7201000)
                    log.setNightEndTime(project.getNightEndTime());
                else
                    log.setNightEndTime(projectOld.getNightEndTime());

                log.setProjectId(project.getId());

                if (StringUtils.isNotEmpty(project.getName()))
                    log.setProjectName(project.getName());
                else
                    log.setProjectName(projectOld.getName());

                if (project.getProjectType().compareTo(ProjectType.Unknown) != 0)
                    log.setProjectType(project.getProjectType());
                else
                    log.setProjectType(projectOld.getProjectType());

                if (project.getOilPirce() != 0)
                    log.setOilPrice(project.getOilPirce());
                else
                    log.setOilPrice(projectOld.getOilPirce());

                if (project.getStatus().compareTo(ProjectStatus.Unknown) != 0)
                    log.setStatus(project.getStatus());
                else
                    log.setStatus(projectOld.getStatus());

                log.setEarlyEndPoint(project.getEarlyEndPoint());
                log.setNightStartPoint(project.getNightStartPoint());
                log.setNightEndPoint(project.getNightEndPoint());

                log.setCreateTime(new Date());
                log.setModifyEnum(ModifyEnum.MODIFY);
                projectModifyLogServiceI.save(log);
            }
            Project projectNew = projectServiceI.save(project);
            //调用任务调度的方法
            Date date = new Date();
            /*List<Map> mapList = projectCarWorkInfoServiceI.getMachineIdListByDate(project.getId(), date);*/
            /*config.startCron(mapList, project, date);*/
            config.startCron(projectNew, date);
            modifyQuartz(projectNew);
            saveDept(project, projectNew);

            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping(value = "/saveAll", produces = "application/json")
    @Transactional
    public Object save(@RequestBody List<Project> projectList, HttpServletRequest request) {
        try {
            for (Project project : projectList) {
                if (project.getId() != null && project.getId() != 0) {
                    Project projectOld = projectServiceI.get(project.getId());
                    //获取当前用户对象
                    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                    ProjectModifyLog log = new ProjectModifyLog();
                    log.setUserId(sysUser.getId());
                    log.setUserName(sysUser.getAccount());
                    log.setNotEnd(project.getEnd());
                    log.setBeforeDispatchMode(projectOld.getDispatchMode());
                    log.setBeforeEarlyEndTime(projectOld.getEarlyEndTime());
                    log.setBeforeEarlyStartTime(projectOld.getEarlyStartTime());
                    log.setBeforeNightStartTime(projectOld.getNightStartTime());
                    log.setBeforeNightEndTime(projectOld.getNightEndTime());
                    log.setBeforeOilPrice(projectOld.getOilPirce());
                    log.setBeforeProjectType(projectOld.getProjectType());
                    log.setBeforeStatus(projectOld.getStatus());
                    log.setBeforeEarlyEndPoint(projectOld.getEarlyEndPoint());
                    log.setBeforeNightStartPoint(projectOld.getNightStartPoint());
                    log.setBeforeNightEndPoint(projectOld.getNightEndPoint());
                    if (project.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) != 0)
                        log.setDispatchMode(project.getDispatchMode());
                    else
                        log.setDispatchMode(projectOld.getDispatchMode());

                    if (project.getEarlyEndTime().getTime() != 35999000L)
                        log.setEarlyEndTime(project.getEarlyEndTime());
                    else
                        log.setEarlyEndTime(projectOld.getEarlyEndTime());

                    if (project.getEarlyStartTime().getTime() != -7200000L)
                        log.setEarlyStartTime(project.getEarlyStartTime());
                    else
                        log.setEarlyStartTime(projectOld.getEarlyStartTime());

                    if (project.getEarlyStartTime().getTime() != 36000000L)
                        log.setNightStartTime(project.getNightStartTime());
                    else
                        log.setNightStartTime(projectOld.getNightStartTime());

                    if (project.getNightEndTime().getTime() != -7201000)
                        log.setNightEndTime(project.getNightEndTime());
                    else
                        log.setNightEndTime(projectOld.getNightEndTime());

                    log.setProjectId(project.getId());

                    if (StringUtils.isNotEmpty(project.getName()))
                        log.setProjectName(project.getName());
                    else
                        log.setProjectName(projectOld.getName());

                    if (project.getProjectType().compareTo(ProjectType.Unknown) != 0)
                        log.setProjectType(project.getProjectType());
                    else
                        log.setProjectType(projectOld.getProjectType());

                    if (project.getOilPirce() != 0)
                        log.setOilPrice(project.getOilPirce());
                    else
                        log.setOilPrice(projectOld.getOilPirce());

                    if (project.getStatus().compareTo(ProjectStatus.Unknown) != 0)
                        log.setStatus(project.getStatus());
                    else
                        log.setStatus(projectOld.getStatus());

                    log.setEarlyEndPoint(project.getEarlyEndPoint());
                    log.setNightStartPoint(project.getNightStartPoint());
                    log.setNightEndPoint(project.getNightEndPoint());

                    log.setCreateTime(new Date());
                    log.setModifyEnum(ModifyEnum.MODIFY);
                    projectModifyLogServiceI.save(log);
                }
                Project projectNew = projectServiceI.save(project);
                //调用任务调度的方法
                Date date = new Date();
                /*List<Map> mapList = projectCarWorkInfoServiceI.getMachineIdListByDate(project.getId(), date);*/
                /*config.startCron(mapList, project, date);*/
                config.startCron(projectNew, date);
                modifyQuartz(projectNew);
                saveDept(project, projectNew);
            }
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            Project project = projectServiceI.get(id);
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            ProjectModifyLog log = new ProjectModifyLog();
            log.setUserId(sysUser.getId());
            log.setUserName(sysUser.getAccount());
            log.setBeforeDispatchMode(project.getDispatchMode());
            log.setBeforeEarlyEndTime(project.getEarlyEndTime());
            log.setBeforeEarlyStartTime(project.getEarlyStartTime());
            log.setBeforeNightStartTime(project.getNightStartTime());
            log.setBeforeNightEndTime(project.getNightEndTime());
            log.setBeforeOilPrice(project.getOilPirce());
            log.setBeforeProjectType(project.getProjectType());
            log.setBeforeStatus(project.getStatus());
            log.setCreateTime(new Date());
            log.setModifyEnum(ModifyEnum.DELETE);
            projectModifyLogServiceI.save(log);
            projectServiceI.delete(id);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping(value = "/deleteAll", produces = "application/json")
    public Object deleteAll(@RequestBody List<Long> ids) {
        try {
            List<ProjectModifyLog> logList = new ArrayList<>();
            for (Long id : ids) {
                Project project = projectServiceI.get(id);
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                ProjectModifyLog log = new ProjectModifyLog();
                log.setUserId(sysUser.getId());
                log.setUserName(sysUser.getAccount());
                log.setBeforeDispatchMode(project.getDispatchMode());
                log.setBeforeEarlyEndTime(project.getEarlyEndTime());
                log.setBeforeEarlyStartTime(project.getEarlyStartTime());
                log.setBeforeNightStartTime(project.getNightStartTime());
                log.setBeforeNightEndTime(project.getNightEndTime());
                log.setBeforeOilPrice(project.getOilPirce());
                log.setBeforeProjectType(project.getProjectType());
                log.setBeforeStatus(project.getStatus());
                log.setCreateTime(new Date());
                log.setModifyEnum(ModifyEnum.DELETE);
                logList.add(log);
            }
            projectModifyLogServiceI.batchSave(logList);
            projectServiceI.delete(ids);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/query")
    public Object query(@RequestParam(value = "time", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String name) {

        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        // 这里适应app端
        if(pageSize != null && pageSize < 1)
            page = 10000;

        Specification<Project> spec = new Specification<Project>() {
            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (name != null && !name.isEmpty())
                    list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));

                if (reangePickerValue != null && reangePickerValue.size() == 2) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                        Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                        Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                        list.add(cb.between(root.get("startTime").as(Date.class), startTime, endTime));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                query.orderBy(cb.asc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return projectServiceI.query(spec, PageRequest.of(cur, page));
    }

    @RequestMapping("/queryByPlat")
    public Result query(Integer current, Integer pageSize, Project project) throws IOException {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<Project> spec = new Specification<Project>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (StringUtils.isNotEmpty(project.getName()))
                    list.add(cb.like(root.get("name").as(String.class), "%" + project.getName() + "%"));
                if (project.getStatus().compareTo(ProjectStatus.Unknown) != 0)
                    list.add(cb.equal(root.get("status").as(ProjectStatus.class), project.getStatus()));
                if (StringUtils.isNotEmpty(project.getAddress()))
                    list.add(cb.like(root.get("address").as(String.class), "%" + project.getAddress() + "%"));
                if (StringUtils.isNotEmpty(project.getDetailAddress()))
                    list.add(cb.like(root.get("detailAddress").as(String.class), "%" + project.getDetailAddress() + "%"));
                if (StringUtils.isNotEmpty(project.getRootUser()))
                    list.add(cb.like(root.get("rootUser").as(String.class), "%" + project.getRootUser() + "%"));
                if (project.getBeginDate() != null && project.getEndDate() != null) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                        Date startTime = simpleDateFormat.parse(DateUtils.formatDateByPattern(project.getBeginDate(), SmartminingConstant.DATEFORMAT));
                        Date endTime = simpleDateFormat.parse(DateUtils.formatDateByPattern(project.getEndDate(), SmartminingConstant.DATEFORMAT));
                        list.add(cb.between(root.get("createDate").as(Date.class), startTime, endTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (StringUtils.isNotEmpty(project.getChargePerson()))
                    list.add(cb.like(root.get("chargePerson").as(String.class), "%" + project.getChargePerson() + "%"));
                if (StringUtils.isNotEmpty(project.getContact()))
                    list.add(cb.like(root.get("contact").as(String.class), "%" + project.getContact() + "%"));
                if (project.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) != 0)
                    list.add(cb.equal(root.get("dispatchMode").as(ProjectDispatchMode.class), project.getDispatchMode()));
                if (project.getProjectType().compareTo(ProjectType.Unknown) != 0)
                    list.add(cb.equal(root.get("projectType").as(ProjectType.class), project.getProjectType()));

                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        List<Project> projectList = projectServiceI.query(spec, PageRequest.of(cur, page)).getContent();
        List<JSONObject> otherInfo = getOtherInfo(projectList);
        return Result.ok(otherInfo, otherInfo.size());
    }

    private List<JSONObject> getOtherInfo(List<Project> projectList) throws IOException {
        List<SysRole> roleList = sysRoleServiceI.getAll();
        List<SysUserProjectRole> userProjectRoleList = sysUserProjectRoleServiceI.getAll();
        List<JSONObject> resultList = new ArrayList<>();
        for (Project p : projectList) {
            List<SysUser> userList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("project", p);
            List<SysRole> roles = new ArrayList<>();
            List<SysRole> subRole = new ArrayList<>();
            Set<SysRole> roleSet = new HashSet<>();
            List<JSONObject> userRoot = new ArrayList<>();
            //默认角色
            for (SysRole r : roleList) {
                //1.第一种是根用户的角色
                if (null != r.getProjectId() &&
                        null != r.getUseType() &&
                        r.getProjectId().equals(p.getId()) &&
                        r.getUseType().equals(UseType.Default)) {
                    if (null != r.getParentId()) {
                        roles.add(sysRoleServiceI.get(r.getParentId()));
                    }
                }
                //2.第二种是创建出来给项目的默认角色
                if (null != r.getProjectId() &&
                        null != r.getUseType() &&
                        null != r.getIsDefault() &&
                        r.getProjectId().equals(p.getId()) &&
                        r.getIsDefault() &&
                        r.getUseType().equals(UseType.Project)) {
                    if (null != r.getParentId()) {
                        //屏蔽子角色,显示父级
                        roles.add(sysRoleServiceI.get(r.getParentId()));
                    }

                    //分配给项目的默认角色的子角色列表,重新设置默认岗位时需要用到
                    subRole.add(r);
                }
                roleSet.addAll(roles);
                jsonObject.put("role", roleSet);
                jsonObject.put("subRole", subRole);
            }

            //根用户
            for (SysUserProjectRole userProjectRole : userProjectRoleList) {
                if (null != userProjectRole.getIsRoot() && userProjectRole.getIsRoot() && userProjectRole.getProjectId().equals(p.getId())) {
                    SysUser user = sysUserServiceI.get(userProjectRole.getUserId());
                    userList.add(user);
                }
            }

            //user去重
            Set<SysUser> userSet = new HashSet<>(userList);
            for (SysUser user : userSet) {
                if (user == null) {
                    continue;
                }
                List<Long> roleIdList = new ArrayList<>();
                JSONObject userRole = new JSONObject();
                List<SysRole> roleListRoot = new ArrayList<>();
                List<Long> uprList = new ArrayList<>();

                for (SysUserProjectRole userProjectRole : userProjectRoleList) {
                    if (user.getId().equals(userProjectRole.getUserId()) && userProjectRole.getProjectId().equals(p.getId()) && null != userProjectRole.getIsRoot() && userProjectRole.getIsRoot()) {
                        roleIdList.add(userProjectRole.getRoleId());
                        uprList.add(userProjectRole.getId());
                    }
                }

                for (Long roleId : roleIdList) {
                    for (SysRole role : roleList) {
                        if (roleId.equals(role.getId())) {
                            roleListRoot.add(role);
                        }
                    }
                }

                userRole = JSONObject.parseObject(JSONObject.toJSONString(user));
                userRole.put("role", roleListRoot);
                userRole.put("userProjectRole", uprList);
                userRoot.add(userRole);
            }
            jsonObject.put("root", userRoot);
            resultList.add(jsonObject);
        }
        return resultList;
    }

    @RequestMapping("/get")
    public Object get(HttpServletRequest request) {
        try {
            return projectServiceI.get(Long.parseLong(request.getHeader("projectId")));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/setOilPrice")
    public Result setOilPrice(HttpServletRequest request, Long oilPrice){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Project project = projectServiceI.get(projectId);
            project.setOilPirce(oilPrice);

            ProjectModifyLog log = new ProjectModifyLog();
            log.setProjectId(projectId);
            log.setNotEnd(project.getEnd());
            log.setUserId(sysUser.getId());
            log.setUserName(sysUser.getAccount());
            log.setBeforeDispatchMode(project.getDispatchMode());
            log.setBeforeEarlyEndTime(project.getEarlyEndTime());
            log.setBeforeEarlyStartTime(project.getEarlyStartTime());
            log.setBeforeNightStartTime(project.getNightStartTime());
            log.setBeforeNightEndTime(project.getNightEndTime());
            log.setBeforeOilPrice(project.getOilPirce());
            log.setBeforeProjectType(project.getProjectType());
            log.setBeforeStatus(project.getStatus());
            log.setBeforeEarlyEndPoint(project.getEarlyEndPoint());
            log.setBeforeNightStartPoint(project.getNightStartPoint());
            log.setBeforeNightEndPoint(project.getNightEndPoint());
            log.setEarlyStartTime(project.getEarlyStartTime());
            log.setEarlyEndTime(project.getEarlyEndTime());
            log.setEarlyEndPoint(project.getEarlyEndPoint());
            log.setNightStartPoint(project.getNightStartPoint());
            log.setNightEndPoint(project.getNightEndPoint());
            log.setNightStartTime(project.getNightStartTime());
            log.setNightEndTime(project.getNightEndTime());
            log.setCreateTime(new Date());
            log.setModifyEnum(ModifyEnum.MODIFY);
            log.setDispatchMode(project.getDispatchMode());
            log.setProjectName(project.getName());
            log.setProjectType(project.getProjectType());
            log.setOilPrice(oilPrice);
            log.setStatus(project.getStatus());
            projectModifyLogServiceI.save(log);

            projectServiceI.save(project);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/setProjectType")
    public Result setProjectType(HttpServletRequest request, ProjectType projectType, ProjectDispatchMode projectDispatchMode){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Project project = projectServiceI.get(projectId);
            project.setProjectType(projectType);
            project.setDispatchMode(projectDispatchMode);

            ProjectModifyLog log = new ProjectModifyLog();
            log.setProjectId(projectId);
            log.setNotEnd(project.getEnd());
            log.setUserId(sysUser.getId());
            log.setUserName(sysUser.getAccount());
            log.setBeforeDispatchMode(project.getDispatchMode());
            log.setBeforeEarlyEndTime(project.getEarlyEndTime());
            log.setBeforeEarlyStartTime(project.getEarlyStartTime());
            log.setBeforeNightStartTime(project.getNightStartTime());
            log.setBeforeNightEndTime(project.getNightEndTime());
            log.setBeforeOilPrice(project.getOilPirce());
            log.setBeforeProjectType(project.getProjectType());
            log.setBeforeStatus(project.getStatus());
            log.setBeforeEarlyEndPoint(project.getEarlyEndPoint());
            log.setBeforeNightStartPoint(project.getNightStartPoint());
            log.setBeforeNightEndPoint(project.getNightEndPoint());
            log.setEarlyStartTime(project.getEarlyStartTime());
            log.setEarlyEndTime(project.getEarlyEndTime());
            log.setEarlyEndPoint(project.getEarlyEndPoint());
            log.setNightStartPoint(project.getNightStartPoint());
            log.setNightEndPoint(project.getNightEndPoint());
            log.setNightStartTime(project.getNightStartTime());
            log.setNightEndTime(project.getNightEndTime());
            log.setCreateTime(new Date());
            log.setModifyEnum(ModifyEnum.MODIFY);
            log.setDispatchMode(projectDispatchMode);
            log.setProjectName(project.getName());
            log.setProjectType(projectType);
            log.setOilPrice(project.getOilPirce());
            log.setStatus(project.getStatus());
            projectModifyLogServiceI.save(log);

            projectServiceI.save(project);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.ok();
        }
        return Result.ok();
    }

    @RequestMapping("/setWorkTime")
    @Transactional
    public Object setWorkTime(HttpServletRequest request, Time earlyStart, ProjectWorkTimePoint earlyEndPoint, Time earlyEnd, ProjectWorkTimePoint nightStartPoint, Time nightStart, ProjectWorkTimePoint nightEndPoint, Time nightEnd) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));

            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Project projectOld = projectServiceI.get(projectId);
            ProjectModifyLog log = new ProjectModifyLog();
            log.setProjectId(projectId);
            log.setNotEnd(projectOld.getEnd());
            log.setUserId(sysUser.getId());
            log.setUserName(sysUser.getAccount());
            log.setBeforeDispatchMode(projectOld.getDispatchMode());
            log.setBeforeEarlyEndTime(projectOld.getEarlyEndTime());
            log.setBeforeEarlyStartTime(projectOld.getEarlyStartTime());
            log.setBeforeNightStartTime(projectOld.getNightStartTime());
            log.setBeforeNightEndTime(projectOld.getNightEndTime());
            log.setBeforeOilPrice(projectOld.getOilPirce());
            log.setBeforeProjectType(projectOld.getProjectType());
            log.setBeforeStatus(projectOld.getStatus());
            log.setBeforeEarlyEndPoint(projectOld.getEarlyEndPoint());
            log.setBeforeNightStartPoint(projectOld.getNightStartPoint());
            log.setBeforeNightEndPoint(projectOld.getNightEndPoint());
            log.setEarlyStartTime(earlyStart);
            log.setEarlyEndTime(earlyEnd);
            log.setEarlyEndPoint(earlyEndPoint);
            log.setNightStartPoint(nightStartPoint);
            log.setNightEndPoint(nightEndPoint);
            log.setNightStartTime(nightStart);
            log.setNightEndTime(nightEnd);
            log.setCreateTime(new Date());
            log.setModifyEnum(ModifyEnum.MODIFY);
            log.setDispatchMode(projectOld.getDispatchMode());
            log.setProjectName(projectOld.getName());
            log.setProjectType(projectOld.getProjectType());
            log.setOilPrice(projectOld.getOilPirce());
            log.setStatus(projectOld.getStatus());
            projectModifyLogServiceI.save(log);

            projectServiceI.setWorkTime(projectId, earlyStart, earlyEndPoint, earlyEnd, nightStartPoint, nightStart, nightEndPoint, nightEnd);
            Project newProject = new Project();
            newProject.setId(projectId);
            newProject.setEarlyStartTime(earlyStart);
            newProject.setEarlyEndPoint(earlyEndPoint);
            newProject.setEarlyEndTime(earlyEnd);
            newProject.setNightStartTime(nightStart);
            newProject.setNightStartPoint(nightStartPoint);
            newProject.setNightEndTime(nightEnd);
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date start = dateMap.get("start");
            if (date.getTime() < start.getTime())
                date = DateUtils.subtractionOneDay(date);
            config.startCron(newProject, date);
            modifyQuartz(newProject);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }
}
