package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectOilCarUser;
import com.seater.smartmining.service.ProjectOilCarUserServiceI;
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
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/3 11:41
 */
@RestController
@RequestMapping("/api/projectOilCarUser")
public class ProjectOilCarUserController {

    @Autowired
    ProjectOilCarUserServiceI projectOilCarUserServiceI;

    @RequestMapping("/save")
    @Transactional
    public Object save(ProjectOilCarUser projectOilCarUser, HttpServletRequest request) {
        try {
            projectOilCarUserServiceI.save(projectOilCarUser);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            projectOilCarUserServiceI.delete(id);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String managerName, Boolean isAll, HttpServletRequest request) {
        try {
            if (isAll != null && isAll)
                return projectOilCarUserServiceI.getAll();

            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<ProjectOilCarUser> spec = new Specification<ProjectOilCarUser>() {

                @Override
                public Predicate toPredicate(Root<ProjectOilCarUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();

                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return projectOilCarUserServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }
}
