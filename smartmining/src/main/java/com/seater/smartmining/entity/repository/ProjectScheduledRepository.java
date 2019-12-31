package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.ProjectScheduled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Deprecated
public interface ProjectScheduledRepository extends JpaRepository<ProjectScheduled, Long>, JpaSpecificationExecutor<ProjectScheduled> {
    List<ProjectScheduled> getByProjectIdOrderById(Long projectId);
    List<ProjectScheduled> getByProjectIdAndCarIdOrderById(Long projectId, Long carId);
    List<ProjectScheduled> getByProjectIdAndDiggingMachineIdOrderById(Long projectId, Long diggingMachineId);
    ProjectScheduled getByProjectIdAndDiggingMachineIdAndCarIdOrderById(Long projectId, Long diggingMachineId, Long carId);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id,digging_machine_code from project_scheduled " +
            " where project_id = ?1 limit ?2, ?3")
    List<Map> getByProjectIdPage(Long projectId, int cur, int page);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id, digging_machine_code from project_scheduled" +
            " where project_id = ?1")
    List<Map> getByProjectIdCount(Long projectId);

    List<ProjectScheduled> getAllByProjectIdAndDiggingMachineId(Long projectId, Long machineId);

    List<ProjectScheduled> getAllByProjectIdAndCarId(Long projectId, Long carId);

    List<ProjectScheduled> getAllByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_scheduled where digging_machine_id = ?1 and project_id = ?2")
    void deleteByDiggingMachineIdAndProjectId(Long machineId, Long projectId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into project_scheduled(car_brand_id,car_brand_name,car_code,car_id,car_model_id,car_model_name,car_owner_id,car_owner_name,digging_machine_brand_id,digging_machine_brand_name, " +
            " digging_machine_code,digging_machine_id,digging_machine_model_id,digging_machine_model_name,digging_machine_owner_id,digging_machine_owner_name, " +
            " distance,materia_name,material_id,pricing_type,project_id,work_status,group_code,manager_id,manager_name,employee_id,employee_name) " +
            " value(#{#projectScheduled.carBrandId}, #{#projectScheduled.carBrandName}, #{#projectScheduled.carCode}, #{#projectScheduled.carId}, #{#projectScheduled.carModelId}, #{#projectScheduled.carModelName}, #{#projectScheduled.carOwnerId}, #{#projectScheduled.carOwnerName}, #{#projectScheduled.diggingMachineBrandId}, #{#projectScheduled.diggingMachineBrandName}," +
            " #{#projectScheduled.diggingMachineCode}, #{#projectScheduled.diggingMachineId}, #{#projectScheduled.diggingMachineModelId}, #{#projectScheduled.diggingMachineModelName}, #{#projectScheduled.diggingMachineOwnerId}, #{#projectScheduled.diggingMachineOwnerName}," +
            " #{#projectScheduled.distance}, #{#projectScheduled.materiaName}, #{#projectScheduled.materialId}, #{#projectScheduled.pricingType}, #{#projectScheduled.projectId}, #{#projectScheduled.workStatus}, #{#projectScheduled.groupCode}, #{#projectScheduled.managerId}, #{#projectScheduled.managerName}, #{#projectScheduled.employeeId}, #{#projectScheduled.employeeName}) on duplicate key update car_brand_id = #{#projectScheduled.carBrandId}," +
            " car_brand_name = #{#projectScheduled.carBrandName},car_code = #{#projectScheduled.carCode},car_id = #{#projectScheduled.carId},car_model_id = #{#projectScheduled.carModelId},car_model_name = #{#projectScheduled.carModelName},car_owner_id = #{#projectScheduled.carOwnerId},car_owner_name = #{#projectScheduled.carOwnerName},digging_machine_brand_id = #{#projectScheduled.diggingMachineBrandId}," +
            " digging_machine_brand_name = #{#projectScheduled.diggingMachineBrandName},digging_machine_code = #{#projectScheduled.diggingMachineCode},digging_machine_id = #{#projectScheduled.diggingMachineId},digging_machine_model_id = #{#projectScheduled.diggingMachineModelId}," +
            " digging_machine_model_name = #{#projectScheduled.diggingMachineModelName},digging_machine_owner_id = #{#projectScheduled.diggingMachineOwnerId},digging_machine_owner_name = #{#projectScheduled.diggingMachineOwnerName}," +
            " distance = #{projectScheduled.distance},materia_name = #{#projectScheduled.materiaName},material_id = #{#projectScheduled.materialId},pricing_type = #{#projectScheduled.pricingType},project_id = #{#projectScheduled.projectId},work_status = #{#projectScheduled.workStatus},group_code = #{#projectScheduled.groupCode}," +
            " manager_id = #{#projectScheduled.managerId},manager_name = #{#projectScheduled.managerName},employee_id = #{#projectScheduled.employeeId},employee_name = #{#projectScheduled.employeeName}")
    void saveOrModify(ProjectScheduled projectScheduled);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_scheduled where car_id = ?1 and project_id = ?2")
    void deleteByCarIdAndProjectId(Long carId, Long projectId);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id,digging_machine_code from project_scheduled" +
            " where project_id = ?1 and car_id = ?2")
    List<Map> getByProjectIdAndCarIdOnDigging(Long projectId, Long carId);

    @Query(nativeQuery = true, value = "select distinct group_code from project_scheduled" +
            " where project_id = ?1 limit ?2,?3")
    List<Map> getByProjectIdOnGroupId(Long projectId, int current, int page);

    @Query(nativeQuery = true, value = "select distinct group_code from project_scheduled" +
            " where project_id = ?1 and manager_id like regexp ?2 limit ?3,?4")
    List<Map> getByProjectIdAndManagerIdOnGroupId(Long projectId, String managerId,int current, int page);

    @Query(nativeQuery = true, value = "select distinct group_code from project_scheduled" +
            " where project_id = ?1")
    List<Map> getByAllProjectIdOnGroupId(Long projectId);

    @Query(nativeQuery = true, value = "select distinct group_code from project_scheduled" +
            " where project_id = ?1 and pricing_type = ?2")
    List<Map> getByAllProjectIdAndPricingType(Long projectId, Integer pricingType);

    ProjectScheduled getByProjectIdAndCarId(Long projectId, Long carId);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id,digging_machine_code,distance,material_id,materia_name,pricing_type,digging_machine_brand_id,manager_id,manager_name,employee_id,employee_name from project_scheduled " +
            " where project_id = ?1 and group_code = ?2")
    List<Map> getByProjectIdAndGroupCode(Long projectId, String groupCode);

    @Query(nativeQuery = true, value = "select car_id,car_code from project_scheduled where project_id = ?1 and digging_machine_id = ?2 ")
    List<Map> getByProjectIdAndDiggingMachineId(Long projectId, Long diggingMachineId);

    List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineId(Long projectId, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_scheduled where project_id = ?1 and digging_machine_id = ?2 and manager_id regexp ?3")
    List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(Long projectId, Long machineId, String managerId);

    List<ProjectScheduled> getGroupCodeByProjectIdAndCarId(Long projectId, Long carId);

    @Query(nativeQuery = true, value = "select * from project_scheduled where project_id = ?1 and car_id = ?2 and manager_id regexp ?3")
    List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndManagerId(Long projectId, Long carId, String managerId);

    List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId);

    @Query(nativeQuery = true, value = "select * from project_scheduled where project_id = ?1 and car_id = ?2 and digging_machine_id = ?3 and manager_id regexp ?3")
    List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(Long projectId, Long carId, Long machineId, String managerId);

    @Query(nativeQuery = true, value = "select distinct digging_machine_id,digging_machine_code,distance,material_id,materia_name,pricing_type,digging_machine_brand_id,manager_id,manager_name,employee_id,employee_name from project_scheduled " +
            " where project_id = ?1 and group_code = ?2 order by digging_machine_id")
    List<Map> getByProjectIdAndGroupCodeOrderByDiggingMachineId(Long projectId, String groupCode);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_scheduled where digging_machine_code = ?1 and project_id = ?2")
    void deleteByDiggingMachineCodeAndProjectId(String machineCode, Long projectId);
}
