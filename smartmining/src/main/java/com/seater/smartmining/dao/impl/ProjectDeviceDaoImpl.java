package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDeviceDaoI;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.entity.repository.ProjectDeviceRepository;
import com.seater.smartmining.enums.ProjectDeviceType;
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
import java.util.concurrent.TimeUnit;

@Component
public class ProjectDeviceDaoImpl implements ProjectDeviceDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDeviceRepository projectDeviceRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdevice:";

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
    public Page<ProjectDevice> query(Specification<ProjectDevice> spec, Pageable pageable) {
        return projectDeviceRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectDevice> query(Specification<ProjectDevice> spec) {
        return projectDeviceRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDevice> query(Pageable pageable) {
        return projectDeviceRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDevice> query() {
        return projectDeviceRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDevice get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDevice.class);
        }
        if(projectDeviceRepository.existsById(id))
        {
            ProjectDevice log = projectDeviceRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDevice save(ProjectDevice log) throws IOException {
        ProjectDevice log1 = projectDeviceRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDeviceRepository.deleteById(id);
    }

    @Override
    public List<ProjectDevice> getAll() {
        return projectDeviceRepository.findAll();
    }

    @Override
    public ProjectDevice getByProjectIdAndUid(Long projectId, String uid) {
        return projectDeviceRepository.getByProjectIdAndUid(projectId, uid);
    }

    @Override
    public ProjectDevice getByUid(String uid) {
        return projectDeviceRepository.getByUid(uid);
    }

    @Override
    public ProjectDevice getAllByProjectIdAndCodeAndDeviceType(Long projectId, String code, Integer type) {
        return projectDeviceRepository.getAllByProjectIdAndCodeAndDeviceType(projectId, code, type);
    }

    @Override
    public List<ProjectDevice> getAllByAndFileName(String fileName) {
        return projectDeviceRepository.getAllByAndFileName(fileName);
    }

    @Override
    public List<ProjectDevice> getAllByCodeAndDeviceType(String code, Integer deviceType) {
        return projectDeviceRepository.getAllByCodeAndDeviceType(code, deviceType);
    }

    @Override
    public ProjectDevice getAllByDeviceCode(String deviceCode) {
        return projectDeviceRepository.getAllByDeviceCode(deviceCode);
    }

    @Override
    public List<ProjectDevice> getAllByProjectId(Long projectId) {
        return projectDeviceRepository.getAllByProjectId(projectId);
    }

    @Override
    public void batchSave(List<ProjectDevice> projectDeviceList) {
        projectDeviceRepository.saveAll(projectDeviceList);
    }

    @Override
    public List<ProjectDevice> getAllByProjectIdAndDeviceType(Long projectId, Integer deviceType) {
        return projectDeviceRepository.getAllByProjectIdAndDeviceType(projectId, deviceType);
    }

    @Override
    public List<ProjectDevice> getAllCarDeviceAndMachineDevice(Long projectId) {
        return projectDeviceRepository.getAllCarDeviceAndMachineDevice(projectId);
    }

}
