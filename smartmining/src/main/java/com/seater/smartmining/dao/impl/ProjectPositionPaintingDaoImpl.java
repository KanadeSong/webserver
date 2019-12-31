package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectPositionPaintingDaoI;
import com.seater.smartmining.entity.ProjectPositionPainting;
import com.seater.smartmining.entity.repository.ProjectPositionPaintingRepository;
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
 * @Date 2019/12/18 0018 9:43
 */
@Component
public class ProjectPositionPaintingDaoImpl implements ProjectPositionPaintingDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectPositionPaintingRepository projectPositionPaintingRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectpositionpainting:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectPositionPainting get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectPositionPainting.class);
        }
        if(projectPositionPaintingRepository.existsById(id)){
            ProjectPositionPainting log = projectPositionPaintingRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectPositionPainting save(ProjectPositionPainting log) throws IOException {
        ProjectPositionPainting log1 = projectPositionPaintingRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectPositionPaintingRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectPositionPainting> query() {
        return projectPositionPaintingRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec) {
        return projectPositionPaintingRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectPositionPainting> query(Pageable pageable) {
        return projectPositionPaintingRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectPositionPainting> query(Specification<ProjectPositionPainting> spec, Pageable pageable) {
        return projectPositionPaintingRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectPositionPainting> getAll() {
        return projectPositionPaintingRepository.findAll();
    }
}
