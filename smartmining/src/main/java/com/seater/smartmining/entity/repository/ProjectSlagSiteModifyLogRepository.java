package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSlagSiteModifyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/15 17:54
 */
public interface ProjectSlagSiteModifyLogRepository extends JpaRepository<ProjectSlagSiteModifyLog, Long>, JpaSpecificationExecutor<ProjectSlagSiteModifyLog> {
}
