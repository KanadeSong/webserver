package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectDiggingPartCountAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/16 0016 11:40
 */
public interface ProjectDiggingPartCountAmountRepository extends JpaRepository<ProjectDiggingPartCountAmount, Long>, JpaSpecificationExecutor<ProjectDiggingPartCountAmount> {

    ProjectDiggingPartCountAmount getAllByProjectIdAndCountId(Long projectId, Long countId);
}
