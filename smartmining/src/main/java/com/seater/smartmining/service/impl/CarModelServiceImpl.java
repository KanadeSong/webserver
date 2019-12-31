package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.CarModelDaoI;
import com.seater.smartmining.entity.CarModel;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.service.CarModelServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CarModelServiceImpl implements CarModelServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CarModelDaoI carModelDaoI;

    @Override
    public CarModel get(Long id) throws IOException{
        return carModelDaoI.get(id);
    }

    @Override
    public CarModel save(CarModel log) throws IOException{
        return carModelDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        carModelDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        carModelDaoI.delete(ids);
    }

    @Override
    public Page<CarModel> query(Pageable pageable) {
        return carModelDaoI.query(pageable);
    }

    @Override
    public Page<CarModel> query() {
        return carModelDaoI.query();
    }

    @Override
    public Page<CarModel> query(Example<CarModel> example) {
        return carModelDaoI.query(example);
    }

    @Override
    public Page<CarModel> query(Example<CarModel> example, Pageable pageable) {
        return carModelDaoI.query(example, pageable);
    }

    @Override
    public List<CarModel> getAll() {
        return carModelDaoI.getAll();
    }

    @Override
    public List<CarModel> findAllByOrderById() {
        return carModelDaoI.findAllByOrderById();
    }

    @Override
    public List<CarModel> findByTypeOrderById(CarType type) {
        return carModelDaoI.findByTypeOrderById(type);
    }

    @Override
    public List<CarModel> queryByParams(Specification<CarModel> spec) {
        return carModelDaoI.queryByParams(spec);
    }
}
