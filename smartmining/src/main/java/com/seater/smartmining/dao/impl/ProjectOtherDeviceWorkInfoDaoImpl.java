package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectOtherDeviceWorkInfoDaoI;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.ProjectOtherDeviceWorkInfo;
import com.seater.smartmining.entity.repository.ProjectOtherDeviceWorkInfoRepository;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
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
 * @Date 2019/10/10 0010 15:56
 */
@Component
public class ProjectOtherDeviceWorkInfoDaoImpl implements ProjectOtherDeviceWorkInfoDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectOtherDeviceWorkInfoRepository projectOtherDeviceWorkInfoRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectotherdeviceworkinfo:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectOtherDeviceWorkInfo get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectOtherDeviceWorkInfo.class);
        }
        if(projectOtherDeviceWorkInfoRepository.existsById(id))
        {
            ProjectOtherDeviceWorkInfo log = projectOtherDeviceWorkInfoRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectOtherDeviceWorkInfo save(ProjectOtherDeviceWorkInfo log) throws IOException {
        ProjectOtherDeviceWorkInfo log1 = projectOtherDeviceWorkInfoRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectOtherDeviceWorkInfoRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query() {
        return projectOtherDeviceWorkInfoRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec) {
        return projectOtherDeviceWorkInfoRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Pageable pageable) {
        return projectOtherDeviceWorkInfoRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec, Pageable pageable) {
        return projectOtherDeviceWorkInfoRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectOtherDeviceWorkInfo> getAll() {
        return projectOtherDeviceWorkInfoRepository.findAll();
    }

    @Override
    public ProjectOtherDeviceWorkInfo getAllByProjectIdAndCodeAndCarType(Long projectId, String code, Integer carType, Integer status) {
        return projectOtherDeviceWorkInfoRepository.getAllByProjectIdAndCodeAndCarType(projectId, code, carType, status);
    }

    @Override
    public List<Map> getDayReportByProjectIdAndDateIdentificationAndCarType(Long projectId, Date date, Integer carType) {
        return projectOtherDeviceWorkInfoRepository.getDayReportByProjectIdAndDateIdentificationAndCarType(projectId, date, carType);
    }
}
