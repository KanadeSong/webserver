package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.VaildEnums;
import com.seater.smartmining.enums.WorkMergeSuccessEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.LocationUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import net.bytebuddy.asm.Advice;
import org.apache.shiro.SecurityUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/3 0003 23:40
 */
@RestController
@RequestMapping("/api/projectMqttCardReport")
public class ProjectMqttCardReportController extends BaseController {

    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectMachineLocationServiceI projectMachineLocationServiceI;
    @Autowired
    private ProjectMqttUpdateExctServiceI projectMqttUpdateExctServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ProjectCarMaterialServiceI projectCarMaterialServiceI;

    @RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Shift shift, @RequestParam Date date) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            ScheduleService.cardCountReport(projectId, shift, date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(Integer current, Integer pageSize, HttpServletRequest request, String carCode, Date startTime, Date endTime) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectMqttCardReport> specification = new Specification<ProjectMqttCardReport>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectMqttCardReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                if (StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode"), '%' + carCode + '%'));
                if (startTime != null && endTime != null)
                    list.add(cb.between(root.get("timeDischarge"), startTime, endTime));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        /*Page<ProjectMqttCardReport> reportPage = projectMqttCardReportServiceI.query(specification, PageRequest.of(cur, page));
        List<ProjectMqttCardReport> reportList = reportPage.getContent();
        for (ProjectMqttCardReport report : reportList) {
            Date createTime = report.getTimeDischarge();
            Date startDate = DateUtils.getAddSecondDate(createTime, -(60 * 60));
            ProjectCarWorkInfo projectCarWorkInfo = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndMaxTimeDischarge(projectId, carCode);
            if (projectCarWorkInfo != null) {
                Date lastTime = projectCarWorkInfo.getTimeDischarge();
                Long second = createTime.getTime() - lastTime.getTime();
                if (second < 60 * 60)
                    startDate = lastTime;
            }
            List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, report.getCarCode(), startDate, createTime);
            if (locationList != null && locationList.size() > 0) {
                ProjectMachineLocation projectMachineLocation = locationList.get(0);
                report.setLocationText(JSON.toJSONString(projectMachineLocation));
                Date startDateTwo = DateUtils.getAddSecondDate(projectMachineLocation.getCreateTime(), -(60 * 60));
                List<ProjectMqttUpdateExct> updateExctList = projectMqttUpdateExctServiceI.getAllByProjectIDAndSlagcarCodeAndCreateTime(projectId, report.getCarCode(), startDateTwo, projectMachineLocation.getCreateTime());
                ProjectMqttUpdateExct projectMqttUpdateExct = null;
                for(ProjectMqttUpdateExct exct : updateExctList){
                    if(exct.getSlagcarCode().equals(projectMachineLocation.getCarCode())){
                        projectMqttUpdateExct = exct;
                        break;
                    }
                }
                report.setMachineMayBe(JSON.toJSONString(projectMqttUpdateExct));
            }
        }
        int totalPages = reportPage.getTotalPages();
        long totalCounts = reportPage.getTotalElements();
        Map map = new HashMap();
        map.put("totalPages", totalPages);
        map.put("totalCounts", totalCounts);
        map.put("content", reportList);*/
        return Result.ok(projectMqttCardReportServiceI.query(specification, PageRequest.of(cur, page)));
    }

    @RequestMapping("/detail")
    public Result detail(HttpServletRequest request, @RequestParam String carCode, @RequestParam Date createTime) {
        Long projectId = CommonUtil.getProjectId(request);
        Date startTime = DateUtils.getAddSecondDate(createTime, -(60 * 60));
        List<ProjectMachineLocation> locationList = projectMachineLocationServiceI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startTime, createTime);
        List<ProjectMqttUpdateExct> updateExctList = projectMqttUpdateExctServiceI.getAllByProjectIDAndSlagcarCodeAndCreateTime(projectId, carCode, startTime, createTime);
        Map map = new HashMap();
        map.put("card", locationList);
        map.put("update", updateExctList);
        return Result.ok(map);
    }

    @RequestMapping("/solve")
    public Result solve(HttpServletRequest request, @RequestParam Long id, String machineCode, Long materialId, Date loadTime, Date timeDischarge, Long slagSiteId, @RequestParam Long distance, @RequestParam PricingTypeEnums pricingType, ProjectDispatchMode dispatchMode) {
        try {
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Long projectId = CommonUtil.getProjectId(request);
            ProjectMqttCardReport report = projectMqttCardReportServiceI.get(id);
            if (report == null)
                throw new SmartminingProjectException("请输入正确的异常ID");
            ProjectCar car = projectCarServiceI.get(report.getCarId());
            if (car == null)
                throw new SmartminingProjectException("渣车不存在，请确认是否已经退场或移除");
            ProjectDiggingMachine diggingMachine = null;
            if(report.getMachineId() != null && report.getMachineId() != 0){
                diggingMachine = projectDiggingMachineServiceI.get(report.getMachineId());
            }else {
                if (StringUtils.isNotEmpty(machineCode)) {
                    diggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                }
            }
            if (diggingMachine == null)
                throw new SmartminingProjectException("挖机不存在，请输入正确的挖机编号");
            ProjectCarWorkInfo info = new ProjectCarWorkInfo();
            info.setProjectId(projectId);
            info.setCarId(car.getId());
            info.setCarCode(car.getCode());
            info.setCarOwnerId(car.getOwnerId());
            info.setCarOwnerName(car.getOwnerName());
            info.setDiggingMachineId(diggingMachine.getId());
            info.setDiggingMachineCode(diggingMachine.getCode());
            if (report.getLoader() != null && report.getLoader() != 0) {
                ProjectMaterial projectMaterial = projectMaterialServiceI.get(report.getLoader());
                info.setMaterialId(projectMaterial.getId());
                info.setMaterialName(projectMaterial.getName());
            } else if (materialId != null && materialId != 0) {
                ProjectMaterial projectMaterial = projectMaterialServiceI.get(materialId);
                info.setMaterialId(projectMaterial.getId());
                info.setMaterialName(projectMaterial.getName());
            } else {
                throw new SmartminingProjectException("物料不存在，请选择所装载的物料");
            }
            if (report.getTimeLoad() != null && report.getTimeLoad().getTime() != 0)
                info.setTimeLoad(report.getTimeLoad());
            else if (loadTime != null && loadTime.getTime() != 0)
                info.setTimeLoad(loadTime);
            else
                throw new SmartminingProjectException("装载时间不存在，请输入该条作业的装载时间");
            if (report.getTimeDischarge() != null && report.getTimeDischarge().getTime() != 0)
                info.setTimeDischarge(report.getTimeDischarge());
            else if (timeDischarge != null && timeDischarge.getTime() != 0)
                info.setTimeDischarge(timeDischarge);
            else
                throw new SmartminingProjectException("卸载时间不存在，请输入该条作业的卸载时间");
            info.setCubic(car.getModifyCapacity());
            info.setPass(Score.UnPass);
            ProjectUnloadLog unloadLog = projectUnloadLogServiceI.getAllByProjectIDAndTimeDischargeAndCarCode(projectId, report.getTimeDischarge(), report.getCarCode());
            if (unloadLog != null) {
                ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.get(unloadLog.getSlagfieldID());
                if (projectSlagSite != null) {
                    info.setSlagSiteId(projectSlagSite.getId());
                    info.setSlagSiteName(projectSlagSite.getName());
                } else if (slagSiteId != null && slagSiteId != 0) {
                    projectSlagSite = projectSlagSiteServiceI.get(slagSiteId);
                    if (projectSlagSite != null) {
                        info.setSlagSiteId(projectSlagSite.getId());
                        info.setSlagSiteName(projectSlagSite.getName());
                    } else {
                        throw new SmartminingProjectException("渣场不存在，请输入正确的渣场信息");
                    }
                } else {
                    throw new SmartminingProjectException("渣场不存在，请输入正确的渣场信息");
                }
            } else if (slagSiteId != null && slagSiteId != 0) {
                ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.get(slagSiteId);
                if (projectSlagSite != null) {
                    info.setSlagSiteId(projectSlagSite.getId());
                    info.setSlagSiteName(projectSlagSite.getName());
                } else {
                    throw new SmartminingProjectException("渣场不存在，请输入正确的渣场信息");
                }
            } else {
                throw new SmartminingProjectException("请输入该记录倒渣的渣场");
            }
            info.setDistance(distance);
            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (info.getCubic() / 1000000L); //精确到分
            info.setPayableDistance(payableDistance);
            info.setAmount(amount);
            info.setShift(report.getShift());
            info.setUnLoadUp(true);
            info.setLoadUp(true);
            info.setStatus(ProjectCarWorkStatus.Finish);
            info.setRemark("异常作业信息恢复，默认不合格且挖机和渣场都无效");
            info.setIsVaild(VaildEnums.BOTHNOTVALID);
            info.setMergeCode(WorkMergeSuccessEnum.HandleErrorMerge.getValue());
            info.setMergeMessage(WorkMergeSuccessEnum.HandleErrorMerge.getName());
            info.setDetailId(sysUser.getId());
            info.setDetailName(sysUser.getAccount());
            info.setPricingType(pricingType);
            info.setDateIdentification(report.getDateIdentification());
            info.setInfoValid(false);
            if (report.getDispatchMode() != null && report.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) != 0)
                info.setDispatchMode(report.getDispatchMode());
            else if (dispatchMode != null && report.getDispatchMode().compareTo(ProjectDispatchMode.Unknown) != 0)
                info.setDispatchMode(dispatchMode);
            else
                throw new SmartminingProjectException("请输入调度模式");
            projectCarWorkInfoServiceI.save(info);
            report.setDetail(true);
            projectMqttCardReportServiceI.save(report);
            return Result.ok();
        } catch (SmartminingProjectException e) {
            return Result.error(e.getMsg());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("后台异常");
        }
    }

}
