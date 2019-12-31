package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.DiggingMachineDaoI;
import com.seater.smartmining.entity.DiggingMachine;
import com.seater.smartmining.entity.repository.DiggingMachineRepository;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
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
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:19
 */
@Component
public class DiggingMachineDaoImpl implements DiggingMachineDaoI {
    
    @Autowired
    DiggingMachineRepository diggingMachineRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:diggingMachine:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public DiggingMachine get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, DiggingMachine.class);
        }
        if(diggingMachineRepository.existsById(id))
        {
            DiggingMachine log = diggingMachineRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        diggingMachineRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public DiggingMachine save(DiggingMachine log) throws IOException {
        DiggingMachine log1 = diggingMachineRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public Page<DiggingMachine> query(Specification<DiggingMachine> spec, Pageable pageable) {
        return diggingMachineRepository.findAll(spec,pageable);
    }

    @Override
    public List<DiggingMachine> queryWx(Specification<DiggingMachine> spec) {
        return diggingMachineRepository.findAll(spec);
    }

    @Override
    public Page<DiggingMachine> findByOwnerId(Long userId, Pageable pageable) {
        return diggingMachineRepository.findByOwnerId(userId,pageable);
    }

    @Override
    public List<Map[]> findByOwnerId(Long userId) {
        return diggingMachineRepository.findByOwnerId(userId);
    }

    @Override
    public List<DiggingMachine> findByOwnerIdAndDriverId(Long ownerId, Long driverId) {
        return diggingMachineRepository.findByOwnerIdAndDriverId(ownerId,driverId);
    }

    @Override
    public DiggingMachine findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId) {
        return diggingMachineRepository.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId,driverId);
    }
}
