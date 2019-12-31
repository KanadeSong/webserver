package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 15:45
 */
public interface ProjectSmartminingErrorLogServiceI {

    ProjectSmartminingErrorLog get(Long id) throws IOException;
    ProjectSmartminingErrorLog save(ProjectSmartminingErrorLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSmartminingErrorLog> query();
    Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec);
    Page<ProjectSmartminingErrorLog> query(Pageable pageable);
    Page<ProjectSmartminingErrorLog> query(Specification<ProjectSmartminingErrorLog> spec, Pageable pageable);
    List<ProjectSmartminingErrorLog> getAll();
}
