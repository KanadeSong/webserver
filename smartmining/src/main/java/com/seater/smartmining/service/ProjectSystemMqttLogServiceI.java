package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectSystemMqttLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 12:53
 */
public interface ProjectSystemMqttLogServiceI {

    ProjectSystemMqttLog get(Long id) throws IOException;
    ProjectSystemMqttLog save(ProjectSystemMqttLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSystemMqttLog> query();
    Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec);
    Page<ProjectSystemMqttLog> query(Pageable pageable);
    Page<ProjectSystemMqttLog> query(Specification<ProjectSystemMqttLog> spec, Pageable pageable);
    List<ProjectSystemMqttLog> getAll();
}
