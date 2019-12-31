package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectSlagSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectSlagSiteRepository extends JpaRepository<ProjectSlagSite, Long>, JpaSpecificationExecutor<ProjectSlagSite> {
    ProjectSlagSite getByProjectIdAndDeviceUid(Long projectId, String deviceUid);

    ProjectSlagSite getByProjectIdAndDistance(Long projectId, Long distance);

    @Query(nativeQuery = true, value = "select * from project_slag_site where project_id = ?1 and manager_id like %?2%")
    List<ProjectSlagSite> getAllByProjectIdAndManagerId(Long projectId, String managerId);

    List<ProjectSlagSite> getAllByProjectId(Long projectId);

    //专门查询临时渣场 这里必须用 =号 不能用模糊查询
    @Query(nativeQuery = true, value = "select * from project_slag_site where project_id = ?1 and name = ?2")
    List<ProjectSlagSite> getAllByProjectIdAndName(Long projectId, String name);

    @Query(nativeQuery = true, value = "select * from project_slag_site where project_id = ?1 and slag_site_code = ?2")
    ProjectSlagSite getAllByProjectIdAndSlagSiteCode(Long projectId, String slagSiteCode);
}