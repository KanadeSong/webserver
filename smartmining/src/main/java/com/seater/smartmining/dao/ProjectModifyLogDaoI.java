package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectModifyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/16 0016 11:01
 */
public interface ProjectModifyLogDaoI {

    ProjectModifyLog get(Long id) throws IOException;
    ProjectModifyLog save(ProjectModifyLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectModifyLog> query();
    Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec);
    Page<ProjectModifyLog> query(Pageable pageable);
    Page<ProjectModifyLog> query(Specification<ProjectModifyLog> spec, Pageable pageable);
    List<ProjectModifyLog> getAll();
    void batchSave(List<ProjectModifyLog> logList);
}
