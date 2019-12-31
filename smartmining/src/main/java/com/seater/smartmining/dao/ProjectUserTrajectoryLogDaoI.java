package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectUserTrajectoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 15:32
 */
public interface ProjectUserTrajectoryLogDaoI {

    ProjectUserTrajectoryLog get(Long id) throws IOException;
    ProjectUserTrajectoryLog save(ProjectUserTrajectoryLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectUserTrajectoryLog> query();
    Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec);
    Page<ProjectUserTrajectoryLog> query(Pageable pageable);
    Page<ProjectUserTrajectoryLog> query(Specification<ProjectUserTrajectoryLog> spec, Pageable pageable);
    List<ProjectUserTrajectoryLog> getAll();
    void saveAll(List<ProjectUserTrajectoryLog> saveList);
}
