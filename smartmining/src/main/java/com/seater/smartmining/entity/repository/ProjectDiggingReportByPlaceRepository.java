package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingReportByPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/19 0019 11:19
 */
public interface ProjectDiggingReportByPlaceRepository extends JpaRepository<ProjectDiggingReportByPlace, Long>, JpaSpecificationExecutor<ProjectDiggingReportByPlace> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_digging_report_by_place where project_id = ?1 and datediff(date_identification, ?2) = 0")
    void deleteByProjectIdAndAndDateIdentification(Long projectId, Date date);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, machine_id, machine_code, place_id, place_name, shifts from project_digging_report_by_place " +
            " where project_id = ?1 and date_identification <= ?2 and date_identification >= ?3" +
            " group by machine_id, machine_code, place_id, place_name, shifts")
    List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "select sum(work_time) as workTime, machine_id, machine_code, place_id, place_name, shifts from project_digging_report_by_place " +
            " where project_id = ?1 and date_identification <= ?2 and date_identification >= ?3 and machine_code = ?4" +
            " group by machine_id, machine_code, place_id, place_name, shifts")
    List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime, String machineCode);
}
