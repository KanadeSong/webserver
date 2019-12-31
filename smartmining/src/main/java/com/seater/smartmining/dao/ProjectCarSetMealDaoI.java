package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCarSetMeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:29
 */
public interface ProjectCarSetMealDaoI {

    ProjectCarSetMeal get(Long id) throws IOException;
    ProjectCarSetMeal save(ProjectCarSetMeal log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectCarSetMeal> query();
    Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec);
    Page<ProjectCarSetMeal> query(Pageable pageable);
    Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec, Pageable pageable);
    List<ProjectCarSetMeal> getAll();
}
