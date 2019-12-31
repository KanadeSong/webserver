package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectWorkTimePoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.sql.Time;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.earlyStartTime = ?2, p.earlyEndPoint = ?3, p.earlyEndTime = ?4, " +
            "p.nightStartPoint = ?5, p.nightStartTime = ?6, p.nightEndPoint = ?7, p.nightEndTime = ?8 WHERE p.id = ?1")
    void setWorkTime(Long id, Time earlyStart, ProjectWorkTimePoint earlyEndPoint, Time earlyEnd, ProjectWorkTimePoint nightStartPoint, Time nightStart, ProjectWorkTimePoint nightEndPoint, Time nightEnd);

    @Query(nativeQuery = true, countQuery = "SELECT\n" +
            "	count(p.id)\n" +
            "FROM\n" +
            "	project p\n" +
            "	LEFT JOIN sys_user_project_role upr ON upr.project_id = p.id\n" +
            "	LEFT JOIN sys_user u ON u.id = upr.user_id \n" +
            "WHERE\n" +
            "	u.id = ?1 " +
            "AND upr.valid = 1 " +
            "GROUP BY\n" +
            "	p.id",
            value = "SELECT\n" +
            "	p.* \n" +
            "FROM\n" +
            "	project p\n" +
            "	LEFT JOIN sys_user_project_role upr ON upr.project_id = p.id\n" +
            "	LEFT JOIN sys_user u ON u.id = upr.user_id \n" +
            "WHERE\n" +
            "	u.id = ?1 " +
            "AND upr.valid = 1 " +
            "GROUP BY\n" +
            "	p.id")
    public Page<Project> findByUserId(Long userId, Pageable pageable);
}
