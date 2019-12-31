package com.seater.smartmining.dao;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectHourPrice;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface ProjectHourPriceDaoI {
     ProjectHourPrice get(Long id) throws IOException;
     ProjectHourPrice save(ProjectHourPrice log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectHourPrice> query();
     Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec);
     Page<ProjectHourPrice> query(Pageable pageable);
     Page<ProjectHourPrice> query(Specification<ProjectHourPrice> spec, Pageable pageable);
     List<ProjectHourPrice> getAll();
     List<ProjectHourPrice> getByProjectIdAndBrandIdAndModelIdAndCarType(Long projectId, Long brandId, Long modelId, Integer carType);
     List<ProjectHourPrice> getAllByProjectId(Long projectId);
}