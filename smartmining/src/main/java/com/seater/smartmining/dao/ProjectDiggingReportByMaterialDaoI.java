package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDiggingReportByMaterial;
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
 * @Date 2019/8/20 0020 11:17
 */
public interface ProjectDiggingReportByMaterialDaoI {

    ProjectDiggingReportByMaterial get(Long id) throws IOException;
    ProjectDiggingReportByMaterial save(ProjectDiggingReportByMaterial log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingReportByMaterial> query();
    Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec);
    Page<ProjectDiggingReportByMaterial> query(Pageable pageable);
    Page<ProjectDiggingReportByMaterial> query(Specification<ProjectDiggingReportByMaterial> spec, Pageable pageable);
    List<ProjectDiggingReportByMaterial> getAll();
    void batchSave(List<ProjectDiggingReportByMaterial> materialList);
    void deleteByProjectIdAndDateIdentification(Long projectId, Date date);
}
