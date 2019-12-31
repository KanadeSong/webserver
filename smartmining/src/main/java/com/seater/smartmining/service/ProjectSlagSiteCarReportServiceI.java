package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectSlagSiteCarReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/29 0029 15:17
 */
public interface ProjectSlagSiteCarReportServiceI {

    ProjectSlagSiteCarReport get(Long id) throws IOException;
    ProjectSlagSiteCarReport save(ProjectSlagSiteCarReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSlagSiteCarReport> query();
    Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec);
    Page<ProjectSlagSiteCarReport> query(Pageable pageable);
    Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec, Pageable pageable);
    List<ProjectSlagSiteCarReport> getAll();
    void batchSave(List<ProjectSlagSiteCarReport> reportList);
    List<ProjectSlagSiteCarReport> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<ProjectSlagSiteCarReport> queryAll(Specification<ProjectSlagSiteCarReport> spec);
}
