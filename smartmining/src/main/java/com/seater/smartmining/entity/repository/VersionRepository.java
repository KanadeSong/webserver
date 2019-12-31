package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/4 0004 10:37
 */
public interface VersionRepository extends JpaRepository<Version, Long>, JpaSpecificationExecutor<Version> {
}
