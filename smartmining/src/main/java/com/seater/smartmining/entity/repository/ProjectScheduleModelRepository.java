package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:25
 */
public interface ProjectScheduleModelRepository extends JpaRepository<ProjectScheduleModel, Long>, JpaSpecificationExecutor<ProjectScheduleModel> {
    ProjectScheduleModel getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    @Transactional
    void deleteByGroupCode(String groupCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_schedule_model where group_code in ?1")
    void deleteByGroupCodes(List<String> groupCodes);

    List<ProjectScheduleModel> getAllByProjectId(Long projectId);

    List<ProjectScheduleModel> getAllByProjectIdAndProgrammeId(Long projectId, Long programmeId);
}
