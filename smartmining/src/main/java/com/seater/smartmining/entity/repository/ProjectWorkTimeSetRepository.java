package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectWorkTimeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProjectWorkTimeSetRepository extends JpaRepository<ProjectWorkTimeSet, Long>, JpaSpecificationExecutor<ProjectWorkTimeSet> {
}
