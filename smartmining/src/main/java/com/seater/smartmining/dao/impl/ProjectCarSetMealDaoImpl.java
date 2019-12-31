package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarSetMealDaoI;
import com.seater.smartmining.entity.ProjectCarSetMeal;
import com.seater.smartmining.entity.repository.ProjectCarSetMealRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:31
 */
@Component
public class ProjectCarSetMealDaoImpl implements ProjectCarSetMealDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarSetMealRepository projectCarSetMealRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarsetmeal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCarSetMeal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCarSetMeal.class);
        }
        if(projectCarSetMealRepository.existsById(id))
        {
            ProjectCarSetMeal log = projectCarSetMealRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCarSetMeal save(ProjectCarSetMeal log) throws IOException {
        ProjectCarSetMeal log1 = projectCarSetMealRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarSetMealRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectCarSetMeal> query() {
        return projectCarSetMealRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec) {
        return projectCarSetMealRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarSetMeal> query(Pageable pageable) {
        return projectCarSetMealRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCarSetMeal> query(Specification<ProjectCarSetMeal> spec, Pageable pageable) {
        return projectCarSetMealRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectCarSetMeal> getAll() {
        return projectCarSetMealRepository.findAll();
    }
}
