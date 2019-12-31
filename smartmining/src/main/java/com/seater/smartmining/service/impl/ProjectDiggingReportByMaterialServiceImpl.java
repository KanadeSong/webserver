package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDiggingReportByMaterialDaoI;
import com.seater.smartmining.entity.ProjectDiggingReportByMaterial;
import com.seater.smartmining.service.ProjectDiggingReportByMaterialServiceI;
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
 * @Date 2019/8/20 0020 11:48
 */
@Service
public class ProjectDiggingReportByMaterialServiceImpl implements ProjectDiggingReportByMaterialServiceI {

    @Autowired
    private ProjectDiggingReportByMaterialDaoI projectDiggingReportByMaterialDaoI;

    @Override
    public ProjectDiggingReportByMaterial get(Long id) throws IOException {
        return projectDiggingReportByMaterialDaoI.get(id);
    }

    @Override
    public ProjectDiggingReportByMaterial save(ProjectDiggingReportByMaterial log) throws IOException {
        return projectDiggingReportByMaterialDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDiggingReportByMaterialDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDiggingReportByMaterialDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query() {
        return projectDiggingReportByMaterialDaoI.query();
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec) {
        return projectDiggingReportByMaterialDaoI.query(spec);
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Pageable pageable) {
        return projectDiggingReportByMaterialDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec, Pageable pageable) {
        return projectDiggingReportByMaterialDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDiggingReportByMaterial> getAll() {
        return projectDiggingReportByMaterialDaoI.getAll();
    }

    @Override
    public void batchSave(List<ProjectDiggingReportByMaterial> materialList) {
        projectDiggingReportByMaterialDaoI.batchSave(materialList);
    }

    @Override
    public void deleteByProjectIdAndDateIdentification(Long projectId, Date date) {
        projectDiggingReportByMaterialDaoI.deleteByProjectIdAndDateIdentification(projectId, date);
    }
}
