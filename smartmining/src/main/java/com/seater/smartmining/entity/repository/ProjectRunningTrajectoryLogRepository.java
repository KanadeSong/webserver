package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectRunningTrajectoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 9:11
 */
public interface ProjectRunningTrajectoryLogRepository extends JpaRepository<ProjectRunningTrajectoryLog, Long>, JpaSpecificationExecutor<ProjectRunningTrajectoryLog> {
}
