package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectAppStatisticsByCarDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsByCar;
import com.seater.smartmining.entity.repository.ProjectAppStatisticsByCarRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/9 0009 12:00
 */
@Component
public class ProjectAppStatisticsByCarDaoImpl implements ProjectAppStatisticsByCarDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectAppStatisticsByCarRepository projectAppStatisticsByCarRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectappstatisticsbycar:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public ProjectAppStatisticsByCar get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectAppStatisticsByCar.class);
        }
        if(projectAppStatisticsByCarRepository.existsById(id))
        {
            ProjectAppStatisticsByCar log = projectAppStatisticsByCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectAppStatisticsByCar save(ProjectAppStatisticsByCar log) throws JsonProcessingException {
        ProjectAppStatisticsByCar log1 = projectAppStatisticsByCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectAppStatisticsByCarRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query() {
        return projectAppStatisticsByCarRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec) {
        return projectAppStatisticsByCarRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Pageable pageable) {
        return projectAppStatisticsByCarRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsByCar> query(Specification<ProjectAppStatisticsByCar> spec, Pageable pageable) {
        return projectAppStatisticsByCarRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsByCar> getAll() {
        return projectAppStatisticsByCarRepository.findAll();
    }

    @Override
    public void deleteByCreateDate(Date createDate, Long projectId) {
        projectAppStatisticsByCarRepository.deleteByCreateDate(createDate, projectId);
    }

    @Override
    public ProjectAppStatisticsByCar getAllByProjectIdAndCarCodeAndShiftAndDate(Long projectId, String carCode, Integer value, Date date) {
        return projectAppStatisticsByCarRepository.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, carCode, value, date);
    }

    @Override
    public List<ProjectAppStatisticsByCar> getAllByProjectIdAndShiftAndCreateDate(Long projectId, Integer value, Date date) {
        return projectAppStatisticsByCarRepository.getAllByProjectIdAndShiftAndCreateDate(projectId, value, date);
    }

    @Override
    public void batchSave(List<ProjectAppStatisticsByCar> saveList) {
        projectAppStatisticsByCarRepository.saveAll(saveList);
    }
}
