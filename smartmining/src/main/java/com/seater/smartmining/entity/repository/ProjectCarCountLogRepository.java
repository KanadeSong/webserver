package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarCountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/2 0002 14:42
 */
public interface ProjectCarCountLogRepository extends JpaRepository<ProjectCarCountLog, Long>, JpaSpecificationExecutor<ProjectCarCountLog> {
}
