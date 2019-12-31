package com.seater.smartmining.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.manager.InterPhoneManager;
import com.seater.smartmining.service.InterPhoneGroupServiceI;
import com.seater.smartmining.service.InterPhoneMemberServiceI;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Description
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:27
 */
@RestController
@RequestMapping("/api/interPhoneGroup")
public class InterPhoneGroupController extends BaseController {


    @Autowired
    private InterPhoneUtil interPhoneUtil;

    @Autowired
    private InterPhoneGroupServiceI interPhoneGroupServiceI;

    @Autowired
    private InterPhoneMemberServiceI interPhoneMemberServiceI;

    @Autowired
    private InterPhoneManager interPhoneManager;

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, GroupType groupType, String name, Long slagSiteId, Boolean isSyn, Long id) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);

        //组
        Specification<InterPhoneGroup> spec = new Specification<InterPhoneGroup>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<InterPhoneGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (!ObjectUtils.isEmpty(projectId)) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                }
                if (!ObjectUtils.isEmpty(slagSiteId)) {
                    list.add(cb.equal(root.get("slagSiteId").as(Long.class), slagSiteId));
                }
                if (!ObjectUtils.isEmpty(id)) {
                    list.add(cb.equal(root.get("id").as(Long.class), id));
                }
                if (!ObjectUtils.isEmpty(groupType)) {
                    list.add(cb.equal(root.get("groupType").as(GroupType.class), groupType));
                }
                if (!ObjectUtils.isEmpty(name)) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                if (!ObjectUtils.isEmpty(isSyn)) {
                    list.add(cb.equal(root.get("isSyn").as(Boolean.class), isSyn));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        Page<InterPhoneGroup> search = interPhoneGroupServiceI.query(spec, PageRequest.of(cur, page));
        List<InterPhoneGroup> content = search.getContent();
        if (null == content) {
            content = new ArrayList<>();
        }

        List<Long> ids = new ArrayList<>();
        for (InterPhoneGroup group : content) {
            ids.add(group.getId());
        }
        //成员
        List<InterPhoneMember> memberList = new ArrayList<>();
        if (ids.size() > 0) {
            Specification<InterPhoneMember> specMem = (root, query, cb) -> {
                Expression<Long> exp = root.<Long>get("interPhoneGroupId");
                return exp.in(ids);
            };


            memberList = interPhoneMemberServiceI.queryWx(specMem);
        }
        List<Object> groupList = new ArrayList<>();
        for (InterPhoneGroup group : content) {
            JSONObject groupMember = JSONObject.parseObject(JSONObject.toJSONString(group));
            List<InterPhoneMember> members = new ArrayList<>();
            for (InterPhoneMember member : memberList) {
                if (group.getId().equals(member.getInterPhoneGroupId())) {
                    members.add(member);
                }
            }
            groupMember.put("members", members);
            groupList.add(groupMember);
        }
        return Result.ok(groupList, search.getTotalElements());
    }

    @PostMapping("/saveOnHand")
    @Transactional
    public Object saveOnHand(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        //组
        InterPhoneGroup group = jsonObject.getObject("group", InterPhoneGroup.class);
        if (!allowGroup(group.getGroupType())) {
            return Result.error("不支持手动创建的组类型");
        }
        //成员
        JSONArray memberList = jsonObject.getJSONArray("memberList");

        //生成我方的对讲组唯一id(UUID)
        group.setGroupCode(UUID.randomUUID().toString());
        group.setProjectId(projectId);
        InterPhoneGroup save = interPhoneGroupServiceI.save(group);

        //插入成员
        for (Object member : memberList) {
            InterPhoneMember interPhoneMember = JSONObject.parseObject(JSONObject.toJSONString(member), InterPhoneMember.class);
            //绑定组id
            interPhoneMember.setInterPhoneGroupId(save.getId());
            //绑定项目
            interPhoneMember.setProjectId(projectId);
            interPhoneMemberServiceI.save(interPhoneMember);
        }
        Object o = interPhoneManager.saveAndSyn(save.getId(), false);
        ThreadUtil.execAsync(() -> interPhoneManager.calConditionMemberList(projectId));
        return Result.ok(o);
    }

    @PostMapping("/update")
    @Transactional
    public Object update(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        //组
        InterPhoneGroup group = jsonObject.getObject("group", InterPhoneGroup.class);
        if (!allowGroup(group.getGroupType())) {
            return Result.error("不支持手动创建的组类型");
        }
        //成员
        JSONArray memberList = jsonObject.getJSONArray("memberList");

        group.setProjectId(projectId);
        InterPhoneGroup save = interPhoneGroupServiceI.save(group);

        //删除旧组的成员
        interPhoneMemberServiceI.deleteAllByInterPhoneGroupId(save.getId());
        interPhoneMemberServiceI.deleteAllFixByInterPhoneGroupId(save.getId());

        //插入新成员
        for (Object member : memberList) {
            InterPhoneMember interPhoneMember = JSONObject.parseObject(JSONObject.toJSONString(member), InterPhoneMember.class);
            //绑定组id
            interPhoneMember.setInterPhoneGroupId(save.getId());
            //绑定项目
            interPhoneMember.setProjectId(projectId);
            interPhoneMemberServiceI.save(interPhoneMember);
        }
        Object o = interPhoneManager.updateAndSyn(save.getId());
        ThreadUtil.execAsync(() -> interPhoneManager.calConditionMemberList(projectId));
        return Result.ok(o);
    }

    /**
     * 失效/生效 组
     *
     * @param ids 组id列表
     * @return 结果
     */
    @PostMapping("/valid")
    public Object valid(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                InterPhoneGroup group = interPhoneGroupServiceI.get(id);
                group.setIsValid(!group.getIsValid());
                interPhoneGroupServiceI.save(group);
                interPhoneManager.validAndSyn(group.getId(), group.getIsValid());
            }
            return Result.ok(ids);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除组和清空组成员
     *
     * @param ids 组id 列表
     * @return 结果
     */
    @RequestMapping("/delete")
    @Transactional
    public Object delete(@RequestBody List<Long> ids, HttpServletRequest request) {
        try {
            for (Long id : ids) {

                //同步删除
                Object o = interPhoneManager.deleteAndSyn(id);
                //删除明细
                try {
                    List<InterPhoneMember> memberList = interPhoneMemberServiceI.findAllByInterPhoneGroupId(id);
                    for (InterPhoneMember member : memberList) {
                        interPhoneMemberServiceI.delete(member.getId());
                    }
                    interPhoneGroupServiceI.delete(id);
                } catch (Exception e) {

                }
            }
            return Result.ok(ids);
        } catch (Exception exception) {
            return Result.error(exception.getMessage());
        }
    }

    @PostMapping("/addMember")
    public Object addMember(Long interPhoneGroupId, List<InterPhoneMember> memberList, HttpServletRequest request) {
        try {
            for (Object member : memberList) {
                InterPhoneMember interPhoneMember = JSONObject.parseObject(JSONObject.toJSONString(member), InterPhoneMember.class);
                interPhoneMember.setInterPhoneGroupId(interPhoneGroupId);
                interPhoneMember.setProjectId(ProjectUtils.getProjectId(request));
                interPhoneMemberServiceI.save(interPhoneMember);
            }
            ThreadUtil.execAsync(() -> interPhoneManager.dispatch(interPhoneGroupId));
            ThreadUtil.execAsync(() -> interPhoneManager.calConditionMemberList(ProjectUtils.getProjectId(request)));
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/removeMember")
    public Object removeMember(Long interPhoneGroupId, Long id, HttpServletRequest request) {
        try {
            interPhoneMemberServiceI.delete(id);
        } catch (Exception e) {
        }
        ThreadUtil.execAsync(() -> interPhoneManager.dispatch(interPhoneGroupId));
        ThreadUtil.execAsync(() -> interPhoneManager.calConditionMemberList(ProjectUtils.getProjectId(request)));
        return Result.ok();
    }

    /**
     * 检查组类型(开关)
     *
     * @param groupType 前端的组类型
     * @return 是否允许创建该组
     */
    private boolean allowGroup(GroupType groupType) {
        try {
            boolean flag = false;
            switch (groupType) {
                case SlagSite:
                    break;
                case Manage:
                    break;
                case UndistributedCar:
                    break;
                case UndistributedMachine:
                    break;
                case Unknown:
                    break;
                case Temp:
                    flag = true;
                    break;
                case Captain:
                    flag = true;
                    break;
                case Support:
                    flag = true;
                    break;
            }
            return flag;
        } catch (Exception e) {
            return false;
        }
    }
}
