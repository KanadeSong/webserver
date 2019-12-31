package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectWorkTimeByCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/18 0018 11:21
 */
public interface ProjectWorkTimeByCarRepository extends JpaRepository<ProjectWorkTimeByCar, Long>, JpaSpecificationExecutor<ProjectWorkTimeByCar> {

    @Query(nativeQuery = true, value = "select * from project_work_time_by_car where project_id = ?1 and car_code = ?2 and status = ?3 order by start_time desc")
    List<ProjectWorkTimeByCar> getAllByProjectIdAndCarCodeAndStatus(Long projectId, String carCode, Integer status);
}
