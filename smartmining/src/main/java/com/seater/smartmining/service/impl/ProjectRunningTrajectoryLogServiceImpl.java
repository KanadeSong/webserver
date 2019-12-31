package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectRunningTrajectoryLogDaoI;
import com.seater.smartmining.entity.ProjectRunningTrajectoryLog;
import com.seater.smartmining.service.ProjectRunningTrajectoryLogServiceI;
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
 * @Date 2019/12/17 0017 9:41
 */
@Service
public class ProjectRunningTrajectoryLogServiceImpl implements ProjectRunningTrajectoryLogServiceI {

    @Autowired
    private ProjectRunningTrajectoryLogDaoI projectRunningTrajectoryLogDaoI;

    @Override
    public ProjectRunningTrajectoryLog get(Long id) throws IOException {
        return projectRunningTrajectoryLogDaoI.get(id);
    }

    @Override
    public ProjectRunningTrajectoryLog save(ProjectRunningTrajectoryLog log) throws IOException {
        return projectRunningTrajectoryLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectRunningTrajectoryLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectRunningTrajectoryLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query() {
        return projectRunningTrajectoryLogDaoI.query();
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec) {
        return projectRunningTrajectoryLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Pageable pageable) {
        return projectRunningTrajectoryLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec, Pageable pageable) {
        return projectRunningTrajectoryLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectRunningTrajectoryLog> getAll() {
        return projectRunningTrajectoryLogDaoI.getAll();
    }

    @Override
    public void saveAll(List<ProjectRunningTrajectoryLog> saveList) {
        projectRunningTrajectoryLogDaoI.saveAll(saveList);
    }
}
