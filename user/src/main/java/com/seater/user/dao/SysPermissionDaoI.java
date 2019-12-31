package com.seater.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.SysPermission;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface SysPermissionDaoI {

    public SysPermission get(Long id) throws IOException;
    public SysPermission save(SysPermission log) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysPermission> query();
    public Page<SysPermission> query(Example<SysPermission> example);
    public Page<SysPermission> query(Pageable pageable);
    public Page<SysPermission> query(Example<SysPermission> example, Pageable pageable);
    public List<SysPermission> getAll();
    public List<SysPermission> queryWx(Specification<SysPermission> spec);
    public Page<SysPermission> queryWx(Specification<SysPermission> spec, Pageable pageable);
    public List<SysPermission> findAllByRoleIdAndProjectId(Long roleId, Long projectId);
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds);

    List<SysPermission> getUserPermissionByRoleId(Long roleId);
}
