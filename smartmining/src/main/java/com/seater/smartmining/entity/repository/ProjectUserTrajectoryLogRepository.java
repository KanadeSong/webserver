package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectUserTrajectoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 15:25
 */
public interface ProjectUserTrajectoryLogRepository extends JpaRepository<ProjectUserTrajectoryLog, Long>, JpaSpecificationExecutor<ProjectUserTrajectoryLog> {
}
