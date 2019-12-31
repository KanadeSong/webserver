package com.seater.smartmining.controller;


import com.seater.smartmining.entity.ProjectLoadLog;
import com.seater.smartmining.service.ProjectLoadLogServiceI;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/projectLoadLog")
public class ProjectLoadLogController {
    @Autowired
    private ProjectLoadLogServiceI ProjectLoadLogServiceI;

    @RequestMapping("/query")
    public Object query(@RequestParam(value = "rangePickerValue", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String eventId, Long projectCarId, Long diggingMachineId, HttpServletRequest request)
    {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<ProjectLoadLog> spec = new Specification<ProjectLoadLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectLoadLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(projectCarId != null) {
                    list.add(cb.equal(root.get("carId").as(Long.class), projectCarId));
                }

                if(diggingMachineId != null) {
                    list.add(cb.equal(root.get("excavatCurrent").as(Long.class), diggingMachineId));
                }

                if(eventId != null) {
                    list.add(cb.like(root.get("eventId").as(String.class), "%" + eventId + "%"));
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

        return ProjectLoadLogServiceI.query(spec, PageRequest.of(cur, page));
    }
}
