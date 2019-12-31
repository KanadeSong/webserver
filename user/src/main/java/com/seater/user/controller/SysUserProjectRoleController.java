package com.seater.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.MD5Helper;
import com.seater.user.entity.*;
import com.seater.user.entity.JoinType;
import com.seater.user.entity.repository.UserProjectRelationRepository;
import com.seater.user.manager.RoleManager;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

/**
 * @Description 项目外用户-角色关系
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/1 15:56
 */
@RestController
@RequestMapping("/api/userProjectRole")
public class SysUserProjectRoleController {

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

    @Autowired
    UserProjectRelationRepository userProjectRelationRepository;

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    SysRoleServiceI sysRoleServiceI;

    @Autowired
    RoleManager roleManager;

    /**
     * 待加入项目的员工列表
     *
     * @param projectId 项目id
     *                  人员类型 员工;车主;司机....
     * @return
     */
    @PostMapping("/toJoinProjectTemp")
    public Object joinProjectTemp(Long projectId, Integer current, Integer pageSize) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        // 从关系表中查找待加入人员列表
        Specification<UserProjectRelation> spec = new Specification<UserProjectRelation>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<UserProjectRelation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("joinStatus").as(JoinStatus.class), JoinStatus.Unorganized));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return userProjectRelationRepository.findAll(spec, PageRequest.of(cur, page));
    }

    /**
     * 已加入项目的员工列表
     *
     * @param projectId
     * @param current
     * @param pageSize
     * @param roleId    岗位id
     * @return
     */
    @PostMapping("/joinedProject")
    public Object joinedPro(Long projectId, Integer current, Integer pageSize, Long roleId, String name, HttpServletRequest request) {
        //  当前登陆人权限最大的角色排序值(sort)
        if (name == null) name = "";
        Long roleMixSort = (Long) SecurityUtils.getSubject().getSession().getAttribute(Constants.ROLE_MIX_SORT);
        if (roleMixSort == null) {
            roleMixSort = 10L;
        }
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        // 查找项目中关联的人
        Page<Map[]> userInProject_d = sysUserProjectRoleServiceI.findByProjectIdAndValidIsTrue(projectId, name, roleMixSort, PageRequest.of(cur, page));
        List<Map[]> userInProject = sysUserProjectRoleServiceI.findByProjectIdAndValidIsTrue(projectId);
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(roleId)) {
            Specification<SysUserProjectRole> spec = new Specification<SysUserProjectRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysUserProjectRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("roleId").as(Long.class), roleId));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            //  按照岗位筛选出来的人
            List<SysUserProjectRole> userProjectRoles = sysUserProjectRoleServiceI.queryWx(spec);
            List<SysUser> sysUsers = new ArrayList<>();
            List<Long> userIds = new ArrayList<>();
            for (SysUserProjectRole userProjectRole : userProjectRoles) {
                userIds.add(userProjectRole.getUserId());
            }
            if (userIds.size() > 0) {
                Specification<SysUser> specUsers = new Specification<SysUser>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.and(root.get("id").in(userIds)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                sysUsers = sysUserServiceI.queryWx(specUsers);
            }
            hashMap.put("userByRoleId", sysUsers);
        }
        hashMap.put("userInProD", userInProject_d);
        hashMap.put("userInPro", userInProject);
        return hashMap;
    }

    @PostMapping("/joinedProjectAll")
    public Object joinedProjectAll(Long projectId, Integer current, Integer pageSize, Long roleId, String name, HttpServletRequest request) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            // 查找项目中关联的人
            return sysUserProjectRoleServiceI.findAll(PageRequest.of(cur, page));
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 人员扫码加入项目
     *
     * @param userId    项目主id
     * @param projectId 项目id
     * @param type      加入类型
     * @param joinerId  待加入用户id
     * @return
     */
    @PostMapping("/joinProject")
    @Transactional
    public Object joinProject(Long userId, Long projectId, String type, Long joinerId, String joinerName, String mobile, String joinerOpenId) {

        try {
//            SysUserProjectRole sysUserProjectRole = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(joinerId, projectId);

            Specification<SysUserProjectRole> spec = new Specification<SysUserProjectRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysUserProjectRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("userId").as(Long.class), joinerId));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<SysUserProjectRole> sysUserProjectRoles = sysUserProjectRoleServiceI.queryWx(spec);

            if (sysUserProjectRoles.size() != 0) {
                return CommonUtil.errorJson("你已加入项目,无需重复加入");
            }
            UserProjectRelation relationTemp = userProjectRelationRepository.findByUserIdAndProjectIdAndJoinerIdAndValidIsTrue(userId, projectId, joinerId);

            if (relationTemp == null) {
                relationTemp = new UserProjectRelation();
            }
            //  车主暂时不能加入项目
