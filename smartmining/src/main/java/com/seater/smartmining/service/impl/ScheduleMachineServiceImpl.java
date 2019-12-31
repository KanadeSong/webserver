package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ScheduleMachineDaoI;
import com.seater.smartmining.entity.ScheduleMachine;
import com.seater.smartmining.service.ScheduleMachineServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:42
 */
@Service
public class ScheduleMachineServiceImpl implements ScheduleMachineServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ScheduleMachineDaoI scheduleMachineDaoI;

    @Override
    public ScheduleMachine get(Long id) throws IOException {
        return scheduleMachineDaoI.get(id);
    }

    @Override
    public ScheduleMachine save(ScheduleMachine log) throws JsonProcessingException {
        return scheduleMachineDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        scheduleMachineDaoI.delete(id);
    }

    @Override
    public Page<ScheduleMachine> query() {
        return scheduleMachineDaoI.query();
    }

    @Override
    public Page<ScheduleMachine> query(Specification<ScheduleMachine> spec) {
        return scheduleMachineDaoI.query(spec);
    }

    @Override
    public Page<ScheduleMachine> query(Pageable pageable) {
        return scheduleMachineDaoI.query(pageable);
    }

    @Override
    public Page<ScheduleMachine> query(Specification<ScheduleMachine> spec, Pageable pageable) {
        return scheduleMachineDaoI.query(spec, pageable);
    }

    @Override
    public List<ScheduleMachine> getAll() {
        return scheduleMachineDaoI.getAll();
    }

    @Override
    public List<ScheduleMachine> getAllByProjectId(Long projectId) {
        return scheduleMachineDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndMachineIdAndIsVaild(Long projectId, Long machineId, boolean flag) {
        return scheduleMachineDaoI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, flag);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return scheduleMachineDaoI.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public void deleteByProjectIdAndMachineCode(Long projectId, String machineCode) {
        scheduleMachineDaoI.deleteByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        scheduleMachineDaoI.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public ScheduleMachine getByProjectIdAndMachineCode(Long projectId, String machineCode) {
        return scheduleMachineDaoI.getByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleMachineDaoI.getGroupCodeList();
    }

    @Override
    public List<ScheduleMachine> getAllByQuery(Specification<ScheduleMachine> spec) {
        return scheduleMachineDaoI.getAllByQuery(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleMachineDaoI.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleMachine> scheduleMachineList) {
        scheduleMachineDaoI.batchSave(scheduleMachineList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid) {
        return scheduleMachineDaoI.getAllByProjectIdAndIsVaild(projectId, isValid);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        scheduleMachineDaoI.deleteByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId) {
        return scheduleMachineDaoI.getAllByProjectIdAndIsVaildAndInSchedule(projectId);
    }

    @Override
    public void deleteByProjectIdAndMachineCodeList(Long projectId, List<String> machineList) {
        scheduleMachineDaoI.deleteByProjectIdAndMachineCodeList(projectId, machineList);
    }
}
