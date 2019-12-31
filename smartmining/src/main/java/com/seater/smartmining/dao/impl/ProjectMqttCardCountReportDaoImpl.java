package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMqttCardCountReportDaoI;
import com.seater.smartmining.entity.ProjectMqttCardCountReport;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.entity.repository.ProjectMqttCardCountReportRepository;
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
 * @Date 2019/11/13 0013 12:45
 */
@Component
public class ProjectMqttCardCountReportDaoImpl implements ProjectMqttCardCountReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMqttCardCountReportRepository projectMqttCardCountReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmqttcardcountreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMqttCardCountReport get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMqttCardCountReport.class);
        }
        if(projectMqttCardCountReportRepository.existsById(id)){
            ProjectMqttCardCountReport log = projectMqttCardCountReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMqttCardCountReport save(ProjectMqttCardCountReport log) throws JsonProcessingException {
        ProjectMqttCardCountReport log1 = projectMqttCardCountReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMqttCardCountReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMqttCardCountReport> query() {
        return projectMqttCardCountReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec) {
        return projectMqttCardCountReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Pageable pageable) {
        return projectMqttCardCountReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMqttCardCountReport> query(Specification<ProjectMqttCardCountReport> spec, Pageable pageable) {
        return projectMqttCardCountReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMqttCardCountReport> getAll() {
        return projectMqttCardCountReportRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectMqttCardCountReport> saveList) {
        projectMqttCardCountReportRepository.saveAll(saveList);
    }

    @Override
    public void deleteByProjectIdAndCreateTime(Long projectId, Date date, Integer shift) {
        projectMqttCardCountReportRepository.deleteByProjectIdAndCreateTime(projectId, date, shift);
    }
}
