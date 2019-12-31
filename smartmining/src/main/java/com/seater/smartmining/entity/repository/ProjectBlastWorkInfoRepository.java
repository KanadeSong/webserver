package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectBlastWorkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 12:54
 */
public interface ProjectBlastWorkInfoRepository extends JpaRepository<ProjectBlastWorkInfo, Long>, JpaSpecificationExecutor<ProjectBlastWorkInfo> {
}
