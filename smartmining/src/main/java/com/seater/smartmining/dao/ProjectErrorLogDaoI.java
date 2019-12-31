package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/22 0022 17:21
 */
public interface ProjectErrorLogDaoI {

    ProjectErrorLog get(Long id) throws IOException;
    ProjectErrorLog save(ProjectErrorLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectErrorLog> query();
    Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec);
    Page<ProjectErrorLog> query(Pageable pageable);
    Page<ProjectErrorLog> query(Specification<ProjectErrorLog> spec, Pageable pageable);
    List<ProjectErrorLog> getAll();
}
