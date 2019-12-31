package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectAppStatisticsByMachine;
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
 * @Date 2019/6/9 0009 12:45
 */
public interface ProjectAppStatisticsByMachineRepository extends JpaRepository<ProjectAppStatisticsByMachine, Long>, JpaSpecificationExecutor<ProjectAppStatisticsByMachine> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_app_statistics_by_machine where datediff(create_date, ?1) = 0 and project_id = ?2")
    void deleteByCreateDate(Date createDate, Long projectId);

    @Query(nativeQuery = true, value = "select * from project_app_statistics_by_machine where project_id = ?1 and shifts = ?2 and datediff(create_date, ?3) = 0 and machine_code = ?4")
    ProjectAppStatisticsByMachine getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date, String machineCode);

    @Query(nativeQuery = true, value = "select * from project_app_statistics_by_machine where project_id = ?1 and shifts = ?2 and datediff(create_date, ?3) = 0")
    List<ProjectAppStatisticsByMachine> getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date);
}
