package com.seater.smartmining.controller;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 * @Date 2019/11/19 0019 11:50
 */
@RestController
@RequestMapping("/api/projectCarTotalCountReport")
public class ProjectCarTotalCountReportController extends BaseController {

    @Autowired
    private ProjectCarTotalCountReportServiceI projectCarTotalCountReportServiceI;
    @Autowired
    private ProjectCarTotalCountReportByTotalServiceI projectCarTotalCountReportByTotalServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private WorkDateService workDateService;

    @RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Date date, @RequestParam Shift shift) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        /*Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
        Date startTime = dateMap.get("start");
        if (date.getTime() < startTime.getTime())
            date = DateUtils.getAddDate(date, -1);
        date = DateUtils.createReportDateByMonth(date);*/
        ScheduleService.totalCountCarReport(projectId, shift, date, null);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, String carCode, Date date, Shift shift) throws IOException {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        if (date == null) {
            date = new Date();
            date = DateUtils.getAddDate(date, -1);
            date = DateUtils.createReportDateByMonth(date);
        }
        if (shift == null || shift.compareTo(Shift.Unknown) == 0)
            shift = workDateService.getShift(date, projectId);
        Shift shiftQuery = shift;
        Date startTime = DateUtils.getStartDateByNow(date);
        Date endTime = DateUtils.getEndDateByNow(date);
        Specification<ProjectCarTotalCountReport> spec = new Specification<ProjectCarTotalCountReport>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarTotalCountReport> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                list.add(cb.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                list.add(cb.equal(root.get("shift").as(Shift.class), shiftQuery));
                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));

                query.orderBy(cb.desc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        Page<ProjectCarTotalCountReport> reportPage = projectCarTotalCountReportServiceI.query(spec, PageRequest.of(cur, page));
        Map map = new HashMap();
        map.put("detail", reportPage);
        if (reportPage != null) {
            List<ProjectCarTotalCountReport> reportList = reportPage.getContent();
            if (reportList != null && reportList.size() > 0) {
                ProjectCarTotalCountReportByTotal total = projectCarTotalCountReportByTotalServiceI.get(reportList.get(0).getTotalId());
                map.put("total", total);
            } else {
                map.put("total", null);
            }
        } else {
            map.put("total", null);
        }
        return Result.ok(map);
    }

    @RequestMapping("/find")
    public Result find(HttpServletRequest request){
        Long projectId = CommonUtil.getProjectId(request);
        List<ProjectUnloadLog> logList = projectUnloadLogServiceI.getAllByProjectIDAndTimeDischargeAndIsVaild();
        List<String> totalList = new ArrayList<>();
        for(ProjectUnloadLog log : logList){
            String carCode = log.getCarCode();
            Long time = log.getTimeDischarge().getTime();
            totalList.add(carCode + time);
        }
        Date startTime = DateUtils.stringFormatDate("2019-12-13 05:30:00", SmartminingConstant.DATEFORMAT);
        Date endTime = DateUtils.stringFormatDate("2019-12-13 17:29:59", SmartminingConstant.DATEFORMAT);
        List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndTimeDischargeAndStatus(projectId, startTime, endTime);
        List<String> infoList = new ArrayList<>();
        for(ProjectCarWorkInfo info : workInfoList){
            String carCode = info.getCarCode();
            Long time = info.getTimeDischarge().getTime();
            infoList.add(carCode + time);
        }
        Date date = DateUtils.stringFormatDate("2019-12-13 00:00:00", SmartminingConstant.DATEFORMAT);
        date = DateUtils.createReportDateByMonth(date);
        List<ProjectMqttCardReport> reportList = projectMqttCardReportServiceI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, 1);
        List<String> errorList = new ArrayList<>();
        for(ProjectMqttCardReport report : reportList){
            String carCode = report.getCarCode();
            Long time = report.getTimeDischarge().getTime();
            errorList.add(carCode + time);
        }
        List<String> resultList = new ArrayList<>();
        for(int i = 0; i < totalList.size(); i++){
            String key = totalList.get(i);
            if(!infoList.contains(key) && !errorList.contains(key)){
                resultList.add(key);
            }
        }
        return Result.ok(resultList);
    }
}
