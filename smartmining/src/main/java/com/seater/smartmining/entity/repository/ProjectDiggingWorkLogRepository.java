package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingWorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/18 0018 18:00
 */
public interface ProjectDiggingWorkLogRepository extends JpaRepository<ProjectDiggingWorkLog, Long>, JpaSpecificationExecutor<ProjectDiggingWorkLog> {
}
