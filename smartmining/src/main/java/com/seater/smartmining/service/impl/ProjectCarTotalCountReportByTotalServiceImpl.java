package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarTotalCountReportByTotalDaoI;
import com.seater.smartmining.entity.ProjectCarTotalCountReportByTotal;
import com.seater.smartmining.service.ProjectCarTotalCountReportByTotalServiceI;
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
 * @Date 2019/11/22 0022 17:17
 */
@Service
public class ProjectCarTotalCountReportByTotalServiceImpl implements ProjectCarTotalCountReportByTotalServiceI {

    @Autowired
    private ProjectCarTotalCountReportByTotalDaoI projectCarTotalCountReportByTotalDaoI;

    @Override
    public ProjectCarTotalCountReportByTotal get(Long id) throws IOException {
        return projectCarTotalCountReportByTotalDaoI.get(id);
    }

    @Override
    public ProjectCarTotalCountReportByTotal save(ProjectCarTotalCountReportByTotal log) throws IOException {
        return projectCarTotalCountReportByTotalDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarTotalCountReportByTotalDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarTotalCountReportByTotalDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query() {
        return projectCarTotalCountReportByTotalDaoI.query();
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec) {
        return projectCarTotalCountReportByTotalDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Pageable pageable) {
        return projectCarTotalCountReportByTotalDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec, Pageable pageable) {
        return projectCarTotalCountReportByTotalDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> queryAll(Specification<ProjectCarTotalCountReportByTotal> spec) {
        return projectCarTotalCountReportByTotalDaoI.queryAll(spec);
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> getAll() {
        return projectCarTotalCountReportByTotalDaoI.getAll();
    }

    @Override
    public List<ProjectCarTotalCountReportByTotal> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectCarTotalCountReportByTotalDaoI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectCarTotalCountReportByTotalDaoI.deleteByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }
}
