package com.seater.user.dao;

import com.seater.user.entity.SysRolePermission;
import com.seater.user.entity.UseType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface SysRolePermissionDaoI {

    public SysRolePermission get(Long id) throws IOException;
    public SysRolePermission save(SysRolePermission log) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysRolePermission> query();
    public Page<SysRolePermission> query(Example<SysRolePermission> example);
    public Page<SysRolePermission> query(Pageable pageable);
    public Page<SysRolePermission> query(Example<SysRolePermission> example, Pageable pageable);
    public List<SysRolePermission> getAll();
    public List<SysRolePermission> queryWx(Specification<SysRolePermission> spec);
    public Page<SysRolePermission> queryWx(Specification<SysRolePermission> spec, Pageable pageable);
    public void deleteByRoleIdAndProjectId(Long roleId, Long projectId);

    void deleteByRoleIdAndProjectIdAndUseType(Long roleId, Long projectId, UseType useType);
    void deleteAllByRoleIdAndUseType(Long roleId, UseType useType);
    void deleteAllByRoleId(Long roleId);
}
