package com.seater.smartmining.exception.service;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import com.seater.smartmining.service.ProjectSmartminingErrorLogServiceI;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 17:24
 */
@Service
public class SmartminingExceptionService {

    @Autowired
    private ProjectSmartminingErrorLogServiceI projectSmartminingErrorLogServiceI;

    public void save(HttpServletRequest request, Exception e){
        try {
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            ProjectSmartminingErrorLog log = new ProjectSmartminingErrorLog();
            Long projectId = 0L;
            if (request != null && StringUtils.isNotEmpty(request.getHeader("projectId"))) {
                projectId = Long.parseLong(request.getHeader("projectId"));
                log.setProjectId(projectId);
            }
            log.setErrorMessage(e.getMessage());
            if (sysUser != null) {
                log.setUserId(sysUser.getId());
                log.setUserName(sysUser.getName());
            }
            log.setCreateTime(new Date());
            StackTraceElement element = e.getStackTrace()[0];
            log.setLineNumber(element.getLineNumber());
            log.setDeclaringClass(element.getClassName());
            log.setMethodName(element.getMethodName());
            log.setDetailMessage(JSON.toJSONString(e.getStackTrace()));
            projectSmartminingErrorLogServiceI.save(log);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void save(Exception e){
        try {
            ProjectSmartminingErrorLog log = new ProjectSmartminingErrorLog();
            log.setErrorMessage(e.getMessage());
            log.setCreateTime(new Date());
            StackTraceElement element = e.getStackTrace()[0];
            log.setLineNumber(element.getLineNumber());
            log.setDeclaringClass(element.getClassName());
            log.setMethodName(element.getMethodName());
            log.setDetailMessage(JSON.toJSONString(e.getStackTrace()));
            projectSmartminingErrorLogServiceI.save(log);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void save(Exception e, String params){
        try {
            ProjectSmartminingErrorLog log = new ProjectSmartminingErrorLog();
            log.setErrorMessage(e.getMessage());
            log.setCreateTime(new Date());
            StackTraceElement element = e.getStackTrace()[0];
            log.setLineNumber(element.getLineNumber());
            log.setDeclaringClass(element.getClassName());
            log.setMethodName(element.getMethodName());
            log.setDetailMessage(JSON.toJSONString(e.getStackTrace()));
            log.setParams(params);
            projectSmartminingErrorLogServiceI.save(log);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void save(Exception e, String params, String typeMessage){
        try {
            ProjectSmartminingErrorLog log = new ProjectSmartminingErrorLog();
            log.setErrorMessage(e.getMessage());
            log.setCreateTime(new Date());
            StackTraceElement element = e.getStackTrace()[0];
            log.setLineNumber(element.getLineNumber());
            log.setDeclaringClass(element.getClassName());
            log.setMethodName(element.getMethodName());
            log.setDetailMessage(JSON.toJSONString(e.getStackTrace()));
            log.setParams(params);
            log.setTypeMessage(typeMessage);
            projectSmartminingErrorLogServiceI.save(log);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
