package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.MatchingDegree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 11:38
 */
public interface MatchingDegreeRepository extends JpaRepository<MatchingDegree,Long>, JpaSpecificationExecutor<MatchingDegree> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from matching_degree where project_id = ?1 and datediff(create_time, ?2) = 0 and time_type = ?3 and shifts = ?4")
    void deleteByProjectIdAndTimeAndType(Long projectId, Date date, Integer type, Integer shifts);

    //统计周数据
    @Query(nativeQuery = true, value = "select car_id, car_code, sum(finish_count) as finish_count, slag_site_id, slag_site_name, sum(upload_count_by_machine) as upload_count_by_machine,   " +
            " sum(upload_count_by_car) as upload_count_by_car, sum(upload_total_count_by_car) AS upload_total_count_by_car, project_id, sum(un_valid_count) as un_valid_count, shifts" +
            " from matching_degree where time_type = 1 and project_id = ?1 and create_time >= ?2 and create_time <= ?3 group by car_id, car_code, slag_site_id, slag_site_name, project_id, shifts")
    List<Map> getAllByProjectIdAndStartTimeAndEndTimeByWeek(Long projectId, Date startTime, Date endTime);
}
