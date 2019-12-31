package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.WorkMergeErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/4 0004 11:16
 */
public interface WorkMergeErrorLogRepository extends JpaRepository<WorkMergeErrorLog, Long>, JpaSpecificationExecutor<WorkMergeErrorLog> {

}
