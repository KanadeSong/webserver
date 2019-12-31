package com.seater.user.service.impl;

import com.seater.user.dao.SysUserProjectRoleDaoI;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.SysUserProjectRoleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/16 9:09
 */
@Service
public class SysUserProjectRoleServiceImpl implements SysUserProjectRoleServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysUserProjectRoleDaoI sysUserProjectRoleDaoI;

    @Override
    public SysUserProjectRole get(Long id) throws IOException {
        return sysUserProjectRoleDaoI.get(id);
    }

    @Override
    public SysUserProjectRole save(SysUserProjectRole sysUserRole) throws IOException {
        return sysUserProjectRoleDaoI.save(sysUserRole);
    }

    @Override
    public void delete(Long id) {
        sysUserProjectRoleDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        sysUserProjectRoleDaoI.delete(ids);
    }

    @Override
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> example, Pageable pageable) {
        return sysUserProjectRoleDaoI.query(example, pageable);
    }

    @Override
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> example) {
        return sysUserProjectRoleDaoI.query(example);
    }

    @Override
    public List<SysUserProjectRole> queryWx(Specification<SysUserProjectRole> spec) {
        return sysUserProjectRoleDaoI.queryWx(spec);
    }

    @Override
    public Page<SysUserProjectRole> query(Pageable pageable) {
        return sysUserProjectRoleDaoI.query(pageable);
    }

    @Override
    public Page<SysUserProjectRole> query() {
        return sysUserProjectRoleDaoI.query();
    }

    @Override
    public List<SysUserProjectRole> getAll() {
        return sysUserProjectRoleDaoI.getAll();
    }

    @Override
    public List<SysUserProjectRole> findAllByUserIdAndValid(Long userId, Boolean valid) {
        return sysUserProjectRoleDaoI.findAllByUserIdAndValid(userId, valid);
    }

    @Override
    public List<SysUserProjectRole> findByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId) {
        return sysUserProjectRoleDaoI.findByUserIdAndProjectIdAndValidIsTrue(userId, projectId);
    }

    @Override
    public Page<Map[]> findByProjectIdAndValidIsTrue(Long projectId, String name, Long sort, PageRequest pageRequest) {
        return sysUserProjectRoleDaoI.findByProjectIdAndValidIsTrue(projectId, name, sort, pageRequest);
    }

    @Override
    public List<Map[]> findByProjectIdAndValidIsTrue(Long projectId) {
        return sysUserProjectRoleDaoI.findByProjectIdAndValidIsTrue(projectId);
    }

    @Override
    public int inValidProjectByUserIdAndProjectId(Long userId, Long projectId) {
        return sysUserProjectRoleDaoI.inValidProjectByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public void deleteByUserIdAndProjectId(Long userId, Long projectId) {
        sysUserProjectRoleDaoI.deleteByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Page<Map[]> findAll(PageRequest pageRequest) {
        return sysUserProjectRoleDaoI.findAll(pageRequest);
    }

    @Override
    public List<SysUserProjectRole> findAllByProjectAndIsRoot(Long projectId, Boolean isRoot) {
        return sysUserProjectRoleDaoI.findAllByProjectAndIsRoot(projectId, isRoot);
    }

    @Override
    public void updateRoleIdByRoleIdAndProjectId(Long newRoleId, Long roleId, Long projectId) {
        sysUserProjectRoleDaoI.updateRoleIdByRoleIdAndProjectId(newRoleId, roleId, projectId);
    }

    @Override
    public void updateRoleIdByRoleId(Long newRoleId, Long roleId) {
        sysUserProjectRoleDaoI.updateRoleIdByRoleId(newRoleId, roleId);
    }

    @Override
    public void deleteAllByProjectIdAndIsRoot(Long projectId, Boolean isRoot) {
        sysUserProjectRoleDaoI.deleteAllByProjectIdAndIsRoot(projectId, isRoot);
    }

    @Override
    public void deleteAllByRoleId(Long roleId) {
        sysUserProjectRoleDaoI.deleteAllByRoleId(roleId);
    }

}
