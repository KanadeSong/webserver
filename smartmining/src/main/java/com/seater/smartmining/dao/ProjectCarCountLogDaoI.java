package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarCountLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/2 0002 14:43
 */
public interface ProjectCarCountLogDaoI {

    ProjectCarCountLog get(Long id) throws IOException;
    ProjectCarCountLog save(ProjectCarCountLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarCountLog> query();
    Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec);
    Page<ProjectCarCountLog> query(Pageable pageable);
    Page<ProjectCarCountLog> query(Specification<ProjectCarCountLog> spec, Pageable pageable);
    List<ProjectCarCountLog> getAll();
}
