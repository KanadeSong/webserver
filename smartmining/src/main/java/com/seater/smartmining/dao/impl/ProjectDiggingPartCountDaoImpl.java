package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingPartCountDaoI;
import com.seater.smartmining.entity.ProjectDiggingPartCount;
import com.seater.smartmining.entity.repository.ProjectDiggingPartCountRepository;
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
 * @Date 2019/2/28 0028 16:05
 */
@Component
public class ProjectDiggingPartCountDaoImpl implements ProjectDiggingPartCountDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectDiggingPartCountRepository projectDiggingPartCountRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingpartcount:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingPartCount get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectDiggingPartCount.class);
        }
        if(projectDiggingPartCountRepository.existsById(id)){
            ProjectDiggingPartCount log = projectDiggingPartCountRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectDiggingPartCount save(ProjectDiggingPartCount log) throws JsonProcessingException {
        ProjectDiggingPartCount log1 = projectDiggingPartCountRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingPartCountRepository.deleteById(id);
    }

    @Override
    public Page<ProjectDiggingPartCount> query() {
        return projectDiggingPartCountRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec) {
        return projectDiggingPartCountRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Pageable pageable) {
        return projectDiggingPartCountRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingPartCount> query(Specification<ProjectDiggingPartCount> spec, Pageable pageable) {
        return projectDiggingPartCountRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingPartCount> getAll() {
        return projectDiggingPartCountRepository.findAll();
    }

    @Override
    public List<ProjectDiggingPartCount> getByProjectIdAndTotalIdAndMachineId(Long projectId, Long totalId, Long machineId) {
        return projectDiggingPartCountRepository.getByProjectIdAndTotalIdAndMachineId(projectId, totalId, machineId);
    }

    @Override
    public void deleteByProjectIdAndReportDateAndMachineId(Long projectId, Date reportDate, Long machineId) {
        projectDiggingPartCountRepository.deleteByProjectIdAndReportDateAndMachineId(projectId, reportDate ,machineId);
    }

    @Override
    public List<Map> getMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectDiggingPartCountRepository.getMachineIdByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getByProjectIdAndMachineIdAndTime(Long projectId, Long machineId, Date time) {
        return projectDiggingPartCountRepository.getByProjectIdAndMachineIdAndTime(projectId, machineId, time);
    }
}
