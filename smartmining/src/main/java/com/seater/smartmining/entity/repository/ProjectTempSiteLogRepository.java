package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectTempSiteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/21 0021 11:30
 */
public interface ProjectTempSiteLogRepository extends JpaRepository<ProjectTempSiteLog, Long>, JpaSpecificationExecutor<ProjectTempSiteLog> {

    @Query("select max(timeDischarge) from ProjectTempSiteLog where carCode = ?1 and valid = true")
    List<Date> getMaxUnloadDateByCarCode(String carCode);
}
