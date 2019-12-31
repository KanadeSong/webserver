package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectHourPriceDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectHourPrice;
import com.seater.smartmining.entity.repository.ProjectHourPriceRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectHourPriceDaoImpl implements ProjectHourPriceDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectHourPriceRepository projectHourPriceRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projecthourprice:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec, Pageable pageable) {
        return projectHourPriceRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec) {
        return projectHourPriceRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectHourPrice> query(Pageable pageable) {
        return projectHourPriceRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectHourPrice> query() {
        return projectHourPriceRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectHourPrice get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectHourPrice.class);
        }
        if(projectHourPriceRepository.existsById(id))
        {
            ProjectHourPrice log = projectHourPriceRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectHourPrice save(ProjectHourPrice log) throws IOException {
        ProjectHourPrice log1 = projectHourPriceRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectHourPriceRepository.deleteById(id);
    }

    @Override
    public List<ProjectHourPrice> getAll() {
        return projectHourPriceRepository.findAll();
    }

    @Override
    public List<ProjectHourPrice> getByProjectIdAndBrandIdAndModelIdAndCarType(Long projectId, Long brandId, Long modelId, Integer carType) {
        return projectHourPriceRepository.getByProjectIdAndBrandIdAndModelIdAndCarType(projectId, brandId, modelId, carType);
    }

    @Override
    public List<ProjectHourPrice> getAllByProjectId(Long projectId) {
        return projectHourPriceRepository.getAllByProjectId(projectId);
    }

}