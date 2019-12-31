package com.seater.smartmining.dao;

import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectCarMaterial;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProjectCarDaoI {
     ProjectCar get(Long id) throws IOException;
     ProjectCar save(ProjectCar log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectCar> query();
     Page<ProjectCar> query(Specification<ProjectCar> spec);
     Page<ProjectCar> query(Pageable pageable);
     Page<ProjectCar> query(Specification<ProjectCar> spec, Pageable pageable);
     List<ProjectCar> getAll();
     List<ProjectCar> getByProjectIdOrderById(Long projectId);
     Integer getCountByProjectId(Long projectId);
     void setICCardByProjectIdAndCarId(Long carId, String icCardNumber, Boolean icCardStatus);
     ProjectCar getByProjectIdAndCode(Long projectId, String code);
     Map getCarsCountByProjectId(Long projectId);
     List<ProjectCar> queryWx(Specification<ProjectCar> spec);
     List<ProjectCar> getAllByProjectIdAndSeleted(Long projectId, Boolean selected);
     void batchSave(List<ProjectCar> projectCarList);
     List<String> getAllByProjectIdAndVaild(Long projectId, Boolean valid);
     List<ProjectCar> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild);
     void updateSeleted(boolean selected, List<String> carCodeList);
}
