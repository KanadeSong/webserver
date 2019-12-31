package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectDeviceElectrifyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/10 0010 16:37
 */
public interface ProjectDeviceElectrifyLogDaoI {

    ProjectDeviceElectrifyLog get(Long id) throws IOException;
    ProjectDeviceElectrifyLog save(ProjectDeviceElectrifyLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectDeviceElectrifyLog> query();
    Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec);
    Page<ProjectDeviceElectrifyLog> query(Pageable pageable);
    Page<ProjectDeviceElectrifyLog> query(Specification<ProjectDeviceElectrifyLog> spec, Pageable pageable);
    List<ProjectDeviceElectrifyLog> getAll();
    ProjectDeviceElectrifyLog getAllByProjectIdAndUidElectrifyTime(Long projectId, String carCode, Date date, Integer deviceType);
}
