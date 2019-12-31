package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ProjectDiggingMachineRepository  extends JpaRepository<ProjectDiggingMachine, Long>, JpaSpecificationExecutor<ProjectDiggingMachine> {
    @Query(nativeQuery = true, value = "select * from project_digging_machine where project_id = ?1 and is_vaild = true")
    List<ProjectDiggingMachine> getByProjectIdOrderById(Long projectId);

    List<ProjectDiggingMachine> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild);

    List<ProjectDiggingMachine> getByProjectIdAndCode(Long projectId, String code);

    ProjectDiggingMachine getByProjectIdAndUid(Long projectId, String uid);

    List<ProjectDiggingMachine> getById(Long id);

    ProjectDiggingMachine getAllByUid(String uid);

    @Query(nativeQuery = true, value = "select count(*) as count from project_digging_machine where project_id = ?1 and is_vaild = true")
    Map getAllCountByProjectId(Long projectId);

    @Modifying
    @Query("update ProjectDiggingMachine set icCardNumber = ?2, icCardStatus = ?3 where id = ?1")
    void setICCardByDiggingMachineId(Long diggingMachineId, String icCardNumber, Boolean icCardStatus);

    @Query(nativeQuery = true, value = "select * from project_digging_machine where project_id = ?1 and is_vaild = true and selected = ?2")
    List<ProjectDiggingMachine> getAllByProjectIdAndIsVaildAndSelected(Long projectId, Boolean selected);

    @Query(nativeQuery = true, value = "select code from project_digging_machine where project_id = ?1 and is_vaild = ?2")
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isVaild);

    @Modifying
    @Query("update ProjectDiggingMachine p set p.selected = ?1 where p.code in ?2")
    void updateSeleted(boolean selected, List<String> machineCodeList);
}
