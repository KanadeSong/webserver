package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingMachineDaoI;
import com.seater.smartmining.entity.ProjectDiggingMachine;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ProjectDiggingMachineServiceImpl implements ProjectDiggingMachineServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDiggingMachineDaoI projectDiggingMachineDaoI;

    @Override
    public ProjectDiggingMachine get(Long id) throws IOException {
        return projectDiggingMachineDaoI.get(id);
    }

    @Override
    public ProjectDiggingMachine save(ProjectDiggingMachine log) throws IOException{
        return projectDiggingMachineDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingMachineDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingMachineDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingMachine> query(Pageable pageable) {
        return projectDiggingMachineDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingMachine> query() {
        return projectDiggingMachineDaoI.query();
    }

    @Override
    public Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec) {
        return projectDiggingMachineDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec, Pageable pageable) {
        return projectDiggingMachineDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingMachine> queryWx(Specification<ProjectDiggingMachine> spec) {
        return projectDiggingMachineDaoI.queryWx(spec);
    }

    @Override
    public List<ProjectDiggingMachine> getAll() {
        return projectDiggingMachineDaoI.getAll();
    }

    @Override
    public List<ProjectDiggingMachine> getByProjectIdOrderById(Long projectId) {
        return projectDiggingMachineDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public List<ProjectDiggingMachine> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectDiggingMachineDaoI.getByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public ProjectDiggingMachine getByProjectIdAndCode(Long projectId, String code) {
        List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineDaoI.getByProjectIdAndCode(projectId, code);
        if(projectDiggingMachineList.size()>0){
            return projectDiggingMachineDaoI.getByProjectIdAndCode(projectId, code).get(0);
        }
        return null;
    }

    @Override
    public ProjectDiggingMachine getByProjectIdAndUid(Long projectId, String uid) {
        return projectDiggingMachineDaoI.getByProjectIdAndUid(projectId, uid);
    }

    @Override
    public ProjectDiggingMachine getAllByUid(String uid) {
        return projectDiggingMachineDaoI.getAllByUid(uid);
    }

    @Override
    public Map getAllCountByProjectId(Long projectId) {
        return projectDiggingMachineDaoI.getAllCountByProjectId(projectId);
    }

    @Override
    public void setICCardByDiggingMachineId(Long diggingMachineId, String icCardNumber, Boolean icCardStatus) {
        projectDiggingMachineDaoI.setICCardByDiggingMachineId(diggingMachineId, icCardNumber, icCardStatus);
    }

    @Override
    public List<ProjectDiggingMachine> getAllByProjectIdAndIsVaildAndSelected(Long projectId, Boolean selected) {
        return projectDiggingMachineDaoI.getAllByProjectIdAndIsVaildAndSelected(projectId, selected);
    }

    @Override
    public void batchSave(List<ProjectDiggingMachine> projectDiggingMachineList) {
        projectDiggingMachineDaoI.batchSave(projectDiggingMachineList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectDiggingMachineDaoI.getAllByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public void updateSeleted(boolean selected, List<String> machineCodeList) {
        projectDiggingMachineDaoI.updateSeleted(selected, machineCodeList);
    }
}
