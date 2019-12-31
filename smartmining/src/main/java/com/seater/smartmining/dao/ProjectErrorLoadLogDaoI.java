package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectErrorLoadLog;
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
 * @Date 2019/11/1 0001 0:21
 */
public interface ProjectErrorLoadLogDaoI {

    ProjectErrorLoadLog get(Long id) throws IOException;
    ProjectErrorLoadLog save(ProjectErrorLoadLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectErrorLoadLog> query();
    Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec);
    Page<ProjectErrorLoadLog> query(Pageable pageable);
    Page<ProjectErrorLoadLog> query(Specification<ProjectErrorLoadLog> spec, Pageable pageable);
    List<ProjectErrorLoadLog> getAll();
    ProjectErrorLoadLog getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date dateIdentification, Integer shift);
    List<ProjectErrorLoadLog> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date dateIdentification, Integer shift);
}
