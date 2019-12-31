package com.seater.user.service.impl;

import com.seater.user.dao.SysUserDaoI;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysUserDaoI userDaoI;

    @Override
    public SysUser get(Long id) throws IOException {
        return userDaoI.get(id);
    }

    @Override
    public SysUser save(SysUser user) throws IOException {
        return userDaoI.save(user);
    }

    @Override
    public void delete(Long id) {
        userDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        userDaoI.delete(ids);
    }

    @Override
    public Page<SysUser> query(Example<SysUser> example, Pageable pageable) {
        return userDaoI.query(example, pageable);
    }

    @Override
    public Page<SysUser> query(Specification<SysUser> spec, Pageable pageable) {
        return userDaoI.query(spec,pageable);
    }

    @Override
    public Page<SysUser> query(Example<SysUser> example) {
        return userDaoI.query(example);
    }

    @Override
    public Page<SysUser> query(Pageable pageable) {
        return userDaoI.query(pageable);
    }

    @Override
    public Page<SysUser> query() {
        return userDaoI.query();
    }

    @Override
    public List<SysUser> getAll() {
        return userDaoI.getAll();
    }

    @Override
    public SysUser getByAccount(String account) {
        return userDaoI.getByAccount(account);
    }

    @Override
    public List<SysUser> findByAccountAndPassword(String account, String password) {
        return userDaoI.findByAccountAndPassword(account, password);
    }

    @Override
    public SysUser getByAccountAndPassword(String account,String password) {
        return userDaoI.getByAccountAndPassword(account,password);
    }

    @Override
    public SysUser getByOpenId(String openId) {
        return userDaoI.getByOpenId(openId);
    }

    @Override
    public Page<Map[]> getDriverByOwnerIdStatus(Long ownerId, Pageable pageable) {
        return userDaoI.getDriverByOwnerIdStatus(ownerId,pageable);
    }

    @Override
    public Page<Map[]> getDriverByOwnerIdStatusTemp(Long ownerId, Pageable pageable) {
        return userDaoI.getDriverByOwnerIdStatusTemp(ownerId,pageable);
    }

    @Override
    public Page<SysUser> getDriverByOwnerId(Long ownerId, Pageable pageable) {
        return userDaoI.getDriverByOwnerId(ownerId,pageable);
    }

    @Override
    public Page<SysUser> getDriverByProjectId(Long projectId, Pageable pageable) {
        return userDaoI.getDriverByProjectId(projectId,pageable);
    }

    @Override
    public List<SysUser> queryWx(Specification<SysUser> spec) {
        return userDaoI.queryWx(spec);
    }
}
