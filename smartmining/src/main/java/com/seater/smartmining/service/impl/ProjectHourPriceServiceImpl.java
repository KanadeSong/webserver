package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectHourPriceDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectHourPrice;
import com.seater.smartmining.service.ProjectHourPriceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectHourPriceServiceImpl implements ProjectHourPriceServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectHourPriceDaoI projectHourPriceDaoI;

    @Override
    public ProjectHourPrice get(Long id) throws IOException{
        return projectHourPriceDaoI.get(id);
    }

    @Override
    public ProjectHourPrice save(ProjectHourPrice log) throws IOException{
        return projectHourPriceDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectHourPriceDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectHourPriceDaoI.delete(ids);
    }

    @Override
    public Page<ProjectHourPrice> query(Pageable pageable) {
        return projectHourPriceDaoI.query(pageable);
    }

    @Override
    public Page<ProjectHourPrice> query() {
        return projectHourPriceDaoI.query();
    }

    @Override
    public Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec) {
        return projectHourPriceDaoI.query(spec);
    }

    @Override
    public Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec, Pageable pageable) {
        return projectHourPriceDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectHourPrice> getAll() {
        return projectHourPriceDaoI.getAll();
    }

    @Override
    public ProjectHourPrice getByProjectIdAndBrandIdAndModelIdAndCarType(Long projectId, Long brandId, Long modelId, Integer carType){
        List<ProjectHourPrice> projectHourPriceList = projectHourPriceDaoI.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, brandId, modelId, carType);
        if(projectHourPriceList.size()>0){
            return projectHourPriceList.get(0);
        }
        return null;
    }

    @Override
    public List<ProjectHourPrice> getAllByProjectId(Long projectId) {
        return projectHourPriceDaoI.getAllByProjectId(projectId);
    }

}
