package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.SlagCarDaoI;
import com.seater.smartmining.entity.SlagCar;
import com.seater.smartmining.entity.repository.ProjectRepository;
import com.seater.smartmining.entity.repository.SlagCarRepository;
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
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:19
 */
@Component
public class SlagCarDaoImpl implements SlagCarDaoI {
    
    @Autowired
    SlagCarRepository slagCarRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:slagCar:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}

    @Override
    public SlagCar get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SlagCar.class);
        }
        if(slagCarRepository.existsById(id))
        {
            SlagCar log = slagCarRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        slagCarRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public SlagCar save(SlagCar log) throws IOException {
        SlagCar log1 = slagCarRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public Page<SlagCar> query(Specification<SlagCar> spec, Pageable pageable) {
        return slagCarRepository.findAll(spec,pageable);
    }

    @Override
    public List<SlagCar> getAllByProjectId(Long projectId) {
        return slagCarRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<SlagCar> queryWx(Specification<SlagCar> spec) {
        return slagCarRepository.findAll(spec);
    }

    @Override
    public Page<SlagCar> findByOwnerId(Long userId, Pageable pageable) {
        return slagCarRepository.findByOwnerId(userId,pageable);
    }

    @Override
    public SlagCar findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId) {
        return slagCarRepository.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId,driverId);
    }
    
    @Override
    public List<SlagCar> findByOwnerIdAndDriverId(Long ownerId, Long driverId) {
        return slagCarRepository.findByOwnerIdAndDriverId(ownerId,driverId);
    }

    @Override
    public SlagCar getAllByProjectIdAndCodeInProject(Long projectId, String slagCarCode) {
        return slagCarRepository.getAllByProjectIdAndCodeInProject(projectId, slagCarCode);
    }
}
