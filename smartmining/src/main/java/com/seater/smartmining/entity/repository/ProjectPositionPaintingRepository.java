package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectPositionPainting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 9:40
 */
public interface ProjectPositionPaintingRepository extends JpaRepository<ProjectPositionPainting, Long>, JpaSpecificationExecutor<ProjectPositionPainting> {
}
