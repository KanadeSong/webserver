package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingMachineDaoI;
import com.seater.smartmining.entity.ProjectDiggingMachine;
import com.seater.smartmining.entity.repository.ProjectDiggingMachineRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectDiggingMachineDaoImpl implements ProjectDiggingMachineDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingMachineRepository projectDiggingMachineRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingmachine:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec, Pageable pageable) {
        return projectDiggingMachineRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingMachine> queryWx(Specification<ProjectDiggingMachine> spec) {
        return projectDiggingMachineRepository.findAll(spec);
    }

    @Override
    public Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec) {
        return projectDiggingMachineRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingMachine> query(Pageable pageable) {
        return projectDiggingMachineRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingMachine> query() {
        return projectDiggingMachineRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDiggingMachine get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingMachine.class);
        }
        if(projectDiggingMachineRepository.existsById(id))
        {
            ProjectDiggingMachine log = projectDiggingMachineRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingMachine save(ProjectDiggingMachine log) throws IOException {
        ProjectDiggingMachine log1 = projectDiggingMachineRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingMachineRepository.deleteById(id);
    }

    @Override
    public List<ProjectDiggingMachine> getAll() {
        return projectDiggingMachineRepository.findAll();
    }

    @Override
    public List<ProjectDiggingMachine> getByProjectIdOrderById(Long projectId) {
        return projectDiggingMachineRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public List<ProjectDiggingMachine> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectDiggingMachineRepository.getByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public List<ProjectDiggingMachine> getByProjectIdAndCode(Long projectId, String code) {
        return projectDiggingMachineRepository.getByProjectIdAndCode(projectId, code);
    }

    @Override
    public ProjectDiggingMachine getByProjectIdAndUid(Long projectId, String uid) {
        return projectDiggingMachineRepository.getByProjectIdAndUid(projectId, uid);
    }

    @Override
    public ProjectDiggingMachine getAllByUid(String uid) {
        return projectDiggingMachineRepository.getAllByUid(uid);
    }

    @Override
    public Map getAllCountByProjectId(Long projectId) {
        return projectDiggingMachineRepository.getAllCountByProjectId(projectId);
    }

    @Override
    public void setICCardByDiggingMachineId(Long diggingMachineId, String icCardNumber, Boolean icCardStatus) {
        projectDiggingMachineRepository.setICCardByDiggingMachineId(diggingMachineId, icCardNumber, icCardStatus);
    }

    @Override
    public List<ProjectDiggingMachine> getAllByProjectIdAndIsVaildAndSelected(Long projectId, Boolean selected) {
        return projectDiggingMachineRepository.getAllByProjectIdAndIsVaildAndSelected(projectId, selected);
    }

    @Override
    public void batchSave(List<ProjectDiggingMachine> projectDiggingMachineList) {
        projectDiggingMachineRepository.saveAll(projectDiggingMachineList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectDiggingMachineRepository.getAllByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public void updateSeleted(boolean selected, List<String> machineCodeList) {
        projectDiggingMachineRepository.updateSeleted(selected, machineCodeList);
    }

}
