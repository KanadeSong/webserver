package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectAppStatisticsByMachineDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsByMachine;
import com.seater.smartmining.service.ProjectAppStatisticsByMachineServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 12:59
 */
@Service
public class ProjectAppStatisticsByMachineServiceImpl implements ProjectAppStatisticsByMachineServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectAppStatisticsByMachineDaoI projectAppStatisticsByMachineDaoI;

    @Override
    public ProjectAppStatisticsByMachine get(Long id) throws IOException {
        return projectAppStatisticsByMachineDaoI.get(id);
    }

    @Override
    public ProjectAppStatisticsByMachine save(ProjectAppStatisticsByMachine log) throws JsonProcessingException {
        return projectAppStatisticsByMachineDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectAppStatisticsByMachineDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectAppStatisticsByMachineDaoI.delete(ids);
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query() {
        return projectAppStatisticsByMachineDaoI.query();
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec) {
        return projectAppStatisticsByMachineDaoI.query(spec);
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Pageable pageable) {
        return projectAppStatisticsByMachineDaoI.query(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec, Pageable pageable) {
        return projectAppStatisticsByMachineDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsByMachine> getAll() {
        return projectAppStatisticsByMachineDaoI.getAll();
    }

    @Override
    public void deleteByCreateDate(Date createDate, Long projectId) {
        projectAppStatisticsByMachineDaoI.deleteByCreateDate(createDate, projectId);
    }

    @Override
    public ProjectAppStatisticsByMachine getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date, String machineCode) {
        return projectAppStatisticsByMachineDaoI.getAllByProjectIdAndShiftsAndCreateDate(projectId, value, date, machineCode);
    }

    @Override
    public List<ProjectAppStatisticsByMachine> getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date) {
        return projectAppStatisticsByMachineDaoI.getAllByProjectIdAndShiftsAndCreateDate(projectId, value, date);
    }

    @Override
    public void batchSave(List<ProjectAppStatisticsByMachine> saveList) {
        projectAppStatisticsByMachineDaoI.batchSave(saveList);
    }
}
