package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ScheduleMachineModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 11:05
 */
public interface ScheduleMachineModelDaoI {

    ScheduleMachineModel get(Long id) throws IOException;
    ScheduleMachineModel save(ScheduleMachineModel log) throws JsonProcessingException;
    void delete(Long id);
    Page<ScheduleMachineModel> query();
    Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec);
    Page<ScheduleMachineModel> query(Pageable pageable);
    Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec, Pageable pageable);
    List<ScheduleMachineModel> getAll();
    List<ScheduleMachineModel> getAllByProjectId(Long projectId);
    List<ScheduleMachineModel> getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(Long projectId, String groupCode, boolean valid);
    void deleteByProjectIdAndMachineCode(Long projectId, String machineCode);
    void deleteByProjectIdAndMachineCodeListAndProgrammeId(Long projectId, List<String> machineList, Long programmeId);
    List<String> getGroupCodeList();
    void deleteByGroupCode(String groupCode);
    void batchSave(List<ScheduleMachineModel> saveList);
    void deleteByGroupCodes(List<String> groupCodes);
    List<ScheduleMachineModel> queryByParams(Specification<ScheduleMachineModel> spec);
}
