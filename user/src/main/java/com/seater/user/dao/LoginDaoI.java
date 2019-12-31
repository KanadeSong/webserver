package com.seater.user.dao;


import com.seater.user.entity.SysPermission;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.SysUser;

import java.util.List;

/**
 * 处理登陆和授权的相关逻辑的Dao层
 */
public interface LoginDaoI {
    /**
     * 根据用户名和密码查询对应的用户信息
     */
    SysUser getByAccountAndPassword(String username, String password);

    /**
     * 根据用户ID 查询当前用户拥有的权限信息 如果多角色会出现重复权限的情况,建议用分步查询方法
     * @param userId    用户id
     * @return  当前用户权限信息
     */
    public List<SysPermission> getUserPermissionByUserId(Long userId);

    /**
     * 根据用户ID查询用户角色
     * @param userId
     * @return
     */
    public List<SysRole> getUserRolesByUserId(Long userId);

    /**
     * 获取全部角色列表 主要时管理员用到
     * @return
     */
    public List<SysRole> getAllRoles();
    
    /**
     * 根据角色id查询对应角色的权限信息
     * @param roleIds   多个角色id
     * @return  对应角色的权限信息
     */
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds);
}
