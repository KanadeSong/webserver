package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCarDaoI;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.entity.repository.ProjectCarRepository;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ProjectCarDaoImpl implements ProjectCarDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCarRepository projectCarRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcar:";

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
    public Page<ProjectCar> query(Specification<ProjectCar> spec, Pageable pageable) {
        return projectCarRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectCar> query(Specification<ProjectCar> spec) {
        return projectCarRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectCar> query(Pageable pageable) {
        return projectCarRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectCar> query() {
        return projectCarRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectCar get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectCar.class);
        }
        if(projectCarRepository.existsById(id))
        {
            ProjectCar log = projectCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectCar save(ProjectCar log) throws IOException {
        ProjectCar log1 = projectCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectCarRepository.deleteById(id);
    }

    @Override
    public List<ProjectCar> getAll() {
        return projectCarRepository.findAll();
    }

    @Override
    public List<ProjectCar> getByProjectIdOrderById(Long projectId) {
        return projectCarRepository.getByProjectIdOrderById(projectId);
    }

    @Override
    public Integer getCountByProjectId(Long projectId) {
        return projectCarRepository.getCountByProjectId(projectId);
    }

    @Override
    public void setICCardByProjectIdAndCarId(Long carId, String icCardNumber, Boolean icCardStatus) {
        projectCarRepository.setICCardByCarId(carId, icCardNumber, icCardStatus);
    }

    @Override
    public ProjectCar getByProjectIdAndCode(Long projectId, String code) {
        return projectCarRepository.getByProjectIdAndCode(projectId, code);
    }

    @Override
    public Map getCarsCountByProjectId(Long projectId) {
        return projectCarRepository.getCarsCountByProjectId(projectId);
    }

    @Override
    public List<ProjectCar> queryWx(Specification<ProjectCar> spec) {
        return projectCarRepository.findAll(spec);
    }

    @Override
    public List<ProjectCar> getAllByProjectIdAndSeleted(Long projectId, Boolean selected) {
        return projectCarRepository.getAllByProjectIdAndSeleted(projectId, selected);
    }

    @Override
    public void batchSave(List<ProjectCar> projectCarList) {
        projectCarRepository.saveAll(projectCarList);
    }

    @Override
    public List<String> getAllByProjectIdAndVaild(Long projectId, Boolean valid) {
        return projectCarRepository.getAllByProjectIdAndVaild(projectId, valid);
    }

    @Override
    public List<ProjectCar> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild) {
        return projectCarRepository.getByProjectIdAndIsVaild(projectId, isVaild);
    }

    @Override
    public void updateSeleted(boolean selected, List<String> carCodeList) {
        projectCarRepository.updateSeleted(selected, carCodeList);
    }
}