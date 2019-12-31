package com.seater.user.entity.repository;

import com.seater.user.entity.SysRolePermission;
import com.seater.user.entity.UseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission,Long>, JpaSpecificationExecutor<SysRolePermission> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from sys_role_permission where role_id = ?1 and project_id = ?2")
    public void deleteByRoleIdAndProjectId(Long roleId, Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from sys_role_permission where role_id = ?1 and project_id = ?2 AND use_type = ?3")
    void deleteByRoleIdAndProjectIdAndUseType(Long roleId, Long projectId, Integer useType);

    @Transactional
    @Modifying
//    @Query(nativeQuery = true , value = "delete from sys_role_permission where role_id = ?1 AND use_type = ?2")
    void deleteAllByRoleIdAndUseType(Long roleId, UseType useType);

    @Transactional
    @Modifying
    void deleteAllByRoleId(Long roleId);
}
