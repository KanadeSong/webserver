package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectCarEfficiency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/17 0017 16:54
 */
public interface ProjectCarEfficiencyServiceI {

    ProjectCarEfficiency get(Long id) throws IOException;
    ProjectCarEfficiency save(ProjectCarEfficiency log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarEfficiency> query();
    Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec);
    Page<ProjectCarEfficiency> query(Pageable pageable);
    Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec, Pageable pageable);
    List<ProjectCarEfficiency> getAll();
}
