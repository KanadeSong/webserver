package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectWorkTimeByDiggingLog;
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
 * @Date 2019/11/23 0023 15:56
 */
public interface ProjectWorkTimeByDiggingLogDaoI {

    ProjectWorkTimeByDiggingLog get(Long id) throws IOException;
    ProjectWorkTimeByDiggingLog save(ProjectWorkTimeByDiggingLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectWorkTimeByDiggingLog> query();
    Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec);
    Page<ProjectWorkTimeByDiggingLog> query(Pageable pageable);
    Page<ProjectWorkTimeByDiggingLog> query(Specification<ProjectWorkTimeByDiggingLog> spec, Pageable pageable);
    List<ProjectWorkTimeByDiggingLog> getAll();
    List<ProjectWorkTimeByDiggingLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift);
}
