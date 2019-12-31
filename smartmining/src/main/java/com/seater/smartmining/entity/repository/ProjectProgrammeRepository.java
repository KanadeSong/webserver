package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/11 0011 13:46
 */
public interface ProjectProgrammeRepository extends JpaRepository<ProjectProgramme, Long>, JpaSpecificationExecutor<ProjectProgramme> {
}
