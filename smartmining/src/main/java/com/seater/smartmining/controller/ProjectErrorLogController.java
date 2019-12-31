package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectErrorLog;
import com.seater.smartmining.service.ProjectErrorLogServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/22 0022 17:32
 */
@RestController
@RequestMapping("/api/projecterrorlog")
public class ProjectErrorLogController {

    @Autowired
    private ProjectErrorLogServiceI projectErrorLogServiceI;

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Integer current, Integer pageSize){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectErrorLog> spec = new Specification<ProjectErrorLog>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectErrorLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return projectErrorLogServiceI.query(spec, PageRequest.of(cur, page));
    }

//    @RequestMapping("/save")
    public Result save(@RequestBody ProjectErrorLog log){
        try {
            System.out.println(log);
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            log.setUserId(sysUser.getId());
            log.setUserName(sysUser.getName());
            projectErrorLogServiceI.save(log);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids){
        projectErrorLogServiceI.delete(ids);
        return Result.ok();
    }
}
