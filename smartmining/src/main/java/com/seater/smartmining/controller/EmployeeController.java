package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.smartmining.entity.Employee;
import com.seater.smartmining.service.EmployeeServiceI;
import com.seater.user.entity.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/projectUser")
public class EmployeeController {
    @Autowired
    private EmployeeServiceI employeeServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(Employee employee, HttpServletRequest request)
    {
        try {
            employee.setProjectId(Long.parseLong(request.getHeader("projectId")));
            employeeServiceI.save(employee);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("delete")
    @Transactional
    public Object delete(Long id)
    {
        try {
            employeeServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("query")
    public Object query(Integer current, Integer pageSize, String name, String workCode, String idCard, Sex sex, Boolean isAll, HttpServletRequest request)
    {
        try {
            if(isAll != null && isAll)
                return employeeServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            int cur = (current == null  || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<Employee> spec = new Specification<Employee>(){

                @Override
                public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();

                    if(name != null && !name.isEmpty()){
                        list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                    }

                    if(workCode != null && !workCode.isEmpty()){
                        list.add(cb.like(root.get("workCode").as(String.class), "%" + workCode + "%"));
                    }

                    if(idCard != null && !idCard.isEmpty()){
                        list.add(cb.like(root.get("idCard").as(String.class), "%" + idCard + "%"));
                    }

                    if(sex != null) {
                        list.add(cb.equal(root.get("sex").as(Sex.class), sex));
                    }

                    list.add(cb.equal(root.get("projectId").as(Long.class),  Long.parseLong(request.getHeader("projectId"))));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return employeeServiceI.query(spec, PageRequest.of(cur, page));
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}
