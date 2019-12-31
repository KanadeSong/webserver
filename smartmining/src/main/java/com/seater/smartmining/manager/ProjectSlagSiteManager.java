package com.seater.smartmining.manager;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.entity.ProjectSlagSiteModifyLog;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.service.InterPhoneGroupServiceI;
import com.seater.smartmining.service.InterPhoneMemberServiceI;
import com.seater.smartmining.service.ProjectSlagSiteModifyLogServiceI;
import com.seater.smartmining.service.ProjectSlagSiteServiceI;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResult;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Description 渣场管理
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/15 17:40
 */

@Slf4j
@Service
public class ProjectSlagSiteManager {

    @Autowired
    ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    ProjectSlagSiteModifyLogServiceI projectSlagSiteModifyLogServiceI;
    @Autowired
    InterPhoneUtil interPhoneUtil;
    @Autowired
    InterPhoneGroupServiceI interPhoneGroupServiceI;
    @Autowired
    InterPhoneMemberServiceI interPhoneMemberServiceI;
    @Autowired
    InterPhoneManager interPhoneManager;

    public void logModifyProjectSlagSite(ProjectSlagSite projectSlagSiteOld, ProjectSlagSite projectSlagSiteNew, ModifyEnum modifyEnum, Long projectId) {
        try {
            SysUser currentUser = CommonUtil.getCurrentUser();
            ProjectSlagSiteModifyLog modifyLog = new ProjectSlagSiteModifyLog();
            BeanUtils.copyProperties(projectSlagSiteNew, modifyLog);
            modifyLog.setId(null);

            //修改时用新的id
            if (ModifyEnum.MODIFY.equals(modifyEnum)) {
                modifyLog.setModifyId(projectSlagSiteNew.getId());
                modifyLog.setBeforeProjectId(projectSlagSiteNew.getProjectId());

                //为空新增
                try {
                    InterPhoneGroup group = interPhoneGroupServiceI.findBySlagSiteIdAndGroupType(projectSlagSiteNew.getId(), GroupType.SlagSite);
                    if (null == group) {
                        createTalkBackGroup(projectSlagSiteNew, projectId);
                    } else {
                        updateTalkBackGroup(projectSlagSiteNew, projectId, projectSlagSiteOld);
                    }
                } catch (Exception e) {
                    /**
                     * 返回数据  >>>>>   {"body":{"code":-20003,"msg":"该集团没有部门"},"headers":{"Date":["Fri, 30 Aug 2019 03:55:08 GMT"],"Content-Type":["application/json;charset=UTF-8"],"Transfer-Encoding":["chunked"]},"statusCode":"OK","statusCodeValue":200}
                     */
                    //java.lang.NullPointerException  很有可能是这个项目没有对应的部门
                    e.printStackTrace();
                    log.error("更新第三方对讲信息信息失败,{}", e.getMessage());
                }

            }
            //删除用旧的id
            if (ModifyEnum.DELETE.equals(modifyEnum)) {
                modifyLog.setModifyId(projectSlagSiteOld.getId());
                modifyLog.setBeforeProjectId(projectSlagSiteOld.getProjectId());

                try {
                    deleteTalkBackGroup(projectSlagSiteOld, projectId);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("更新第三方对讲信息信息失败,{}", e.getMessage());
                }

            }
            modifyLog.setBeforeDescription(projectSlagSiteOld.getDescription());
            modifyLog.setBeforeDeviceCode(projectSlagSiteOld.getDeviceCode());
            modifyLog.setBeforeDeviceId(projectSlagSiteOld.getDeviceId());
            modifyLog.setBeforeDeviceUid(projectSlagSiteOld.getDeviceUid());
            modifyLog.setBeforeDistance(projectSlagSiteOld.getDistance());
            modifyLog.setBeforeManagerId(projectSlagSiteOld.getManagerId());
            modifyLog.setBeforeManagerName(projectSlagSiteOld.getManagerName());
            modifyLog.setBeforeMaterialId(projectSlagSiteOld.getMaterialId());
            modifyLog.setBeforeMaterialName(projectSlagSiteOld.getMaterialName());
            modifyLog.setBeforeName(projectSlagSiteOld.getName());

            modifyLog.setBeforeSlagSite(projectSlagSiteOld.getSlagSite());
            modifyLog.setBeforeSwipeIntervent(projectSlagSiteOld.getSwipeIntervent());

            modifyLog.setCreateTime(new Date());
            modifyLog.setUserId(currentUser.getId());
            modifyLog.setUserName(currentUser.getName());
            modifyLog.setModifyEnum(modifyEnum);
            ProjectSlagSiteModifyLog save = projectSlagSiteModifyLogServiceI.save(modifyLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建渣场的对讲组
     *
     * @param slagSite  渣场
     * @param projectId 项目id
     * @return 结果
     */
    private Object createTalkBackGroup(ProjectSlagSite slagSite, Long projectId) {
        try {
            InterPhoneGroup group = new InterPhoneGroup();
            group.setGroupCode(UUID.randomUUID().toString());
            group.setProjectId(projectId);
            group.setSlagSiteId(slagSite.getId());
            //渣场组
            group.setGroupType(GroupType.SlagSite);
            if (!StringUtils.isEmpty(slagSite.getName())) {
                group.setName(slagSite.getName() + "-" + GroupType.SlagSite.getValue());
            } else {
                group.setName(ProjectUtils.groupName(GroupType.SlagSite, projectId));
            }
            if (!StringUtils.isEmpty(slagSite.getDescription())) {
                group.setDescription(slagSite.getDescription());
            } else {
                group.setDescription(ProjectUtils.groupName(GroupType.SlagSite, projectId));
            }

            //先保存本地信息
            InterPhoneGroup save = interPhoneGroupServiceI.save(group);
            ThreadUtil.execAsync(() -> interPhoneManager.saveAndSyn(save.getId(), false));
            //成员
            List<Object> managerList = ProjectUtils.sortManager(slagSite.getManagerId());
            for (Object manager : managerList) {
                Long userId = Long.parseLong((manager + "").replace("\"", ""));
                JSONObject account = interPhoneManager.getInterPhoneAccountAndIdByUserObject(userId, UserObjectType.Person, projectId);
                InterPhoneMember member = new InterPhoneMember();
                member.setUserObjectType(UserObjectType.Person);
                member.setUserObjectId(userId);
                member.setProjectId(projectId);
                member.setInterPhoneGroupId(save.getId());
                member.setInterPhoneAccount(account.getString("account"));
                member.setInterPhoneAccountId(account.getString("accountId"));
                member.setUserObjectName(account.getString("userObjectName"));
                interPhoneMemberServiceI.save(member);
            }

            Object o = interPhoneManager.dispatch(save.getId());
            log.debug(JSONObject.toJSONString(o));
            return o;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 更新对讲组信息
     *
     * @param slagSite
     * @param projectId
     * @return
     */
    private Object updateTalkBackGroup(ProjectSlagSite slagSite, Long projectId, ProjectSlagSite slagSiteOld) {
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.findBySlagSiteIdAndGroupType(slagSite.getId(), GroupType.SlagSite);
            if (!StringUtils.isEmpty(slagSite.getName())) {
                group.setName(slagSite.getName() + "-" + GroupType.SlagSite.getValue());

            } else {
                group.setName(ProjectUtils.groupName(GroupType.SlagSite, projectId));
            }
            if (!StringUtils.isEmpty(slagSite.getDescription())) {
                group.setDescription(slagSite.getDescription());
            } else {
                group.setDescription(ProjectUtils.groupName(GroupType.SlagSite, projectId));
            }

            List<Long> objectListNew = ProjectUtils.sortManagerToNumber(slagSite.getManagerId());
            List<Long> objectListOld = ProjectUtils.sortManagerToNumber(slagSiteOld.getManagerId());

            Set<Long> objectListFinal = new HashSet<>(objectListNew);
            //交集
            objectListNew.retainAll(objectListOld);
            //去重并集
            objectListFinal.addAll(objectListNew);
            //成员修改
            List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByInterPhoneGroupId(group.getId());

            //清除旧成员
            for (Long o : objectListOld) {
                for (InterPhoneMember member : memberList) {
                    if (member.getUserObjectType().equals(UserObjectType.Person) &&
                            member.getUserObjectId().equals(o)) {
                        interPhoneMemberServiceI.delete(member.getId());
                    }
                }
            }

            //加入新成员
            for (Long o : objectListFinal) {
                JSONObject account = interPhoneManager.getInterPhoneAccountAndIdByUserObject(o, UserObjectType.Person, projectId);
                InterPhoneMember member = new InterPhoneMember();
                member.setInterPhoneGroupId(group.getId());
                member.setProjectId(projectId);
                member.setUserObjectId(o);
                member.setUserObjectType(UserObjectType.Person);
//                member.setGroupIdThird(group.getGroupIdThird());
                member.setInterPhoneAccount(account.getString("account"));
                member.setInterPhoneAccountId(account.getString("accountId"));
                member.setUserObjectName(account.getString("userObjectName"));
                interPhoneMemberServiceI.save(member);
            }

            //更新第三方群组信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", group.getGroupIdThird());
            jsonObject.put("name", group.getName());
            jsonObject.put("groupCode", group.getGroupCode());
            interPhoneGroupServiceI.save(group);
            Object o = interPhoneUtil.updateTalkBackGroup(jsonObject);
            interPhoneManager.dispatch(group.getId());
            return o;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 删除对讲组
     *
     * @param slagSit   渣场
     * @param projectId 项目id
     * @return 结果
     */
    private Object deleteTalkBackGroup(ProjectSlagSite slagSit, Long projectId) {
        //删除对讲组
        try {
            InterPhoneGroup group = interPhoneGroupServiceI.findBySlagSiteIdAndGroupType(slagSit.getId(), GroupType.SlagSite);
            //移除成员
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("talkBackGroupId", group.getGroupIdThird());
            interPhoneUtil.removeAllTalkBackGroup(jsonObject);
            //删除组
            Object o = interPhoneUtil.deleteTalkBackGroup(jsonObject);
            interPhoneManager.deleteAndSyn(group.getId());
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("渣场修改,删除渣场对讲组(第三方)失败,{}", e.getMessage());
            return null;
        }
    }
}
