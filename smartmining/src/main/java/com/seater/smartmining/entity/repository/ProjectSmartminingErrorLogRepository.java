package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 15:31
 */
public interface ProjectSmartminingErrorLogRepository extends JpaRepository<ProjectSmartminingErrorLog, Long>, JpaSpecificationExecutor<ProjectSmartminingErrorLog> {


}
