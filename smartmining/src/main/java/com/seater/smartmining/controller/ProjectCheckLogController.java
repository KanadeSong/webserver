package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.service.ProjectCheckLogServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
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
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/projectCheckLog")
public class ProjectCheckLogController {
    @Autowired
    private ProjectCheckLogServiceI projectCheckLogServiceI;

    @RequestMapping("/query")
    public Object query(@RequestParam(value = "rangePickerValue", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String eventId, Float minH, Float maxH, Long projectCarId, Long diggingMachineId, HttpServletRequest request)
    {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<ProjectCheckLog> spec = new Specification<ProjectCheckLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCheckLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(projectCarId != null) {
                    list.add(cb.equal(root.get("carId").as(Long.class), projectCarId));
                }

                if(diggingMachineId != null) {
                    list.add(cb.equal(root.get("excavatCurrent").as(Long.class), diggingMachineId));
                }

                if(eventId != null) {
                    list.add(cb.like(root.get("eventId").as(String.class), "%" + eventId + "%"));
                }

                if(minH != null && maxH != null)
                {
                    list.add(cb.between(root.get("heightAvg").as(int.class), (int)(minH * 1000), (int)(maxH * 1000)));
                }
                else if(minH != null)
                {
                    list.add(cb.greaterThanOrEqualTo(root.get("heightAvg").as(int.class), (int)(minH * 1000)));
                }
                else if(maxH != null)
                {
                    list.add(cb.lessThanOrEqualTo(root.get("heightAvg").as(int.class), (int)(maxH * 1000)));
                }

                if(reangePickerValue != null && reangePickerValue.size() == 2) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                        Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                        Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                        list.add(cb.between(root.get("recviceDate").as(Date.class), startTime, endTime));
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }

                list.add(cb.equal(root.get("projectID").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return projectCheckLogServiceI.query(spec, PageRequest.of(cur, page));
    }
}
