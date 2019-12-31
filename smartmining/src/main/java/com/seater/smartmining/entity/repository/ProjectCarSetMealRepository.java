package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarSetMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:28
 */
public interface ProjectCarSetMealRepository extends JpaRepository<ProjectCarSetMeal, Long>, JpaSpecificationExecutor<ProjectCarSetMeal> {
}
