package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDeviceElectrifyLogDaoI;
import com.seater.smartmining.entity.ProjectDeviceElectrifyLog;
import com.seater.smartmining.service.ProjectDeviceElectrifyLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/12/10 0010 16:48
 */
@Service
public class ProjectDeviceElectrifyLogServiceImpl implements ProjectDeviceElectrifyLogServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDeviceElectrifyLogDaoI projectDeviceElectrifyLogDaoI;

    @Override
    public ProjectDeviceElectrifyLog get(Long id) throws IOException {
        return projectDeviceElectrifyLogDaoI.get(id);
    }

    @Override
    public ProjectDeviceElectrifyLog save(ProjectDeviceElectrifyLog log) throws IOException {
        return projectDeviceElectrifyLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDeviceElectrifyLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDeviceElectrifyLogDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query() {
        return projectDeviceElectrifyLogDaoI.query();
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec) {
        return projectDeviceElectrifyLogDaoI.query(spec);
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Pageable pageable) {
        return projectDeviceElectrifyLogDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec, Pageable pageable) {
        return projectDeviceElectrifyLogDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDeviceElectrifyLog> getAll() {
        return projectDeviceElectrifyLogDaoI.getAll();
    }

    @Override
    public ProjectDeviceElectrifyLog getAllByProjectIdAndUidElectrifyTime(Long projectId, String carCode, Date date, Integer deviceType) {
        return projectDeviceElectrifyLogDaoI.getAllByProjectIdAndUidElectrifyTime(projectId, carCode, date, deviceType);
    }
}
