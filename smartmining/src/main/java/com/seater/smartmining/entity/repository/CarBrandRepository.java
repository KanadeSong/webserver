package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarBrand;
import com.seater.smartmining.entity.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CarBrandRepository extends JpaRepository<CarBrand, Long>, JpaSpecificationExecutor<CarBrand> {
    List<CarBrand> findAllByOrderById();
}
