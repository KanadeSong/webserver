package com.seater.smartmining.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCubicDetailElse;
import com.seater.smartmining.service.ProjectCubicDetailElseServiceI;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/7 0007 16:42
 */
@RestController
@RequestMapping("/api/projectcubicdetailelse")
public class ProjectCubicDetailElseController {

    @Autowired
    private ProjectCubicDetailElseServiceI projectCubicDetailElseServiceI;

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectCubicDetailElse projectCubicDetailElse){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            projectCubicDetailElse.setProjectId(projectId);
            projectCubicDetailElseServiceI.save(projectCubicDetailElse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }
}
