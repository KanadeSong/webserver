package com.seater.user.entity.repository;

import com.seater.user.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;


public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
   public List<SysUser> findByAccountAndPassword(String account, String passwrod);
   public SysUser getByAccount(String account);
   public SysUser getByOpenId(String openId);
   public SysUser getByAccountAndPassword(String account, String password);

   @Query(nativeQuery = true,value = "SELECT\n" +
           "	u.* \n" +
           "FROM\n" +
           "	sys_user u\n" +
           "	LEFT JOIN owner_driver_relation odr ON odr.driver_id = u.id \n" +
           "WHERE\n" +
           "	odr.owner_id = ?1 " +
           "AND odr.valid = 1 ")
   public Page<SysUser> getDriverByOwnerId(Long ownerId, Pageable pageable);

   @Query(nativeQuery = true,value = "SELECT\n" +
           "	u.*, \n" +
           "	odr.join_status, \n" +
           "	odr.driver_id, \n" +
           "	odr.owner_id, \n" +
           "	odr.id o_d_r_id \n" +
           "FROM\n" +
           "	sys_user u\n" +
           "	LEFT JOIN owner_driver_relation odr ON odr.driver_id = u.id \n" +
           "WHERE\n" +
           "	odr.owner_id = ?1 " +
           "AND odr.join_status = 1 " +
           "AND odr.valid = 1 ",
   countQuery = "SELECT\n" +
           "	count(u.id) \n" +
           "FROM\n" +
           "	sys_user u\n" +
           "	LEFT JOIN owner_driver_relation odr ON odr.driver_id = u.id \n" +
           "WHERE\n" +
           "	odr.owner_id = ?1 " +
           "AND odr.join_status = 1 " +
           "AND odr.valid = 1 ")
   // Status   >>>>> 带状态
   public Page<Map[]> getDriverByOwnerIdStatus(Long ownerId, Pageable pageable);

   @Query(nativeQuery = true,value = "SELECT\n" +
           "	u.*, \n" +
           "	odr.join_status, \n" +
           "	odr.driver_id, \n" +
           "	odr.owner_id \n" +
           "FROM\n" +
           "	sys_user u\n" +
           "	LEFT JOIN owner_driver_relation_temp odr ON odr.driver_id = u.id \n" +
           "WHERE\n" +
           "	odr.owner_id = ?1 " +
           "AND odr.valid = 1 ",
            countQuery = "SELECT\n" +
                    "	count(u.id)\n" +
                    "FROM\n" +
                    "	sys_user u\n" +
                    "	LEFT JOIN owner_driver_relation_temp odr ON odr.driver_id = u.id \n" +
                    "WHERE\n" +
                    "	odr.owner_id = ?1 " +
                    "AND odr.valid = 1 ")
   // StatusTemp  >>>>> 有效的缓存
   public Page<Map[]> getDriverByOwnerIdStatusTemp(Long ownerId, Pageable pageable); 

   @Query(nativeQuery = true, value = "SELECT\n" +
           "	u.id,\n" +
           "	u.account,\n" +
           "	u.mobile,\n" +
           "	u.name,\n" +
           "	u.password,\n" +
           "	u.sex,\n" +
           "	u.add_time,\n" +
           "	u.id_no,\n" +
           "	u.open_id,\n" +
           "	u.valid,\n" +
           "	u.address,\n" +
           "	u.avatar,\n" +
           "	u.vip_level\n" +
           "FROM\n" +
           "	sys_user u\n" +
           "	LEFT JOIN project_car c ON c.driver_id = u.id \n" +
           "WHERE\n" +
           "	c.project_id = ?1 " +
           "GROUP BY " +
           "c.driver_id ",
            countQuery = "SELECT\n" +
                    "	count(u.id)\n" +
                    "FROM\n" +
                    "	sys_user u\n" +
                    "	LEFT JOIN project_car c ON c.driver_id = u.id \n" +
                    "WHERE\n" +
                    "	c.project_id = ?1 " +
                    "GROUP BY " +
                    "c.driver_id ")
   // GROUP BY 司机去重
   public Page<SysUser> getDriverByProjectId(Long projectId, Pageable pageable);
}
