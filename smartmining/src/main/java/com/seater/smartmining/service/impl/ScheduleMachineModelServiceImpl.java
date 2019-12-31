package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ScheduleMachineModelDaoI;
import com.seater.smartmining.entity.ScheduleMachineModel;
import com.seater.smartmining.service.ScheduleMachineModelServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 11:11
 */
@Service
public class ScheduleMachineModelServiceImpl implements ScheduleMachineModelServiceI {

    @Autowired
    private ScheduleMachineModelDaoI scheduleMachineModelDaoI;

    @Override
    public ScheduleMachineModel get(Long id) throws IOException {
        return scheduleMachineModelDaoI.get(id);
    }

    @Override
    public ScheduleMachineModel save(ScheduleMachineModel log) throws JsonProcessingException {
        return scheduleMachineModelDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        scheduleMachineModelDaoI.delete(id);
    }

    @Override
    public Page<ScheduleMachineModel> query() {
        return scheduleMachineModelDaoI.query();
    }

    @Override
    public Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec) {
        return scheduleMachineModelDaoI.query(spec);
    }

    @Override
    public Page<ScheduleMachineModel> query(Pageable pageable) {
        return scheduleMachineModelDaoI.query(pageable);
    }

    @Override
    public Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec, Pageable pageable) {
        return scheduleMachineModelDaoI.query(spec, pageable);
    }

    @Override
    public List<ScheduleMachineModel> getAll() {
        return scheduleMachineModelDaoI.getAll();
    }

    @Override
    public List<ScheduleMachineModel> getAllByProjectId(Long projectId) {
        return scheduleMachineModelDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachineModel> getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(Long projectId, String groupCode, boolean valid) {
        return scheduleMachineModelDaoI.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, groupCode, valid);
    }

    @Override
    public void deleteByProjectIdAndMachineCode(Long projectId, String machineCode) {
        scheduleMachineModelDaoI.deleteByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public void deleteByProjectIdAndMachineCodeListAndProgrammeId(Long projectId, List<String> machineList, Long programmeId) {
        scheduleMachineModelDaoI.deleteByProjectIdAndMachineCodeListAndProgrammeId(projectId, machineList, programmeId);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleMachineModelDaoI.getGroupCodeList();
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleMachineModelDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleMachineModel> saveList) {
        scheduleMachineModelDaoI.batchSave(saveList);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        scheduleMachineModelDaoI.deleteByGroupCodes(groupCodes);
    }

    @Override
    public List<ScheduleMachineModel> queryByParams(Specification<ScheduleMachineModel> spec) {
        return scheduleMachineModelDaoI.queryByParams(spec);
    }
}
