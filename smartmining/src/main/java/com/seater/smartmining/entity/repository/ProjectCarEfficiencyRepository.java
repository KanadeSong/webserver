package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCarEfficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 16:44
 */
public interface ProjectCarEfficiencyRepository extends JpaRepository<ProjectCarEfficiency, Long>, JpaSpecificationExecutor<ProjectCarEfficiency> {

}
