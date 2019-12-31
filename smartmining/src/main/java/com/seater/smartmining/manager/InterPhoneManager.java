package com.seater.smartmining.manager;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.mqtt.domain.LoginReply;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResult;
import com.seater.smartmining.utils.interPhone.InterPhoneResultArr;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 对讲机管理
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/29 11:27
 */
@Slf4j
@Service
public class InterPhoneManager {

    @Autowired
    ProjectCarServiceI projectCarServiceI;

    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    InterPhoneUtil interPhoneUtil;

    @Autowired
    InterPhoneApplyServiceI interPhoneApplyServiceI;

    @Autowired
    InterPhoneGroupServiceI interPhoneGroupServiceI;

    @Autowired
    InterPhoneMemberServiceI interPhoneMemberServiceI;

    @Autowired
    ProjectScheduleServiceI projectScheduleServiceI;

    @Autowired
    ScheduleMachineServiceI scheduleMachineServiceI;

    @Autowired
    ScheduleCarServiceI scheduleCarServiceI;

    @Autowired
    ProjectSlagSiteServiceI projectSlagSiteServiceI;

    @Autowired
    InterPhoneScheduleServiceI interPhoneScheduleServiceI;

    /**
     * 直接新增对讲组和同步对讲组(如果有成员可以选择调度成员)
     *
     * @param interPhoneGroupId 对讲组id
     * @param isSyn             是否执行调度
     * @return 结果
     */
    public synchronized InterPhoneGroup saveAndSyn(Long interPhoneGroupId, Boolean isSyn) {
        try {
            interPhoneUtil.initInterPhoneApiInfo();
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByInterPhoneGroupId(interPhoneGroupId);
            Long projectId = group.getProjectId();
            JSONObject jsonObject = new JSONObject();
            //组名称
            String groupName = ProjectUtils.groupName(group.getGroupType(), projectId);

            if (StringUtils.isEmpty(group.getName())) {
                group.setName(groupName);
                jsonObject.put("name", groupName);
            } else {
                jsonObject.put("name", group.getName());
            }

            if (StringUtils.isEmpty(group.getDescription())) {
                group.setDescription(groupName);
                jsonObject.put("description", groupName);
            } else {
                jsonObject.put("description", group.getDescription());
            }
            //组名称保存
            group = interPhoneGroupServiceI.save(group);

            //同步建组
            //找出对应项目的部门
            String deptId = interPhoneUtil.departmentIdFindByProjectId(projectId);
            //我方组的唯一id
            jsonObject.put("departmentId", deptId);
            jsonObject.put("groupCode", group.getGroupCode());
            InterPhoneResult talkBackGroup = (InterPhoneResult) interPhoneUtil.createTalkBackGroup(jsonObject);
            String groupIdThird = talkBackGroup.getData().getString("id");
            //回写第三方群组id
            group.setGroupIdThird(groupIdThird);
            group = interPhoneGroupServiceI.save(group);

            for (InterPhoneMember member : memberList) {
                //获取对应设备或者人的对讲机账号
                JSONObject accountObj = getInterPhoneAccountAndIdByUserObject(member.getUserObjectId(), member.getUserObjectType(), projectId);
                //回写账号id
                member.setInterPhoneAccountId(accountObj.getString("accountId"));
                //回写账号
                member.setInterPhoneAccount(accountObj.getString("account"));
                member.setUserObjectName(accountObj.getString("userObjectName"));
                interPhoneMemberServiceI.save(member);
            }

            if (isSyn) {
                dispatch(group.getId());
            }
            return group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param interPhoneGroupId
     * @return
     */
    public synchronized Object updateAndSyn(Long interPhoneGroupId) {
        //删除旧组
        clearMember(interPhoneGroupId);
        //创建新组并且调度
        Object o = dispatch(interPhoneGroupId);
        return o;
    }

    /**
     * 同步删除组
     *
     * @param interPhoneGroupId 组id
     * @return 结果
     */
    public synchronized Object deleteAndSyn(Long interPhoneGroupId) {
        try {
            interPhoneUtil.initInterPhoneApiInfo();
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            interPhoneMemberServiceI.deleteAllByInterPhoneGroupId(interPhoneGroupId);

            //同步删除对讲组
            Runnable runnable = () -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("talkBackGroupId", group.getGroupIdThird());
                jsonObject.put("id", group.getGroupIdThird());
                interPhoneUtil.removeAllTalkBackGroup(jsonObject);
                interPhoneUtil.deleteTalkBackGroup(jsonObject);
                try {
                    interPhoneGroupServiceI.delete(interPhoneGroupId);
                } catch (Exception e) {
                }
            };
            ThreadUtil.execAsync(runnable);

            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 失效/生效 组
     *
     * @param interPhoneGroupId 组id
     */
    public synchronized void validAndSyn(Long interPhoneGroupId, Boolean isValid) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            if (isValid) {
                //生效
                dispatch(interPhoneGroupId);
            } else {
                //失效
                clearMember(interPhoneGroupId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("失效/生效组失败...,组id:{},是否生效组:{}", interPhoneGroupId, isValid);
        }

    }

    /**
     * 清空组成员
     *
     * @param interPhoneGroupId
     */
    public synchronized void clearMember(Long interPhoneGroupId) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
//            interPhoneMemberServiceI.deleteAllByInterPhoneGroupId(interPhoneGroupId);
            Runnable runnable = () -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("talkBackGroupId", group.getGroupIdThird());
                interPhoneUtil.removeAllTalkBackGroup(jsonObject);
            };
            ThreadUtil.execAsync(runnable);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清空组成员失败:{}", System.currentTimeMillis());
        }
    }

