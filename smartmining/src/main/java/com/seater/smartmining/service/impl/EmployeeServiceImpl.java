package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.EmployeeDaoI;
import com.seater.smartmining.entity.Employee;
import com.seater.smartmining.service.EmployeeServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    EmployeeDaoI employeeDaoI;

    @Override
    public Employee get(Long id) throws IOException{
        return employeeDaoI.get(id);
    }

    @Override
    public Employee save(Employee log) throws IOException{
        return employeeDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        employeeDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        employeeDaoI.delete(ids);
    }

    @Override
    public Page<Employee> query(Pageable pageable) {
        return employeeDaoI.query(pageable);
    }

    @Override
    public Page<Employee> query() {
        return employeeDaoI.query();
    }

    @Override
    public Page<Employee> query(Specification<Employee> spec) {
        return employeeDaoI.query(spec);
    }

    @Override
    public Page<Employee> query(Specification<Employee> spec, Pageable pageable) {
        return employeeDaoI.query(spec, pageable);
    }

    @Override
    public List<Employee> getAll() {
        return employeeDaoI.getAll();
    }

    @Override
    public List<Employee> getByProjectIdOrderById(Long projectId) {
        return employeeDaoI.getByProjectIdOrderById(projectId);
    }
}
