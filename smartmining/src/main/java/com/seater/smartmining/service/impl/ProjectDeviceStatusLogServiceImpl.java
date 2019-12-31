package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDeviceStatusLogDaoI;
import com.seater.smartmining.entity.ProjectDeviceStatusLog;
import com.seater.smartmining.service.ProjectDeviceStatusLogServiceI;
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
 * @Date 2019/11/21 0021 15:15
 */
@Service
public class ProjectDeviceStatusLogServiceImpl implements ProjectDeviceStatusLogServiceI {

    @Autowired
    private ProjectDeviceStatusLogDaoI projectDeviceStatusLogDaoI;

    @Override
    public ProjectDeviceStatusLog get(Long id) throws IOException {
        return projectDeviceStatusLogDaoI.get(id);
    }

    @Override
    public ProjectDeviceStatusLog save(ProjectDeviceStatusLog log) throws IOException {
        return projectDeviceStatusLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDeviceStatusLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDeviceStatusLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDeviceStatusLog> query() {
        return projectDeviceStatusLogDaoI.query();
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec) {
        return projectDeviceStatusLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Pageable pageable) {
        return projectDeviceStatusLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec, Pageable pageable) {
        return projectDeviceStatusLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDeviceStatusLog> getAll() {
        return projectDeviceStatusLogDaoI.getAll();
    }

    @Override
    public ProjectDeviceStatusLog getAllByUid(String uid) {
        return projectDeviceStatusLogDaoI.getAllByUid(uid);
    }

    @Override
    public List<ProjectDeviceStatusLog> getAllByUnlineTime() {
        return projectDeviceStatusLogDaoI.getAllByUnlineTime();
    }
}
