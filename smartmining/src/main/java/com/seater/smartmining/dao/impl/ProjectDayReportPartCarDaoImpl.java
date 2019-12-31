package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectDayReportPartCarDaoI;
import com.seater.smartmining.entity.ProjectDayReportPartCar;
import com.seater.smartmining.entity.repository.ProjectDayReportPartCarRepository;
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
public class ProjectDayReportPartCarDaoImpl implements ProjectDayReportPartCarDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDayReportPartCarRepository projectDayReportPartCarRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectdayreportpartcar:";

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
    public Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec, Pageable pageable) {
        return projectDayReportPartCarRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec) {
        return projectDayReportPartCarRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectDayReportPartCar> query(Pageable pageable) {
        return projectDayReportPartCarRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectDayReportPartCar> query() {
        return projectDayReportPartCarRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectDayReportPartCar get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectDayReportPartCar.class);
        }
        if(projectDayReportPartCarRepository.existsById(id))
        {
            ProjectDayReportPartCar log = projectDayReportPartCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectDayReportPartCar save(ProjectDayReportPartCar log) throws IOException {
        ProjectDayReportPartCar log1 = projectDayReportPartCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectDayReportPartCarRepository.deleteById(id);
    }

    @Override
    public List<ProjectDayReportPartCar> getAll() {
        return projectDayReportPartCarRepository.findAll();
    }

    @Override
    public List<ProjectDayReportPartCar> getByReportIdOrderByCarCode(Long reportId) {
        return projectDayReportPartCarRepository.getByReportIdOrderByCarCode(reportId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportPartCarRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public ProjectDayReportPartCar getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarRepository.getByProjectIdAndReportDate(projectId,reportDate);
    }

    @Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, String startTime, String endTime) {
        return projectDayReportPartCarRepository.getMonthReportByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    /*@Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportPartCarRepository.getMonthReportByProjectIdAndReportDate(projectId, startTime, endTime);
    }*/

    @Override
    public Map getTotalInfoByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarRepository.getTotalInfoByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getGrandInfoByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarRepository.getGrandInfoByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getHistoryInfoByProjectId(Long projectId) {
        return projectDayReportPartCarRepository.getHistoryInfoByProjectId(projectId);
    }

    @Override
    public Map getSettlementDetailByProjectIdAndReportIdAndCarId(Long projectId, Long reportId, Long carId) {
        return projectDayReportPartCarRepository.getSettlementDetailByProjectIdAndReportIdAndCarId(projectId, reportId, carId);
    }

    @Override
    public List<Map> getMonthCarCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportPartCarRepository.getMonthCarCountByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReportPartCar> queryWx(Specification<ProjectDayReportPartCar> spec) {
        return projectDayReportPartCarRepository.findAll(spec);
    }
}
