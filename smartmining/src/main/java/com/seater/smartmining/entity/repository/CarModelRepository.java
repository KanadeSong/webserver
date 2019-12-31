package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarModel;
import com.seater.smartmining.entity.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long>, JpaSpecificationExecutor<CarModel> {
    List<CarModel> findAllByOrderById();
    List<CarModel> findByTypeOrderById(CarType type);
}
