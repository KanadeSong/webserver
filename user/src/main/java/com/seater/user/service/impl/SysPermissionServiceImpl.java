package com.seater.user.service.impl;

import com.seater.user.dao.SysPermissionDaoI;
import com.seater.user.entity.SysPermission;
import com.seater.user.service.SysPermissionServiceI;
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
public class SysPermissionServiceImpl implements SysPermissionServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysPermissionDaoI permissionDaoI;

    @Override
    public SysPermission get(Long id) throws IOException {
        return permissionDaoI.get(id);
    }

    @Override
    public SysPermission save(SysPermission log) throws IOException {
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
    public Page<SysPermission> query(Example<SysPermission> example, Pageable pageable) {
        return permissionDaoI.query(example, pageable);
    }

    @Override
    public Page<SysPermission> query(Example<SysPermission> example) {
        return permissionDaoI.query(example);
    }

    @Override
    public Page<SysPermission> query(Pageable pageable) {
        return permissionDaoI.query(pageable);
    }

    @Override
    public Page<SysPermission> query() {
        return permissionDaoI.query();
    }

    @Override
    public List<SysPermission> getAll() {
        return permissionDaoI.getAll();
    }

    @Override
    public List<SysPermission> queryWx(Specification<SysPermission> spec) {
        return permissionDaoI.queryWx(spec);
    }

    @Override
    public Page<SysPermission> queryWx(Specification<SysPermission> spec, Pageable pageable) {
        return permissionDaoI.queryWx(spec,pageable);
    }

    @Override
    public List<SysPermission> findAllByRoleIdAndProjectId(Long roleId, Long projectId) {
        return permissionDaoI.findAllByRoleIdAndProjectId(roleId,projectId);
    }

    @Override
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds) {
        return permissionDaoI.getUserPermissionByRoleIds(roleIds);
    }

    @Override
    public List<SysPermission> getUserPermissionByRoleId(Long roleId) {
        return permissionDaoI.getUserPermissionByRoleId(roleId);
    }

}
