package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectBlastWorkInfoDaoI;
import com.seater.smartmining.entity.ProjectBlastWorkInfo;
import com.seater.smartmining.service.ProjectBlastWorkInfoServiceI;
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
 * @Date 2019/10/12 0012 12:59
 */
@Service
public class ProjectBlastWorkInfoServiceImpl implements ProjectBlastWorkInfoServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectBlastWorkInfoDaoI projectBlastWorkInfoDaoI;

    @Override
    public ProjectBlastWorkInfo get(Long id) throws IOException {
        return projectBlastWorkInfoDaoI.get(id);
    }

    @Override
    public ProjectBlastWorkInfo save(ProjectBlastWorkInfo log) throws JsonProcessingException {
        return projectBlastWorkInfoDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectBlastWorkInfoDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectBlastWorkInfoDaoI.delete(ids);
    }

    @Override
    public Page<ProjectBlastWorkInfo> query() {
        return projectBlastWorkInfoDaoI.query();
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Specification<ProjectBlastWorkInfo> spec) {
        return projectBlastWorkInfoDaoI.query(spec);
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Pageable pageable, Specification<ProjectBlastWorkInfo> spec) {
        return projectBlastWorkInfoDaoI.query(pageable, spec);
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Pageable pageable) {
        return projectBlastWorkInfoDaoI.query(pageable);
    }

    @Override
    public List<ProjectBlastWorkInfo> getAll() {
        return projectBlastWorkInfoDaoI.getAll();
    }
}
