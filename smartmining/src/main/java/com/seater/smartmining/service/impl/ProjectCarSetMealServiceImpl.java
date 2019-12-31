package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarSetMealDaoI;
import com.seater.smartmining.entity.ProjectCarSetMeal;
import com.seater.smartmining.service.ProjectCarSetMealServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:40
 */
@Service
public class ProjectCarSetMealServiceImpl implements ProjectCarSetMealServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCarSetMealDaoI projectCarSetMealDaoI;
    @Override
    public ProjectCarSetMeal get(Long id) throws IOException {
        return projectCarSetMealDaoI.get(id);
    }

    @Override
    public ProjectCarSetMeal save(ProjectCarSetMeal log) throws IOException {
        return projectCarSetMealDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarSetMealDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarSetMealDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarSetMeal> query() {
        return projectCarSetMealDaoI.query();
    }

    @Override
    public Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec) {
        return projectCarSetMealDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarSetMeal> query(Pageable pageable) {
        return projectCarSetMealDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec, Pageable pageable) {
        return projectCarSetMealDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarSetMeal> getAll() {
        return projectCarSetMealDaoI.getAll();
    }
}
