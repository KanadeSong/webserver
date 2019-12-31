package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectCarEfficiencyDaoI;
import com.seater.smartmining.entity.ProjectCarEfficiency;
import com.seater.smartmining.service.ProjectCarEfficiencyServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date 2019/12/17 0017 16:54
 */
@Service
public class ProjectCarEfficiencyServiceImpl implements ProjectCarEfficiencyServiceI {

    @Autowired
    private ProjectCarEfficiencyDaoI projectCarEfficiencyDaoI;

    @Override
    public ProjectCarEfficiency get(Long id) throws IOException {
        return projectCarEfficiencyDaoI.get(id);
    }

    @Override
    public ProjectCarEfficiency save(ProjectCarEfficiency log) throws IOException {
        return projectCarEfficiencyDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectCarEfficiencyDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectCarEfficiencyDaoI.delete(ids);
    }

    @Override
    public Page<ProjectCarEfficiency> query() {
        return projectCarEfficiencyDaoI.query();
    }

    @Override
    public Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec) {
        return projectCarEfficiencyDaoI.query(spec);
    }

    @Override
    public Page<ProjectCarEfficiency> query(Pageable pageable) {
        return projectCarEfficiencyDaoI.query(pageable);
    }

    @Override
    public Page<ProjectCarEfficiency> query(Specification<ProjectCarEfficiency> spec, Pageable pageable) {
        return projectCarEfficiencyDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectCarEfficiency> getAll() {
        return projectCarEfficiencyDaoI.getAll();
    }
}
