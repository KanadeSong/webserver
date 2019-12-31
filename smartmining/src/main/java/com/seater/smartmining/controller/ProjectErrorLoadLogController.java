package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectErrorLoadLog;
import com.seater.smartmining.service.ProjectErrorLoadLogServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
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
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/1 0001 11:19
 */
@RestController
@RequestMapping("/api/projectErrorLoadLog")
public class ProjectErrorLoadLogController {

    @Autowired
    private ProjectErrorLoadLogServiceI projectErrorLoadLogServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, String carCode, Date startTime, Date endTime){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectErrorLoadLog> spec = new Specification<ProjectErrorLoadLog>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectErrorLoadLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                if(StringUtils.isNotEmpty(carCode))
                    list.add(criteriaBuilder.like(root.get("carCode").as(String.class), '%' + carCode + '%'));
                if(startTime != null && endTime != null)
                    list.add(criteriaBuilder.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectErrorLoadLogServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
