package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingPartCountGrandDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.ProjectDiggingPartCountGrand;
import com.seater.smartmining.entity.repository.ProjectDiggingPartCountGrandRepository;
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
 * @Date 2019/2/28 0028 17:12
 */
@Component
public class ProjectDiggingPartCountGrandDaoImpl implements ProjectDiggingPartCountGrandDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingPartCountGrandRepository projectDiggingPartCountGrandRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingpartcountgrand:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingPartCountGrand get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectDiggingPartCountGrand.class);
        }
        if(projectDiggingPartCountGrandRepository.existsById(id)){
            ProjectDiggingPartCountGrand log = projectDiggingPartCountGrandRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingPartCountGrand save(ProjectDiggingPartCountGrand log) throws JsonProcessingException {
        ProjectDiggingPartCountGrand log1 = projectDiggingPartCountGrandRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return null;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingPartCountGrandRepository.deleteById(id);
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query() {
        return projectDiggingPartCountGrandRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec) {
        return projectDiggingPartCountGrandRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Pageable pageable) {
        return projectDiggingPartCountGrandRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCountGrand> query(Specification<ProjectDiggingPartCountGrand> spec, Pageable pageable) {
        return projectDiggingPartCountGrandRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCountGrand> getAll() {
        return projectDiggingPartCountGrandRepository.findAll();
    }

    @Override
    public List<ProjectDiggingPartCountGrand> getAllByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        return projectDiggingPartCountGrandRepository.getAllByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountGrandRepository.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate, machineId);
    }

    @Override
    public ProjectDiggingPartCountGrand getAllByProjectIdAndTotalId(Long projectId, Long totalId) {
        return projectDiggingPartCountGrandRepository.getAllByProjectIdAndTotalId(projectId, totalId);
    }
}
