package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.CarModelDaoI;
import com.seater.smartmining.entity.CarModel;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.repository.CarModelRepository;
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
public class CarModelDaoImpl implements CarModelDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CarModelRepository carModelRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:carmodel:";

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
    public Page<CarModel> query(Example<CarModel> example, Pageable pageable) {
        return carModelRepository.findAll(example, pageable);
    }

    @Override
    public Page<CarModel> query(Example<CarModel> example) {
        return carModelRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<CarModel> query(Pageable pageable) {
        return carModelRepository.findAll(pageable);
    }

    @Override
    public Page<CarModel> query() {
        return carModelRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public CarModel get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, CarModel.class);
        }
        if(carModelRepository.existsById(id))
        {
            CarModel log = carModelRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public CarModel save(CarModel log) throws IOException {
        CarModel log1 = carModelRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        carModelRepository.deleteById(id);
    }

    @Override
    public List<CarModel> getAll() {
        return carModelRepository.findAll();
    }

    @Override
    public List<CarModel> findAllByOrderById() {
        return carModelRepository.findAllByOrderById();
    }

    @Override
    public List<CarModel> findByTypeOrderById(CarType type) {
            return carModelRepository.findByTypeOrderById(type);
    }

    @Override
    public List<CarModel> queryByParams(Specification<CarModel> spec) {
        return carModelRepository.findAll(spec);
    }
}