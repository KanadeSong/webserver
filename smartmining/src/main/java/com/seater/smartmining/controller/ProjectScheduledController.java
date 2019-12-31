package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.MD5Helper;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.exception.SmartNullPointException;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Deprecated
@RestController
//@RequestMapping("/api/projectScheduled")
public class ProjectScheduledController {

    @Autowired
    private ProjectScheduledServiceI projectScheduledServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectLoadLogServiceI projectLoadLogServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;

    Long count = 0L;

    @RequestMapping("/query")
    @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_QUERY)
    public Object query(Integer current, Integer pageSize, Boolean isAll, HttpServletRequest request) {
        try {
            if (isAll != null && isAll)
                return projectScheduledServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            Specification<ProjectScheduled> spec = new Specification<ProjectScheduled>() {
                @Override
                public Predicate toPredicate(Root<ProjectScheduled> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectScheduledServiceI.query(spec);
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    /**
     * 优化前 暂时使用 todo 勿删
     * 混编
     */
    @RequestMapping(value = "/newSave", produces = "application/json")
    @Transactional
    /*@RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_SAVE)*/
    public Result scheduledManyToMany(@RequestBody List<ScheduledRequest> scheduledRequestList, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Project project = projectServiceI.get(projectId);
            if (scheduledRequestList != null) {
                Date createTime = DateUtils.createReportDateByMonth(new Date());
                String uuid = UUID.randomUUID().toString() + projectId;
                DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
                String employeeId = "";
                String employeeName = "";
                for (ScheduledRequest scheduled : scheduledRequestList) {
                    if (project.getProjectType().compareTo(ProjectType.EconomicVersion) == 0 || project.getProjectType().compareTo(ProjectType.EnhanceVersion) == 0) {
                        if (scheduledRequestList.size() > 1) {
                            throw new SmartminingProjectException("当前版本不支持混装");
                        }
                        if (scheduled.getCarsArray() != null || scheduled.getCarsArray().length > 0) {
                            for (int i = 0; i < scheduled.getCarsArray().length; i++) {
                                if (scheduled.getCarsArray()[i] != null && scheduled.getCarsArray()[i] != 0) {
                                    projectScheduledServiceI.deleteByCarIdAndProjectId(scheduled.getCarsArray()[i], projectId);
                                }
                            }
                        }
                    }
                    projectScheduledServiceI.deleteByDiggingMachineIdAndProjectId(scheduled.getMachineId(), projectId);
                    ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(scheduled.getMachineId());
                    if (machine == null) {
                        throw new SmartminingProjectException("挖机不存在！");
                    }
                    employeeId = employeeId + machine.getDriverId() + SmartminingConstant.COMMA;
                    employeeName = employeeName + machine.getDriverName() + SmartminingConstant.COMMA;
                    /*if (scheduled.getCarsArray() != null || scheduled.getCarsArray().length > 0) {
                        for (int i = 0; i < scheduled.getCarsArray().length; i++) {
                            if (scheduled.getCarsArray()[i] != null && scheduled.getCarsArray()[i] != 0) {
                                projectScheduledServiceI.deleteByCarIdAndProjectId(scheduled.getCarsArray()[i], projectId);
                                projectScheduledLogServiceI.deleteByCarIdAndProjectId(scheduled.getCarsArray()[i], projectId);
                            }
                        }
                    } else {
                        throw new SmartNullPointException("没有指定对应的渣车信息");
                    }*/
                }
                if (scheduledRequestList.get(0).getCarsArray() != null || scheduledRequestList.get(0).getCarsArray().length > 0) {
                    for (int i = 0; i < scheduledRequestList.get(0).getCarsArray().length; i++) {
                        if (scheduledRequestList.get(0).getCarsArray()[i] != null && scheduledRequestList.get(0).getCarsArray()[i] != 0) {
                            ProjectCar projectCar = projectCarServiceI.get(scheduledRequestList.get(0).getCarsArray()[i]);
                            if (projectCar != null) {
                                if (i < scheduledRequestList.get(0).getCarsArray().length - 1) {
                                    employeeId = employeeId + projectCar.getDriverId() + SmartminingConstant.COMMA;
                                    employeeName = employeeName + projectCar.getDriverName() + SmartminingConstant.COMMA;
                                } else {
                                    employeeId = employeeId + projectCar.getDriverId();
                                    employeeName = employeeName + projectCar.getDriverName();
                                }
                            } else {
                                throw new SmartminingProjectException("渣车车辆不存在");
                            }
                        }
                    }
                }
                for (ScheduledRequest scheduled : scheduledRequestList) {
                    ProjectScheduled projectScheduled = new ProjectScheduled();
                    projectScheduled.setProjectId(projectId);
                    projectScheduled.setGroupCode(uuid);
                    projectScheduled.setMaterialId(scheduled.getMaterialId());
                    projectScheduled.setMateriaName(scheduled.getMaterialName());
                    projectScheduled.setDiggingMachineId(scheduled.getMachineId());
                    projectScheduled.setDiggingMachineCode(scheduled.getMachineCode());
                    projectScheduled.setPricingType(scheduled.getPricingType());
                    //组长编号数组
                    String managerId = StringUtils.converArrayToString(scheduled.getManagerId());
                    projectScheduled.setManagerId(managerId);
                    //组长名称数组
                    String managerName = StringUtils.converArrayToString(scheduled.getManagerName());
                    projectScheduled.setManagerName(managerName);
                    //组员编号数组
                    projectScheduled.setEmployeeId(employeeId);
                    //组员名称数组
                    projectScheduled.setEmployeeName(employeeName);
                    ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(scheduled.getMachineId());
                    if (diggingMachine != null) {
                        if (StringUtils.isEmpty(diggingMachine.getUid())) {
                            ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, diggingMachine.getCode(), 2);
                            if (projectDevice != null) {
                                if (StringUtils.isNotEmpty(projectDevice.getUid())) {
                                    diggingMachine.setUid(projectDevice.getUid());
                                }
                            }
                        }
                        diggingMachine.setSelected(true);
                        projectDiggingMachineServiceI.save(diggingMachine);
                    }
                    if (diggingMachine != null) {
                        for (int i = 0; i < scheduled.getCarsArray().length; i++) {
                            if (scheduled.getCarsArray()[i] != null && scheduled.getCarsArray()[i] != 0) {
                                /*projectScheduledServiceI.deleteByCarIdAndProjectId(scheduled.getCarsArray()[i], projectId);
                                projectScheduledLogServiceI.deleteByCarIdAndProjectId(scheduled.getCarsArray()[i], projectId);*/
                                ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, scheduled.getCarsStrArray()[i], 1);
                                projectScheduled.setDistance(scheduled.getDistance());
                                projectScheduled.setDiggingMachineBrandId(diggingMachine.getBrandId());
                                projectScheduled.setDiggingMachineModelId(diggingMachine.getModelId());
                                projectScheduled.setDiggingMachineModelName(diggingMachine.getModelName());
                                projectScheduled.setDiggingMachineOwnerId(diggingMachine.getOwnerId());
                                projectScheduled.setDiggingMachineOwnerName(diggingMachine.getOwnerName());
                                projectScheduled.setCarId(scheduled.getCarsArray()[i]);
                                projectScheduled.setCarCode(scheduled.getCarsStrArray()[i]);
                                ProjectCar projectCar = projectCarServiceI.get(scheduled.getCarsArray()[i]);
                                if (projectCar != null) {
                                    projectScheduled.setCarBrandId(projectCar.getBrandId());
                                    projectScheduled.setCarBrandName(projectCar.getBrandName());
                                    projectScheduled.setCarModelId(projectCar.getModelId());
                                    projectScheduled.setCarModelName(projectCar.getModelName());
                                    projectScheduled.setCarOwnerId(projectCar.getOwnerId());
                                    projectScheduled.setCarOwnerName(projectCar.getOwnerName());
                                    projectScheduled.setEmployeeId(employeeId);
                                    projectScheduled.setEmployeeName(employeeName);
                                    if (projectDevice != null) {
                                        if (StringUtils.isNotEmpty(projectDevice.getUid())) {
                                            projectCar.setUid(projectDevice.getUid());
                                        }
                                    }
                                    projectCar.setSeleted(true);
                                    projectCarServiceI.save(projectCar);
                                    if (projectDevice != null) {
                                        if (StringUtils.isNotEmpty(projectDevice.getUid())) {
                                            //发送数据到设备
                                            String cmdInd = "schedule";
                                            Long pktID = count;
                                            Long slagcarID = scheduled.getCarsArray()[i];
                                            String replytopic = "smartmining/slagcar/cloud/" + projectDevice.getUid() + "/request";
                                            handler.handleMessageScheduleByCar(cmdInd, replytopic, pktID, projectId, slagcarID, projectDevice.getUid(), "云端主动请求");
                                            count++;
                                        }
                                    }
                                } else {
                                    throw new SmartNullPointException("渣车不存在");
                                }
                                /*projectScheduledServiceI.saveOrModify(projectScheduled);*/
                                projectScheduledServiceI.save(projectScheduled);
                            } else {
                                throw new SmartNullPointException("没有指定对应的渣车信息");
                            }
                        }

                    } else {
                        throw new SmartNullPointException("挖机不存在");
                    }
                    if (diggingMachine != null) {
                        /*DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");*/
                        String cmdInd = "schedule";
                        Long pktID = count;
                        Long excavatorID = projectScheduled.getDiggingMachineId();
                        String replytopic = "smartmining/excavator/cloud/" + diggingMachine.getUid() + "/request";
                        handler.handleMessageSchedule(cmdInd, replytopic, pktID, projectId, excavatorID, diggingMachine.getUid(), "云端主动请求");
                        count++;
                    }
                }
            } else {
                throw new SmartNullPointException("挖机数据不能为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    /**
     * 优化前 勿删 todo 暂时使用
     *
     * @param current
     * @param machineCode
     * @param carCode
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping("/newQuery")
    //@RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_QUERY)
    public Result newQuery(Integer current, String machineCode, String carCode, Integer pageSize, HttpServletRequest request) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            cur = cur * page;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //判断是查询全部还是筛选
            boolean flag = true;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if(jsonArray != null){
                if(jsonArray.contains(SmartminingConstant.MANAGERELATED))
                    flag = false;
            }

            List<Map> groupMap = new ArrayList<>();
            List<Map> countMap = new ArrayList<>();
            Integer total = 0;
            Project project = projectServiceI.get(projectId);
            List<ScheduledInfo> scheduledInfoList = new ArrayList<>();
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            //当前版本为升级版和完整版
            if (project.getProjectType().compareTo(ProjectType.UpgradeVersion) == 0 || project.getProjectType().compareTo(ProjectType.CompleteVersion) == 0) {
                countMap = projectScheduledServiceI.getByAllProjectIdOnGroupId(projectId);
                List<ProjectScheduled> projectScheduleds = null;
                if (StringUtils.isEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    if (flag) {
                        groupMap = projectScheduledServiceI.getByProjectIdOnGroupId(projectId, cur, page);
                    }else{
                        groupMap = projectScheduledServiceI.getByProjectIdAndManagerIdOnGroupId(projectId, "[^0-9]" + sysUser.getId() + "[^0-9]", cur, page);
                    }
                } else if (StringUtils.isNotEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineId(projectId, projectDiggingMachine.getId());
                    }else {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, projectDiggingMachine.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                } else if (StringUtils.isEmpty(machineCode) && StringUtils.isNotEmpty(carCode)) {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarId(projectId, projectCar.getId());
                    }else{
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, projectCar.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                } else {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(projectId, projectCar.getId(), projectDiggingMachine.getId());
                    }else{
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(projectId, projectCar.getId(), projectDiggingMachine.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                }
                if (projectScheduleds != null) {
                    Map map = new HashMap();
                    map.put("group_code", projectScheduleds.get(0).getGroupCode());
                    groupMap.add(map);
                }
                if (groupMap != null) {
                    for (int i = 0; i < groupMap.size(); i++) {
                        ScheduledInfo scheduledInfo = new ScheduledInfo();
                        if (groupMap.get(i) != null) {
                            String groupCode = "";
                            if (groupMap.get(i).get("group_code") != null)
                                groupCode = groupMap.get(i).get("group_code").toString();
                            List<Map> projectScheduledList = projectScheduledServiceI.getByProjectIdAndGroupCodeOrderByDiggingMachineId(projectId, groupCode);
                            if (projectScheduledList != null) {
                                List<ScheduledRequest> scheduledDiggingMachineList = new ArrayList<>();
                                for (int z = 0; z < projectScheduledList.size(); z++) {
                                    ScheduledRequest diggingMachine = new ScheduledRequest();
                                    diggingMachine.setGroupCode(groupCode);
                                    if (projectScheduledList.get(z).get("distance") != null)
                                        diggingMachine.setDistance(Long.parseLong(projectScheduledList.get(z).get("distance").toString()));
                                    if (projectScheduledList.get(z).get("digging_machine_code") != null)
                                        diggingMachine.setMachineCode(projectScheduledList.get(z).get("digging_machine_code").toString());
                                    if (projectScheduledList.get(z).get("digging_machine_id") != null)
                                        diggingMachine.setMachineId(Long.parseLong(projectScheduledList.get(z).get("digging_machine_id").toString()));
                                    if (projectScheduledList.get(z).get("material_id") != null)
                                        diggingMachine.setMaterialId(Long.parseLong(projectScheduledList.get(z).get("material_id").toString()));
                                    if (projectScheduledList.get(z).get("materia_name") != null)
                                        diggingMachine.setMaterialName(projectScheduledList.get(z).get("materia_name").toString());
                                    if (projectScheduledList.get(z).get("pricing_type") != null) {
                                        Integer value = Integer.valueOf(projectScheduledList.get(z).get("pricing_type").toString());
                                        PricingTypeEnums pricingType = PricingTypeEnums.convert(value);
                                        diggingMachine.setPricingType(pricingType);
                                    }
                                    if (projectScheduledList.get(z).get("digging_machine_brand_id") != null)
                                        diggingMachine.setDiggingMachineBrandId(Long.parseLong(projectScheduledList.get(z).get("digging_machine_brand_id").toString()));
                                    if (projectScheduledList.get(z).get("manager_id") != null) {
                                        String mId = projectScheduledList.get(z).get("manager_id").toString();
                                        String[] mids = mId.split(SmartminingConstant.COMMA);
                                        Long[] midsLong = new Long[mids.length];
                                        for (int j = 0; j < mids.length; j++) {
                                            if (StringUtils.isNotEmpty(mids[j])) {
                                                Long mid = Long.parseLong(mids[j]);
                                                midsLong[j] = mid;
                                            }
                                        }
                                        diggingMachine.setManagerId(midsLong);
                                    }
                                    if (projectScheduledList.get(z).get("manager_name") != null) {
                                        String mName = projectScheduledList.get(z).get("manager_name").toString();
                                        String[] mNames = mName.split(SmartminingConstant.COMMA);
                                        diggingMachine.setManagerName(mNames);
                                    }
                                    if (projectScheduledList.get(z).get("employee_id") != null) {
                                        String mId = projectScheduledList.get(z).get("employee_id").toString();//projectScheduledList.get(z).get("employee_id").toString();
                                        String[] empIds = mId.split(SmartminingConstant.COMMA);
                                        Long[] empIdsLong = new Long[empIds.length];
                                        for (int j = 0; j < empIds.length; j++) {
                                            if (StringUtils.isNotEmpty(empIds[j])) {
                                                Long mid = Long.parseLong(empIds[j]);
                                                empIdsLong[j] = mid;
                                            }
                                        }
                                        diggingMachine.setEmployeeId(empIdsLong);
                                    }
                                    if (projectScheduledList.get(z).get("employee_name") != null) {
                                        String eName = projectScheduledList.get(z).get("employee_name").toString(); //projectScheduledList.get(z).get("employee_name").toString();
                                        String[] eNames = eName.split(SmartminingConstant.COMMA);
                                        diggingMachine.setEmployeeName(eNames);
                                    }
                                    scheduledDiggingMachineList.add(diggingMachine);
                                    List<Map> carsInfoList = projectScheduledServiceI.getByProjectIdAndDiggingMachineId(projectId, diggingMachine.getMachineId());
                                    Long[] carsArray = new Long[carsInfoList.size()];
                                    String[] carsStrArray = new String[carsInfoList.size()];
                                    for (int j = 0; j < carsInfoList.size(); j++) {
                                        if (carsInfoList.get(j).get("car_id") != null)
                                            carsArray[j] = Long.parseLong(carsInfoList.get(j).get("car_id").toString());
                                        if (carsInfoList.get(j).get("car_code") != null)
                                            carsStrArray[j] = carsInfoList.get(j).get("car_code").toString();
                                    }
                                    diggingMachine.setCarsArray(carsArray);
                                    diggingMachine.setCarsStrArray(carsStrArray);
                                }
                                scheduledInfo.setScheduledDiggingMachineList(scheduledDiggingMachineList);
                                scheduledInfoList.add(scheduledInfo);
                            }
                        }
                    }
                }
            } else {
                List<ProjectScheduled> projectScheduledList = new ArrayList<>();
                if (StringUtils.isEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    groupMap = projectScheduledServiceI.getByProjectIdPage(projectId, cur, page);
                    countMap = projectScheduledServiceI.getByProjectIdCount(projectId);
                } else if (StringUtils.isNotEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                } else if (StringUtils.isEmpty(machineCode) && StringUtils.isNotEmpty(carCode)) {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndCarId(projectId, projectCar.getId());
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, projectScheduledList.get(0).getDiggingMachineCode());
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                } else {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndCarIdAndDiggingMachineId(projectId, projectCar.getId(), projectDiggingMachine.getId());
                    projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, projectScheduledList.get(0).getDiggingMachineCode());
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                }
                if (groupMap.size() > 0) {
                    for (int i = 0; i < groupMap.size(); i++) {
                        ScheduledInfo scheduledInfo = new ScheduledInfo();
                        Long machineId = Long.parseLong(groupMap.get(i).get("digging_machine_id").toString());
                        if(flag) {
                            projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndDiggingMachineId(projectId, machineId);
                        }else{
                            projectScheduledList = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, machineId, "[^0-9]" + sysUser.getId() + "[^0-9]");
                        }
                        if (projectScheduledList.size() > 0) {
                            String groupCode = projectScheduledList.get(0).getGroupCode();
                            List<ScheduledRequest> scheduledDiggingMachineList = new ArrayList<>();
                            ScheduledRequest scheduledRequest = new ScheduledRequest();
                            scheduledRequest.setGroupCode(groupCode);
                            scheduledRequest.setMachineId(projectScheduledList.get(0).getDiggingMachineId());
                            scheduledRequest.setMachineCode(projectScheduledList.get(0).getDiggingMachineCode());
                            scheduledRequest.setPricingType(projectScheduledList.get(0).getPricingType());
                            scheduledRequest.setMaterialName(projectScheduledList.get(0).getMateriaName());
                            scheduledRequest.setMaterialId(projectScheduledList.get(0).getMaterialId());
                            scheduledRequest.setDiggingMachineBrandId(projectScheduledList.get(0).getDiggingMachineBrandId());
                            scheduledRequest.setDistance(projectScheduledList.get(0).getDistance());

                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getManagerId())) {
                                String mId = projectScheduledList.get(0).getManagerId();
                                String[] mids = mId.split(SmartminingConstant.COMMA);
                                Long[] midsLong = new Long[mids.length];
                                for (int j = 0; j < mids.length; j++) {
                                    Long mid = Long.parseLong(mids[j]);
                                    midsLong[j] = mid;
                                }
                                scheduledRequest.setManagerId(midsLong);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getManagerName())) {
                                String mName = projectScheduledList.get(0).getManagerName();
                                String[] mNames = mName.split(SmartminingConstant.COMMA);
                                scheduledRequest.setManagerName(mNames);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getEmployeeId())) {
                                String mId = projectScheduledList.get(0).getEmployeeId();
                                String[] empIds = mId.split(SmartminingConstant.COMMA);
                                Long[] empIdsLong = new Long[empIds.length];
                                for (int j = 0; j < empIds.length; j++) {
                                    Long mid = Long.parseLong(empIds[j]);
                                    empIdsLong[j] = mid;
                                }
                                scheduledRequest.setEmployeeId(empIdsLong);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getEmployeeName())) {
                                String eName = projectScheduledList.get(0).getEmployeeName();
                                String[] eNames = eName.split(SmartminingConstant.COMMA);
                                scheduledRequest.setEmployeeName(eNames);
                            }
                            Long[] array = new Long[projectScheduledList.size()];
                            String[] arrayStr = new String[projectScheduledList.size()];
                            int j = 0;
                            for (ProjectScheduled scheduled : projectScheduledList) {
                                array[j] = scheduled.getCarId();
                                arrayStr[j] = scheduled.getCarCode();
                                j++;
                            }
                            scheduledRequest.setCarsArray(array);
                            scheduledRequest.setCarsStrArray(arrayStr);
                            scheduledDiggingMachineList.add(scheduledRequest);

                            scheduledInfo.setScheduledDiggingMachineList(scheduledDiggingMachineList);
                            scheduledInfoList.add(scheduledInfo);
                        }
                    }
                }
            }
            for (int i = 0; i < countMap.size(); i++) {
                if (countMap.get(i) != null)
                    total++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("list", scheduledInfoList);
            return Result.ok(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 优化中 todo 勿删
     *
     * @param current
     * @param machineCode
     * @param carCode
     * @param pageSize
     * @param request
     * @return
     */
    public Result newQuery(Integer current, String machineCode, String carCode, Integer pageSize, HttpServletRequest request, PricingTypeEnums pricingType) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            cur = cur * page;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //判断是查询全部还是筛选
            boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if(jsonArray != null){
                if(jsonArray.contains(SmartminingConstant.ALLDATA))
                    flag = true;
            }
            List<Map> groupMap = new ArrayList<>();
            List<Map> countMap = new ArrayList<>();
            Integer total = 0;
            Project project = projectServiceI.get(projectId);
            List<ScheduledInfo> scheduledInfoList = new ArrayList<>();
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            //当前版本为升级版和完整版
            if (project.getProjectType().compareTo(ProjectType.UpgradeVersion) == 0 || project.getProjectType().compareTo(ProjectType.CompleteVersion) == 0) {
                countMap = projectScheduledServiceI.getByAllProjectIdOnGroupId(projectId);
                List<ProjectScheduled> projectScheduleds = null;
                if (StringUtils.isEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    if (flag) {
                        groupMap = projectScheduledServiceI.getByProjectIdOnGroupId(projectId, cur, page);
                    }else{
                        groupMap = projectScheduledServiceI.getByProjectIdAndManagerIdOnGroupId(projectId, "[^0-9]" + sysUser.getId() + "[^0-9]", cur, page);
                    }
                } else if (StringUtils.isNotEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineId(projectId, projectDiggingMachine.getId());
                    }else {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, projectDiggingMachine.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                } else if (StringUtils.isEmpty(machineCode) && StringUtils.isNotEmpty(carCode)) {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarId(projectId, projectCar.getId());
                    }else{
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, projectCar.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                } else {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    if(flag) {
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(projectId, projectCar.getId(), projectDiggingMachine.getId());
                    }else{
                        projectScheduleds = projectScheduledServiceI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(projectId, projectCar.getId(), projectDiggingMachine.getId(), "[^0-9]" + sysUser.getId() + "[^0-9]");
                    }
                }
                if (projectScheduleds != null) {
                    Map map = new HashMap();
                    map.put("group_code", projectScheduleds.get(0).getGroupCode());
                    groupMap.add(map);
                }
                if (groupMap != null) {
                    for (int i = 0; i < groupMap.size(); i++) {
                        ScheduledInfo scheduledInfo = new ScheduledInfo();
                        if (groupMap.get(i) != null) {
                            String groupCode = "";
                            if (groupMap.get(i).get("group_code") != null)
                                groupCode = groupMap.get(i).get("group_code").toString();
                            List<Map> projectScheduledList = projectScheduledServiceI.getByProjectIdAndGroupCodeOrderByDiggingMachineId(projectId, groupCode);
                            if (projectScheduledList != null) {
                                List<ScheduledRequest> scheduledDiggingMachineList = new ArrayList<>();
                                for (int z = 0; z < projectScheduledList.size(); z++) {
                                    ScheduledRequest diggingMachine = new ScheduledRequest();
                                    diggingMachine.setGroupCode(groupCode);
                                    if (projectScheduledList.get(z).get("distance") != null)
                                        diggingMachine.setDistance(Long.parseLong(projectScheduledList.get(z).get("distance").toString()));
                                    if (projectScheduledList.get(z).get("digging_machine_code") != null)
                                        diggingMachine.setMachineCode(projectScheduledList.get(z).get("digging_machine_code").toString());
                                    if (projectScheduledList.get(z).get("digging_machine_id") != null)
                                        diggingMachine.setMachineId(Long.parseLong(projectScheduledList.get(z).get("digging_machine_id").toString()));
                                    if (projectScheduledList.get(z).get("material_id") != null)
                                        diggingMachine.setMaterialId(Long.parseLong(projectScheduledList.get(z).get("material_id").toString()));
                                    if (projectScheduledList.get(z).get("materia_name") != null)
                                        diggingMachine.setMaterialName(projectScheduledList.get(z).get("materia_name").toString());
                                    if (projectScheduledList.get(z).get("pricing_type") != null) {
                                        Integer value = Integer.valueOf(projectScheduledList.get(z).get("pricing_type").toString());
                                        PricingTypeEnums pricingTypeEnums = PricingTypeEnums.convert(value);
                                        diggingMachine.setPricingType(pricingTypeEnums);
                                    }
                                    if (projectScheduledList.get(z).get("digging_machine_brand_id") != null)
                                        diggingMachine.setDiggingMachineBrandId(Long.parseLong(projectScheduledList.get(z).get("digging_machine_brand_id").toString()));
                                    if (projectScheduledList.get(z).get("manager_id") != null) {
                                        String mId = projectScheduledList.get(z).get("manager_id").toString();
                                        String[] mids = mId.split(SmartminingConstant.COMMA);
                                        Long[] midsLong = new Long[mids.length];
                                        for (int j = 0; j < mids.length; j++) {
                                            if (StringUtils.isNotEmpty(mids[j])) {
                                                Long mid = Long.parseLong(mids[j]);
                                                midsLong[j] = mid;
                                            }
                                        }
                                        diggingMachine.setManagerId(midsLong);
                                    }
                                    if (projectScheduledList.get(z).get("manager_name") != null) {
                                        String mName = projectScheduledList.get(z).get("manager_name").toString();
                                        String[] mNames = mName.split(SmartminingConstant.COMMA);
                                        diggingMachine.setManagerName(mNames);
                                    }
                                    if (projectScheduledList.get(z).get("employee_id") != null) {
                                        String mId = projectScheduledList.get(z).get("employee_id").toString();//projectScheduledList.get(z).get("employee_id").toString();
                                        String[] empIds = mId.split(SmartminingConstant.COMMA);
                                        Long[] empIdsLong = new Long[empIds.length];
                                        for (int j = 0; j < empIds.length; j++) {
                                            if (StringUtils.isNotEmpty(empIds[j])) {
                                                Long mid = Long.parseLong(empIds[j]);
                                                empIdsLong[j] = mid;
                                            }
                                        }
                                        diggingMachine.setEmployeeId(empIdsLong);
                                    }
                                    if (projectScheduledList.get(z).get("employee_name") != null) {
                                        String eName = projectScheduledList.get(z).get("employee_name").toString(); //projectScheduledList.get(z).get("employee_name").toString();
                                        String[] eNames = eName.split(SmartminingConstant.COMMA);
                                        diggingMachine.setEmployeeName(eNames);
                                    }
                                    scheduledDiggingMachineList.add(diggingMachine);
                                    List<Map> carsInfoList = projectScheduledServiceI.getByProjectIdAndDiggingMachineId(projectId, diggingMachine.getMachineId());
                                    Long[] carsArray = new Long[carsInfoList.size()];
                                    String[] carsStrArray = new String[carsInfoList.size()];
                                    for (int j = 0; j < carsInfoList.size(); j++) {
                                        if (carsInfoList.get(j).get("car_id") != null)
                                            carsArray[j] = Long.parseLong(carsInfoList.get(j).get("car_id").toString());
                                        if (carsInfoList.get(j).get("car_code") != null)
                                            carsStrArray[j] = carsInfoList.get(j).get("car_code").toString();
                                    }
                                    diggingMachine.setCarsArray(carsArray);
                                    diggingMachine.setCarsStrArray(carsStrArray);
                                }
                                scheduledInfo.setScheduledDiggingMachineList(scheduledDiggingMachineList);
                                scheduledInfoList.add(scheduledInfo);
                            }
                        }
                    }
                }
            } else {
                List<ProjectScheduled> projectScheduledList = new ArrayList<>();
                if (StringUtils.isEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    groupMap = projectScheduledServiceI.getByProjectIdPage(projectId, cur, page);
                    countMap = projectScheduledServiceI.getByProjectIdCount(projectId);
                } else if (StringUtils.isNotEmpty(machineCode) && StringUtils.isEmpty(carCode)) {
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                } else if (StringUtils.isEmpty(machineCode) && StringUtils.isNotEmpty(carCode)) {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndCarId(projectId, projectCar.getId());
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, projectScheduledList.get(0).getDiggingMachineCode());
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                } else {
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndCarIdAndDiggingMachineId(projectId, projectCar.getId(), projectDiggingMachine.getId());
                    projectDiggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, projectScheduledList.get(0).getDiggingMachineCode());
                    if (projectDiggingMachine != null) {
                        Map map = new HashMap();
                        map.put("digging_machine_id", projectDiggingMachine.getId());
                        map.put("digging_machine_code", projectDiggingMachine.getCode());
                        groupMap.add(map);
                        countMap.add(map);
                    }
                }
                if (groupMap.size() > 0) {
                    for (int i = 0; i < groupMap.size(); i++) {
                        ScheduledInfo scheduledInfo = new ScheduledInfo();
                        Long machineId = Long.parseLong(groupMap.get(i).get("digging_machine_id").toString());
                        if(flag) {
                            projectScheduledList = projectScheduledServiceI.getAllByProjectIdAndDiggingMachineId(projectId, machineId);
                        }else{
                            projectScheduledList = projectScheduledServiceI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, machineId, "[^0-9]" + sysUser.getId() + "[^0-9]");
                        }
                        if (projectScheduledList.size() > 0) {
                            String groupCode = projectScheduledList.get(0).getGroupCode();
                            List<ScheduledRequest> scheduledDiggingMachineList = new ArrayList<>();
                            ScheduledRequest scheduledRequest = new ScheduledRequest();
                            scheduledRequest.setGroupCode(groupCode);
                            scheduledRequest.setMachineId(projectScheduledList.get(0).getDiggingMachineId());
                            scheduledRequest.setMachineCode(projectScheduledList.get(0).getDiggingMachineCode());
                            scheduledRequest.setPricingType(projectScheduledList.get(0).getPricingType());
                            scheduledRequest.setMaterialName(projectScheduledList.get(0).getMateriaName());
                            scheduledRequest.setMaterialId(projectScheduledList.get(0).getMaterialId());
                            scheduledRequest.setDiggingMachineBrandId(projectScheduledList.get(0).getDiggingMachineBrandId());
                            scheduledRequest.setDistance(projectScheduledList.get(0).getDistance());

                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getManagerId())) {
                                String mId = projectScheduledList.get(0).getManagerId();
                                String[] mids = mId.split(SmartminingConstant.COMMA);
                                Long[] midsLong = new Long[mids.length];
                                for (int j = 0; j < mids.length; j++) {
                                    Long mid = Long.parseLong(mids[j]);
                                    midsLong[j] = mid;
                                }
                                scheduledRequest.setManagerId(midsLong);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getManagerName())) {
                                String mName = projectScheduledList.get(0).getManagerName();
                                String[] mNames = mName.split(SmartminingConstant.COMMA);
                                scheduledRequest.setManagerName(mNames);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getEmployeeId())) {
                                String mId = projectScheduledList.get(0).getEmployeeId();
                                String[] empIds = mId.split(SmartminingConstant.COMMA);
                                Long[] empIdsLong = new Long[empIds.length];
                                for (int j = 0; j < empIds.length; j++) {
                                    Long mid = Long.parseLong(empIds[j]);
                                    empIdsLong[j] = mid;
                                }
                                scheduledRequest.setEmployeeId(empIdsLong);
                            }
                            if (StringUtils.isNotEmpty(projectScheduledList.get(0).getEmployeeName())) {
                                String eName = projectScheduledList.get(0).getEmployeeName();
                                String[] eNames = eName.split(SmartminingConstant.COMMA);
                                scheduledRequest.setEmployeeName(eNames);
                            }
                            Long[] array = new Long[projectScheduledList.size()];
                            String[] arrayStr = new String[projectScheduledList.size()];
                            int j = 0;
                            for (ProjectScheduled scheduled : projectScheduledList) {
                                array[j] = scheduled.getCarId();
                                arrayStr[j] = scheduled.getCarCode();
                                j++;
                            }
                            scheduledRequest.setCarsArray(array);
                            scheduledRequest.setCarsStrArray(arrayStr);
                            scheduledDiggingMachineList.add(scheduledRequest);

                            scheduledInfo.setScheduledDiggingMachineList(scheduledDiggingMachineList);
                            scheduledInfoList.add(scheduledInfo);
                        }
                    }
                }
            }
            for (int i = 0; i < countMap.size(); i++) {
                if (countMap.get(i) != null)
                    total++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("list", scheduledInfoList);
            return Result.ok(result);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/save", produces = "application/json")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_SAVE)
    public Object save(@RequestBody List<ProjectScheduled> projectScheduledList, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            for (ProjectScheduled projectScheduled : projectScheduledList) {
                projectScheduled.setProjectId(projectId);
                projectScheduledServiceI.save(projectScheduled);
                /*ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(projectScheduled.getDiggingMachineId());*/
            }
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_DELETE)
    public Result delete(@RequestBody List<Long> machineIdList, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            if (machineIdList != null) {
                DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
                String cmdInd = "schedule";
                Long pktID = count;
                for (Long machineId : machineIdList) {
                    List<ProjectScheduled> scheduledList = projectScheduledServiceI.getByProjectIdAndDiggingMachineIdOrderById(projectId, machineId);
                    for (ProjectScheduled scheduled : scheduledList) {
                        ProjectDevice projectDevice = projectDeviceServiceI.getAllByProjectIdAndCodeAndDeviceType(projectId, scheduled.getCarCode(), 1);
                        if (projectDevice != null) {
                            String replytopic = "smartmining/slagcar/cloud/" + projectDevice.getUid() + "/request";
                            handler.handleMessageScheduleByCar(cmdInd, replytopic, pktID, projectId, scheduled.getCarId(), projectDevice.getUid(), "云端主动请求");
                            count++;
                        }
                    }
                    projectScheduledServiceI.deleteByDiggingMachineIdAndProjectId(machineId, projectId);
                    ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(machineId);
                    if (diggingMachine != null) {
                        Long excavatorID = machineId;
                        String replytopic = "smartmining/excavator/cloud/" + diggingMachine.getUid() + "/request";
                        handler.handleMessageSchedule(cmdInd, replytopic, pktID, projectId, excavatorID, diggingMachine.getUid(), "云端主动请求");
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }
}
