package com.seater.user.entity.repository;

import com.seater.user.entity.SysRole;
import com.seater.user.entity.UseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface SysRoleRepository extends JpaRepository<SysRole,Long>, JpaSpecificationExecutor<SysRole> {

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	r.*\n" +
            "FROM\n" +
            "	sys_role r\n" +
            "	LEFT JOIN sys_user_role ur ON r.id = ur.role_id\n" +
            "	LEFT JOIN sys_user u ON u.id = ur.user_id \n" +
            "WHERE\n" +
            "	u.id = ?1\n" +
            "AND u.valid = 1")
    public List<SysRole> getUserRolesByUserId(Long userId);

    @Query(nativeQuery = true,value = "SELECT\n" +
            "	r.id,\n" +
            "	r.role_name,\n" +
            "	r.add_time add_time,\n" +
            "   r.valid valid\n" +
            "FROM\n" +
            "	sys_role r\n" +
            "	LEFT JOIN sys_user_project_role upr ON upr.role_id = r.id\n" +
            "	LEFT JOIN sys_user u ON u.id = upr.user_id\n" +
            "	LEFT JOIN project p ON p.id = upr.project_id \n" +
            "WHERE\n" +
            "	u.id = ?1 \n" +
            "	AND p.id = ?2")
    public List<SysRole> findAllByUserIdAndProjectId(Long userId, Long projectId);

    public List<SysRole> findAllByIdIsIn(List<Long> ids);

    List<SysRole> getByUseType(UseType useType);

    public List<SysRole> getAllByProjectId(Long projectId);

    List<SysRole> findAllByUseTypeAndParentIdIsNull(UseType useType);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from sys_role where parent_id = ?1")
    void deleteAllByParentId(Long parentId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from sys_role where project_id = ?1 and is_default = ?2")
    void deleteAllByProjectIdAndIsDefault(Long projectId, Boolean isDefault);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update sys_role set role_name = ?1 where parent_id = ?2")
    void updateAllRoleNameByParentId(String roleName, Long parentId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from sys_role where id = ?1")
    void deleteByRoleId(Long id);

}