//            if (type.equals(JoinType.CarOwner.toString())) {
//                relationTemp.setJoinType(JoinType.CarOwner);
//                relationTemp.setRoleId(Constants.WX_APP_CAR_OWNER_ROLE_IN_PROJECT);
//            }
            //  员工
            if (type.equals(JoinType.Employee.toString())) {
                relationTemp.setJoinType(JoinType.Employee);
                //   设置项目内员工角色id
                //获取项目中排序最小的角色
                Specification<SysRole> specMin = new Specification<SysRole>() {
                    List<Predicate> list = new ArrayList<>();

                    @Override
                    public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        query.orderBy(cb.desc(root.get("sort")));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<SysRole> sysRoleList = sysRoleServiceI.queryWx(specMin);
                if (sysRoleList.size() > 0 && sysRoleList.get(0).getSort() != null) {
                    relationTemp.setRoleId(sysRoleList.get(0).getId());
                } else {
                    relationTemp.setRoleId(0L);
                }
            } else {
                return CommonUtil.errorJson("未指定类型或类型错误,无法加入");
            }
            //  更新缓存表
            relationTemp.setMobile(mobile);
            relationTemp.setJoinerName(joinerName);
            relationTemp.setJoinerOpenId(joinerOpenId);
            relationTemp.setJoinerId(joinerId);
            relationTemp.setJoinStatus(JoinStatus.Unorganized);
            relationTemp.setUserId(userId);
            relationTemp.setValid(true);
            relationTemp.setProjectId(projectId);
            relationTemp.setUpdateTime(new Date());
            userProjectRelationRepository.save(relationTemp);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }


    /**
     * 项目方确认让其加入
     *
     * @return
     */
    @PostMapping("/proConfirm")
    @Transactional
    public Object proConfirm(Long id, Long projectId, Long joinerId) {

        try {
            UserProjectRelation userProjectRelation = userProjectRelationRepository.getById(id);
            if (sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(joinerId, projectId).size() == 0) {
                //  用户-项目-角色关系生成
                SysUserProjectRole sysUserProjectRole = new SysUserProjectRole();
                sysUserProjectRole.setUserId(joinerId);
                sysUserProjectRole.setProjectId(projectId);
                sysUserProjectRole.setValid(true);
                sysUserProjectRole.setRoleId(userProjectRelation.getRoleId());
                sysUserProjectRole.setDistributeStatus(DistributeStatus.Undistribute);
                sysUserProjectRoleServiceI.save(sysUserProjectRole);
                //  删除缓存
                userProjectRelationRepository.delete(userProjectRelation);

//                //  创建和绑定对讲机账号
//                SysUser sysUser = sysUserServiceI.get(userProjectRole.getUserId());
//                if (StringUtils.isEmpty(sysUser.getInterPhoneAccount()) || StringUtils.isEmpty(sysUser.getInterPhoneAccountId())){
//                    JSONObject userAccount = UprojectUtils.createTalkBackUserAccount(projectId, userProjectRole.getUserId(), UserObjectType.Person,sysUser.getName());
//                    sysUser.setInterPhoneAccount(userAccount.getString("account"));
//                    sysUser.setInterPhoneAccountId(userAccount.getString("accountId"));
//                    sysUserServiceI.save(sysUser);
//                }
//                //  创建和绑定对讲机账号end

                return CommonUtil.successJson("操作成功");
            } else {
                return CommonUtil.errorJson("已加入项目,无需重复加入");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 拒绝申请
     *
     * @return
     */
    @PostMapping("/refuse")
    @Transactional
    public Object refuse(Long id) {
        try {
            UserProjectRelation userProjectRelation = userProjectRelationRepository.getById(id);
            if (userProjectRelation != null) {
                userProjectRelationRepository.deleteById(userProjectRelation.getId());
                return CommonUtil.successJson("操作成功");
            } else {
                return CommonUtil.errorJson("拒绝失败,申请不存在");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @PostMapping("/delete")
    public Object delete(Long id) {
        try {
            sysUserProjectRoleServiceI.delete(id);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 删除批量
     *
     * @return
     */
    @PostMapping("/deletes")
    public Object deletes(@RequestBody List<Long> ids) {
        try {
            sysUserProjectRoleServiceI.delete(ids);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 失效
     *
     * @return
     */
    @PostMapping("/inValid")
    public Object inValid(Long id) {
        try {
            SysUserProjectRole sysUserProjectRole = sysUserProjectRoleServiceI.get(id);
            sysUserProjectRole.setValid(false);
            sysUserProjectRole.setInvalidTime(new Date());
            sysUserProjectRoleServiceI.save(sysUserProjectRole);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 编辑人员角色
     *
     * @param jsonObject (userId, projectId 项目id, List roleIds 赋予的角色列表)
     * @return
     */
    @PostMapping("/edit")
    @Transactional
    public Object edit(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        try {
            //  主键
            JSONArray ids = JSONArray.parseArray(JSONObject.toJSONString(jsonObject.get("ids")));
            Long userId = Long.parseLong(jsonObject.get("userId").toString());
            Long projectId = CommonUtil.getProjectId(request);
            JSONArray roleIds = JSONArray.parseArray(JSONObject.toJSONString(jsonObject.get("roleIds")));
            //  对讲机账号和id暂存
            SysUserProjectRole back = new SysUserProjectRole();
            for (Object id : ids) {
                SysUserProjectRole userProjectRole = sysUserProjectRoleServiceI.get(Long.parseLong(id.toString()));
                if (!StringUtils.isEmpty(userProjectRole.getInterPhoneAccount()) && !StringUtils.isEmpty(userProjectRole.getInterPhoneAccountId())) {
                    back = userProjectRole;
                }
                //根用户不删
                if (null != userProjectRole.getIsRoot() && userProjectRole.getIsRoot()) {
                    continue;
                }
                sysUserProjectRoleServiceI.delete(Long.parseLong(id.toString()));
            }

            //  一个职位都不设置时默认设置回员工
            //  根据项目id获取到该项目排序最大的角色
            Specification<SysRole> spec = new Specification<SysRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    //  必须倒序
                    query.orderBy(cb.desc(root.get("sort").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<SysRole> userProjectRoles = sysRoleServiceI.queryWx(spec);
            if (roleIds.size() == 0) {
                roleIds.add(userProjectRoles.get(0).getId());
            }
            //不只是选中权限最小的角色
            else if (roleIds.size() > 1) {
                JSONArray roleIdsFinal = new JSONArray();
                for (Object roleId : roleIds) {
                    long roleIdL = Long.parseLong(roleId.toString());
                    if (userProjectRoles.get(0).getId().equals(roleIdL)) {
                        continue;
                    }
                    roleIdsFinal.add(roleId);
                }
                roleIds = roleIdsFinal;
            }

            //查出项目中相同 userId 和 projectId 的
            List<SysUserProjectRole> projectRoleList = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(userId, projectId);
            //去重
            Set<Object> roleIdSet = new HashSet<>(roleIds);
            for (Object roleId : roleIdSet) {
                //判断是否已经存在相同roleId和projectId的 ,有就不加了
                boolean flag = false;
                for (SysUserProjectRole projectRole : projectRoleList) {
                    if (null != projectRole.getRoleId() &&
                            null != projectRole.getProjectId() &&
                            projectRole.getRoleId().equals(Long.parseLong(roleId + "")) &&
                            projectRole.getProjectId().equals(Long.parseLong(projectId + ""))) {
                        flag = true;
                    }
                }
                if (flag) {
                    continue;
                }

                SysUserProjectRole sysUserProjectRole = new SysUserProjectRole();
                sysUserProjectRole.setRoleId(Long.parseLong(roleId.toString()));
                sysUserProjectRole.setProjectId(projectId);
                sysUserProjectRole.setValid(true);
                sysUserProjectRole.setAddTime(new Date());
                sysUserProjectRole.setUserId(userId);
                sysUserProjectRole.setDistributeStatus(DistributeStatus.Distributed);
                sysUserProjectRole.setInterPhoneAccount(back.getInterPhoneAccount());
                sysUserProjectRole.setInterPhoneAccountId(back.getInterPhoneAccountId());
                sysUserProjectRoleServiceI.save(sysUserProjectRole);
            }
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    /**
     * 项目内手动新增用户
     *
     * @param jsonObject
     * @param id
     * @return
     */
    @PostMapping("/save")
    @Transactional
    public Object save(@RequestBody JSONObject jsonObject, @RequestParam(name = "id", required = false) Long id, HttpServletRequest request) {
        try {
            //  主键
            JSONArray ids = JSONArray.parseArray(JSONObject.toJSONString(jsonObject.get("ids")));
            SysUser sysUser = jsonObject.getObject("sysUser", SysUser.class);
            Long projectId = Long.parseLong(request.getHeader("projectId").toString());
            JSONArray roleIds = JSONArray.parseArray(JSONObject.toJSONString(jsonObject.get("roleIds")));

            //  判断新增时是否存在账号
            SysUser user = sysUserServiceI.get(sysUser.getId());
            if (ObjectUtils.isEmpty(user)) {
                SysUser sysUserExist = sysUserServiceI.getByAccount(sysUser.getAccount());
                if (!ObjectUtils.isEmpty(sysUserExist)) {
                    return CommonUtil.errorJson("已存在该账号:" + sysUserExist.getAccount());
                }
            } else {
                sysUser.setVipLevel(user.getVipLevel());
                sysUser.setMobile(user.getMobile());
            }

            if (!StringUtils.isEmpty(sysUser.getPassword())) {
                sysUser.setPassword(MD5Helper.encode(sysUser.getPassword()));
            }
            SysUser save = sysUserServiceI.save(sysUser);

            //  对讲机账号和id暂存
            SysUserProjectRole back = new SysUserProjectRole();
            for (Object roleId : ids) {
                SysUserProjectRole userProjectRole = sysUserProjectRoleServiceI.get(Long.parseLong(roleId.toString()));
                if (!StringUtils.isEmpty(userProjectRole.getInterPhoneAccount()) && !StringUtils.isEmpty(userProjectRole.getInterPhoneAccountId())) {
                    back = userProjectRole;
                }
                sysUserProjectRoleServiceI.delete(Long.parseLong(roleId.toString()));
            }

            //  一个职位都不设置时默认设置回员工
            if (roleIds.size() == 0) {
                //  根据项目id获取到该项目排序最大的角色
                Specification<SysRole> spec = new Specification<SysRole>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        //  倒序
                        query.orderBy(cb.desc(root.get("sort").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<SysRole> userProjectRoles = sysRoleServiceI.queryWx(spec);
                roleIds.add(userProjectRoles.get(0).getId());
            }

            for (Object roleId : roleIds) {
                SysUserProjectRole sysUserProjectRole = new SysUserProjectRole();
                sysUserProjectRole.setRoleId(Long.parseLong(roleId.toString()));
                sysUserProjectRole.setProjectId(projectId);
                sysUserProjectRole.setValid(true);
                sysUserProjectRole.setAddTime(new Date());
                sysUserProjectRole.setUserId(save.getId());
                sysUserProjectRole.setDistributeStatus(DistributeStatus.Distributed);
                sysUserProjectRole.setInterPhoneAccount(back.getInterPhoneAccount());
                sysUserProjectRole.setInterPhoneAccountId(back.getInterPhoneAccountId());
                sysUserProjectRoleServiceI.save(sysUserProjectRole);
            }
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败" + e.getMessage());
        }
    }

    @PostMapping("/setRoot")
    @Transactional
    public Object setRoot(SysUserProjectRole userProjectRole) throws IOException {
        if (userProjectRole.getIsRoot()) {
            userProjectRole.setIsRoot(true);
        }
        SysUserProjectRole save = sysUserProjectRoleServiceI.save(userProjectRole);
        SysRole sysRole = sysRoleServiceI.get(userProjectRole.getRoleId());
        sysRole.setProjectId(userProjectRole.getProjectId());
        return CommonUtil.successJson(save);
    }

    @PostMapping("/updateRoot")
    @Transactional
    public Object updateRoot(SysUserProjectRole userProjectRole) throws IOException {
        if (userProjectRole.getIsRoot()) {
            userProjectRole.setIsRoot(true);
        }
        SysUserProjectRole projectRole = sysUserProjectRoleServiceI.get(userProjectRole.getId());
        userProjectRole.setIsRoot(true);
        userProjectRole.setInterPhoneAccountId(projectRole.getInterPhoneAccountId());
        userProjectRole.setInterPhoneAccount(projectRole.getInterPhoneAccount());
        userProjectRole.setAddTime(projectRole.getAddTime());
        sysUserProjectRoleServiceI.deleteByUserIdAndProjectId(userProjectRole.getUserId(), userProjectRole.getProjectId());
        SysUserProjectRole save = sysUserProjectRoleServiceI.save(userProjectRole);
        SysRole sysRole = sysRoleServiceI.get(userProjectRole.getRoleId());
        sysRole.setProjectId(userProjectRole.getProjectId());
        return CommonUtil.successJson(save);
    }

    @PostMapping("/batchSetRoot")
    @Transactional
    public Object batchSetRoot(@RequestBody JSONArray jsonArray) throws IOException {
        List<Object> objectList = new ArrayList<>();
        //设置根用户
        for (Object o : jsonArray) {
            Long userId = JSONObject.parseObject(JSONObject.toJSONString(o)).getLong("userId");
            JSONArray roleIds = JSONObject.parseObject(JSONObject.toJSONString(o)).getJSONArray("roleIds");
            Long projectId = JSONObject.parseObject(JSONObject.toJSONString(o)).getLong("projectId");
            for (Object roleId : roleIds) {
                //根据给定父角色创建子角色
                SysRole sysRole = sysRoleServiceI.get(Long.parseLong(roleId + ""));
                SysRole role = new SysRole();
                BeanUtils.copyProperties(sysRole, role);
                role.setRoleName(sysRole.getRoleName());
                role.setDefaultName("项目id:" + projectId + "-" + sysRole.getRoleName() + "-子角色");
                role.setProjectId(projectId);
                role.setParentId(sysRole.getId());
                role.setUseType(UseType.Default);
                role.setId(null);
                SysRole newRole = sysRoleServiceI.save(role);

                SysUserProjectRole userProjectRole = new SysUserProjectRole();
                userProjectRole.setIsRoot(true);
                userProjectRole.setDistributeStatus(DistributeStatus.Distributed);
                userProjectRole.setValid(true);
                userProjectRole.setUserId(userId);
                userProjectRole.setRoleId(newRole.getId());
                userProjectRole.setProjectId(projectId);
                SysUserProjectRole save = sysUserProjectRoleServiceI.save(userProjectRole);
                objectList.add(save);
            }
        }
        return CommonUtil.successJsonData(objectList);
    }


    @PostMapping("/batchUpdateRoot")
    @Transactional
    public Object batchUpdateRoot(@RequestBody JSONArray jsonArray) throws IOException {
        List<Object> objectList = new ArrayList<>();

        Long projectId = 0L;
        for (Object o : jsonArray) {
            projectId = JSONObject.parseObject(JSONObject.toJSONString(o)).getLong("projectId");
        }
        List<SysUserProjectRole> rootList = sysUserProjectRoleServiceI.findAllByProjectAndIsRoot(projectId, true);
        //先删除角色
        for (SysUserProjectRole root : rootList) {
            if (null != root.getRoleId()) {
                sysRoleServiceI.delete(root.getRoleId());
            }
        }
        ////再删除关系
        sysUserProjectRoleServiceI.deleteAllByProjectIdAndIsRoot(projectId, true);

        for (Object o : jsonArray) {
            Long userId = JSONObject.parseObject(JSONObject.toJSONString(o)).getLong("userId");
            JSONArray ids = JSONObject.parseObject(JSONObject.toJSONString(o)).getJSONArray("ids");
            JSONArray roleIds = JSONObject.parseObject(JSONObject.toJSONString(o)).getJSONArray("roleIds");
            projectId = JSONObject.parseObject(JSONObject.toJSONString(o)).getLong("projectId");

            //调试
            //根据项目id获取所有根
            //缓存
//            List<SysUserProjectRole> tempList = new ArrayList<>();


            //对讲账号id缓存
            String accountId = "";
            String account = "";
            for (SysUserProjectRole root : rootList) {
                if (null != root.getUserId() &&
                        null != root.getProjectId() &&
                        !StringUtils.isEmpty(root.getInterPhoneAccountId()) &&
                        !StringUtils.isEmpty(root.getInterPhoneAccount()) &&
                        root.getUserId().equals(userId) &&
                        root.getProjectId().equals(projectId)) {
                    accountId = root.getInterPhoneAccountId();
                    account = root.getInterPhoneAccount();
                }
            }


            //

            /*for (Object id : ids) {
                SysUserProjectRole userProjectRole = sysUserProjectRoleServiceI.get(Long.parseLong(id + ""));
                if (!StringUtils.isEmpty(userProjectRole.getInterPhoneAccountId())) {

                }
                if (!StringUtils.isEmpty(userProjectRole.getInterPhoneAccount())) {

                }

                sysRoleServiceI.delete(userProjectRole.getRoleId());

                sysUserProjectRoleServiceI.delete(Long.parseLong(id + ""));
            }*/
            for (Object roleId : roleIds) {
                //根据给定父角色创建子角色
                SysRole sysRole = sysRoleServiceI.get(Long.parseLong(roleId + ""));
                SysRole role = new SysRole();
                role.setRoleName(sysRole.getRoleName());
                role.setDefaultName("项目id:" + projectId + "-" + sysRole.getRoleName() + "-子角色");
                role.setProjectId(projectId);
                role.setParentId(sysRole.getId());
                role.setUseType(UseType.Default);
                role.setId(null);
                SysRole newRole = sysRoleServiceI.save(role);

                SysUserProjectRole userProjectRole = new SysUserProjectRole();
                userProjectRole.setInterPhoneAccountId(accountId);
                userProjectRole.setInterPhoneAccount(account);
                userProjectRole.setIsRoot(true);
                userProjectRole.setDistributeStatus(DistributeStatus.Distributed);
                userProjectRole.setValid(true);
                userProjectRole.setUserId(userId);
                userProjectRole.setRoleId(newRole.getId());
                userProjectRole.setProjectId(projectId);
                SysUserProjectRole save = sysUserProjectRoleServiceI.save(userProjectRole);
                objectList.add(save);
            }
        }
        return CommonUtil.successJsonData(objectList);
    }

    @PostMapping("/setDefault")
    @Transactional
    public Object setDefault(@RequestBody JSONObject jsonObject) throws IOException {

        JSONArray roleIds = jsonObject.getJSONArray("roleIds");
        Long projectId = jsonObject.getLong("projectId");
        List<SysRole> oldList = roleManager.getAllDefaultByProjectId(projectId);
        //删除项目的旧的默认角色
        sysRoleServiceI.deleteAllByProjectIdAndIsDefault(projectId, true);

        for (Object roleId : roleIds) {
            //所选的父角色
            SysRole parent = sysRoleServiceI.get(Long.parseLong(roleId + ""));
            //创建***默认岗位***的子角色分配给项目端
            SysRole role = new SysRole();
            role.setId(null);
            role.setIsDefault(true);
            role.setRoleName(parent.getRoleName());
            role.setDefaultName("项目id:" + projectId + "-" + parent.getRoleName() + "-" + "子角色(默认岗位)");
            role.setAddTime(new Date());
            role.setUpdateTime(new Date());
            //分配的是项目端的
            role.setUseType(UseType.Project);
            role.setValid(true);
            role.setParentId(parent.getId());
            role.setProjectId(projectId);
            //新id
            SysRole save = sysRoleServiceI.save(role);
            if (oldList != null && oldList.size() != 0) {
                for (SysRole old : oldList) {
                    //相同的父id
                    if (null != old.getParentId() &&
                            null != role.getParentId() &&
                            old.getParentId().equals(save.getParentId())) {
                        //更新关系表
                        sysUserProjectRoleServiceI.updateRoleIdByRoleId(save.getId(), old.getId());
                        //删除旧角色
                        /*SysRole role1 = sysRoleServiceI.get(old.getId());
                        if (role1 != null) {
                            sysRoleServiceI.delete(old.getId());
                        }*/
                    }
                }
            }
        }

        //删除旧roleId关系
        if (oldList != null && oldList.size() != 0) {
            for (Object old : oldList) {
                SysRole oldRole = JSONObject.parseObject(JSONObject.toJSONString(old), SysRole.class);
                sysUserProjectRoleServiceI.deleteAllByRoleId(oldRole.getId());
            }
        }
        return CommonUtil.successJsonData(jsonObject);
    }

}
