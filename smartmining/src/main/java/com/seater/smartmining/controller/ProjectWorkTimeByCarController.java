package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectWorkTimeByCar;
import com.seater.smartmining.service.ProjectWorkTimeByCarServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
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
 * @Date 2019/11/18 0018 12:12
 */
@RestController
@RequestMapping("/api/projectWorkTimeByCar")
public class ProjectWorkTimeByCarController {

    @Autowired
    private ProjectWorkTimeByCarServiceI projectWorkTimeByCarServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize){
        Long projectId = CommonUtil.getProjectId(request);
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectWorkTimeByCar> specification = new Specification<ProjectWorkTimeByCar>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectWorkTimeByCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectWorkTimeByCarServiceI.query(specification, PageRequest.of(cur, page)));
    }
}
