package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectDispatchMode;
import com.seater.smartmining.entity.ProjectMqttCardCountReport;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectMqttCardCountReportServiceI;
import com.seater.smartmining.service.ProjectMqttCardReportServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/13 0013 12:51
 */
@RestController
@RequestMapping("/api/projectMqttCardCountReport")
public class ProjectMqttCardCountReportController extends BaseController{

    @Autowired
    private ProjectMqttCardCountReportServiceI projectMqttCardCountReportServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;

    @RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Date date, @RequestParam Shift shift) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        ScheduleService.workExceptionReport(projectId, shift, date, null);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectMqttCardCountReport> spec = new Specification<ProjectMqttCardCountReport>() {
            @Override
            public Predicate toPredicate(Root<ProjectMqttCardCountReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.equal(root.get("projectId").as(Long.class), projectId);
            }
        };
        return Result.ok(projectMqttCardCountReportServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
