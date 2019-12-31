package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectModifyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/16 0016 11:00
 */
public interface ProjectModifyLogRepository extends JpaRepository<ProjectModifyLog,Long>, JpaSpecificationExecutor<ProjectModifyLog> {
}
