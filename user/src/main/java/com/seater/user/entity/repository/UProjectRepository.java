package com.seater.user.entity.repository;

import com.seater.user.entity.UProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UProjectRepository extends JpaRepository<UProject, Long>, JpaSpecificationExecutor<UProject> {
    @Query(nativeQuery = true,value = "SELECT\n" +
            "	p.* \n" +
            "FROM\n" +
            "	project p\n" +
            "	LEFT JOIN sys_user_project up ON up.project_id = p.id\n" +
            "	LEFT JOIN sys_user u ON u.id = up.user_id \n" +
            "WHERE\n" +
            "	u.id = ?1 \n" +
            "	AND u.valid = 1")
    public List<UProject> findProjectsByUserId(Long userId);
    
    public List<UProject> findAllByIdIsIn(List<Long> projectIdList);
    
    public UProject getById(Long id);
}
