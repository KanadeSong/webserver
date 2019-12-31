package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.entity.OwnerDriverRelation;
import com.seater.smartmining.entity.repository.OwnerDriverRelationRepository;
import com.seater.smartmining.service.OwnerDriverRelationServiceI;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:47
 */
@Service
public class OwnerDriverRelationServiceImpl implements OwnerDriverRelationServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    OwnerDriverRelationRepository ownerDriverRelationRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:ownerDriverRelation:";

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
    public Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example, Pageable pageable) {
        return ownerDriverRelationRepository.findAll(example, pageable);
    }

    @Override
    public Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example) {
        return ownerDriverRelationRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<OwnerDriverRelation> query(Pageable pageable) {
        return ownerDriverRelationRepository.findAll(pageable);
    }

    @Override
    public Page<OwnerDriverRelation> query() {
        return ownerDriverRelationRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public OwnerDriverRelation get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, OwnerDriverRelation.class);
        }
        if(ownerDriverRelationRepository.existsById(id))
        {
            OwnerDriverRelation log = ownerDriverRelationRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public OwnerDriverRelation save(OwnerDriverRelation log) throws IOException {
        OwnerDriverRelation log1 = ownerDriverRelationRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        ownerDriverRelationRepository.deleteById(id);
    }

    @Override
    public List<OwnerDriverRelation> getAll() {
        return ownerDriverRelationRepository.findAll();
    }

    @Override
    public OwnerDriverRelation findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverOpenId) {
        return ownerDriverRelationRepository.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId,driverOpenId);
    }

    @Override
    public List<OwnerDriverRelation> findByOwnerIdAndDriverId(Long ownerId, Long driverId) {
        return ownerDriverRelationRepository.findByOwnerIdAndDriverId(ownerId,driverId);
    }

    @Override
    public List<OwnerDriverRelation> queryWx(Specification<OwnerDriverRelation> spec) {
        return ownerDriverRelationRepository.findAll(spec);
    }

}
