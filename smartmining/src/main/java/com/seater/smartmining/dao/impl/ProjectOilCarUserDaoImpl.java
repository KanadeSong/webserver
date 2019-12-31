package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectOilCarUserDaoI;
import com.seater.smartmining.entity.ProjectOilCarUser;
import com.seater.smartmining.entity.repository.ProjectOilCarUserRepository;
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

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/2 18:31
 */
@Component
public class ProjectOilCarUserDaoImpl implements ProjectOilCarUserDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectOilCarUserRepository projectOilCarUserRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectOilCarUser:";

    String getKey(Long id) {
        return keyGroup + id.toString();
    }

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }


    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public void deleteByOilCarId(Long oilCarId) {
        projectOilCarUserRepository.deleteAllByOilCarId(oilCarId);
    }

    @Override
    public Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec, Pageable pageable) {
        return projectOilCarUserRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectOilCarUser> query(Specification<ProjectOilCarUser> spec) {
        return projectOilCarUserRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectOilCarUser> query(Pageable pageable) {
        return projectOilCarUserRepository.findAll(pageable);
    }

    @Override
    public Page<ProjectOilCarUser> query() {
        return projectOilCarUserRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public ProjectOilCarUser get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ProjectOilCarUser.class);
        }
        if (projectOilCarUserRepository.existsById(id)) {
            ProjectOilCarUser log = projectOilCarUserRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public ProjectOilCarUser save(ProjectOilCarUser log) throws IOException {
        ProjectOilCarUser log1 = projectOilCarUserRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectOilCarUserRepository.deleteById(id);
    }

    @Override
    public List<ProjectOilCarUser> getAll() {
        return projectOilCarUserRepository.findAll();
    }

    @Override
    public List<ProjectOilCarUser> queryWx(Specification<ProjectOilCarUser> spec) {
        return projectOilCarUserRepository.findAll(spec);
    }
}
