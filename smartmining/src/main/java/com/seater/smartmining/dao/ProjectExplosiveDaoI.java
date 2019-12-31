package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectExplosive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:36
 */
public interface ProjectExplosiveDaoI {

    ProjectExplosive get(Long id) throws IOException;
    ProjectExplosive save(ProjectExplosive log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectExplosive> query();
    Page<ProjectExplosive> query(Specification<ProjectExplosive> spec);
    Page<ProjectExplosive> query(Pageable pageable);
    Page<ProjectExplosive> query(Specification<ProjectExplosive> spec, Pageable pageable);
    List<ProjectExplosive> getAll();
    List<ProjectExplosive> getByProjectIdOrderById(Long projectId);
}
