package com.seater.smartmining.controller;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectPositionPainting;
import com.seater.smartmining.entity.ProjectWorkTimeByDigging;
import com.seater.smartmining.service.ProjectPositionPaintingServiceI;
import com.seater.smartmining.service.ProjectWorkTimeByDiggingServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:用户自定义地图坐标
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 10:44
 */
@RestController
@RequestMapping("/api/projectPositionPainting")
public class ProjectPositionPaintingController {

    @Autowired
    private ProjectPositionPaintingServiceI projectPositionPaintingServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectPositionPainting> spec = new Specification<ProjectPositionPainting>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectPositionPainting> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(projectId != null && projectId != 0)
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectPositionPaintingServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectPositionPainting projectPositionPainting) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        //获取当前用户对象
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        projectPositionPainting.setProjectId(projectId);
        if(projectPositionPainting.getId() == null || projectPositionPainting.getId() == 0) {
            projectPositionPainting.setCreateId(sysUser.getId());
            projectPositionPainting.setCreateName(sysUser.getAccount());
        }else{
            projectPositionPainting.setModifyId(sysUser.getId());
            projectPositionPainting.setModifyName(sysUser.getAccount());
            projectPositionPainting.setModifyTime(new Date());
        }
        projectPositionPaintingServiceI.save(projectPositionPainting);
        return Result.ok();
    }
}
