package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectWorkTimeByCarDaoI;
import com.seater.smartmining.entity.ProjectWorkTimeByCar;
import com.seater.smartmining.entity.repository.ProjectWorkTimeByCarRepository;
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
 * @Date 2019/11/18 0018 11:22
 */
@Component
public class ProjectWorkTimeByCarDaoImpl implements ProjectWorkTimeByCarDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectWorkTimeByCarRepository projectWorkTimeByCarRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectworktimebycar:";
    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectWorkTimeByCar get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectWorkTimeByCar.class);
        }
        if(projectWorkTimeByCarRepository.existsById(id)){
            ProjectWorkTimeByCar log = projectWorkTimeByCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectWorkTimeByCar save(ProjectWorkTimeByCar log) throws IOException {
        ProjectWorkTimeByCar log1 = projectWorkTimeByCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectWorkTimeByCarRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectWorkTimeByCar> query() {
        return projectWorkTimeByCarRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec) {
        return projectWorkTimeByCarRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Pageable pageable) {
        return projectWorkTimeByCarRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectWorkTimeByCar> query(Specification<ProjectWorkTimeByCar> spec, Pageable pageable) {
        return projectWorkTimeByCarRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectWorkTimeByCar> getAll() {
        return projectWorkTimeByCarRepository.findAll();
    }

    @Override
    public List<ProjectWorkTimeByCar> getAllByProjectIdAndCarCodeAndStatus(Long projectId, String carCode, Integer status) {
        return projectWorkTimeByCarRepository.getAllByProjectIdAndCarCodeAndStatus(projectId, carCode, status);
    }
}
