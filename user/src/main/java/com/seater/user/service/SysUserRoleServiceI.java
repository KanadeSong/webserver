package com.seater.user.service;

import com.seater.user.entity.SysUserRole;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface SysUserRoleServiceI {

    public SysUserRole get(Long id) throws IOException;
    public SysUserRole save(SysUserRole sysUserRole) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysUserRole> query();
    public Page<SysUserRole> query(Example<SysUserRole> example);
    public Page<SysUserRole> query(Pageable pageable);
    public Page<SysUserRole> query(Example<SysUserRole> example, Pageable pageable);
    public List<SysUserRole> getAll();
    public void deleteAllByUserId(Long userId);
}
