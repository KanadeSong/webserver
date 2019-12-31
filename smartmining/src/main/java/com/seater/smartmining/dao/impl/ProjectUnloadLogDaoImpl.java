package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectUnloadLogDaoI;
import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.entity.ProjectUnloadLog;
import com.seater.smartmining.entity.repository.ProjectUnloadLogRepository;
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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectUnloadLogDaoImpl implements ProjectUnloadLogDaoI
{
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectUnloadLogRepository projectUnloadLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectunloadlog:";

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
    public Page<ProjectUnloadLog> query(Specification< ProjectUnloadLog> spec, Pageable pageable) {
        return projectUnloadLogRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectUnloadLog> queryAll(Specification<ProjectUnloadLog> specification) {
        return projectUnloadLogRepository.findAll(specification);
    }

    @Override
    public List<ProjectUnloadLog> queryParams(Specification<ProjectUnloadLog> spec) {
        return projectUnloadLogRepository.findAll(spec);
    }

    @Override
    public Page< ProjectUnloadLog> query(Specification< ProjectUnloadLog> spec) {
        return projectUnloadLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectUnloadLog> query(Pageable pageable) {
        return projectUnloadLogRepository.findAll(pageable);
    }

    @Override
    public Page< ProjectUnloadLog> query() {
        return projectUnloadLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public  ProjectUnloadLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectUnloadLog.class);
        }
        if(projectUnloadLogRepository.existsById(id))
        {
            ProjectUnloadLog log = projectUnloadLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public  ProjectUnloadLog save( ProjectUnloadLog log) throws IOException {
        ProjectUnloadLog log1 = projectUnloadLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectUnloadLogRepository.deleteById(id);
    }

    @Override
    public List<ProjectUnloadLog> getAll() {
        return projectUnloadLogRepository.findAll();
    }

    @Override
    public Date getMaxUnloadDateByCarCode(String carCode, Date date) {
        List<Date> list = projectUnloadLogRepository.getMaxUnloadDateByCarCode(carCode, date);
        if(list == null || list.size() <= 0)
            return null;

        return list.get(0);
    }

    @Override
    public List<ProjectUnloadLog> getAllByRecviceDate(Date receiveDate) {
        return projectUnloadLogRepository.getAllByRecviceDate(receiveDate);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischarge(Long projectId, Date startDate, Date endDate, String uid) {
        return projectUnloadLogRepository.getAllByProjectIDAndTimeDischarge(projectId, startDate, endDate, uid);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getAllByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCodeByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getCarCodeByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarCountByProjectIDAndTime(Long projectId, Date startTime, Date endTime, Date uploadTime) {
        return projectUnloadLogRepository.getCarCountByProjectIDAndTime(projectId, startTime, endTime, uploadTime);
    }

    @Override
    public List<Map> getUnValidByProjectIDAndTime(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getUnValidByProjectIDAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getUploadCountByCheck(Long projectId, Date startTime, Date endTime, Date checkTime) {
        return projectUnloadLogRepository.getUploadCountByCheck(projectId, startTime, endTime, checkTime);
    }

    @Override
    public List<Map> getReportInfoGroupBySlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids) {
        return projectUnloadLogRepository.getReportInfoGroupBySlagSite(projectId, startTime, endTime, ids);
    }

    @Override
    public List<Map> getReportInfoGroup(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getReportInfoGroup(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalReportInfoByCarCode(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getTotalReportInfoByCarCode(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getTotalReportInfoByCarCodeAndSlagSite(Long projectId, Date startTime, Date endTime, List<Long> ids) {
        return projectUnloadLogRepository.getTotalReportInfoByCarCodeAndSlagSite(projectId, startTime, endTime, ids);
    }

    @Override
    public ProjectUnloadLog getAllByProjectIDAndTimeDischargeAndCarCode(Long projectId, Date timeDischarge, String carCode) {
        return projectUnloadLogRepository.getAllByProjectIDAndTimeDischargeAndCarCode(projectId, timeDischarge, carCode);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaild() {
        return projectUnloadLogRepository.getAllByProjectIDAndTimeDischargeAndIsVaild();
    }

    @Override
    public Map getTotalCountByProjectIDAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectUnloadLogRepository.getTotalCountByProjectIDAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectUnloadLog> getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(Long projectId, Date startTime, Date endTime, Boolean valid, Boolean detail) {
        return projectUnloadLogRepository.getAllByProjectIDAndTimeDischargeAndIsVaildAndDetail(projectId, startTime, endTime, valid, detail);
    }
}
