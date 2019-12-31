package com.seater.smartmining.controller;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectModifyScheduleLog;
import com.seater.smartmining.entity.ProjectMqttCardReport;
import com.seater.smartmining.entity.ProjectWorkTimeByDiggingLog;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.ProjectModifyScheduleLogServiceI;
import com.seater.smartmining.service.ProjectMqttCardReportServiceI;
import com.seater.smartmining.service.ProjectWorkTimeByDiggingLogServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
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
 * @Date 2019/11/29 0029 12:51
 */
@RestController
@RequestMapping("/api/projectWorkTimeByDiggingLog")
public class ProjectWorkTimeByDiggingLogController extends BaseController{

    @Autowired
    private ProjectWorkTimeByDiggingLogServiceI projectWorkTimeByDiggingLogServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectModifyScheduleLogServiceI projectModifyScheduleLogServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private WorkDateService workDateService;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize){
        Long projectId = CommonUtil.getProjectId(request);
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<ProjectWorkTimeByDiggingLog> spec = new Specification<ProjectWorkTimeByDiggingLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectWorkTimeByDiggingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(projectId != null && projectId != 0)
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                //list.add(cb.isTrue(root.get("success")));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectWorkTimeByDiggingLogServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/reportBySceneInfo")
    public Result reportBySceneInfo(HttpServletRequest request, @RequestParam Date date) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        Map<String, Date> dateMap = workDateService.getWorkTime(projectId,date);
        Date startTime = dateMap.get("start");
        Shift shift = workDateService.getShift(date, projectId);
        if(date.getTime() < startTime.getTime())
            date = DateUtils.getAddDate(date, -1);
        date = DateUtils.createReportDateByMonth(date);
        //获取排班修改记录
        List<ProjectModifyScheduleLog> scheduleLogList = projectModifyScheduleLogServiceI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        List<ProjectWorkTimeByDiggingLog> diggingLogList = projectWorkTimeByDiggingLogServiceI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        List<ProjectMqttCardReport> reportList = projectMqttCardReportServiceI.getAllByProjectIdAndDateIdentificationAndShift(projectId, date, shift.getAlias());
        List<Map> resultList = new ArrayList<>();
        int i = 0;
        for(ProjectModifyScheduleLog log : scheduleLogList){
            Map map = new HashMap();
            map.put("createName", log.getModifyName());
            map.put("scheduleDate", log.getModifyTime());
            map.put("action", "排班修改");
            resultList.add(map);
            i++;
            if(i >= 9)
                break;
        }
        i = 0;
        for(ProjectWorkTimeByDiggingLog log : diggingLogList){
            Map map = new HashMap();
            map.put("carCode", log.getCarCode());
            map.put("workTimeDate", log.getCreateTime());
            map.put("action", log.getDoStatus().getName());
            resultList.add(map);
            i++;
            if(i >= 9)
                break;
        }
        i = 0;
        for(ProjectMqttCardReport report : reportList){
            Map map = new HashMap();
            map.put("carCode", report.getCarCode());
            map.put("exceptionDate", report.getCreatTime());
            map.put("action", report.getMessage());
            resultList.add(map);
            i++;
            if(i >= 9)
                break;
        }
        return Result.ok(resultList);
    }
}
