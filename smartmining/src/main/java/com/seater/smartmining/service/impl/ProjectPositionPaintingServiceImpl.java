package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectPositionPaintingDaoI;
import com.seater.smartmining.entity.ProjectPositionPainting;
import com.seater.smartmining.service.ProjectPositionPaintingServiceI;
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
 * @Date 2019/12/18 0018 10:41
 */
@Service
public class ProjectPositionPaintingServiceImpl implements ProjectPositionPaintingServiceI {

    @Autowired
    private ProjectPositionPaintingDaoI projectPositionPaintingDaoI;

    @Override
    public ProjectPositionPainting get(Long id) throws IOException {
        return projectPositionPaintingDaoI.get(id);
    }

    @Override
    public ProjectPositionPainting save(ProjectPositionPainting log) throws IOException {
        return projectPositionPaintingDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectPositionPaintingDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectPositionPaintingDaoI.delete(ids);
    }

    @Override
    public Page<ProjectPositionPainting> query() {
        return projectPositionPaintingDaoI.query();
    }

    @Override
    public Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec) {
        return projectPositionPaintingDaoI.query(spec);
    }

    @Override
    public Page<ProjectPositionPainting> query(Pageable pageable) {
        return projectPositionPaintingDaoI.query(pageable);
    }

    @Override
    public Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec, Pageable pageable) {
        return projectPositionPaintingDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectPositionPainting> getAll() {
        return projectPositionPaintingDaoI.getAll();
    }
}
