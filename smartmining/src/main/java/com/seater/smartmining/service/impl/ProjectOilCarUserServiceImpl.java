package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectOilCarUserDaoI;
import com.seater.smartmining.entity.ProjectOilCarUser;
import com.seater.smartmining.service.ProjectOilCarUserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/2 18:29
 */
@Service
public class ProjectOilCarUserServiceImpl implements ProjectOilCarUserServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectOilCarUserDaoI projectOilCarUserDaoI;

    @Override
    public ProjectOilCarUser get(Long id) throws IOException {
        return projectOilCarUserDaoI.get(id);
    }

    @Override
    public ProjectOilCarUser save(ProjectOilCarUser log) throws IOException {
        return projectOilCarUserDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectOilCarUserDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectOilCarUserDaoI.delete(ids);
    }

    @Override
    public void deleteByOilCarId(Long oilCarId) {
        projectOilCarUserDaoI.deleteByOilCarId(oilCarId);
    }

    @Override
    public Page<ProjectOilCarUser> query(Pageable pageable) {
        return projectOilCarUserDaoI.query(pageable);
    }

    @Override
    public Page<ProjectOilCarUser> query() {
        return projectOilCarUserDaoI.query();
    }

    @Override
    public Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec) {
        return projectOilCarUserDaoI.query(spec);
    }

    @Override
    public Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec, Pageable pageable) {
        return projectOilCarUserDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectOilCarUser> getAll() {
        return projectOilCarUserDaoI.getAll();
    }

    @Override
    public List<ProjectOilCarUser> queryWx(Specification<ProjectOilCarUser> spec) {
        return projectOilCarUserDaoI.queryWx(spec);
    }
}
