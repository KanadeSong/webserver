package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectMachineLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/4 0004 21:05
 */
public interface ProjectMachineLocationRepository extends JpaRepository<ProjectMachineLocation, Long>, JpaSpecificationExecutor<ProjectMachineLocation> {

    @Query(nativeQuery = true, value = "select * from project_machine_location where project_id = ?1" +
            " and car_code = ?2 and create_time >= ?3 and create_time <= ?4 order by create_time desc")
    List<ProjectMachineLocation> getAllByProjectIdAndCarCodeAndCreateTime(Long projectId, String carCode, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select * from project_machine_location where project_id = ?1" +
            " and create_time >= ?2 and create_time <= ?3")
    List<ProjectMachineLocation> getAllByProjectIdAndCreateTime(Long projectId, Date startTime, Date endTime);
}
