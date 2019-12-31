package com.seater.user.manager;

import com.seater.user.entity.SysPermission;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.SysPermissionServiceI;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.util.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 角色业务管理
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/12 18:25
 */
@Service
public class RoleManager {

    @Autowired
    SysRoleServiceI roleServiceI;

    @Autowired
    SysPermissionServiceI permissionServiceI;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

    public boolean isSuperInProject(SysUser user) {
        if (user.getAccount().equals(Constants.SUPER_USER_ACCOUNT)) {
            return true;
        }
        return false;
    }

    public List<Long> getAllRoleIdByProjectId(Long projectId) {
        List<SysRole> roleList = roleServiceI.getAllByProjectId(projectId);
        List<Long> roleIds = new ArrayList<>();
        for (SysRole role : roleList) {
            roleIds.add(role.getId());
            if (null != role.getParentId()) {
                roleIds.add(role.getParentId());
            }
        }
        return roleIds;
    }

    public List<SysRole> getAllRoleByProjectId(Long projectId) {
        return roleServiceI.getAllByProjectId(projectId);
    }

    public List<SysRole> getRootByProjectId(Long projectId) {
        Specification<SysRole> spec = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.isNull(root.get("parentId")));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return roleServiceI.queryWx(spec);
    }


    /**
     * 根据项目id获取项目所有权限
     *
     * @return
     */
    public List<SysPermission> getPermissionsByProjectId(Long projectId) {
        List<Long> roleIdList = getAllRoleIdByProjectId(projectId);
        return permissionServiceI.getUserPermissionByRoleIds(roleIdList);
    }

    public List<SysPermission> getAllPermission() {
        return permissionServiceI.getAll();
    }

    public List<SysPermission> getPermissionsByRoleIds(List<Long> roleIds) {
        return permissionServiceI.getUserPermissionByRoleIds(roleIds);
    }

    public List<Long> getRoleIdByRoleList(List<SysRole> roleList) {
        List<Long> roleIdList = new ArrayList<>();
        for (SysRole role : roleList) {
            if (null != role && null != role.getId() && null != role.getParentId()) {
                roleIdList.add(role.getId());
                roleIdList.add(role.getParentId());
            }
        }
        return roleIdList;
    }

    public List<SysRole> getRoleByRoleIds(List<Long> roleIds) {
        Specification<SysRole> spec = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();
            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.in(root.get("id").as(Long.class).in(roleIds)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return roleServiceI.queryWx(spec);
    }

    public List<SysPermission> getPermissionsByRootAndProjectId(Long projectId) {
        List<SysUserProjectRole> isRoot = sysUserProjectRoleServiceI.findAllByProjectAndIsRoot(projectId, true);
        List<Long> roleIdList = new ArrayList<>();
        List<Long> roleIdAndParentList = new ArrayList<>();
        for (SysUserProjectRole projectRole : isRoot) {
            if (null != projectRole.getIsRoot() && projectRole.getIsRoot()) {
                roleIdList.add(projectRole.getRoleId());
            }
        }

        List<SysPermission> permissionList = new ArrayList<>();
        if (roleIdList.size() > 0) {
            List<SysRole> roleList = roleServiceI.findAllByIdIsIn(roleIdList);
            for (SysRole role : roleList) {
                roleIdAndParentList.add(role.getId());
                if (null != role.getParentId()) {
                    roleIdAndParentList.add(role.getParentId());
                }
            }
            permissionList = getPermissionsByRoleIds(roleIdAndParentList);
        }
        return permissionList;
    }

    public List<SysRole> getAllDefaultByProjectId(Long projectId) {
        Specification<SysRole> spec = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("isDefault").as(Boolean.class), true));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return roleServiceI.queryWx(spec);
    }
}
