package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.ProjectOtherDeviceWorkInfo;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 15:54
 */
public interface ProjectOtherDeviceWorkInfoRepository extends JpaRepository<ProjectOtherDeviceWorkInfo, Long>, JpaSpecificationExecutor<ProjectOtherDeviceWorkInfo> {

    //开机中的车辆
    @Query(nativeQuery = true, value = "select * from project_other_device_work_info where project_id = ?1 and code = ?2 and car_type = ?3 and status = ?4")
    ProjectOtherDeviceWorkInfo getAllByProjectIdAndCodeAndCarType(Long projectId, String code, Integer carType, Integer status);

    @Query(nativeQuery = true, value = "select device_id, code, sum(work_time) as workTime, sum(amount) as amount from project_other_device_work_info" +
            " where project_id = ?1 and datediff(date_identification, ?2) = 0 and car_type = ?3 group by device_id, CODE")
    List<Map> getDayReportByProjectIdAndDateIdentificationAndCarType(Long projectId, Date date, Integer carType);
}
