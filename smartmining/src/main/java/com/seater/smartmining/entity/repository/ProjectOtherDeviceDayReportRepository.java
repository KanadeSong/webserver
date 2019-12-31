package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectOtherDeviceDayReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/19 0019 12:54
 */
public interface ProjectOtherDeviceDayReportRepository extends JpaRepository<ProjectOtherDeviceDayReport, Long>, JpaSpecificationExecutor<ProjectOtherDeviceDayReport> {
}
