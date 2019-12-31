package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDispatchMode;
import com.seater.smartmining.entity.ProjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:15
 */
public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Long>, JpaSpecificationExecutor<ProjectSchedule> {

    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);

    void deleteByGroupCode(String groupCode);

    List<ProjectSchedule> getAllByProjectId(Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_schedule where project_id = ?1")
    void deleteAll(Long projectId);

    @Query(nativeQuery = true, value = "select * from project_schedule where project_id = ?1 and manager_id like %?2% limit ?3,?4")
    List<ProjectSchedule> getAllByProjectIdAndManagerId(Long projectId, String managerId, Integer current, Integer pageSize);

    @Query(nativeQuery = true, value = "select * from project_schedule where project_id = ?1 and manager_id like %?2%")
    List<ProjectSchedule> getAllByProjectIdAndManagerIdOrderById(Long projectId, String managerId);

    @Query(nativeQuery = true, value = "select * from project_schedule where project_id = ?1 and group_code = ?2")
    ProjectSchedule getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    List<ProjectSchedule> findByProjectIdAndGroupCodeAndDispatchMode(Long projectId, String groupCode, ProjectDispatchMode dispatchMode);
    List<ProjectSchedule> findByProjectIdAndDispatchMode(Long projectId, ProjectDispatchMode dispatchMode);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	manager_id,\n" +
            "	manager_name \n" +
            "FROM\n" +
            "	project_schedule \n" +
            "WHERE\n" +
            "	project_id = ?1 \n" +
            "GROUP BY\n" +
            "	manager_id,\n" +
            "	manager_name")
    List<Map> getAllDistinctByProjectId(Long projectId);
}
