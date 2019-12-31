package com.seater.smartmining.controller;

import cn.hutool.core.date.DateUtil;
import com.seater.smartmining.entity.ReportPublish;
import com.seater.smartmining.enums.ReportEnum;
import com.seater.smartmining.service.ReportPublishServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description 对应渣车和挖机的日报和月报的报表发布按钮
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/23 15:46
 */
@RestController
@RequestMapping("/api/reportPublish")
public class ReportPublishController extends BaseController {

    @Autowired
    ReportPublishServiceI reportPublishServiceI;


    @PostMapping("/today")
    @Transactional
    public Object today(HttpServletRequest request, Date reportDate, ReportEnum reportEnum) throws IOException {
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        ReportPublish reportPublish = reportPublishServiceI.findByProjectIdAndReportDateAndReportEnum(projectId, DateUtil.beginOfDay(reportDate), reportEnum);
        if (reportPublish == null) {
            reportPublish = new ReportPublish();
            reportPublish.setUpdateDate(new Date());
            reportPublish.setProjectId(projectId);
            reportPublish.setReportDate(DateUtil.beginOfDay(reportDate));
            reportPublish.setUserId(sysUser.getId());
            reportPublish.setUserName(sysUser.getName());
            reportPublish.setReportEnum(reportEnum);
            reportPublish = reportPublishServiceI.save(reportPublish);
        }
        return Result.ok(reportPublish);
    }

    @PostMapping("/isPublish")
    @Transactional
    public Object isPublish(Long id, HttpServletRequest request) throws IOException {
        ReportPublish reportPublish = reportPublishServiceI.get(id);
        if (reportPublish == null) {
            return Result.error("查询不到该发布记录");
        }
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        reportPublish.setPublishWx(!reportPublish.getPublishWx());
        reportPublish.setUpdateDate(new Date());
        reportPublish.setUserId(sysUser.getId());
        reportPublish.setUserName(sysUser.getName());
        reportPublishServiceI.save(reportPublish);
        return Result.ok("操作成功");
    }

}
