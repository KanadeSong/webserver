package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarFillLogDaoI;
import com.seater.smartmining.entity.ProjectCarFillLog;
import com.seater.smartmining.entity.repository.ProjectCarFillLogRepository;
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

@Component
public class ProjectCarFillLogDaoImpl implements ProjectCarFillLogDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarFillLogRepository projectCarFillLogRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcarfilllog:";

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
    public Page<ProjectCarFillLog> query(Specification< ProjectCarFillLog> spec, Pageable pageable) {
        return projectCarFillLogRepository.findAll(spec, pageable);
    }

    @Override
    public Page< ProjectCarFillLog> query(Specification< ProjectCarFillLog> spec) {
        return projectCarFillLogRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCarFillLog> query(Pageable pageable) {
        return projectCarFillLogRepository.findAll(pageable);
    }

    @Override
    public Page< ProjectCarFillLog> query() {
        return projectCarFillLogRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public  ProjectCarFillLog get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,  ProjectCarFillLog.class);
        }
        if(projectCarFillLogRepository.existsById(id))
        {
            ProjectCarFillLog log = projectCarFillLogRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public  ProjectCarFillLog save( ProjectCarFillLog log) throws IOException {
        ProjectCarFillLog log1 = projectCarFillLogRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarFillLogRepository.deleteById(id);
    }

    @Override
    public List<ProjectCarFillLog> getAll() {
        return projectCarFillLogRepository.findAll();
    }

    @Override
    public List<ProjectCarFillLog> getByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getCarGrandTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getCarGrandTotalFillByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingGrandTotalFillByProjectIdAndTime(String code, Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getDiggingGrandTotalFillByProjectIdAndTime(code, projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTotalFillByProjectIdAndTimeGroupByCar(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getDiggingTotalFillByProjectIdAndTimeGroupByCar(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingTotalFillByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getDiggingTotalFillByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public List<Map> getHistoryDiggingTotalFillByProjectId(Long projectId) {
        return projectCarFillLogRepository.getHistoryDiggingTotalFillByProjectId(projectId);
    }

    @Override
    public Map getCarFillByProjectIdAAndCarIdAndTime(Long projectId, Long carId, Date beginDate, Date endDate) {
        return projectCarFillLogRepository.getCarFillByProjectIdAAndCarIdAndTime(projectId, carId, beginDate, endDate);
    }

    @Override
    public Map getCarFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date date) {
        return projectCarFillLogRepository.getCarFillByProjectIdAndCarIdAndTime(projectId, carId, date);
    }

    @Override
    public List<ProjectCarFillLog> queryWx(Specification<ProjectCarFillLog> spec) {
        return projectCarFillLogRepository.findAll(spec);
    }

    @Override
    public Map getDiggingFillByProjectIdAndCarIdAndTime(Long projectId, Long carId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getDiggingFillByProjectIdAndCarIdAndTime(projectId, carId, startTime, endTime);
    }

    @Override
    public List<Map> getDiggingMachineIdByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return projectCarFillLogRepository.getDiggingMachineIdByProjectIdAndTime(projectId, startTime, endTime);
    }

    @Override
    public Map getAllByProjectIdAndDate(Long projectId, Date reportDate) {
        return projectCarFillLogRepository.getAllByProjectIdAndDate(projectId, reportDate);
    }

    @Override
    public List<Map> getAllByProjectIdAndDateAndCarType(Long projectId, Date reportDate) {
        return projectCarFillLogRepository.getAllByProjectIdAndDateAndCarType(projectId, reportDate);
    }

    @Override
    public List<Map> getFillLogReport(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogRepository.getFillLogReport(projectId, startTime, endTime, carType);
    }

    @Override
    public List<Map> getFillLogReportMonth(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogRepository.getFillLogReportMonth(projectId, startTime, endTime, carType);
    }

    @Override
    public List<Map> getFillLogReportHistory(Long projectId, Date endTime, Integer carType) {
        return projectCarFillLogRepository.getFillLogReportHistory(projectId, endTime, carType);
    }

    @Override
    public List<Map> getFillLogOnCar(Long projectId, Date date, Integer carType) {
        return projectCarFillLogRepository.getFillLogOnCar(projectId, date, carType);
    }

    @Override
    public List<Map> getFillLogOnCarMonth(Long projectId, Date startTime, Date endTime, Integer carType) {
        return projectCarFillLogRepository.getFillLogOnCarMonth(projectId, startTime, endTime, carType);
    }
}
