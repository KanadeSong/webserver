package com.seater.user.dao;

import com.seater.user.entity.SysUser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SysUserDaoI {
    public SysUser get(Long id) throws IOException;
    public SysUser save(SysUser user) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysUser> query();
    public Page<SysUser> query(Example<SysUser> example);
    public Page<SysUser> query(Pageable pageable);
    public Page<SysUser> query(Example<SysUser> example, Pageable pageable);
    public Page<SysUser> query(Specification<SysUser> spec, Pageable pageable);
    public List<SysUser> getAll();
    public SysUser getByAccount(String account);
    public List<SysUser> findByAccountAndPassword(String account, String password);
    public SysUser getByAccountAndPassword(String account, String password);
    public SysUser getByOpenId(String openId);
    public Page<Map[]> getDriverByOwnerIdStatus(Long ownerId, Pageable pageable);
    public Page<Map[]> getDriverByOwnerIdStatusTemp(Long ownerId, Pageable pageable);
    public Page<SysUser> getDriverByOwnerId(Long ownerId, Pageable pageable);
    public Page<SysUser> getDriverByProjectId(Long projectId, Pageable pageable);
    public List<SysUser> queryWx(Specification<SysUser> spec);
}