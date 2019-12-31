package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingPartCountTotalDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCountTotal;
import com.seater.smartmining.entity.repository.ProjectDiggingPartCountTotalRespository;
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
 * @Date 2019/2/28 0028 15:45
 */
@Component
public class ProjectDiggingPartCountTotalDaoImpl implements ProjectDiggingPartCountTotalDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingPartCountTotalRespository projectDiggingPartCountTotalRespository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingpartcounttotal:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingPartCountTotal get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingPartCountTotal.class);
        }
        if(projectDiggingPartCountTotalRespository.existsById(id)){
            ProjectDiggingPartCountTotal log = projectDiggingPartCountTotalRespository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingPartCountTotal save(ProjectDiggingPartCountTotal log) throws JsonProcessingException {
        ProjectDiggingPartCountTotal log1 = projectDiggingPartCountTotalRespository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingPartCountTotalRespository.deleteById(id);
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query() {
        return projectDiggingPartCountTotalRespository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec) {
        return projectDiggingPartCountTotalRespository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Pageable pageable) {
        return projectDiggingPartCountTotalRespository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountTotal> query(Specification<ProjectDiggingPartCountTotal> spec, Pageable pageable) {
        return projectDiggingPartCountTotalRespository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountTotal> getAll() {
        return projectDiggingPartCountTotalRespository.findAll();
    }

    @Override
    public List<ProjectDiggingPartCountTotal> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        return projectDiggingPartCountTotalRespository.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountTotalRespository.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }
}
