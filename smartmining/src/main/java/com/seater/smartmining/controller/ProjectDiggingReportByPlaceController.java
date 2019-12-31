package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectDiggingReportByPlace;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectDiggingReportByPlaceServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/19 0019 11:29
 */
@RestController
@RequestMapping("/api/projectDiggingReportByPlace")
public class ProjectDiggingReportByPlaceController {

    @Autowired
    private ProjectDiggingReportByPlaceServiceI projectDiggingReportByPlaceServiceI;
    @Autowired
    private EntityManager entityManager;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/report")
    public Result reportByPlace(HttpServletRequest request, @RequestParam Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ScheduleService.reportDiggingByPlace(projectId, reportDate, null);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /*@RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, Date startTime, Date endTime, String machineCode) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<ProjectDiggingReportByPlace> placeList = new ArrayList<>();
        if(StringUtils.isNotEmpty(machineCode))
            placeList = projectDiggingReportByPlaceServiceI.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime, machineCode);
        else
            placeList = projectDiggingReportByPlaceServiceI.getAllByProjectIdAndDateIdentification(projectId, startTime, endTime);
        List<ProjectDiggingReportByPlace> resultList = new ArrayList<>();
        Integer total = placeList.size();
        int index = cur * page;
        for(int i = index; i < index + page; i++){
            if(resultList.size() < i){
                ProjectDiggingReportByPlace place = placeList.get(i);
                resultList.add(place);
            }else{
                break;
            }
        }
        Map map = new HashMap();
        map.put("totalCount", total);
        map.put("result", resultList);
        return Result.ok(map);
    }*/

    @RequestMapping("/tempReport")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, @RequestParam Date startTime, @RequestParam Date endTime, String machineCode, Integer shifts, Long placeId,Integer choose, boolean sort) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        List<Predicate> predicateList = new ArrayList<>();
        Root<ProjectDiggingReportByPlace> root = cq.from(ProjectDiggingReportByPlace.class);//具体实体的Root
        if(StringUtils.isNotEmpty(machineCode))
            predicateList.add(cb.like(root.get("machineCode").as(String.class), "%" + machineCode + "%"));
        if(placeId != null && placeId != 0)
            predicateList.add(cb.equal(root.get("placeId").as(Long.class), placeId));
        if(shifts != null && shifts != 0)
            predicateList.add(cb.equal(root.get("shifts").as(Long.class), shifts));
        predicateList.add(cb.equal(root.get("projectId").as(Long.class), projectId));
        predicateList.add(cb.between(root.get("dateIdentification"), startTime, endTime));
        Predicate[] p = new Predicate[predicateList.size()];
        predicateList.toArray(p);
        cq.where(p).multiselect(cb.sum(root.get("workTime")).alias("workTime"), root.get("machineId").alias("machineId"), root.get("machineCode").alias("machineCode"),
                root.get("placeId").alias("placeId"), root.get("placeName").alias("placeName"), root.get("shifts").alias("shifts")).groupBy(root.get("machineId"), root.get("machineCode"),
                root.get("placeId"), root.get("placeName"), root.get("shifts"));
        if(choose != null && choose != 0) {
            if (choose == 1) {
                if(sort)
                    cq.orderBy(cb.desc(cb.sum(root.get("workTime"))));
                else
                    cq.orderBy(cb.asc(cb.sum(root.get("workTime"))));
            }else if (choose == 2) {
                if(sort)
                    cq.orderBy(cb.desc(root.get("placeId")));
                else
                    cq.orderBy(cb.asc(root.get("placeId")));
            }else if (choose == 3) {
                if(sort)
                    cq.orderBy(cb.desc(root.get("shifts")));
                else
                    cq.orderBy(cb.asc(root.get("shifts")));
            }else {
                if(sort)
                    cq.orderBy(cb.desc(root.get("machineId")));
                else
                    cq.orderBy(cb.asc(root.get("machineId")));
            }
        }
        List<Tuple> list = entityManager.createQuery(cq).getResultList();
        List<Map> resultList = new ArrayList<>();
        Integer total = list.size();
        int index = cur * page;
        //总的工作时间
        BigDecimal totalWorkTime = new BigDecimal(0);
        for(int i = 0; i < list.size(); i++){
            Tuple tuple = list.get(i);
            BigDecimal workTime = new BigDecimal(tuple.get(0).toString());
            totalWorkTime = totalWorkTime.add(workTime);
        }
        for(int i = index; i < index + page; i++){
            if(list.size() > i){
                Tuple tuple = list.get(i);
                BigDecimal workTime = new BigDecimal(tuple.get(0).toString());
                Map map = new HashMap();
                map.put("workTime", workTime);
                map.put("machineId", tuple.get(1));
                map.put("machineCode", tuple.get(2));
                map.put("placeId", tuple.get(3));
                map.put("placeName", tuple.get(4));
                map.put("shifts", tuple.get(5));
                resultList.add(map);
            }else{
                break;
            }
        }
        Map map = new HashMap();
        map.put("totalCount", total);
        map.put("content", resultList);
        map.put("totalTime", totalWorkTime);
        return Result.ok(map);
    }

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, Integer current, Integer pageSize, Date startTime, Date endTime, Long placeId){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<ProjectDiggingReportByPlace> spec = new Specification<ProjectDiggingReportByPlace>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectDiggingReportByPlace> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                if(placeId != null)
                    list.add(criteriaBuilder.equal(root.get("placeId").as(Long.class), placeId));
                if(startTime != null && endTime != null)
                    list.add(criteriaBuilder.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return projectDiggingReportByPlaceServiceI.query(spec, PageRequest.of(cur, page));
    }
}
