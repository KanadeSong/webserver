package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarTotalCountReportDaoI;
import com.seater.smartmining.entity.ProjectCarTotalCountReport;
import com.seater.smartmining.service.ProjectCarTotalCountReportServiceI;
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
 * @Date 2019/11/19 0019 11:48
 */
@Service
public class ProjectCarTotalCountReportServiceImpl implements ProjectCarTotalCountReportServiceI {

    @Autowired
    private ProjectCarTotalCountReportDaoI projectCarTotalCountReportDaoI;

    @Override
    public ProjectCarTotalCountReport get(Long id) throws IOException {
        return projectCarTotalCountReportDaoI.get(id);
    }

    @Override
    public ProjectCarTotalCountReport save(ProjectCarTotalCountReport log) throws IOException {
        return projectCarTotalCountReportDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarTotalCountReportDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarTotalCountReportDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarTotalCountReport> query() {
        return projectCarTotalCountReportDaoI.query();
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec) {
        return projectCarTotalCountReportDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Pageable pageable) {
        return projectCarTotalCountReportDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec, Pageable pageable) {
        return projectCarTotalCountReportDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarTotalCountReport> getAll() {
        return projectCarTotalCountReportDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectCarTotalCountReport> reportList) {
        projectCarTotalCountReportDaoI.batchSave(reportList);
    }

    @Override
    public void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectCarTotalCountReportDaoI.deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
