package com.seater.smartmining.dao;

import com.seater.smartmining.entity.Employee;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface EmployeeDaoI {
     Employee get(Long id) throws IOException;
     Employee save(Employee log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<Employee> query();
     Page<Employee> query(Specification<Employee> spec);
     Page<Employee> query(Pageable pageable);
     Page<Employee> query(Specification<Employee> spec, Pageable pageable);
     List<Employee> getAll();
     List<Employee> getByProjectIdOrderById(Long projectId);
}