package com.seater.smartmining.controller;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
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
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/5 0005 12:32
 */
@RestController
@RequestMapping("/api/projectMachineLocation")
public class ProjectMachineLocationController extends BaseController {

    @Autowired
    private ProjectMachineLocationServiceI projectMachineLocationServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private ProjectMqttUpdateExctServiceI projectMqttUpdateExctServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectMachineLocation> spec = new Specification<ProjectMachineLocation>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectMachineLocation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectMachineLocationServiceI.query(spec, PageRequest.of(cur, page)));
    }

    /*@RequestMapping("/detail")
    public Result detail(HttpServletRequest request, @RequestParam String carCode, @RequestParam Date createTime) {
        Long projectId = CommonUtil.getProjectId(request);
        Date startTime = DateUtils.getAddSecondDate(createTime, -(60 * 60));
        List<ProjectMqttCardReport> reportList = projectMqttCardReportServiceI.getAllByProjectIdAndCarCodeAndTimeDischarge(projectId, carCode, startTime, createTime);
        List<ProjectMqttUpdateExct> updateExctList = projectMqttUpdateExctServiceI.getAllByProjectIDAndSlagcarCodeAndCreateTime(projectId, carCode, startTime, createTime);
        Map map = new HashMap();
        map.put("card", reportList);
        map.put("update", updateExctList);
        return Result.ok(map);
    }*/
}
