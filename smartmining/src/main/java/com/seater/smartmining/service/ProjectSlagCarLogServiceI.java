package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectSlagCarLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 17:01
 */
public interface ProjectSlagCarLogServiceI {

    ProjectSlagCarLog get(Long id) throws IOException;
    ProjectSlagCarLog save(ProjectSlagCarLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectSlagCarLog> query();
    Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec);
    Page<ProjectSlagCarLog> query(Pageable pageable);
    Page<ProjectSlagCarLog> query(Specification<ProjectSlagCarLog> spec, Pageable pageable);
    List<ProjectSlagCarLog> getAll();
    List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime);
    ProjectSlagCarLog getAllByProjectIDAndCarCodeAndTerminalTime(Long projectId, String carCode, Long terminalTime);
    List<ProjectSlagCarLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime);
}
