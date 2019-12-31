package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingReportByPlaceDaoI;
import com.seater.smartmining.entity.ProjectDiggingReportByPlace;
import com.seater.smartmining.service.ProjectDiggingReportByPlaceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/19 0019 11:27
 */
@Service
public class ProjectDiggingReportByPlaceServiceImpl implements ProjectDiggingReportByPlaceServiceI {

    @Autowired
    private ProjectDiggingReportByPlaceDaoI projectDiggingReportByPlaceDaoI;

    @Override
    public ProjectDiggingReportByPlace get(Long id) throws IOException {
        return projectDiggingReportByPlaceDaoI.get(id);
    }

    @Override
    public ProjectDiggingReportByPlace save(ProjectDiggingReportByPlace log) throws IOException {
        return projectDiggingReportByPlaceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingReportByPlaceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingReportByPlaceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query() {
        return projectDiggingReportByPlaceDaoI.query();
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec) {
        return projectDiggingReportByPlaceDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Pageable pageable) {
        return projectDiggingReportByPlaceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec, Pageable pageable) {
        return projectDiggingReportByPlaceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAll() {
        return projectDiggingReportByPlaceDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectDiggingReportByPlace> placeList) {
        projectDiggingReportByPlaceDaoI.batchSave(placeList);
    }

    @Override
    public void deleteByProjectIdAndAndDateIdentification(Long projectId, Date date) {
        projectDiggingReportByPlaceDaoI.deleteByProjectIdAndAndDateIdentification(projectId, date);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectDiggingReportByPlaceDaoI.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime, String machineCode) {
        return projectDiggingReportByPlaceDaoI.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime, machineCode);
    }
}
