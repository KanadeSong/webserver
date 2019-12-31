package com.seater.smartmining.exception;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.entity.ProjectSmartminingErrorLog;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.service.ProjectSmartminingErrorLogServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 16:16
 */
@RestControllerAdvice
public class SmartminingExceptionHandle {

    @Autowired
    private SmartminingExceptionService smartminingExceptionService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @ExceptionHandler(value = Exception.class)
    public Result smartminingExceptionHandle(HttpServletRequest request, Exception e){
        Long projectId = CommonUtil.getProjectId(request);
        if(projectId != null && projectId != 0)
            stringRedisTemplate.delete("schedule" + projectId);
        smartminingExceptionService.save(request, e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = SmartminingProjectException.class)
    public Result smartminingSelfExceptionHandle(HttpServletRequest request, SmartminingProjectException e){
        Long projectId = CommonUtil.getProjectId(request);
        if(projectId != null && projectId != 0)
            stringRedisTemplate.delete("schedule" + projectId);
        smartminingExceptionService.save(request, e);
        return Result.error(e.getMsg());
    }
}
