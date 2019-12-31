package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectOtherDeviceWorkInfoDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.ProjectOtherDeviceWorkInfo;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
import com.seater.smartmining.service.ProjectOtherDeviceWorkInfoServiceI;
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
 * @Date 2019/10/10 0010 16:02
 */
@Service
public class ProjectOtherDeviceWorkInfoServiceImpl implements ProjectOtherDeviceWorkInfoServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectOtherDeviceWorkInfoDaoI projectOtherDeviceWorkInfoDaoI;

    @Override
    public ProjectOtherDeviceWorkInfo get(Long id) throws IOException {
        return projectOtherDeviceWorkInfoDaoI.get(id);
    }

    @Override
    public ProjectOtherDeviceWorkInfo save(ProjectOtherDeviceWorkInfo log) throws IOException {
        return projectOtherDeviceWorkInfoDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectOtherDeviceWorkInfoDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectOtherDeviceWorkInfoDaoI.delete(ids);
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query() {
        return projectOtherDeviceWorkInfoDaoI.query();
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec) {
        return projectOtherDeviceWorkInfoDaoI.query(spec);
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Pageable pageable) {
        return projectOtherDeviceWorkInfoDaoI.query(pageable);
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec, Pageable pageable) {
        return projectOtherDeviceWorkInfoDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectOtherDeviceWorkInfo> getAll() {
        return projectOtherDeviceWorkInfoDaoI.getAll();
    }

    @Override
    public ProjectOtherDeviceWorkInfo getAllByProjectIdAndCodeAndCarType(Long projectId, String code, Integer carType, Integer status) {
        return projectOtherDeviceWorkInfoDaoI.getAllByProjectIdAndCodeAndCarType(projectId, code, carType, status);
    }

    @Override
    public List<Map> getDayReportByProjectIdAndDateIdentificationAndCarType(Long projectId, Date date, Integer carType) {
        return projectOtherDeviceWorkInfoDaoI.getDayReportByProjectIdAndDateIdentificationAndCarType(projectId, date, carType);
    }
}
