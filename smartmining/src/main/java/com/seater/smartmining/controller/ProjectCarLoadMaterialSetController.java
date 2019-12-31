package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import com.seater.smartmining.service.ProjectCarLoadMaterialSetServiceI;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/projectCarLoadMaterialSet")
public class ProjectCarLoadMaterialSetController {
    @Autowired
    private ProjectCarLoadMaterialSetServiceI projectCarLoadMaterialSetServiceI;

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Long carId, Boolean isAll)
    {
        try {
            if(isAll != null && isAll)
                return projectCarLoadMaterialSetServiceI.getByProjectIdAndCarIdOrderById(Long.parseLong(request.getHeader("projectId")), carId);

            Specification<ProjectCarLoadMaterialSet> spec = new Specification<ProjectCarLoadMaterialSet>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarLoadMaterialSet> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if(carId != null)
                        list.add(cb.equal(root.get("carID").as(Long.class), carId));

                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));

                    return  cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectCarLoadMaterialSetServiceI.query(spec);
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }
}
