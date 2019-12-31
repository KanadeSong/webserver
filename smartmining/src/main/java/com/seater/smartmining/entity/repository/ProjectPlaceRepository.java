package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:40
 */
public interface ProjectPlaceRepository extends JpaRepository<ProjectPlace, Long>, JpaSpecificationExecutor<ProjectPlace> {


}
