package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectUserTrajectoryLogDaoI;
import com.seater.smartmining.entity.ProjectUserTrajectoryLog;
import com.seater.smartmining.service.ProjectUserTrajectoryLogServiceI;
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
 * @Date 2019/12/18 0018 15:37
 */
@Service
public class ProjectUserTrajectoryLogServiceImpl implements ProjectUserTrajectoryLogServiceI {

    @Autowired
    private ProjectUserTrajectoryLogDaoI projectUserTrajectoryLogDaoI;

    @Override
    public ProjectUserTrajectoryLog get(Long id) throws IOException {
        return projectUserTrajectoryLogDaoI.get(id);
    }

    @Override
    public ProjectUserTrajectoryLog save(ProjectUserTrajectoryLog log) throws IOException {
        return projectUserTrajectoryLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectUserTrajectoryLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectUserTrajectoryLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query() {
        return projectUserTrajectoryLogDaoI.query();
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec) {
        return projectUserTrajectoryLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Pageable pageable) {
        return projectUserTrajectoryLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec, Pageable pageable) {
        return projectUserTrajectoryLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectUserTrajectoryLog> getAll() {
        return projectUserTrajectoryLogDaoI.getAll();
    }

    @Override
    public void saveAll(List<ProjectUserTrajectoryLog> saveList) {
        projectUserTrajectoryLogDaoI.saveAll(saveList);
    }
}
