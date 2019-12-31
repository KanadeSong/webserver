package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ProjectCarRepository extends JpaRepository<ProjectCar, Long>, JpaSpecificationExecutor<ProjectCar> {
    List<ProjectCar> getByProjectIdOrderById(Long projectId);

    List<ProjectCar> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild);
    List<ProjectCar> getById(Long id);

    @Query(nativeQuery = true, value = "select count(*) from project_car where project_id = ?1 and is_vaild = true")
    Integer getCountByProjectId(Long projectId);

    @Modifying
    @Query("update ProjectCar set icCardNumber = ?2, icCardStatus = ?3 where id = ?1")
    void setICCardByCarId(Long carId, String icCardNumber, Boolean icCardStatus);

    ProjectCar getByProjectIdAndCode(Long projectId, String code);
    ProjectCar getByProjectIdAndUid(Long projectId, String uid);
    List<ProjectCar> findByProjectIdAndUid(Long projectId, String uid);
    List<ProjectCar> findByProjectIdAndUidAndIsVaild(Long projectId, String uid, Boolean isValue);

    @Query(nativeQuery = true, value = "select count(*) as count from project_car where project_id = ?1 and is_vaild = true")
    Map getCarsCountByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "select * from project_car where project_id = ?1 and is_vaild = true and seleted = ?2")
    List<ProjectCar> getAllByProjectIdAndSeleted(Long projectId, Boolean selected);

    @Query(nativeQuery = true, value = "select code from project_car where project_id = ?1 and is_vaild = ?2")
    List<String> getAllByProjectIdAndVaild(Long projectId, Boolean valid);

    @Modifying
    @Query("update ProjectCar p set p.seleted = ?1 where p.code in ?2")
    void updateSeleted(boolean selected, List<String> carCodeList);
}
