package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectHourPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectHourPriceRepository extends JpaRepository<ProjectHourPrice, Long>, JpaSpecificationExecutor<ProjectHourPrice> {

    @Query(nativeQuery = true, value = "select * from project_hour_price where project_id = ?1 and is_vaild = true and brand_id = ?2 and model_id = ?3 and car_type = ?4")
    List<ProjectHourPrice> getByProjectIdAndBrandIdAndModelIdAndCarType(Long projectId, Long brandId, Long modelId, Integer carType);

    List<ProjectHourPrice> getAllByProjectId(Long projectId);
}
