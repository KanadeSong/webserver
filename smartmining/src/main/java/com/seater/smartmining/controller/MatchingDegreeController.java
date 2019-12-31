package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.MatchingDegree;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.enums.TimeTypeEnum;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.MatchingDegreeServiceI;
import com.seater.smartmining.utils.date.DateUtils;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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
 * @Date 2019/7/1 0001 12:03
 */
@RestController
@RequestMapping("/api/matchingDegree")
public class MatchingDegreeController {

    @Autowired
    private MatchingDegreeServiceI matchingDegreeServiceI;
    @Autowired
    private WorkDateService workDateService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/query")
    public Object query(HttpServletRequest request, @RequestParam(value = "rangePickerValue", required = false) ArrayList<Long> reangePickerValue, String carCode, Integer current, Integer pageSize) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        Specification<MatchingDegree> spec = new Specification<MatchingDegree>() {
            @Override
            public Predicate toPredicate(Root<MatchingDegree> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                if (reangePickerValue != null && reangePickerValue.size() == 2) {
                    try {
                        Date startTime = DateUtils.convertDate(reangePickerValue.get(0));
                        Date endTime = DateUtils.convertDate(reangePickerValue.get(1));
                        list.add(cb.between(root.get("createTime").as(Date.class), startTime, endTime));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return matchingDegreeServiceI.query(spec, PageRequest.of(cur, page));
    }

    @RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Date reportDate) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, reportDate);
            Date earlyStart = dateMap.get("start");
            Date earlyEnd = dateMap.get("earlyEnd");
            Date nightStart = dateMap.get("nightStart");
            Date nightEnd = dateMap.get("end");
            if(earlyStart.getTime() > reportDate.getTime()) {
                earlyStart = DateUtils.subtractionOneDay(earlyStart);
                earlyEnd = DateUtils.subtractionOneDay(earlyEnd);
                nightStart = DateUtils.subtractionOneDay(nightStart);
                nightEnd = DateUtils.subtractionOneDay(nightEnd);
            }
            ScheduleService.matchingDegreeReport(projectId, earlyStart, earlyEnd, TimeTypeEnum.DAY,  null);
            ScheduleService.matchingDegreeReport(projectId, nightStart, nightEnd, TimeTypeEnum.DAY,  null);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }
}
