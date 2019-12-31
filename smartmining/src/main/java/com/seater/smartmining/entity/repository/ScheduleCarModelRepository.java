package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ScheduleCarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:24
 */
public interface ScheduleCarModelRepository extends JpaRepository<ScheduleCarModel, Long>, JpaSpecificationExecutor<ScheduleCarModel> {

    List<ScheduleCarModel> getAllByProjectId(Long projectId);
    List<ScheduleCarModel> getAllByProjectIdAndGroupCodeAndIsVaild(Long projectId, String groupCode, boolean valid);
    void deleteByProjectIdAndCarCode(Long projectId, String carCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_car_model where project_id = ?1 and car_code in ?2 and programme_id = ?3")
    void deleteByProjectIdAndCarCodeListAndProgrammeId(Long projectId, List<String> carCodeList, Long programmeId);

    @Transactional
    void deleteByGroupCode(String groupCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_car_model where group_code in ?1")
    void deleteByGroupCodes(List<String> groupCodes);

    @Query(nativeQuery = true, value = "select * from schedule_car_model where project_id = ?1 and groupCode in ?2 " +
            " and car_code = ?3 and is_vaild = true")
    ScheduleCarModel getAllByProjectIdAndGroupCodes(Long projectId, List<String> groupCodeList, String carCode);
}
