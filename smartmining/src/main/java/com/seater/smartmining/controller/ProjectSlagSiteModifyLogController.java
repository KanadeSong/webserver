package com.seater.smartmining.controller;

import cn.hutool.core.date.DateUtil;
import com.seater.smartmining.entity.ProjectSlagSiteModifyLog;
import com.seater.smartmining.enums.ModifyEnum;
import com.seater.smartmining.service.ProjectSlagSiteModifyLogServiceI;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/22 11:12
 */

@RestController
@RequestMapping("/api/projectSlagSiteModifyLog")
public class ProjectSlagSiteModifyLogController extends BaseController {

    @Autowired
    ProjectSlagSiteModifyLogServiceI slagSiteModifyLogServiceI;

    @PostMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, String name, Date createTime, String userName, ModifyEnum modifyEnum) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectSlagSiteModifyLog> spec = new Specification<ProjectSlagSiteModifyLog>() {

            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<ProjectSlagSiteModifyLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!ObjectUtils.isEmpty(projectId)) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                }
                if (!ObjectUtils.isEmpty(name)) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                if (!ObjectUtils.isEmpty(createTime)) {
                    list.add(cb.between(root.get("createTime").as(Date.class), DateUtil.beginOfDay(createTime), DateUtil.endOfDay(createTime)));
                }
                if (!ObjectUtils.isEmpty(userName)) {
                    list.add(cb.like(root.get("userName").as(String.class), "%" + userName + "%"));
                }
                if (!ObjectUtils.isEmpty(modifyEnum)) {
                    list.add(cb.equal(root.get("modifyEnum").as(ModifyEnum.class), modifyEnum));
                }
                query.orderBy(cb.desc(root.get("createTime").as(Date.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return slagSiteModifyLogServiceI.query(spec, PageRequest.of(cur, page));
    }

}
