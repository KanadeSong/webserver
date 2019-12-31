package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDeviceStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 15:08
 */
public interface ProjectDeviceStatusLogRepository extends JpaRepository<ProjectDeviceStatusLog, Long>, JpaSpecificationExecutor<ProjectDeviceStatusLog> {

    @Query(nativeQuery = true, value = "select * from project_device_status_log where uid = ?1 and unline_time is null")
    ProjectDeviceStatusLog getAllByUid(String uid);

    @Query(nativeQuery = true, value = "select * from project_device_status_log where unline_time is null")
    List<ProjectDeviceStatusLog> getAllByUnlineTime();
}
