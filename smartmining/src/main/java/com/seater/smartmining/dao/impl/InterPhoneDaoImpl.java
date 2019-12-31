package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.InterPhoneDaoI;
import com.seater.smartmining.entity.InterPhone;
import com.seater.smartmining.entity.repository.InterPhoneRepository;
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
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/17 15:42
 */
@Component
public class InterPhoneDaoImpl implements InterPhoneDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    InterPhoneRepository interPhoneRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhone:";

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
    public Page<InterPhone> query(Specification<InterPhone> spec, Pageable pageable) {
        return interPhoneRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InterPhone> query(Specification<InterPhone> spec) {
        return interPhoneRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<InterPhone> query(Pageable pageable) {
        return interPhoneRepository.findAll(pageable);
    }

    @Override
    public Page<InterPhone> query() {
        return interPhoneRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public InterPhone get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, InterPhone.class);
        }
        if(interPhoneRepository.existsById(id))
        {
            InterPhone log = interPhoneRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public InterPhone save(InterPhone log) throws IOException {
        InterPhone log1 = interPhoneRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        interPhoneRepository.deleteById(id);
    }

    @Override
    public List<InterPhone> getAll() {
        return interPhoneRepository.findAll();
    }

    @Override
    public List<InterPhone> queryWx(Specification<InterPhone> spec) {
        return interPhoneRepository.findAll(spec);
    }
}
