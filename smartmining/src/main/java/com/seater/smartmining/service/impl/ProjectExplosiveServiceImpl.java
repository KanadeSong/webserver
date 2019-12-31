package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectExplosiveDaoI;
import com.seater.smartmining.entity.ProjectExplosive;
import com.seater.smartmining.service.ProjectExplosiveServiceI;
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
 * @Date 2019/10/10 0010 17:43
 */
@Service
public class ProjectExplosiveServiceImpl implements ProjectExplosiveServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectExplosiveDaoI projectExplosiveDaoI;

    @Override
    public ProjectExplosive get(Long id) throws IOException {
        return projectExplosiveDaoI.get(id);
    }

    @Override
    public ProjectExplosive save(ProjectExplosive log) throws IOException {
        return projectExplosiveDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectExplosiveDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectExplosiveDaoI.delete(ids);
    }

    @Override
    public Page<ProjectExplosive> query() {
        return projectExplosiveDaoI.query();
    }

    @Override
    public Page<ProjectExplosive> query(Specification<ProjectExplosive> spec) {
        return projectExplosiveDaoI.query(spec);
    }

    @Override
    public Page<ProjectExplosive> query(Pageable pageable) {
        return projectExplosiveDaoI.query(pageable);
    }

    @Override
    public Page<ProjectExplosive> query(Specification<ProjectExplosive> spec, Pageable pageable) {
        return projectExplosiveDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectExplosive> getAll() {
        return projectExplosiveDaoI.getAll();
    }

    @Override
    public List<ProjectExplosive> getByProjectIdOrderById(Long projectId) {
        return projectExplosiveDaoI.getByProjectIdOrderById(projectId);
    }
}
