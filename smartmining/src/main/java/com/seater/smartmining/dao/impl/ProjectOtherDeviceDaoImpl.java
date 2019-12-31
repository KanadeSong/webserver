package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectOtherDeviceDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.repository.ProjectOtherDeviceRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectOtherDeviceDaoImpl implements ProjectOtherDeviceDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectOtherDeviceRepository projectOtherDeviceRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectotherdevice:";

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
    public Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec, Pageable pageable) {
        return projectOtherDeviceRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectOtherDevice> query(Specification<ProjectOtherDevice> spec) {
        return projectOtherDeviceRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectOtherDevice> query(Pageable pageable) {
        return projectOtherDeviceRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectOtherDevice> query() {
        return projectOtherDeviceRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectOtherDevice get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectOtherDevice.class);
        }
        if(projectOtherDeviceRepository.existsById(id))
        {
            ProjectOtherDevice log = projectOtherDeviceRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectOtherDevice save(ProjectOtherDevice log) throws IOException {
        ProjectOtherDevice log1 = projectOtherDeviceRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectOtherDeviceRepository.deleteById(id);
    }

    @Override
    public List<ProjectOtherDevice> getAll() {
        return projectOtherDeviceRepository.findAll();
    }

    @Override
    public List<ProjectOtherDevice> getByProjectIdOrderById(Long projectId) {
        return projectOtherDeviceRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public List<ProjectOtherDevice> queryWx(Specification<ProjectOtherDevice> spec) {
        return projectOtherDeviceRepository.findAll(spec);
    }

    @Override
    public ProjectOtherDevice getAllByUid(String uid) {
        return projectOtherDeviceRepository.getAllByUid(uid);
    }

    @Override
    public List<ProjectOtherDevice> getByProjectIdAndCarTypeIs(Long projectId, CarType carType) {
        return projectOtherDeviceRepository.getByProjectIdAndCarTypeIs(projectId, carType);
    }

    @Override
    public void saveAll(List<ProjectOtherDevice> projectOtherDeviceList) {
        projectOtherDeviceRepository.saveAll(projectOtherDeviceList);
    }

    @Override
    public ProjectOtherDevice getAllByProjectIdAndCodeAndCarType(Long projectId, String code, CarType carType) {
        return projectOtherDeviceRepository.getAllByProjectIdAndCodeAndCarType(projectId, code, carType);
    }

    @Override
    public List<ProjectOtherDevice> getAllByProjectId(Long projectId) {
        return projectOtherDeviceRepository.getAllByProjectId(projectId);
    }

}
