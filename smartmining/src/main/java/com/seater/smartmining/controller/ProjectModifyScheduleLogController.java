package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectModifyScheduleLog;
import com.seater.smartmining.service.ProjectModifyScheduleLogServiceI;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/26 0026 11:38
 */
@RestController
@RequestMapping("/api/projectModifyScheduleLog")
public class ProjectModifyScheduleLogController {

    @Autowired
    private ProjectModifyScheduleLogServiceI projectModifyScheduleLogServiceI;

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Integer current, Integer pageSize){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<ProjectModifyScheduleLog> spec = new Specification<ProjectModifyScheduleLog>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectModifyScheduleLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return projectModifyScheduleLogServiceI.query(spec, PageRequest.of(cur, page));
    }
}
