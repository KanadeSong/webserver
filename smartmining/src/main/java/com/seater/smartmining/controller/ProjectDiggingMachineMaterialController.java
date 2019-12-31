package com.seater.smartmining.controller;


import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import com.seater.smartmining.service.ProjectDiggingMachineMaterialServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;

@RestController
@RequestMapping("api/projectDiggingMachineMaterial")
public class ProjectDiggingMachineMaterialController {
    @Autowired
    private ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Boolean isAll)
    {
        try {
            if(isAll != null && isAll)
                return projectDiggingMachineMaterialServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            Specification<ProjectDiggingMachineMaterial> spec = new Specification<ProjectDiggingMachineMaterial>() {
                @Override
                public Predicate toPredicate(Root<ProjectDiggingMachineMaterial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectDiggingMachineMaterialServiceI.query(spec);
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectDiggingMachineMaterial projectDiggingMachineMaterial, HttpServletRequest request)
    {
        try {
            projectDiggingMachineMaterial.setProjectId(Long.parseLong(request.getHeader("projectId")));
            projectDiggingMachineMaterialServiceI.save(projectDiggingMachineMaterial);
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
            projectDiggingMachineMaterialServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}
