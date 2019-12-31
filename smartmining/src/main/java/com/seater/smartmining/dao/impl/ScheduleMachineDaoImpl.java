package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.ScheduleMachineDaoI;
import com.seater.smartmining.entity.ScheduleMachine;
import com.seater.smartmining.entity.repository.ScheduleMachineRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/23 0023 14:33
 */
@Component
public class ScheduleMachineDaoImpl implements ScheduleMachineDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ScheduleMachineRepository scheduleMachineRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:schedulemachine:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public ScheduleMachine get(Long id) throws IOException {
        if(id == 0L) return null;
        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, ScheduleMachine.class);
        }
        if(scheduleMachineRepository.existsById(id)){
            ScheduleMachine log = scheduleMachineRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }
        return null;
    }

    @Override
    public ScheduleMachine save(ScheduleMachine log) throws JsonProcessingException {
        ScheduleMachine log1 = scheduleMachineRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        scheduleMachineRepository.deleteById(id);
    }

    @Override
    public Page<ScheduleMachine> query() {
        return scheduleMachineRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleMachine> query(Specification<ScheduleMachine> spec) {
        return scheduleMachineRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<ScheduleMachine> query(Pageable pageable) {
        return scheduleMachineRepository.findAll(pageable);
    }

    @Override
    public Page<ScheduleMachine> query(Specification<ScheduleMachine> spec, Pageable pageable) {
        return scheduleMachineRepository.findAll(spec, pageable);
    }

    @Override
    public List<ScheduleMachine> getAll() {
        return scheduleMachineRepository.findAll();
    }

    @Override
    public List<ScheduleMachine> getAllByProjectId(Long projectId) {
        return scheduleMachineRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndMachineIdAndIsVaild(Long projectId, Long machineId, boolean flag) {
        return scheduleMachineRepository.getAllByProjectIdAndMachineIdAndIsVaild(projectId, machineId, flag);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndGroupCode(Long projectId, String groupCode) {
        return scheduleMachineRepository.getAllByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public void deleteByProjectIdAndMachineCode(Long projectId, String machineCode) {
        scheduleMachineRepository.deleteByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public void deleteByProjectIdAndGroupCode(Long projectId, String groupCode) {
        scheduleMachineRepository.deleteByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public ScheduleMachine getByProjectIdAndMachineCode(Long projectId, String machineCode) {
        return scheduleMachineRepository.getByProjectIdAndMachineCode(projectId, machineCode);
    }

    @Override
    public List<String> getGroupCodeList() {
        return scheduleMachineRepository.getGroupCodeList();
    }

    @Override
    public List<ScheduleMachine> getAllByQuery(Specification<ScheduleMachine> spec) {
        return scheduleMachineRepository.findAll(spec);
    }

    @Override
    public void deleteByGroupCode(String groupCode) {
        scheduleMachineRepository.deleteByGroupCode(groupCode);
    }

    @Override
    public void batchSave(List<ScheduleMachine> scheduleMachineList) {
        scheduleMachineRepository.saveAll(scheduleMachineList);
    }

    @Override
    public List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isValid) {
        return scheduleMachineRepository.getAllByProjectIdAndIsVaild(projectId, isValid);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        scheduleMachineRepository.deleteByProjectId(projectId);
    }

    @Override
    public List<ScheduleMachine> getAllByProjectIdAndIsVaildAndInSchedule(Long projectId) {
        return scheduleMachineRepository.getAllByProjectIdAndIsVaildAndInSchedule(projectId);
    }

    @Override
    public void deleteByProjectIdAndMachineCodeList(Long projectId, List<String> machineList) {
        scheduleMachineRepository.deleteByProjectIdAndMachineCodeList(projectId, machineList);
    }
}
