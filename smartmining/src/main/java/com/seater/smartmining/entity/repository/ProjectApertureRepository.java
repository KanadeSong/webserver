package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectAperture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:53
 */
public interface ProjectApertureRepository extends JpaRepository<ProjectAperture, Long>, JpaSpecificationExecutor<ProjectAperture> {


}
