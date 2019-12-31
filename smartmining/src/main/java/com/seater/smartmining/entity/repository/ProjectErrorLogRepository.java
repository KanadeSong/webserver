package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/22 0022 17:24
 */
public interface ProjectErrorLogRepository extends JpaRepository<ProjectErrorLog, Long>, JpaSpecificationExecutor<ProjectErrorLog> {
}
