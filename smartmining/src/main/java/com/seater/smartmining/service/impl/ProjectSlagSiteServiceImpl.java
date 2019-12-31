package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectSlagSiteDaoI;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.service.ProjectSlagSiteServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectSlagSiteServiceImpl implements ProjectSlagSiteServiceI {
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ProjectSlagSiteDaoI projectSlagSiteDaoI;

    @Override
    public ProjectSlagSite get(Long id) throws IOException {
        return projectSlagSiteDaoI.get(id);
    }

    @Override
    public ProjectSlagSite save(ProjectSlagSite log) throws IOException{
        return projectSlagSiteDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectSlagSiteDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectSlagSiteDaoI.delete(ids);
    }

    @Override
    public Page<ProjectSlagSite> query(Pageable pageable) {
        return projectSlagSiteDaoI.query(pageable);
    }

    @Override
    public Page<ProjectSlagSite> query() {
        return projectSlagSiteDaoI.query();
    }

    @Override
    public Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec) {
        return projectSlagSiteDaoI.query(spec);
    }

    @Override
    public Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec, Pageable pageable) {
        return projectSlagSiteDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectSlagSite> getAll() {
        return projectSlagSiteDaoI.getAll();
    }

    @Override
    public ProjectSlagSite getByProjectIdAndDeviceUid(Long projectId, String deviceUid) {
        return projectSlagSiteDaoI.getByProjectIdAndDeviceUid(projectId, deviceUid);
    }

    @Override
    public ProjectSlagSite getByProjectIdAndDistance(Long projectId, Long distance) {
        return projectSlagSiteDaoI.getByProjectIdAndDistance(projectId, distance);
    }

    @Override
    public List<ProjectSlagSite> getAllByProjectId(Long projectId) {
        return projectSlagSiteDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<ProjectSlagSite> getAllByProjectIdAndName(Long projectId, String name) {
        return projectSlagSiteDaoI.getAllByProjectIdAndName(projectId, name);
    }

    @Override
    public ProjectSlagSite getAllByProjectIdAndSlagSiteCode(Long projectId, String slagSiteCode) {
        return projectSlagSiteDaoI.getAllByProjectIdAndSlagSiteCode(projectId, slagSiteCode);
    }
}
