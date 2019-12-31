package com.seater.smartmining.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.ApplyStatus;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResult;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.Sex;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 对讲机申请审核
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:27
 */
@RestController
@RequestMapping("/api/interPhoneApply")
public class InterPhoneApplyController {

    @Autowired
    private InterPhoneApplyServiceI interPhoneApplyServiceI;

    @Autowired
    private ProjectCarServiceI projectCarServiceI;

    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @Autowired
    private SysUserProjectRoleServiceI userProjectRoleServiceI;

    @Autowired
    private ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    private InterPhoneUtil interPhoneUtil;

    @Autowired
    private ProjectServiceI projectServiceI;

    @PostMapping("/account")
    public Object account(HttpServletRequest request) {
        Long projectId = ProjectUtils.getProjectId(request);
        SysUser user = CommonUtil.getCurrentUser();
        List<SysUserProjectRole> roleList = userProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(user.getId(), projectId);
        for (SysUserProjectRole role : roleList) {
            if (!StringUtils.isEmpty(role.getInterPhoneAccount())) {
                return Result.ok(role);
            }
        }
        return Result.ok(null);
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String name, String code, Long projectId, Sex sex, Boolean isAll, UserObjectType userObjectType, String mobile, String ownerName, ApplyStatus applyStatus, HttpServletRequest request) {
        try {
            if (isAll != null && isAll)
                return interPhoneApplyServiceI.getAll();

            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Long projectId1 = ProjectUtils.getProjectId(request);
            Specification<InterPhoneApply> spec = new Specification<InterPhoneApply>() {

                @Override
                public Predicate toPredicate(Root<InterPhoneApply> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();

                    if (!StringUtils.isEmpty(name)) {
                        list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                    }
                    if (!StringUtils.isEmpty(applyStatus)) {
                        list.add(cb.equal(root.get("applyStatus").as(ApplyStatus.class), applyStatus));
                    }
                    if (!StringUtils.isEmpty(ownerName)) {
                        list.add(cb.like(root.get("ownerName").as(String.class), "%" + ownerName + "%"));
                    }
                    if (!StringUtils.isEmpty(code)) {
                        list.add(cb.like(root.get("code").as(String.class), "%" + code + "%"));
                    }
                    if (!StringUtils.isEmpty(mobile)) {
                        list.add(cb.like(root.get("mobile").as(String.class), "%" + mobile + "%"));
                    }
                    if (!StringUtils.isEmpty(userObjectType)) {
                        list.add(cb.equal(root.get("userObjectType").as(UserObjectType.class), userObjectType));
                    }
                    if (sex != null) {
                        list.add(cb.equal(root.get("sex").as(Sex.class), sex));
                    }
                    if (!StringUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    }
                    if (!StringUtils.isEmpty(projectId1)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId1));
                    }
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return interPhoneApplyServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 申请对讲机账号
     *
     * @param interPhoneApply
     * @return
     */
    @PostMapping("/save")
    @Transactional
    public Object save(InterPhoneApply interPhoneApply) {

        try {
            //判断是否已存在该相同申请
            if (isExist(interPhoneApply)) {
                return Result.error("已存在相同申请");
            }
            if (StringUtils.isEmpty(interPhoneApply.getPassword())) {
                interPhoneApply.setPassword(Constants.INTER_PHONE_PASSWORD);
            }
            interPhoneApply.setApplyStatus(ApplyStatus.Apply);
            interPhoneApplyServiceI.save(interPhoneApply);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Transactional
    public Object update(InterPhoneApply interPhoneApply) throws IOException {
        if (StringUtils.isEmpty(interPhoneApply.getInterPhoneAccountId())) {
            return Result.error("对讲账号id为空,修改失败");
        }
        InterPhoneApply save = interPhoneApplyServiceI.get(interPhoneApply.getId());
        save.setPassword(interPhoneApply.getPassword());
        save.setName(interPhoneApply.getName());
        interPhoneApplyServiceI.save(save);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", save.getPassword());
        jsonObject.put("id", save.getInterPhoneAccountId());
        jsonObject.put("name", save.getName());
        jsonObject.put("priority", 5);//默认
        InterPhoneResult o = (InterPhoneResult) interPhoneUtil.updateTalkBackUser(jsonObject);
        return Result.ok(o);
    }

    @PostMapping("/batchSave")
    @Transactional
    public Object batchSave(@RequestBody List<InterPhoneApply> interPhoneApplyList) {
        try {
            List<InterPhoneApply> applyList = new ArrayList<>();
            for (InterPhoneApply interPhoneApply : interPhoneApplyList) {
                InterPhoneApply apply = interPhoneApplyServiceI.get(interPhoneApply.getId());
                apply.setName(interPhoneApply.getName());
                apply.setPassword(interPhoneApply.getPassword());
                apply.setAccountName(interPhoneApply.getAccountName());
                interPhoneApply.setApplyStatus(ApplyStatus.Apply);
                applyList.add(apply);
            }

            for (InterPhoneApply apply : applyList) {
                ThreadUtil.execAsync(() -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", apply.getInterPhoneAccountId());
                    jsonObject.put("name", apply.getAccountName());
                    jsonObject.put("password", apply.getPassword());
                    interPhoneUtil.updateTalkBackUser(jsonObject);
                });
                interPhoneApplyServiceI.save(apply);
            }
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 失效申请
     *
     * @param
     * @return
     */
    @PostMapping("/disable")
    @Transactional
    public Object disable(Long id) {

        try {
            InterPhoneApply interPhoneApply = interPhoneApplyServiceI.get(id);
            interPhoneApply.setApplyStatus(ApplyStatus.Disable);
            validAccount(interPhoneApply, false);
            interPhoneApplyServiceI.save(interPhoneApply);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            InterPhoneApply interPhoneApply = interPhoneApplyServiceI.get(id);
            validAccount(interPhoneApply, false);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", interPhoneApply.getInterPhoneAccountId());
            Object o;
            try {
                o = interPhoneUtil.deleteTalkBackUser(jsonObject);
            } catch (Exception e) {

            }
            interPhoneApplyServiceI.delete(id);
            return Result.ok();
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(exception.getMessage());
        }
    }

    @RequestMapping("/batchDelete")
    @Transactional
    public Object batchDelete(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                InterPhoneApply interPhoneApply = interPhoneApplyServiceI.get(id);
                validAccount(interPhoneApply, false);
                if (!StringUtils.isEmpty(interPhoneApply.getInterPhoneAccountId())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", interPhoneApply.getInterPhoneAccountId());
                    interPhoneUtil.deleteTalkBackUser(jsonObject);
                }
                interPhoneApplyServiceI.delete(id);
            }
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

    }

    /**
     * 回收/恢复对讲账号
     *
     * @param interPhoneApply 申请
     * @param isValid         生效/失效
     */
    private void validAccount(InterPhoneApply interPhoneApply, Boolean isValid) {
        try {
            if (null == interPhoneApply.getUserObjectType()) {
                return;
            }
            switch (interPhoneApply.getUserObjectType()) {
                case DiggingMachine:
                    ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(interPhoneApply.getUserObjectId());
                    if (isValid) {
                        machine.setInterPhoneAccountId(interPhoneApply.getInterPhoneAccountId());
                        machine.setInterPhoneAccount(interPhoneApply.getInterPhoneAccount());
                    } else {
                        machine.setInterPhoneAccountId("");
                        machine.setInterPhoneAccount("");
                    }
                    projectDiggingMachineServiceI.save(machine);
                    break;
                case SlagCar:
                    ProjectCar car = projectCarServiceI.get(interPhoneApply.getUserObjectId());
                    if (isValid) {
                        car.setInterPhoneAccountId(interPhoneApply.getInterPhoneAccountId());
                        car.setInterPhoneAccount(interPhoneApply.getInterPhoneAccount());
                    } else {
                        car.setInterPhoneAccountId("");
                        car.setInterPhoneAccount("");
                    }
                    projectCarServiceI.save(car);
                    break;
                case Person:
                    List<SysUserProjectRole> roleList = userProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(interPhoneApply.getUserObjectId(), interPhoneApply.getProjectId());
                    for (SysUserProjectRole role : roleList) {
                        if (isValid) {
                            role.setInterPhoneAccountId(interPhoneApply.getInterPhoneAccountId());
                            role.setInterPhoneAccount(interPhoneApply.getInterPhoneAccount());
                        } else {
                            role.setInterPhoneAccountId("");
                            role.setInterPhoneAccount("");

                        }
                        userProjectRoleServiceI.save(role);
                    }
                    break;
                case OilCar:
                    ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.get(interPhoneApply.getUserObjectId());
                    if (isValid) {
                        otherDevice.setInterPhoneAccountId(interPhoneApply.getInterPhoneAccountId());
                        otherDevice.setInterPhoneAccount(interPhoneApply.getInterPhoneAccount());
                    } else {
                        otherDevice.setInterPhoneAccountId("");
                        otherDevice.setInterPhoneAccount("");
                    }
                    projectOtherDeviceServiceI.save(otherDevice);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isExist(InterPhoneApply interPhoneApply) {
        //判断是否已存在该相同申请
        Specification<InterPhoneApply> spec = (r, q, c) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(c.equal(r.get("userObjectId").as(Long.class), interPhoneApply.getUserObjectId()));
            list.add(c.equal(r.get("userObjectType").as(UserObjectType.class), interPhoneApply.getUserObjectType()));
            list.add(c.equal(r.get("projectId").as(Long.class), interPhoneApply.getProjectId()));
            list.add(c.equal(r.get("status").as(Boolean.class), true));
            list.add(c.notEqual(r.get("applyStatus").as(ApplyStatus.class), ApplyStatus.Disable));
            list.add(c.notEqual(r.get("applyStatus").as(ApplyStatus.class), ApplyStatus.Rejected));
            return c.and(list.toArray(new Predicate[list.size()]));
        };
        List<InterPhoneApply> applyList = interPhoneApplyServiceI.queryWx(spec);
        if (applyList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 激活账号
     *
     * @param jsonObject {
     *                   "ids": [
     *                   11
     *                   ],
     *                   "creditMonths": 2
     *                   }
     * @return
     */
    @PostMapping("/active")
    public Object active(@RequestBody JSONObject jsonObject) {
        try {
            List<Long> ids = jsonObject.getJSONArray("ids").toJavaList(Long.class);
            Integer creditMonths = jsonObject.getInteger("creditMonths");
            for (Long id : ids) {
                InterPhoneApply apply = interPhoneApplyServiceI.get(id);
                if (StringUtils.isEmpty(apply.getInterPhoneAccountId())) {
                    return Result.error("当前申请不存在对讲账号id,激活失败,id:" + apply);
                }
            }
            interPhoneUtil.initInterPhoneApiInfo();
            List<Object> list = new ArrayList<>();
            for (Long id : ids) {
                InterPhoneApply apply = interPhoneApplyServiceI.get(id);

                JSONObject active = new JSONObject();
                active.put("talkbackUserId", apply.getInterPhoneAccountId());
                if (null == creditMonths || creditMonths <= 0) {
                    active.put("creditMonths", Constants.INTER_PHONE_CREDIT_MONTHS);
                }
                JSONObject renew = interPhoneUtil.batchRenew(active);
                if (renew.getInteger("code") != -1) {
                    apply.setActiveStatus(true);
                    interPhoneApplyServiceI.save(apply);
                }
                list.add(renew);
            }
            return Result.ok(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/project")
    public Object project(HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            Project project = projectServiceI.get(projectId);
            List<ProjectCar> carList = projectCarServiceI.getByProjectIdAndIsVaild(projectId, true);
            List<ProjectDiggingMachine> machineList = projectDiggingMachineServiceI.getByProjectIdAndIsVaild(projectId, true);
            for (ProjectCar car : carList) {
                Specification<InterPhoneApply> spec = (r, q, c) -> {
                    List<Predicate> list = new ArrayList<>();
                    list.add(c.equal(r.get("projectId").as(Long.class), projectId));
                    list.add(c.equal(r.get("userObjectId").as(Long.class), car.getId()));
                    list.add(c.equal(r.get("userObjectType").as(UserObjectType.class), UserObjectType.SlagCar));
                    list.add(c.notEqual(r.get("applyStatus").as(ApplyStatus.class), ApplyStatus.Disable));
                    list.add(c.notEqual(r.get("status").as(Boolean.class), false));
                    return c.and(list.toArray(new Predicate[list.size()]));
                };
                List<InterPhoneApply> applyList = interPhoneApplyServiceI.queryWx(spec);
                boolean flag = true;
                if (applyList.size() > 0) {
                    flag = false;
                }
                if (flag) {
                    InterPhoneApply apply = new InterPhoneApply();
                    apply.setPassword(Constants.INTER_PHONE_PASSWORD);
                    apply.setActiveStatus(false);
                    apply.setAccountName("渣车" + car.getCode());
                    apply.setName("渣车" + car.getCode());
                    apply.setInterPhoneAddTime(null);
                    apply.setAddTime(new Date());
                    apply.setCode(car.getCode());
                    apply.setOwnerId(car.getOwnerId());
                    apply.setOwnerName(car.getOwnerName());
                    apply.setUserObjectId(car.getId());
                    apply.setUserObjectType(UserObjectType.SlagCar);
                    apply.setProjectId(projectId);
                    apply.setProjectName(project.getName());
                    interPhoneApplyServiceI.save(apply);
                }
            }

            for (ProjectDiggingMachine machine : machineList) {
                List<InterPhoneApply> applyList = interPhoneApplyServiceI.findAllByUserObjectIdAndUserObjectType(machine.getId(), UserObjectType.DiggingMachine);
                boolean flag = true;
                if (applyList.size() > 0) {
                    flag = false;
                }
                if (flag) {
                    InterPhoneApply apply = new InterPhoneApply();
                    apply.setPassword(Constants.INTER_PHONE_PASSWORD);
                    apply.setActiveStatus(false);
                    apply.setAccountName("挖机" + machine.getCode());
                    apply.setName("挖机" + machine.getCode());
                    apply.setInterPhoneAddTime(null);
                    apply.setAddTime(new Date());
                    apply.setCode(machine.getCode());
                    apply.setOwnerId(machine.getOwnerId());
                    apply.setOwnerName(machine.getOwnerName());
                    apply.setUserObjectId(machine.getId());
                    apply.setUserObjectType(UserObjectType.DiggingMachine);
                    apply.setProjectId(projectId);
                    apply.setProjectName(project.getName());
                    interPhoneApplyServiceI.save(apply);
                }
            }

            return Result.ok(projectId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
