package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectApertureDaoI;
import com.seater.smartmining.entity.ProjectAperture;
import com.seater.smartmining.service.ProjectApertureServiceI;
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
 * @Date 2019/10/10 0010 17:59
 */
@Service
public class ProjectApertureServiceImpl implements ProjectApertureServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectApertureDaoI projectApertureDaoI;

    @Override
    public ProjectAperture get(Long id) throws IOException {
        return projectApertureDaoI.get(id);
    }

    @Override
    public ProjectAperture save(ProjectAperture log) throws IOException {
        return projectApertureDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectApertureDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectApertureDaoI.delete(ids);
    }

    @Override
    public Page<ProjectAperture> query() {
        return projectApertureDaoI.query();
    }

    @Override
    public Page<ProjectAperture> query(Specification<ProjectAperture> spec) {
        return projectApertureDaoI.query(spec);
    }

    @Override
    public Page<ProjectAperture> query(Pageable pageable) {
        return projectApertureDaoI.query(pageable);
    }

    @Override
    public Page<ProjectAperture> query(Specification<ProjectAperture> spec, Pageable pageable) {
        return projectApertureDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectAperture> getAll() {
        return projectApertureDaoI.getAll();
    }
}
