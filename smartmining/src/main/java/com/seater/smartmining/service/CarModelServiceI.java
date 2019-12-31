package com.seater.smartmining.service;

import com.seater.smartmining.entity.CarModel;
import com.seater.smartmining.entity.CarType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface CarModelServiceI {
     CarModel get(Long id) throws IOException;
     CarModel save(CarModel log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<CarModel> query();
     Page<CarModel> query(Example<CarModel> example);
     Page<CarModel> query(Pageable pageable);
     Page<CarModel> query(Example<CarModel> example, Pageable pageable);
     List<CarModel> getAll();
     List<CarModel> findAllByOrderById();
     List<CarModel> findByTypeOrderById(CarType type);
     List<CarModel> queryByParams(Specification<CarModel> spec);
}