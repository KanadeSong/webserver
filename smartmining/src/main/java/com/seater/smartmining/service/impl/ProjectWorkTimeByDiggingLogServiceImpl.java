package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectWorkTimeByDiggingLogDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeByDiggingLog;
import com.seater.smartmining.service.ProjectWorkTimeByDiggingLogServiceI;
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
 * @Date 2019/11/23 0023 16:01
 */
@Service
public class ProjectWorkTimeByDiggingLogServiceImpl implements ProjectWorkTimeByDiggingLogServiceI {
    @Autowired
    private ProjectWorkTimeByDiggingLogDaoI projectWorkTimeByDiggingLogDaoI;

    @Override
    public ProjectWorkTimeByDiggingLog get(Long id) throws IOException {
        return projectWorkTimeByDiggingLogDaoI.get(id);
    }

    @Override
    public ProjectWorkTimeByDiggingLog save(ProjectWorkTimeByDiggingLog log) throws IOException {
        return projectWorkTimeByDiggingLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectWorkTimeByDiggingLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectWorkTimeByDiggingLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query() {
        return projectWorkTimeByDiggingLogDaoI.query();
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec) {
        return projectWorkTimeByDiggingLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Pageable pageable) {
        return projectWorkTimeByDiggingLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec, Pageable pageable) {
        return projectWorkTimeByDiggingLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectWorkTimeByDiggingLog> getAll() {
        return projectWorkTimeByDiggingLogDaoI.getAll();
    }

    @Override
    public List<ProjectWorkTimeByDiggingLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectWorkTimeByDiggingLogDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
