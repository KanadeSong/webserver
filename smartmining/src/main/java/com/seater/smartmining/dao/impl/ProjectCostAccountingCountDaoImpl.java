package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ProjectCostAccountingCountDaoI;
import com.seater.smartmining.entity.ProjectCostAccountingCount;
import com.seater.smartmining.entity.repository.ProjectCostAccountingCountRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
 * @Date 2019/2/22 0022 11:23
 */
@Component
public class ProjectCostAccountingCountDaoImpl implements ProjectCostAccountingCountDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectCostAccountingCountRepository projectCostAccountingCountRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:projectcostaccountingcount:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ProjectCostAccountingCount get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null){
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj,ProjectCostAccountingCount.class);
        }
        if(projectCostAccountingCountRepository.existsById(id)){
            ProjectCostAccountingCount log = projectCostAccountingCountRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ProjectCostAccountingCount save(ProjectCostAccountingCount log) throws JsonProcessingException {
        ProjectCostAccountingCount log1 = projectCostAccountingCountRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public List<ProjectCostAccountingCount> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectCostAccountingCountRepository.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectCostAccountingCountRepository.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
