package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectAppStatisticsByMachineDaoI;
import com.seater.smartmining.entity.ProjectAppStatisticsByMachine;
import com.seater.smartmining.entity.repository.ProjectAppStatisticsByMachineRepository;
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
 * @Date 2019/6/9 0009 12:47
 */
@Component
public class ProjectAppStatisticsByMachineDaoImpl implements ProjectAppStatisticsByMachineDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectAppStatisticsByMachineRepository projectAppStatisticsByMachineRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectappstatisticsbymachine:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectAppStatisticsByMachine get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectAppStatisticsByMachine.class);
        }
        if(projectAppStatisticsByMachineRepository.existsById(id))
        {
            ProjectAppStatisticsByMachine log = projectAppStatisticsByMachineRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectAppStatisticsByMachine save(ProjectAppStatisticsByMachine log) throws JsonProcessingException {
        ProjectAppStatisticsByMachine log1 = projectAppStatisticsByMachineRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectAppStatisticsByMachineRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query() {
        return projectAppStatisticsByMachineRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec) {
        return projectAppStatisticsByMachineRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Pageable pageable) {
        return projectAppStatisticsByMachineRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectAppStatisticsByMachine> query(Specification<ProjectAppStatisticsByMachine> spec, Pageable pageable) {
        return projectAppStatisticsByMachineRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectAppStatisticsByMachine> getAll() {
        return projectAppStatisticsByMachineRepository.findAll();
    }

    @Override
    public void deleteByCreateDate(Date createDate, Long projectId) {
        projectAppStatisticsByMachineRepository.deleteByCreateDate(createDate, projectId);
    }

    @Override
    public ProjectAppStatisticsByMachine getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date, String machineCode) {
        return projectAppStatisticsByMachineRepository.getAllByProjectIdAndShiftsAndCreateDate(projectId, value, date, machineCode);
    }

    @Override
    public List<ProjectAppStatisticsByMachine> getAllByProjectIdAndShiftsAndCreateDate(Long projectId, Integer value, Date date) {
        return projectAppStatisticsByMachineRepository.getAllByProjectIdAndShiftsAndCreateDate(projectId, value, date);
    }

    @Override
    public void batchSave(List<ProjectAppStatisticsByMachine> saveList) {
        projectAppStatisticsByMachineRepository.saveAll(saveList);
    }
}
