package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectCarMaterial;
import com.seater.smartmining.service.ProjectCarMaterialServiceI;
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
@RequestMapping("api/projectCarMaterialPrice")
public class ProjectCarMaterialController {
    @Autowired
    private ProjectCarMaterialServiceI projectCarMaterialServiceI;

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Boolean isAll)
    {
        try {
            if(isAll != null && isAll)
                return projectCarMaterialServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            Specification<ProjectCarMaterial> spec = new Specification<ProjectCarMaterial>() {
                @Override
                public Predicate toPredicate(Root<ProjectCarMaterial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectCarMaterialServiceI.query(spec);
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectCarMaterial projectCarMaterial, HttpServletRequest request)
    {
        try {
            projectCarMaterial.setProjectId(Long.parseLong(request.getHeader("projectId")));
            projectCarMaterialServiceI.save(projectCarMaterial);
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
            projectCarMaterialServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}
