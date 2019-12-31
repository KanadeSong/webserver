package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectExplosive;
import com.seater.smartmining.service.ProjectExplosiveServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:45
 */
@RestController
@RequestMapping("/api/projectExplosive")
public class ProjectExplosiveController {

    @Autowired
    private ProjectExplosiveServiceI projectExplosiveServiceI;

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectExplosive projectExplosive){
        try {
            Long projectId = CommonUtil.getProjectId(request);
            projectExplosive.setProjectId(projectId);
            projectExplosive.setValid(true);
            projectExplosiveServiceI.save(projectExplosive);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids){
        projectExplosiveServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(Integer current, Integer pageSize, HttpServletRequest request){
        int cur = (current == null  || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectExplosive> spec = new Specification<ProjectExplosive>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectExplosive> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (projectId != null)
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectExplosiveServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
