package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ScheduleMachineModel;
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
public interface ScheduleMachineModelRepository extends JpaRepository<ScheduleMachineModel, Long>, JpaSpecificationExecutor<ScheduleMachineModel> {

    List<ScheduleMachineModel> getAllByProjectId(Long projectId);
    List<ScheduleMachineModel> getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(Long projectId, String groupCode, boolean valid);
    void deleteByProjectIdAndMachineCode(Long projectId, String machineCode);
    @Transactional
    void deleteByGroupCode(String groupCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_machine_model where project_id = ?1 and machine_code in ?2 and programme_id = ?3")
    void deleteByProjectIdAndMachineCodeListAndProgrammeId(Long projectId, List<String> machineList, Long programmeId);

    @Query(nativeQuery = true, value = "select distinct group_code from schedule_machine_model")
    List<String> getGroupCodeList();

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_machine_model where group_code in ?1")
    void deleteByGroupCodes(List<String> groupCodes);
}
