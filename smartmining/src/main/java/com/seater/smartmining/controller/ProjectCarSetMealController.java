package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectCarSetMeal;
import com.seater.smartmining.service.ProjectCarSetMealServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 15:43
 */
@RestController
@RequestMapping("/api/projectCarSetMeal")
public class ProjectCarSetMealController {
    @Autowired
    private ProjectCarSetMealServiceI projectCarSetMealServiceI;

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, @Valid ProjectCarSetMeal projectCarSetMeal){
        try {
            Long projectId = CommonUtil.getProjectId(request);
            if(projectId != null && projectId != 0)
                projectCarSetMeal.setProjectId(projectId);
            projectCarSetMealServiceI.save(projectCarSetMeal);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids){
        projectCarSetMealServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, String name){
        Long projectId = CommonUtil.getProjectId(request);
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectCarSetMeal> spec = new Specification<ProjectCarSetMeal>() {
            @Override
            public Predicate toPredicate(Root<ProjectCarSetMeal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (name != null && !name.isEmpty())
                    list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                if(projectId != null && projectId != 0)
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return Result.ok(projectCarSetMealServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
