package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.NozzleDaoI;
import com.seater.smartmining.entity.Nozzle;
import com.seater.smartmining.entity.repository.NozzleRepository;
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
 * @Date 2019/4/23 13:39
 */
@Component
public class NozzleDaoImpl implements NozzleDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    NozzleRepository nozzleRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:Nozzle:";

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
    public Page<Nozzle> query(Specification<Nozzle> spec, Pageable pageable) {
        return nozzleRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Nozzle> query(Specification<Nozzle> spec) {
        return nozzleRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<Nozzle> query(Pageable pageable) {
        return nozzleRepository.findAll(pageable);
    }

    @Override
    public Page<Nozzle> query() {
        return nozzleRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Nozzle get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, Nozzle.class);
        }
        if(nozzleRepository.existsById(id))
        {
            Nozzle log = nozzleRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public Nozzle save(Nozzle log) throws IOException {
        Nozzle log1 = nozzleRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        nozzleRepository.deleteById(id);
    }

    @Override
    public List<Nozzle> getAll() {
        return nozzleRepository.findAll();
    }

    @Override
    public List<Nozzle> queryWx(Specification<Nozzle> spec) {
        return nozzleRepository.findAll(spec);
    }
}
