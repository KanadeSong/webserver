package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectLoadLog;
import com.seater.smartmining.entity.ProjectSlagCarLog;
import com.seater.smartmining.service.ProjectSlagCarLogServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
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

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/24 0024 9:18
 */
@RestController
@RequestMapping("/api/projectSlagCarLog")
public class ProjectSlagCarLogController {
    @Autowired
    private ProjectSlagCarLogServiceI projectSlagCarLogServiceI;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, @RequestParam(value = "rangePickerValue", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String eventId, Long diggingMachineId,String carCode){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<ProjectSlagCarLog> spec = new Specification<ProjectSlagCarLog>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectSlagCarLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(StringUtils.isNotEmpty(carCode))
                    list.add(criteriaBuilder.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                if(StringUtils.isNotEmpty(eventId))
                    list.add(criteriaBuilder.like(root.get("eventId").as(String.class), "%" + eventId + "%"));
                if(reangePickerValue != null && reangePickerValue.size() == 2) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                        Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                        Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                        list.add(criteriaBuilder.between(root.get("recviceDate").as(Date.class), startTime, endTime));
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
                if(diggingMachineId != null && diggingMachineId != 0)
                    list.add(criteriaBuilder.equal(root.get("excavatCurrent").as(Long.class), diggingMachineId));
                list.add(criteriaBuilder.equal(root.get("projectID").as(Long.class), projectId));
                query.orderBy(criteriaBuilder.desc(root.get("id").as(Long.class)));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectSlagCarLogServiceI.query(spec, PageRequest.of(cur, page)));
    }
}
