package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    public List<Employee> getByProjectIdOrderById(Long projectId);
}
