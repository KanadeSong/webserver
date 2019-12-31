package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.ProjectCostAccountingCountDaoI;
import com.seater.smartmining.entity.ProjectCostAccountingCount;
import com.seater.smartmining.service.ProjectCostAccountingCountServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/22 0022 11:27
 */
@Service
public class ProjectCostAccountingCountServiceImpl implements ProjectCostAccountingCountServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectCostAccountingCountDaoI projectCostAccountingCountDaoI;

    @Override
    public ProjectCostAccountingCount get(Long id) throws IOException {
        return projectCostAccountingCountDaoI.get(id);
    }

    @Override
    public ProjectCostAccountingCount save(ProjectCostAccountingCount log) throws JsonProcessingException {
        return projectCostAccountingCountDaoI.save(log);
    }

    @Override
    public List<ProjectCostAccountingCount> getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectCostAccountingCountDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectCostAccountingCountDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }
}