    /**
     * 移除成员
     *
     * @param interPhoneGroupId 组id
     * @param memberList        成员列表
     */
    public synchronized void removeMember(Long interPhoneGroupId, List<InterPhoneMember> memberList) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            Runnable runnable = () -> {
                JSONObject jsonObject = interPhoneMemberToParam(memberList);
                jsonObject.put("talkBackGroupId", group.getGroupIdThird());
                interPhoneUtil.delTalkBackUser(jsonObject);
            };
            ThreadUtil.execAsync(runnable);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("移除组成员失败:{}", System.currentTimeMillis());
        }
    }

    /**
     * 更新组的信息(对讲组信息,不包括成员)
     *
     * @param interPhoneGroupId 组id
     * @return 结果
     */
    public Object updateInfo(Long interPhoneGroupId) {

        try {
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", group.getGroupIdThird());
            jsonObject.put("name", group.getName());
            jsonObject.put("groupCode", group.getDescription());
            Object o = interPhoneUtil.updateTalkBackGroup(jsonObject);
            return o;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行组的调度
     *
     * @param interPhoneGroupId 组id
     * @return 结果
     */
    public synchronized Object dispatch(Long interPhoneGroupId) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.get(interPhoneGroupId);
            if (StringUtils.isEmpty(group.getGroupIdThird())) {
                log.warn("当前群组没有第三方对讲组id,不进行第三方对讲调度操作!!!,对讲组:{}", JSONObject.toJSONString(group));
                return null;
            }
            List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByInterPhoneGroupId(interPhoneGroupId);
            JSONObject param = interPhoneMemberToParam(memberList);
            param.put("talkBackGroupId", group.getGroupIdThird());
            param.put("id", group.getGroupIdThird());
            param.put("name", group.getName());
            JSONArray ids = param.getJSONArray("ids");
            InterPhoneResultArr arr = null;
            if (ids.size() > 0) {
                interPhoneUtil.updateTalkBackGroup(param);
                arr = interPhoneUtil.dispatchTalkBackUser(param);
                if (arr != null && arr.getData().size() > 0) {
                    //调度完成标记回写
                    group.setIsSyn(true);
                    interPhoneGroupServiceI.save(group);
                }
            }
            if (memberList.size() == 0 || ids.size() == 0) {
                group.setIsSyn(false);
                interPhoneGroupServiceI.save(group);
                log.warn("该组没有对讲机账号成员, 对讲组id:{}", interPhoneGroupId);
                ThreadUtil.execAsync(() -> clearMember(interPhoneGroupId));
            }
            return arr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 修改排班时根据当前排班情况重新计算对讲组
     *
     * @param scheduleId 排班id
     * @return 结果
     */
    public synchronized InterPhoneGroup dispatchSchedule(Long scheduleId) {
        try {
            ProjectSchedule schedule = projectScheduleServiceI.get(scheduleId);
            Long projectId = schedule.getProjectId();

            //重新计算排班组   ===>    对讲组*******************
            //删除关系
            interPhoneScheduleServiceI.deleteAllByScheduleId(scheduleId);
            //删除旧成员
            interPhoneMemberServiceI.deleteAllByScheduleId(scheduleId);

            //去重后的管理员列表
            List<Map> managerIdList = projectScheduleServiceI.getAllDistinctByProjectId(projectId);

            InterPhoneGroup group = null;
            for (Map manager : managerIdList) {

                //管理员列表
                String managerIdArr = ProjectUtils.sortManagerToString(manager.get("manager_id").toString());
                //本次修改的
                String managerIdSch = ProjectUtils.sortManagerToString(schedule.getManagerId());
                //查询是否存在相同的管理员的组
                if (managerIdArr.equals(managerIdSch)) {
                    //管理员列表和组类型查询是否已存在对讲组
                    group = interPhoneGroupServiceI.findByGroupCodeAndGroupTypeAndProjectId(managerIdArr, GroupType.Manage, projectId);
                    if (null == group) {
                        String managerNameArr = ProjectUtils.groupName(GroupType.Manage, projectId);
                        if (!ObjectUtils.isEmpty(manager.get("manager_name"))) {
                            managerNameArr = manager.get("manager_name").
                                    toString().
                                    replace("[", "").
                                    replace("]", "").
                                    replace("\\", "").
                                    replace(" ", "").
                                    replace("\"", "").
                                    trim();
                        }
                        group = new InterPhoneGroup();
                        group.setGroupType(GroupType.Manage);
                        group.setProjectId(projectId);
                        group.setName(managerNameArr);
                        //充当唯一标识
                        group.setGroupCode(managerIdArr);
                        group = interPhoneGroupServiceI.save(group);

                        //第三方同步
                        group = saveAndSyn(group.getId(), false);
                    }
                    //写入关系
                    InterPhoneSchedule interPhoneSchedule = new InterPhoneSchedule();
                    interPhoneSchedule.setProjectId(projectId);
                    interPhoneSchedule.setScheduleId(schedule.getId());
                    interPhoneSchedule.setInterPhoneGroupId(group.getId());
                    interPhoneSchedule.setGroupCode(schedule.getGroupCode());
                    interPhoneScheduleServiceI.save(interPhoneSchedule);
                }
            }
            if (null == group) {
                log.error("计算排班组    ===>    对讲组失败...排班组id:{}", scheduleId);
                return null;
            }

            //重新计算排班组   ===>    对讲组*******************

            //排班对应的对讲组
            /*InterPhoneGroup group = interPhoneGroupServiceI.findByScheduleId(scheduleId);

            if (null == group) {
                log.warn("不存在该排班组对应的对讲组,尝试重新计算,排班组id:{}", scheduleId);
                calScheduleGroup(projectId);
                group = interPhoneGroupServiceI.findByScheduleId(scheduleId);
                if (null == group) {
                    log.warn("重新计算失败,放弃计算该排班组,排班组id:{}", scheduleId);
                    return null;
                }
            }*/
            //更新
            String groupName = ProjectUtils.managerName(schedule.getManagerName());
            if (StringUtils.isEmpty(groupName)) {
                groupName = ProjectUtils.groupName(group.getGroupType(), projectId);
            }
            group.setName(groupName);
            interPhoneGroupServiceI.save(group);

            List<ScheduleCar> carList = scheduleCarServiceI.getAllByProjectIdAndIsVaildAndInSchedule(schedule.getProjectId());
            carList = carList.stream().filter(s -> s.getGroupCode().equals(schedule.getGroupCode())).collect(Collectors.toList());

            List<ScheduleMachine> machineList = scheduleMachineServiceI.getAllByProjectIdAndIsVaildAndInSchedule(schedule.getProjectId());
            machineList = machineList.stream().filter(s -> s.getGroupCode().equals(schedule.getGroupCode())).collect(Collectors.toList());

            //新成员(渣车)
            for (ScheduleCar car : carList) {
                if (car.getGroupCode().equals(schedule.getGroupCode())) {
                    InterPhoneMember member = new InterPhoneMember();
                    member.setScheduleId(scheduleId);
                    member.setProjectId(projectId);
//                    member.setGroupIdThird(group.getGroupIdThird());
                    member.setUserObjectType(UserObjectType.SlagCar);
                    member.setUserObjectId(car.getCarId());
                    member.setInterPhoneGroupId(group.getId());
                    JSONObject account = getInterPhoneAccountAndIdByUserObject(car.getCarId(), UserObjectType.SlagCar, projectId);
                    member.setInterPhoneAccount(account.getString("account"));
                    member.setInterPhoneAccountId(account.getString("accountId"));
                    member.setUserObjectName(account.getString("userObjectName"));
                    interPhoneMemberServiceI.save(member);
                }
            }

            //新成员(挖机)
            for (ScheduleMachine machine : machineList) {
                if (machine.getGroupCode().equals(schedule.getGroupCode())) {
                    InterPhoneMember member = new InterPhoneMember();
                    member.setScheduleId(scheduleId);
                    member.setProjectId(projectId);
//                    member.setGroupIdThird(group.getGroupIdThird());
                    member.setUserObjectType(UserObjectType.DiggingMachine);
                    member.setUserObjectId(machine.getMachineId());
                    member.setInterPhoneGroupId(group.getId());
                    JSONObject account = getInterPhoneAccountAndIdByUserObject(machine.getMachineId(), UserObjectType.DiggingMachine, projectId);
                    member.setInterPhoneAccount(account.getString("account"));
                    member.setInterPhoneAccountId(account.getString("accountId"));
                    member.setUserObjectName(account.getString("userObjectName"));
                    interPhoneMemberServiceI.save(member);
                }
            }

            //新成员(管理员)
            List<Long> managerList = ProjectUtils.sortManagerToNumber(schedule.getManagerId());

            for (Long manager : managerList) {
                InterPhoneMember member = new InterPhoneMember();
                member.setScheduleId(scheduleId);
                member.setProjectId(projectId);
//                member.setGroupIdThird(group.getGroupIdThird());
                member.setUserObjectType(UserObjectType.Person);
                member.setUserObjectId(manager);
                member.setInterPhoneGroupId(group.getId());
                JSONObject account = getInterPhoneAccountAndIdByUserObject(manager, UserObjectType.Person, projectId);
                member.setInterPhoneAccount(account.getString("account"));
                member.setInterPhoneAccountId(account.getString("accountId"));
                member.setUserObjectName(account.getString("userObjectName"));
                interPhoneMemberServiceI.save(member);
            }
            cleanUpSchedule(projectId);
            final Long groupId = group.getId();
            ThreadUtil.execAsync(() -> dispatch(groupId));
            ThreadUtil.execAsync(() -> calConditionMemberList(projectId));
            return group;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("排班组 ===> 对讲组转换失败...,排班组id:{}", scheduleId);
            return null;
        }
    }

    /**
     * 清理无用的排班组 ===> 对讲组
     *
     * @param projectId 项目id
     */
    private synchronized void cleanUpSchedule(Long projectId) {
        try {
            //去重后的管理员列表
            List<Map> managerIdList = projectScheduleServiceI.getAllDistinctByProjectId(projectId);

            List<GroupType> groupTypes = new ArrayList<>();
            groupTypes.add(GroupType.Manage);
            List<InterPhoneGroup> groupList = interPhoneGroupServiceI.findAllByProjectIdAndGroupTypeIn(projectId, groupTypes);

            for (InterPhoneGroup group : groupList) {
                //当前排班管理员是否符合对讲组的groupCode
                boolean isConform = false;
                for (Map manager : managerIdList) {
                    String managerId = ProjectUtils.sortManagerToString(group.getGroupCode());
                    //管理员列表
                    String managerIdArr = ProjectUtils.sortManagerToString(manager.get("manager_id").toString());
                    if (managerIdArr.equals(managerId)) {
                        isConform = true;
                    }
                }
                if (!isConform) {
                    interPhoneScheduleServiceI.deleteAllByInterPhoneGroupId(group.getId());
                    deleteAndSyn(group.getId());
                }

            }


        } catch (Exception e) {

        }
    }

    /**
     * 删除排班对应的对讲组
     *
     * @param scheduleId 排班组id
     */
    public synchronized void deleteSchedule(Long scheduleId) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.findByScheduleId(scheduleId);
            if (null != group && group.getGroupType().equals(GroupType.Manage)) {
                interPhoneScheduleServiceI.deleteAllByScheduleId(scheduleId);
                interPhoneMemberServiceI.deleteAllByScheduleId(scheduleId);
                calConditionMemberList(group.getProjectId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("排班组 ===> 对讲组删除失败...,排班组id:{}", scheduleId);
        }
    }

    /**
     * 渣场组调度
     *
     * @param projectId  项目id
     * @param slagSiteId 渣场id
     * @param carIdList  渣车id列表
     * @param isEnter    是否进入渣场,true 进入 ,false 离开
     */
    public synchronized boolean dispatchSlagSite(Long projectId, Long slagSiteId, List<Long> carIdList, Boolean isEnter) {
        try {
            interPhoneUtil.initInterPhoneApiInfo();
            ProjectSlagSite slagSite = projectSlagSiteServiceI.get(slagSiteId);

            //渣场对应组,如果没有,就新建
            InterPhoneGroup group = interPhoneGroupServiceI.findBySlagSiteIdAndGroupType(slagSiteId, GroupType.SlagSite);
            if (null == group) {
                group = new InterPhoneGroup();
                group.setName(slagSite.getName() + "-渣场");
                group.setProjectId(projectId);
                group.setGroupType(GroupType.SlagSite);
                group.setDescription(slagSite.getDescription());
                group.setSlagSiteId(slagSite.getId());
                group.setUpdateTime(new Date());
                group = interPhoneGroupServiceI.save(group);
                saveAndSyn(group.getId(), false);
            }
            group.setName(slagSite.getName() + "-渣场");
            group.setProjectId(projectId);
            group.setGroupType(GroupType.SlagSite);
            group.setDescription(slagSite.getDescription());
            group.setSlagSiteId(slagSite.getId());
            group.setUpdateTime(new Date());
            interPhoneGroupServiceI.save(group);

            if (isEnter) {
                //进组
                for (Long carId : carIdList) {
                    InterPhoneMember member = new InterPhoneMember();
                    member.setUserObjectType(UserObjectType.SlagCar);
                    member.setUserObjectId(carId);
                    member.setProjectId(projectId);
                    member.setInterPhoneGroupId(group.getId());

                    JSONObject acc = getInterPhoneAccountAndIdByUserObject(carId, UserObjectType.SlagCar, projectId);
                    member.setInterPhoneAccount(acc.getString("account"));
                    member.setInterPhoneAccountId(acc.getString("accountId"));
                    member.setUserObjectName(acc.getString("userObjectName"));
                    interPhoneMemberServiceI.save(member);
                }

            } else {
                //出组
                List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByInterPhoneGroupId(group.getId());
                List<InterPhoneMember> removeList = new ArrayList<>();
                for (Long carId : carIdList) {
                    for (InterPhoneMember member : memberList) {
                        if (member.getUserObjectId().equals(carId)) {
                            removeList.add(member);
                        }
                    }
                }
                removeMember(group.getId(), removeList);
                for (InterPhoneMember member : removeList) {
                    interPhoneMemberServiceI.delete(member.getId());
                }
            }

            final Long groupId = group.getId();
            ThreadUtil.execAsync(() -> dispatch(groupId));
            ThreadUtil.execAsync(() -> calConditionMemberList(projectId));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("渣场组 ===> 对讲组调度失败,{}", System.currentTimeMillis());
            return false;
        }

    }

    /**
     * 司机端app通过mqtt登陆
     *
     * @param reply
     * @return
     */
    public LoginReply driverLoginByMqtt(LoginReply reply) {
        try {
            Specification<InterPhoneApply> spec = (r, q, c) -> {
                List<Predicate> list = new ArrayList<>();
                list.add(c.equal(r.get("projectId").as(Long.class), reply.getProjectId()));
                list.add(c.equal(r.get("userObjectId").as(Long.class), reply.getCarId()));
                list.add(c.equal(r.get("userObjectType").as(UserObjectType.class), UserObjectType.SlagCar));
                list.add(c.equal(r.get("password").as(String.class), reply.getPassword()));
                return c.and(list.toArray(new Predicate[list.size()]));
            };
            List<InterPhoneApply> applyList = interPhoneApplyServiceI.queryWx(spec);
            if (applyList.size() > 0) {
                reply.setCmdStatus(0);
            } else {
                reply.setCmdStatus(401);
            }
        } catch (Exception e) {
            reply.setCmdStatus(401);
        }
        return reply;
    }

    /**
     * 司机端app修改密码
     *
     * @param reply  消息body
     * @param newPwd 新密码
     * @return 结果body
     */
    public LoginReply driverResetPwdByMqtt(LoginReply reply, String newPwd) {
        try {
            //状态
            reply.setCmdStatus(-1);
            Specification<InterPhoneApply> spec = (r, q, c) -> {
                List<Predicate> list = new ArrayList<>();
                list.add(c.equal(r.get("projectId").as(Long.class), reply.getProjectId()));
                list.add(c.equal(r.get("userObjectId").as(Long.class), reply.getCarId()));
                list.add(c.equal(r.get("userObjectType").as(UserObjectType.class), UserObjectType.SlagCar));
                list.add(c.equal(r.get("password").as(String.class), reply.getPassword()));
                return c.and(list.toArray(new Predicate[list.size()]));
            };
            List<InterPhoneApply> applyList = interPhoneApplyServiceI.queryWx(spec);
            if (applyList.size() > 0) {
                InterPhoneApply apply = applyList.get(0);
                if (apply.getPassword().equals(reply.getPassword())) {
                    apply.setPassword(newPwd);
                    interPhoneApplyServiceI.save(apply);
                    reply.setPassword(newPwd);
                    reply.setCmdStatus(0);
                }
                //有账号id就同步第三方
                if (null != apply.getInterPhoneAccountId()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", apply.getInterPhoneAccountId());
                    jsonObject.put("name", apply.getAccountName());
                    jsonObject.put("password", apply.getPassword());
                    interPhoneUtil.updateTalkBackUser(jsonObject);
                }
            }
            return reply;
        } catch (Exception e) {
            reply.setCmdStatus(-1);
            return reply;
        }
    }

    /**
     * 计算当前项目空闲设备(只计算渣车和挖机)
     * 根据成员表和挖机渣车表来计算
     *
     * @param projectId 项目id
     * @return 计算出来的成员列表
     */
    public synchronized List<InterPhoneMember> calConditionMemberList(Long projectId) {
        try {
            long start = System.currentTimeMillis();
            //渣车空闲组
            InterPhoneGroup carGroup = interPhoneGroupServiceI.findByGroupTypeAndProjectId(GroupType.UndistributedCar, projectId);
            //挖机空闲组
            InterPhoneGroup machineGroup = interPhoneGroupServiceI.findByGroupTypeAndProjectId(GroupType.UndistributedMachine, projectId);

            //空闲组不存在时就创建
            if (null == carGroup) {
                carGroup = new InterPhoneGroup();
                carGroup.setGroupType(GroupType.UndistributedCar);
                carGroup.setProjectId(projectId);
                carGroup.setGroupCode(UUID.randomUUID().toString());
                carGroup.setName(ProjectUtils.groupName(carGroup.getGroupType(), projectId));
                carGroup = interPhoneGroupServiceI.save(carGroup);
                saveAndSyn(carGroup.getId(), false);
            } else {
                carGroup.setUpdateTime(new Date());
                carGroup = interPhoneGroupServiceI.save(carGroup);
                clearMember(carGroup.getId());
            }

            if (null == machineGroup) {
                machineGroup = new InterPhoneGroup();
                machineGroup.setGroupType(GroupType.UndistributedMachine);
                machineGroup.setProjectId(projectId);
                machineGroup.setGroupCode(UUID.randomUUID().toString());
                machineGroup.setName(ProjectUtils.groupName(machineGroup.getGroupType(), projectId));
                machineGroup = interPhoneGroupServiceI.save(machineGroup);
                saveAndSyn(machineGroup.getId(), false);
            } else {
                machineGroup.setUpdateTime(new Date());
                machineGroup = interPhoneGroupServiceI.save(machineGroup);
                clearMember(machineGroup.getId());
            }

            //删除旧的空闲成员
            interPhoneMemberServiceI.deleteAllByInterPhoneGroupId(carGroup.getId());
            interPhoneMemberServiceI.deleteAllByInterPhoneGroupId(machineGroup.getId());

            List<ProjectCar> carList = projectCarServiceI.getByProjectIdAndIsVaild(projectId, true);
            List<ProjectDiggingMachine> machineList = projectDiggingMachineServiceI.getByProjectIdAndIsVaild(projectId, true);

            //非空闲渣车成员
            List<InterPhoneMember> unFreeCarMember = new ArrayList<>(50);
            //非空闲挖机成员
            List<InterPhoneMember> unFreeMachineMember = new ArrayList<>(50);
            //项目中的所有成员
            List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByProjectId(projectId);

            for (InterPhoneMember member : memberList) {
                //不在空闲的挖机组和渣车组的成员
                if (!member.getInterPhoneGroupId().equals(carGroup.getId()) &&
                        !member.getInterPhoneGroupId().equals(machineGroup.getId())) {
                    if (null == member.getUserObjectType()) {
                        continue;
                    }
                    switch (member.getUserObjectType()) {
                        case SlagCar:
                            unFreeCarMember.add(member);
                            break;
                        case DiggingMachine:
                            unFreeMachineMember.add(member);
                            break;
                        default:
                            break;
                    }
                }
            }

            //需要重新插入的成员
            List<InterPhoneMember> memberInsertList = new ArrayList<>();

            for (ProjectCar car : carList) {
                boolean flag = true;
                for (InterPhoneMember member : unFreeCarMember) {
                    //找到该车对应的成员,就不插这个成员了
                    if (car.getId().equals(member.getUserObjectId()) &&
                            car.getVaild() &&
                            member.getUserObjectType().equals(UserObjectType.SlagCar)) {
                        flag = false;
                    }
                }
                if (flag) {
                    //成员类型是渣车
                    InterPhoneMember memberInsert = new InterPhoneMember();
                    memberInsert.setProjectId(projectId);
                    memberInsert.setUserObjectId(car.getId());
//                    memberInsert.setGroupIdThird(carGroup.getGroupIdThird());
                    memberInsert.setUserObjectType(UserObjectType.SlagCar);
                    if (null != car.getInterPhoneAccount()) {
                        memberInsert.setInterPhoneAccount(car.getInterPhoneAccount());
                    }
                    if (null != car.getInterPhoneAccountId()) {
                        memberInsert.setInterPhoneAccountId(car.getInterPhoneAccountId());
                    }
                    memberInsert.setUserObjectName(car.getCode());
                    memberInsert.setInterPhoneGroupId(carGroup.getId());
                    memberInsertList.add(memberInsert);
                }
            }

            for (ProjectDiggingMachine machine : machineList) {
                boolean flag = true;
                for (InterPhoneMember member : unFreeMachineMember) {
                    if (machine.getId().equals(member.getUserObjectId()) &&
                            machine.getVaild() &&
                            member.getUserObjectType().equals(UserObjectType.DiggingMachine)) {
                        flag = false;
                    }
                }
                if (flag) {
                    //成员类型是挖机
                    InterPhoneMember memberInsert = new InterPhoneMember();
                    memberInsert.setProjectId(projectId);
                    memberInsert.setUserObjectId(machine.getId());
//                    memberInsert.setGroupIdThird(machineGroup.getGroupIdThird());
                    memberInsert.setUserObjectType(UserObjectType.DiggingMachine);
                    if (!StringUtils.isEmpty(machine.getInterPhoneAccount())) {
                        memberInsert.setInterPhoneAccount(machine.getInterPhoneAccount());
                    }
                    if (!StringUtils.isEmpty(machine.getInterPhoneAccountId())) {
                        memberInsert.setInterPhoneAccountId(machine.getInterPhoneAccountId());
                    }
                    memberInsert.setUserObjectName(machine.getCode());
                    memberInsert.setInterPhoneGroupId(machineGroup.getId());
                    memberInsertList.add(memberInsert);
                }
            }

            interPhoneMemberServiceI.batchSave(memberInsertList);
            log.debug("计算空闲调度设备完成,当前项目:{},耗时:{}", projectId, System.currentTimeMillis() - start);

            //第三方调度
            final Long carGroupId = carGroup.getId();
            final Long machineGroupId = machineGroup.getId();
            ThreadUtil.execAsync(() -> dispatch(carGroupId));
            ThreadUtil.execAsync(() -> dispatch(machineGroupId));
            return memberInsertList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    /**
     * 通过给定使用者id和类型获取对应的对讲机账号id
     *
     * @param userObjectId   使用者主键
     * @param userObjectType 使用者类型
     * @param projectId      项目id
     * @return {
     * "account": "050100260001",
     * "accountId": "277569c8d9474c488ebbbb5a33def5ad"
     * }
     */
    public JSONObject getInterPhoneAccountAndIdByUserObject(Long userObjectId, UserObjectType userObjectType, Long projectId) {
        try {
            JSONObject jsonObject = new JSONObject();
            switch (userObjectType) {
                case SlagCar:
                    ProjectCar car = projectCarServiceI.get(userObjectId);
                    jsonObject = new JSONObject();
                    jsonObject.put("userObjectName", car.getCode());
                    if (!StringUtils.isEmpty(car.getInterPhoneAccountId())) {
                        jsonObject.put("accountId", car.getInterPhoneAccountId());
                        jsonObject.put("account", car.getInterPhoneAccount());
                    }
                    break;
                case DiggingMachine:
                    ProjectDiggingMachine machine = projectDiggingMachineServiceI.get(userObjectId);
                    jsonObject = new JSONObject();
                    jsonObject.put("userObjectName", machine.getCode());
                    if (!StringUtils.isEmpty(machine.getInterPhoneAccountId())) {
                        jsonObject.put("accountId", machine.getInterPhoneAccountId());
                        jsonObject.put("account", machine.getInterPhoneAccount());
                    }
                    break;
                case Person:
                    List<SysUserProjectRole> projectRoleList = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(userObjectId, projectId);
                    SysUser user = sysUserServiceI.get(userObjectId);
                    jsonObject = new JSONObject();
                    jsonObject.put("userObjectName", user.getName());
                    for (SysUserProjectRole projectRole : projectRoleList) {
                        if (!StringUtils.isEmpty(projectRole.getInterPhoneAccountId())) {
                            jsonObject.put("accountId", projectRole.getInterPhoneAccountId());
                            jsonObject.put("account", projectRole.getInterPhoneAccount());
                        }
                    }
                    break;
                case OilCar:
                    ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.get(userObjectId);
                    jsonObject = new JSONObject();
                    jsonObject.put("userObjectName", otherDevice.getCode());
                    if (!StringUtils.isEmpty(otherDevice.getInterPhoneAccountId())) {
                        jsonObject.put("accountId", otherDevice.getInterPhoneAccountId());
                        jsonObject.put("account", otherDevice.getInterPhoneAccount());
                    }
                    break;
                default:
                    break;
            }
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param userObjectId
     * @param userObjectType
     * @param projectId
     * @return [
     * {
     * "id":"399b6ca2d5684f4cae9c0cd5f7f7ae9f"
     * },
     * {
     * "id":"277569c8d9474c488ebbbb5a33def5ad"
     * }
     * ]
     */
    public List<JSONObject> appendInterPhoneAccountIdByUserObject(Long userObjectId, UserObjectType userObjectType, Long projectId) {

        List<JSONObject> jsonObjectList = new ArrayList<>();
        try {
            switch (userObjectType) {
                case SlagCar:
                    ProjectCar projectCar = projectCarServiceI.get(userObjectId);
                    if (null != projectCar && !StringUtils.isEmpty(projectCar.getInterPhoneAccountId())) {
                        JSONObject id = new JSONObject();
                        id.put("id", projectCar.getInterPhoneAccountId());
                        jsonObjectList.add(id);
                    }
                    break;
                case DiggingMachine:
                    ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.get(userObjectId);
                    if (null != diggingMachine && !StringUtils.isEmpty(diggingMachine.getInterPhoneAccountId())) {
                        JSONObject id = new JSONObject();
                        id.put("id", diggingMachine.getInterPhoneAccountId());
                        jsonObjectList.add(id);
                    }
                    break;
                case Person:
                    List<SysUserProjectRole> projectRoleList = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(userObjectId, projectId);
                    for (SysUserProjectRole projectRole : projectRoleList) {
                        if (null != projectRole && !StringUtils.isEmpty(projectRole.getInterPhoneAccountId())) {
                            JSONObject id = new JSONObject();
                            id.put("id", projectRole.getInterPhoneAccountId());
                            jsonObjectList.add(id);
                        }
                    }
                    break;
                case OilCar:
                    ProjectOtherDevice otherDevice = projectOtherDeviceServiceI.get(userObjectId);
                    if (null != otherDevice && !StringUtils.isEmpty(otherDevice.getInterPhoneAccountId())) {
                        JSONObject id = new JSONObject();
                        id.put("id", otherDevice.getInterPhoneAccountId());
                        jsonObjectList.add(id);
                    }
                    break;
                default:
                    break;
            }
            //去重
            Set<JSONObject> jsonObjectSet = new HashSet<>(jsonObjectList);
            return new ArrayList<>(jsonObjectSet);
        } catch (IOException e) {
            e.printStackTrace();
            //至少返回[]
            return new ArrayList<>();
        }
    }

    /**
     * @param interPhoneMemberList 对讲成员列表
     * @return {
     * "ids":[
     * {
     * "id":"399b6ca2d5684f4cae9c0cd5f7f7ae9f"
     * },
     * {
     * "id":"277569c8d9474c488ebbbb5a33def5ad"
     * }
     * ]
     * }
     */
    public JSONObject interPhoneMemberToParam(List<InterPhoneMember> interPhoneMemberList) {
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (InterPhoneMember interPhoneMember : interPhoneMemberList) {
            /*jsonObjectList = appendInterPhoneAccountIdByUserObject(interPhoneMember.getUserObjectId(), interPhoneMember.getUserObjectType(), interPhoneMember.getProjectId());*/
            if (!StringUtils.isEmpty(interPhoneMember.getInterPhoneAccountId())) {
                JSONObject id = new JSONObject();
                id.put("id", interPhoneMember.getInterPhoneAccountId());
                jsonObjectList.add(id);
            }
        }
        //去重
        Set<JSONObject> jsonObjectSet = new HashSet<>(jsonObjectList);
        jsonObject.put("ids", jsonObjectSet);
        //不推荐下面这种直接拿,建议在该方法外设置 talkBackGroupId
//        jsonObject.put("talkBackGroupId", interPhoneMemberList.get(0).getGroupIdThird());
        return jsonObject;
    }
}
