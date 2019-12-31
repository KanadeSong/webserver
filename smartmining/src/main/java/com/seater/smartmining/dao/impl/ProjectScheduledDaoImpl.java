package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectScheduledDaoI;
import com.seater.smartmining.entity.ProjectScheduled;
import com.seater.smartmining.entity.repository.ProjectScheduledRepository;
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

@Deprecated
@Component
public class ProjectScheduledDaoImpl implements ProjectScheduledDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectScheduledRepository projectScheduledRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectscheduled:";

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
    public Page<ProjectScheduled> query(Specification<ProjectScheduled> spec, Pageable pageable) {
        return projectScheduledRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectScheduled> query(Specification<ProjectScheduled> spec) {
        return projectScheduledRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectScheduled> query(Pageable pageable) {
        return projectScheduledRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectScheduled> query() {
        return projectScheduledRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectScheduled get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectScheduled.class);
        }
        if(projectScheduledRepository.existsById(id))
        {
            ProjectScheduled log = projectScheduledRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectScheduled save(ProjectScheduled log) throws IOException {
        ProjectScheduled log1 = projectScheduledRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectScheduledRepository.deleteById(id);
    }

    @Override
    public List<ProjectScheduled> getAll() {
        return projectScheduledRepository.findAll();
    }

    @Override
    public List<ProjectScheduled> getByProjectIdOrderById(Long projectId) {
        return projectScheduledRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectScheduled getByProjectIdAndDiggingMachineIdAndCarIdOrderById(Long projectId, Long diggingMachineId, Long carId) {
        return projectScheduledRepository.getByProjectIdAndDiggingMachineIdAndCarIdOrderById(projectId, diggingMachineId, carId);
    }

    @Override
    public List<Map> getByProjectIdPage(Long projectId, int cur, int page) {
        return projectScheduledRepository.getByProjectIdPage(projectId, cur, page);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndDiggingMachineId(Long projectId, Long machineId) {
        return projectScheduledRepository.getAllByProjectIdAndDiggingMachineId(projectId, machineId);
    }

    @Override
    public void deleteByDiggingMachineIdAndProjectId(Long machineId, Long projectId) {
        projectScheduledRepository.deleteByDiggingMachineIdAndProjectId(machineId, projectId);
    }

    @Override
    public void deleteByCarIdAndProjectId(Long carId, Long projectId) {
        projectScheduledRepository.deleteByCarIdAndProjectId(carId, projectId);
    }

    @Override
    public List<Map> getByProjectIdAndCarIdOnDigging(Long projectId, Long carId) {
        return projectScheduledRepository.getByProjectIdAndCarIdOnDigging(projectId, carId);
    }

    @Override
    public List<Map> getByProjectIdCount(Long projectId) {
        return projectScheduledRepository.getByProjectIdCount(projectId);
    }

    @Override
    public ProjectScheduled getByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledRepository.getByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public List<Map> getByProjectIdOnGroupId(Long projectId, int current, int page) {
        return projectScheduledRepository.getByProjectIdOnGroupId(projectId, current, page);
    }

    @Override
    public List<Map> getByProjectIdAndManagerIdOnGroupId(Long projectId, String managerId, int current, int page) {
        return projectScheduledRepository.getByProjectIdAndManagerIdOnGroupId(projectId, managerId, current, page);
    }

    @Override
    public List<Map> getByAllProjectIdOnGroupId(Long projectId) {
        return projectScheduledRepository.getByAllProjectIdOnGroupId(projectId);
    }

    @Override
    public List<Map> getByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return projectScheduledRepository.getByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineId(Long projectId, Long machineId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndDiggingMachineId(projectId, machineId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(Long projectId, Long machineId, String managerId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, machineId, managerId);
    }

    @Override
    public List<Map> getByProjectIdAndDiggingMachineId(Long projectId, Long diggingMachineId) {
        return projectScheduledRepository.getByProjectIdAndDiggingMachineId(projectId, diggingMachineId);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledRepository.getAllByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public void deleteByDiggingMachineCodeAndProjectId(String machineCode, Long projectId) {
        projectScheduledRepository.deleteByDiggingMachineCodeAndProjectId(machineCode, projectId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndManagerId(Long projectId, Long carId, String managerId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndCarIdAndManagerId(projectId, carId, managerId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(projectId, carId, machineId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(Long projectId, Long carId, Long machineId, String managerId) {
        return projectScheduledRepository.getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(projectId, carId, machineId, managerId);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId) {
        return projectScheduledRepository.getAllByProjectIdAndCarIdAndDiggingMachineId(projectId, carId, machineId);
    }

    @Override
    public List<ProjectScheduled> queryWx(Specification<ProjectScheduled> spec) {
        return projectScheduledRepository.findAll(spec);
    }

    @Override
    public void saveOrModify(ProjectScheduled projectScheduled) {
        projectScheduledRepository.saveOrModify(projectScheduled);
    }

    @Override
    public List<Map> getByProjectIdAndGroupCodeOrderByDiggingMachineId(Long projectId, String groupCode) {
        return projectScheduledRepository.getByProjectIdAndGroupCodeOrderByDiggingMachineId(projectId, groupCode);
    }

    @Override
    public List<Map> getByAllProjectIdAndPricingType(Long projectId, Integer pricingType) {
        return projectScheduledRepository.getByAllProjectIdAndPricingType(projectId, pricingType);
    }

    @Override
    public List<ProjectScheduled> getByProjectIdAndCarIdOrderById(Long projectId, Long carId) {
        return projectScheduledRepository.getByProjectIdAndCarIdOrderById(projectId, carId);
    }

    @Override
    public List<ProjectScheduled> getByProjectIdAndDiggingMachineIdOrderById(Long projectId, Long diggingMachineId) {
        return projectScheduledRepository.getByProjectIdAndDiggingMachineIdOrderById(projectId, diggingMachineId);
    }
}
