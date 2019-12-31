package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ScheduleMachineModelDaoI;
import com.seater.smartmining.entity.ScheduleMachineModel;
import com.seater.smartmining.entity.repository.ScheduleMachineModelRepository;
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
 * @Date 2019/11/15 0015 11:06
 */
@Component
public class ScheduleMachineModelDaoImpl implements ScheduleMachineModelDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ScheduleMachineModelRepository scheduleMachineModelRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:schedulemachinemodel:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ScheduleMachineModel get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ScheduleMachineModel.class);
        }
        if(scheduleMachineModelRepository.existsById(id)){
            ScheduleMachineModel log = scheduleMachineModelRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ScheduleMachineModel save(ScheduleMachineModel log) throws JsonProcessingException {
        ScheduleMachineModel log1 = scheduleMachineModelRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        scheduleMachineModelRepository.deleteById(id);
    }

    @Override
    public Page<ScheduleMachineModel> query() {
        return scheduleMachineModelRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec) {
        return scheduleMachineModelRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleMachineModel> query(Pageable pageable) {
        return scheduleMachineModelRepository.findAll(pageable);
    }

    @Override
    public Page<ScheduleMachineModel> query(Specification<ScheduleMachineModel> spec, Pageable pageable) {
        return scheduleMachineModelRepository.findAll(spec, pageable);
    }

    @Override
    public List<ScheduleMachineModel> getAll() {
        return scheduleMachineModelRepository.findAll();
    }

    @Override
    public List<ScheduleMachineModel> getAllByProjectId(Long projectId) {
        return scheduleMachineModelRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachineModel> getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(Long projectId, String groupCode, boolean valid) {
        return scheduleMachineModelRepository.getAllByProjectIdAndGroupCodeAndIsVaildOrderByMachineCode(projectId, groupCode, valid);
    }

    @Override
    public void deleteByProjectIdAndMachineCode(Long projectId, String machineCode) {
        scheduleMachineModelRepository.deleteByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public void deleteByProjectIdAndMachineCodeListAndProgrammeId(Long projectId, List<String> machineList, Long programmeId) {
        scheduleMachineModelRepository.deleteByProjectIdAndMachineCodeListAndProgrammeId(projectId, machineList, programmeId);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleMachineModelRepository.getGroupCodeList();
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleMachineModelRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleMachineModel> saveList) {
        scheduleMachineModelRepository.saveAll(saveList);
    }

    @Override
    public void deleteByGroupCodes(List<String> groupCodes) {
        scheduleMachineModelRepository.deleteByGroupCodes(groupCodes);
    }

    @Override
    public List<ScheduleMachineModel> queryByParams(Specification<ScheduleMachineModel> spec) {
        return scheduleMachineModelRepository.findAll(spec);
    }
}
