package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectDayReportDaoI;
import com.seater.smartmining.dao.ProjectDayReportPartCarDaoI;
import com.seater.smartmining.entity.ProjectDayReport;
import com.seater.smartmining.entity.ProjectDayReportPartCar;
import com.seater.smartmining.service.ProjectDayReportPartCarServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProjectDayReportPartCarServiceImpl implements ProjectDayReportPartCarServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectDayReportPartCarDaoI projectDayReportPartCarDaoI;

    @Override
    public ProjectDayReportPartCar get(Long id) throws IOException {
        return projectDayReportPartCarDaoI.get(id);
    }

    @Override
    public ProjectDayReportPartCar save(ProjectDayReportPartCar log) throws IOException{
        return projectDayReportPartCarDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectDayReportPartCarDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectDayReportPartCarDaoI.delete(ids);
    }

    @Override
    public Page<ProjectDayReportPartCar> query(Pageable pageable) {
        return projectDayReportPartCarDaoI.query(pageable);
    }

    @Override
    public Page<ProjectDayReportPartCar> query() {
        return projectDayReportPartCarDaoI.query();
    }

    @Override
    public Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec) {
        return projectDayReportPartCarDaoI.query(spec);
    }

    @Override
    public Page<ProjectDayReportPartCar> query(Specification<ProjectDayReportPartCar> spec, Pageable pageable) {
        return projectDayReportPartCarDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectDayReportPartCar> getAll() {
        return projectDayReportPartCarDaoI.getAll();
    }

    @Override
    public List<ProjectDayReportPartCar> getByReportIdOrderByCarCode(Long reportId) {
        return projectDayReportPartCarDaoI.getByReportIdOrderByCarCode(reportId);
    }

    @Override
    public void deleteByProjectIdAndReportDate(Long projectId, Date reportDate) {
        projectDayReportPartCarDaoI.deleteByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public ProjectDayReportPartCar getByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarDaoI.getByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, String startTime, String endTime) {
        return projectDayReportPartCarDaoI.getMonthReportByProjectIdAndReportDate(projectId, startTime, endTime);
    }


    /*@Override
    public List<Map> getMonthReportByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportPartCarDaoI.getMonthReportByProjectIdAndReportDate(projectId, startTime, endTime);
    }
*/
    @Override
    public Map getTotalInfoByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarDaoI.getTotalInfoByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getGrandInfoByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return projectDayReportPartCarDaoI.getGrandInfoByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public Map getHistoryInfoByProjectId(Long projectId) {
        return projectDayReportPartCarDaoI.getHistoryInfoByProjectId(projectId);
    }

    @Override
    public Map getSettlementDetailByProjectIdAndReportIdAndCarId(Long projectId, Long reportId, Long carId) {
        return projectDayReportPartCarDaoI.getSettlementDetailByProjectIdAndReportIdAndCarId(projectId, reportId, carId);
    }

    @Override
    public List<Map> getMonthCarCountByProjectIdAndReportDate(Long projectId, Date startTime, Date endTime) {
        return projectDayReportPartCarDaoI.getMonthCarCountByProjectIdAndReportDate(projectId, startTime, endTime);
    }

    @Override
    public List<ProjectDayReportPartCar> queryWx(Specification<ProjectDayReportPartCar> spec) {
        return projectDayReportPartCarDaoI.queryWx(spec);
    }
}
