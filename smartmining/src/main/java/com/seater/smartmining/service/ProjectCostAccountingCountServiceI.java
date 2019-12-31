package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.ProjectCostAccountingCount;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 11:23
 */
public interface ProjectCostAccountingCountServiceI {

    ProjectCostAccountingCount get(Long id) throws IOException;
    ProjectCostAccountingCount save(ProjectCostAccountingCount log) throws JsonProcessingException;
    List<ProjectCostAccountingCount> getByProjectIdAndReportDate(Long projectId, Date reportDate);
    void deleteByProjectIdAndReportDate(Long projectId, Date reportDate);
}
