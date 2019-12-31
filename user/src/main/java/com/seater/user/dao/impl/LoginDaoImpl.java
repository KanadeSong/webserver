package com.seater.user.dao.impl;

import com.seater.user.dao.LoginDaoI;
import com.seater.user.entity.SysPermission;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.repository.SysPermissionRepository;
import com.seater.user.entity.repository.SysRoleRepository;
import com.seater.user.entity.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 处理登陆和授权的相关逻辑的Dao层实现
 * 主要用到三个相关repository(数据表)
 * 用户---角色---权限
 */
@Repository
public class LoginDaoImpl implements LoginDaoI {
    
    @Autowired
    SysUserRepository sysUserRepository;
    
    @Autowired
    SysPermissionRepository sysPermissionRepository;
    
    @Autowired
    SysRoleRepository sysRoleRepository;


    @Override
    public SysUser getByAccountAndPassword(String username,String password) {
        return sysUserRepository.getByAccountAndPassword(username,password);
    }

    /**
     * 根据用户ID 查询当前用户拥有的权限信息 注：没去重 建议使用下面的两个实现去重 @
     * @param userId    用户id
     * @return  当前用户权限信息
     */
    @Override
    public List<SysPermission> getUserPermissionByUserId(Long userId) {
        return sysPermissionRepository.getUserPermissionByUserId(userId);
    }

    @Override
    public List<SysRole> getUserRolesByUserId(Long userId) {
        return sysRoleRepository.getUserRolesByUserId(userId);
    }

    @Override
    public List<SysRole> getAllRoles() {
        return sysRoleRepository.findAll();
    }

    @Override
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds) {
        return sysPermissionRepository.getUserPermissionByRoleIds(roleIds);
    }


}
