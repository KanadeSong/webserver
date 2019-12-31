package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDiggingReportByPlaceDaoI;
import com.seater.smartmining.entity.ProjectDiggingReportByPlace;
import com.seater.smartmining.entity.repository.ProjectDiggingReportByPlaceRepository;
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
 * @Date 2019/8/19 0019 11:21
 */
@Component
public class ProjectDiggingReportByPlaceDaoImpl implements ProjectDiggingReportByPlaceDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingReportByPlaceRepository projectDiggingReportByPlaceRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdiggingreportbyplace:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectDiggingReportByPlace get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDiggingReportByPlace.class);
        }
        if(projectDiggingReportByPlaceRepository.existsById(id))
        {
            ProjectDiggingReportByPlace log = projectDiggingReportByPlaceRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDiggingReportByPlace save(ProjectDiggingReportByPlace log) throws IOException {
        ProjectDiggingReportByPlace log1 = projectDiggingReportByPlaceRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDiggingReportByPlaceRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query() {
        return projectDiggingReportByPlaceRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec) {
        return projectDiggingReportByPlaceRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Pageable pageable) {
        return projectDiggingReportByPlaceRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDiggingReportByPlace> query(Specification<ProjectDiggingReportByPlace> spec, Pageable pageable) {
        return projectDiggingReportByPlaceRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAll() {
        return projectDiggingReportByPlaceRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectDiggingReportByPlace> placeList) {
        projectDiggingReportByPlaceRepository.saveAll(placeList);
    }

    @Override
    public void deleteByProjectIdAndAndDateIdentification(Long projectId, Date date) {
        projectDiggingReportByPlaceRepository.deleteByProjectIdAndAndDateIdentification(projectId, date);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectDiggingReportByPlaceRepository.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDiggingReportByPlace> getAllByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime, String machineCode) {
        return projectDiggingReportByPlaceRepository.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime, machineCode);
    }
}
