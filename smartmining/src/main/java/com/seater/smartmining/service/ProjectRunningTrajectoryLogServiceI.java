package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectRunningTrajectoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 9:38
 */
public interface ProjectRunningTrajectoryLogServiceI {

    ProjectRunningTrajectoryLog get(Long id) throws IOException;
    ProjectRunningTrajectoryLog save(ProjectRunningTrajectoryLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectRunningTrajectoryLog> query();
    Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec);
    Page<ProjectRunningTrajectoryLog> query(Pageable pageable);
    Page<ProjectRunningTrajectoryLog> query(Specification<ProjectRunningTrajectoryLog> spec, Pageable pageable);
    List<ProjectRunningTrajectoryLog> getAll();
    void saveAll(List<ProjectRunningTrajectoryLog> saveList);
}
