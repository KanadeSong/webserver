package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDeviceDaoI;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.service.ProjectDeviceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectDeviceServiceImpl implements ProjectDeviceServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDeviceDaoI projectDeviceDaoI;

    @Override
    public ProjectDevice get(Long id) throws IOException {
        return projectDeviceDaoI.get(id);
    }

    @Override
    public ProjectDevice save(ProjectDevice log) throws IOException{
        return projectDeviceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDeviceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDeviceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDevice> query(Pageable pageable) {
        return projectDeviceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDevice> query() {
        return projectDeviceDaoI.query();
    }

    @Override
    public Page<ProjectDevice> query(Specification<ProjectDevice> spec) {
        return projectDeviceDaoI.query(spec);
    }

    @Override
    public Page<ProjectDevice> query(Specification<ProjectDevice> spec, Pageable pageable) {
        return projectDeviceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDevice> getAll() {
        return projectDeviceDaoI.getAll();
    }

    @Override
    public ProjectDevice getByProjectIdAndUid(Long projectId, String uid) {
        return projectDeviceDaoI.getByProjectIdAndUid(projectId, uid);
    }

    @Override
    public ProjectDevice getByUid(String uid) {
        return projectDeviceDaoI.getByUid(uid);
    }

    @Override
    public ProjectDevice getAllByProjectIdAndCodeAndDeviceType(Long projectId, String code, Integer type) {
        return projectDeviceDaoI.getAllByProjectIdAndCodeAndDeviceType(projectId, code, type);
    }

    @Override
    public List<ProjectDevice> getAllByAndFileName(String fileName) {
        return projectDeviceDaoI.getAllByAndFileName(fileName);
    }

    @Override
    public List<ProjectDevice> getAllByCodeAndDeviceType(String code, Integer deviceType) {
        return projectDeviceDaoI.getAllByCodeAndDeviceType(code, deviceType);
    }

    @Override
    public ProjectDevice getAllByDeviceCode(String deviceCode) {
        return projectDeviceDaoI.getAllByDeviceCode(deviceCode);
    }

    @Override
    public List<ProjectDevice> getAllByProjectId(Long projectId) {
        return projectDeviceDaoI.getAllByProjectId(projectId);
    }

    @Override
    public void batchSave(List<ProjectDevice> projectDeviceList) {
        projectDeviceDaoI.batchSave(projectDeviceList);
    }

    @Override
    public List<ProjectDevice> getAllByProjectIdAndDeviceType(Long projectId, Integer deviceType) {
        return projectDeviceDaoI.getAllByProjectIdAndDeviceType(projectId, deviceType);
    }

    @Override
    public List<ProjectDevice> getAllCarDeviceAndMachineDevice(Long projectId) {
        return projectDeviceDaoI.getAllCarDeviceAndMachineDevice(projectId);
    }

}
