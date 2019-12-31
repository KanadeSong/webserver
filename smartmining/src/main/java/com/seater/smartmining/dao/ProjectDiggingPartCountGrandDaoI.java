package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
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
 * @Date 2019/2/28 0028 17:08
 */
public interface ProjectDiggingPartCountGrandDaoI {

    ProjectDiggingPartCountGrand get(Long id) throws IOException;
    ProjectDiggingPartCountGrand save(ProjectDiggingPartCountGrand log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectDiggingPartCountGrand> query();
    Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec);
    Page<ProjectDiggingPartCountGrand> query(Pageable pageable);
    Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec, Pageable pageable);
    List<ProjectDiggingPartCountGrand> getAll();
    List<ProjectDiggingPartCountGrand> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
    ProjectDiggingPartCountGrand getAllByProjectIdAndTotalId(Long projectId, Long totalId);
}
