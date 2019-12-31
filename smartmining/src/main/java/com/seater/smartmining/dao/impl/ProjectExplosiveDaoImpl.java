package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectExplosiveDaoI;
import com.seater.smartmining.entity.ProjectExplosive;
import com.seater.smartmining.entity.repository.ProjectExplosiveRepository;
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
 * @Date 2019/10/10 0010 17:38
 */
@Component
public class ProjectExplosiveDaoImpl implements ProjectExplosiveDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectExplosiveRepository projectExplosiveRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectexplosive:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectExplosive get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectExplosive.class);
        }
        if(projectExplosiveRepository.existsById(id))
        {
            ProjectExplosive log = projectExplosiveRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectExplosive save(ProjectExplosive log) throws IOException {
        ProjectExplosive log1 = projectExplosiveRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectExplosiveRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectExplosive> query() {
        return projectExplosiveRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectExplosive> query(Specification<ProjectExplosive> spec) {
        return projectExplosiveRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectExplosive> query(Pageable pageable) {
        return projectExplosiveRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectExplosive> query(Specification<ProjectExplosive> spec, Pageable pageable) {
        return projectExplosiveRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectExplosive> getAll() {
        return projectExplosiveRepository.findAll();
    }

    @Override
    public List<ProjectExplosive> getByProjectIdOrderById(Long projectId) {
        return projectExplosiveRepository.getAllByProjectIdOrderById(projectId);
    }
}
