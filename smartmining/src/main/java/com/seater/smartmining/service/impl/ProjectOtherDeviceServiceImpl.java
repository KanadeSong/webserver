package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectOtherDeviceDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.service.ProjectOtherDeviceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectOtherDeviceServiceImpl implements ProjectOtherDeviceServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectOtherDeviceDaoI projectOtherDeviceDaoI;

    @Override
    public ProjectOtherDevice get(Long id) throws IOException {
        return projectOtherDeviceDaoI.get(id);
    }

    @Override
    public ProjectOtherDevice save(ProjectOtherDevice log) throws IOException{
        return projectOtherDeviceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectOtherDeviceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectOtherDeviceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectOtherDevice> query(Pageable pageable) {
        return projectOtherDeviceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectOtherDevice> query() {
        return projectOtherDeviceDaoI.query();
    }

    @Override
    public Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec) {
        return projectOtherDeviceDaoI.query(spec);
    }

    @Override
    public Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec, Pageable pageable) {
        return projectOtherDeviceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectOtherDevice> getAll() {
        return projectOtherDeviceDaoI.getAll();
    }

    @Override
    public List<ProjectOtherDevice> queryWx(Specification<ProjectOtherDevice> spec) {
        return projectOtherDeviceDaoI.queryWx(spec);
    }

    @Override
    public ProjectOtherDevice getAllByUid(String uid) {
        return projectOtherDeviceDaoI.getAllByUid(uid);
    }

    @Override
    public List<ProjectOtherDevice> getByProjectIdAndCarTypeIs(Long projectId, CarType carType) {
        return projectOtherDeviceDaoI.getByProjectIdAndCarTypeIs(projectId, carType);
    }

    @Override
    public void saveAll(List<ProjectOtherDevice> projectOtherDeviceList) {
        projectOtherDeviceDaoI.saveAll(projectOtherDeviceList);
    }

    @Override
    public ProjectOtherDevice getAllByProjectIdAndCodeAndCarType(Long projectId, String code, CarType carType) {
        return projectOtherDeviceDaoI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType);
    }

    @Override
    public List<ProjectOtherDevice> getAllByProjectId(Long projectId) {
        return projectOtherDeviceDaoI.getAllByProjectId(projectId);
    }
}
