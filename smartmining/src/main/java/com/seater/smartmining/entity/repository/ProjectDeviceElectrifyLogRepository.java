package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDeviceElectrifyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/10 0010 16:27
 */
public interface ProjectDeviceElectrifyLogRepository extends JpaRepository<ProjectDeviceElectrifyLog, Long>, JpaSpecificationExecutor<ProjectDeviceElectrifyLog> {

    @Query(nativeQuery = true, value = "select * from project_device_electrify_log where project_id = ?1 and car_code = ?2 and electrify_time = ?3 and device_type = ?4")
    ProjectDeviceElectrifyLog getAllByProjectIdAndUidElectrifyTime(Long projectId, String carCode, Date date, Integer deviceType);
}
