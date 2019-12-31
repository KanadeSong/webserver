package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.domain.ScheduleModelResponse;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.DeviceStartStatusEnum;
import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.quartz.QuartzConstant;
import com.seater.smartmining.quartz.QuartzManager;
import com.seater.smartmining.quartz.job.ScheduleModelStartJob;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 11:31
 */
@RestController
@RequestMapping("/api/projectScheduleModel")
public class ProjectScheduleModelController {
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectScheduleModelServiceI projectScheduleModelServiceI;
    @Autowired
    private ScheduleCarModelServiceI scheduleCarModelServiceI;
    @Autowired
    private ScheduleMachineModelServiceI scheduleMachineModelServiceI;
    @Autowired
    private ProjectProgrammeServiceI projectProgrammeServiceI;
    @Autowired
    private ProjectModifyScheduleModelLogServiceI projectModifyScheduleModelLogServiceI;
    @Autowired
    private QuartzManager quartzManager;


    @RequestMapping(value = "/save", produces = "application/json")
    @Transactional(rollbackFor = Exception.class)
    public Result save(HttpServletRequest request, @RequestBody List<ScheduleModelResponse> scheduleModelResponses) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        //获取当前用户对象
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        //排班集合
        List<ProjectScheduleModel> projectScheduleSaveList = new ArrayList<>();
        //挖机排班集合
        List<ScheduleMachineModel> scheduleMachineSaveList = new ArrayList<>();
        //渣车排班集合
        List<ScheduleCarModel> scheduleCarListSaveList = new ArrayList<>();
        //获取所有渣车集合
        List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdOrderById(projectId);
        //生成渣车索引
        Map<String, Integer> carIndexMap = new HashMap<>();
        for (int i = 0; i < projectCarList.size(); i++) {
            carIndexMap.put(projectCarList.get(i).getCode(), i);
        }
        //获取所有挖机集合
        List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
        //生成挖机索引
        Map<String, Integer> machineIndexMap = new HashMap<>();
        for (int i = 0; i < projectDiggingMachineList.size(); i++) {
            machineIndexMap.put(projectDiggingMachineList.get(i).getCode(), i);
        }
        for (ScheduleModelResponse response : scheduleModelResponses) {
            if (response.getProjectScheduleModel().getDeviceStartStatus().compareTo(DeviceStartStatusEnum.Only) == 0 || response.getProjectScheduleModel().getDeviceStartStatus().compareTo(DeviceStartStatusEnum.Check) == 0) {
                if (response.getScheduleMachineList().size() > 1)
                    throw new SmartminingProjectException("当前版本不支持混编");
            }
            //生成唯一组别编号
            String groupCode = UUID.randomUUID().toString() + projectId;
            if (response.getProjectScheduleModel() == null)
                throw new SmartminingProjectException("排班信息不能为空");
            if (response.getScheduleMachineList() == null || response.getScheduleMachineList().size() < 1)
                throw new SmartminingProjectException("排班信息中挖机数据不能为空");
            ProjectScheduleModel projectScheduleModel = response.getProjectScheduleModel();
            //获取新增或者修改的渣车集合
            List<ScheduleCarModel> newScheduleCarList = response.getScheduleCarList();
            //获取新增或者修改的挖机集合
            List<ScheduleMachineModel> newScheduleMachineList = response.getScheduleMachineList();
            //新的渣车编号集合
            List<String> newCarCodeList = new ArrayList<>();
            //新的挖机编号集合
            List<String> newMachineCodeList = new ArrayList<>();
            for (ScheduleCarModel car : newScheduleCarList) {
                Integer index = carIndexMap.get(car.getCarCode());
                if (index == null)
                    throw new SmartminingProjectException("渣车不存在，请检查渣车是否有效");
                ProjectCar projectCar = projectCarList.get(index);
                projectCar.setSeleted(true);
                projectCarServiceI.save(projectCar);
                newCarCodeList.add(car.getCarCode());
            }
            for (ScheduleMachineModel machine : newScheduleMachineList) {
                Integer index = machineIndexMap.get(machine.getMachineCode());
                if (index == null)
                    throw new SmartminingProjectException("挖机不存在，请检查挖机是否有效");
                ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineList.get(index);
                projectDiggingMachine.setSelected(true);
                projectDiggingMachineServiceI.save(projectDiggingMachine);
                newMachineCodeList.add(machine.getMachineCode());
            }
            if (StringUtils.isNotEmpty(projectScheduleModel.getGroupCode())) {
                groupCode = projectScheduleModel.getGroupCode();
                ProjectScheduleModel oldSchedule = projectScheduleModelServiceI.getAllByProjectIdAndGroupCode(projectId, projectScheduleModel.getGroupCode());
                projectScheduleModel.setCreateId(oldSchedule.getCreateId());
                projectScheduleModel.setCreateName(oldSchedule.getCreateName());
                projectScheduleModel.setCreateTime(oldSchedule.getCreateTime());
                projectScheduleModel.setModifyTime(new Date());
                if (oldSchedule == null)
                    throw new SmartminingProjectException("该分组编号对应的排班信息不存在，请检查groupCode是否正确");
                List<ScheduleCarModel> scheduleCarList = scheduleCarModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, response.getProjectScheduleModel().getGroupCode(), true);
                List<ScheduleMachineModel> scheduleMachineList = scheduleMachineModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, response.getProjectScheduleModel().getGroupCode(), true);
                ProjectModifyScheduleModelLog log = new ProjectModifyScheduleModelLog();
                log.setBeforeGroupCode(oldSchedule.getGroupCode());
                log.setBeforeManagerId(oldSchedule.getManagerId());
                log.setBeforeManagerName(oldSchedule.getManagerName());
                log.setBeforePlaceId(oldSchedule.getPlaceId());
                log.setBeforePlaceName(oldSchedule.getPlaceName());
                log.setModifyId(sysUser.getId());
                log.setModifyName(sysUser.getName());
                log.setModifyTime(new Date());
                log.setBeforeCarJson(JSON.toJSONString(scheduleCarList));
                log.setBeforeMachineJson(JSON.toJSONString(scheduleMachineList));
                log.setManagerId(response.getProjectScheduleModel().getManagerId());
                log.setManagerName(response.getProjectScheduleModel().getManagerName());
                log.setCarJson(JSON.toJSONString(response.getScheduleCarList()));
                log.setMachineJson(JSON.toJSONString(response.getScheduleMachineList()));
                log.setGroupCode(groupCode);
                log.setPlaceId(response.getProjectScheduleModel().getPlaceId());
                log.setPlaceName(response.getProjectScheduleModel().getPlaceName());
                log.setProjectId(oldSchedule.getProjectId());
                log.setModifyEnum(ModifyEnum.MODIFY);
                log.setBeforeScheduleJson(JSON.toJSONString(oldSchedule));
                log.setScheduleJson(JSON.toJSONString(response.getProjectScheduleModel()));
                projectModifyScheduleModelLogServiceI.save(log);
                List<String> oldCarCodeList = new ArrayList<>();
                List<String> oldMachineCodeList = new ArrayList<>();
                for (ScheduleCarModel car : scheduleCarList) {
                    oldCarCodeList.add(car.getCarCode());
                }
                for (ScheduleMachineModel machine : scheduleMachineList) {
                    oldMachineCodeList.add(machine.getMachineCode());
                }
                scheduleCarModelServiceI.deleteByProjectIdAndCarCodeListAndProgrammeId(projectId, newCarCodeList, projectScheduleModel.getProgrammeId());
                scheduleCarModelServiceI.deleteByProjectIdAndCarCodeListAndProgrammeId(projectId, oldCarCodeList, projectScheduleModel.getProgrammeId());
                scheduleMachineModelServiceI.deleteByProjectIdAndMachineCodeListAndProgrammeId(projectId, newMachineCodeList, projectScheduleModel.getProgrammeId());
                scheduleMachineModelServiceI.deleteByProjectIdAndMachineCodeListAndProgrammeId(projectId, oldMachineCodeList, projectScheduleModel.getProgrammeId());
            } else {
                if (newCarCodeList.size() > 0)
                    scheduleCarModelServiceI.deleteByProjectIdAndCarCodeListAndProgrammeId(projectId, newCarCodeList, projectScheduleModel.getProgrammeId());
                if (newMachineCodeList.size() > 0)
                    scheduleMachineModelServiceI.deleteByProjectIdAndMachineCodeListAndProgrammeId(projectId, newMachineCodeList, projectScheduleModel.getProgrammeId());
                projectScheduleModel.setProjectId(projectId);
                projectScheduleModel.setCreateId(sysUser.getId());
                projectScheduleModel.setCreateName(sysUser.getName());
                projectScheduleModel.setGroupCode(groupCode);
                projectScheduleModel.setCreateTime(new Date());
                projectScheduleModel.setScheduleCode(StringUtils.createCode(projectId));
            }
            projectScheduleModel.setModifyId(sysUser.getId());
            projectScheduleModel.setModifyName(sysUser.getName());
            for (ScheduleMachineModel machine : response.getScheduleMachineList()) {
                machine.setGroupCode(groupCode);
                scheduleMachineSaveList.add(machine);
            }
            for (ScheduleCarModel car : response.getScheduleCarList()) {
                car.setGroupCode(groupCode);
                scheduleCarListSaveList.add(car);
            }
            projectScheduleSaveList.add(projectScheduleModel);
        }
        scheduleCarModelServiceI.batchSave(scheduleCarListSaveList);
        scheduleMachineModelServiceI.batchSave(scheduleMachineSaveList);
        projectScheduleModelServiceI.batchSave(projectScheduleSaveList);
        ScheduleService.deleteScheduleModel();
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, @RequestParam Long programmeId, String machineCode, String carCode, PricingTypeEnums pricingType, String placeName, String userName, String sort, boolean asc) {
        Long projectId = CommonUtil.getProjectId(request);
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        //返回的集合
        List<ScheduleModelResponse> responseList = new ArrayList<>();
        List<ProjectScheduleModel> projectScheduleModelList = null;
        Long totalCount = 0L;
        if(StringUtils.isNotEmpty(machineCode)){
            projectScheduleModelList = projectScheduleModelServiceI.getAllByProjectIdAndProgrammeId(projectId, programmeId);
        }else {
            Specification<ProjectScheduleModel> specificationSchedule = new Specification<ProjectScheduleModel>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectScheduleModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(criteriaBuilder.equal(root.get("programmeId").as(Long.class), programmeId));
                    if (placeName != null)
                        list.add(criteriaBuilder.like(root.get("placeName").as(String.class), "%" + placeName + "%"));
                    if (userName != null) {
                        String params = "\"" + userName + "\"";
                        list.add(criteriaBuilder.like(root.get("managerName").as(String.class), "%" + params + "%"));
                    }
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            Page<ProjectScheduleModel> projectScheduleModelPage = projectScheduleModelServiceI.query(specificationSchedule, PageRequest.of(cur, page));
            projectScheduleModelList = projectScheduleModelPage.getContent();
            totalCount = projectScheduleModelPage.getTotalElements();
        }
        List<String> groupCodeList = new ArrayList<>();
        //生成索引
        Map<String, Integer> projectScheduleModelMapIndex = new HashMap<>();
        for (int i = 0; i < projectScheduleModelList.size(); i++) {
            groupCodeList.add(projectScheduleModelList.get(i).getGroupCode());
            projectScheduleModelMapIndex.put(projectScheduleModelList.get(i).getGroupCode() + projectScheduleModelList.get(i).getProgrammeId(), i);
        }
        if (groupCodeList.size() < 1) {
            Map map = new HashMap();
            map.put("totalCount", 0);
            map.put("content", responseList);
            return Result.ok(map);
        }
        if (StringUtils.isNotEmpty(carCode)) {
            ScheduleCarModel scheduleCarModel = scheduleCarModelServiceI.getAllByProjectIdAndGroupCodes(projectId, groupCodeList, carCode);
            Integer index = projectScheduleModelMapIndex.get(scheduleCarModel.getGroupCode() + programmeId);
            ProjectScheduleModel projectScheduleModel = index != null ? projectScheduleModelList.get(index) : null;
            List<ScheduleCarModel> scheduleCarModelList = scheduleCarModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, projectScheduleModel.getGroupCode(), true);
            List<ScheduleMachineModel> scheduleMachineModelList = scheduleMachineModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, projectScheduleModel.getGroupCode(), true);
            ScheduleModelResponse response = new ScheduleModelResponse();
            response.setProjectScheduleModel(projectScheduleModel);
            response.setScheduleMachineList(scheduleMachineModelList);
            response.setScheduleCarList(scheduleCarModelList);
            responseList.add(response);
            Map map = new HashMap();
            map.put("totalCount", totalCount);
            map.put("content", responseList);
        } else {
            Specification<ScheduleMachineModel> specificationMachine = new Specification<ScheduleMachineModel>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ScheduleMachineModel> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if (StringUtils.isNotEmpty(machineCode))
                        list.add(criteriaBuilder.like(root.get("machineCode").as(String.class), "%" + machineCode + "%"));
                    if (pricingType != null)
                        list.add(criteriaBuilder.equal(root.get("pricingType").as(Integer.class), pricingType.getValue()));
                    if (StringUtils.isNotEmpty(sort)) {
                        if (asc) {
                            query.orderBy(criteriaBuilder.asc(root.get(sort).as(String.class)));
                        } else {
                            query.orderBy(criteriaBuilder.desc(root.get(sort).as(String.class)));
                        }
                    }
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    list.add(criteriaBuilder.isTrue(root.get("isVaild")));
                    Expression<String> exp = root.<String>get("groupCode");
                    list.add(exp.in(groupCodeList)); // 往in中添加所有id 实现in 查询
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ScheduleMachineModel> machineList = scheduleMachineModelServiceI.queryByParams(specificationMachine);
            //生成排班信息索引
            Map<String, Integer> scheduleIndex = new HashMap<>();
            for (int i = 0; i < machineList.size(); i++) {
                scheduleIndex.put(machineList.get(i).getGroupCode(), i);
            }
            //totalCount = Long.parseLong(String.valueOf(groupCodeList.size()));
            if (StringUtils.isNotEmpty(machineCode))
                totalCount = 1L;
            for (Map.Entry<String, Integer> entry : scheduleIndex.entrySet()) {
                String groupCode = entry.getKey();
                Integer index = projectScheduleModelMapIndex.get(groupCode + programmeId);
                if(index != null) {
                    ScheduleModelResponse response = new ScheduleModelResponse();
                    List<ScheduleCarModel> scheduleCarList = scheduleCarModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, groupCode, true);
                    List<ScheduleMachineModel> scheduleMachineList = scheduleMachineModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, groupCode, true);
                    response.setScheduleCarList(scheduleCarList);
                    response.setScheduleMachineList(scheduleMachineList);
                    response.setProjectScheduleModel(projectScheduleModelList.get(index));
                    responseList.add(response);
                }
            }
        }
        Map map = new HashMap();
        map.put("totalCount", totalCount);
        map.put("content", responseList);
        return Result.ok(map);
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @RequiresPermissions(PermissionConstants.PROJECT_SCHEDULED_DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result delete(HttpServletRequest request, @RequestBody List<Long> ids) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            //获取当前用户对象
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            for (Long id : ids) {
                ProjectScheduleModel schedule = projectScheduleModelServiceI.get(id);
                List<ScheduleMachineModel> scheduleMachineList = scheduleMachineModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, schedule.getGroupCode(), true);
                List<ScheduleCarModel> scheduleCarList = scheduleCarModelServiceI.getAllByProjectIdAndGroupCodeAndIsVaild(projectId, schedule.getGroupCode(), true);
                ProjectModifyScheduleModelLog log = new ProjectModifyScheduleModelLog();
                log.setProjectId(projectId);
                log.setBeforePlaceId(schedule.getPlaceId());
                log.setBeforePlaceName(schedule.getPlaceName());
                log.setBeforeManagerId(schedule.getManagerId());
                log.setBeforeManagerName(schedule.getManagerName());
                log.setBeforeGroupCode(schedule.getGroupCode());
                log.setBeforeMachineJson(JSON.toJSONString(scheduleMachineList));
                log.setBeforeCarJson(JSON.toJSONString(scheduleCarList));
                log.setModifyId(sysUser.getId());
                log.setModifyName(sysUser.getName());
                log.setModifyTime(new Date());
                log.setModifyEnum(ModifyEnum.DELETE);
                projectModifyScheduleModelLogServiceI.save(log);
                scheduleMachineModelServiceI.deleteByGroupCode(schedule.getGroupCode());
                scheduleCarModelServiceI.deleteByGroupCode(schedule.getGroupCode());
                projectScheduleModelServiceI.delete(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    public static void main(String[] args) {
        List<ScheduleModelResponse> scheduleModelResponses = new ArrayList<>();
        ScheduleModelResponse response = new ScheduleModelResponse();
        ProjectScheduleModel model = new ProjectScheduleModel();
        model.setDeviceStartStatus(DeviceStartStatusEnum.DiggingMachine);
        model.setDispatchMode(ProjectDispatchMode.GroupMixture);
        model.setManagerId("[\"20194\"]");
        model.setManagerName("[\"余华安\"]");
        model.setPlaceId(10L);
        model.setPlaceName("1号平台");
        model.setSlagSiteId("[12]");
        model.setSlagSiteName("[\"lam1修改\"]");
        model.setProjectId(1L);
        model.setProgrammeId(3L);
        model.setProgrammeName("方案一");
        List<ScheduleMachineModel> scheduleMachineModels = new ArrayList<>();
        ScheduleMachineModel scheduleMachineModel = new ScheduleMachineModel();
        scheduleMachineModel.setProjectId(3L);
        scheduleMachineModel.setMachineId(80L);
        scheduleMachineModel.setMachineCode("3333");
        scheduleMachineModel.setPricingType(PricingTypeEnums.Hour);
        scheduleMachineModel.setMaterialId(0L);
        scheduleMachineModel.setProgrammeId(3L);
        scheduleMachineModel.setMaterialName("");
        scheduleMachineModel.setDistance(20000L);
        scheduleMachineModel.setIsVaild(true);
        scheduleMachineModels.add(scheduleMachineModel);
        ScheduleMachineModel scheduleMachineModel1 = new ScheduleMachineModel();
        scheduleMachineModel1.setProjectId(3L);
        scheduleMachineModel1.setMachineId(76L);
        scheduleMachineModel1.setMachineCode("1111");
        scheduleMachineModel1.setPricingType(PricingTypeEnums.Cube);
        scheduleMachineModel1.setMaterialId(0L);
        scheduleMachineModel1.setProgrammeId(3L);
        scheduleMachineModel1.setMaterialName("");
        scheduleMachineModel1.setDistance(20000L);
        scheduleMachineModel1.setIsVaild(true);
        scheduleMachineModels.add(scheduleMachineModel1);
        List<ScheduleCarModel> scheduleCarModels = new ArrayList<>();
        ScheduleCarModel scheduleCarModel = new ScheduleCarModel();
        scheduleCarModel.setProjectId(3L);
        scheduleCarModel.setCarId(1541786L);
        scheduleCarModel.setCarCode("0111");
        scheduleCarModel.setProgrammeId(3L);
        scheduleCarModels.add(scheduleCarModel);
        ScheduleCarModel scheduleCarModel1 = new ScheduleCarModel();
        scheduleCarModel1.setProjectId(3L);
        scheduleCarModel1.setCarId(1541717L);
        scheduleCarModel1.setCarCode("0122");
        scheduleCarModels.add(scheduleCarModel1);
        response.setProjectScheduleModel(model);
        response.setScheduleCarList(scheduleCarModels);
        response.setScheduleMachineList(scheduleMachineModels);
        scheduleModelResponses.add(response);
        String json = JSON.toJSONString(scheduleModelResponses);
        System.out.println(json);
    }
}
