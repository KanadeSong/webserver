package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/28 0028 16:05
 */
public interface ProjectDiggingPartCountServiceI {
    ProjectDiggingPartCount get(Long id) throws IOException;
    ProjectDiggingPartCount save(ProjectDiggingPartCount log) throws JsonProcessingException;
    void delete(Long id);
    Page<ProjectDiggingPartCount> query();
    Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec);
    Page<ProjectDiggingPartCount> query(Pageable pageable);
    Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec, Pageable pageable);
    List<ProjectDiggingPartCount> getAll();
    List<ProjectDiggingPartCount> getByProjectIdAndTotalIdAndMachineId(Long projectId, Long totalId, Long machineId);
    void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId);
    List<Map> getMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
    List<Map> getByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date time);
}
