package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDiggingReportByPlace;
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
 * @Date 2019/8/19 0019 11:20
 */
public interface ProjectDiggingReportByPlaceDaoI {

    ProjectDiggingReportByPlace get(Long id) throws IOException;
    ProjectDiggingReportByPlace save(ProjectDiggingReportByPlace log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingReportByPlace> query();
    Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec);
    Page<ProjectDiggingReportByPlace> query(Pageable pageable);
    Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec, Pageable pageable);
    List<ProjectDiggingReportByPlace> getAll();
    void batchSave(List<ProjectDiggingReportByPlace> placeList);
    void deleteByProjectIdAndAndDateIdentification(Long projectId, Date date);
    List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);
    List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime, String machineCode);
}
