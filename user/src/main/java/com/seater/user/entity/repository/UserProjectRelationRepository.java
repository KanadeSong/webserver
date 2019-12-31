package com.seater.user.entity.repository;

import com.seater.user.entity.UserProjectRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/1 16:21
 */
public interface UserProjectRelationRepository extends JpaRepository<UserProjectRelation,Long>, JpaSpecificationExecutor<UserProjectRelation> {
    
    public UserProjectRelation findByUserIdAndProjectIdAndJoinerIdAndValidIsTrue(Long ownerId, Long projectId, Long joinerId);
    
    @Query(nativeQuery = true, 
            value = "SELECT\n" +
            "	u.*,\n" +
            "	p.id project_id,\n" +
            "	p.`name` project_name,\n" +
            "	p.avatar project_avatar," +
            "   r.join_status\n" +
            "FROM\n" +
            "	sys_user u\n" +
            "	LEFT JOIN user_project_relation r ON r.joiner_id = u.id\n" +
            "	LEFT JOIN project p ON p.id = r.project_id \n" +
            "WHERE\n" +
            "	r.user_id = ?1 \n" +
            "AND r.project_id = ?2 " +
            "AND r.valid = 1 " +
            "AND r.join_status = 0",
    countQuery = "SELECT\n" +
            "	count(u.id)\n" +
            "FROM\n" +
            "	sys_user u\n" +
            "	LEFT JOIN user_project_relation r ON r.joiner_id = u.id\n" +
            "	LEFT JOIN project p ON p.id = r.project_id \n" +
            "WHERE\n" + 
            "	r.user_id = ?1 \n" +
            "AND r.project_id = ?2 " +
            "AND r.valid = 1 " +
            "AND r.join_status = 0")
    //  valid = 1 有效申请  join_status = 0 未加入状态
    public Page<Map[]> findByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId, PageRequest pageRequest);
    
    UserProjectRelation getById(Long id);
    
    @Modifying
    void deleteById(Long id);
}
