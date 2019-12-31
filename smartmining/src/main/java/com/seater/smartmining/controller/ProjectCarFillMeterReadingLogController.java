package com.seater.smartmining.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectCarFillMeterReadingLog;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.ProjectCarFillMeterReadingLogServiceI;
import com.seater.smartmining.service.ProjectOtherDeviceServiceI;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/projectCarFillMeterReadingLog")
public class ProjectCarFillMeterReadingLogController {
    @Autowired
    ProjectCarFillMeterReadingLogServiceI projectCarFillMeterReadingServiceI;

    @Autowired
    ProjectServiceI projectServiceI;

    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    WorkDateService workDateService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, String oilCarCode) {
        try {

            if (StringUtils.isEmpty(oilCarCode)) {
                return Result.error("参数错误,请检查");
            }
            Specification<ProjectOtherDevice> specCar = new Specification<ProjectOtherDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectOtherDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("code").as(String.class), oilCarCode));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectOtherDevice> projectOtherDevices = projectOtherDeviceServiceI.queryWx(specCar);
            if (projectOtherDevices.size() < 1) {
                return Result.error("项目中不存在该油车,请检查");
            }

            Long projectId = Long.parseLong(request.getHeader("projectId"));
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectCarFillMeterReadingLog> spec = new Specification<ProjectCarFillMeterReadingLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(oilCarCode)) {
                        list.add(cb.equal(root.get("oilCarCode").as(String.class), oilCarCode));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarFillMeterReadingLog> content = projectCarFillMeterReadingServiceI.query(spec, PageRequest.of(cur, page)).getContent();

            //  没有就给新增一条
            if (content.size() < 1) {
                for (int i = 0; i < 2; i++) {
                    ProjectCarFillMeterReadingLog readingLog = new ProjectCarFillMeterReadingLog();
                    readingLog.setId(null);
                    readingLog.setPort(i + 1);
                    readingLog.setProjectId(projectId);
                    readingLog.setOilCarCode(oilCarCode);
                    readingLog.setOilCarId(projectOtherDevices.get(0).getId());
                    readingLog.setOperatorId(projectOtherDevices.get(0).getManagerId());
                    readingLog.setOperatorName(projectOtherDevices.get(0).getManagerName());
                    projectCarFillMeterReadingServiceI.save(readingLog);
                }

                content = projectCarFillMeterReadingServiceI.query(spec, PageRequest.of(cur, page)).getContent();
            }

            //  统计当月
            Specification<ProjectCarFillMeterReadingLog> specMonthPort1 = new Specification<ProjectCarFillMeterReadingLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("oilCarCode").as(String.class), oilCarCode));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    list.add(cb.equal(root.get("port").as(Integer.class), 1));
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfMonth(new Date()), DateUtil.endOfMonth(new Date())));

                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            Specification<ProjectCarFillMeterReadingLog> specMonthPort2 = new Specification<ProjectCarFillMeterReadingLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("oilCarCode").as(String.class), oilCarCode));
                    list.add(cb.equal(root.get("port").as(Integer.class), 2));
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfMonth(new Date()), DateUtil.endOfMonth(new Date())));

                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            List<ProjectCarFillMeterReadingLog> readingMonthPort1 = projectCarFillMeterReadingServiceI.queryWx(specMonthPort1);
            List<ProjectCarFillMeterReadingLog> readingMonthPort2 = projectCarFillMeterReadingServiceI.queryWx(specMonthPort2);
            //  当月合计
            Long monthTotal = 0L;
            for (ProjectCarFillMeterReadingLog reading : readingMonthPort1) {
                monthTotal = monthTotal + reading.getOilMeterTodayTotal();
            }
            for (ProjectCarFillMeterReadingLog reading : readingMonthPort2) {
                monthTotal = monthTotal + reading.getOilMeterTodayTotal();
            }

            //  两个端口
            List<ProjectCarFillMeterReadingLog> readingLogList = new ArrayList<>();
            HashMap<String, Object> resultObject = new HashMap<String, Object>();
            if (content.size() > 0) {
                //  当日加油升数
                resultObject.put("portsAddOilToday", content.get(0).getOilMeterTodayTotal() + content.get(1).getOilMeterTodayTotal());
                //  历史加油升数
                resultObject.put("portsAddOilHistory", projectCarFillMeterReadingServiceI.getHistoryByOilCarId(content.get(0).getOilCarId()));
                //  当月加油升数小计
                resultObject.put("portsAddOilMonth", monthTotal);
                readingLogList.add(content.get(0));
                readingLogList.add(content.get(1));
                //  两个油枪的记录
                resultObject.put("ports", readingLogList);
            } else {
                //  前端要求默认全部都要有对象
                resultObject.put("portsAddOilToday", 0L);
                resultObject.put("portsAddOilHistory", 0L);
                resultObject.put("portsAddOilMonth", 0L);
                ProjectCarFillMeterReadingLog port1 = new ProjectCarFillMeterReadingLog();
                port1.setPort(1);
                ProjectCarFillMeterReadingLog port2 = new ProjectCarFillMeterReadingLog();
                port2.setPort(2);
                readingLogList.add(port1);
                readingLogList.add(port2);
                resultObject.put("ports", readingLogList);
            }
            return Result.ok(resultObject);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping(value = "/newSave")
    @Transactional
    public Object newSave(@RequestBody ProjectCarFillMeterReadingLog projectCarFillLog, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            projectCarFillLog.setProjectId(projectId);
            projectCarFillMeterReadingServiceI.save(projectCarFillLog);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @PostMapping("/newQuery")
    public Object newQuery(Integer current, Integer pageSize, HttpServletRequest request, String oilCarCode, Integer port, Date startDate, Date endDate) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectCarFillMeterReadingLog> spec = new Specification<ProjectCarFillMeterReadingLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(oilCarCode)) {
                        list.add(cb.like(root.get("oilCarCode").as(String.class), "%" + oilCarCode + "%"));
                    }
                    if (!ObjectUtils.isEmpty(port)) {
                        list.add(cb.equal(root.get("port").as(Integer.class), port));
                    }
                    if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
                        list.add(cb.between(root.get("addTime").as(Date.class), startDate, endDate));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));

                    query.orderBy(cb.desc(root.get("updateDate").as(Date.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return projectCarFillMeterReadingServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/save")
    @Transactional
    public Object save(@RequestBody List<JSONObject> infoObject, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            for (JSONObject info : infoObject) {
                if (info.getString("endOilMeterToday") == null || info.getString("id") == null) {
                    return Result.error("参数错误,请检查");
                }
            }

            //  检查完成要保存的
            List<ProjectCarFillMeterReadingLog> readingLogList = new ArrayList<>();

            for (JSONObject info : infoObject) {
                //  更新的
                ProjectCarFillMeterReadingLog projectCarFillMeterReadingLogUpdate = new ProjectCarFillMeterReadingLog();
                //  旧的
                ProjectCarFillMeterReadingLog projectCarFillMeterReadingLog = projectCarFillMeterReadingServiceI.get(info.getLong("id"));
                //  输入大小对比 输入量 > 当日起始数
                if (info.getLong("endOilMeterToday") < projectCarFillMeterReadingLog.getStartOilMeterToday()) {
                    return Result.error("数据参数错误,请检查输入量是否小于当日起始量");
                }
                BeanUtils.copyProperties(projectCarFillMeterReadingLog, projectCarFillMeterReadingLogUpdate);
                //  设置当日油表终止数
                projectCarFillMeterReadingLogUpdate.setEndOilMeterToday(info.getLong("endOilMeterToday"));
                projectCarFillMeterReadingLogUpdate.setUpdateDate(new Date());
                projectCarFillMeterReadingLogUpdate.setOperatorId(sysUser.getId());
                projectCarFillMeterReadingLogUpdate.setOperatorName(sysUser.getName());

                //  当日油表合计数 = 当日油表终止数 - 当日油表初始数
                projectCarFillMeterReadingLogUpdate.setOilMeterTodayTotal(projectCarFillMeterReadingLogUpdate.getEndOilMeterToday() - projectCarFillMeterReadingLogUpdate.getStartOilMeterToday());
                //  根据给定的时间进行班次判定
                projectCarFillMeterReadingLogUpdate.setShifts(workDateService.getTargetDateShift(projectCarFillMeterReadingLogUpdate.getUpdateDate(), projectId));
                projectCarFillMeterReadingLogUpdate.setDateIdentification(workDateService.getTargetDateIdentification(projectCarFillMeterReadingLogUpdate.getUpdateDate(), projectId));
                readingLogList.add(projectCarFillMeterReadingLogUpdate);
            }

            for (ProjectCarFillMeterReadingLog readingLog : readingLogList) {
                projectCarFillMeterReadingServiceI.save(readingLog);
            }

            //  更新油车表的终止数
            for (JSONObject info : infoObject) {
                ProjectCarFillMeterReadingLog projectCarFillMeterReadingLog = projectCarFillMeterReadingServiceI.get(info.getLong("id"));

                ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.get(projectCarFillMeterReadingLog.getOilCarId());

                if (projectCarFillMeterReadingLog.getPort() == 1) {
                    projectOtherDevice.setEndOilMeterPort1(projectCarFillMeterReadingLog.getEndOilMeterToday());
                }
                if (projectCarFillMeterReadingLog.getPort() == 2) {
                    projectOtherDevice.setEndOilMeterPort2(projectCarFillMeterReadingLog.getEndOilMeterToday());
                }
                projectOtherDeviceServiceI.save(projectOtherDevice);
            }

            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("delete")
    @Transactional
    public Object delete(Long id) {
        try {
            projectCarFillMeterReadingServiceI.delete(id);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

}
