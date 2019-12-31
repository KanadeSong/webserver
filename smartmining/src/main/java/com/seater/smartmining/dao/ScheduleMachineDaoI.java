package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ScheduleMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:32
 */
public interface ScheduleMachineDaoI {

    ScheduleMachine get(Long id) throws IOException;
    ScheduleMachine save(ScheduleMachine log) throws JsonProcessingException;
    void delete(Long id);
    Page<ScheduleMachine> query();
    Page<ScheduleMachine> query(Specification<ScheduleMachine> spec);
    Page<ScheduleMachine> query(Pageable pageable);
    Page<ScheduleMachine> query(Specification<ScheduleMachine> spec, Pageable pageable);
    List<ScheduleMachine> getAll();
    List<ScheduleMachine> getAllByProjectId(Long projectId);
    List<ScheduleMachine> getAllByProjectIdAndMachineIdAndIsVaild(Long projectId, Long machineId, boolean flag);
    List<ScheduleMachine> getAllByProjectIdAndGroupCode(Long projectId, String groupCode);
    void deleteByProjectIdAndMachineCode(Long projectId, String machineCode);
    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);
    ScheduleMachine getByProjectIdAndMachineCode(Long projectId, String machineCode);
    List<String> getGroupCodeList();
    List<ScheduleMachine> getAllByQuery(Specification<ScheduleMachine> spec);
    void deleteByGroupCode(String groupCode);
    void batchSave(List<ScheduleMachine> scheduleMachineList);
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid);
    void deleteByProjectId(Long projectId);
    List<ScheduleMachine> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId);
    void  deleteByProjectIdAndMachineCodeList(Long projectId, List<String> machineList);
}
