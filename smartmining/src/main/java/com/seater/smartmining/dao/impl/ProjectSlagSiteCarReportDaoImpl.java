package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectSlagSiteCarReportDaoI;
import com.seater.smartmining.entity.ProjectSlagSiteCarReport;
import com.seater.smartmining.entity.repository.ProjectSlagSiteCarReportRepository;
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
 * @Date 2019/7/29 0029 15:13
 */
@Component
public class ProjectSlagSiteCarReportDaoImpl implements ProjectSlagSiteCarReportDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectSlagSiteCarReportRepository projectSlagSiteCarReportRepository;
    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectslagsitecarreport:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectSlagSiteCarReport get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectSlagSiteCarReport.class);
        }
        if(projectSlagSiteCarReportRepository.existsById(id)){
            ProjectSlagSiteCarReport log = projectSlagSiteCarReportRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectSlagSiteCarReport save(ProjectSlagSiteCarReport log) throws IOException {
        ProjectSlagSiteCarReport log1 = projectSlagSiteCarReportRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectSlagSiteCarReportRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids){
            delete(id);
        }
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query() {
        return projectSlagSiteCarReportRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec) {
        return projectSlagSiteCarReportRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Pageable pageable) {
        return projectSlagSiteCarReportRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectSlagSiteCarReport> query(Specification<ProjectSlagSiteCarReport> spec, Pageable pageable) {
        return projectSlagSiteCarReportRepository.findAll(spec, pageable);
    }

    @Override
    public List<ProjectSlagSiteCarReport> getAll() {
        return projectSlagSiteCarReportRepository.findAll();
    }

    @Override
    public void batchSave(List<ProjectSlagSiteCarReport> reportList) {
        projectSlagSiteCarReportRepository.saveAll(reportList);
    }

    @Override
    public List<ProjectSlagSiteCarReport> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectSlagSiteCarReportRepository.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectSlagSiteCarReportRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<ProjectSlagSiteCarReport> queryAll(Specification<ProjectSlagSiteCarReport> spec) {
        return projectSlagSiteCarReportRepository.findAll(spec);
    }
}
