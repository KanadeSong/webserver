package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.smartmining.entity.ProjectWorkTimeSet;
import com.seater.smartmining.service.ProjectWorkTimeSetServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/projectWorkTimeSet")
public class ProjectWorkTimeSetController {
    @Autowired
    private ProjectWorkTimeSetServiceI projectWorkTimeSetServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
    }

    @RequestMapping("/query")
    public Object query(HttpServletRequest request/*, Boolean isAll*/)
    {
        try {
            /*if(isAll != null && isAll)
                return projectWorkTimeSetServiceI.getByProjectId(Long.parseLong(request.getHeaders("projectId")));*/

            Specification<ProjectWorkTimeSet> spec = new Specification<ProjectWorkTimeSet>() {
                @Override
                public Predicate toPredicate(Root<ProjectWorkTimeSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectWorkTimeSetServiceI.query(spec);
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectWorkTimeSet projectWorkTimeSet, HttpServletRequest request)
    {
        try {
            projectWorkTimeSet.setProjectId(Long.parseLong(request.getHeader("projectId")));
            projectWorkTimeSetServiceI.save(projectWorkTimeSet);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id)
    {
        try {
            projectWorkTimeSetServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

}
