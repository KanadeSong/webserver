package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSettlementTotalDaoI;
import com.seater.smartmining.entity.ProjectSettlementTotal;
import com.seater.smartmining.entity.repository.ProjectSettlementTotalRepository;
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
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/2 0002 17:50
 */
@Component
public class ProjectSettlementTotalDaoImpl implements ProjectSettlementTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSettlementTotalRepository projectSettlementTotalRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectsettlementtotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSettlementTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSettlementTotal.class);
        }
        if(projectSettlementTotalRepository.existsById(id)){
            ProjectSettlementTotal log = projectSettlementTotalRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSettlementTotal save(ProjectSettlementTotal log) throws JsonProcessingException {
        ProjectSettlementTotal log1 = projectSettlementTotalRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSettlementTotalRepository.deleteById(id);
    }

    @Override
    public Page<ProjectSettlementTotal> query() {
        return projectSettlementTotalRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec) {
        return projectSettlementTotalRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSettlementTotal> query(Pageable pageable) {
        return projectSettlementTotalRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSettlementTotal> query(Specification<ProjectSettlementTotal> spec, Pageable pageable) {
        return projectSettlementTotalRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSettlementTotal> getAll() {
        return projectSettlementTotalRepository.findAll();
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate, Long carId) {
        projectSettlementTotalRepository.deleteByProjectIdAndReportDate(projectId, reportDate, carId);
    }

    @Override
    public List<ProjectSettlementTotal> getByProjectIdAndCarIdAndReportDate(Long projectId, Long carId, Date reportDate) {
        return projectSettlementTotalRepository.getByProjectIdAndCarIdAndReportDate(projectId, carId, reportDate);
    }
}
