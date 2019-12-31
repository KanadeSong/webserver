package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectCarCount;
import com.seater.smartmining.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/15 0015 14:06
 */
public interface ProjectCarCountRepository extends JpaRepository<ProjectCarCount, Long>, JpaSpecificationExecutor<ProjectCarCount> {

    @Query(nativeQuery = true, value = "select * from project_car_count where project_id = ?1 " +
            " and car_code = ?2 and datediff(date_identification, ?3) = 0 and shifts = ?4 and car_type = ?5")
    ProjectCarCount getAllByProjectIdAndCarCodeAndDateIdentificationAndShiftsAndCarType(Long projectId, String carCode, Date date, Integer shifts, Integer carType);

    @Query(nativeQuery = true, value = "select * from project_car_count where project_id = ?1" +
            " and datediff(date_identification, ?2) = 0 and shifts = ?3 and car_type = ?4")
    List<ProjectCarCount> getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(Long projectId, Date date, Integer shift, Integer carType);
}
