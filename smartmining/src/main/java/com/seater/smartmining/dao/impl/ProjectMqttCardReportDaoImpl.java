package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectMqttCardReportDaoI;
import com.seater.smartmining.entity.ProjectMqttCardReport;
import com.seater.smartmining.entity.repository.ProjectMqttCardReportRepository;
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
 * @Date 2019/11/3 0003 14:55
 */
@Component
public class ProjectMqttCardReportDaoImpl implements ProjectMqttCardReportDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMqttCardReportRepository projectMqttCardReportRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectmqttcardreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectMqttCardReport get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectMqttCardReport.class);
        }
        if(projectMqttCardReportRepository.existsById(id)){
            ProjectMqttCardReport log = projectMqttCardReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectMqttCardReport save(ProjectMqttCardReport log) throws JsonProcessingException {
        ProjectMqttCardReport log1 = projectMqttCardReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectMqttCardReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectMqttCardReport> query() {
        return projectMqttCardReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec) {
        return projectMqttCardReportRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectMqttCardReport> query(Pageable pageable) {
        return projectMqttCardReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectMqttCardReport> query(Specification<ProjectMqttCardReport> spec, Pageable pageable) {
        return projectMqttCardReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectMqttCardReport> getAll() {
        return projectMqttCardReportRepository.findAll();
    }

    @Override
    public void deleteAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        projectMqttCardReportRepository.deleteAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportRepository.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndTimeDischarge(Long projectId, String carCode, Date startTime, Date endTime) {
        return projectMqttCardReportRepository.getAllByProjectIdAndCarCodeAndTimeDischarge(projectId, carCode, startTime, endTime);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndTimeDischarge(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportRepository.getAllByProjectIdAndTimeDischarge(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getReportCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportRepository.getReportCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getErrorCountByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportRepository.getErrorCountByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public List<Map> getErrorCodeByProjectIdAndDateIdentificationAndShift(Long projectId, Date date, Integer shift) {
        return projectMqttCardReportRepository.getErrorCodeByProjectIdAndDateIdentificationAndShift(projectId, date, shift);
    }

    @Override
    public Map getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift) {
        return projectMqttCardReportRepository.getTotalCountByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift);
    }

    @Override
    public List<Map> getUnValidCountByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportRepository.getUnValidCountByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getUnValidCountMonthByProjectIdAndDateIdentification(Long projectId, Date startTime, Date endTime) {
        return projectMqttCardReportRepository.getUnValidCountMonthByProjectIdAndDateIdentification(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectMqttCardReport> getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(Long projectId, String carCode, Date date, Integer shift) {
        return projectMqttCardReportRepository.getAllByProjectIdAndCarCodeAndDateIdentificationAndShift(projectId, carCode, date, shift);
    }
}
