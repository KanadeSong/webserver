package com.seater.smartmining.quartz;

import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.utils.params.Result;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description TODO 测试用
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/18 11:38
 */
@RestController
@RequestMapping("/api/quartz")
public class JobController {

    @Autowired
    QuartzManager quartzManager;

    @Autowired
    ProjectServiceI projectServiceI;

    @RequestMapping("/add")
    public Object add(String jobName, String cron, HttpServletRequest request) throws SchedulerException, IOException {
        long projectId = Long.parseLong(request.getHeader("projectId"));
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("project", projectServiceI.get(projectId));
        quartzManager.addJob(jobName, MeterReadingJob.class, cron, jobDataMap);

        return Result.ok("任务开始");
    }

    @RequestMapping("/modify")
    public Object modify(String jobName, String cron, HttpServletRequest request) throws IOException {
        long projectId = Long.parseLong(request.getHeader("projectId"));
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("project", projectServiceI.get(projectId));
        quartzManager.modifyJobTime(jobName, cron, jobDataMap);
        return Result.ok("修改成功");
    }

    @RequestMapping("/remove")
    public Object remove(String jobName, String cron, HttpServletRequest request) throws IOException {
        quartzManager.removeJob(jobName);
        return Result.ok("删除成功");
    }
}
