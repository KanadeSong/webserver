package com.seater.user.dao;

import com.seater.user.entity.SysRole;
import com.seater.user.entity.UseType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface SysRoleDaoI {

    public SysRole get(Long id) throws IOException;
    public SysRole save(SysRole log) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysRole> query();
    public Page<SysRole> query(Example<SysRole> example);
    public Page<SysRole> query(Pageable pageable);
    public Page<SysRole> query(Example<SysRole> example, Pageable pageable);
    public List<SysRole> getAll();
    public List<SysRole> queryWx(Specification<SysRole> spec);
    public Page<SysRole> queryWx(Specification<SysRole> spec, Pageable pageable);
    List<SysRole> getByUseType(UseType useType);
    public List<SysRole> getAllByProjectId(Long projectId);
    public List<SysRole> findAllByIdIsIn(List<Long> ids);

    List<SysRole> findAllByUseTypeAndParentIdIsNull(UseType useType);

    void deleteAllByParentId(Long parentId);

    void deleteAllByProjectIdAndIsDefault(Long projectId, Boolean isDefault);

    void updateAllRoleNameByParentId(String roleName, Long parentId);
}
