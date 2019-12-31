package com.seater.smartmining.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectMqttCardReport;
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
 * @Date 2019/11/3 0003 14:54
 */
public interface ProjectMqttCardReportDaoI {

    ProjectMqttCardReport get(Long id) throws IOException;
    ProjectMqttCardReport save(ProjectMqttCardReport log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectMqttCardReport> query();
    Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec);
    Page<ProjectMqttCardReport> query(Pageable pageable);
    Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec, Pageable pageable);
    List<ProjectMqttCardReport> getAll();
    void deleteAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    List<ProjectMqttCardReport> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndTimeDischarge(Long projectId, String carCode, Date startTime, Date endTime);
    List<ProjectMqttCardReport> getAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime);
    List<Map> getReportCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    List<Map> getErrorCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    List<Map> getErrorCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
    Map getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift);
    List<Map> getUnValidCountByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);
    List<Map> getUnValidCountMonthByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);
    List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift);
}
