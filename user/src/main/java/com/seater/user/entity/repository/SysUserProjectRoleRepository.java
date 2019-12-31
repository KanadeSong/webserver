package com.seater.user.entity.repository;

import com.seater.user.entity.SysUserProjectRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/22 9:11
 */
public interface SysUserProjectRoleRepository extends JpaRepository<SysUserProjectRole, Long>, JpaSpecificationExecutor<SysUserProjectRole> {
    public List<SysUserProjectRole> findAllByUserIdAndValid(Long userId, Boolean valid);

    public List<SysUserProjectRole> findByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId);

    /*
    * "SELECT\n" +
            "	u.* \n" +
            "FROM\n" +
            "	sys_user u,\n" +
            "	sys_user_project_role r \n" +
            "WHERE\n" +
            "	u.id = r.user_id \n" +
            "	AND r.project_id = ?1 \n" +
            "	AND r.valid = 1\n"
    * */
    @Query(nativeQuery = true,
            countQuery = "SELECT\n" +
                    "	u.* \n" +
                    "FROM\n" +
                    "	sys_user u,\n" +
                    "	sys_user_project_role r,\n" +
                    "	sys_role sr \n" +
                    "WHERE\n" +
                    "	u.id = r.user_id \n" +
                    "	AND r.role_id = sr.id \n" +
                    "	AND r.project_id = ?1 \n" +
                    "	AND r.valid = 1 \n" +
                    "	AND sr.sort >= ?3 \n" +
                    "	AND u.`name` LIKE %?2% \n" +
                    "GROUP BY\n" +
                    "	u.id",
            value = "SELECT\n" +
                    "	u.* \n" +
                    "FROM\n" +
                    "	sys_user u,\n" +
                    "	sys_user_project_role r,\n" +
                    "	sys_role sr \n" +
                    "WHERE\n" +
                    "	u.id = r.user_id \n" +
                    "	AND r.role_id = sr.id \n" +
                    "	AND r.project_id = ?1 \n" +
                    "	AND r.valid = 1 \n" +
                    "	AND sr.sort >= ?3 \n" +
                    "	AND u.`name` LIKE %?2% \n" +
                    "GROUP BY\n" +
                    "	u.id")
    public Page<Map[]> findByProjectIdAndValidIsTrue(Long projectId, String name, Long sort, PageRequest pageRequest);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	u.*,\n" +
            "	p.id project_id,\n" +
            "	p.`name` project_name,\n" +
            "	p.avatar project_avatar,\n" +
            "	r.id u_p_r_id,\n" +
            "   o.id role_id,\n" +
            "	o.role_name,\n" +
            "	o.valid role_valid\n" +
            "FROM\n" +
            "	sys_user u\n" +
            "	LEFT JOIN sys_user_project_role r ON r.user_id = u.id\n" +
            "	LEFT JOIN project p ON p.id = r.project_id \n" +
            "   LEFT JOIN sys_role o ON o.id = r.role_id \n" +
            "WHERE\n" +
            "	r.project_id = ?1\n" +
            "	AND r.valid = 1")
    public List<Map[]> findByProjectIdAndValidIsTrue(Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE sys_user_project_role \n" +
            "SET valid = 0 \n" +
            "WHERE\n" +
            "	user_id = ?1 \n" +
            "	AND project_id = ?2")
    public int inValidProjectByUserIdAndProjectId(Long userId, Long projectId);

    public void deleteByUserIdAndProjectId(Long userId, Long projectId);

    public List<SysUserProjectRole> findAllByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId);

    @Query(nativeQuery = true,
            countQuery = "SELECT\n" +
                    "	u.*,\n" +
                    "	p.id project_id,\n" +
                    "	p.`name` project_name,\n" +
                    "	p.avatar project_avatar,\n" +
                    "	r.id u_p_r_id,\n" +
                    "	o.id role_id,\n" +
                    "	o.role_name,\n" +
                    "	o.valid role_valid \n" +
                    "FROM\n" +
                    "	sys_user u,\n" +
                    "	sys_user_project_role r,\n" +
                    "	project p,\n" +
                    "	sys_role o \n" +
                    "WHERE\n" +
                    "	u.id = r.user_id \n" +
                    "	AND r.project_id = p.id \n" +
                    "	AND r.role_id = o.id",
            value = "SELECT\n" +
                    "	u.*,\n" +
                    "	p.id project_id,\n" +
                    "	p.`name` project_name,\n" +
                    "	p.avatar project_avatar,\n" +
                    "	r.id u_p_r_id,\n" +
                    "	o.id role_id,\n" +
                    "	o.role_name,\n" +
                    "	o.valid role_valid \n" +
                    "FROM\n" +
                    "	sys_user u,\n" +
                    "	sys_user_project_role r,\n" +
                    "	project p,\n" +
                    "	sys_role o \n" +
                    "WHERE\n" +
                    "	u.id = r.user_id \n" +
                    "	AND r.project_id = p.id \n" +
                    "	AND r.role_id = o.id")
    public Page<Map[]> findAllJoinedProject(PageRequest pageRequest);

    List<SysUserProjectRole> findAllByProjectIdAndIsRoot(Long projectId,Boolean isRoot);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE sys_user_project_role SET role_id = ?1 where role_id = ?2 and project_id = ?3 ")
    void updateRoleIdByRoleIdAndProjectId(Long newRoleId, Long roleId, Long projectId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE sys_user_project_role SET role_id = ?1 where role_id = ?2")
    void updateRoleIdByRoleId(Long newRoleId, Long roleId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from sys_user_project_role where project_id = ?1 and is_root = ?2")
    void deleteAllByProjectIdAndIsRoot(Long projectId, Boolean isRoot);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from sys_user_project_role where role_id = ?1")
    void deleteAllByRoleId(Long roleId);
}
