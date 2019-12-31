package com.seater.smartmining.service;

import com.seater.smartmining.entity.CarBrand;
import com.seater.smartmining.entity.CarType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface CarBrandServiceI {
     CarBrand get(Long id) throws IOException;
     CarBrand save(CarBrand log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<CarBrand> query();
     Page<CarBrand> query(Example<CarBrand> example);
     Page<CarBrand> query(Pageable pageable);
     Page<CarBrand> query(Example<CarBrand> example, Pageable pageable);
     List<CarBrand> getAll();
     List<CarBrand> findAllByOrderById();
     List<CarBrand> queryAllByParams(Specification<CarBrand> spec);
}