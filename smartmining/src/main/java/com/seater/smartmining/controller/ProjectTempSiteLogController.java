package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.entity.ProjectTempSiteLog;
import com.seater.smartmining.entity.ProjectUnloadLog;
import com.seater.smartmining.service.ProjectSlagSiteServiceI;
import com.seater.smartmining.service.ProjectTempSiteLogServiceI;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/21 0021 11:38
 */
@RestController
@RequestMapping("/api/ProjectTempSiteLog")
public class ProjectTempSiteLogController {

    @Autowired
    private ProjectTempSiteLogServiceI projectTempSiteLogServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectTempSiteLog log) throws IOException {
        ProjectTempSiteLog ret = null;
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        log.setProjectId(projectId);
        if (log != null) {
            log.setTerminalTime(log.getTimeDischarge().getTime());
            //log.setTimeLoad(new Date(log.getTimeLoad().getTime() * 1000));
            //log.setTimeCheck(new Date(log.getTimeCheck().getTime() * 1000));
            //log.setTimeDischarge(new Date(log.getTimeDischarge().getTime() * 1000));

            Date lastTimeDischarge = projectTempSiteLogServiceI.getMaxUnloadDateByCarCode(log.getCarCode());
            ProjectSlagSite slagSite = projectSlagSiteServiceI.get(log.getSlagSiteId());
            Long si = 0L;
            if (slagSite != null) {
                si = slagSite.getSwipeIntervent();
                log.setSlagSiteName(slagSite.getName());
                if (lastTimeDischarge == null || log.getTimeDischarge().getTime() >= (lastTimeDischarge.getTime() +
                        si)) {   //在限制时间内不允许多次刷卡
                    ret = projectTempSiteLogServiceI.save(log);
                } else {
                    log.setRemark("渣车" + log.getCarCode() + "在限制时间内刷卡.");
                    log.setValid(false);
                    projectTempSiteLogServiceI.save(log);
                    ret = null;
                }
            } else {
                log.setRemark("找不到对应渣场信息：" + log.getCarCode());
                log.setValid(false);
                projectTempSiteLogServiceI.save(log);
                ret = null;
            }
        }
        return Result.ok(ret);
    }
}
