package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectCarTotalCountReport;
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
 * @Date 2019/11/19 0019 11:48
 */
public interface ProjectCarTotalCountReportServiceI {

    ProjectCarTotalCountReport get(Long id) throws IOException;
    ProjectCarTotalCountReport save(ProjectCarTotalCountReport log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarTotalCountReport> query();
    Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec);
    Page<ProjectCarTotalCountReport> query(Pageable pageable);
    Page<ProjectCarTotalCountReport> query(Specification<ProjectCarTotalCountReport> spec, Pageable pageable);
    List<ProjectCarTotalCountReport> getAll();
    void batchSave(List<ProjectCarTotalCountReport> reportList);
    void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
