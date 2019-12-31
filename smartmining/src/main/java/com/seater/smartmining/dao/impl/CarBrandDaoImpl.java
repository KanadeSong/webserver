package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.CarBrandDaoI;
import com.seater.smartmining.entity.CarBrand;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.repository.CarBrandRepository;
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

@Component
public class CarBrandDaoImpl implements CarBrandDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CarBrandRepository carBrandRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:carbrand:";

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
    public Page<CarBrand> query(Example<CarBrand> example, Pageable pageable) {
        return carBrandRepository.findAll(example, pageable);
    }

    @Override
    public Page<CarBrand> query(Example<CarBrand> example) {
        return carBrandRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<CarBrand> query(Pageable pageable) {
        return carBrandRepository.findAll(pageable);
    }

    @Override
    public Page<CarBrand> query() {
        return carBrandRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public CarBrand get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, CarBrand.class);
        }
        if(carBrandRepository.existsById(id))
        {
            CarBrand log = carBrandRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public CarBrand save(CarBrand log) throws IOException {
        CarBrand log1 = carBrandRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        carBrandRepository.deleteById(id);
    }

    @Override
    public List<CarBrand> getAll() {
        return carBrandRepository.findAll();
    }

    @Override
    public List<CarBrand> findAllByOrderById() {
        return carBrandRepository.findAllByOrderById();
    }

    @Override
    public List<CarBrand> queryAllByParams(Specification<CarBrand> spec) {
        return carBrandRepository.findAll(spec);
    }
}