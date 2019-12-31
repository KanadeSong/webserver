package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDiggingWorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/18 0018 17:52
 */
public interface ProjectDiggingWorkLogDaoI {

    ProjectDiggingWorkLog get(Long id) throws IOException;
    ProjectDiggingWorkLog save(ProjectDiggingWorkLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDiggingWorkLog> query();
    Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec);
    Page<ProjectDiggingWorkLog> query(Pageable pageable);
    Page<ProjectDiggingWorkLog> query(Specification<ProjectDiggingWorkLog> spec, Pageable pageable);
    List<ProjectDiggingWorkLog> getAll();
}
