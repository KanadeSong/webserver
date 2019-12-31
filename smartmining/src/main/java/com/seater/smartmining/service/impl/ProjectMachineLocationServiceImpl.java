package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectMachineLocationDaoI;
import com.seater.smartmining.entity.ProjectMachineLocation;
import com.seater.smartmining.service.ProjectMachineLocationServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 21:11
 */
@Service
public class ProjectMachineLocationServiceImpl implements ProjectMachineLocationServiceI {
    @Autowired
    private ProjectMachineLocationDaoI projectMachineLocationDaoI;

    @Override
    public ProjectMachineLocation get(Long id) throws IOException {
        return projectMachineLocationDaoI.get(id);
    }

    @Override
    public ProjectMachineLocation save(ProjectMachineLocation log) throws IOException {
        return projectMachineLocationDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectMachineLocationDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectMachineLocationDaoI.delete(ids);
    }

    @Override
    public Page<ProjectMachineLocation> query() {
        return projectMachineLocationDaoI.query();
    }

    @Override
    public Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec) {
        return projectMachineLocationDaoI.query(spec);
    }

    @Override
    public Page<ProjectMachineLocation> query(Pageable pageable) {
        return projectMachineLocationDaoI.query(pageable);
    }

    @Override
    public Page<ProjectMachineLocation> query(Specification<ProjectMachineLocation> spec, Pageable pageable) {
        return projectMachineLocationDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectMachineLocation> getAll() {
        return projectMachineLocationDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectMachineLocation> saveList) {
        projectMachineLocationDaoI.batchSave(saveList);
    }

    @Override
    public List<ProjectMachineLocation> getAllByProjectIdAndCarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMachineLocationDaoI.getAllByProjectIdAndCarCodeAndCreateTime(projectId, carCode, startTime, endTime);
    }
}
