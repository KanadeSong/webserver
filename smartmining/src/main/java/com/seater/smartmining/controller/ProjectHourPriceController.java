package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectHourPrice;
import com.seater.smartmining.service.ProjectHourPriceServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/projectHourPrice")
public class ProjectHourPriceController {
    @Autowired
    private ProjectHourPriceServiceI projectHourPriceServiceI;

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectHourPrice projectHourPrice, HttpServletRequest request)
    {
        try {
            projectHourPrice.setProjectId(Long.parseLong(request.getHeader("projectId")));
            projectHourPriceServiceI.save(projectHourPrice);
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
            projectHourPriceServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Integer current, Integer pageSize)
    {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        try {
            Specification<ProjectHourPrice> spec = new Specification<ProjectHourPrice>() {
                @Override
                public Predicate toPredicate(Root<ProjectHourPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectHourPriceServiceI.query(spec, PageRequest.of(cur, page));
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}
