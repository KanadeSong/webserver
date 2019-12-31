package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.CarBrandDaoI;
import com.seater.smartmining.entity.CarBrand;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.service.CarBrandServiceI;
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
public class CarBrandServiceImpl implements CarBrandServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CarBrandDaoI carBrandDaoI;

    @Override
    public CarBrand get(Long id) throws IOException{
        return carBrandDaoI.get(id);
    }

    @Override
    public CarBrand save(CarBrand log) throws IOException{
        return carBrandDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        carBrandDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        carBrandDaoI.delete(ids);
    }

    @Override
    public Page<CarBrand> query(Pageable pageable) {
        return carBrandDaoI.query(pageable);
    }

    @Override
    public Page<CarBrand> query() {
        return carBrandDaoI.query();
    }

    @Override
    public Page<CarBrand> query(Example<CarBrand> example) {
        return carBrandDaoI.query(example);
    }

    @Override
    public Page<CarBrand> query(Example<CarBrand> example, Pageable pageable) {
        return carBrandDaoI.query(example, pageable);
    }

    @Override
    public List<CarBrand> getAll() {
        return carBrandDaoI.getAll();
    }

    @Override
    public List<CarBrand> findAllByOrderById() {
        return carBrandDaoI.findAllByOrderById();
    }

    @Override
    public List<CarBrand> queryAllByParams(Specification<CarBrand> spec) {
        return carBrandDaoI.queryAllByParams(spec);
    }
}
