package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectCarTotalCountReportByTotal;
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
 * @Date 2019/11/22 0022 17:17
 */
public interface ProjectCarTotalCountReportByTotalServiceI {

    ProjectCarTotalCountReportByTotal get(Long id) throws IOException;
    ProjectCarTotalCountReportByTotal save(ProjectCarTotalCountReportByTotal log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarTotalCountReportByTotal> query();
    Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec);
    Page<ProjectCarTotalCountReportByTotal> query(Pageable pageable);
    Page<ProjectCarTotalCountReportByTotal> query(Specification<ProjectCarTotalCountReportByTotal> spec, Pageable pageable);
    List<ProjectCarTotalCountReportByTotal> queryAll(Specification<ProjectCarTotalCountReportByTotal> spec);
    List<ProjectCarTotalCountReportByTotal> getAll();
    List<ProjectCarTotalCountReportByTotal> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    void deleteByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
