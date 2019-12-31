package com.seater.user.service.impl;

import com.seater.user.dao.SysRolePermissionDaoI;
import com.seater.user.entity.SysRolePermission;
import com.seater.user.entity.UseType;
import com.seater.user.service.SysRolePermissionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SysRolePermissionServiceImpl implements SysRolePermissionServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysRolePermissionDaoI permissionDaoI;

    @Override
    public SysRolePermission get(Long id) throws IOException {
        return permissionDaoI.get(id);
    }

    @Override
    public SysRolePermission save(SysRolePermission log) throws IOException {
        return permissionDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        permissionDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        permissionDaoI.delete(ids);
    }

    @Override
    public Page<SysRolePermission> query(Example<SysRolePermission> example, Pageable pageable) {
        return permissionDaoI.query(example, pageable);
    }

    @Override
    public Page<SysRolePermission> query(Example<SysRolePermission> example) {
        return permissionDaoI.query(example);
    }

    @Override
    public Page<SysRolePermission> query(Pageable pageable) {
        return permissionDaoI.query(pageable);
    }

    @Override
    public Page<SysRolePermission> query() {
        return permissionDaoI.query();
    }

    @Override
    public List<SysRolePermission> getAll() {
        return permissionDaoI.getAll();
    }

    @Override
    public List<SysRolePermission> queryWx(Specification<SysRolePermission> spec) {
        return permissionDaoI.queryWx(spec);
    }

    @Override
    public Page<SysRolePermission> queryWx(Specification<SysRolePermission> spec, Pageable pageable) {
        return permissionDaoI.queryWx(spec,pageable);
    }

    @Override
    public void deleteByRoleIdAndProjectId(Long roleId, Long projectId) {
        permissionDaoI.deleteByRoleIdAndProjectId(roleId, projectId);
    }

    @Override
    public void deleteByRoleIdAndProjectIdAndUseType(Long roleId, Long projectId, UseType useType) {
        permissionDaoI.deleteByRoleIdAndProjectIdAndUseType(roleId, projectId, useType);
    }

    @Override
    public void deleteAllByRoleIdAndUseType(Long roleId, UseType useType) {
        permissionDaoI.deleteAllByRoleIdAndUseType(roleId, useType);
    }

    @Override
    public void deleteAllByRoleId(Long roleId) {
        permissionDaoI.deleteAllByRoleId(roleId);
    }
}
