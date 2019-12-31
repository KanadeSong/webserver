package com.seater.smartmining.controller;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.service.ProjectOtherDeviceWorkInfoServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/19 0019 13:05
 */
@RestController
@RequestMapping("/api/projectOtherDeviceDayReport")
public class ProjectOtherDeviceDayReportController {
    @Autowired
    private ProjectOtherDeviceWorkInfoServiceI projectOtherDeviceWorkInfoServiceI;

    @RequestMapping("/report")
    public Result report(HttpServletRequest request, @RequestParam Date reportDate, @RequestParam CarType carType){
        Long projectId = CommonUtil.getProjectId(request);
        return Result.ok();
    }
}
