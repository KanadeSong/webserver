package com.seater.user.entity.repository;

import com.seater.user.entity.SysPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysPermissionRepository extends JpaRepository<SysPermission, Long>, JpaSpecificationExecutor<SysPermission> {

    //    @Query(nativeQuery = true,value = "SELECT\n" +
//            "	p.id,\n" +
//            "	p.menu_code,\n" +
//            "	p.menu_name,\n" +
//            "	p.permission_code,\n" +
//            "	p.permission_name,\n" +
//            "	p.parent_id,\n" +
//            "	p.parent_id_tree,\n" +
//            "	p.type,\n" +
//            "	p.sort,\n" +
//            "	p.required_Permission,\n" +
//            "	p.add_time,\n" +
//            "	p.valid,\n" +
//            "	p.project_id\n" +
//            "FROM\n" +
//            "	sys_user u\n" +
//            "	LEFT JOIN sys_user_role ur ON u.id = ur.user_id\n" +
//            "	LEFT JOIN sys_role r ON r.id = ur.role_id\n" +
//            "	LEFT JOIN sys_role_permission rp ON r.id = rp.role_id\n" +
//            "	LEFT JOIN sys_permission p ON rp.permission_id = p.id \n" +
//            "WHERE\n" +
//            "	u.id = ?1 \n" +
//            "ORDER BY\n" +
//            "	p.permission_code")
//    public List<SysPermission> getUserPermissionByUserId(Long userId);
//    
//    
//    @Query(nativeQuery = true,value = "SELECT\n" +
//            "	p.id,\n" +
//            "	p.menu_code,\n" +
//            "	p.menu_name,\n" +
//            "	p.menu_url,\n" +
//            "	p.permission_code,\n" +
//            "	p.permission_name,\n" +
//            "	p.parent_id,\n" +
//            "	p.parent_id_tree,\n" +
//            "	p.type,\n" +
//            "	p.sort,\n" +
//            "	p.required_Permission,\n" +
//            "	p.add_time,\n" +
//            "	p.valid,\n" +
//            "	p.project_id\n" +
//            "FROM\n" +
//            "	sys_permission p\n" +
//            "	LEFT JOIN sys_role_permission rp ON rp.permission_id = p.id\n" +
//            "	LEFT JOIN sys_role r ON r.id = rp.role_id \n" +
//            "WHERE\n" +
//            "	r.id in (?1)\n" +
//            "GROUP BY\n" +
//            "	p.id \n" +
//            "ORDER BY\n" +
//            "	p.sort asc")
//    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds);
    @Query(nativeQuery = true, value = "SELECT\n" +
            "	p.*\n" +
            "FROM\n" +
            "	sys_user u\n" +
            "	LEFT JOIN sys_user_role ur ON u.id = ur.user_id\n" +
            "	LEFT JOIN sys_role r ON r.id = ur.role_id\n" +
            "	LEFT JOIN sys_role_permission rp ON r.id = rp.role_id\n" +
            "	LEFT JOIN sys_permission p ON rp.permission_id = p.id \n" +
            "WHERE\n" +
            "	u.id = ?1 \n" +
            "ORDER BY\n" +
            "	p.permission_code")
    public List<SysPermission> getUserPermissionByUserId(Long userId);


    @Query(nativeQuery = true, value = "SELECT\n" +
            "	p.*\n" +
            "FROM\n" +
            "	sys_permission p\n" +
            "	LEFT JOIN sys_role_permission rp ON rp.permission_id = p.id\n" +
            "	LEFT JOIN sys_role r ON r.id = rp.role_id \n" +
            "WHERE\n" +
            "	r.id in (?1)\n" +
            "GROUP BY\n" +
            "	p.id \n" +
            "ORDER BY\n" +
            "	p.sort asc")
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	p.*\n" +
            "FROM\n" +
            "	sys_permission p\n" +
            "	LEFT JOIN sys_role_permission rp ON rp.permission_id = p.id\n" +
            "	LEFT JOIN sys_role r ON r.id = rp.role_id \n" +
            "WHERE\n" +
            "	r.id = ?1\n" +
            "GROUP BY\n" +
            "	p.id \n" +
            "ORDER BY\n" +
            "	p.sort asc")
    List<SysPermission> getUserPermissionByRoleId(Long roleId);

    @Query(nativeQuery = true,
            value = "SELECT\n" +
                    "	p.*\n" +
                    "FROM\n" +
                    "	sys_permission p\n" +
                    "	LEFT JOIN sys_role_permission rp ON rp.permission_id = p.id\n" +
                    "	LEFT JOIN sys_role r ON r.id = rp.role_id \n" +
                    "WHERE\n" +
                    "	r.id = ?1\n" +
                    "AND    " +
                    "   r.project_id = ?2\n" +
                    "GROUP BY\n" +
                    "	p.id \n" +
                    "ORDER BY\n" +
                    "	p.sort asc")
    public List<SysPermission> findAllByRoleIdAndProjectId(Long roleId, Long projectId);
}
