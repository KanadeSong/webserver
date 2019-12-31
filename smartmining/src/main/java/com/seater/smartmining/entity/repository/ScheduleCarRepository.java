package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ScheduleCar;
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
 * @Date 2019/5/23 0023 14:46
 */
public interface ScheduleCarRepository extends JpaRepository<ScheduleCar, Long>, JpaSpecificationExecutor<ScheduleCar> {

    List<ScheduleCar> getAllByProjectId(Long projectId);

    List<ScheduleCar> getAllByProjectIdAndCarIdAndIsVaild(Long projectId, Long carId, boolean flag);

    @Query(nativeQuery = true, value = "select * from schedule_car where project_id = ?1 and group_code = ?2 and is_vaild = true")
    List<ScheduleCar> getAllByProjectIdAndGroupCode(Long projectId, String groupCode);

    List<ScheduleCar> findByProjectIdAndGroupCodeAndIsVaildIsTrue(Long projectId, String groupCode);

    @Transactional
    void deleteByProjectIdAndCarCode(Long projectId, String carCode);

    @Transactional
    void deleteByProjectIdAndGroupCode(Long projectId, String groupCode);

    @Transactional
    void deleteByGroupCode(String groupCode);

    @Query(nativeQuery = true, value = "select * from schedule_car where project_id = ?1 and car_code = ?2")
    ScheduleCar getAllByProjectIdAndCarCode(Long projectId, String carCode);

    @Transactional
    @Query(nativeQuery = true, value = "select distinct group_code from schedule_car")
    List<String> getGroupCodeList();

    @Query(nativeQuery = true, value = "select car_code from schedule_car where project_id = ?1 and is_vaild = ?2")
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid);

    @Query(nativeQuery = true, value = "select * from schedule_car where project_id = ?1 and is_vaild = ?2")
    List<ScheduleCar> getAllByProjectIdAndIsVaildObject(Long projectId, Boolean isValid);


    @Query(nativeQuery = true, value = "SELECT\n" +
            "	c.* \n" +
            "FROM\n" +
            "	schedule_car c,\n" +
            "	project_schedule s \n" +
            "WHERE\n" +
            "	s.group_code = c.group_code \n" +
            "	AND s.project_id = ?1 \n" +
            "	AND c.is_vaild = TRUE")
    List<ScheduleCar> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_car where project_id = ?1 and car_code in ?2")
    void  deleteByProjectIdAndCarCodeList(Long projectId, List<String> carCodeList);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from schedule_car where project_id = ?1")
    void deleteByProjectId(Long projectId);
}
