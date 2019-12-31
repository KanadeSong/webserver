package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectPlaceDaoI;
import com.seater.smartmining.entity.ProjectPlace;
import com.seater.smartmining.service.ProjectPlaceServiceI;
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
 * @Date 2019/6/6 0006 14:47
 */
@Service
public class ProjectPlaceServiceImpl implements ProjectPlaceServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectPlaceDaoI projectPlaceDaoI;

    @Override
    public ProjectPlace get(Long id) throws IOException {
        return projectPlaceDaoI.get(id);
    }

    @Override
    public ProjectPlace save(ProjectPlace log) throws IOException {
        return projectPlaceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectPlaceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectPlaceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectPlace> query() {
        return projectPlaceDaoI.query();
    }

    @Override
    public Page<ProjectPlace> query(Specification<ProjectPlace> spec) {
        return projectPlaceDaoI.query(spec);
    }

    @Override
    public Page<ProjectPlace> query(Pageable pageable) {
        return projectPlaceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectPlace> query(Specification<ProjectPlace> spec, Pageable pageable) {
        return projectPlaceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectPlace> getAll() {
        return projectPlaceDaoI.getAll();
    }
}
