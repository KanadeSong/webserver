package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectProgrammeDaoI;
import com.seater.smartmining.entity.ProjectProgramme;
import com.seater.smartmining.service.ProjectProgrammeServiceI;
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
 * @Date 2019/11/14 0014 18:04
 */
@Service
public class ProjectProgrammeServiceImpl implements ProjectProgrammeServiceI {

    @Autowired
    private ProjectProgrammeDaoI projectProgrammeDaoI;
    @Override
    public ProjectProgramme get(Long id) throws IOException {
        return projectProgrammeDaoI.get(id);
    }

    @Override
    public ProjectProgramme save(ProjectProgramme log) throws IOException {
        return projectProgrammeDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectProgrammeDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectProgrammeDaoI.delete(ids);
    }

    @Override
    public Page<ProjectProgramme> query() {
        return projectProgrammeDaoI.query();
    }

    @Override
    public Page<ProjectProgramme> query(Specification<ProjectProgramme> spec) {
        return projectProgrammeDaoI.query(spec);
    }

    @Override
    public Page<ProjectProgramme> query(Pageable pageable) {
        return projectProgrammeDaoI.query(pageable);
    }

    @Override
    public Page<ProjectProgramme> query(Specification<ProjectProgramme> spec, Pageable pageable) {
        return projectProgrammeDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectProgramme> getAll() {
        return projectProgrammeDaoI.getAll();
    }
}
