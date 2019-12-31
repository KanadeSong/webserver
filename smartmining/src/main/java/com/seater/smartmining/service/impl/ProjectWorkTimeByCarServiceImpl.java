package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectWorkTimeByCarDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeByCar;
import com.seater.smartmining.service.ProjectWorkTimeByCarServiceI;
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
 * @Date 2019/11/18 0018 11:28
 */
@Service
public class ProjectWorkTimeByCarServiceImpl implements ProjectWorkTimeByCarServiceI {

    @Autowired
    private ProjectWorkTimeByCarDaoI projectWorkTimeByCarDaoI;
    @Override
    public ProjectWorkTimeByCar get(Long id) throws IOException {
        return projectWorkTimeByCarDaoI.get(id);
    }

    @Override
    public ProjectWorkTimeByCar save(ProjectWorkTimeByCar log) throws IOException {
        return projectWorkTimeByCarDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectWorkTimeByCarDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectWorkTimeByCarDaoI.delete(ids);
    }

    @Override
    public Page<ProjectWorkTimeByCar> query() {
        return projectWorkTimeByCarDaoI.query();
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec) {
        return projectWorkTimeByCarDaoI.query(spec);
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Pageable pageable) {
        return projectWorkTimeByCarDaoI.query(pageable);
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec, Pageable pageable) {
        return projectWorkTimeByCarDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectWorkTimeByCar> getAll() {
        return projectWorkTimeByCarDaoI.getAll();
    }

    @Override
    public List<ProjectWorkTimeByCar> getAllByProjectIdAndCarCodeAndStatus(Long projectId, String carCode, Integer status) {
        return projectWorkTimeByCarDaoI.getAllByProjectIdAndCarCodeAndStatus(projectId, carCode, status);
    }
}
