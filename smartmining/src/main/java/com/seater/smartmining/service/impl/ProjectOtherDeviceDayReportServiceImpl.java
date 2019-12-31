package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectOtherDeviceDayReportDaoI;
import com.seater.smartmining.entity.ProjectOtherDeviceDayReport;
import com.seater.smartmining.service.ProjectOtherDeviceDayReportServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/10/19 0019 13:02
 */
@Service
public class ProjectOtherDeviceDayReportServiceImpl implements ProjectOtherDeviceDayReportServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectOtherDeviceDayReportDaoI projectOtherDeviceDayReportDaoI;

    @Override
    public ProjectOtherDeviceDayReport get(Long id) throws IOException {
        return projectOtherDeviceDayReportDaoI.get(id);
    }

    @Override
    public ProjectOtherDeviceDayReport save(ProjectOtherDeviceDayReport log) throws IOException {
        return projectOtherDeviceDayReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectOtherDeviceDayReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectOtherDeviceDayReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query() {
        return projectOtherDeviceDayReportDaoI.query();
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec) {
        return projectOtherDeviceDayReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Pageable pageable) {
        return projectOtherDeviceDayReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectOtherDeviceDayReport> query(Specification<ProjectOtherDeviceDayReport> spec, Pageable pageable) {
        return projectOtherDeviceDayReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectOtherDeviceDayReport> getAll() {
        return projectOtherDeviceDayReportDaoI.getAll();
    }
}
