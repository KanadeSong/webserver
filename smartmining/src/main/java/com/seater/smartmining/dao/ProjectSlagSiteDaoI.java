package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectSlagSite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectSlagSiteDaoI {
    ProjectSlagSite get(Long id) throws IOException;
    ProjectSlagSite save(ProjectSlagSite log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSlagSite> query();
    Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec);
    Page<ProjectSlagSite> query(Pageable pageable);
    Page<ProjectSlagSite> query(Specification<ProjectSlagSite> spec, Pageable pageable);
    List<ProjectSlagSite> getAll();
    ProjectSlagSite getByProjectIdAndDeviceUid(Long projectId, String deviceUid);
    ProjectSlagSite getByProjectIdAndDistance(Long projectId, Long distance);
    List<ProjectSlagSite> getAllByProjectId(Long projectId);
    List<ProjectSlagSite> getAllByProjectIdAndName(Long projectId, String name);
    ProjectSlagSite getAllByProjectIdAndSlagSiteCode(Long projectId, String slagSiteCode);
}
