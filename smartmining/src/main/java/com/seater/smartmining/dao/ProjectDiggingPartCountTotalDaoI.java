package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingPartCountTotal;
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
 * @Date 2019/2/28 0028 15:41
 */
public interface ProjectDiggingPartCountTotalDaoI {

    ProjectDiggingPartCountTotal get(Long id) throws IOException;
    ProjectDiggingPartCountTotal save(ProjectDiggingPartCountTotal log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectDiggingPartCountTotal> query();
    Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec);
    Page<ProjectDiggingPartCountTotal> query(Pageable pageable);
    Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec, Pageable pageable);
    List<ProjectDiggingPartCountTotal> getAll();
    List<ProjectDiggingPartCountTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
}
