package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectBlastWorkInfoDaoI;
import com.seater.smartmining.entity.ProjectBlastWorkInfo;
import com.seater.smartmining.entity.repository.ProjectBlastWorkInfoRepository;
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
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 12:55
 */
@Component
public class ProjectBlastWorkInfoDaoImpl implements ProjectBlastWorkInfoDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectBlastWorkInfoRepository projectBlastWorkInfoRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectblastworkinfo:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectBlastWorkInfo get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectBlastWorkInfo.class);
        }
        if(projectBlastWorkInfoRepository.existsById(id)){
            ProjectBlastWorkInfo log = projectBlastWorkInfoRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectBlastWorkInfo save(ProjectBlastWorkInfo log) throws JsonProcessingException {
        ProjectBlastWorkInfo log1 = projectBlastWorkInfoRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        projectBlastWorkInfoRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<ProjectBlastWorkInfo> query() {
        return projectBlastWorkInfoRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Specification<ProjectBlastWorkInfo> spec) {
        return projectBlastWorkInfoRepository.findAll(spec,PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Pageable pageable, Specification<ProjectBlastWorkInfo> spec) {
        return projectBlastWorkInfoRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ProjectBlastWorkInfo> query(Pageable pageable) {
        return projectBlastWorkInfoRepository.findAll(pageable);
    }

    @Override
    public List<ProjectBlastWorkInfo> getAll() {
        return projectBlastWorkInfoRepository.findAll();
    }
}
