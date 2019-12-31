package com.seater.user.service.impl;

import com.seater.user.dao.SysUserRoleDaoI;
import com.seater.user.entity.SysUserRole;
import com.seater.user.service.SysUserRoleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SysUserRoleServiceImpl implements SysUserRoleServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysUserRoleDaoI sysUserRoleDaoI;

    @Override
    public SysUserRole get(Long id) throws IOException {
        return sysUserRoleDaoI.get(id);
    }

    @Override
    public SysUserRole save(SysUserRole sysUserRole) throws IOException {
        return sysUserRoleDaoI.save(sysUserRole);
    }

    @Override
    public void delete(Long id) {
        sysUserRoleDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        sysUserRoleDaoI.delete(ids);
    }

    @Override
    public Page<SysUserRole> query(Example<SysUserRole> example, Pageable pageable) {
        return sysUserRoleDaoI.query(example, pageable);
    }

    @Override
    public Page<SysUserRole> query(Example<SysUserRole> example) {
        return sysUserRoleDaoI.query(example);
    }

    @Override
    public Page<SysUserRole> query(Pageable pageable) {
        return sysUserRoleDaoI.query(pageable);
    }

    @Override
    public Page<SysUserRole> query() {
        return sysUserRoleDaoI.query();
    }

    @Override
    public List<SysUserRole> getAll() {
        return sysUserRoleDaoI.getAll();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        sysUserRoleDaoI.deleteAllByUserId(userId);
    }

}
