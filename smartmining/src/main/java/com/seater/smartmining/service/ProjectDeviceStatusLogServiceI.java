package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectDeviceStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 15:14
 */
public interface ProjectDeviceStatusLogServiceI {

    ProjectDeviceStatusLog get(Long id) throws IOException;
    ProjectDeviceStatusLog save(ProjectDeviceStatusLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDeviceStatusLog> query();
    Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec);
    Page<ProjectDeviceStatusLog> query(Pageable pageable);
    Page<ProjectDeviceStatusLog> query(Specification<ProjectDeviceStatusLog> spec, Pageable pageable);
    List<ProjectDeviceStatusLog> getAll();
    ProjectDeviceStatusLog getAllByUid(String uid);
    List<ProjectDeviceStatusLog> getAllByUnlineTime();
}
