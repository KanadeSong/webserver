package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectModifyScheduleModelLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 14:14
 */
public interface ProjectModifyScheduleModelLogDaoI {

    ProjectModifyScheduleModelLog get(Long id) throws IOException;
    ProjectModifyScheduleModelLog save(ProjectModifyScheduleModelLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectModifyScheduleModelLog> query();
    Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec);
    Page<ProjectModifyScheduleModelLog> query(Pageable pageable);
    Page<ProjectModifyScheduleModelLog> query(Specification<ProjectModifyScheduleModelLog> spec, Pageable pageable);
    List<ProjectModifyScheduleModelLog> getAll();
}
