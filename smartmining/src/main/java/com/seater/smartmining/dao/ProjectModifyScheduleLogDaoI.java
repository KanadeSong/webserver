package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectModifyScheduleLog;
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
 * @Date 2019/7/26 0026 10:45
 */
public interface ProjectModifyScheduleLogDaoI {

    ProjectModifyScheduleLog get(Long id) throws IOException;
    ProjectModifyScheduleLog save(ProjectModifyScheduleLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectModifyScheduleLog> query();
    Page<ProjectModifyScheduleLog> query(Specification<ProjectModifyScheduleLog> spec);
    Page<ProjectModifyScheduleLog> query(Pageable pageable);
    Page<ProjectModifyScheduleLog> query(Specification<ProjectModifyScheduleLog> spec, Pageable pageable);
    List<ProjectModifyScheduleLog> getAll();
    List<ProjectModifyScheduleLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
