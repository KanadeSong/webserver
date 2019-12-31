package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ScheduleMachine;
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
 * @Date 2019/5/23 0023 14:31
 */
public interface ScheduleMachineRepository extends JpaRepository<ScheduleMachine, Long>, JpaSpecificationExecutor<ScheduleMachine> {

    List<ScheduleMachine> getAllByProjectId(Long projectId);

    List<ScheduleMachine> getAllByProjectIdAndMachineIdAndIsVaild(Long projectId, Long machineId, boolean flag);

    @Query(nativeQuery = true, value = "select * from schedule_machine where project_id = ?1 and group_code = ?2 and is_vaild = true")
    List<ScheduleMachine> getAllByProjectIdAndGroupCode(Long projectId, String groupCode);

    @Transactional
    void deleteByProjectIdAndMachineCode(Long projectId, String machineCode);

    @Transactional
    void deleteByGroupCode(String groupCode);

    @Transactional
    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_machine where project_id = ?1")
    void deleteByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select * from schedule_machine where project_id = ?1 and machine_code = ?2 and is_vaild is true")
    ScheduleMachine getByProjectIdAndMachineCode(Long projectId, String machineCode);

    @Query(nativeQuery = true, value = "select distinct group_code from schedule_machine")
    List<String> getGroupCodeList();

    @Query(nativeQuery = true, value = "select machine_code from schedule_machine" +
            " where project_id = ?1 and is_vaild = ?2")
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	c.* \n" +
            "FROM\n" +
            "	schedule_machine c,\n" +
            "	project_schedule s \n" +
            "WHERE\n" +
            "	s.group_code = c.group_code \n" +
            "	AND s.project_id = ?1 \n" +
            "	AND c.is_vaild = TRUE")
    List<ScheduleMachine> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_machine where project_id = ?1 and machine_code in ?2")
    void  deleteByProjectIdAndMachineCodeList(Long projectId, List<String> machineList);
}
