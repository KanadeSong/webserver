package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectExplosive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:32
 */
public interface ProjectExplosiveRepository extends JpaRepository<ProjectExplosive, Long>, JpaSpecificationExecutor<ProjectExplosive> {

    List<ProjectExplosive> getAllByProjectIdOrderById(Long projectId);
}
