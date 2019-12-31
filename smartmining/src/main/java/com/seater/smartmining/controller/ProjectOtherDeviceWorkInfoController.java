package com.seater.smartmining.controller;

import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.DeviceDoStatusEnum;
import com.seater.smartmining.enums.ProjectDeviceType;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.OtherDeviceStatusJob;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.api.WorkStartOrEndService;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:除钻孔之外的其他设备工作信息
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 16:04
 */
@RestController
@RequestMapping("/api/projectOtherDeviceWorkInfo")
public class ProjectOtherDeviceWorkInfoController extends BaseController {

    @Autowired
    private ProjectOtherDeviceWorkInfoServiceI projectOtherDeviceWorkInfoServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectOtherDeviceServiceI projectOtherDeviceServiceI;
    @Autowired
    private ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private WorkStartOrEndService workStartOrEndService;

    private Long count = 0L;

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectOtherDeviceWorkInfo projectOtherDeviceWorkInfo) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date start = dateMap.get("start");
            if (date.getTime() < start.getTime())
                start = DateUtils.subtractionOneDay(start);
            Date dateIdentification = DateUtils.createReportDateByMonth(start);
            ShiftsEnums shiftsEnums = workDateService.getTargetDateShift(date, projectId);
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            ProjectOtherDevice device = projectOtherDeviceServiceI.getAllByProjectIdAndCodeAndCarType(projectId, projectOtherDeviceWorkInfo.getCode(), projectOtherDeviceWorkInfo.getCarType());
            if (device == null)
                return Result.error("设备不存在，请核对设备信息后重新添加。");
            projectOtherDeviceWorkInfo.setAuditorId(sysUser.getId());
            projectOtherDeviceWorkInfo.setAuditorName(sysUser.getAccount());
            projectOtherDeviceWorkInfo.setProjectId(projectId);
            projectOtherDeviceWorkInfo.setCreateTime(date);
            projectOtherDeviceWorkInfo.setShift(shiftsEnums);
            projectOtherDeviceWorkInfo.setRemark(sysUser.getName() + "手动添加");
            projectOtherDeviceWorkInfo.setStatus(ProjectOtherDeviceStatusEnum.Stop);
            projectOtherDeviceWorkInfo.setDateIdentification(dateIdentification);
            Long time = DateUtils.calculationHour(projectOtherDeviceWorkInfo.getStartTime(), projectOtherDeviceWorkInfo.getEndTime());
            BigDecimal workTime = new BigDecimal((float) time / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            projectOtherDeviceWorkInfo.setWorkTime(workTime);
            ProjectHourPrice projectHourPrice = projectHourPriceServiceI.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, device.getBrandId(), device.getModelId(), projectOtherDeviceWorkInfo.getCarType().getValue());
            if (projectHourPrice == null)
                return Result.error("该车辆并未设置计时单价，下机失败。");
            BigDecimal price = new BigDecimal((float) projectHourPrice.getPrice() / 100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            BigDecimal amount = price.multiply(workTime).setScale(2, BigDecimal.ROUND_CEILING);
            projectOtherDeviceWorkInfo.setAmount(amount);
            projectOtherDeviceWorkInfo.setCreateTime(date);
            projectOtherDeviceWorkInfoServiceI.save(projectOtherDeviceWorkInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(Integer current, Integer pageSize, HttpServletRequest request, String code, CarType carType, Date startTime, Date endTime) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectOtherDeviceWorkInfo> spec = new Specification<ProjectOtherDeviceWorkInfo>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectOtherDeviceWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (projectId != null)
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                if (StringUtils.isNotEmpty(code))
                    list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                if (startTime != null && endTime != null)
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                if (carType != null)
                    list.add(cb.equal(root.get("carType").as(CarType.class), carType));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectOtherDeviceWorkInfoServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids) {
        projectOtherDeviceWorkInfoServiceI.delete(ids);
        return Result.ok();
    }

    /**
     * 强制下机 ---》强制下机、故障、停用
     *
     * @param request
     * @param code
     * @param status
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/stop")
    public Result stop(HttpServletRequest request, @RequestParam String code, @RequestParam ProjectOtherDeviceStatusEnum status, @RequestParam CarType carType) {
        try {
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            Long projectId = CommonUtil.getProjectId(request);
            ProjectOtherDeviceWorkInfo workInfo = null;
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            DeviceDoStatusEnum doStatus = DeviceDoStatusEnum.UnKnow;
            if(status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0)
                doStatus = DeviceDoStatusEnum.Start;
            else
                doStatus = DeviceDoStatusEnum.Stop;
            ProjectDeviceType deviceType = ProjectDeviceType.Unknown;
            if(carType.compareTo(CarType.Forklift) == 0)
                deviceType = ProjectDeviceType.ForkliftDevice;
            else if(carType.compareTo(CarType.Roller) == 0)
                deviceType = ProjectDeviceType.RollerDevice;
            else if(carType.compareTo(CarType.GunHammer) == 0)
                deviceType = ProjectDeviceType.GunHammerDevice;
            else if(carType.compareTo(CarType.SingleHook) == 0)
                deviceType = ProjectDeviceType.SingleHookDevice;
            else if(carType.compareTo(CarType.WateringCar) == 0)
                deviceType = ProjectDeviceType.WateringCarDevice;
            else if(carType.compareTo(CarType.Scraper) == 0)
                deviceType = ProjectDeviceType.ScraperDevice;
            else if(carType.compareTo(CarType.Punch) == 0)
                deviceType = ProjectDeviceType.PunchDevice;
            ProjectOtherDevice device = projectOtherDeviceServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType);
            if (device == null) {
                handler.saveWorkLog(projectId, deviceType, 0L, code, doStatus, sysUser.getId(), sysUser.getAccount(), false, "设备不存在，请检查设备编号和设备类型，下机失败");
                throw new SmartminingProjectException("设备不存在，请检查设备编号和设备类型，下机失败");
            }
            ProjectHourPrice projectHourPrice = projectHourPriceServiceI.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, device.getBrandId(), device.getModelId(), carType.getValue());
            if (projectHourPrice == null) {
                handler.saveWorkLog(projectId, deviceType, device.getId(), code, doStatus, sysUser.getId(), sysUser.getAccount(), false, "该车辆并未设置计时单价，请设置单价后操作");
                throw new SmartminingProjectException("该车辆并未设置计时单价，请设置单价后操作");
            }
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date startTime = dateMap.get("start");
            if (date.getTime() < startTime.getTime())
                startTime = DateUtils.subtractionOneDay(startTime);
            Date dateIdentification = DateUtils.createReportDateByMonth(startTime);
            if (status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0) {
                List<ProjectOtherDeviceStatusEnum> statusList = new ArrayList<>();
                statusList.add(ProjectOtherDeviceStatusEnum.WoekRequest);
                workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType.getValue(), ProjectOtherDeviceStatusEnum.Working.getAlias());
                if (workInfo != null) {
                    handler.saveWorkLog(projectId, deviceType, device.getId(), code, doStatus, sysUser.getId(), sysUser.getAccount(), false, "该设备正在上机中，上机失败");
                    throw new SmartminingProjectException("该设备正在上机中，上机失败");
                } else {
                    ShiftsEnums shifts = workDateService.getTargetDateShift(date, projectId);
                    workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType.getValue(), ProjectOtherDeviceStatusEnum.WoekRequest.getAlias());
                    if (workInfo == null)
                        workInfo = new ProjectOtherDeviceWorkInfo();
                    workInfo.setProjectId(projectId);
                    workInfo.setStatus(ProjectOtherDeviceStatusEnum.Working);
                    workInfo.setStartTime(date);
                    workInfo.setShift(shifts);
                    workInfo.setAuditorId(sysUser.getId());
                    workInfo.setAuditorName(sysUser.getAccount());
                    workInfo.setDeviceId(device.getId());
                    workInfo.setCode(code);
                    workInfo.setCreateTime(date);
                    workInfo.setCarType(carType);
                    workInfo.setRemark("用户:" + sysUser.getAccount() + "强制开机");
                    workInfo.setDateIdentification(dateIdentification);
                    device.setStartTime(date);
                    device.setEndTime(new Date(0));
                }
            } else {
                workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType.getValue(), ProjectOtherDeviceStatusEnum.Working.getAlias());
                if (workInfo == null)
                    workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType.getValue(), ProjectOtherDeviceStatusEnum.StopRequest.getAlias());
                if (workInfo != null) {
                    workInfo.setEndTime(date);
                    workInfo.setStatus(status);
                    String remark = "用户:" + sysUser.getAccount();
                    if (status.compareTo(ProjectOtherDeviceStatusEnum.Stop) == 0)
                        remark = remark + "强制下机";
                    else if (status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0)
                        remark = remark + "强制上机";
                    else if (status.compareTo(ProjectOtherDeviceStatusEnum.Fault) == 0)
                        remark = remark + "因故障强制下机";
                    else if (status.compareTo(ProjectOtherDeviceStatusEnum.NotUse) == 0)
                        remark = remark + "因停用强制下机";
                    workInfo.setRemark(workInfo.getRemark() + "\t" + remark);
                    Long time = DateUtils.calculationHour(workInfo.getStartTime(), workInfo.getEndTime());
                    BigDecimal workTime = new BigDecimal((float) time / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                    workInfo.setWorkTime(workTime);
                    BigDecimal price = new BigDecimal((float) projectHourPrice.getPrice() / 100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                    BigDecimal amount = price.multiply(workTime).setScale(2, BigDecimal.ROUND_CEILING);
                    workInfo.setAmount(amount);
                    device.setEndTime(date);
                    device.setWorkTime(workTime);
                } else {
                    handler.saveWorkLog(projectId, deviceType, device.getId(), code, doStatus, sysUser.getId(), sysUser.getAccount(), false, "该设备未开机成功，下机失败");
                    throw new SmartminingProjectException("该设备未开机成功，下机失败");
                }
            }
            handler.saveWorkLog(projectId, deviceType, device.getId(), code, doStatus, sysUser.getId(), sysUser.getAccount(), true, "请求成功");
            device.setStatus(status);
            Integer returnStatus = 0;
            if (status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0)
                returnStatus = 1;
            else
                returnStatus = 2;
            //workStartOrEndService.sendMessageToDevice(carType, projectId, device.getCode(), device.getId(), returnStatus);
            projectOtherDeviceWorkInfoServiceI.save(workInfo);
            projectOtherDeviceServiceI.save(device);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    /**
     * 其它设备上下机审核
     *
     * @param request
     * @param deviceId
     * @param carType
     * @param status
     * @return
     */
    @Transactional
    @RequestMapping("/examine")
    public Result examine(HttpServletRequest request, @RequestParam Long deviceId, @RequestParam CarType carType, @RequestParam ProjectOtherDeviceStatusEnum status) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.get(deviceId);
            ProjectOtherDeviceWorkInfo workInfo = null;
            Date date = new Date();
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            if (status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0) {
                workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, otherDevice.getCode(), carType.getValue(), ProjectOtherDeviceStatusEnum.WoekRequest.getAlias());
                if (workInfo == null)
                    return Result.error("该设备并未请求开机");
                ShiftsEnums shifts = workDateService.getTargetDateShift(date, projectId);
                workInfo.setStartTime(date);
                workInfo.setShift(shifts);
                workInfo.setRemark(workInfo.getRemark() + "\t上机审核成功，确认审核用户：" + sysUser.getAccount());
                otherDevice.setStartTime(date);
                otherDevice.setEndTime(new Date(0));
            } else {
                workInfo = projectOtherDeviceWorkInfoServiceI.getAllByProjectIdAndCodeAndCarType(projectId, otherDevice.getCode(), carType.getValue(), ProjectOtherDeviceStatusEnum.StopRequest.getAlias());
                if (workInfo == null)
                    return Result.error("该设备并未请求下机");
                workInfo.setEndTime(date);
                Long time = DateUtils.calculationHour(workInfo.getStartTime(), workInfo.getEndTime());
                BigDecimal workTime = new BigDecimal((float) time / 3600L).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                workInfo.setWorkTime(workTime);
                ProjectHourPrice projectHourPrice = projectHourPriceServiceI.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, otherDevice.getBrandId(), otherDevice.getModelId(), carType.getValue());
                if (projectHourPrice == null)
                    return Result.error("该车辆并未设置计时单价，下机失败。");
                BigDecimal price = new BigDecimal((float) projectHourPrice.getPrice() / 100).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                BigDecimal amount = price.multiply(workTime).setScale(2, BigDecimal.ROUND_CEILING);
                workInfo.setAmount(amount);
                workInfo.setRemark(workInfo.getRemark() + "\t下机审核成功，确认审核用户：" + sysUser.getAccount());
                otherDevice.setEndTime(date);
                otherDevice.setWorkTime(workTime);
            }
            workInfo.setStatus(status);
            workInfo.setAuditorId(sysUser.getId());
            workInfo.setAuditorName(sysUser.getAccount());

            Integer returnStatus = 0;
            if (status.compareTo(ProjectOtherDeviceStatusEnum.Working) == 0)
                returnStatus = 1;
            else
                returnStatus = 2;
            workStartOrEndService.sendMessageToDevice(carType, projectId, otherDevice.getCode(), deviceId, returnStatus);
            projectOtherDeviceServiceI.save(otherDevice);
            projectOtherDeviceWorkInfoServiceI.save(workInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }
}
